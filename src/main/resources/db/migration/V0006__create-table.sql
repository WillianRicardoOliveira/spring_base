CREATE TABLE organizacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id)
);

CREATE INDEX idx_organizacao_nome_status
ON organizacao (nome, status);