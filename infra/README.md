# Infraestructura AWS con Terraform

Infraestructura para AWS ElastiCache Redis con VPC y subnets.

## Recursos Creados

- **VPC** con DNS habilitado
- **Subnet Pública** con auto-asignación de IPs públicas
- **2 Subnets Privadas** en diferentes AZs
- **Internet Gateway** para la subnet pública
- **ElastiCache Redis Cluster** (Cluster Mode Enabled)
  - 3 shards (node groups)
  - 1 réplica por shard
  - Encriptación en tránsito y reposo
  - Multi-AZ con failover automático
  - Autenticación con auth token
- **Security Group** para ElastiCache
- **CloudWatch Logs** para slow-log y engine-log
- **SSM Parameter Store** para guardar el auth token

## Prerequisitos

1. AWS CLI configurado con credenciales
2. Terraform >= 1.0 instalado
3. Permisos en AWS para crear:
   - VPC, Subnets, Internet Gateway
   - ElastiCache
   - Security Groups
   - CloudWatch Logs
   - SSM Parameter Store

## Uso

### 1. Inicializar Terraform

```bash
cd infra
terraform init
```

### 2. Crear archivo de variables

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edita `terraform.tfvars` con tus valores:

```hcl
aws_region   = "us-east-1"
project_name = "retofinal"
environment  = "dev"

# ElastiCache
elasticache_node_type = "cache.t3.micro"  # Cambiar a cache.r7g.large para producción
num_node_groups       = 3
replicas_per_node_group = 1
```

### 3. Planificar cambios

```bash
terraform plan
```

### 4. Aplicar infraestructura

```bash
terraform apply
```

Confirma con `yes` cuando se solicite.

### 5. Obtener información

```bash
# Ver todos los outputs
terraform output

# Ver el endpoint de conexión
terraform output elasticache_configuration_endpoint

# Ver configuración para Spring Boot
terraform output spring_boot_config

# Obtener el auth token desde SSM
aws ssm get-parameter --name "/retofinal/dev/redis/auth-token" --with-decryption --query "Parameter.Value" --output text
```

## Configurar Spring Boot

Después de aplicar Terraform, configura tu aplicación:

```bash
# Obtener el endpoint
export REDIS_ENDPOINT=$(terraform output -raw elasticache_configuration_endpoint)

# Obtener el password
export REDIS_PASSWORD=$(aws ssm get-parameter --name "/retofinal/dev/redis/auth-token" --with-decryption --query "Parameter.Value" --output text)

# Variables para Spring Boot
export REDIS_MODE=cluster
export REDIS_CLUSTER_NODES=$REDIS_ENDPOINT:6379
export REDIS_SSL_ENABLED=true
export REDIS_PASSWORD=$REDIS_PASSWORD
```

O agrega a tu `application.properties`:

```properties
redis.mode=cluster
spring.data.redis.cluster.nodes=${REDIS_CLUSTER_NODES}
spring.data.redis.password=${REDIS_PASSWORD}
spring.data.redis.ssl.enabled=true
```

## Costos Estimados

Con la configuración por defecto (`cache.t3.micro`):
- **ElastiCache**: ~$0.017/hora por nodo × 6 nodos = ~$0.102/hora (~$74/mes)
- **VPC/Networking**: Gratis (dentro del free tier)
- **CloudWatch Logs**: Primeros 5GB gratis

**Para producción**, considera usar `cache.r7g.large` o mayor.

## Destruir Infraestructura

```bash
terraform destroy
```

**⚠️ ADVERTENCIA**: Esto eliminará todos los recursos y datos.

## Arquitectura

```
VPC (10.0.0.0/16)
├── Public Subnet (10.0.1.0/24) - AZ-a
│   └── Internet Gateway
├── Private Subnet 1 (10.0.10.0/24) - AZ-a
│   └── ElastiCache Nodes
└── Private Subnet 2 (10.0.11.0/24) - AZ-b
    └── ElastiCache Nodes

ElastiCache Cluster
├── Shard 1: 1 Master + 1 Replica
├── Shard 2: 1 Master + 1 Replica
└── Shard 3: 1 Master + 1 Replica
```

## Seguridad

- ✅ Encriptación en reposo habilitada
- ✅ Encriptación en tránsito (TLS) habilitada
- ✅ Autenticación con AUTH token
- ✅ Auth token guardado en SSM Parameter Store (encriptado)
- ✅ Security Group que solo permite tráfico desde la VPC
- ✅ ElastiCache en subnets privadas (no accesible desde Internet)

## Monitoreo

Los logs están disponibles en CloudWatch:
- `/aws/elasticache/retofinal-dev/slow-log` - Queries lentas
- `/aws/elasticache/retofinal-dev/engine-log` - Logs del motor Redis

## Notas

- El auth token se genera automáticamente y se guarda en SSM Parameter Store
- Multi-AZ está habilitado para alta disponibilidad
- Automatic failover está habilitado
- Los backups automáticos se retienen por 5 días
