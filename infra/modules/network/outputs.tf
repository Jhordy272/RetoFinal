output "vpc_id" {
  value = aws_vpc.main.id
}

output "vpc_cidr" {
  value = aws_vpc.main.cidr_block
}

output "public_subnet_id" {
  value = aws_subnet.public.id
}

output "public_subnet_ids" {
  value = [aws_subnet.public.id, aws_subnet.public_2.id]
}

output "security_groups_ids" {
  value = [aws_security_group.bastion.id]
}

output "elb_name" {
  value = aws_elb.elb.name
}

output "elb_dns_name" {
  value = aws_elb.elb.dns_name
}
