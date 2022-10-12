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
