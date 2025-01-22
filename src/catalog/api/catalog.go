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

package api

import (
	"context"

	"github.com/aws-containers/retail-store-sample-app/catalog/model"
	"github.com/aws-containers/retail-store-sample-app/catalog/repository"
)

// CatalogAPI type
type CatalogAPI struct {
	repository repository.CatalogRepository
}

func (a *CatalogAPI) GetProducts(tags []string, order string, pageNum, pageSize int, ctx context.Context) ([]model.Product, error) {
	products, err := a.repository.GetProducts(tags, order, pageNum, pageSize, ctx)
	if err != nil {
		return nil, err
	}
	return products, nil
}

func (a *CatalogAPI) GetProduct(id string, ctx context.Context) (*model.Product, error) {
	return a.repository.GetProduct(id, ctx)
}

func (a *CatalogAPI) GetTags(ctx context.Context) ([]model.Tag, error) {
	return a.repository.GetTags(ctx)
}

func (a *CatalogAPI) GetSize(tags []string, ctx context.Context) (int, error) {
	return a.repository.CountProducts(tags, ctx)
}

// NewCatalogAPI constructor
func NewCatalogAPI(repository repository.CatalogRepository) (*CatalogAPI, error) {
	return &CatalogAPI{
		repository: repository,
	}, nil
}
