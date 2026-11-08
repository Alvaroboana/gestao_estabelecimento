USE gestao;

DELIMITER $$

DROP FUNCTION IF EXISTS fn_total_auditorias_periodo$$
CREATE FUNCTION fn_total_auditorias_periodo(
    p_inicio DATETIME,
    p_fim DATETIME
)
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN (
        SELECT COUNT(*)
        FROM auditoria
        WHERE data_operacao >= p_inicio
          AND data_operacao < p_fim
    );
END$$

DROP FUNCTION IF EXISTS fn_total_auditorias_tabela$$
CREATE FUNCTION fn_total_auditorias_tabela(p_tabela VARCHAR(64))
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN (
        SELECT COUNT(*)
        FROM auditoria
        WHERE tabela_afetada = p_tabela
    );
END$$

DROP FUNCTION IF EXISTS fn_ultima_operacao_registro$$
CREATE FUNCTION fn_ultima_operacao_registro(
    p_tabela VARCHAR(64),
    p_id_registro BIGINT
)
RETURNS DATETIME
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN (
        SELECT MAX(data_operacao)
        FROM auditoria
        WHERE tabela_afetada = p_tabela
          AND id_registro = p_id_registro
    );
END$$

DROP FUNCTION IF EXISTS fn_operacoes_utilizador$$
CREATE FUNCTION fn_operacoes_utilizador(
    p_id_utilizador BIGINT,
    p_inicio DATETIME,
    p_fim DATETIME
)
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN (
        SELECT COUNT(*)
        FROM auditoria
        WHERE id_utilizador = p_id_utilizador
          AND data_operacao >= p_inicio
          AND data_operacao < p_fim
    );
END$$

DROP FUNCTION IF EXISTS fn_total_venda$$
CREATE FUNCTION fn_total_venda(p_id_venda BIGINT)
RETURNS DECIMAL(10, 2)
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN (
        SELECT COALESCE(SUM((quantidade * preco) - desconto), 0.00)
        FROM item_vendido
        WHERE id_venda = p_id_venda
    );
END$$

DROP FUNCTION IF EXISTS fn_quantidade_estoque$$
CREATE FUNCTION fn_quantidade_estoque(p_id_produto BIGINT)
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN (
        SELECT COALESCE(SUM(quantidade), 0)
        FROM estoque
        WHERE id_produto = p_id_produto
          AND (data_validade IS NULL OR data_validade >= CURRENT_DATE)
    );
END$$

DROP FUNCTION IF EXISTS fn_total_reembolsado_item$$
CREATE FUNCTION fn_total_reembolsado_item(p_id_item BIGINT)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN (
        SELECT COALESCE(SUM(quantidade), 0)
        FROM reembolso
        WHERE id_item = p_id_item
    );
END$$

DROP FUNCTION IF EXISTS fn_produto_abaixo_minimo$$
CREATE FUNCTION fn_produto_abaixo_minimo(p_id_produto BIGINT)
RETURNS BOOLEAN
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN fn_quantidade_estoque(p_id_produto) < (
        SELECT quantidade_minima
        FROM produto
        WHERE id_produto = p_id_produto
    );
END$$

DROP FUNCTION IF EXISTS fn_dias_para_validade$$
CREATE FUNCTION fn_dias_para_validade(p_data_validade DATE)
RETURNS INT
DETERMINISTIC
BEGIN
    IF p_data_validade IS NULL THEN
        RETURN NULL;
    END IF;
    RETURN DATEDIFF(p_data_validade, CURRENT_DATE);
END$$

DROP FUNCTION IF EXISTS fn_total_vendas_funcionario$$
CREATE FUNCTION fn_total_vendas_funcionario(
    p_id_funcionario BIGINT,
    p_inicio DATETIME,
    p_fim DATETIME
)
RETURNS DECIMAL(12, 2)
DETERMINISTIC
READS SQL DATA
BEGIN
    RETURN (
        SELECT COALESCE(SUM(total), 0.00)
        FROM venda
        WHERE id_funcionario = p_id_funcionario
          AND estado = 'CONCLUIDA'
          AND data >= p_inicio
          AND data < p_fim
    );
END$$

DELIMITER ;
