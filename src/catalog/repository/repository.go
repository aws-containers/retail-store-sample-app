package repository

import (
	"context"
	"fmt"

	"github.com/aws-containers/retail-store-sample-app/catalog/config"
	"github.com/aws-containers/retail-store-sample-app/catalog/model"
	"gorm.io/driver/mysql"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"gorm.io/plugin/opentelemetry/tracing"
)

type Database struct {
	DB *gorm.DB
}

type CatalogRepository interface {
	GetProducts(tags []string, order string, pageNum, pageSize int, ctx context.Context) ([]model.Product, error)
	CountProducts(tags []string, ctx context.Context) (int, error)
	GetProduct(id string, ctx context.Context) (*model.Product, error)
	GetTags(ctx context.Context) ([]model.Tag, error)
}

func createMySQLDatabase(config config.DatabaseConfiguration) (*gorm.DB, error) {
	connectionString := fmt.Sprintf("%s:%s@tcp(%s)/%s?timeout=%ds&charset=utf8mb4&parseTime=True&loc=Local", config.User, config.Password, config.Endpoint, config.Name, config.ConnectTimeout)
	return gorm.Open(mysql.Open(connectionString), &gorm.Config{})
}

func NewRepository(config config.DatabaseConfiguration) (CatalogRepository, error) {
	var db *gorm.DB
	var err error

	if config.Type == "mysql" {
		fmt.Printf("Using mysql database %s\n", config.Endpoint)
		db, err = createMySQLDatabase(config)
	} else {
		fmt.Println("Using in-memory database")
		db, err = gorm.Open(sqlite.Open("file::memory:?cache=shared"), &gorm.Config{})
	}

	if err != nil {
		panic("failed to connect database")
	}

	if err := db.Use(tracing.NewPlugin(tracing.WithoutMetrics())); err != nil {
		panic(err)
	}

	fmt.Println("Running database migration...")

	// Migrate the schema
	db.AutoMigrate(&model.Product{})

	fmt.Println("Database migration complete")

	products, err := LoadProductData()
	if err != nil {
		fmt.Printf("Error: %v\n", err)
		return nil, err
	}

	tags, err := LoadProductTagData()
	if err != nil {
		fmt.Printf("Error: %v\n", err)
		return nil, err
	}

	tagMap := make(map[string]model.Tag)

	for _, tag := range tags {
		tagEntity := model.Tag{Name: tag.Name, DisplayName: tag.DisplayName}
		db.Save(tagEntity)

		tagMap[tag.Name] = tagEntity
	}

	for _, product := range products {
		var result model.Product
		r := db.
			Where("id = ?", product.ID).
			Limit(1).
			Find(&result)

		if r.RowsAffected > 0 {
			continue
		}

		productTags := []model.Tag{}
		for _, tag := range product.Tags {
			productTags = append(productTags, tagMap[tag])
		}

		db.Create(&model.Product{
			ID:          product.ID,
			Name:        product.Name,
			Description: product.Description,
			Price:       product.Price,
			Tags:        productTags,
		})
	}

	return &Database{
		DB: db,
	}, nil
}

func (db *Database) GetProducts(tags []string, order string, pageNum, pageSize int, ctx context.Context) ([]model.Product, error) {
	products := []model.Product{}

	query := db.DB.Preload("Tags")

	// Apply tags filter if provided
	if len(tags) > 0 {
		query = query.Joins("JOIN product_tags ON product_tags.product_id = products.id").
			Joins("JOIN tags ON tags.name = product_tags.tag_name").
			Where("tags.name IN ?", tags).
			Group("products.id")
	}

	// Apply ordering
	switch order {
	case "price_asc":
		query = query.Order("price asc")
	case "price_desc":
		query = query.Order("price desc")
	default:
		query = query.Order("products.name asc") // default ordering
	}

	// Apply pagination
	offset := (pageNum - 1) * pageSize
	query = query.Offset(offset).Limit(pageSize)

	// Execute the query
	err := query.WithContext(ctx).Find(&products).Error
	if err != nil {
		return nil, err
	}

	return products, err
}

func (db *Database) GetProduct(id string, ctx context.Context) (*model.Product, error) {
	product := model.Product{}

	err := db.DB.WithContext(ctx).
		Preload("Tags").
		Where("id = ?", id).
		First(&product).Error

	if err != nil {
		return nil, err
	}

	return &product, err
}

func (db *Database) CountProducts(tags []string, ctx context.Context) (int, error) {
	var count int64

	query := db.DB.WithContext(ctx).Model(&model.Product{})

	// Apply tags filter if provided
	if len(tags) > 0 {
		query = query.Joins("JOIN product_tags ON product_tags.product_id = products.id").
			Joins("JOIN tags ON tags.name = product_tags.tag_name").
			Where("tags.name IN ?", tags).
			Group("products.id")
	}

	err := query.Count(&count).Error
	if err != nil {
		return 0, err
	}

	return int(count), nil
}

func (db *Database) GetTags(ctx context.Context) ([]model.Tag, error) {
	tags := []model.Tag{}

	err := db.DB.WithContext(ctx).
		Model(&model.Tag{}).
		Order("display_name asc"). // Order by display name alphabetically
		Find(&tags).Error

	if err != nil {
		return nil, fmt.Errorf("failed to fetch tags: %w", err)
	}

	return tags, err
}
