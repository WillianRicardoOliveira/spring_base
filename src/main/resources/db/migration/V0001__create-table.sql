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
ON subsidiaria (
    id_empresa,
    nome,
    status
);

CREATE TABLE usuario_empresa (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario_organizacao BIGINT NOT NULL,
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
    'Listar permissões',
    'ACESSO_PERMISSAO_LISTAR',
    'Permite listar permissões',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    8,
    'Detalhar permissão',
    'ACESSO_PERMISSAO_DETALHAR',
    'Permite detalhar permissão',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    11,
    'Vincular permissão ao perfil',
    'ACESSO_PERFIL_PERMISSAO_CRIAR',
    'Permite vincular permissão ao perfil',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    12,
    'Listar permissões do perfil',
    'ACESSO_PERFIL_PERMISSAO_LISTAR',
    'Permite listar permissões vinculadas ao perfil',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    13,
    'Detalhar vínculo perfil permissão',
    'ACESSO_PERFIL_PERMISSAO_DETALHAR',
    'Permite detalhar vínculo entre perfil e permissão',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    14,
    'Remover permissão do perfil',
    'ACESSO_PERFIL_PERMISSAO_EXCLUIR',
    'Permite remover permissão vinculada ao perfil',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    15,
    'Vincular perfil ao usuário',
    'ACESSO_USUARIO_PERFIL_CRIAR',
    'Permite vincular perfil ao usuário',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    16,
    'Listar perfis do usuário',
    'ACESSO_USUARIO_PERFIL_LISTAR',
    'Permite listar perfis vinculados ao usuário',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    17,
    'Detalhar vínculo usuário perfil',
    'ACESSO_USUARIO_PERFIL_DETALHAR',
    'Permite detalhar vínculo entre usuário e perfil',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    18,
    'Remover perfil do usuário',
    'ACESSO_USUARIO_PERFIL_EXCLUIR',
    'Permite remover perfil vinculado ao usuário',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    19,
    'Criar usuários',
    'ACESSO_USUARIO_CRIAR',
    'Permite criar usuários na organização',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    20,
    'Listar usuários',
    'ACESSO_USUARIO_LISTAR',
    'Permite listar usuários da organização',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    21,
    'Detalhar usuário',
    'ACESSO_USUARIO_DETALHAR',
    'Permite detalhar usuário da organização',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    22,
    'Excluir usuário',
    'ACESSO_USUARIO_EXCLUIR',
    'Permite remover o acesso do usuário à organização',
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
    'Criar subsidiárias',
    'CONFIGURACAO_SUBSIDIARIA_CRIAR',
    'Permite criar subsidiárias',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Listar subsidiárias',
    'CONFIGURACAO_SUBSIDIARIA_LISTAR',
    'Permite listar subsidiárias',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Detalhar subsidiária',
    'CONFIGURACAO_SUBSIDIARIA_DETALHAR',
    'Permite detalhar subsidiárias',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Editar subsidiárias',
    'CONFIGURACAO_SUBSIDIARIA_EDITAR',
    'Permite editar subsidiárias',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Excluir subsidiárias',
    'CONFIGURACAO_SUBSIDIARIA_EXCLUIR',
    'Permite remover subsidiárias',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Vincular usuário a empresa',
    'ACESSO_USUARIO_EMPRESA_CRIAR',
    'Permite vincular usuários a empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Listar empresas do usuário',
    'ACESSO_USUARIO_EMPRESA_LISTAR',
    'Permite listar os vínculos entre usuários e empresas',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Detalhar empresa do usuário',
    'ACESSO_USUARIO_EMPRESA_DETALHAR',
    'Permite detalhar o vínculo entre usuário e empresa',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Editar empresa do usuário',
    'ACESSO_USUARIO_EMPRESA_EDITAR',
    'Permite editar o acesso do usuário à empresa',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Remover empresa do usuário',
    'ACESSO_USUARIO_EMPRESA_EXCLUIR',
    'Permite remover o vínculo entre usuário e empresa',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Vincular usuário a subsidiária',
    'ACESSO_USUARIO_SUBSIDIARIA_CRIAR',
    'Permite vincular usuários a subsidiárias',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Listar subsidiárias do usuário',
    'ACESSO_USUARIO_SUBSIDIARIA_LISTAR',
    'Permite listar os vínculos entre usuários e subsidiárias',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Detalhar subsidiária do usuário',
    'ACESSO_USUARIO_SUBSIDIARIA_DETALHAR',
    'Permite detalhar o vínculo entre usuário e subsidiária',
    0,
    TRUE,
    'ORGANIZACAO'
),
(
    'Remover subsidiária do usuário',
    'ACESSO_USUARIO_SUBSIDIARIA_EXCLUIR',
    'Permite remover o vínculo entre usuário e subsidiária',
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
    'Criar organizações',
    'PLATAFORMA_ORGANIZACAO_CRIAR',
    'Permite criar organizações na plataforma',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Listar organizações',
    'PLATAFORMA_ORGANIZACAO_LISTAR',
    'Permite listar organizações na plataforma',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Detalhar organização',
    'PLATAFORMA_ORGANIZACAO_DETALHAR',
    'Permite detalhar organizações na plataforma',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Editar organizações',
    'PLATAFORMA_ORGANIZACAO_EDITAR',
    'Permite editar organizações na plataforma',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Alterar status de organizações',
    'PLATAFORMA_ORGANIZACAO_STATUS',
    'Permite inativar e reativar organizações',
    0,
    TRUE,
    'PLATAFORMA'
),
(
    'Excluir organizações',
    'PLATAFORMA_ORGANIZACAO_EXCLUIR',
    'Permite remover organizações logicamente',
    0,
    TRUE,
    'PLATAFORMA'
);

