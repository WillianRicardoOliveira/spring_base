CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email
        UNIQUE (email)
);

CREATE TABLE usuario_sessao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    refresh_token_hash VARCHAR(64) NOT NULL,
    access_token_jti VARCHAR(100),
    status TINYINT NOT NULL DEFAULT 0,
    expira_em DATETIME NOT NULL,
    revogado_em DATETIME,
    revogado_por BIGINT,
    motivo_revogacao VARCHAR(100),
    ip VARCHAR(45),
    user_agent VARCHAR(255),
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_sessao_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id),
    CONSTRAINT uk_usuario_sessao_refresh_token_hash
        UNIQUE (refresh_token_hash)
);

CREATE INDEX idx_usuario_sessao_usuario_status
ON usuario_sessao (
    id_usuario,
    status
);

CREATE INDEX idx_usuario_sessao_expira_em
ON usuario_sessao (
    expira_em
);

CREATE INDEX idx_usuario_sessao_access_token_jti
ON usuario_sessao (
    access_token_jti
);

CREATE TABLE usuario_login_tentativa (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    quantidade_falhas INT NOT NULL DEFAULT 0,
    ultima_falha_em DATETIME,
    bloqueado_ate DATETIME,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_login_tentativa_email
        UNIQUE (email)
);

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
ON organizacao (
    nome,
    status
);

CREATE TABLE convite_organizacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome_organizacao VARCHAR(100) NOT NULL,
    email_administrador VARCHAR(100) NOT NULL,
    email_pendente VARCHAR(100),
    token_hash VARCHAR(64) NOT NULL,
    expira_em DATETIME NOT NULL,
    aceito_em DATETIME,
    status VARCHAR(30) NOT NULL,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_convite_organizacao_token_hash
        UNIQUE (token_hash),
    CONSTRAINT uk_convite_organizacao_email_pendente
        UNIQUE (email_pendente)
);

CREATE INDEX idx_convite_organizacao_email_status_expiracao
ON convite_organizacao (
    email_administrador,
    status,
    expira_em
);

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
        UNIQUE (
            id_usuario,
            id_organizacao
        ),
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

CREATE TABLE perfil (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_organizacao BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    tipo_sistema VARCHAR(50),
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_perfil_organizacao_tipo_sistema
        UNIQUE (
            id_organizacao,
            tipo_sistema
        ),
    CONSTRAINT fk_perfil_organizacao
        FOREIGN KEY (id_organizacao)
        REFERENCES organizacao (id)
);

CREATE INDEX idx_perfil_organizacao_status_nome
ON perfil (
    id_organizacao,
    status,
    nome
);

CREATE TABLE permissao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    chave VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 0,
    sistema BOOLEAN NOT NULL DEFAULT FALSE,
    escopo VARCHAR(30) NOT NULL,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_permissao_chave
        UNIQUE (chave)
);

CREATE TABLE perfil_plataforma (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    tipo_sistema VARCHAR(50),
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_perfil_plataforma_tipo_sistema
        UNIQUE (tipo_sistema)
);

CREATE TABLE perfil_plataforma_permissao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_perfil_plataforma BIGINT NOT NULL,
    id_permissao BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_perfil_plataforma_permissao_perfil_permissao
        UNIQUE (
            id_perfil_plataforma,
            id_permissao
        ),
    CONSTRAINT fk_perfil_plataforma_permissao_perfil
        FOREIGN KEY (id_perfil_plataforma)
        REFERENCES perfil_plataforma (id),
    CONSTRAINT fk_perfil_plataforma_permissao_permissao
        FOREIGN KEY (id_permissao)
        REFERENCES permissao (id)
);

CREATE INDEX idx_perfil_plataforma_permissao_perfil_status
ON perfil_plataforma_permissao (
    id_perfil_plataforma,
    status,
    id_permissao
);

CREATE TABLE usuario_perfil_plataforma (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    id_perfil_plataforma BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_perfil_plataforma_usuario_perfil
        UNIQUE (
            id_usuario,
            id_perfil_plataforma
        ),
    CONSTRAINT fk_usuario_perfil_plataforma_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id),
    CONSTRAINT fk_usuario_perfil_plataforma_perfil
        FOREIGN KEY (id_perfil_plataforma)
        REFERENCES perfil_plataforma (id)
);

CREATE INDEX idx_usuario_perfil_plataforma_usuario_status
ON usuario_perfil_plataforma (
    id_usuario,
    status,
    id_perfil_plataforma
);

CREATE TABLE perfil_permissao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_perfil BIGINT NOT NULL,
    id_permissao BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_perfil_permissao_perfil_permissao
        UNIQUE (
            id_perfil,
            id_permissao
        ),
    CONSTRAINT fk_perfil_permissao_perfil
        FOREIGN KEY (id_perfil)
        REFERENCES perfil (id),
    CONSTRAINT fk_perfil_permissao_permissao
        FOREIGN KEY (id_permissao)
        REFERENCES permissao (id)
);

CREATE INDEX idx_perfil_permissao_perfil_status_permissao
ON perfil_permissao (
    id_perfil,
    status,
    id_permissao
);

