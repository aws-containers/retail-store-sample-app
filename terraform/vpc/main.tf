# Create a VPC with public and private subnets across multiple availability zones
resource "aws_vpc" "vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
}

# Create subnets using the subnet module
module "subnet" {
  source           = "./modules/subnet"
  var_vpc_cidr     = aws_vpc.vpc.cidr_block
  var_subnet_az_id = var.vpc_subnet_az_id
  var_vpc_id       = aws_vpc.vpc.id
}

# Create an Internet Gateway using the igw module
module "igw" {
  source         = "./modules/igw"
  var_igw_vpc_id = aws_vpc.vpc.id
}

# Create a NAT Gateway using the nat_gw module
module "nat_gw" {
  source              = "./modules/nat_gw"
  var_nat_gw_location = module.subnet.output_public_subnet_ids[0]
}

# Create route tables using the route-table module
module "route-table" {
  source               = "./modules/route-table"
  var_vpc              = aws_vpc.vpc.id
  var_igw              = module.igw.output_igw_id
  var_nat_gw           = module.nat_gw.output_first_nat_gateway_id
  var_local_cidr_block = aws_vpc.vpc.cidr_block
}

# Associate route tables with subnets
resource "aws_route_table_association" "public_route_table_association" {
  for_each       = module.subnet.output_public_subnet
  subnet_id      = each.value.id
  route_table_id = module.route-table.output_public_route_table_id
}

resource "aws_route_table_association" "private_route_table_association" {
  for_each       = module.subnet.output_private_subnet
  subnet_id      = each.value.id
  route_table_id = module.route-table.output_private_route_table_id
}

# Create network ACLs for public and private subnets
resource "aws_network_acl" "private_subnet_network_acl" {
  vpc_id = aws_vpc.vpc.id
  ingress {
    protocol   = "-1"
    rule_no    = 100
    action     = "allow"
    cidr_block = "0.0.0.0/0"
    from_port  = 0
    to_port    = 0
  }
  egress {
    protocol   = "-1"
    rule_no    = 100
    action     = "allow"
    cidr_block = "0.0.0.0/0"
    from_port  = 0
    to_port    = 0
  }
  tags = {
    Name = "private_subnet_network_acl"
  }
}

resource "aws_network_acl" "public_subnet_network_acl" {
  vpc_id = aws_vpc.vpc.id
  ingress {
    protocol   = "-1"
    rule_no    = 100
    action     = "allow"
    cidr_block = "0.0.0.0/0"
    from_port  = 0
    to_port    = 0
  }
  egress {
    protocol   = "-1"
    rule_no    = 100
    action     = "allow"
    cidr_block = "0.0.0.0/0"
    from_port  = 0
    to_port    = 0
  }
  tags = {
    Name = "public_subnet_network_acl"
  }
}

resource "aws_network_acl_association" "public_subnet_network_acl_association" {
  for_each       = module.subnet.output_public_subnet
  subnet_id      = each.value.id
  network_acl_id = aws_network_acl.public_subnet_network_acl.id
}

resource "aws_network_acl_association" "private_subnet_network_acl_association" {
  for_each       = module.subnet.output_private_subnet
  subnet_id      = each.value.id
  network_acl_id = aws_network_acl.private_subnet_network_acl.id
}
