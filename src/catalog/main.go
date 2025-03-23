// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this
// software and associated documentation files (the "Software"), to deal in the Software
// without restriction, including without limitation the rights to use, copy, modify,
// merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
// PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
// SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package main

import (
	"context"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"strconv"
	"syscall"
	"time"

	"github.com/aws-containers/retail-store-sample-app/catalog/api"
	"github.com/aws-containers/retail-store-sample-app/catalog/config"
	"github.com/aws-containers/retail-store-sample-app/catalog/controller"
	"github.com/aws-containers/retail-store-sample-app/catalog/middleware"
	"github.com/aws-containers/retail-store-sample-app/catalog/repository"
	"github.com/gin-gonic/gin"
	_ "github.com/go-sql-driver/mysql"
	"github.com/sethvargo/go-envconfig/pkg/envconfig"
	ginprometheus "github.com/zsais/go-gin-prometheus"

	"go.opentelemetry.io/contrib/detectors/aws/ec2"
	"go.opentelemetry.io/contrib/instrumentation/github.com/gin-gonic/gin/otelgin"
	"go.opentelemetry.io/contrib/propagators/aws/xray"
	"go.opentelemetry.io/otel"

	"go.opentelemetry.io/otel/exporters/otlp/otlptrace"
	"go.opentelemetry.io/otel/exporters/otlp/otlptrace/otlptracehttp"
	"go.opentelemetry.io/otel/propagation"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
)

// @title Catalog API
// @version 1.0
// @description This API serves the product catalog

// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html

// @host localhost:8080
// @BasePath /

func main() {
	ctx := context.Background()

	_, otelPresent := os.LookupEnv("OTEL_SERVICE_NAME")

	if otelPresent {
		_, err := initTracer(ctx)
		if err != nil {
			log.Fatal(err)
		}
	}

	var config config.AppConfiguration
	if err := envconfig.Process(ctx, &config); err != nil {
		log.Fatal(err)
	}

	db, err := repository.NewRepository(config.Database)
	if err != nil {
		log.Fatal(err)
	}

	api, err := api.NewCatalogAPI(db)
	if err != nil {
		log.Fatal(err)
	}

	r := gin.New()
	r.Use(gin.LoggerWithConfig(gin.LoggerConfig{
		SkipPaths: []string{"/health"},
	}))

	p := ginprometheus.NewPrometheus("gin")
	p.Use(r)

	c, err := controller.NewController(api)
	if err != nil {
		log.Fatalln("Error creating controller", err)
	}

	chaosController := middleware.NewChaosController()

	chaosController.SetupChaosRoutes(r)

	catalog := r.Group("/catalog")

	catalog.Use(chaosController.ChaosMiddleware())
	catalog.Use(otelgin.Middleware("catalog-server"))

	catalog.GET("/products", c.GetProducts)

	catalog.GET("/size", c.CatalogSize)
	catalog.GET("/tags", c.ListTags)
	catalog.GET("/products/:id", c.GetProduct)

	r.GET("/health", func(c *gin.Context) {
		if !chaosController.IsHealthy() {
			c.AbortWithError(503, fmt.Errorf("health check failed"))
			return
		}

		c.String(http.StatusOK, "OK")
	})

	r.GET("/topology", func(c *gin.Context) {
		topology := make(map[string]string)

		topology["persistenceProvider"] = config.Database.Type
		topology["databaseEndpoint"] = "N/A"

		if config.Database.Type != "in-memory" {
			topology["databaseEndpoint"] = config.Database.Endpoint
		}

		c.JSON(http.StatusOK, topology)
	})

	srv := &http.Server{
		Addr:    ":" + strconv.Itoa(config.Port),
		Handler: r,
	}

	// Initializing the server in a goroutine so that
	// it won't block the graceful shutdown handling below
	go func() {
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("listen: %s\n", err)
		}
	}()

	// Wait for interrupt signal to gracefully shutdown the server with
	// a timeout of 5 seconds.
	quit := make(chan os.Signal, 1)
	// kill (no param) default send syscall.SIGTERM
	// kill -2 is syscall.SIGINT
	// kill -9 is syscall.SIGKILL but can't be catch, so don't need add it
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	log.Println("Shutting down server...")

	// The context is used to inform the server it has 5 seconds to finish
	// the request it is currently handling
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	if err := srv.Shutdown(ctx); err != nil {
		log.Fatal("Server forced to shutdown:", err)
	}

	log.Println("Server exiting")
}

func initTracer(ctx context.Context) (*sdktrace.TracerProvider, error) {
	client := otlptracehttp.NewClient()
	exporter, err := otlptrace.New(ctx, client)
	if err != nil {
		return nil, fmt.Errorf("creating OTLP trace exporter: %w", err)
	}
	idg := xray.NewIDGenerator()
	ec2ResourceDetector := ec2.NewResourceDetector()
	resource, _ := ec2ResourceDetector.Detect(context.Background())
	tp := sdktrace.NewTracerProvider(
		sdktrace.WithBatcher(exporter),
		sdktrace.WithIDGenerator(idg),
		sdktrace.WithResource(resource),
	)
	otel.SetTextMapPropagator(propagation.NewCompositeTextMapPropagator(propagation.TraceContext{}, propagation.Baggage{}))
	otel.SetTracerProvider(tp)
	return tp, nil
}
