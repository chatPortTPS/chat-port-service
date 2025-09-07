# Chat Port Service

Servicio base desarrollado con **Red Hat Build of Quarkus (RHBQ)**, **Apache Camel** y **ActiveMQ Artemis JMS** para aplicaciones empresariales de integración.

## 🏗️ Arquitectura

Este servicio implementa una arquitectura basada en **Enterprise Integration Patterns (EIP)** utilizando:

- **Quarkus**: Framework Java supersónico y subatómico para aplicaciones nativas de la nube
- **Apache Camel**: Framework de integración con soporte para más de 300 conectores
- **ActiveMQ Artemis**: Broker de mensajería JMS de alto rendimiento
- **Jackson**: Procesamiento JSON/XML
- **SmallRye**: Implementaciones de especificaciones MicroProfile
- **H2/Oracle**: Soporte dual de base de datos (desarrollo/producción)

### Flujo de Datos

```
Cliente → Quarkus HTTP → Camel Routes → Business Logic → Response
                                    ↓
                               JMS Queue (Artemis)
                                    ↓
                               Database (H2/Oracle)
```

## 📦 Dependencias Principales

### Core Framework
- **Quarkus Platform**: `3.8.6.SP3-redhat-00002`
- **Apache Camel Quarkus**: Integración y enrutamiento
- **Artemis Quarkus**: `3.2.0` - Mensajería JMS

### REST y Serialización
- `quarkus-resteasy-reactive`: REST endpoints reactivos
- `quarkus-resteasy-reactive-jackson`: Serialización JSON
- `jackson-datatype-jsr310`: Soporte para tipos de fecha Java 8+

### Base de Datos
- `quarkus-jdbc-h2`: Driver H2 para desarrollo
- `quarkus-jdbc-oracle`: Driver Oracle para producción
- `quarkus-agroal`: Pool de conexiones

### Camel Components
- `camel-quarkus-direct`: Enrutamiento interno
- `camel-quarkus-rest`: Endpoints REST
- `camel-quarkus-platform-http`: Integración con servidor HTTP
- `camel-quarkus-jms`: Mensajería JMS
- `camel-quarkus-jackson`: Serialización JSON en Camel

### Observabilidad
- `quarkus-smallrye-health`: Health checks
- `quarkus-smallrye-openapi`: Documentación API
- `quarkus-logging-json`: Logs estructurados

## 🚀 Inicio de Desarrollo

### Prerrequisitos
- **Java 17+**
- **Maven 3.8+**
- **Variables de entorno** configuradas (ver sección Variables de Entorno)

### Variables de Entorno Requeridas

```bash
export SERVICE_NAME_BASE="/chatPort"
export SWAGGER_ENABLE="true"
export OPENAPI_ENABLED="true"
export AMQ_ARTEMIS_ENABLED="false"  # Deshabilitado para desarrollo local
export AMQ_ARTEMIS_URL="tcp://localhost:61616"
export AMQ_ARTEMIS_USERNAME="artemis"
export AMQ_ARTEMIS_PASSWORD="artemis"
export DB_USERNAME="sa"
export DB_PASSWORD=""
export DB_HOST="localhost"
export DB_PORT="1521"
export DB_SERVICE="testdb"
```

### Inicio Rápido

1. **Clonar y configurar el proyecto:**
```bash
cd chat-port-service
cp .env.example .env  # Opcional: configurar variables
```

2. **Ejecutar en modo desarrollo:**
```bash
./mvnw quarkus:dev
```

3. **Verificar la aplicación:**
   - Aplicación: http://localhost:8181/chatPort
   - Health Check: http://localhost:8181/chatPort/q/health
   - Swagger UI: http://localhost:8181/chatPort/swagger
   - OpenAPI Camel: http://localhost:8181/chatPort/openapi-camel

### Modo de Desarrollo

Quarkus incluye **hot reload**, por lo que los cambios en código se reflejan automáticamente sin reiniciar la aplicación.

**Características en desarrollo:**
- Recarga automática de clases Java
- Recarga de configuración
- Live coding con Camel routes
- Consola de desarrollo en http://localhost:8181/q/dev

## 🧪 Testing

### Estructura de Tests

```
src/test/java/
└── com/chat/port/services/
    └── operacion/example/
        └── ExampleResourceTest.java
```

### Configuración de Test

Los tests utilizan un perfil específico con:
- **H2 en memoria**: Base de datos temporal para tests
- **Artemis deshabilitado**: Sin dependencias externas
- **Configuración simplificada**: Variables predefinidas en `src/test/resources/application.properties`

### Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=ExampleResourceTest

