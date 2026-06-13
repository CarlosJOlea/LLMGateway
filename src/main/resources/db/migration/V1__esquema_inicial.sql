-- Esquema inicial del LLM Gateway.
-- Reemplaza al ddl-auto de Hibernate: a partir de aqui el esquema se versiona
-- con Flyway y Hibernate solo valida que las entidades coincidan.

CREATE TABLE usuarios (
    id            UUID PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled       BOOLEAN      NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL
);

CREATE TABLE sesiones_chat (
    id         UUID PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    active     BOOLEAN      NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    usuario_id UUID         NOT NULL REFERENCES usuarios (id)
);

CREATE INDEX idx_sesiones_chat_usuario ON sesiones_chat (usuario_id);

CREATE TABLE mensajes (
    id                UUID PRIMARY KEY,
    role              VARCHAR(20) NOT NULL,
    content           TEXT        NOT NULL,
    prompt_tokens     INTEGER,
    completion_tokens INTEGER,
    total_tokens      INTEGER,
    message_order     INTEGER     NOT NULL,
    created_at        TIMESTAMP   NOT NULL,
    sesion_chat_id    UUID        NOT NULL REFERENCES sesiones_chat (id)
);

CREATE INDEX idx_mensajes_sesion ON mensajes (sesion_chat_id, message_order);

CREATE TABLE tokens_refresh (
    id         UUID PRIMARY KEY,
    token      VARCHAR(500) NOT NULL UNIQUE,
    revoked    BOOLEAN      NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    usuario_id UUID         NOT NULL REFERENCES usuarios (id)
);

CREATE TABLE api_keys (
    id           UUID PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    key_prefix   VARCHAR(16)  NOT NULL,
    key_hash     VARCHAR(64)  NOT NULL UNIQUE,
    revoked      BOOLEAN      NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    last_used_at TIMESTAMP,
    usuario_id   UUID         NOT NULL REFERENCES usuarios (id)
);

CREATE INDEX idx_api_keys_usuario ON api_keys (usuario_id);

CREATE TABLE registros_uso (
    id                UUID PRIMARY KEY,
    model_name        VARCHAR(100) NOT NULL,
    prompt_tokens     INTEGER      NOT NULL,
    completion_tokens INTEGER      NOT NULL,
    total_tokens      INTEGER      NOT NULL,
    source            VARCHAR(30)  NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    usuario_id        UUID         NOT NULL REFERENCES usuarios (id)
);

CREATE INDEX idx_registros_uso_usuario ON registros_uso (usuario_id, created_at);
