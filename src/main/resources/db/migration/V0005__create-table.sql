CREATE TABLE usuario_subsidiaria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario_empresa BIGINT NOT NULL,
    id_subsidiaria BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_subsidiaria_usuario_empresa
        FOREIGN KEY (id_usuario_empresa)
        REFERENCES usuario_empresa (id),
    CONSTRAINT fk_usuario_subsidiaria_subsidiaria
        FOREIGN KEY (id_subsidiaria)
        REFERENCES subsidiaria (id)
);

CREATE INDEX idx_usuario_subsidiaria_usuario_empresa_status
ON usuario_subsidiaria (
    id_usuario_empresa,
    status
);

CREATE INDEX idx_usuario_subsidiaria_subsidiaria_status
ON usuario_subsidiaria (
    id_subsidiaria,
    status
);

CREATE INDEX idx_usuario_subsidiaria_vinculo_status
ON usuario_subsidiaria (
    id_usuario_empresa,
    id_subsidiaria,
    status
);

INSERT INTO permissao (
    nome,
    chave,
    descricao,
    status,
    sistema
) VALUES
(
    'Vincular usuario a subsidiaria',
    'ACESSO_USUARIO_SUBSIDIARIA_CRIAR',
    'Permite vincular usuarios a subsidiarias',
    0,
    TRUE
),
(
    'Listar subsidiarias do usuario',
    'ACESSO_USUARIO_SUBSIDIARIA_LISTAR',
    'Permite listar os vinculos entre usuarios e subsidiarias',
    0,
    TRUE
),
(
    'Detalhar subsidiaria do usuario',
    'ACESSO_USUARIO_SUBSIDIARIA_DETALHAR',
    'Permite detalhar o vinculo entre usuario e subsidiaria',
    0,
    TRUE
),
(
    'Remover subsidiaria do usuario',
    'ACESSO_USUARIO_SUBSIDIARIA_EXCLUIR',
    'Permite remover o vinculo entre usuario e subsidiaria',
    0,
    TRUE
);

INSERT INTO perfil_permissao (
    id_perfil,
    id_permissao,
    status
)
SELECT
    1,
    permissao.id,
    0
FROM permissao
WHERE permissao.chave IN (
    'ACESSO_USUARIO_SUBSIDIARIA_CRIAR',
    'ACESSO_USUARIO_SUBSIDIARIA_LISTAR',
    'ACESSO_USUARIO_SUBSIDIARIA_DETALHAR',
    'ACESSO_USUARIO_SUBSIDIARIA_EXCLUIR'
);