package test

import (
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"

	"github.com/aws-containers/retail-store-sample-app/catalog/middleware"
)

func setupTestRouter() (*gin.Engine, *middleware.ChaosController) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	cc := middleware.NewChaosController()

	// Add the chaos middleware
	router.Use(cc.ChaosMiddleware())

	// Setup test endpoint
	router.GET("/test", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{"status": "ok"})
	})

	// Setup chaos routes
	cc.SetupChaosRoutes(router)

	return router, cc
}

func TestChaosMiddleware_Latency(t *testing.T) {
	router, _ := setupTestRouter()

	// Test adding latency
	t.Run("Set latency", func(t *testing.T) {
		w := httptest.NewRecorder()
		req, _ := http.NewRequest("POST", "/chaos/latency/1000", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		// Test the endpoint with latency
		start := time.Now()
		w = httptest.NewRecorder()
		req, _ = http.NewRequest("GET", "/test", nil)
		router.ServeHTTP(w, req)
		duration := time.Since(start)

		assert.Equal(t, http.StatusOK, w.Code)
		assert.GreaterOrEqual(t, duration.Milliseconds(), int64(1000))
	})

	// Test disabling latency
	t.Run("Disable latency", func(t *testing.T) {
		w := httptest.NewRecorder()
		req, _ := http.NewRequest("DELETE", "/chaos/latency", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		// Test the endpoint without latency
		start := time.Now()
		w = httptest.NewRecorder()
		req, _ = http.NewRequest("GET", "/test", nil)
		router.ServeHTTP(w, req)
		duration := time.Since(start)

		assert.Equal(t, http.StatusOK, w.Code)
		assert.Less(t, duration.Milliseconds(), int64(1000))
	})
}

func TestChaosMiddleware_ErrorStatus(t *testing.T) {
	router, _ := setupTestRouter()

	t.Run("Set error status", func(t *testing.T) {
		w := httptest.NewRecorder()
		req, _ := http.NewRequest("POST", "/chaos/status/503", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		// Test the endpoint with error status
		w = httptest.NewRecorder()
		req, _ = http.NewRequest("GET", "/test", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusServiceUnavailable, w.Code)
	})

	t.Run("Disable error status", func(t *testing.T) {
		w := httptest.NewRecorder()
		req, _ := http.NewRequest("DELETE", "/chaos/status", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		// Test the endpoint without error status
		w = httptest.NewRecorder()
		req, _ = http.NewRequest("GET", "/test", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)
	})
}

func TestChaosMiddleware_Health(t *testing.T) {
	router, cc := setupTestRouter()

	t.Run("Set unhealthy", func(t *testing.T) {
		w := httptest.NewRecorder()
		req, _ := http.NewRequest("POST", "/chaos/health", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)
		assert.False(t, cc.IsHealthy())
	})

	t.Run("Set healthy", func(t *testing.T) {
		w := httptest.NewRecorder()
		req, _ := http.NewRequest("DELETE", "/chaos/health", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)
		assert.True(t, cc.IsHealthy())
	})
}

func TestChaosMiddleware_InvalidInputs(t *testing.T) {
	router, _ := setupTestRouter()

	t.Run("Invalid latency", func(t *testing.T) {
		w := httptest.NewRecorder()
		req, _ := http.NewRequest("POST", "/chaos/latency/-100", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)
	})

	t.Run("Invalid status code", func(t *testing.T) {
		w := httptest.NewRecorder()
		req, _ := http.NewRequest("POST", "/chaos/status/999", nil)
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)
	})
}
