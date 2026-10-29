CREATE DATABASE IF NOT EXISTS gestao CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestao;

CREATE TABLE IF NOT EXISTS pessoa (
    id_pessoa BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS contacto (
    id_contacto BIGINT PRIMARY KEY AUTO_INCREMENT,
    contacto VARCHAR(30) NOT NULL,
    id_pessoa BIGINT NOT NULL,
    CONSTRAINT fk_contacto_pessoa FOREIGN KEY (id_pessoa) 
        REFERENCES pessoa(id_pessoa) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cidade (
    id_cidade BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS bairro (
    id_bairro BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(60) NOT NULL,
    id_cidade BIGINT NOT NULL,
    CONSTRAINT fk_bairro_cidade FOREIGN KEY (id_cidade) 
        REFERENCES cidade(id_cidade) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS endereco (
    id_endereco BIGINT PRIMARY KEY AUTO_INCREMENT,
    quarteirao VARCHAR(30) NOT NULL,
    rua VARCHAR(100) NOT NULL,
    numero_casa VARCHAR(20) NOT NULL,
    id_bairro BIGINT NOT NULL,
    id_pessoa BIGINT NOT NULL,
    CONSTRAINT fk_endereco_bairro FOREIGN KEY (id_bairro) 
        REFERENCES bairro(id_bairro) ON DELETE RESTRICT,
    CONSTRAINT fk_endereco_pessoa FOREIGN KEY (id_pessoa) 
        REFERENCES pessoa(id_pessoa) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cargo (
    id_cargo BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    salario_base DECIMAL(10, 2) NOT NULL CHECK (salario_base >= 0)
);

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_pessoa BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_cliente_pessoa FOREIGN KEY (id_pessoa) 
        REFERENCES pessoa(id_pessoa) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS funcionario (
    id_funcionario BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_pessoa BIGINT NOT NULL UNIQUE,
    bi VARCHAR(20) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    data_inicio DATE NOT NULL,
    estado ENUM('ATIVO', 'INATIVO', 'SUSPENSO') NOT NULL DEFAULT 'ATIVO',
    id_cargo BIGINT NOT NULL,
    CONSTRAINT fk_funcionario_pessoa FOREIGN KEY (id_pessoa) 
        REFERENCES pessoa(id_pessoa) ON DELETE CASCADE,
    CONSTRAINT fk_funcionario_cargo FOREIGN KEY (id_cargo) 
        REFERENCES cargo(id_cargo) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS utilizador (
    id_utilizador BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    perfil ENUM('ADMIN', 'GERENTE', 'VENDEDOR') NOT NULL,
    id_funcionario BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_utilizador_funcionario FOREIGN KEY (id_funcionario) 
        REFERENCES funcionario(id_funcionario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fornecedor (
    id_fornecedor BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_pessoa BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_fornecedor_pessoa FOREIGN KEY (id_pessoa) 
        REFERENCES pessoa(id_pessoa) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS produto (
    id_produto BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL CHECK (preco >= 0),
    descricao TEXT NULL,
    unidade VARCHAR(20) NOT NULL,
    estado ENUM('ATIVO', 'DESCONTINUADO') NOT NULL DEFAULT 'ATIVO',
    quantidade_minima INT NOT NULL DEFAULT 0 CHECK (quantidade_minima >= 0),
    id_categoria BIGINT NOT NULL,
    CONSTRAINT fk_produto_categoria FOREIGN KEY (id_categoria) 
        REFERENCES categoria(id_categoria) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS estoque (
    id_estoque BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantidade INT NOT NULL CHECK (quantidade >= 0),
    data_entrada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_validade DATE NULL,
    id_produto BIGINT NOT NULL,
    id_funcionario BIGINT NOT NULL,
    CONSTRAINT fk_estoque_produto FOREIGN KEY (id_produto) 
        REFERENCES produto(id_produto) ON DELETE CASCADE,
    CONSTRAINT fk_estoque_funcionario FOREIGN KEY (id_funcionario) 
        REFERENCES funcionario(id_funcionario) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS fornecedor_produto (
    id_fornecedor_produto BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_produto BIGINT NOT NULL,
    id_fornecedor BIGINT NOT NULL,
    CONSTRAINT uq_fornecedor_produto UNIQUE (id_produto, id_fornecedor),
    CONSTRAINT fk_fp_produto FOREIGN KEY (id_produto) 
        REFERENCES produto(id_produto) ON DELETE CASCADE,
    CONSTRAINT fk_fp_fornecedor FOREIGN KEY (id_fornecedor) 
        REFERENCES fornecedor(id_fornecedor) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS venda (
    id_venda BIGINT PRIMARY KEY AUTO_INCREMENT,
    data DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (total >= 0),
    estado ENUM('PENDENTE', 'CONCLUIDA', 'ANULADA', 'REEMBOLSADA') NOT NULL DEFAULT 'PENDENTE',
    id_cliente BIGINT NOT NULL,
    id_funcionario BIGINT NOT NULL,
    CONSTRAINT fk_venda_cliente FOREIGN KEY (id_cliente) 
        REFERENCES cliente(id_cliente) ON DELETE RESTRICT,
    CONSTRAINT fk_venda_funcionario FOREIGN KEY (id_funcionario) 
        REFERENCES funcionario(id_funcionario) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS item_vendido (
    id_item BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco DECIMAL(10, 2) NOT NULL CHECK (preco >= 0),
    desconto DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (desconto >= 0),
    id_venda BIGINT NOT NULL,
    id_produto BIGINT NOT NULL,
    CONSTRAINT fk_item_venda FOREIGN KEY (id_venda) 
        REFERENCES venda(id_venda) ON DELETE CASCADE,
    CONSTRAINT fk_item_produto FOREIGN KEY (id_produto) 
        REFERENCES produto(id_produto) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS reembolso (
    id_reembolso BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    valor DECIMAL(10, 2) NOT NULL CHECK (valor >= 0),
    motivo TEXT NOT NULL,
    data DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_item BIGINT NOT NULL,
    id_funcionario BIGINT NOT NULL,
    CONSTRAINT fk_reembolso_item FOREIGN KEY (id_item) 
        REFERENCES item_vendido(id_item) ON DELETE RESTRICT,
    CONSTRAINT fk_reembolso_funcionario FOREIGN KEY (id_funcionario) 
        REFERENCES funcionario(id_funcionario) ON DELETE RESTRICT
);
