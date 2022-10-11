package api

import (
	"github.com/niallthomson/microservices-demo/catalog/model"
	"github.com/niallthomson/microservices-demo/catalog/repository"
)

// CatalogAPI type
type CatalogAPI struct {
	repository repository.Repository
}

func (a *CatalogAPI) GetProducts(tags []string, order string, pageNum, pageSize int) ([]model.Product, error) {
	return a.repository.List(tags, order, pageNum, pageSize)
}

func (a *CatalogAPI) GetProduct(id string) (*model.Product, error) {
	return a.repository.Get(id)
}

func (a *CatalogAPI) GetTags() ([]model.Tag, error) {
	return a.repository.Tags()
}

func (a *CatalogAPI) GetSize(tags []string) (int, error) {
	return a.repository.Count(tags)
}

// NewCatalogAPI constructor
func NewCatalogAPI(repository repository.Repository) (*CatalogAPI, error) {
	/*repository, err := repository.NewRepository(configuration)
	if err != nil {
		log.Println("Error creating catalog API", err)
		return nil, err
	}*/

	return &CatalogAPI{
		repository: repository,
	}, nil
}
