variable "name_prefix" {
  description = "Prefijo para nombrar los recursos"
  type        = string
}

variable "mongodb_uri" {
  description = "URI de conexión a MongoDB Atlas (se almacena en Secrets Manager, nunca como env var plana)"
  type        = string
  sensitive   = true
}

variable "recovery_window_in_days" {
  description = <<-EOT
    Días que AWS conserva el secreto tras un destroy antes de eliminarlo de
    forma irreversible. 0 = eliminación inmediata (útil en dev). Valor permitido
    en prod: 7 a 30.
  EOT
  type        = number
  default     = 0
}

variable "tags" {
  description = "Tags comunes"
  type        = map(string)
  default     = {}
}

