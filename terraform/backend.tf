terraform {
  backend "s3" {
    bucket         = "your-terraform-state-bucket" # Create this manually first
    key            = "project-bedrock/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-state-locking"    # Create this manually first
    encrypt        = true
  }
}