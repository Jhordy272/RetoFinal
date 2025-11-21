variable "project_name" {
  description = "Project name"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "vpc_cidr" {
  description = "VPC CIDR block"
  type        = string
}

variable "subnet_ids" {
  description = "Subnet IDs for Aurora (at least 2 AZs required)"
  type        = list(string)
}

variable "engine_version" {
  description = "Aurora PostgreSQL engine version"
  type        = string
  default     = "15.5"
}

variable "database_name" {
  description = "Initial database name"
  type        = string
  default     = "key_management"
}

variable "master_username" {
  description = "Master username"
  type        = string
  default     = "postgres"
}

variable "master_password" {
  description = "Master password"
  type        = string
  default     = "postgres123"
}

variable "instance_class" {
  description = "Instance class for Aurora nodes"
  type        = string
  default     = "db.r6g.large"
}
