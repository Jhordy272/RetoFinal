output "instance_ids" {
  description = "IDs of the EC2 instances"
  value       = [for instance in aws_instance.instance : instance.id]
}