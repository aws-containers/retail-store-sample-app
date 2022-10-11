package model

// Tag exported
type Tag struct {
	Name        string `json:"name" db:"name"`
	DisplayName string `json:"displayName" db:"display_name"`
}
