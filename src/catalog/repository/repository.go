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

package repository

import (
	"fmt"

	"github.com/aws-containers/retail-store-sample-app/catalog/config"
	"github.com/aws-containers/retail-store-sample-app/catalog/model"
	"github.com/prometheus/client_golang/prometheus"
)

type Repository interface {
	List(tags []string, order string, pageNum, pageSize int) ([]model.Product, error)
	Count(tags []string) (int, error)
	Get(id string) (*model.Product, error)
	Tags() ([]model.Tag, error)
	Collector() prometheus.Collector
	ReaderCollector() prometheus.Collector
}

func NewRepository(config config.DatabaseConfiguration) (Repository, error) {
	if config.Type == "mysql" {
		return newMySQLRepository(config)
	}

	return nil, fmt.Errorf("Unknown database type: %s", config.Type)
}
