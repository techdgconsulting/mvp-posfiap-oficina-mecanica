output "state_bucket_name" {
  description = "S3 bucket name for Terraform remote state."
  value       = aws_s3_bucket.terraform_state.bucket
}

output "backend_config_example" {
  description = "Backend configuration values to copy into infra/backend.tf."
  value = {
    bucket       = aws_s3_bucket.terraform_state.bucket
    key          = "oficina-dgcar/academic/terraform.tfstate"
    region       = var.aws_region
    encrypt      = true
    use_lockfile = true
  }
}
