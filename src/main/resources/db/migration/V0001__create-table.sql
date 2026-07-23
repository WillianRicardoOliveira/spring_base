
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
    CONSTRAINT uk_usuario_email UNIQUE (email)
);

CREATE TABLE usuario_sessao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    refresh_token_hash VARCHAR(255) NOT NULL,
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
    CONSTRAINT fk_usuario_sessao_usuario_id FOREIGN KEY (id_usuario) REFERENCES usuario (id),
    CONSTRAINT uk_usuario_sessao_refresh_token_hash UNIQUE (refresh_token_hash)
);

CREATE INDEX idx_usuario_sessao_usuario_status
ON usuario_sessao (id_usuario, status);

CREATE INDEX idx_usuario_sessao_expira_em
ON usuario_sessao (expira_em);

CREATE INDEX idx_usuario_sessao_access_token_jti
ON usuario_sessao (access_token_jti);

CREATE TABLE perfil (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 0,
    sistema BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE usuario_perfil (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    id_perfil BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_perfil_usuario_id FOREIGN KEY (id_usuario) REFERENCES usuario (id),
    CONSTRAINT fk_usuario_perfil_perfil_id FOREIGN KEY (id_perfil) REFERENCES perfil (id)
);

CREATE TABLE permissao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    chave VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 0,
    sistema BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em DATETIME,
    criado_por BIGINT,
    atualizado_em DATETIME,
    atualizado_por BIGINT,
    removido_em DATETIME,
    removido_por BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_permissao_chave UNIQUE (chave)
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
    CONSTRAINT fk_perfil_permissao_perfil_id FOREIGN KEY (id_perfil) REFERENCES perfil (id),
    CONSTRAINT fk_perfil_permissao_permissao_id FOREIGN KEY (id_permissao) REFERENCES permissao (id)
);

INSERT INTO usuario (id, email, senha, status) VALUES 
(1, 'admin@futuro.com', '$2a$10$UsBJfx7xN/gblDp41EOBfeHhIKW/9Z7x9fUg4uVZJI0FtVQFDehSW', 0);

INSERT INTO perfil (id, nome, descricao, status, sistema) VALUES
(1, 'Administrador', 'Perfil com acesso total ao sistema', 0, TRUE);

INSERT INTO permissao (id, nome, chave, descricao, status, sistema) VALUES
(1, 'Criar perfis', 'ACESSO_PERFIL_CRIAR', 'Permite criar perfis de acesso', 0, TRUE),
(2, 'Listar perfis', 'ACESSO_PERFIL_LISTAR', 'Permite listar perfis de acesso', 0, TRUE),
(3, 'Detalhar perfil', 'ACESSO_PERFIL_DETALHAR', 'Permite detalhar perfil de acesso', 0, TRUE),
(4, 'Editar perfil', 'ACESSO_PERFIL_EDITAR', 'Permite editar perfil de acesso', 0, TRUE),
(5, 'Excluir perfil', 'ACESSO_PERFIL_EXCLUIR', 'Permite remover perfil de acesso', 0, TRUE),

(6, 'Criar permissões', 'ACESSO_PERMISSAO_CRIAR', 'Permite criar permissões', 0, TRUE),
(7, 'Listar permissões', 'ACESSO_PERMISSAO_LISTAR', 'Permite listar permissões', 0, TRUE),
(8, 'Detalhar permissão', 'ACESSO_PERMISSAO_DETALHAR', 'Permite detalhar permissão', 0, TRUE),
(9, 'Editar permissão', 'ACESSO_PERMISSAO_EDITAR', 'Permite editar permissão', 0, TRUE),
(10, 'Excluir permissão', 'ACESSO_PERMISSAO_EXCLUIR', 'Permite remover permissão', 0, TRUE),