-- ##################################################### --
-- ESTRUTURAS COMENTADAS PARA DESENVOLVIMENTO FUTURO     --
-- ##################################################### --

/*
-- Criação da tabela RegimeTributacaoFederal
CREATE TABLE regime_tributacao_federal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    removido TINYINT(1) NOT NULL DEFAULT 0
);
*/
/*
-- Criação da tabela SetorAtividade
CREATE TABLE setor_atividade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    removido TINYINT(1) NOT NULL DEFAULT 0
);
*/



/*
create table endereco(
    id bigint not null auto_increment,
    cep varchar(10) not null,
    localidade varchar(100) not null,
    uf varchar(2) not null,
    bairro varchar(100) not null,
    logradouro varchar(150) not null,
    numero varchar(10) not null,
    complemento varchar(50),
    ativo tinyint(1) not null,
    primary key(id)
);
*/
































/*
-- Criação da tabela Entidade
CREATE TABLE entidade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pessoa_juridica TINYINT NOT NULL,
    nome_completo VARCHAR(255) NOT NULL,    
    numero_documento VARCHAR(20) NOT NULL,
    inscricao_estadual VARCHAR(15),
    inscricao_municipal VARCHAR(15),    
    cliente TINYINT NOT NULL,
    fornecedor TINYINT NOT NULL,
    parceiro TINYINT NOT NULL,
    transportador TINYINT NOT NULL,    
    id_regime_tributacao_federal BIGINT NOT NULL,
    id_setor_atividade BIGINT NOT NULL,
    id_endereco BIGINT NOT NULL,   
    contato_principal VARCHAR(255) NOT NULL,
    email_nfe VARCHAR(255) NOT NULL,    
    email_comercial VARCHAR(255) NOT NULL,
    primeiro_telefone VARCHAR(20) NOT NULL,
    segundo_telefone VARCHAR(20) NOT NULL,
    nacional TINYINT NOT NULL,    
    id_matriz BIGINT,
    status TINYINT,
    FOREIGN KEY (id_regime_tributacao_federal) REFERENCES regime_tributacao_federal(id),
    FOREIGN KEY (id_setor_atividade) REFERENCES setor_atividade(id),
    FOREIGN KEY (id_endereco) REFERENCES endereco(id),
    FOREIGN KEY (id_matriz) REFERENCES entidade(id)
);
*/



















































