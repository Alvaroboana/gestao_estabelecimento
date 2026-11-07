USE gestao;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_listar_auditoria_por_tabela$$
CREATE PROCEDURE sp_listar_auditoria_por_tabela()
BEGIN
    DECLARE terminado BOOLEAN DEFAULT FALSE;
    DECLARE v_tabela VARCHAR(64);
    DECLARE cur CURSOR FOR
        SELECT DISTINCT tabela_afetada FROM auditoria ORDER BY tabela_afetada;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET terminado = TRUE;

    DROP TEMPORARY TABLE IF EXISTS tmp_resumo_auditoria;
    CREATE TEMPORARY TABLE tmp_resumo_auditoria (
        tabela_afetada VARCHAR(64) NOT NULL,
        quantidade_inserts BIGINT NOT NULL,
        quantidade_updates BIGINT NOT NULL,
        quantidade_deletes BIGINT NOT NULL
    );

    OPEN cur;
    ciclo: LOOP
        FETCH cur INTO v_tabela;
        IF terminado THEN
            LEAVE ciclo;
        END IF;

        INSERT INTO tmp_resumo_auditoria
        SELECT
            v_tabela,
            COALESCE(SUM(operacao = 'INSERT'), 0),
            COALESCE(SUM(operacao = 'UPDATE'), 0),
            COALESCE(SUM(operacao = 'DELETE'), 0)
        FROM auditoria
        WHERE tabela_afetada = v_tabela;
    END LOOP;
    CLOSE cur;

    SELECT * FROM tmp_resumo_auditoria ORDER BY tabela_afetada;
    DROP TEMPORARY TABLE tmp_resumo_auditoria;
END$$

DROP PROCEDURE IF EXISTS sp_gerar_resumo_auditoria_mensal$$
CREATE PROCEDURE sp_gerar_resumo_auditoria_mensal(
    IN p_inicio DATE,
    IN p_fim DATE
)
BEGIN
    DECLARE terminado BOOLEAN DEFAULT FALSE;
    DECLARE v_mes CHAR(7);
    DECLARE cur CURSOR FOR
        SELECT DISTINCT DATE_FORMAT(data_operacao, '%Y-%m')
        FROM auditoria
        WHERE data_operacao >= p_inicio
          AND data_operacao < DATE_ADD(p_fim, INTERVAL 1 DAY)
        ORDER BY 1;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET terminado = TRUE;

    DROP TEMPORARY TABLE IF EXISTS tmp_resumo_auditoria_mensal;
    CREATE TEMPORARY TABLE tmp_resumo_auditoria_mensal (
        mes CHAR(7) NOT NULL,
        quantidade BIGINT NOT NULL
    );

    OPEN cur;
    ciclo: LOOP
        FETCH cur INTO v_mes;
        IF terminado THEN
            LEAVE ciclo;
        END IF;

        INSERT INTO tmp_resumo_auditoria_mensal
        SELECT v_mes, COUNT(*)
        FROM auditoria
        WHERE DATE_FORMAT(data_operacao, '%Y-%m') = v_mes;
    END LOOP;
    CLOSE cur;

    SELECT mes, quantidade
    FROM tmp_resumo_auditoria_mensal
    ORDER BY mes;
    DROP TEMPORARY TABLE tmp_resumo_auditoria_mensal;
END$$

DELIMITER ;
