####################################################################
# Worker node group (unmanaged, ASG-based via CloudFormation)
####################################################################

resource "tls_private_key" "node_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "local_sensitive_file" "node_pem" {
  filename        = pathexpand("~/.ssh/retail-store-eks.pem")
  file_permission = "600"
  content         = tls_private_key.node_key.private_key_pem
}

resource "aws_key_pair" "nodes" {
  key_name   = "${var.cluster_name}-nodes"
  public_key = trimspace(tls_private_key.node_key.public_key_openssh)
}

# ---- IAM: node instance role ----

data "aws_iam_policy_document" "assume_ec2" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "node_instance_role" {
  name               = var.node_role_name
  assume_role_policy = data.aws_iam_policy_document.assume_ec2.json
  path               = "/"
}

resource "aws_iam_role_policy_attachment" "node_eks_worker" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
  role       = aws_iam_role.node_instance_role.name
}

resource "aws_iam_role_policy_attachment" "node_eks_cni" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
  role       = aws_iam_role.node_instance_role.name
}

resource "aws_iam_role_policy_attachment" "node_ecr_ro" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.node_instance_role.name
}

resource "aws_iam_role_policy_attachment" "node_ssm" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
  role       = aws_iam_role.node_instance_role.name
}

resource "aws_iam_role_policy_attachment" "node_additional" {
  policy_arn = aws_iam_policy.node_additional.arn
  role       = aws_iam_role.node_instance_role.name
}

resource "aws_iam_instance_profile" "nodes" {
  name = "${var.cluster_name}-NodeInstanceProfile"
  path = "/"
  role = aws_iam_role.node_instance_role.id
}

# ---- Security group ----

resource "aws_security_group" "nodes" {
  name        = "${var.cluster_name}-NodeSG"
  description = "Security group for all worker nodes"
  vpc_id      = data.aws_vpc.default.id

  tags = {
    "kubernetes.io/cluster/${var.cluster_name}" = "owned"
  }
}

resource "aws_vpc_security_group_ingress_rule" "nodes_self" {
  description                  = "Nodes can communicate with each other"
  ip_protocol                  = "-1"
  security_group_id            = aws_security_group.nodes.id
  referenced_security_group_id = aws_security_group.nodes.id
}

resource "aws_vpc_security_group_egress_rule" "nodes_egress_all" {
  description       = "Allow all outbound traffic"
  ip_protocol       = "-1"
  cidr_ipv4         = "0.0.0.0/0"
  security_group_id = aws_security_group.nodes.id
}

resource "aws_vpc_security_group_ingress_rule" "nodes_from_control_plane" {
  description                  = "Control plane → kubelet + pods"
  security_group_id            = aws_security_group.nodes.id
  referenced_security_group_id = aws_eks_cluster.retail_store.vpc_config[0].cluster_security_group_id
  from_port                    = 1025
  to_port                      = 65535
  ip_protocol                  = "TCP"
}

resource "aws_vpc_security_group_ingress_rule" "nodes_443_from_control_plane" {
  description                  = "Control plane → extension API server (443)"
  security_group_id            = aws_security_group.nodes.id
  referenced_security_group_id = aws_eks_cluster.retail_store.vpc_config[0].cluster_security_group_id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "TCP"
}

resource "aws_vpc_security_group_ingress_rule" "control_plane_from_nodes_443" {
  description                  = "Nodes → cluster API (443)"
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "TCP"
  referenced_security_group_id = aws_security_group.nodes.id
  security_group_id            = aws_eks_cluster.retail_store.vpc_config[0].cluster_security_group_id
}

resource "aws_vpc_security_group_egress_rule" "control_plane_to_nodes" {
  description                  = "Control plane → kubelet + pods"
  referenced_security_group_id = aws_security_group.nodes.id
  security_group_id            = aws_eks_cluster.retail_store.vpc_config[0].cluster_security_group_id
  from_port                    = 1025
  to_port                      = 65535
  ip_protocol                  = "TCP"
}

resource "aws_vpc_security_group_egress_rule" "control_plane_to_nodes_443" {
  description                  = "Control plane → extension API servers"
  referenced_security_group_id = aws_security_group.nodes.id
  security_group_id            = aws_eks_cluster.retail_store.vpc_config[0].cluster_security_group_id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "TCP"
}

# ---- Launch template + ASG (via CloudFormation for UpdatePolicy support) ----

resource "aws_launch_template" "nodes" {
  name = "${var.cluster_name}-NodeLT"

  block_device_mappings {
    device_name = "/dev/xvda"
    ebs {
      delete_on_termination = true
      volume_size           = 30
      volume_type           = "gp3"
    }
  }

  iam_instance_profile {
    name = aws_iam_instance_profile.nodes.name
  }

  key_name               = aws_key_pair.nodes.key_name
  instance_type          = var.node_instance_type
  vpc_security_group_ids = [aws_security_group.nodes.id]
  image_id               = data.aws_ssm_parameter.node_ami.value

  metadata_options {
    http_put_response_hop_limit = 2
    http_endpoint               = "enabled"
    http_tokens                 = "optional"
  }

  tag_specifications {
    resource_type = "instance"
    tags = {
      Name = "${var.cluster_name}-worker-node"
    }
  }

  user_data = base64encode(<<-EOF
    #!/bin/bash
    set -o xtrace
    /etc/eks/bootstrap.sh ${var.cluster_name}
    /opt/aws/bin/cfn-signal --exit-code $? \
      --stack  ${var.cluster_name}-stack \
      --resource NodeGroup \
      --region ${var.aws_region}
    EOF
  )
}

resource "time_sleep" "wait_for_lt" {
  depends_on      = [aws_launch_template.nodes]
  create_duration = "30s"
}

resource "aws_cloudformation_stack" "node_asg" {
  depends_on = [time_sleep.wait_for_lt]
  name       = "${var.cluster_name}-stack"

  template_body = <<-EOF
    Description: "Retail Store EKS node autoscaler"
    Resources:
      NodeGroup:
        Type: AWS::AutoScaling::AutoScalingGroup
        Properties:
          VPCZoneIdentifier: ["${data.aws_subnets.public.ids[0]}","${data.aws_subnets.public.ids[1]}","${data.aws_subnets.public.ids[2]}"]
          MinSize: "${var.node_group_min_size}"
          MaxSize: "${var.node_group_max_size}"
          DesiredCapacity: "${var.node_group_desired_capacity}"
          HealthCheckType: EC2
          LaunchTemplate:
            LaunchTemplateId: "${aws_launch_template.nodes.id}"
            Version: "${aws_launch_template.nodes.latest_version}"
        UpdatePolicy:
          AutoScalingScheduledAction:
            IgnoreUnmodifiedGroupSizeProperties: true
          AutoScalingRollingUpdate:
            MaxBatchSize: 1
            MinInstancesInService: "${var.node_group_desired_capacity}"
            PauseTime: PT5M
    Outputs:
      NodeAutoScalingGroup:
        Value: !Ref NodeGroup
  EOF
}
