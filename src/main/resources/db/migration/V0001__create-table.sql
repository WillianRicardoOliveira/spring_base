
-- Criação da tabela RegimeTributacaoFederal
CREATE TABLE regime_tributacao_federal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    removido TINYINT(1) NOT NULL DEFAULT 0
);

-- Criação da tabela SetorAtividade
CREATE TABLE setor_atividade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    removido TINYINT(1) NOT NULL DEFAULT 0
);

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

-- Criação da tabela Entidade
CREATE TABLE entidade (
    codigo BIGINT PRIMARY KEY AUTO_INCREMENT,
    pessoa_juridica TINYINT NOT NULL,
    nome_completo VARCHAR(255) NOT NULL,    
    numero_documento VARCHAR(20) NOT NULL,
    inscricao_estadual VARCHAR(15),
    inscricao_municipal VARCHAR(15),    
    cliente TINYINT NOT NULL,
    fornecedor TINYINT NOT NULL,
    parceiro TINYINT NOT NULL,
    transportador TINYINT NOT NULL,    
    regime_tributacao_federal BIGINT NOT NULL, -- FK
    setor_atividade BIGINT NOT NULL, -- FK    
    endereco BIGINT NOT NULL, -- FK    
    contato_principal VARCHAR(255) NOT NULL,
    email_nfe VARCHAR(255) NOT NULL,    
    email_comercial VARCHAR(255) NOT NULL,
    primeiro_telefone VARCHAR(20) NOT NULL,
    segundo_telefone VARCHAR(20) NOT NULL,    
    observacao VARCHAR(255),    
    nacional TINYINT NOT NULL,    
    matriz BIGINT NOT NULL, -- FK
    FOREIGN KEY (regime_tributacao_federal) REFERENCES regime_tributacao_federal(id),
    FOREIGN KEY (setor_atividade) REFERENCES setor_atividade(id),
    FOREIGN KEY (endereco) REFERENCES endereco(id),
    FOREIGN KEY (matriz) REFERENCES entidade(codigo)
);




















































create table usuario(
    id bigint not null auto_increment,
    email varchar(100) not null,
    senha varchar(255) not null,
    ativo TINYINT(1) not null,
    primary key(id)
);

insert into usuario (id, email, senha, ativo) values (1, 'admin@futuro.com', '$2a$10$UsBJfx7xN/gblDp41EOBfeHhIKW/9Z7x9fUg4uVZJI0FtVQFDehSW', 1);


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
