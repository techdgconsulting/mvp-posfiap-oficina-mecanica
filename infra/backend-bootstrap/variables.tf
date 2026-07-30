variable "aws_region" {
  description = "AWS region where the Terraform backend resources will be created."
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name used as a prefix for backend resources."
  type        = string
  default     = "oficina-dgcar"
}

variable "environment" {
  description = "Environment name for backend resource naming."
  type        = string
  default     = "academic"
}

variable "state_bucket_name" {
  description = "Globally unique S3 bucket name for Terraform state."
  type        = string
}

variable "lock_table_name" {
  description = "DynamoDB table name for Terraform state locking."
  type        = string
  default     = "oficina-dgcar-academic-terraform-lock"
}
