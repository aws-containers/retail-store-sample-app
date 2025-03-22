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

package middleware

import (
	"net/http"
	"strconv"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
)

type ChaosController struct {
	mu              sync.RWMutex
	latency         time.Duration
	errorStatus     int
	isLatencyOn     bool
	isErrorStatusOn bool
	isHealthy       bool
}

func NewChaosController() *ChaosController {
	return &ChaosController{
		latency:         0,
		errorStatus:     0,
		isLatencyOn:     false,
		isErrorStatusOn: false,
		isHealthy:       true,
	}
}

// Middleware function that applies chaos
func (cc *ChaosController) ChaosMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		// Skip chaos for chaos control endpoints
		if isChaosControlPath(c.Request.URL.Path) {
			c.Next()
			return
		}

		cc.mu.RLock()
		defer cc.mu.RUnlock()

		// Apply artificial latency if enabled
		if cc.isLatencyOn {
			time.Sleep(cc.latency)
		}

		// Return error status if enabled
		if cc.isErrorStatusOn {
			c.AbortWithStatus(cc.errorStatus)
			return
		}

		c.Next()
	}
}

// SetupChaosRoutes adds the chaos control endpoints
func (cc *ChaosController) SetupChaosRoutes(r *gin.Engine) {
	chaos := r.Group("/chaos")
	{
		// Enable/disable latency
		chaos.POST("/latency/:ms", cc.setLatency)
		chaos.DELETE("/latency", cc.disableLatency)

		// Enable/disable status code
		chaos.POST("/status/:code", cc.setErrorStatus)
		chaos.DELETE("/status", cc.disableErrorStatus)

		// Get current chaos status
		chaos.GET("/status", cc.getChaosStatus)

		// Enable/disable health check
		chaos.POST("/health", cc.setHealth)
		chaos.DELETE("/health", cc.disableHealth)
	}
}

func (cc *ChaosController) setLatency(c *gin.Context) {
	ms := c.Param("ms")
	latency, err := strconv.Atoi(ms)
	if err != nil || latency < 0 {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "Invalid latency value. Please provide a positive integer in milliseconds.",
		})
		return
	}

	cc.mu.Lock()
	cc.latency = time.Duration(latency) * time.Millisecond
	cc.isLatencyOn = true
	cc.mu.Unlock()

	c.JSON(http.StatusOK, gin.H{
		"message": "Latency set to " + ms + "ms",
	})
}

func (cc *ChaosController) setErrorStatus(c *gin.Context) {
	code := c.Param("code")
	status, err := strconv.Atoi(code)
	if err != nil || status < 100 || status > 599 {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "Invalid HTTP status code. Please provide a valid status code (100-599).",
		})
		return
	}

	cc.mu.Lock()
	cc.errorStatus = status
	cc.isErrorStatusOn = true
	cc.mu.Unlock()

	c.JSON(http.StatusOK, gin.H{
		"message": "Error status code set to " + code,
	})
}

func (cc *ChaosController) setHealth(c *gin.Context) {
	cc.mu.Lock()
	cc.isHealthy = false
	cc.mu.Unlock()

	c.JSON(http.StatusOK, gin.H{
		"message": "Health check endpoint disabled",
	})
}

func (cc *ChaosController) disableLatency(c *gin.Context) {
	cc.mu.Lock()
	cc.isLatencyOn = false
	cc.mu.Unlock()

	c.JSON(http.StatusOK, gin.H{
		"message": "Latency disabled",
	})
}

func (cc *ChaosController) disableErrorStatus(c *gin.Context) {
	cc.mu.Lock()
	cc.isErrorStatusOn = false
	cc.mu.Unlock()

	c.JSON(http.StatusOK, gin.H{
		"message": "Error status disabled",
	})
}

func (cc *ChaosController) disableHealth(c *gin.Context) {
	cc.mu.Lock()
	cc.isHealthy = true
	cc.mu.Unlock()

	c.JSON(http.StatusOK, gin.H{
		"message": "Health endpoint enabled",
	})
}

func (cc *ChaosController) getChaosStatus(c *gin.Context) {
	cc.mu.RLock()
	defer cc.mu.RUnlock()

	c.JSON(http.StatusOK, gin.H{
		"latency": map[string]interface{}{
			"enabled": cc.isLatencyOn,
			"value":   cc.latency.Milliseconds(),
		},
		"error_status": map[string]interface{}{
			"enabled": cc.isErrorStatusOn,
			"code":    cc.errorStatus,
		},
	})
}

func (cc *ChaosController) IsHealthy() bool {
	return cc.isHealthy
}

func isChaosControlPath(path string) bool {
	return len(path) >= 6 && path[:6] == "/chaos"
}
