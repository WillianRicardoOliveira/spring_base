create table usuario(
    id bigint not null auto_increment,
    email varchar(100) not null,
    senha varchar(255) not null,
    primary key(id)
);

create table estado(
    id bigint not null auto_increment,
    nome varchar(100) not null,
    sigla char(2) not null,
    primary key(id)
);

create table endereco(
    id bigint not null auto_increment,
    logradouro varchar(100) not null,
    bairro varchar(100) not null,
    cep varchar(9) not null,
    id_estado bigint not null,
    cidade varchar(100) not null,
    numero varchar(20) not null,
    complemento varchar(100),
    primary key(id),
    constraint fk_endereco_estado_id foreign key(id_estado) references estado(id)
);

create table pessoa(
    id bigint not null auto_increment,
    nome varchar(100) not null,
    nascimento varchar(10) not null,
    genero varchar(50) not null,
    cpf varchar(20) not null,
    telefone varchar(20) not null,
    id_endereco bigint not null,
    id_usuario bigint not null,
    aceite_termo TINYINT(1) not null,
    tipo_pessoa varchar(50) not null,
    ativo TINYINT(1) not null,
    primary key(id),
    constraint fk_pessoa_endereco_id foreign key(id_endereco) references endereco(id),
    constraint fk_pessoa_usuario_id foreign key(id_usuario) references usuario(id)
);