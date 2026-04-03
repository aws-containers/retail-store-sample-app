output "distribution_id" {
  value       = aws_cloudfront_distribution.distribution.id
  description = "CloudFront Distribution ID"
}

output "distribution_domain_name" {
  value       = aws_cloudfront_distribution.distribution.domain_name
  description = "CloudFront Distribution Domain Name"
}

output "hosted_zone_id" {
  value       = aws_cloudfront_distribution.distribution.hosted_zone_id # This is the ID of the hosted zone that CloudFront uses to route requests for your distribution. You can use this value when you create a CNAME record to route traffic to your distribution.
  description = "CloudFront Distribution Hosted Zone ID"
}