# Tests con coverage
./mvnw test jacoco:report
```

### Tipos de Test Incluidos

- **Integration Tests**: Pruebas de endpoints REST completos
- **Unit Tests**: Lógica de negocio aislada
- **Contract Tests**: Validación de estructura de respuestas JSON

## 📚 Documentación API

### OpenAPI/Swagger

El servicio expone documentación automática en múltiples formatos:

#### 🔹 Swagger UI (Interfaz Visual)
- **URL**: `http://localhost:8181/chatPort/swagger`
- **Descripción**: Interfaz web interactiva para probar endpoints
- **Configuración**: Controlado por `SWAGGER_ENABLE`
- **Características**: 
  - Prueba endpoints en tiempo real
  - Visualización de schemas
  - Autenticación integrada

#### 🔹 OpenAPI Camel (Especificación Camel)
- **URL**: `http://localhost:8181/chatPort/openapi-camel`
- **Descripción**: Especificación OpenAPI generada por Camel
- **Formato**: JSON/YAML estándar
- **Configuración**: Controlado por `openapi.enabled`
- **Uso**: Integración con herramientas externas, generación de clientes

#### 🔹 SmallRye OpenAPI (Especificación Quarkus)
- **URL**: `http://localhost:8181/q/openapi`
- **Descripción**: Especificación OpenAPI nativa de Quarkus
- **Características**: 
  - Anotaciones JAX-RS
  - Integración con MicroProfile
  - Soporte para múltiples formatos

### Configuración de Documentación

```properties
# Habilitar/deshabilitar Swagger UI
quarkus.swagger-ui.enable=${SWAGGER_ENABLE}
quarkus.swagger-ui.path=${SERVICE_NAME_BASE}/swagger

# Configuración OpenAPI Camel
quarkus.camel.openapi.expose.enabled=true
openapi.enabled=${OPENAPI_ENABLED}

# Metadatos API
api.title=chat-port-service
api.version=1.0.0-SNAPSHOT
```

## 📊 Observabilidad

### Health Checks
- **URL**: `http://localhost:8181/chatPort/q/health`
- **Tipos**: Liveness, Readiness, Custom checks
- **Formato**: JSON estándar MicroProfile Health

### Métricas
- Métricas JVM automáticas
- Métricas de Camel routes
- Métricas de pool de conexiones

### Logging
- **Formato**: JSON estructurado
- **Niveles**: Configurables por paquete
- **Archivo**: `logs/access.log` (rotativo)

## 🐳 Despliegue

### Desarrollo Local
```bash
./mvnw quarkus:dev
```

### Construcción de Aplicación
```bash
# JAR ejecutable
./mvnw clean package

# Imagen nativa (requiere GraalVM)
./mvnw clean package -Pnative
```

### Docker
```bash
# Imagen JVM
docker build -f src/main/docker/Dockerfile.jvm -t chat-port-service:jvm .

# Imagen nativa
docker build -f src/main/docker/Dockerfile.native -t chat-port-service:native .
```

## 🔧 Configuración Avanzada

### Perfiles de Base de Datos

**Desarrollo (H2):**
```properties
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb
```

**Producción (Oracle):**
```properties
quarkus.datasource.db-kind=oracle
quarkus.datasource.jdbc.url=jdbc:oracle:thin:@${DB_HOST}:${DB_PORT}/${DB_SERVICE}
```

### Configuración de Artemis

```properties
quarkus.artemis.enabled=${AMQ_ARTEMIS_ENABLED}
quarkus.artemis.url=${AMQ_ARTEMIS_URL}
quarkus.artemis.username=${AMQ_ARTEMIS_USERNAME}
quarkus.artemis.password=${AMQ_ARTEMIS_PASSWORD}
```

### CORS y Seguridad

```properties
quarkus.http.cors=true
quarkus.http.cors.origins=https://frontend.domain.com
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
```

## 🏢 Arquitectura Empresarial

### Patrones Implementados
- **Enterprise Integration Patterns**: Uso extensivo de Camel EIP
- **Circuit Breaker**: Resilencia en integraciones
- **Retry Patterns**: Manejo de fallos temporales
- **Dead Letter Queue**: Manejo de mensajes fallidos
- **Content-Based Router**: Enrutamiento basado en contenido

### Escalabilidad
- **Reactive Programming**: RESTEasy Reactive para alta concurrencia
- **Connection Pooling**: Agroal para gestión eficiente de BBDD
- **JMS Clustering**: Soporte para Artemis en cluster

### Monitoreo Empresarial
- **Distributed Tracing**: Preparado para Jaeger/Zipkin
- **Structured Logging**: Logs en formato JSON para agregación
- **Business Metrics**: Métricas customizables por dominio

---

## 📝 Notas de Desarrollo

- Las rutas Camel se definen en `ApiRoutes.java`
- Los endpoints REST utilizan Camel REST DSL
- La configuración sigue el patrón de 12-factor apps
- Soporte completo para contenedores y Kubernetes

Para más información, consulta la documentación oficial de [Quarkus](https://quarkus.io/), [Apache Camel](https://camel.apache.org/) y [ActiveMQ Artemis](https://activemq.apache.org/components/artemis/).
