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

package config

// Configuration exported
type AppConfiguration struct {
	Port     int `env:"PORT,default=8080"`
	Database DatabaseConfiguration
}

// DatabaseConfiguration exported
type DatabaseConfiguration struct {
	Type           string `env:"DB_TYPE,default=mysql"`
	Endpoint       string `env:"DB_ENDPOINT,default=catalog-db:3306"`
	ReadEndpoint   string `env:"DB_READ_ENDPOINT"`
	Name           string `env:"DB_NAME,default=sampledb"`
	User           string `env:"DB_USER,default=catalog_user"`
	Password       string `env:"DB_PASSWORD"`
	Migrate        bool   `env:"DB_MIGRATE,default=true"`
	ConnectTimeout int    `env:"DB_CONNECT_TIMEOUT,default=5"`
}
