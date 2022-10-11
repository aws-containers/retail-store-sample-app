package model

// Product exported
type Product struct {
	ID          string   `json:"id" db:"id"`
	Name        string   `json:"name" db:"name"`
	Description string   `json:"description" db:"description"`
	ImageURL    string   `json:"imageUrl" db:"image_url"`
	Price       int      `json:"price" db:"price"`
	Count       int      `json:"count" db:"count"`
	Tags        []string `json:"tag" db:"-"`
	TagString   string   `json:"-" db:"tag_name"`
}

type CatalogSizeResponse struct {
	Size int `json:"size"`
}
