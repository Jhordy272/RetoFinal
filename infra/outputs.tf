output "vpc_id" {
  value = module.network.vpc_id
}

output "public_subnet_id" {
  value = module.network.public_subnet_id
}

# ELB Outputs
output "elb_dns_name" {
  description = "ELB DNS name for accessing the application"
  value       = module.network.elb_dns_name
}

# Database Outputs
output "db_cluster_endpoint" {
  description = "Aurora cluster writer endpoint"
  value       = module.database.cluster_endpoint
}

output "db_reader_endpoint" {
  description = "Aurora cluster reader endpoint"
  value       = module.database.reader_endpoint
}

output "db_port" {
  description = "Database port"
  value       = module.database.cluster_port
}

output "db_name" {
  description = "Database name"
  value       = module.database.database_name
}

output "db_username" {
  description = "Database master username"
  value       = module.database.master_username
}

# Redis Outputs
output "redis_endpoint" {
  value = module.cache.redis_endpoint
}

output "redis_port" {
  value = module.cache.redis_port
}

output "redis_connection_string" {
  value = module.cache.redis_connection_string
}
