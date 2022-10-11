package controller

import (
	"net/http"
	"strconv"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/niallthomson/microservices-demo/catalog/api"
	"github.com/niallthomson/microservices-demo/catalog/httputil"
	"github.com/niallthomson/microservices-demo/catalog/model"
)

// Controller example
type Controller struct {
	api *api.CatalogAPI
}

// NewController example
func NewController(api *api.CatalogAPI) (*Controller, error) {
	return &Controller{
		api: api,
	}, nil
}

// GetProducts godoc
// @Summary Get catalog
// @Description Get catalog
// @Tags catalog
// @Accept  json
// @Produce  json
// @Param tags query string false "Tagged products to include"
// @Param order query string false "Order of response"
// @Param page query int false "Page number"
// @Param size query int false "Page size"
// @Success 200 {array} model.Product
// @Failure 400 {object} httputil.HTTPError
// @Failure 404 {object} httputil.HTTPError
// @Failure 500 {object} httputil.HTTPError
// @Router /catalogue [get]
func (c *Controller) GetProducts(ctx *gin.Context) {
	var tags []string

	tagString := ctx.Query("tags")
	if len(tagString) > 0 {
		tags = strings.Split(tagString, ",")
	} else {
		tags = []string{}
	}

	order := ctx.Query("order")

	page, err := getQueryInt("page", 1, ctx)
	if err != nil {
		httputil.NewError(ctx, http.StatusBadRequest, err)
		return
	}

	size, err := getQueryInt("size", 10, ctx)
	if err != nil {
		httputil.NewError(ctx, http.StatusBadRequest, err)
		return
	}

	products, err := c.api.GetProducts(tags, order, page, size)
	if err != nil {
		httputil.NewError(ctx, http.StatusNotFound, err)
		return
	}
	ctx.JSON(http.StatusOK, products)
}

// GetProducts godoc
// @Summary Get catalog
// @Description Get catalog
// @Tags catalog
// @Accept  json
// @Produce  json
// @Param id path string true "product ID"
// @Success 200 {object} model.Product
// @Failure 400 {object} httputil.HTTPError
// @Failure 404 {object} httputil.HTTPError
// @Failure 500 {object} httputil.HTTPError
// @Router /catalogue/product/{id} [get]
func (c *Controller) GetProduct(ctx *gin.Context) {
	id := ctx.Param("id")

	product, err := c.api.GetProduct(id)
	if err != nil {
		httputil.NewError(ctx, http.StatusNotFound, err)
		return
	}
	ctx.JSON(http.StatusOK, product)
}

// CatalogSize godoc
// @Summary Get catalog size
// @Description Get catalog size
// @Tags catalog
// @Accept  json
// @Produce  json
// @Param tags query string false "Tagged products to include"
// @Success 200 {object} model.CatalogSizeResponse
// @Failure 400 {object} httputil.HTTPError
// @Failure 404 {object} httputil.HTTPError
// @Failure 500 {object} httputil.HTTPError
// @Router /catalogue/size [get]
func (c *Controller) CatalogSize(ctx *gin.Context) {
	var tags []string

	tagString := ctx.Query("tags")
	if len(tagString) > 0 {
		tags = strings.Split(tagString, ",")
	} else {
		tags = []string{}
	}

	count, err := c.api.GetSize(tags)
	if err != nil {
		httputil.NewError(ctx, http.StatusNotFound, err)
		return
	}
	ctx.JSON(http.StatusOK, model.CatalogSizeResponse{
		Size: count,
	})
}

// ListTags godoc
// @Summary List tags
// @Description get tags
// @Tags catalog
// @Accept  json
// @Produce  json
// @Success 200 {array} model.Tag
// @Failure 400 {object} httputil.HTTPError
// @Failure 404 {object} httputil.HTTPError
// @Failure 500 {object} httputil.HTTPError
// @Router /catalogue/tags [get]
func (c *Controller) ListTags(ctx *gin.Context) {
	accounts, err := c.api.GetTags()
	if err != nil {
		httputil.NewError(ctx, http.StatusNotFound, err)
		return
	}
	ctx.JSON(http.StatusOK, accounts)
}

func getQueryInt(name string, defaultValue int, ctx *gin.Context) (int, error) {
	str := ctx.Query(name)

	if len(str) > 0 {
		return strconv.Atoi(str)
	}

	return defaultValue, nil
}
