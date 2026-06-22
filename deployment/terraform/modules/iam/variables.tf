variable "name_prefix" {
  description = "Prefijo para nombrar los roles"
  type        = string
}

variable "secret_arns" {
  description = "ARNs de secretos en Secrets Manager que el Execution Role debe poder leer (para inyectarlos en la task)"
  type        = list(string)
  default     = []
}

variable "tags" {
  description = "Tags comunes"
  type        = map(string)
  default     = {}
}

