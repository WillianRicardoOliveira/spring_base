CREATE TABLE usuario_empresa (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    id_empresa BIGINT NOT NULL,
    todas_subsidiarias BOOLEAN NOT NULL DEFAULT FALSE,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_empresa_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id),
    CONSTRAINT fk_usuario_empresa_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa (id)
);

CREATE INDEX idx_usuario_empresa_usuario_status
ON usuario_empresa (id_usuario, status);

CREATE INDEX idx_usuario_empresa_empresa_status
ON usuario_empresa (id_empresa, status);

CREATE INDEX idx_usuario_empresa_usuario_empresa_status
ON usuario_empresa (id_usuario, id_empresa, status);

INSERT INTO permissao (
    nome,
    chave,
    descricao,
    status,
    sistema
) VALUES
(
    'Vincular usuario a empresa',
    'ACESSO_USUARIO_EMPRESA_CRIAR',
    'Permite vincular usuarios a empresas',
    0,
    TRUE
),
(
    'Listar empresas do usuario',
    'ACESSO_USUARIO_EMPRESA_LISTAR',
    'Permite listar os vinculos entre usuarios e empresas',
    0,
    TRUE
),
(
    'Detalhar empresa do usuario',
    'ACESSO_USUARIO_EMPRESA_DETALHAR',
    'Permite detalhar o vinculo entre usuario e empresa',
    0,
    TRUE
),
(
    'Editar empresa do usuario',
    'ACESSO_USUARIO_EMPRESA_EDITAR',
    'Permite editar o acesso do usuario a empresa',
    0,
    TRUE
),
(
    'Remover empresa do usuario',
    'ACESSO_USUARIO_EMPRESA_EXCLUIR',
    'Permite remover o vinculo entre usuario e empresa',
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
    'ACESSO_USUARIO_EMPRESA_CRIAR',
    'ACESSO_USUARIO_EMPRESA_LISTAR',
    'ACESSO_USUARIO_EMPRESA_DETALHAR',
    'ACESSO_USUARIO_EMPRESA_EDITAR',
    'ACESSO_USUARIO_EMPRESA_EXCLUIR'
);