(11, 'Vincular permissão ao perfil', 'ACESSO_PERFIL_PERMISSAO_CRIAR', 'Permite vincular permissão ao perfil', 0, TRUE),
(12, 'Listar permissões do perfil', 'ACESSO_PERFIL_PERMISSAO_LISTAR', 'Permite listar permissões vinculadas ao perfil', 0, TRUE),
(13, 'Detalhar vínculo perfil permissão', 'ACESSO_PERFIL_PERMISSAO_DETALHAR', 'Permite detalhar vínculo entre perfil e permissão', 0, TRUE),
(14, 'Remover permissão do perfil', 'ACESSO_PERFIL_PERMISSAO_EXCLUIR', 'Permite remover permissão vinculada ao perfil', 0, TRUE),

(15, 'Vincular perfil ao usuário', 'ACESSO_USUARIO_PERFIL_CRIAR', 'Permite vincular perfil ao usuário', 0, TRUE),
(16, 'Listar perfis do usuário', 'ACESSO_USUARIO_PERFIL_LISTAR', 'Permite listar perfis vinculados ao usuário', 0, TRUE),
(17, 'Detalhar vínculo usuário perfil', 'ACESSO_USUARIO_PERFIL_DETALHAR', 'Permite detalhar vínculo entre usuário e perfil', 0, TRUE),
(18, 'Remover perfil do usuário', 'ACESSO_USUARIO_PERFIL_EXCLUIR', 'Permite remover perfil vinculado ao usuário', 0, TRUE),

(19, 'Criar usuários', 'ACESSO_USUARIO_CRIAR', 'Permite criar usuários', 0, TRUE),
(20, 'Listar usuários', 'ACESSO_USUARIO_LISTAR', 'Permite listar usuários', 0, TRUE),
(21, 'Detalhar usuário', 'ACESSO_USUARIO_DETALHAR', 'Permite detalhar usuário', 0, TRUE),
(22, 'Editar usuário', 'ACESSO_USUARIO_EDITAR', 'Permite editar usuário', 0, TRUE),
(23, 'Excluir usuário', 'ACESSO_USUARIO_EXCLUIR', 'Permite remover usuário', 0, TRUE),
(24, 'Alterar senha de usuário', 'ACESSO_USUARIO_SENHA_EDITAR', 'Permite alterar senha de usuário', 0, TRUE);

INSERT INTO perfil_permissao (id_perfil, id_permissao, status) VALUES
(1, 1, 0),
(1, 2, 0),
(1, 3, 0),
(1, 4, 0),
(1, 5, 0),
(1, 6, 0),
(1, 7, 0),
(1, 8, 0),
(1, 9, 0),
(1, 10, 0),
(1, 11, 0),
(1, 12, 0),
(1, 13, 0),
(1, 14, 0),
(1, 15, 0),
(1, 16, 0),
(1, 17, 0),
(1, 18, 0),
(1, 19, 0),
(1, 20, 0),
(1, 21, 0),
(1, 22, 0),
(1, 23, 0),
(1, 24, 0);

INSERT INTO usuario_perfil (id_usuario, id_perfil, status) VALUES
(1, 1, 0);

CREATE TABLE USUARIO_LOGIN_TENTATIVA (
    ID BIGINT NOT NULL AUTO_INCREMENT,
    EMAIL VARCHAR(100) NOT NULL,
    QUANTIDADE_FALHAS INT NOT NULL DEFAULT 0,
    ULTIMA_FALHA_EM DATETIME NULL,
    BLOQUEADO_ATE DATETIME NULL,
    STATUS TINYINT NOT NULL DEFAULT 0,
    CRIADO_EM DATETIME NULL,
    CRIADO_POR BIGINT NULL,
    ATUALIZADO_EM DATETIME NULL,
    ATUALIZADO_POR BIGINT NULL,
    REMOVIDO_EM DATETIME NULL,
    REMOVIDO_POR BIGINT NULL,
    PRIMARY KEY(ID),
    UNIQUE KEY UK_USUARIO_LOGIN_TENTATIVA_EMAIL (EMAIL)
);

































































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
