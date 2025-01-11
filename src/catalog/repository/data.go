package repository

import (
	"encoding/json"
	"fmt"
	"os"
)

type ProductData struct {
	Name        string   `json:"name"`
	Description string   `json:"description"`
	ID          string   `json:"id"`
	Price       int      `json:"price"`
	Tags        []string `json:"tags"`
}

type ProductTagData struct {
	Name        string `json:"name"`
	DisplayName string `json:"displayName"`
}

func LoadProductData() ([]ProductData, error) {
	// Read the file
	data, err := os.ReadFile("data/products.json")
	if err != nil {
		return nil, fmt.Errorf("error reading file: %v", err)
	}

	// Create a slice to hold the products
	var products []ProductData

	// Unmarshal JSON array into the slice
	err = json.Unmarshal(data, &products)
	if err != nil {
		return nil, fmt.Errorf("error parsing JSON: %v", err)
	}

	return products, nil
}

func LoadProductTagData() ([]ProductTagData, error) {
	// Read the file
	data, err := os.ReadFile("data/tags.json")
	if err != nil {
		return nil, fmt.Errorf("error reading file: %v", err)
	}

	// Create a slice to hold the products
	var productTags []ProductTagData

	// Unmarshal JSON array into the slice
	err = json.Unmarshal(data, &productTags)
	if err != nil {
		return nil, fmt.Errorf("error parsing JSON: %v", err)
	}

	return productTags, nil
}