CREATE TABLE usuario_perfil (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario_organizacao BIGINT NOT NULL,
    id_perfil BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_perfil_usuario_organizacao
        FOREIGN KEY (id_usuario_organizacao)
        REFERENCES usuario_organizacao (id),
    CONSTRAINT fk_usuario_perfil_perfil
        FOREIGN KEY (id_perfil)
        REFERENCES perfil (id)
);

CREATE INDEX idx_usuario_perfil_vinculo_status_perfil
ON usuario_perfil (
    id_usuario_organizacao,
    status,
    id_perfil
);

CREATE TABLE empresa (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_organizacao BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_empresa_organizacao
        FOREIGN KEY (id_organizacao)
        REFERENCES organizacao (id)
);

CREATE INDEX idx_empresa_organizacao_nome_status
ON empresa (
    id_organizacao,
    nome,
    status
);

CREATE TABLE estabelecimento (
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
    CONSTRAINT fk_estabelecimento_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa (id)
);

CREATE INDEX idx_estabelecimento_empresa_nome_status
ON estabelecimento (
    id_empresa,
    nome,
    status
);

CREATE TABLE usuario_empresa (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario_organizacao BIGINT NOT NULL,
    id_empresa BIGINT NOT NULL,
    todos_estabelecimentos BOOLEAN NOT NULL DEFAULT FALSE,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_empresa_usuario_organizacao
        FOREIGN KEY (id_usuario_organizacao)
        REFERENCES usuario_organizacao (id),
    CONSTRAINT fk_usuario_empresa_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa (id)
);

CREATE INDEX idx_usuario_empresa_vinculo_status_empresa
ON usuario_empresa (
    id_usuario_organizacao,
    status,
    id_empresa
);

CREATE INDEX idx_usuario_empresa_empresa_status
ON usuario_empresa (
    id_empresa,
    status
);

CREATE TABLE usuario_estabelecimento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario_empresa BIGINT NOT NULL,
    id_estabelecimento BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_estabelecimento_usuario_empresa
        FOREIGN KEY (id_usuario_empresa)
        REFERENCES usuario_empresa (id),
    CONSTRAINT fk_usuario_estabelecimento_estabelecimento
        FOREIGN KEY (id_estabelecimento)
        REFERENCES estabelecimento (id)
);

CREATE INDEX idx_usuario_estabelecimento_usuario_empresa_status
ON usuario_estabelecimento (
    id_usuario_empresa,
    status
);

CREATE INDEX idx_usuario_estabelecimento_estabelecimento_status
ON usuario_estabelecimento (
    id_estabelecimento,
    status
);

CREATE INDEX idx_usuario_estabelecimento_vinculo_status
ON usuario_estabelecimento (
    id_usuario_empresa,
    id_estabelecimento,
    status
);

INSERT INTO permissao (
    id,
    nome,
    chave,
    descricao,
    status,
    sistema,
    escopo
) VALUES
(
    1,
    'Criar perfis',
    'ACESSO_PERFIL_CRIAR',
    'Permite criar perfis de acesso',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    2,
    'Listar perfis',
    'ACESSO_PERFIL_LISTAR',
    'Permite listar perfis de acesso',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    3,
    'Detalhar perfil',
    'ACESSO_PERFIL_DETALHAR',
    'Permite detalhar perfil de acesso',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    4,
    'Editar perfil',
    'ACESSO_PERFIL_EDITAR',
    'Permite editar perfil de acesso',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    5,
    'Excluir perfil',
    'ACESSO_PERFIL_EXCLUIR',
    'Permite remover perfil de acesso',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    7,
    'Listar permissoes',
    'ACESSO_PERMISSAO_LISTAR',
    'Permite listar permissoes',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    8,
    'Detalhar permissao',
    'ACESSO_PERMISSAO_DETALHAR',
    'Permite detalhar permissao',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    11,
    'Vincular permissao ao perfil',
    'ACESSO_PERFIL_PERMISSAO_CRIAR',
    'Permite vincular permissao ao perfil',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    12,
    'Listar permissoes do perfil',
    'ACESSO_PERFIL_PERMISSAO_LISTAR',
    'Permite listar permissoes vinculadas ao perfil',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    13,
    'Detalhar vinculo perfil permissao',
    'ACESSO_PERFIL_PERMISSAO_DETALHAR',
    'Permite detalhar vinculo entre perfil e permissao',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    14,
    'Remover permissao do perfil',
    'ACESSO_PERFIL_PERMISSAO_EXCLUIR',
    'Permite remover permissao vinculada ao perfil',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    15,
    'Vincular perfil ao usuario',
    'ACESSO_USUARIO_PERFIL_CRIAR',
    'Permite vincular perfil ao usuario',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    16,
    'Listar perfis do usuario',
    'ACESSO_USUARIO_PERFIL_LISTAR',
    'Permite listar perfis vinculados ao usuario',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    17,
    'Detalhar vinculo usuario perfil',
    'ACESSO_USUARIO_PERFIL_DETALHAR',
    'Permite detalhar vinculo entre usuario e perfil',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    18,
    'Remover perfil do usuario',
    'ACESSO_USUARIO_PERFIL_EXCLUIR',
    'Permite remover perfil vinculado ao usuario',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    19,
    'Criar usuarios',
    'ACESSO_USUARIO_CRIAR',
    'Permite criar usuarios na organizacao',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    20,
    'Listar usuarios',
    'ACESSO_USUARIO_LISTAR',
    'Permite listar usuarios da organizacao',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    21,
    'Detalhar usuario',
    'ACESSO_USUARIO_DETALHAR',
    'Permite detalhar usuario da organizacao',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    22,
    'Excluir usuario',
    'ACESSO_USUARIO_EXCLUIR',
    'Permite remover o acesso do usuario a organizacao',
    0,
    TRUE,
    'ORGANIZACAO'
);

