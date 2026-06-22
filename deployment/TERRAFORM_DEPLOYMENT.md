Propósito

Este documento describe en detalle la propuesta de despliegue con Terraform para la solución "wallet-api" (monorepo). Cubre cómo está modelada la infraestructura, los módulos recomendados, variables, estado y los pasos para aplicar los despliegues. Está pensado como guía operativa para equipos de DevOps y desarrolladores que necesiten desplegar o extender la infraestructura.

Checklist (pasos que cubre este documento)
- [x] Resumen de la arquitectura objetivo y qué recursos cubrir con Terraform
- [x] Estructura de Terraform recomendada (módulos y entornos)
- [x] Variables, secretos y backend de estado remoto
- [x] Mapeo entre componentes del código (módulos del repo) y recursos infra
- [x] Ejemplos de comandos para inicializar y aplicar (PowerShell)
- [x] Buenas prácticas, seguridad y consideraciones de escalado

1. Resumen de la arquitectura objetivo

La aplicación wallet-api está dividida en módulos funcionales (por ejemplo `applications/app-service`, `infrastructure/driven-adapters/mongo-repository`, `infrastructure/entry-points/reactive-web`). El objetivo del despliegue es proveer un entorno productivo/QA para ejecutar la API y sus dependencias.

Componentes típicos a modelar con Terraform
- Red (VPC / subnets / route tables) — redes privadas y públicas según proveedor.
- Seguridad (security groups / firewalls / NACLs) — permitir tráfico HTTP(S) y acceso a BD.
- Compute:
  - Contenedores (ECS/Fargate, EKS) o máquinas virtuales (VM/VMScaleSet) para la API (reactive-web / app-service).
  - Opcional: autoscaling, target groups y load balancer (ALB/NLB).
- Registros / imágenes: Repositorio de imágenes (ECR/GCR/Azure ACR).
- Base de datos: MongoDB (self-hosted en VMs o servicio administrado como Atlas/DocumentDB/MongoDB Atlas). Debe considerarse backup y seguridad.
- Secrets / Config: Secret Manager (AWS Secrets Manager, Azure Key Vault, Google Secret Manager) para credenciales y URIs.
- Observabilidad: CloudWatch/Prometheus/Grafana, logs y métricas.
- CI/CD: Pipelines que build-ean imagen y ejecutan `terraform apply` (no provisto por Terraform, pero integrado en flujo).

2. Estructura recomendada del repositorio Terraform

Se recomienda mantener un repositorio separado o una carpeta `deployment/terraform` dentro del monorepo con la siguiente estructura:

deployment/
  terraform/
    modules/
      network/
      compute/
      database/
      registry/
      security/
    envs/
      dev/
        main.tf
        variables.tf
        backend.tf
        terraform.tfvars
      qa/
      prod/

- `modules/` contiene módulos reutilizables (network, compute, database, registry, security).
- `envs/` contiene la configuración por entorno que invoca los módulos con valores concretos.
- `backend.tf` define el backend remoto (ej. S3 + DynamoDB para locking en AWS).

Ejemplo de `envs/prod/main.tf` (esquema):
- Invocar `modules/network` para crear VPC y subnets.
- Invocar `modules/security` para crear security groups.
- Invocar `modules/registry` para crear ECR/ACR.
- Invocar `modules/database` para crear/clase DB.
- Invocar `modules/compute` para crear cluster/servicio y asociar load balancer.

3. Ejemplo de módulo: `modules/compute`

Inputs típicos:
- cluster_name
- desired_count
- container_image
- container_port
- vpc_id, subnets
- security_group_ids
- cpu/memory

Outputs útiles:
- service_url
- load_balancer_dns

4. Variables y secretos

- Variables no sensibles: tamaño de instancias, conteo, nombres.
- Secretos (credenciales de BD, claves privadas): nunca en `terraform.tfvars` en texto claro. Usar:
  - Integración con Vault / Secrets Manager o
  - Referenciar secretos externos e inyectarlos en tiempo de ejecución.

Ejemplo de variables en `variables.tf`:
- variable "env" { type = string }
- variable "region" { type = string }
- variable "db_user" { type = string }
- variable "db_password" { type = string, sensitive = true }

5. Backend de estado remoto y locking

Es crítico usar un backend remoto para el estado de Terraform y habilitar locking para evitar corrupciones.
- AWS: backend S3 (bucket) + DynamoDB table para lock
- Azure: backend azurerm (storage account) con blob + Container
- GCP: backend gcs

Ejemplo (AWS backend.tf):

terraform {
  backend "s3" {
    bucket = "my-terraform-state-bucket"
    key    = "wallet-api/prod/terraform.tfstate"
    region = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt = true
  }
}

6. Mapeo entre código y recursos

- `applications/app-service` —> contenedor que corre los artefactos Java (imagen en `registry`)
- `infrastructure/entry-points/reactive-web` —> servicio expuesto al exterior (ingress / ALB)
- `infrastructure/driven-adapters/mongo-repository` —> si se utiliza MongoDB autoadministrada, crear recursos para instancias/replica set, o en su lugar configurar MongoDB Atlas (módulo específico/terceros)
- Configuración de `spring.data.mongodb.uri` debe apuntar a la instancia desplegada y almacenarse en Secrets Manager o en parámetros de configuración seguros.

