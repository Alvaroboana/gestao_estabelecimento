
CREATE TABLE cidade (
    idCidade BIGINT PRIMARY KEY AUTO_INCREMENT,
    nomeCidade VARCHAR(30) NOT NULL
);

DESC cidade;

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

