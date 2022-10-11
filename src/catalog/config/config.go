package config

// Configuration exported
type AppConfiguration struct {
	Port      int    `env:"PORT,default=8080"`
	ImagePath string `env:"IMAGE_PATH,default=./images/"`
	Database  DatabaseConfiguration
}

// DatabaseConfiguration exported
type DatabaseConfiguration struct {
	Type         string `env:"DB_TYPE,default=mysql"`
	Endpoint     string `env:"DB_ENDPOINT,default=catalog-db:3306"`
	ReadEndpoint string `env:"DB_READ_ENDPOINT"`
	Name         string `env:"DB_NAME,default=sampledb"`
	User         string `env:"DB_USER,default=catalog_user"`
	Password     string `env:"DB_PASSWORD"`
	Migrate      bool   `env:"DB_MIGRATE,default=true"`
}
