package test

import (
	"encoding/json"
	"net/http"
	"testing"

	"github.com/stretchr/testify/assert"

	"github.com/aws-containers/retail-store-sample-app/catalog/model"
)

func TestCatalogList(t *testing.T) {
	writer := makeRequest("GET", "/catalog", nil)

	assert.Equal(t, http.StatusOK, writer.Code)

	var response []model.Product
	json.Unmarshal(writer.Body.Bytes(), &response)

	assert.Equal(t, 10, len(response))
}

func TestCatalogProduct(t *testing.T) {
	writer := makeRequest("GET", "/catalog/product/cc789f85-1476-452a-8100-9e74502198e0", nil)

	assert.Equal(t, http.StatusOK, writer.Code)

	var response model.Product
	json.Unmarshal(writer.Body.Bytes(), &response)

	assert.Equal(t, "Temporal Tickstopper", response.Name)
}

func TestCatalogProductMissing(t *testing.T) {
	writer := makeRequest("GET", "/catalog/product/missing", nil)

	assert.Equal(t, http.StatusNotFound, writer.Code)
}
