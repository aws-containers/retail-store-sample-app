# Create public and private subnets in the VPC
resource "aws_subnet" "public_subnet" {
  vpc_id = var.var_vpc_id

  for_each = toset(var.var_subnet_az_id)

  availability_zone_id = each.value

  cidr_block = cidrsubnet(var.var_vpc_cidr, 8, index(var.var_subnet_az_id, each.key))

  tags = {
    Name = "public_subnet"
  }
}

resource "aws_subnet" "private_subnet" {
  vpc_id = var.var_vpc_id

  for_each = toset(var.var_subnet_az_id)

  availability_zone_id = each.value

  cidr_block = cidrsubnet(var.var_vpc_cidr,
    8,
  index(var.var_subnet_az_id, each.key) + length(var.var_subnet_az_id))

  tags = {
    Name = "private_subnet"
  }
}