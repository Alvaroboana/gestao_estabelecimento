USE gestao;

CREATE OR REPLACE VIEW vw_auditoria_detalhada AS
SELECT
    a.id_auditoria,
    a.tabela_afetada,
    a.id_registro,
    a.operacao,
    a.dados_antes,
    a.dados_depois,
    a.id_utilizador,
    u.username,
    a.data_operacao
FROM auditoria AS a
LEFT JOIN utilizador AS u ON u.id_utilizador = a.id_utilizador;

CREATE OR REPLACE VIEW vw_auditoria_recente AS
SELECT
    id_auditoria,
    tabela_afetada,
    id_registro,
    operacao,
    dados_antes,
    dados_depois,
    id_utilizador,
    username,
    data_operacao
FROM vw_auditoria_detalhada
ORDER BY data_operacao DESC, id_auditoria DESC;

CREATE OR REPLACE VIEW vw_auditoria_produtos AS
SELECT * FROM vw_auditoria_detalhada
WHERE tabela_afetada IN ('produto', 'categoria', 'fornecedor_produto');

CREATE OR REPLACE VIEW vw_auditoria_estoque AS
SELECT * FROM vw_auditoria_detalhada
WHERE tabela_afetada = 'estoque';

CREATE OR REPLACE VIEW vw_auditoria_vendas AS
SELECT * FROM vw_auditoria_detalhada
WHERE tabela_afetada IN ('venda', 'item_vendido', 'reembolso');

CREATE OR REPLACE VIEW vw_resumo_auditoria AS
SELECT
    DATE(data_operacao) AS dia,
    tabela_afetada,
    operacao,
    COUNT(*) AS quantidade
FROM auditoria
GROUP BY DATE(data_operacao), tabela_afetada, operacao;

CREATE OR REPLACE VIEW vw_vendas_detalhadas AS
SELECT
    v.id_venda,
    v.data AS data_venda,
    v.total,
    v.estado,
    c.id_cliente,
    pc.nome AS nome_cliente,
    f.id_funcionario,
    pf.nome AS nome_funcionario
FROM venda AS v
INNER JOIN cliente AS c ON c.id_cliente = v.id_cliente
INNER JOIN pessoa AS pc ON pc.id_pessoa = c.id_pessoa
INNER JOIN funcionario AS f ON f.id_funcionario = v.id_funcionario
INNER JOIN pessoa AS pf ON pf.id_pessoa = f.id_pessoa;

CREATE OR REPLACE VIEW vw_itens_vendidos_detalhados AS
SELECT
    iv.id_item,
    iv.id_venda,
    v.data AS data_venda,
    v.estado AS estado_venda,
    iv.id_produto,
    p.nome AS nome_produto,
    c.id_categoria,
    c.nome AS nome_categoria,
    iv.quantidade,
    iv.preco,
    iv.desconto,
    ((iv.quantidade * iv.preco) - iv.desconto) AS subtotal
FROM item_vendido AS iv
INNER JOIN venda AS v ON v.id_venda = iv.id_venda
INNER JOIN produto AS p ON p.id_produto = iv.id_produto
INNER JOIN categoria AS c ON c.id_categoria = p.id_categoria;

CREATE OR REPLACE VIEW vw_resumo_vendas_diario AS
SELECT
    DATE(v.data) AS dia,
    COUNT(DISTINCT v.id_venda) AS quantidade_vendas,
    COALESCE(SUM(v.total), 0.00) AS total_vendido,
    COALESCE(SUM(iv.quantidade), 0) AS quantidade_itens
FROM venda AS v
LEFT JOIN item_vendido AS iv ON iv.id_venda = v.id_venda
WHERE v.estado = 'CONCLUIDA'
GROUP BY DATE(v.data);

CREATE OR REPLACE VIEW vw_vendas_por_funcionario AS
SELECT
    f.id_funcionario,
    pf.nome AS nome_funcionario,
    COUNT(v.id_venda) AS quantidade_vendas,
    COALESCE(SUM(v.total), 0.00) AS total_vendido
FROM funcionario AS f
INNER JOIN pessoa AS pf ON pf.id_pessoa = f.id_pessoa
LEFT JOIN venda AS v
    ON v.id_funcionario = f.id_funcionario
   AND v.estado = 'CONCLUIDA'
GROUP BY f.id_funcionario, pf.nome;

CREATE OR REPLACE VIEW vw_produtos_mais_vendidos AS
SELECT
    p.id_produto,
    p.nome AS nome_produto,
    c.id_categoria,
    c.nome AS nome_categoria,
    COALESCE(SUM(CASE WHEN v.estado = 'CONCLUIDA' THEN iv.quantidade ELSE 0 END), 0) AS quantidade_vendida,
    COALESCE(SUM(CASE WHEN v.estado = 'CONCLUIDA' THEN (iv.quantidade * iv.preco) - iv.desconto ELSE 0 END), 0.00) AS valor_vendido
FROM produto AS p
INNER JOIN categoria AS c ON c.id_categoria = p.id_categoria
LEFT JOIN item_vendido AS iv ON iv.id_produto = p.id_produto
LEFT JOIN venda AS v ON v.id_venda = iv.id_venda
GROUP BY p.id_produto, p.nome, c.id_categoria, c.nome;

CREATE OR REPLACE VIEW vw_estoque_atual AS
SELECT
    p.id_produto,
    p.nome AS nome_produto,
    p.quantidade_minima,
    COALESCE(SUM(CASE
        WHEN e.data_validade IS NULL OR e.data_validade >= CURRENT_DATE THEN e.quantidade
        ELSE 0
    END), 0) AS quantidade_atual
FROM produto AS p
LEFT JOIN estoque AS e ON e.id_produto = p.id_produto
GROUP BY p.id_produto, p.nome, p.quantidade_minima;

CREATE OR REPLACE VIEW vw_estoque_baixo AS
SELECT
    id_produto,
    nome_produto,
    quantidade_minima,
    quantidade_atual
FROM vw_estoque_atual
WHERE quantidade_atual < quantidade_minima;

CREATE OR REPLACE VIEW vw_lotes_proximos_validade AS
SELECT
    e.id_estoque,
    e.id_produto,
    p.nome AS nome_produto,
    e.quantidade,
    e.data_entrada,
    e.data_validade,
    DATEDIFF(e.data_validade, CURRENT_DATE) AS dias_para_validade,
    e.id_funcionario
FROM estoque AS e
INNER JOIN produto AS p ON p.id_produto = e.id_produto
WHERE e.quantidade > 0
  AND e.data_validade IS NOT NULL
  AND e.data_validade >= CURRENT_DATE
  AND e.data_validade <= DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY);

CREATE OR REPLACE VIEW vw_reembolsos_detalhados AS
SELECT
    r.id_reembolso,
    r.quantidade,
    r.valor,
    r.motivo,
    r.data AS data_reembolso,
    r.id_item,
    iv.id_venda,
    iv.id_produto,
    p.nome AS nome_produto,
    r.id_funcionario,
    pf.nome AS nome_funcionario
FROM reembolso AS r
INNER JOIN item_vendido AS iv ON iv.id_item = r.id_item
INNER JOIN produto AS p ON p.id_produto = iv.id_produto
INNER JOIN funcionario AS f ON f.id_funcionario = r.id_funcionario
INNER JOIN pessoa AS pf ON pf.id_pessoa = f.id_pessoa;
