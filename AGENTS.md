# AGENTS

Guia rapida para personas o agentes que trabajen dentro de este repositorio.

## Objetivo del proyecto

`LLMGateway` expone una API REST para autenticar usuarios, administrar sesiones de chat y enviar historiales de mensajes a un modelo servido por Ollama.

## Como pensar el codigo

- La organizacion sigue una variante de arquitectura hexagonal.
- Cada modulo de negocio separa `application`, `domain` e `infrastructure`.
- Los casos de uso viven en interfaces `port.input`.
- Las dependencias externas se abstraen con `port.output`.
- Los adapters de infraestructura implementan esos puertos.

## Modulos principales

- `structure.auth`
- `structure.user`
- `structure.chatsession`
- `structure.message`
- `structure.ollama`
- `structure.refreshtoken`
- `structure.config`

## Puntos de entrada

- App principal: [LlmGatewayApplication.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/LlmGatewayApplication.java)
- Auth REST: [AuthController.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/structure/auth/infrastructure/input/AuthController.java)
- Chat REST: [ChatSessionController.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/structure/chatsession/infrastructure/adapter/input/ChatSessionController.java)
- Usuario autenticado: [UserController.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/structure/user/infraestructure/input/UserController.java)

## Convenciones utiles

- Los modelos de dominio terminan en `Model`.
- Las entidades JPA viven bajo `infrastructure/.../entity`.
- Los mappers usan MapStruct.
- Los repositorios JPA reales viven en `.../repo` o `.../repository`.
- Los servicios de aplicacion terminan en `Services`.

## Flujo operativo recomendado para cambios

1. Empezar desde el endpoint o caso de uso afectado.
2. Seguir el `port.input` hacia `application.services`.
3. Revisar que `port.output` usa ese servicio.
4. Cambiar adapter, mapper, entidad y repositorio si el dato cruza persistencia.
5. Si el cambio afecta autenticacion, revisar tambien `SecurityConfig` y `JwtAuthenticationFilter`.
6. Si el cambio afecta chat, revisar el contrato de Ollama y el orden de mensajes.

## Archivos que casi siempre hay que revisar

- [application.yaml](/C:/Users/Carlos/Desktop/LLMGateway/src/main/resources/application.yaml)
- [SecurityConfig.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/structure/config/SecurityConfig.java)
- [JwtAuthenticationFilter.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/structure/config/JwtAuthenticationFilter.java)
- [JwtAdapter.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/structure/auth/infrastructure/out/security/JwtAdapter.java)
- [SendMessageServices.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/structure/chatsession/application/services/SendMessageServices.java)
- [OllamaAdapter.java](/C:/Users/Carlos/Desktop/LLMGateway/src/main/java/com/fragua/LLMGateway/structure/ollama/infrastructure/adapter/output/OllamaAdapter.java)

## Riesgos conocidos

- `ddl-auto: create-drop` borra el esquema al cerrar.
- El secret JWT esta en el repo y solo sirve para desarrollo.
- El job de limpieza usa `@Scheduled`, pero la app no muestra `@EnableScheduling`.
- `LogoutUserServices` parece guardar el token original y no el token revocado.
- Hay nombres de paquetes con errores de escritura (`aplication`, `infraestructure`, `ouput`); no romper imports al refactorizar.

## Cuando agregues funcionalidad nueva

- Mantener la separacion `domain` / `application` / `infrastructure`.
- Preferir agregar un nuevo caso de uso antes que crecer un controller con logica.
- Si integras otro proveedor LLM, colgarlo del puerto `ChatModelPort`.
- Si agregas endpoints protegidos, asumir que el `principal` sera `UserModel`.
- Documentar nuevas variables de entorno o propiedades en `README.md`.

## Deuda tecnica visible

- Falta manejo centralizado de excepciones.
- Faltan tests de integracion para auth, refresh token y chat.
- Faltan endpoints para listar sesiones, obtener historial y borrar sesiones, aunque ya existen puertos para parte de ese dominio.
- La configuracion de build en `pom.xml` tiene duplicidad en plugins de Spring Boot y compiler.
