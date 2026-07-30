output "state_bucket_name" {
  description = "S3 bucket name for Terraform remote state."
  value       = aws_s3_bucket.terraform_state.bucket
}

output "lock_table_name" {
  description = "DynamoDB table name for Terraform state locking."
  value       = aws_dynamodb_table.terraform_lock.name
}

output "backend_config_example" {
  description = "Backend configuration values to copy into infra/backend.tf."
  value = {
    bucket         = aws_s3_bucket.terraform_state.bucket
    key            = "oficina-dgcar/academic/terraform.tfstate"
    region         = var.aws_region
    dynamodb_table = aws_dynamodb_table.terraform_lock.name
    encrypt        = true
  }
}
