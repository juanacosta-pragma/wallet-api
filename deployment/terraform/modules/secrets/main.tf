################################################################################
# AWS Secrets Manager — almacena la URI de MongoDB Atlas de forma cifrada
#
# La URI se referencia desde la task definition de ECS con el campo `secrets`
# (en lugar de `environment`). El agente de ECS resuelve el valor en el momento
# de arrancar el contenedor usando el Execution Role, y lo inyecta como variable
# de entorno SPRING_DATA_MONGODB_URI sin que aparezca nunca en los planes de
# Terraform, en la consola de ECS, ni en CloudWatch.
################################################################################

resource "aws_secretsmanager_secret" "mongodb_uri" {
  name                    = "${var.name_prefix}-mongodb-uri"
  description             = "URI de conexión a MongoDB Atlas para la API de wallet"
  recovery_window_in_days = var.recovery_window_in_days

  tags = var.tags
}

resource "aws_secretsmanager_secret_version" "mongodb_uri" {
  secret_id     = aws_secretsmanager_secret.mongodb_uri.id
  secret_string = var.mongodb_uri

  lifecycle {
    # Si rotas la URI manualmente desde la consola de Secrets Manager,
    # Terraform no la sobrescribirá en el próximo apply.
    ignore_changes = [secret_string]
  }
}

