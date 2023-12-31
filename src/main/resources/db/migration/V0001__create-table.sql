create table usuario(
    id bigint not null auto_increment,
    email varchar(100) not null,
    senha varchar(255) not null,
    primary key(id)
);

create table endereco(
    id bigint not null auto_increment,
    cep varchar(9) not null,
    logradouro varchar(100) not null,
    complemento varchar(100),
    bairro varchar(100) not null,
    localidade varchar(100) not null,
    uf varchar(2) not null,
    numero varchar(20) not null,
    primary key(id)
);

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
create table fornecedor(
    id bigint not null auto_increment,
    cnpj varchar(14) not null,
    nome varchar(100) not null,
    telefone varchar(15) not null,
    descricao varchar(250),    
    ativo TINYINT(1) not null,
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
create table tipo_movimentacao(
    id bigint not null auto_increment,
    nome varchar(20) not null,
    ativo TINYINT(1) not null,
    primary key(id)
);
-- #################### --
-- FINANCEIRO           --
-- #################### --
create table categoria_conta (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(100) NOT NULL,
  primary key(id)
);
create table sub_categoria_conta (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(100) NOT NULL,
  id_categoria_conta bigint NOT NULL,
  primary key(id),
  constraint fk_sub_categoria_conta_categoria_id foreign key(id_categoria_conta) references categoria_conta(id)
);
create table conta_pagar (
  id bigint NOT NULL AUTO_INCREMENT,
  id_fornecedor bigint NOT NULL,
  id_sub_categoria bigint NOT NULL,
  descricao varchar(250) DEFAULT NULL,
  valor decimal(8,2) NOT NULL,
  parcelas int NOT NULL,
  status varchar(30) NOT NULL DEFAULT 'pendente',
  primary key(id),
  constraint fk_conta_pagar_fornecedor_id foreign key(id_fornecedor) references fornecedor(id),
  constraint fk_conta_pagar_sub_categoria foreign key(id_sub_categoria) references sub_categoria_conta(id)
);
create table conta_pagar_parcelas (
  id bigint NOT NULL AUTO_INCREMENT,
  id_conta_pagar bigint NOT NULL,
  parcela int NOT NULL,
  vencimento date NOT NULL,
  pagamento datetime DEFAULT NULL,
  valor decimal(8,2) NOT NULL,
  status varchar(30) NOT NULL,
  metodo_pagamento varchar(30) NOT NULL,
  primary key(id),
  constraint fk_conta_pagar_parcelas_conta_pagar_id foreign key(id_conta_pagar) references conta_pagar(id)
)