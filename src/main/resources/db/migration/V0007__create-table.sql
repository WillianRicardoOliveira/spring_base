CREATE TABLE usuario_organizacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    id_organizacao BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_organizacao_usuario_organizacao
        UNIQUE (id_usuario, id_organizacao),
    CONSTRAINT fk_usuario_organizacao_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id),
    CONSTRAINT fk_usuario_organizacao_organizacao
        FOREIGN KEY (id_organizacao)
        REFERENCES organizacao (id)
);

CREATE INDEX idx_usuario_organizacao_usuario_status
ON usuario_organizacao (
    id_usuario,
    status,
    id_organizacao
);