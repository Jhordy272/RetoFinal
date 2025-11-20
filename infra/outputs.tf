output "vpc_id" {
  value = module.network.vpc_id
}

output "public_subnet_id" {
  value = module.network.public_subnet_id
}

output "redis_endpoint" {
  value = module.cache.redis_endpoint
}

output "redis_port" {
  value = module.cache.redis_port
}

output "redis_connection_string" {
  value = module.cache.redis_connection_string
}
