-- Active: 1793018340540@@127.0.0.1@3307

CREATE TABLE cidade (
    idCidade BIGINT PRIMARY KEY AUTO_INCREMENT,
    nomeCidade VARCHAR(30) NOT NULL
);

CREATE TABLE bairro(
    idBairro BIGINT PRIMARY KEY AUTO_INCREMENT,
    nomeBairro VARCHAR(30) NOT NULL,
    idCidade BIGINT NOT NULL,
    FOREIGN KEY(idCidade) REFERENCES cidade(idCidade)
);

CREATE TABLE categoria (
    idCategoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nomeCategoria VARCHAR(40) NOT NULL
);

CREATE TABLE estoque(
    idEstoque BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantidade BIGINT NOT NULL CHECK(quantidade >= 0),
    dataActualizacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dataValidade DATE NULL 
);

CREATE TABLE contacto(
    id_contacto BIGINT PRIMARY KEY AUTO_INCREMENT,
    contacto VARCHAR(20) NOT NULL
);

CREATE TABLE cargo(
    id_cargo BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30) NOT NULL,
    salario DECIMAL(10,2) NOT NULL CHECK(salario >= 0)
);

CREATE TABLE endereco(
    id_endereco BIGINT PRIMARY KEY AUTO_INCREMENT,
    quarteirao VARCHAR(5) NOT NULL,
    numero_casa BIGINT NOT NULL,
    referencia TEXT NULL,
    id_bairro BIGINT NOT NULL,
    FOREIGN KEY(id_bairro) REFERENCES bairro(idBairro)
);

CREATE TABLE produto(
    id_produto BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigoBarra VARCHAR(50) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    descricao TEXT NULL,
    unidade CHAR(10) NOT NULL,
    estado BOOLEAN NOT NULL,
    id_categoria BIGINT NOT NULL,
    FOREIGN KEY(id_categoria) REFERENCES categoria(idCategoria)
);


CREATE TABLE funcionario(
    id_funcionario BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30) NOT NULL,
    dataNascimento DATE NOT NULL,
    nuit VARCHAR(15) NOT NULL,
    estado BOOLEAN NOT NULL,
    id_contacto BIGINT NOT NULL,
    id_cargo BIGINT NOT NULL,
    id_endereco BIGINT NOT NULL,
    FOREIGN KEY(id_contacto) REFERENCES contacto(id_contacto),
    FOREIGN KEY(id_cargo) REFERENCES cargo(id_cargo),
    FOREIGN KEY(id_endereco) REFERENCES endereco(id_endereco));

CREATE TABLE cliente(
    id_cliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NULL,
    dataRegisto DATE NOT NULL,
    id_contacto BIGINT NULL,
    id_endereco BIGINT NULL,
    FOREIGN KEY(id_endereco) REFERENCES endereco(id_endereco),
    FOREIGN KEY(id_contacto) REFERENCES contacto(id_contacto)
);