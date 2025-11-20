# Creates an EC2 instance in AWS
resource "aws_instance" "instance" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  subnet_id              = var.aws_subnet_id
  vpc_security_group_ids = var.aws_security_group_ids
  count                  = var.instance_count
  key_name               = "ec2"

  user_data = <<-EOF
            #!/bin/bash
            sudo yum update -y

            sudo amazon-linux-extras enable docker
            sudo yum install -y docker

            sudo systemctl start docker
            sudo systemctl enable docker

            sudo usermod -aG docker ec2-user
            EOF


  tags = {
    Name = "Instance-${var.user_data}-${count.index}"
  }
}
