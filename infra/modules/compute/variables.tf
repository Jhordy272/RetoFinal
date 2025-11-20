variable "aws_subnet_id" {
    description = "The ID of the AWS subnet"
    type        = string
}

variable "ami_id" {
    description = "The AMI ID to use for the instance"
    type        = string
    default     = "ami-085386e29e44dacd7"
}

variable "instance_type" {
    description = "The type of instance to create"
    type        = string
    default     = "t2.micro"
}

variable "aws_security_group_ids" {
    description = "The security group IDs to associate with the instance"
    type        = list(string)
    default     = []
}

variable "instance_count" {
  description = "Number of EC2 instances to create"
  type        = number
  default     = 1
}

variable "user_data" {
  description = "User data to initialize the instance"
  type        = string
  default     = ""
}