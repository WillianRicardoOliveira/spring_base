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
    ativo TINYINT(1) not null,
    primary key(id)    
);
create table representante(
    id bigint not null auto_increment,
    nome varchar(100) not null,
    celular varchar(15) not null,
    ativo TINYINT(1) not null,
    id_fornecedor bigint not null,
    primary key(id),
    constraint fk_representante_fornecedor_id foreign key(id_fornecedor) references fornecedor(id)
);
