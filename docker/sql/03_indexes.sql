USE gestao;

CREATE INDEX idx_venda_data_estado ON venda (data, estado);
CREATE INDEX idx_venda_funcionario_data ON venda (id_funcionario, data);
CREATE INDEX idx_venda_cliente_data ON venda (id_cliente, data);
CREATE INDEX idx_item_vendido_venda_produto ON item_vendido (id_venda, id_produto);
CREATE INDEX idx_estoque_produto_validade ON estoque (id_produto, data_validade);
CREATE INDEX idx_estoque_funcionario_entrada ON estoque (id_funcionario, data_entrada);
CREATE INDEX idx_reembolso_item_data ON reembolso (id_item, data);
CREATE INDEX idx_reembolso_funcionario_data ON reembolso (id_funcionario, data);
CREATE INDEX idx_auditoria_tabela_registro ON auditoria (tabela_afetada, id_registro);
CREATE INDEX idx_auditoria_data ON auditoria (data_operacao);
CREATE INDEX idx_auditoria_operacao_data ON auditoria (operacao, data_operacao);
CREATE INDEX idx_auditoria_utilizador_data ON auditoria (id_utilizador, data_operacao);
