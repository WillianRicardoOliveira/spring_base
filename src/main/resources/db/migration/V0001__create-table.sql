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
    descricao varchar(250) not null,
    status varchar(30) not null,
    data date not null,
    ativo TINYINT(1) not null,
    primary key(id)
);
create table pessoa(
    id bigint not null auto_increment,
    tipo_movimentacao varchar(20) not null,
    id_compra bigint,
    id_cliente bigint,
    id_produto bigint not null,
    quantidade integer not null,
    total integer not null,
    data datetime not null,
    ativo TINYINT(1) not null,
    primary key(id),
    constraint fk_pessoa_compra_id foreign key(id_compra) references compra(id),
    constraint fk_pessoa_cliente_id foreign key(id_cliente) references cliente(id),
    constraint fk_pessoa_produto_id foreign key(id_produto) references produto(id)
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