7. Enrutamiento de configuración de la aplicación

- Las variables de entorno o `application.yml` deben recibir valores dinámicos del entorno (por ejemplo, URL del servicio de la DB, nombres de bucket, endpoints externos).
- Terraform puede producir `outputs` con valores como `mongo_uri` y el pipeline CI los pasa al despliegue (ej. al crear imagen o al inyectar variables en el contenedor en runtime).

8. Integración CI/CD

Flujo recomendado:
1. Pipeline de build: compilar la aplicación Java, ejecutar tests, construir imagen docker.
2. Push de imagen al registry (ECR/ACR/GCR).
3. Pipeline de infra (opcional) —> Terraform plan/apply para cambios de infra.
4. Pipeline de despliegue —> actualizar servicio (ECS/EKS) con la nueva imagen.

Nota: Separar la responsabilidad de infra y despliegue de imágenes: Terraform se encarga de la infraestructura (clusters, servicios) pero no es la mejor herramienta para actualizar imágenes a la hora (se usan despliegues de CI/CD que alteran la tarea/Deployment con la nueva image tag).

9. Comandos básicos (PowerShell)

Inicializar y validar el entorno (ejecutar desde `deployment/terraform/envs/prod`):

```powershell
cd C:\Proyects\Pragma\Wallet-pragma-api\wallet-api\deployment\terraform\envs\prod
terraform init
terraform validate
terraform plan -var-file="terraform.tfvars"
terraform apply -var-file="terraform.tfvars"
```

Si usas un backend S3 con locking, `terraform init` pedirá credenciales AWS (AWS CLI config o variables de entorno AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY).

10. Ejemplo de `terraform.tfvars` (NO COMMITEAR)

```hcl
env = "prod"
region = "us-east-1"
cluster_name = "wallet-api-prod"
desired_count = 2
# No poner secrets en este archivo para SCM, preferir variables en CICD o secret manager
# db_password = "supersecret"
```

11. Buenas prácticas y consideraciones

- Mantener módulos pequeños y reutilizables.
- No almacenar secretos en repositorio; usar Secret Manager o Vault.
- Versionar los módulos y el provider (p. ej. lock en `required_providers` y `required_version`).
- Usar workspaces por entorno o un layout por carpeta `envs/` si se prefiere (recomendado usar carpetas por entorno para claridad).
- Hacer `terraform plan` y revisiones manuales antes de aplicar en `prod`.
- Asegurar backups y restauración del estado remoto (S3) y la tabla DynamoDB para locking.

12. Consideraciones de seguridad

- Seguridad de red: separar subnets privadas para DB; restringir acceso a la base de datos a través de security groups y VPN/peering si aplica.
- IAM: principios de mínimo privilegio para las entidades que manejan Terraform (roles de CI/CD).
- Auditoría: habilitar logs y monitoreo para detectar cambios.

13. Casos especiales y extensiones

- MongoDB Atlas: si se usa Atlas, pueden aprovecharse módulos públicos de MongoDB Atlas (providers y módulos de la comunidad) y evitar administrar VMs.
- Autoscaling: exponer variables para activar/desactivar autoscaling en `modules/compute`.
- Blue/Green o Canary: introducir infra para despliegues canary (nuevo target group y switch de tráfico) o usar herramientas de delivery como Argo Rollouts.

14. Troubleshooting común

- Error de lock: eliminar lock manualmente en DynamoDB sólo si se confirma que no hay un `apply` en curso.
- Estado desincronizado: comparar `terraform state list` con recursos reales, evitar `state` manual edits salvo que sea necesaria recuperación.
- Problemas de permisos: verificar credenciales del proveedor y permisos IAM.

15. Próximos pasos prácticos (para el equipo)

1. Crear la estructura de carpetas `deployment/terraform/modules` y `deployment/terraform/envs` en este repo.
2. Implementar módulos mínimos: `network`, `security`, `registry`, `compute`, `database`.
3. Definir backend remoto (S3/DynamoDB o equivalente) y crear recursos del backend con procesos manuales o scripts (antes de `terraform init`).
4. Integrar Secrets Manager o Vault para credenciales de MongoDB.
5. Preparar pipelines CI/CD que consuman los outputs de Terraform y desplieguen imágenes.

16. Contacto y mantenimiento

- Autor: equipo de Infra/DevOps del proyecto
- Mantener este documento junto con el código del módulo `deployment/terraform` y actualizar cada vez que cambie la arquitectura.

Anexos: referencias útiles
- Terraform: https://www.terraform.io/docs
- Patterns: Terraform modules (registry) https://registry.terraform.io/
- MongoDB Atlas Terraform provider: https://registry.terraform.io/providers/mongodb/mongodbatlas/latest
- AWS S3 backend + DynamoDB locking: https://www.terraform.io/language/settings/backends/s3


---

Si quieres, puedo:
- 1) generar la estructura inicial de carpetas y archivos Terraform (esqueleto de módulos) dentro del repo,
- 2) crear un `backend.tf` de ejemplo para AWS o Azure, o
- 3) crear un `envs/dev` con `main.tf` que despliegue un cluster mínimo (ej. ECS Fargate con ALB) y una instancia MongoDB de prueba.

Dime cuál prefieres y lo genero automáticamente en el repo (puedo empezar con el esqueleto más sencillo para `dev`).
