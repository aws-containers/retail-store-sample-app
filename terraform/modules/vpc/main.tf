resource "aws_vpc" "main" {
    cidr_block = var.vpc_cidr
    enable_dns_support   = true
    enable_dns_hostnames = true
    tags = {
        Name        = "${var.project_name}-${var.environment}-vpc"
    }
}

resource "aws_subnet" "public_subnets" {
  for_each = var.public_subnet

    vpc_id            = aws_vpc.main.id
    cidr_block        = each.value.cidr_block
    availability_zone = each.value.availability_zone
    map_public_ip_on_launch = true
    tags = {
    Name = "${var.project_name}-${var.environment}-public-${each.key}"

    "kubernetes.io/role/elb" = "1"
  }
}

resource "aws_subnet" "private_subnets" {
  for_each = var.private_subnet

    vpc_id            = aws_vpc.main.id
    cidr_block        = each.value.cidr_block
    availability_zone = each.value.availability_zone
    map_public_ip_on_launch = false
    tags = {
    Name = "${var.project_name}-${var.environment}-private-${each.key}"
  }
}

resource "aws_internet_gateway" "main" {
    vpc_id = aws_vpc.main.id
    tags = {
        Name = "${var.project_name}-${var.environment}-igw"
    }
}

resource "aws_route_table" "public" {
    vpc_id = aws_vpc.main.id
    tags = {
        Name = "${var.project_name}-${var.environment}-public-route-table"
    }
}

resource "aws_route" "public_internet_access" {
    route_table_id         = aws_route_table.public.id
    destination_cidr_block = "0.0.0.0/0"
    gateway_id             = aws_internet_gateway.main.id
}

resource "aws_route_table_association" "public_subnet_association" {
  for_each = aws_subnet.public_subnets

    subnet_id      = each.value.id
    route_table_id = aws_route_table.public.id
}

resource "aws_route_table" "private" {
    vpc_id = aws_vpc.main.id
    tags = {
        Name = "${var.project_name}-${var.environment}-private-route-table"
    }
  
}

resource "aws_route_table_association" "private_rta" {
    for_each = aws_subnet.private_subnets
    subnet_id = each.value.id
    route_table_id = aws_route_table.private.id
    
}