INSERT INTO permissao (
    nome,
    chave,
    descricao,
    status,
    sistema,
    escopo
) VALUES
(
    'Criar empresas',
    'CONFIGURACAO_EMPRESA_CRIAR',
    'Permite criar empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Listar empresas',
    'CONFIGURACAO_EMPRESA_LISTAR',
    'Permite listar empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Detalhar empresa',
    'CONFIGURACAO_EMPRESA_DETALHAR',
    'Permite detalhar empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Editar empresas',
    'CONFIGURACAO_EMPRESA_EDITAR',
    'Permite editar empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Excluir empresas',
    'CONFIGURACAO_EMPRESA_EXCLUIR',
    'Permite remover empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Criar estabelecimentos',
    'CONFIGURACAO_ESTABELECIMENTO_CRIAR',
    'Permite criar estabelecimentos',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Listar estabelecimentos',
    'CONFIGURACAO_ESTABELECIMENTO_LISTAR',
    'Permite listar estabelecimentos',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Detalhar estabelecimento',
    'CONFIGURACAO_ESTABELECIMENTO_DETALHAR',
    'Permite detalhar estabelecimentos',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Editar estabelecimentos',
    'CONFIGURACAO_ESTABELECIMENTO_EDITAR',
    'Permite editar estabelecimentos',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Excluir estabelecimentos',
    'CONFIGURACAO_ESTABELECIMENTO_EXCLUIR',
    'Permite remover estabelecimentos',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Vincular usuario a empresa',
    'ACESSO_USUARIO_EMPRESA_CRIAR',
    'Permite vincular usuarios a empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Listar empresas do usuario',
    'ACESSO_USUARIO_EMPRESA_LISTAR',
    'Permite listar os vinculos entre usuarios e empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Detalhar empresa do usuario',
    'ACESSO_USUARIO_EMPRESA_DETALHAR',
    'Permite detalhar o vinculo entre usuario e empresa',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Editar empresa do usuario',
    'ACESSO_USUARIO_EMPRESA_EDITAR',
    'Permite editar o acesso do usuario a empresa',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Remover empresa do usuario',
    'ACESSO_USUARIO_EMPRESA_EXCLUIR',
    'Permite remover o vinculo entre usuario e empresa',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Vincular usuario a estabelecimento',
    'ACESSO_USUARIO_ESTABELECIMENTO_CRIAR',
    'Permite vincular usuarios a estabelecimentos',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Listar estabelecimentos do usuario',
    'ACESSO_USUARIO_ESTABELECIMENTO_LISTAR',
    'Permite listar os vinculos entre usuarios e estabelecimentos',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Detalhar estabelecimento do usuario',
    'ACESSO_USUARIO_ESTABELECIMENTO_DETALHAR',
    'Permite detalhar o vinculo entre usuario e estabelecimento',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Remover estabelecimento do usuario',
    'ACESSO_USUARIO_ESTABELECIMENTO_EXCLUIR',
    'Permite remover o vinculo entre usuario e estabelecimento',
    0,
    TRUE,
    'ORGANIZACAO'
);

INSERT INTO permissao (
    nome,
    chave,
    descricao,
    status,
    sistema,
    escopo
) VALUES
(
    'Criar organizacoes',
    'PLATAFORMA_ORGANIZACAO_CRIAR',
    'Permite criar organizacoes na plataforma',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Listar organizacoes',
    'PLATAFORMA_ORGANIZACAO_LISTAR',
    'Permite listar organizacoes na plataforma',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Detalhar organizacao',
    'PLATAFORMA_ORGANIZACAO_DETALHAR',
    'Permite detalhar organizacoes na plataforma',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Editar organizacoes',
    'PLATAFORMA_ORGANIZACAO_EDITAR',
    'Permite editar organizacoes na plataforma',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Alterar status de organizacoes',
    'PLATAFORMA_ORGANIZACAO_STATUS',
    'Permite inativar e reativar organizacoes',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Excluir organizacoes',
    'PLATAFORMA_ORGANIZACAO_EXCLUIR',
    'Permite remover organizacoes logicamente',
    0,
    TRUE,
    'PLATAFORMA'
);