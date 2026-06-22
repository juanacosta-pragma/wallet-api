output "mongodb_uri_secret_arn" {
  description = "ARN del secreto que contiene la URI de MongoDB"
  value       = aws_secretsmanager_secret.mongodb_uri.arn
}

output "mongodb_uri_secret_name" {
  description = "Nombre del secreto"
  value       = aws_secretsmanager_secret.mongodb_uri.name
}

