CREATE TABLE subsidiaria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_empresa BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_subsidiaria_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa (id)
);

CREATE INDEX idx_subsidiaria_empresa_nome_status
ON subsidiaria (id_empresa, nome, status);

INSERT INTO permissao (
    nome,
    chave,
    descricao,
    status,
    sistema
) VALUES
(
    'Criar subsidiárias',
    'CONFIGURACAO_SUBSIDIARIA_CRIAR',
    'Permite criar subsidiárias',
    0,
    TRUE
),
(
    'Listar subsidiárias',
    'CONFIGURACAO_SUBSIDIARIA_LISTAR',
    'Permite listar subsidiárias',
    0,
    TRUE
),
(
    'Detalhar subsidiária',
    'CONFIGURACAO_SUBSIDIARIA_DETALHAR',
    'Permite detalhar subsidiárias',
    0,
    TRUE
),
(
    'Editar subsidiárias',
    'CONFIGURACAO_SUBSIDIARIA_EDITAR',
    'Permite editar subsidiárias',
    0,
    TRUE
),
(
    'Excluir subsidiárias',
    'CONFIGURACAO_SUBSIDIARIA_EXCLUIR',
    'Permite remover subsidiárias',
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
    'CONFIGURACAO_SUBSIDIARIA_CRIAR',
    'CONFIGURACAO_SUBSIDIARIA_LISTAR',
    'CONFIGURACAO_SUBSIDIARIA_DETALHAR',
    'CONFIGURACAO_SUBSIDIARIA_EDITAR',
    'CONFIGURACAO_SUBSIDIARIA_EXCLUIR'
);