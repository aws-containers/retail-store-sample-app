resource "aws_internet_gateway" "igw" {
  vpc_id = var.var_igw_vpc_id
  tags = {
    Name = "group5_igw"
  }
}