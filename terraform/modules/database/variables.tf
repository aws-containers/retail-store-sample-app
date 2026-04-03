variable "identifier" {
  description = "Identifier for the RDS instance."
  type        = string
}

variable "db_name" {
  description = "Initial database name."
  type        = string
}

variable "username" {
  description = "Master username for the database."
  type        = string
}

variable "password" {
  description = "Master password for the database."
  type        = string
  sensitive   = true
}

variable "engine" {
  description = "Database engine."
  type        = string
  default     = "mysql"
}

variable "engine_version" {
  description = "Version of the database engine."
  type        = string
  default     = "8.0"
}

variable "instance_class" {
  description = "RDS instance class."
  type        = string
  default     = "db.t3.medium"
}

variable "allocated_storage" {
  description = "Initial allocated storage in GB."
  type        = number
  default     = 20
}

variable "max_allocated_storage" {
  description = "Maximum storage autoscaling limit in GB."
  type        = number
  default     = 100
}

variable "port" {
  description = "Database port."
  type        = number
  default     = 3306
}

variable "vpc_id" {
  description = "VPC ID where RDS will be deployed."
  type        = string
}

variable "subnet_ids" {
  description = "Private subnet IDs used by RDS subnet group."
  type        = list(string)
}

variable "allowed_cidr_blocks" {
  description = "CIDR blocks that can access the database port."
  type        = list(string)
  default     = ["10.0.0.0/16"]
}

variable "allowed_security_group_ids" {
  description = "Security group IDs that can access the database port."
  type        = list(string)
  default     = []
}

variable "multi_az" {
  description = "Whether to deploy in multiple availability zones."
  type        = bool
  default     = true
}

variable "publicly_accessible" {
  description = "Whether the DB instance has a public endpoint."
  type        = bool
  default     = false
}

variable "backup_retention_period" {
  description = "Number of days to retain backups."
  type        = number
  default     = 3
}

variable "skip_final_snapshot" {
  description = "Whether to skip final snapshot on deletion."
  type        = bool
  default     = true
}

variable "tags" {
  description = "Tags to apply to database resources."
  type        = map(string)
  default     = {}
}
