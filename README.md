# LLMGateway

Backend en Spring Boot para autenticacion con JWT, gestion de sesiones de chat y envio de mensajes a un modelo LLM expuesto por Ollama.

## Que hace

- Registra usuarios y autentica por `access token` + `refresh token`.
- Mantiene sesiones de chat por usuario.
- Guarda el historial de mensajes en PostgreSQL.
- Reenvia el historial al endpoint `/api/chat` de Ollama usando OpenFeign.

## Stack

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (`jjwt`)
- MapStruct
- Lombok
- OpenFeign

## Estructura funcional

- `auth`: login, registro, refresh y logout.
- `user`: consulta del usuario autenticado.
- `chatsession`: creacion de sesiones y envio de mensajes.
- `message`: persistencia del historial de mensajes.
- `ollama`: integracion con el proveedor LLM.
- `refreshtoken`: modelo y flujo de renovacion de sesion.
- `config`: seguridad y filtro JWT.

## Requisitos

- Java 25
- PostgreSQL corriendo localmente
- Ollama disponible en `http://localhost:11434`

## Configuracion actual

El archivo [application.yaml](/C:/Users/Carlos/Desktop/LLMGateway/src/main/resources/application.yaml) define por defecto:

- Base de datos: `jdbc:postgresql://localhost:5432/mydatabase`
- Usuario DB: `postgres`
- Password DB: `postgres`
- JWT secret: valor hardcodeado para desarrollo
- JWT access expiration: `3600000`
- JWT refresh expiration: `604800000`
- Ollama URL: `http://localhost:11434`
- JPA `ddl-auto`: `create-drop`

Importante:

- `create-drop` recrea el esquema al iniciar y lo elimina al cerrar la aplicacion.
- El secreto JWT actual no es apto para produccion.

## Endpoints principales

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

### Usuario

- `GET /api/user/me`

### Chat

- `POST /api/chat-sessions`
- `POST /api/chat-sessions/{chatSessionId}/messages`

## Flujo de uso

1. Registrar usuario.
2. Iniciar sesion para obtener `accessToken` y `refreshToken`.
3. Enviar `Authorization: Bearer <token>` a endpoints protegidos.
4. Crear una sesion de chat indicando `prompt` inicial y `model`.
5. Enviar mensajes adicionales a la sesion creada.
6. Renovar el `accessToken` con `/api/auth/refresh` cuando expire.
7. Revocar el `refreshToken` con `/api/auth/logout`.

## Como corre el chat

Al crear una sesion:

- se crea la sesion en DB
- se guarda el `prompt` inicial como mensaje `USER`

Al enviar un nuevo mensaje:

- se recupera la sesion
- se carga el historial ordenado
- se guarda el nuevo mensaje del usuario
- se vuelve a leer el historial
- se llama a Ollama con el historial completo
- se persiste la respuesta del asistente

## Seguridad

- La aplicacion es stateless.
- `/api/auth/**` queda publico.
- El resto de endpoints requieren JWT valido.
- El filtro JWT extrae el email desde el token y carga el usuario desde persistencia.

## Observaciones del estado actual

- Existe un job programado para limpiar refresh tokens expirados o revocados, pero no se observa `@EnableScheduling` en la aplicacion; asi que probablemente no se ejecuta todavia.
- La implementacion de logout construye un token revocado, pero guarda la instancia original en vez de la revocada. Conviene revisarlo antes de depender de ese flujo.
- No hay manejo global de errores ni codigos de respuesta estandarizados para reglas de negocio.
- El wrapper `mvnw` no pudo ejecutarse correctamente en esta revision local, asi que no pude validar tests desde el entorno actual.

## Siguiente paso recomendado

Para estabilizar el proyecto antes de crecerlo:

1. externalizar secretos y credenciales
2. cambiar `ddl-auto`
3. habilitar y probar scheduling
4. corregir logout
5. agregar tests de integracion para auth y chat
