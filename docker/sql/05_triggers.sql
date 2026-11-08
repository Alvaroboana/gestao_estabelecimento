USE gestao;

DELIMITER $$

DROP TRIGGER IF EXISTS trg_pessoa_ai_auditoria$$
CREATE TRIGGER trg_pessoa_ai_auditoria
AFTER INSERT ON pessoa
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('pessoa', NEW.id_pessoa, 'INSERT', NULL, JSON_OBJECT('id_pessoa', NEW.id_pessoa, 'nome', NEW.nome), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_pessoa_au_auditoria$$
CREATE TRIGGER trg_pessoa_au_auditoria
AFTER UPDATE ON pessoa
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('pessoa', NEW.id_pessoa, 'UPDATE', JSON_OBJECT('id_pessoa', OLD.id_pessoa, 'nome', OLD.nome), JSON_OBJECT('id_pessoa', NEW.id_pessoa, 'nome', NEW.nome), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_pessoa_ad_auditoria$$
CREATE TRIGGER trg_pessoa_ad_auditoria
AFTER DELETE ON pessoa
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('pessoa', OLD.id_pessoa, 'DELETE', JSON_OBJECT('id_pessoa', OLD.id_pessoa, 'nome', OLD.nome), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_contacto_ai_auditoria$$
CREATE TRIGGER trg_contacto_ai_auditoria
AFTER INSERT ON contacto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('contacto', NEW.id_contacto, 'INSERT', NULL, JSON_OBJECT('id_contacto', NEW.id_contacto, 'contacto', NEW.contacto, 'id_pessoa', NEW.id_pessoa), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_contacto_au_auditoria$$
CREATE TRIGGER trg_contacto_au_auditoria
AFTER UPDATE ON contacto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('contacto', NEW.id_contacto, 'UPDATE', JSON_OBJECT('id_contacto', OLD.id_contacto, 'contacto', OLD.contacto, 'id_pessoa', OLD.id_pessoa), JSON_OBJECT('id_contacto', NEW.id_contacto, 'contacto', NEW.contacto, 'id_pessoa', NEW.id_pessoa), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_contacto_ad_auditoria$$
CREATE TRIGGER trg_contacto_ad_auditoria
AFTER DELETE ON contacto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('contacto', OLD.id_contacto, 'DELETE', JSON_OBJECT('id_contacto', OLD.id_contacto, 'contacto', OLD.contacto, 'id_pessoa', OLD.id_pessoa), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cidade_ai_auditoria$$
CREATE TRIGGER trg_cidade_ai_auditoria
AFTER INSERT ON cidade
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cidade', NEW.id_cidade, 'INSERT', NULL, JSON_OBJECT('id_cidade', NEW.id_cidade, 'nome', NEW.nome), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cidade_au_auditoria$$
CREATE TRIGGER trg_cidade_au_auditoria
AFTER UPDATE ON cidade
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cidade', NEW.id_cidade, 'UPDATE', JSON_OBJECT('id_cidade', OLD.id_cidade, 'nome', OLD.nome), JSON_OBJECT('id_cidade', NEW.id_cidade, 'nome', NEW.nome), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cidade_ad_auditoria$$
CREATE TRIGGER trg_cidade_ad_auditoria
AFTER DELETE ON cidade
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cidade', OLD.id_cidade, 'DELETE', JSON_OBJECT('id_cidade', OLD.id_cidade, 'nome', OLD.nome), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_bairro_ai_auditoria$$
CREATE TRIGGER trg_bairro_ai_auditoria
AFTER INSERT ON bairro
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('bairro', NEW.id_bairro, 'INSERT', NULL, JSON_OBJECT('id_bairro', NEW.id_bairro, 'nome', NEW.nome, 'id_cidade', NEW.id_cidade), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_bairro_au_auditoria$$
CREATE TRIGGER trg_bairro_au_auditoria
AFTER UPDATE ON bairro
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('bairro', NEW.id_bairro, 'UPDATE', JSON_OBJECT('id_bairro', OLD.id_bairro, 'nome', OLD.nome, 'id_cidade', OLD.id_cidade), JSON_OBJECT('id_bairro', NEW.id_bairro, 'nome', NEW.nome, 'id_cidade', NEW.id_cidade), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_bairro_ad_auditoria$$
CREATE TRIGGER trg_bairro_ad_auditoria
AFTER DELETE ON bairro
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('bairro', OLD.id_bairro, 'DELETE', JSON_OBJECT('id_bairro', OLD.id_bairro, 'nome', OLD.nome, 'id_cidade', OLD.id_cidade), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_endereco_ai_auditoria$$
CREATE TRIGGER trg_endereco_ai_auditoria
AFTER INSERT ON endereco
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('endereco', NEW.id_endereco, 'INSERT', NULL, JSON_OBJECT('id_endereco', NEW.id_endereco, 'quarteirao', NEW.quarteirao, 'rua', NEW.rua, 'numero_casa', NEW.numero_casa, 'id_bairro', NEW.id_bairro, 'id_pessoa', NEW.id_pessoa), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_endereco_au_auditoria$$
CREATE TRIGGER trg_endereco_au_auditoria
AFTER UPDATE ON endereco
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('endereco', NEW.id_endereco, 'UPDATE', JSON_OBJECT('id_endereco', OLD.id_endereco, 'quarteirao', OLD.quarteirao, 'rua', OLD.rua, 'numero_casa', OLD.numero_casa, 'id_bairro', OLD.id_bairro, 'id_pessoa', OLD.id_pessoa), JSON_OBJECT('id_endereco', NEW.id_endereco, 'quarteirao', NEW.quarteirao, 'rua', NEW.rua, 'numero_casa', NEW.numero_casa, 'id_bairro', NEW.id_bairro, 'id_pessoa', NEW.id_pessoa), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_endereco_ad_auditoria$$
CREATE TRIGGER trg_endereco_ad_auditoria
AFTER DELETE ON endereco
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('endereco', OLD.id_endereco, 'DELETE', JSON_OBJECT('id_endereco', OLD.id_endereco, 'quarteirao', OLD.quarteirao, 'rua', OLD.rua, 'numero_casa', OLD.numero_casa, 'id_bairro', OLD.id_bairro, 'id_pessoa', OLD.id_pessoa), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cargo_ai_auditoria$$
CREATE TRIGGER trg_cargo_ai_auditoria
AFTER INSERT ON cargo
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cargo', NEW.id_cargo, 'INSERT', NULL, JSON_OBJECT('id_cargo', NEW.id_cargo, 'nome', NEW.nome, 'salario_base', NEW.salario_base), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cargo_au_auditoria$$
CREATE TRIGGER trg_cargo_au_auditoria
AFTER UPDATE ON cargo
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cargo', NEW.id_cargo, 'UPDATE', JSON_OBJECT('id_cargo', OLD.id_cargo, 'nome', OLD.nome, 'salario_base', OLD.salario_base), JSON_OBJECT('id_cargo', NEW.id_cargo, 'nome', NEW.nome, 'salario_base', NEW.salario_base), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cargo_ad_auditoria$$
CREATE TRIGGER trg_cargo_ad_auditoria
AFTER DELETE ON cargo
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cargo', OLD.id_cargo, 'DELETE', JSON_OBJECT('id_cargo', OLD.id_cargo, 'nome', OLD.nome, 'salario_base', OLD.salario_base), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cliente_ai_auditoria$$
CREATE TRIGGER trg_cliente_ai_auditoria
AFTER INSERT ON cliente
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cliente', NEW.id_cliente, 'INSERT', NULL, JSON_OBJECT('id_cliente', NEW.id_cliente, 'id_pessoa', NEW.id_pessoa), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cliente_au_auditoria$$
CREATE TRIGGER trg_cliente_au_auditoria
AFTER UPDATE ON cliente
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cliente', NEW.id_cliente, 'UPDATE', JSON_OBJECT('id_cliente', OLD.id_cliente, 'id_pessoa', OLD.id_pessoa), JSON_OBJECT('id_cliente', NEW.id_cliente, 'id_pessoa', NEW.id_pessoa), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_cliente_ad_auditoria$$
CREATE TRIGGER trg_cliente_ad_auditoria
AFTER DELETE ON cliente
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('cliente', OLD.id_cliente, 'DELETE', JSON_OBJECT('id_cliente', OLD.id_cliente, 'id_pessoa', OLD.id_pessoa), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_funcionario_ai_auditoria$$
CREATE TRIGGER trg_funcionario_ai_auditoria
AFTER INSERT ON funcionario
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('funcionario', NEW.id_funcionario, 'INSERT', NULL, JSON_OBJECT('id_funcionario', NEW.id_funcionario, 'id_pessoa', NEW.id_pessoa, 'bi', NEW.bi, 'data_nascimento', NEW.data_nascimento, 'data_inicio', NEW.data_inicio, 'estado', NEW.estado, 'id_cargo', NEW.id_cargo), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_funcionario_au_auditoria$$
CREATE TRIGGER trg_funcionario_au_auditoria
AFTER UPDATE ON funcionario
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('funcionario', NEW.id_funcionario, 'UPDATE', JSON_OBJECT('id_funcionario', OLD.id_funcionario, 'id_pessoa', OLD.id_pessoa, 'bi', OLD.bi, 'data_nascimento', OLD.data_nascimento, 'data_inicio', OLD.data_inicio, 'estado', OLD.estado, 'id_cargo', OLD.id_cargo), JSON_OBJECT('id_funcionario', NEW.id_funcionario, 'id_pessoa', NEW.id_pessoa, 'bi', NEW.bi, 'data_nascimento', NEW.data_nascimento, 'data_inicio', NEW.data_inicio, 'estado', NEW.estado, 'id_cargo', NEW.id_cargo), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_funcionario_ad_auditoria$$
CREATE TRIGGER trg_funcionario_ad_auditoria
AFTER DELETE ON funcionario
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('funcionario', OLD.id_funcionario, 'DELETE', JSON_OBJECT('id_funcionario', OLD.id_funcionario, 'id_pessoa', OLD.id_pessoa, 'bi', OLD.bi, 'data_nascimento', OLD.data_nascimento, 'data_inicio', OLD.data_inicio, 'estado', OLD.estado, 'id_cargo', OLD.id_cargo), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_utilizador_ai_auditoria$$
CREATE TRIGGER trg_utilizador_ai_auditoria
AFTER INSERT ON utilizador
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
<<<<<<< HEAD
    VALUES ('utilizador', NEW.id_utilizador, 'INSERT', NULL, JSON_OBJECT('id_utilizador', NEW.id_utilizador, 'username', NEW.username, 'perfil', NEW.perfil, 'troca_senha_obrigatoria', NEW.troca_senha_obrigatoria, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
=======
    VALUES ('utilizador', NEW.id_utilizador, 'INSERT', NULL, JSON_OBJECT('id_utilizador', NEW.id_utilizador, 'username', NEW.username, 'perfil', NEW.perfil, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
END$$

DROP TRIGGER IF EXISTS trg_utilizador_au_auditoria$$
CREATE TRIGGER trg_utilizador_au_auditoria
AFTER UPDATE ON utilizador
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
<<<<<<< HEAD
    VALUES ('utilizador', NEW.id_utilizador, 'UPDATE', JSON_OBJECT('id_utilizador', OLD.id_utilizador, 'username', OLD.username, 'perfil', OLD.perfil, 'troca_senha_obrigatoria', OLD.troca_senha_obrigatoria, 'id_funcionario', OLD.id_funcionario), JSON_OBJECT('id_utilizador', NEW.id_utilizador, 'username', NEW.username, 'perfil', NEW.perfil, 'troca_senha_obrigatoria', NEW.troca_senha_obrigatoria, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
=======
    VALUES ('utilizador', NEW.id_utilizador, 'UPDATE', JSON_OBJECT('id_utilizador', OLD.id_utilizador, 'username', OLD.username, 'perfil', OLD.perfil, 'id_funcionario', OLD.id_funcionario), JSON_OBJECT('id_utilizador', NEW.id_utilizador, 'username', NEW.username, 'perfil', NEW.perfil, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
END$$

DROP TRIGGER IF EXISTS trg_utilizador_ad_auditoria$$
CREATE TRIGGER trg_utilizador_ad_auditoria
AFTER DELETE ON utilizador
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
<<<<<<< HEAD
    VALUES ('utilizador', OLD.id_utilizador, 'DELETE', JSON_OBJECT('id_utilizador', OLD.id_utilizador, 'username', OLD.username, 'perfil', OLD.perfil, 'troca_senha_obrigatoria', OLD.troca_senha_obrigatoria, 'id_funcionario', OLD.id_funcionario), NULL, @id_utilizador_atual);
=======
    VALUES ('utilizador', OLD.id_utilizador, 'DELETE', JSON_OBJECT('id_utilizador', OLD.id_utilizador, 'username', OLD.username, 'perfil', OLD.perfil, 'id_funcionario', OLD.id_funcionario), NULL, @id_utilizador_atual);
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
END$$

DROP TRIGGER IF EXISTS trg_fornecedor_ai_auditoria$$
CREATE TRIGGER trg_fornecedor_ai_auditoria
AFTER INSERT ON fornecedor
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('fornecedor', NEW.id_fornecedor, 'INSERT', NULL, JSON_OBJECT('id_fornecedor', NEW.id_fornecedor, 'id_pessoa', NEW.id_pessoa), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_fornecedor_au_auditoria$$
CREATE TRIGGER trg_fornecedor_au_auditoria
AFTER UPDATE ON fornecedor
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('fornecedor', NEW.id_fornecedor, 'UPDATE', JSON_OBJECT('id_fornecedor', OLD.id_fornecedor, 'id_pessoa', OLD.id_pessoa), JSON_OBJECT('id_fornecedor', NEW.id_fornecedor, 'id_pessoa', NEW.id_pessoa), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_fornecedor_ad_auditoria$$
CREATE TRIGGER trg_fornecedor_ad_auditoria
AFTER DELETE ON fornecedor
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('fornecedor', OLD.id_fornecedor, 'DELETE', JSON_OBJECT('id_fornecedor', OLD.id_fornecedor, 'id_pessoa', OLD.id_pessoa), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_categoria_ai_auditoria$$
CREATE TRIGGER trg_categoria_ai_auditoria
AFTER INSERT ON categoria
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('categoria', NEW.id_categoria, 'INSERT', NULL, JSON_OBJECT('id_categoria', NEW.id_categoria, 'nome', NEW.nome), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_categoria_au_auditoria$$
CREATE TRIGGER trg_categoria_au_auditoria
AFTER UPDATE ON categoria
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('categoria', NEW.id_categoria, 'UPDATE', JSON_OBJECT('id_categoria', OLD.id_categoria, 'nome', OLD.nome), JSON_OBJECT('id_categoria', NEW.id_categoria, 'nome', NEW.nome), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_categoria_ad_auditoria$$
CREATE TRIGGER trg_categoria_ad_auditoria
AFTER DELETE ON categoria
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('categoria', OLD.id_categoria, 'DELETE', JSON_OBJECT('id_categoria', OLD.id_categoria, 'nome', OLD.nome), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_produto_ai_auditoria$$
CREATE TRIGGER trg_produto_ai_auditoria
AFTER INSERT ON produto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
<<<<<<< HEAD
    VALUES ('produto', NEW.id_produto, 'INSERT', NULL, JSON_OBJECT('id_produto', NEW.id_produto, 'codigo_barras', NEW.codigo_barras, 'nome', NEW.nome, 'preco', NEW.preco, 'descricao', NEW.descricao, 'unidade', NEW.unidade, 'estado', NEW.estado, 'quantidade_minima', NEW.quantidade_minima, 'id_categoria', NEW.id_categoria), @id_utilizador_atual);
=======
    VALUES ('produto', NEW.id_produto, 'INSERT', NULL, JSON_OBJECT('id_produto', NEW.id_produto, 'nome', NEW.nome, 'preco', NEW.preco, 'descricao', NEW.descricao, 'unidade', NEW.unidade, 'estado', NEW.estado, 'quantidade_minima', NEW.quantidade_minima, 'id_categoria', NEW.id_categoria), @id_utilizador_atual);
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
END$$

DROP TRIGGER IF EXISTS trg_produto_au_auditoria$$
CREATE TRIGGER trg_produto_au_auditoria
AFTER UPDATE ON produto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
<<<<<<< HEAD
    VALUES ('produto', NEW.id_produto, 'UPDATE', JSON_OBJECT('id_produto', OLD.id_produto, 'codigo_barras', OLD.codigo_barras, 'nome', OLD.nome, 'preco', OLD.preco, 'descricao', OLD.descricao, 'unidade', OLD.unidade, 'estado', OLD.estado, 'quantidade_minima', OLD.quantidade_minima, 'id_categoria', OLD.id_categoria), JSON_OBJECT('id_produto', NEW.id_produto, 'codigo_barras', NEW.codigo_barras, 'nome', NEW.nome, 'preco', NEW.preco, 'descricao', NEW.descricao, 'unidade', NEW.unidade, 'estado', NEW.estado, 'quantidade_minima', NEW.quantidade_minima, 'id_categoria', NEW.id_categoria), @id_utilizador_atual);
=======
    VALUES ('produto', NEW.id_produto, 'UPDATE', JSON_OBJECT('id_produto', OLD.id_produto, 'nome', OLD.nome, 'preco', OLD.preco, 'descricao', OLD.descricao, 'unidade', OLD.unidade, 'estado', OLD.estado, 'quantidade_minima', OLD.quantidade_minima, 'id_categoria', OLD.id_categoria), JSON_OBJECT('id_produto', NEW.id_produto, 'nome', NEW.nome, 'preco', NEW.preco, 'descricao', NEW.descricao, 'unidade', NEW.unidade, 'estado', NEW.estado, 'quantidade_minima', NEW.quantidade_minima, 'id_categoria', NEW.id_categoria), @id_utilizador_atual);
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
END$$

DROP TRIGGER IF EXISTS trg_produto_ad_auditoria$$
CREATE TRIGGER trg_produto_ad_auditoria
AFTER DELETE ON produto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
<<<<<<< HEAD
    VALUES ('produto', OLD.id_produto, 'DELETE', JSON_OBJECT('id_produto', OLD.id_produto, 'codigo_barras', OLD.codigo_barras, 'nome', OLD.nome, 'preco', OLD.preco, 'descricao', OLD.descricao, 'unidade', OLD.unidade, 'estado', OLD.estado, 'quantidade_minima', OLD.quantidade_minima, 'id_categoria', OLD.id_categoria), NULL, @id_utilizador_atual);
=======
    VALUES ('produto', OLD.id_produto, 'DELETE', JSON_OBJECT('id_produto', OLD.id_produto, 'nome', OLD.nome, 'preco', OLD.preco, 'descricao', OLD.descricao, 'unidade', OLD.unidade, 'estado', OLD.estado, 'quantidade_minima', OLD.quantidade_minima, 'id_categoria', OLD.id_categoria), NULL, @id_utilizador_atual);
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
END$$

DROP TRIGGER IF EXISTS trg_estoque_ai_auditoria$$
CREATE TRIGGER trg_estoque_ai_auditoria
AFTER INSERT ON estoque
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('estoque', NEW.id_estoque, 'INSERT', NULL, JSON_OBJECT('id_estoque', NEW.id_estoque, 'quantidade', NEW.quantidade, 'data_entrada', NEW.data_entrada, 'data_validade', NEW.data_validade, 'id_produto', NEW.id_produto, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_estoque_au_auditoria$$
CREATE TRIGGER trg_estoque_au_auditoria
AFTER UPDATE ON estoque
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('estoque', NEW.id_estoque, 'UPDATE', JSON_OBJECT('id_estoque', OLD.id_estoque, 'quantidade', OLD.quantidade, 'data_entrada', OLD.data_entrada, 'data_validade', OLD.data_validade, 'id_produto', OLD.id_produto, 'id_funcionario', OLD.id_funcionario), JSON_OBJECT('id_estoque', NEW.id_estoque, 'quantidade', NEW.quantidade, 'data_entrada', NEW.data_entrada, 'data_validade', NEW.data_validade, 'id_produto', NEW.id_produto, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_estoque_ad_auditoria$$
CREATE TRIGGER trg_estoque_ad_auditoria
AFTER DELETE ON estoque
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('estoque', OLD.id_estoque, 'DELETE', JSON_OBJECT('id_estoque', OLD.id_estoque, 'quantidade', OLD.quantidade, 'data_entrada', OLD.data_entrada, 'data_validade', OLD.data_validade, 'id_produto', OLD.id_produto, 'id_funcionario', OLD.id_funcionario), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_fornecedor_produto_ai_auditoria$$
CREATE TRIGGER trg_fornecedor_produto_ai_auditoria
AFTER INSERT ON fornecedor_produto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('fornecedor_produto', NEW.id_fornecedor_produto, 'INSERT', NULL, JSON_OBJECT('id_fornecedor_produto', NEW.id_fornecedor_produto, 'id_produto', NEW.id_produto, 'id_fornecedor', NEW.id_fornecedor), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_fornecedor_produto_au_auditoria$$
CREATE TRIGGER trg_fornecedor_produto_au_auditoria
AFTER UPDATE ON fornecedor_produto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('fornecedor_produto', NEW.id_fornecedor_produto, 'UPDATE', JSON_OBJECT('id_fornecedor_produto', OLD.id_fornecedor_produto, 'id_produto', OLD.id_produto, 'id_fornecedor', OLD.id_fornecedor), JSON_OBJECT('id_fornecedor_produto', NEW.id_fornecedor_produto, 'id_produto', NEW.id_produto, 'id_fornecedor', NEW.id_fornecedor), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_fornecedor_produto_ad_auditoria$$
CREATE TRIGGER trg_fornecedor_produto_ad_auditoria
AFTER DELETE ON fornecedor_produto
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('fornecedor_produto', OLD.id_fornecedor_produto, 'DELETE', JSON_OBJECT('id_fornecedor_produto', OLD.id_fornecedor_produto, 'id_produto', OLD.id_produto, 'id_fornecedor', OLD.id_fornecedor), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_venda_ai_auditoria$$
CREATE TRIGGER trg_venda_ai_auditoria
AFTER INSERT ON venda
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('venda', NEW.id_venda, 'INSERT', NULL, JSON_OBJECT('id_venda', NEW.id_venda, 'data', NEW.data, 'total', NEW.total, 'estado', NEW.estado, 'id_cliente', NEW.id_cliente, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_venda_au_auditoria$$
CREATE TRIGGER trg_venda_au_auditoria
AFTER UPDATE ON venda
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('venda', NEW.id_venda, 'UPDATE', JSON_OBJECT('id_venda', OLD.id_venda, 'data', OLD.data, 'total', OLD.total, 'estado', OLD.estado, 'id_cliente', OLD.id_cliente, 'id_funcionario', OLD.id_funcionario), JSON_OBJECT('id_venda', NEW.id_venda, 'data', NEW.data, 'total', NEW.total, 'estado', NEW.estado, 'id_cliente', NEW.id_cliente, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_venda_ad_auditoria$$
CREATE TRIGGER trg_venda_ad_auditoria
AFTER DELETE ON venda
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('venda', OLD.id_venda, 'DELETE', JSON_OBJECT('id_venda', OLD.id_venda, 'data', OLD.data, 'total', OLD.total, 'estado', OLD.estado, 'id_cliente', OLD.id_cliente, 'id_funcionario', OLD.id_funcionario), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_item_vendido_ai_auditoria$$
CREATE TRIGGER trg_item_vendido_ai_auditoria
AFTER INSERT ON item_vendido
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('item_vendido', NEW.id_item, 'INSERT', NULL, JSON_OBJECT('id_item', NEW.id_item, 'quantidade', NEW.quantidade, 'preco', NEW.preco, 'desconto', NEW.desconto, 'id_venda', NEW.id_venda, 'id_produto', NEW.id_produto), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_item_vendido_au_auditoria$$
CREATE TRIGGER trg_item_vendido_au_auditoria
AFTER UPDATE ON item_vendido
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('item_vendido', NEW.id_item, 'UPDATE', JSON_OBJECT('id_item', OLD.id_item, 'quantidade', OLD.quantidade, 'preco', OLD.preco, 'desconto', OLD.desconto, 'id_venda', OLD.id_venda, 'id_produto', OLD.id_produto), JSON_OBJECT('id_item', NEW.id_item, 'quantidade', NEW.quantidade, 'preco', NEW.preco, 'desconto', NEW.desconto, 'id_venda', NEW.id_venda, 'id_produto', NEW.id_produto), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_item_vendido_ad_auditoria$$
CREATE TRIGGER trg_item_vendido_ad_auditoria
AFTER DELETE ON item_vendido
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('item_vendido', OLD.id_item, 'DELETE', JSON_OBJECT('id_item', OLD.id_item, 'quantidade', OLD.quantidade, 'preco', OLD.preco, 'desconto', OLD.desconto, 'id_venda', OLD.id_venda, 'id_produto', OLD.id_produto), NULL, @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_reembolso_ai_auditoria$$
CREATE TRIGGER trg_reembolso_ai_auditoria
AFTER INSERT ON reembolso
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('reembolso', NEW.id_reembolso, 'INSERT', NULL, JSON_OBJECT('id_reembolso', NEW.id_reembolso, 'quantidade', NEW.quantidade, 'valor', NEW.valor, 'motivo', NEW.motivo, 'data', NEW.data, 'id_item', NEW.id_item, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_reembolso_au_auditoria$$
CREATE TRIGGER trg_reembolso_au_auditoria
AFTER UPDATE ON reembolso
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('reembolso', NEW.id_reembolso, 'UPDATE', JSON_OBJECT('id_reembolso', OLD.id_reembolso, 'quantidade', OLD.quantidade, 'valor', OLD.valor, 'motivo', OLD.motivo, 'data', OLD.data, 'id_item', OLD.id_item, 'id_funcionario', OLD.id_funcionario), JSON_OBJECT('id_reembolso', NEW.id_reembolso, 'quantidade', NEW.quantidade, 'valor', NEW.valor, 'motivo', NEW.motivo, 'data', NEW.data, 'id_item', NEW.id_item, 'id_funcionario', NEW.id_funcionario), @id_utilizador_atual);
END$$

DROP TRIGGER IF EXISTS trg_reembolso_ad_auditoria$$
CREATE TRIGGER trg_reembolso_ad_auditoria
AFTER DELETE ON reembolso
FOR EACH ROW
BEGIN
    INSERT INTO auditoria (tabela_afetada, id_registro, operacao, dados_antes, dados_depois, id_utilizador)
    VALUES ('reembolso', OLD.id_reembolso, 'DELETE', JSON_OBJECT('id_reembolso', OLD.id_reembolso, 'quantidade', OLD.quantidade, 'valor', OLD.valor, 'motivo', OLD.motivo, 'data', OLD.data, 'id_item', OLD.id_item, 'id_funcionario', OLD.id_funcionario), NULL, @id_utilizador_atual);
END$$

DELIMITER ;
