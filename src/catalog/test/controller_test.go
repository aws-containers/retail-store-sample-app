package test

import (
	"encoding/json"
	"net/http"
	"testing"

	"github.com/stretchr/testify/assert"

	"github.com/aws-containers/retail-store-sample-app/catalog/model"
)

func TestCatalogList(t *testing.T) {
	writer := makeRequest("GET", "/catalogue", nil)

	assert.Equal(t, http.StatusOK, writer.Code)

	var response []model.Product
	json.Unmarshal(writer.Body.Bytes(), &response)

	assert.Equal(t, 6, len(response))
}

func TestCatalogProduct(t *testing.T) {
	writer := makeRequest("GET", "/catalogue/product/6d62d909-f957-430e-8689-b5129c0bb75e", nil)

	assert.Equal(t, http.StatusOK, writer.Code)

	var response model.Product
	json.Unmarshal(writer.Body.Bytes(), &response)

	assert.Equal(t, "Pocket Watch", response.Name)
}

func TestCatalogProductMissing(t *testing.T) {
	writer := makeRequest("GET", "/catalogue/product/missing", nil)

	assert.Equal(t, http.StatusNotFound, writer.Code)
}