/*
create table pessoa(
    id bigint not null auto_increment,
    nome varchar(100) not null,
    nascimento date not null,
    genero varchar(50) not null,
    cpf varchar(20) not null,
    telefone varchar(20) not null,
    id_endereco bigint not null,
    id_usuario bigint not null,
    aceitar_termos TINYINT(1) not null,
    tipo_pessoa varchar(50) not null,
    ativo TINYINT(1) not null,
    primary key(id),
    constraint fk_pessoa_endereco_id foreign key(id_endereco) references endereco(id),
    constraint fk_pessoa_usuario_id foreign key(id_usuario) references usuario(id)
);
*/








/*

-- #################### --
-- ATENDIMENTO          --
-- #################### --
create table cliente(
    id bigint not null auto_increment,
    nome varchar(100) not null,
    telefone varchar(15) not null,
    ativo TINYINT(1) not null,
    primary key(id)
);
-- #################### --
-- ESTOQUE              --
-- #################### --
create table produto(
    id bigint not null auto_increment,
    nome varchar(100) not null,
    descricao varchar(250),
    quantidade int not null,
    minimo int not null,
    maximo int not null,
    ativo tinyint not null,
    primary key(id)
);

create table compra(
    id bigint not null auto_increment,
    nome varchar(100) not null,
    descricao varchar(250) not null,
    status varchar(30) not null,
    data date not null,
    ativo TINYINT(1) not null,
    primary key(id)
);
create table compra_item(
    id bigint not null auto_increment,
    id_compra bigint not null,
    id_fornecedor bigint not null,
    id_produto bigint not null,
    quantidade integer not null,
    valor decimal(8, 2) not null,
    total decimal(8, 2) not null,
    controle integer not null,
    ativo TINYINT(1) not null,
    primary key(id),
    constraint fk_compra_item_compra_id foreign key(id_compra) references compra(id),
    constraint fk_compra_item_fornecedor_id foreign key(id_fornecedor) references fornecedor(id),
    constraint fk_compra_item_produto_id foreign key(id_produto) references produto(id)
);
create table tipo_movimentacao(
    id bigint not null auto_increment,
    nome varchar(20) not null,
    ativo TINYINT(1) not null,
    primary key(id)
);
create table movimentacao(
    id bigint not null auto_increment,
    id_tipo_movimentacao bigint not null,
    id_compra bigint,
    id_produto bigint not null,
    quantidade integer not null,
    total integer not null,
    data datetime not null,
    ativo TINYINT(1) not null,
    primary key(id),
    constraint fk_movimentacao_tipo_movimentacao_id foreign key(id_tipo_movimentacao) references tipo_movimentacao(id),
    constraint fk_movimentacao_compra_id foreign key(id_compra) references compra(id),
    constraint fk_movimentacao_produto_id foreign key(id_produto) references produto(id)
);
-- #################### --
-- FINANCEIRO           --
-- #################### --
create table categoria_conta (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(100) NOT NULL,
  ativo TINYINT(1) not null,
  primary key(id)
);
create table forma_pagamento (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(100) NOT NULL,
  ativo TINYINT(1) not null,
  primary key(id)
);
create table status_pagamento (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(100) NOT NULL,
  ativo TINYINT(1) not null,
  primary key(id)
);
create table banco (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(100) NOT NULL,
  ativo TINYINT(1) NOT NULL,
  primary key(id)
);
create table conta (
  id bigint NOT NULL AUTO_INCREMENT,
  id_banco bigint NOT NULL,
  agencia varchar(5) NOT NULL,
  conta varchar(6) NOT NULL,
  digito varchar(2) NOT NULL,
  pix varchar(50),
  ativo TINYINT(1) NOT NULL,
  primary key(id),
  constraint fk_conta_banco_id foreign key(id_banco) references banco(id)
);
create table cartao (
  id bigint NOT NULL AUTO_INCREMENT,
  id_conta bigint NOT NULL,
  id_forma_pagamento bigint NOT NULL,
  numero_cartao varchar(20) NOT NULL,
  validade_mes varchar(2) NOT NULL,
  validade_ano varchar(4) NOT NULL,
  ativo TINYINT(1) not null,  
  primary key(id),
  constraint fk_cartao_conta_id foreign key(id_conta) references conta(id),
  constraint fk_cartao_forma_pagamento_id foreign key(id_forma_pagamento) references forma_pagamento(id)
);
create table sub_categoria_conta (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(100) NOT NULL,
  ativo TINYINT(1) not null,
  id_categoria_conta bigint NOT NULL,  
  primary key(id),
  constraint fk_sub_categoria_conta_categoria_id foreign key(id_categoria_conta) references categoria_conta(id)
);
create table conta_pagar (
  id bigint NOT NULL AUTO_INCREMENT,
  id_fornecedor bigint NOT NULL,
  id_sub_categoria bigint NOT NULL,
  id_status_pagamento bigint NOT NULL, 
  id_forma_pagamento bigint NOT NULL, 
  descricao varchar(250) DEFAULT NULL,
  valor decimal(8,2) NOT NULL,
  parcelas int NOT NULL,
  ativo TINYINT(1) not null,
  primary key(id),
  constraint fk_conta_pagar_fornecedor_id foreign key(id_fornecedor) references fornecedor(id),
  constraint fk_conta_pagar_sub_categoria foreign key(id_sub_categoria) references sub_categoria_conta(id),
  constraint fk_status_pagamento_pagamento_status_id foreign key(id_status_pagamento) references status_pagamento(id),
  constraint fk_forma_pagamento_pagamento_forma_id foreign key(id_forma_pagamento) references forma_pagamento(id)
);
create table conta_pagar_parcelas (
  id bigint NOT NULL AUTO_INCREMENT,
  id_conta_pagar bigint NOT NULL,
  parcela int NOT NULL,
  vencimento date NOT NULL,
  pagamento datetime DEFAULT NULL,
  valor decimal(8,2) NOT NULL,
  id_status_pagamento bigint NOT NULL, 
  ativo TINYINT(1) not null,
  primary key(id),
  constraint fk_conta_pagar_parcelas_conta_pagar_id foreign key(id_conta_pagar) references conta_pagar(id),
  constraint fk_conta_pagar_parcelas_status_pagamento_id foreign key(id_status_pagamento) references status_pagamento(id)
);*/
-- #################### --
-- DADOS BASICOS        --
-- #################### --
/*

insert into categoria_conta (id, nome, ativo) values (1, 'Despesas Operacionais', 1);
insert into sub_categoria_conta (id, nome, id_categoria_conta, ativo) values (1, 'Água', 1, 1);
insert into sub_categoria_conta (id, nome, id_categoria_conta, ativo) values (2, 'Luz', 1, 1);

insert into regime_tributacao_federal(id, nome, ativo) values (1, 'a', 1)

insert into setor_atividade(id, nome, ativo) values (1, 'a', 1)

INSERT INTO endereco (id, cep, localidade, uf, bairro, logradouro, numero, complemento, ativo) VALUES (1, '00000000', 'abc', 'PR', 'abc', 'abc', 'ab', 'ab', 1);




INSERT INTO pessoa (id, nome, nascimento, genero, cpf, telefone, id_endereco, id_usuario, aceitar_termos, tipo_pessoa, ativo)
VALUES (1, 'adm', '19900212', 'masculino', '06488383906', '988755471', 1, 1, 1, 'CLIENTE', 1);
*/
