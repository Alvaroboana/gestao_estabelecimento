# Sem Normalização

Pessoa (id_pessoa PK, nome, contacto, endereco)
Cliente (id_cliente PK, id_pessoa FK)
Funcionario (id_funcionario PK, id_pessoa FK, bi, data_nascimento, data_inicio, cargo, salario_base, estado, utilizador, senha, perfil)
Fornecedor (id_fornecedor PK, id_pessoa FK)
Categoria (id_categoria PK, nome)
Produto (id_produto PK, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria FK)
Estoque (id_estoque PK, quantidade, data_entrada, data_validade, id_produto FK, id_funcionario FK)
Fornecedor_produto (id_fornecedor PK FK, id_produto PK FK)
Venda (id_venda PK, data, total, estado, id_cliente FK, id_funcionario FK)
Item_vendido (id_item PK, quantidade, preco, desconto, id_venda FK, id_produto FK)
Reembolso (id_reembolso PK, quantidade, valor, motivo, data, id_item FK, id_funcionario FK)

# 1FN

Pessoa (id_pessoa PK, nome)
Contacto (id_contacto PK, contacto, id_pessoa FK)
Endereco (id_endereco PK, cidade, bairro, quarteirao, rua, numero_casa, id_pessoa FK)
Cliente (id_cliente PK, id_pessoa FK)
Funcionario (id_funcionario PK, id_pessoa FK, bi, data_nascimento, data_inicio, cargo, salario_base, estado, utilizador, senha, perfil)
Fornecedor (id_fornecedor PK, id_pessoa FK)
Categoria (id_categoria PK, nome)
Produto (id_produto PK, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria FK)
Estoque (id_estoque PK, quantidade, data_entrada, data_validade, id_produto FK, id_funcionario FK)
Fornecedor_produto (id_fornecedor_produto PK, id_fornecedor FK, id_produto FK)
Venda (id_venda PK, data, total, estado, id_cliente FK, id_funcionario FK)
Item_vendido (id_item PK, quantidade, preco, desconto, id_venda FK, id_produto FK)
Reembolso (id_reembolso PK, quantidade, valor, motivo, data, id_item FK, id_funcionario FK)

# 2FN

Pessoa (id_pessoa PK, nome)
Contacto (id_contacto PK, contacto, id_pessoa FK)
Endereco (id_endereco PK, cidade, bairro, quarteirao, rua, numero_casa, id_pessoa FK)
Cliente (id_cliente PK, id_pessoa FK)
Funcionario (id_funcionario PK, id_pessoa FK, bi, data_nascimento, data_inicio, estado, id_cargo FK)
Cargo (id_cargo PK, nome, salario_base)
Utilizador (id_utilizador PK, username, password_hash, perfil, id_funcionario FK)
Fornecedor (id_fornecedor PK, id_pessoa FK)
Categoria (id_categoria PK, nome)
Produto (id_produto PK, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria FK)
Estoque (id_estoque PK, quantidade, data_entrada, data_validade, id_produto FK, id_funcionario FK)
Fornecedor_produto (id_fornecedor_produto PK, id_fornecedor FK, id_produto FK)
Venda (id_venda PK, data, total, estado, id_cliente FK, id_funcionario FK)
Item_vendido (id_item PK, quantidade, preco, desconto, id_venda FK, id_produto FK)
Reembolso (id_reembolso PK, quantidade, valor, motivo, data, id_item FK, id_funcionario FK)

# 3FN

Pessoa (id_pessoa PK, nome)
Contato (id_contato PK, contacto, id_pessoa FK)
Cidade (id_cidade PK, nome)
Bairro (id_bairro PK, nome, id_cidade FK)
Endereco (id_endereco PK, quarteirao, rua, numero_casa, id_bairro FK, id_pessoa FK)
Cliente (id_cliente PK, id_pessoa FK)
Funcionario (id_funcionario PK, id_pessoa FK, bi, data_nascimento, data_inicio, estado, id_cargo FK)
Cargo (id_cargo PK, nome, salario_base)
Utilizador (id_utilizador PK, username, password_hash, perfil, id_funcionario FK)
Fornecedor (id_fornecedor PK, id_pessoa FK)
Categoria (id_categoria PK, nome)
Produto (id_produto PK, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria FK)
Estoque (id_estoque PK, quantidade, data_entrada, data_validade, id_produto FK, id_funcionario FK)
Fornecedor_produto (id_fornecedor_produto PK, id_produto FK, id_fornecedor FK)
Venda (id_venda PK, data, total, estado, id_cliente FK, id_funcionario FK)
Item_vendido (id_item PK, quantidade, preco, desconto, id_venda FK, id_produto FK)
Reembolso (id_reembolso PK, quantidade, valor, motivo, data, id_item FK, id_funcionario FK)

# ENUM
Funcionario_estado: ATIVO, INATIVO, SUSPENSO
Utilizador_perfil: ADMIN, GERENTE, VENDEDOR
Produto_estado: ATIVO, DESCONTINUADO
Venda_estado: PENDENTE, CONCLUIDA, ANULADA, REEMBOLSADA