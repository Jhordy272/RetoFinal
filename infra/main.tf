module "network" {
  source             = "./modules/network"
  project_name       = var.project_name
  vpc_cidr           = "10.0.0.0/16"
  public_subnet_cidr = "10.0.1.0/24"
  availability_zone  = "us-east-1a"
}

module "bastion" {
  source                 = "./modules/compute"
  depends_on             = [module.network]
  aws_subnet_id          = module.network.public_subnet_id
  aws_security_group_ids = module.network.security_groups_ids
  instance_count         = 3
  user_data              = "bastion"
  instance_type          = "m6id.xlarge"
}

module "database" {
  source          = "./modules/database"
  depends_on      = [module.network]
  project_name    = var.project_name
  vpc_id          = module.network.vpc_id
  vpc_cidr        = module.network.vpc_cidr
  subnet_ids      = module.network.public_subnet_ids
  engine_version  = "16.1"
  database_name   = "key_management"
  master_username = "postgres"
  master_password = "postgres_password"
  instance_class  = "db.r6g.large"
}

module "cache" {
  source                     = "./modules/cache"
  depends_on                 = [module.bastion]
  project_name               = var.project_name
  vpc_id                     = module.network.vpc_id
  vpc_cidr                   = module.network.vpc_cidr
  subnet_id                  = module.network.public_subnet_id
  engine_version             = "7.1"
  node_type                  = "cache.r6g.xlarge"
  num_node_groups            = 1
  replicas_per_node_group    = 1
  snapshot_retention_limit   = 5
  snapshot_window            = "03:00-05:00"
  maintenance_window         = "sun:05:00-sun:07:00"
  automatic_failover_enabled = true
}

resource "aws_elb_attachment" "elb_attachment" {
  depends_on = [module.bastion]
  count      = length(module.bastion.instance_ids)
  elb        = module.network.elb_name
  instance   = module.bastion.instance_ids[count.index]
}
