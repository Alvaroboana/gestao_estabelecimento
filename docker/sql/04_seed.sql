-- =====================================================================
-- DADOS INICIAIS (SEED) DO SISTEMA DE GESTÃO DE ESTABELECIMENTO
-- =====================================================================
USE gestao;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. CARGOS
INSERT INTO cargo (id_cargo, nome, salario_base) VALUES
(1, 'Administrador do Sistema', 75000.00),
(2, 'Gerente Geral', 55000.00),
(3, 'Vendedor / Caixa', 22000.00)
ON DUPLICATE KEY UPDATE nome = VALUES(nome), salario_base = VALUES(salario_base);

-- 2. CIDADES E BAIRROS
INSERT INTO cidade (id_cidade, nome) VALUES
(1, 'Maputo'),
(2, 'Matola'),
(3, 'Beira')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

INSERT INTO bairro (id_bairro, nome, id_cidade) VALUES
(1, 'Polana Cimento', 1),
(2, 'Alto Maé', 1),
(3, 'Central', 1),
(4, 'Sommerschield', 1),
(5, 'Matola Rio', 2),
(6, 'Fomento', 2),
(7, 'Ponta Gêa', 3)
ON DUPLICATE KEY UPDATE nome = VALUES(nome), id_cidade = VALUES(id_cidade);

-- 3. PESSOAS
INSERT INTO pessoa (id_pessoa, nome) VALUES
(1, 'Administrador Principal'),
(2, 'Sara Mendes'),
(3, 'Carlos Nhaca'),
(4, 'João Muthemba'),
(5, 'Ana Paula Cossa'),
(6, 'Sociedade Comercial Zambeze Lda'),
(7, 'Distribuidora Nacional Lda'),
(8, 'Lactícinios de Manhiça'),
(9, 'Bebidas do Sul SA')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

-- 4. CONTACTOS
INSERT INTO contacto (id_contacto, contacto, id_pessoa) VALUES
(1, '+258 84 100 0001', 1),
(2, '+258 84 200 0002', 2),
(3, '+258 84 300 0003', 3),
(4, '+258 82 400 0004', 4),
(5, '+258 87 500 0005', 5),
(6, '+258 21 300 0006', 6),
(7, '+258 21 400 0007', 7),
(8, '+258 21 500 0008', 8),
(9, '+258 21 600 0009', 9)
ON DUPLICATE KEY UPDATE contacto = VALUES(contacto);

-- 5. ENDEREÇOS
INSERT INTO endereco (id_endereco, quarteirao, rua, numero_casa, id_bairro, id_pessoa) VALUES
(1, 'Q. 12', 'Av. 24 de Julho', '1240', 1, 1),
(2, 'Q. 05', 'Av. Eduardo Mondlane', '850', 2, 2),
(3, 'Q. 18', 'Av. Vladimir Lenine', '320', 3, 3),
(4, 'Q. 02', 'Av. Julius Nyerere', '145', 1, 4),
(5, 'Q. 09', 'Av. Karl Marx', '210', 3, 5),
(6, 'Q. 22', 'Rua dos Desportistas', '400', 3, 6),
(7, 'Q. 30', 'Av. das Indústrias', '1020', 5, 7),
(8, 'Q. 04', 'Estrada Nacional nº 1', 'S/N', 6, 8),
(9, 'Q. 15', 'Av. Samora Machel', '512', 7, 9)
ON DUPLICATE KEY UPDATE quarteirao = VALUES(quarteirao), rua = VALUES(rua), numero_casa = VALUES(numero_casa);

-- 6. FUNCIONÁRIOS
INSERT INTO funcionario (id_funcionario, id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo) VALUES
(1, 1, '110100234567A', '1985-04-12', '2020-01-15', 'ATIVO', 1),
(2, 2, '110100345678B', '1990-08-22', '2021-03-01', 'ATIVO', 2),
(3, 3, '110100456789C', '1995-11-05', '2022-06-10', 'ATIVO', 3)
ON DUPLICATE KEY UPDATE bi = VALUES(bi), estado = VALUES(estado), id_cargo = VALUES(id_cargo);

-- 7. UTILIZADORES COM HASH BCRYPT
-- admin: Admin@123
-- sara: Gerente@123
-- carlos: Vendedor@123
INSERT INTO utilizador (id_utilizador, username, password_hash, perfil, id_funcionario) VALUES
(1, 'admin', '$2a$10$.5QKVdvnRy0/eA9jn/hUPuNlhEmkUXqqMliRr60EFtItlPLH5DCIy', 'ADMIN', 1),
(2, 'sara', '$2a$10$2LYiO2AZEDMhvqvb2qca/.Yos80Y0UPRezfQZqhn77MoprmAdS2rW', 'GERENTE', 2),
(3, 'carlos', '$2a$10$zNNjTIfe75SpcWffHe9Se.tpBVfitZRicdoUMABvlVCXfdhLLPKoi', 'VENDEDOR', 3)
ON DUPLICATE KEY UPDATE username = VALUES(username), password_hash = VALUES(password_hash), perfil = VALUES(perfil);

-- 8. CLIENTES
INSERT INTO cliente (id_cliente, id_pessoa) VALUES
(1, 4),
(2, 5),
(3, 6)
ON DUPLICATE KEY UPDATE id_pessoa = VALUES(id_pessoa);

