resource "aws_route_table" "public_route_table" {
  vpc_id = var.var_vpc

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = var.var_igw
  }

  route {
    cidr_block = var.var_local_cidr_block
    gateway_id = "local"
  }

  tags = {
    Name = "public_route_table"
  }
}

resource "aws_route_table" "private_route_table" {
  vpc_id = var.var_vpc

  route {
    cidr_block = var.var_local_cidr_block
    gateway_id = "local"
  }

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = var.var_nat_gw
  }

  tags = {
    Name = "private_route_table"
  }
}