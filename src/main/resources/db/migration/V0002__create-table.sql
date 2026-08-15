CREATE TABLE empresa (
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

CREATE INDEX idx_empresa_nome_status
ON empresa (nome, status);

INSERT INTO permissao (
    nome,
    chave,
    descricao,
    status,
    sistema
) VALUES
(
    'Criar empresas',
    'CONFIGURACAO_EMPRESA_CRIAR',
    'Permite criar empresas',
    0,
    TRUE
),
(
    'Listar empresas',
    'CONFIGURACAO_EMPRESA_LISTAR',
    'Permite listar empresas',
    0,
    TRUE
),
(
    'Detalhar empresa',
    'CONFIGURACAO_EMPRESA_DETALHAR',
    'Permite detalhar empresas',
    0,
    TRUE
),
(
    'Editar empresas',
    'CONFIGURACAO_EMPRESA_EDITAR',
    'Permite editar empresas',
    0,
    TRUE
),
(
    'Excluir empresas',
    'CONFIGURACAO_EMPRESA_EXCLUIR',
    'Permite remover empresas',
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
    'CONFIGURACAO_EMPRESA_CRIAR',
    'CONFIGURACAO_EMPRESA_LISTAR',
    'CONFIGURACAO_EMPRESA_DETALHAR',
    'CONFIGURACAO_EMPRESA_EDITAR',
    'CONFIGURACAO_EMPRESA_EXCLUIR'
);