-- 9. FORNECEDORES
INSERT INTO fornecedor (id_fornecedor, id_pessoa) VALUES
(1, 7),
(2, 8),
(3, 9)
ON DUPLICATE KEY UPDATE id_pessoa = VALUES(id_pessoa);

-- 10. CATEGORIAS
INSERT INTO categoria (id_categoria, nome) VALUES
(1, 'Bebidas'),
(2, 'Laticínios'),
(3, 'Mercearia'),
(4, 'Higiene e Limpeza'),
(5, 'Frescos e Carnes')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

-- 11. PRODUTOS
INSERT INTO produto (id_produto, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria) VALUES
(1, 'Água Mineral 500ml', 25.00, 'Água pura de nascente sem gás', 'UN', 'ATIVO', 20, 1),
(2, 'Refrigerante Cola 2L', 95.00, 'Refrigerante gaseificado sabor cola', 'UN', 'ATIVO', 15, 1),
(3, 'Leite UHT Meio Gordo 1L', 85.00, 'Leite longa vida pasteurizado', 'UN', 'ATIVO', 10, 2),
(4, 'Iogurte Natural 500g', 65.00, 'Iogurte cremoso tradicional', 'UN', 'ATIVO', 8, 2),
(5, 'Arroz Agulha 5kg', 320.00, 'Arroz branco tipo agulha selecionado', 'UN', 'ATIVO', 15, 3),
(6, 'Óleo Vegetal 1L', 110.00, 'Óleo de girassol 100% puro', 'UN', 'ATIVO', 12, 3),
(7, 'Detergente em Pó 1kg', 140.00, 'Detergente multiuso para roupas', 'UN', 'ATIVO', 5, 4),
(8, 'Sabonete Hidratante 90g', 35.00, 'Sabonete em barra com aloe vera', 'UN', 'ATIVO', 20, 4)
ON DUPLICATE KEY UPDATE nome = VALUES(nome), preco = VALUES(preco), estado = VALUES(estado), quantidade_minima = VALUES(quantidade_minima);

-- 12. FORNECEDOR_PRODUTO
INSERT INTO fornecedor_produto (id_fornecedor_produto, id_produto, id_fornecedor) VALUES
(1, 1, 3),
(2, 2, 3),
(3, 3, 2),
(4, 4, 2),
(5, 5, 1),
(6, 6, 1),
(7, 7, 1),
(8, 8, 1)
ON DUPLICATE KEY UPDATE id_produto = VALUES(id_produto), id_fornecedor = VALUES(id_fornecedor);

-- 13. ESTOQUE (Com lotes regulares, lote a expirar e estoque baixo)
INSERT INTO estoque (id_estoque, quantidade, data_entrada, data_validade, id_produto, id_funcionario) VALUES
(1, 120, NOW() - INTERVAL 10 DAY, CURDATE() + INTERVAL 180 DAY, 1, 1),
(2, 45, NOW() - INTERVAL 8 DAY, CURDATE() + INTERVAL 90 DAY, 2, 2),
(3, 14, NOW() - INTERVAL 15 DAY, CURDATE() + INTERVAL 4 DAY, 3, 3),  -- Alerta: a expirar em 4 dias
(4, 30, NOW() - INTERVAL 5 DAY, CURDATE() + INTERVAL 25 DAY, 4, 2),
(5, 6, NOW() - INTERVAL 20 DAY, CURDATE() + INTERVAL 300 DAY, 5, 1), -- Alerta: estoque baixo (6 <= 15)
(6, 60, NOW() - INTERVAL 7 DAY, CURDATE() + INTERVAL 365 DAY, 6, 3),
(7, 25, NOW() - INTERVAL 12 DAY, CURDATE() + INTERVAL 400 DAY, 7, 1),
(8, 80, NOW() - INTERVAL 3 DAY, CURDATE() + INTERVAL 500 DAY, 8, 2)
ON DUPLICATE KEY UPDATE quantidade = VALUES(quantidade), data_validade = VALUES(data_validade);

-- 14. VENDAS
INSERT INTO venda (id_venda, data, total, estado, id_cliente, id_funcionario) VALUES
(1, NOW() - INTERVAL 2 DAY, 170.00, 'CONCLUIDA', 1, 3),
(2, NOW() - INTERVAL 1 DAY, 415.00, 'CONCLUIDA', 2, 3),
(3, NOW() - INTERVAL 2 HOUR, 220.00, 'PENDENTE', 3, 2)
ON DUPLICATE KEY UPDATE total = VALUES(total), estado = VALUES(estado);

-- 15. ITENS VENDIDOS
INSERT INTO item_vendido (id_item, quantidade, preco, desconto, id_venda, id_produto) VALUES
(1, 2, 25.00, 0.00, 1, 1),
(2, 1, 120.00, 0.00, 1, 6),
(3, 1, 320.00, 0.00, 2, 5),
(4, 1, 95.00, 0.00, 2, 2),
(5, 2, 110.00, 0.00, 3, 6)
ON DUPLICATE KEY UPDATE quantidade = VALUES(quantidade), preco = VALUES(preco);

SET FOREIGN_KEY_CHECKS = 1;
