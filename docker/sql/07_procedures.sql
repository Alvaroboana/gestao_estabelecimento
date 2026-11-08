USE gestao;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_consultar_auditoria$$
CREATE PROCEDURE sp_consultar_auditoria(
    IN p_tabela VARCHAR(64),
    IN p_id_registro BIGINT,
    IN p_inicio DATETIME,
    IN p_fim DATETIME,
    IN p_operacao VARCHAR(10)
)
BEGIN
    SELECT
        a.id_auditoria,
        a.tabela_afetada,
        a.id_registro,
        a.operacao,
        a.dados_antes,
        a.dados_depois,
        a.id_utilizador,
        a.data_operacao,
        u.username
    FROM auditoria AS a
    LEFT JOIN utilizador AS u ON u.id_utilizador = a.id_utilizador
    WHERE (p_tabela IS NULL OR a.tabela_afetada = p_tabela)
      AND (p_id_registro IS NULL OR a.id_registro = p_id_registro)
      AND (p_inicio IS NULL OR a.data_operacao >= p_inicio)
      AND (p_fim IS NULL OR a.data_operacao < p_fim)
      AND (p_operacao IS NULL OR a.operacao = p_operacao)
    ORDER BY a.data_operacao DESC, a.id_auditoria DESC;
END$$

DROP PROCEDURE IF EXISTS sp_auditoria_periodo$$
CREATE PROCEDURE sp_auditoria_periodo(IN p_inicio DATETIME, IN p_fim DATETIME)
BEGIN
    CALL sp_consultar_auditoria(NULL, NULL, p_inicio, p_fim, NULL);
END$$

DROP PROCEDURE IF EXISTS sp_auditoria_utilizador$$
CREATE PROCEDURE sp_auditoria_utilizador(
    IN p_id_utilizador BIGINT,
    IN p_inicio DATETIME,
    IN p_fim DATETIME
)
BEGIN
    SELECT
        a.id_auditoria,
        a.tabela_afetada,
        a.id_registro,
        a.operacao,
        a.dados_antes,
        a.dados_depois,
        a.id_utilizador,
        a.data_operacao,
        u.username
    FROM auditoria AS a
    LEFT JOIN utilizador AS u ON u.id_utilizador = a.id_utilizador
    WHERE a.id_utilizador = p_id_utilizador
      AND (p_inicio IS NULL OR a.data_operacao >= p_inicio)
      AND (p_fim IS NULL OR a.data_operacao < p_fim)
    ORDER BY a.data_operacao DESC, a.id_auditoria DESC;
END$$

DROP PROCEDURE IF EXISTS sp_ultima_alteracao_registro$$
CREATE PROCEDURE sp_ultima_alteracao_registro(
    IN p_tabela VARCHAR(64),
    IN p_id_registro BIGINT
)
BEGIN
    SELECT
        a.id_auditoria,
        a.tabela_afetada,
        a.id_registro,
        a.operacao,
        a.dados_antes,
        a.dados_depois,
        a.id_utilizador,
        a.data_operacao,
        u.username
    FROM auditoria AS a
    LEFT JOIN utilizador AS u ON u.id_utilizador = a.id_utilizador
    WHERE a.tabela_afetada = p_tabela
      AND a.id_registro = p_id_registro
    ORDER BY a.data_operacao DESC, a.id_auditoria DESC
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS sp_resumo_auditoria$$
CREATE PROCEDURE sp_resumo_auditoria(
    IN p_inicio DATETIME,
    IN p_fim DATETIME
)
BEGIN
    SELECT
        tabela_afetada,
        operacao,
        COUNT(*) AS quantidade
    FROM auditoria
    WHERE (p_inicio IS NULL OR data_operacao >= p_inicio)
      AND (p_fim IS NULL OR data_operacao < p_fim)
    GROUP BY tabela_afetada, operacao
    ORDER BY tabela_afetada, operacao;
END$$

DROP PROCEDURE IF EXISTS sp_consultar_vendas_periodo$$
CREATE PROCEDURE sp_consultar_vendas_periodo(IN p_inicio DATETIME, IN p_fim DATETIME)
BEGIN
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
    INNER JOIN pessoa AS pf ON pf.id_pessoa = f.id_pessoa
    WHERE v.data >= p_inicio
      AND v.data < p_fim
    ORDER BY v.data DESC;
END$$

DROP PROCEDURE IF EXISTS sp_consultar_estoque_baixo$$
CREATE PROCEDURE sp_consultar_estoque_baixo()
BEGIN
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
    GROUP BY p.id_produto, p.nome, p.quantidade_minima
    HAVING quantidade_atual < p.quantidade_minima
    ORDER BY p.nome;
END$$

DROP PROCEDURE IF EXISTS sp_consultar_reembolsos_periodo$$
CREATE PROCEDURE sp_consultar_reembolsos_periodo(IN p_inicio DATETIME, IN p_fim DATETIME)
BEGIN
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
    INNER JOIN pessoa AS pf ON pf.id_pessoa = f.id_pessoa
    WHERE r.data >= p_inicio
      AND r.data < p_fim
    ORDER BY r.data DESC;
END$$

DELIMITER ;
