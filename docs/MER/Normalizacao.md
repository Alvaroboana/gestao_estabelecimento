# Sem Normalização

Cliente (id_cliente PK, nome, contacto)

Funcionario (id_funcionario PK, nome, contacto, endereco, data_nascimento, salario, data_inicio, cargo, estado, utilizador, senha, perfil)

Categoria (id_categoria PK, nome)

Produto (id_produto PK, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria FK)

Estoque (id_estoque PK, quantidade, data_entrada, data_validade)

Estoque_produto (id_estoque PK FK, id_produto PK FK)

Fornecedor (id_fornecedor PK, nome, contacto, endereco)

Fornecedor_produto (id_produto PK FK, id_fornecedor PK FK)

Venda (id_venda PK, data, total, estado, id_cliente FK, id_funcionario FK)

Item_vendido (id_item PK, quantidade, preco, desconto, id_venda FK, id_produto FK)

Reembolso (id_reembolso PK, quantidade, valor, motivo, data, id_item FK, id_funcionario FK)


# 1FN

Cliente (id_cliente PK, nome)

Contacto (id_contacto PK, contacto, id_cliente FK, id_fornecedor FK, id_funcionario FK)

Endereco (id_endereco PK, cidade, bairro, quarteirao, rua, numero_casa, id_fornecedor FK, id_funcionario FK)

Funcionario (id_funcionario PK, nome, data_nascimento, salario, data_inicio, cargo, estado, utilizador, senha, perfil)

Categoria (id_categoria PK, nome)

Produto (id_produto PK, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria FK)

Estoque (id_estoque PK, quantidade, data_entrada, data_validade)

Estoque_produto (id_estoque_produto PK, id_estoque FK, id_produto FK)

Fornecedor (id_fornecedor PK, nome)

Fornecedor_produto (id_fornecedor_produto PK, id_produto FK, id_fornecedor FK)

Venda (id_venda PK, data, total, estado, id_cliente FK, id_funcionario FK)

Item_vendido (id_item PK, quantidade, preco, desconto, id_venda FK, id_produto FK)

Reembolso (id_reembolso PK, quantidade, valor, motivo, data, id_item FK, id_funcionario FK)


# 2FN

Cliente (id_cliente PK, nome)

Contacto (id_contacto PK, contacto, id_cliente FK, id_fornecedor FK, id_funcionario FK)

Endereco (id_endereco PK, cidade, bairro, quarteirao, rua, numero_casa, id_fornecedor FK, id_funcionario FK)

Funcionario (id_funcionario PK, nome, data_nascimento, salario, data_inicio, cargo, estado, utilizador, senha, perfil)

Fornecedor (id_fornecedor PK, nome)

Categoria (id_categoria PK, nome)

Produto (id_produto PK, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria FK)

Estoque (id_estoque PK, quantidade, data_entrada, data_validade, id_produto FK)

Fornecedor_produto (id_fornecedor_produto PK, id_produto FK, id_fornecedor FK)

Venda (id_venda PK, data, total, estado, id_cliente FK, id_funcionario FK)

Item_vendido (id_item PK, quantidade, preco, desconto, id_venda FK, id_produto FK)

Reembolso (id_reembolso PK, quantidade, valor, motivo, data, id_item FK, id_funcionario FK)


# 3FN

Cliente (id_cliente PK, nome)

Contacto (id_contacto PK, contacto, id_cliente FK, id_fornecedor FK, id_funcionario FK)

Cidade (id_cidade PK, nome)

Bairro (id_bairro PK, nome, id_cidade FK)

Endereco (id_endereco PK, quarteirao, rua, numero_casa, id_bairro FK, id_fornecedor FK, id_funcionario FK)

Cargo (id_cargo PK, nome, salario_base)

Funcionario (id_funcionario PK, nome, data_nascimento, data_inicio, estado, id_cargo FK)

Utilizador (id_utilizador PK, username, password_hash, perfil, id_funcionario FK)

Fornecedor (id_fornecedor PK, nome)

Categoria (id_categoria PK, nome)

Produto (id_produto PK, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria FK)

Estoque (id_estoque PK, quantidade, data_entrada, data_validade, id_produto FK)

Fornecedor_produto (id_fornecedor_produto PK, id_produto FK, id_fornecedor FK)

Venda (id_venda PK, data, estado, id_cliente FK, id_funcionario FK)

Item_vendido (id_item PK, quantidade, preco, desconto, id_venda FK, id_produto FK)

Reembolso (id_reembolso PK, quantidade, valor, motivo, data, id_item FK, id_funcionario FK)





# ENUM

- Funcionario_estado: ATIVO, INATIVO, SUSPENSO
- Utilizador_perfil: ADMIN, GERENTE, VENDEDOR
- Produto_estado: ATIVO, DESCONTINUADO
- Venda_estado: PENDENTE, CONCLUIDA, ANULADA, REEMBOLSADA


