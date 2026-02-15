################################################################################
# 4.5 Event-Driven Extension (Serverless)
################################################################################

# Random string for bucket uniqueness (optional, but using student ID as requested)
resource "random_id" "bucket_suffix" {
  byte_length = 4
}

locals {
  bucket_name = lower("bedrock-assets-${var.student_id}")
}

# S3 Bucket
resource "aws_s3_bucket" "assets" {
  bucket = local.bucket_name

  tags = {
    Project = "Bedrock"
  }
}

# Block public access
resource "aws_s3_bucket_public_access_block" "assets" {
  bucket = aws_s3_bucket.assets.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# IAM Role for Lambda
resource "aws_iam_role" "lambda_role" {
  name = "bedrock_asset_processor_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

# Attach basic execution policy (logs)
resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# Zip the Lambda code
data "archive_file" "lambda_zip" {
  type        = "zip"
  source_dir  = "${path.module}/lambda/bedrock-asset-processor"
  output_path = "${path.module}/lambda/bedrock-asset-processor.zip"
}

# Lambda Function
resource "aws_lambda_function" "processor" {
  filename         = data.archive_file.lambda_zip.output_path
  function_name    = "bedrock-asset-processor"
  role             = aws_iam_role.lambda_role.arn
  handler          = "index.lambda_handler"
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256
  runtime          = "python3.11" # Using a modern Python runtime
  timeout          = 10

  environment {
    variables = {
      LOG_LEVEL = "INFO"
    }
  }

  tags = {
    Project = "Bedrock"
  }
}

# S3 Trigger Permission
resource "aws_lambda_permission" "allow_bucket" {
  statement_id  = "AllowExecutionFromS3Bucket"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.processor.function_name
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.assets.arn
}

# S3 Event Notification
resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = aws_s3_bucket.assets.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.processor.arn
    events              = ["s3:ObjectCreated:*"]
  }

  depends_on = [aws_lambda_permission.allow_bucket]
}
