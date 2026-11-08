USE gestao;

ALTER TABLE estoque
    ADD CONSTRAINT ck_estoque_datas
    CHECK (data_validade IS NULL OR data_validade >= DATE(data_entrada));


ALTER TABLE produto 
ADD COLUMN codigo_barras VARCHAR(32) UNIQUE NULL AFTER id_produto;