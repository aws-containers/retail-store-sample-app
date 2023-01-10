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
	"context"
	"errors"
	"fmt"
	"log"
	"strings"

	"github.com/aws-containers/retail-store-sample-app/catalog/config"
	"github.com/aws-containers/retail-store-sample-app/catalog/model"
	"github.com/dlmiddlecote/sqlstats"
	"github.com/golang-migrate/migrate/v4"
	_ "github.com/golang-migrate/migrate/v4/database/mysql"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	"github.com/jmoiron/sqlx"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/signalfx/splunk-otel-go/instrumentation/github.com/jmoiron/sqlx/splunksqlx"
)

// ErrNotFound is returned when there is no product for a given ID.
var ErrNotFound = errors.New("not found")

// ErrDBConnection is returned when connection with the database fails.
var ErrDBConnection = errors.New("database connection error")

var baseQuery = "SELECT product.product_id AS id, product.name, product.description, product.price, product.count, product.image_url, GROUP_CONCAT(tag.name) AS tag_name FROM product JOIN product_tag ON product.product_id=product_tag.product_id JOIN tag ON product_tag.tag_id=tag.tag_id"

type mySQLRepository struct {
	db       *sqlx.DB
	readerDb *sqlx.DB
}

func newMySQLRepository(config config.DatabaseConfiguration) (Repository, error) {
	connectionString := fmt.Sprintf("%s:%s@tcp(%s)/%s?timeout=%ds", config.User, config.Password, config.Endpoint, config.Name, config.ConnectTimeout)

	if config.Migrate {
		err := migrateMySQL(connectionString)
		if err != nil {
			log.Println("Error: Failed to run migration", err)
			return nil, err
		}
	}

	var readerDb *sqlx.DB

	db, err := createConnection(config.Endpoint, config.User, config.Password, config.Name, config.ConnectTimeout)
	if err != nil {
		log.Println("Error: Unable to connect to database", err)
		return nil, err
	}

	if len(config.ReadEndpoint) > 0 {

		readerDb, err = createConnection(config.ReadEndpoint, config.User, config.Password, config.Name, config.ConnectTimeout)
		if err != nil {
			log.Println("Error: Unable to connect to reader database", err)
			return nil, err
		}
	} else {
		readerDb = db
	}

	return &mySQLRepository{
		db:       db,
		readerDb: readerDb,
	}, nil
}

func createConnection(endpoint string, username string, password string, name string, timeout int) (*sqlx.DB, error) {
	log.Printf("Connecting to %s/%s?timeout=%ds", endpoint, name, timeout)

	connectionString := fmt.Sprintf("%s:%s@tcp(%s)/%s?timeout=%ds", username, password, endpoint, name, timeout)
	db, err := splunksqlx.Open("mysql", connectionString)
	if err != nil {
		return nil, err
	}

	err = db.Ping()
	if err != nil {
		return nil, err
	}

	return db, nil
}

func migrateMySQL(connectionString string) error {
	log.Println("Running database migration...")

	m, err := migrate.New(
		"file://db/migrations",
		"mysql://"+connectionString,
	)
	if err != nil {
		log.Println("Error: Failed to prep migration", err)
		return err
	}

	if err := m.Up(); err != migrate.ErrNoChange {
		log.Println("Error: Failed to apply migration", err)
		return err
	}

	return nil
}

func (s *mySQLRepository) List(tags []string, order string, pageNum, pageSize int, ctx context.Context) ([]model.Product, error) {
	var products []model.Product
	query := baseQuery

	var args []interface{}

	for i, t := range tags {
		if i == 0 {
			query += " WHERE tag.name=?"
			args = append(args, t)
		} else {
			query += " OR tag.name=?"
			args = append(args, t)
		}
	}

	query += " GROUP BY id"

	if order != "" {
		query += " ORDER BY ?"
		args = append(args, order)
	}

	query += ";"

	err := s.readerDb.SelectContext(ctx, &products, query, args...)
	if err != nil {
		log.Println("database error", err)
		return []model.Product{}, ErrDBConnection
	}
	for i, s := range products {
		products[i].Tags = strings.Split(s.TagString, ",")
	}

	products = cut(products, pageNum, pageSize)

	return products, nil
}

func (s *mySQLRepository) Count(tags []string, ctx context.Context) (int, error) {
	query := "SELECT COUNT(DISTINCT product.product_id) FROM product JOIN product_tag ON product.product_id=product_tag.product_id JOIN tag ON product_tag.tag_id=tag.tag_id"

	var args []interface{}

	for i, t := range tags {
		if i == 0 {
			query += " WHERE tag.name=?"
			args = append(args, t)
		} else {
			query += " OR tag.name=?"
			args = append(args, t)
		}
	}

	query += ";"

	sel, err := s.readerDb.Prepare(query)

	if err != nil {
		log.Println("database error", err)
		return 0, ErrDBConnection
	}
	defer sel.Close()

	var count int
	err = sel.QueryRowContext(ctx, args...).Scan(&count)

	if err != nil {
		log.Println("database error", err)
		return 0, ErrDBConnection
	}

	return count, nil
}

func (s *mySQLRepository) Get(id string, ctx context.Context) (*model.Product, error) {
	query := baseQuery + " WHERE product.product_id =? GROUP BY product.product_id;"

	log.Printf("query: %s", query)

	var product model.Product
	err := s.readerDb.GetContext(ctx, &product, query, id)
	if err != nil {
		log.Println("database error", err)
		return nil, ErrNotFound
	}

	product.Tags = strings.Split(product.TagString, ",")

	return &product, nil
}

func (s *mySQLRepository) Tags(ctx context.Context) ([]model.Tag, error) {
	var tags []model.Tag
	query := "SELECT name, display_name FROM tag;"
	rows, err := s.readerDb.QueryContext(ctx, query)
	if err != nil {
		log.Println("database error", err)
		return []model.Tag{}, ErrDBConnection
	}

	for rows.Next() {
		var tag model.Tag

		err = rows.Scan(&tag.Name, &tag.DisplayName)
		if err != nil {
			log.Println("Error reading tag row", err)
			continue
		}
		tags = append(tags, tag)
	}

	return tags, nil
}

func (s *mySQLRepository) Collector() prometheus.Collector {
	return sqlstats.NewStatsCollector("db", s.db)
}

func (s *mySQLRepository) ReaderCollector() prometheus.Collector {
	return sqlstats.NewStatsCollector("reader_db", s.db)
}

func cut(products []model.Product, pageNum, pageSize int) []model.Product {
	if pageNum == 0 || pageSize == 0 {
		return []model.Product{} // pageNum is 1-indexed
	}
	start := (pageNum * pageSize) - pageSize
	if start > len(products) {
		return []model.Product{}
	}
	end := (pageNum * pageSize)
	if end > len(products) {
		end = len(products)
	}
	return products[start:end]
}

func contains(s []string, e string) bool {
	for _, a := range s {
		if a == e {
			return true
		}
	}
	return false
}
