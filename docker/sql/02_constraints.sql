USE gestao;

ALTER TABLE estoque
    ADD CONSTRAINT ck_estoque_datas
    CHECK (data_validade IS NULL OR data_validade >= DATE(data_entrada));
<<<<<<< HEAD


ALTER TABLE produto 
ADD COLUMN codigo_barras VARCHAR(32) UNIQUE NULL AFTER id_produto;
=======
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
