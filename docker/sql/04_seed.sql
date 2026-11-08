USE gestao;


INSERT INTO cargo (nome, salario_base)
SELECT 'Administrador', 35000.00
WHERE NOT EXISTS (SELECT 1 FROM cargo WHERE nome = 'Administrador');

INSERT INTO cargo (nome, salario_base)
SELECT 'Gerente', 25000.00
WHERE NOT EXISTS (SELECT 1 FROM cargo WHERE nome = 'Gerente');

INSERT INTO cargo (nome, salario_base)
SELECT 'Supervisor de Loja', 18500.00
WHERE NOT EXISTS (SELECT 1 FROM cargo WHERE nome = 'Supervisor de Loja');

INSERT INTO cargo (nome, salario_base)
SELECT 'Vendedor', 12000.00
WHERE NOT EXISTS (SELECT 1 FROM cargo WHERE nome = 'Vendedor');

INSERT INTO cargo (nome, salario_base)
SELECT 'Operador de Caixa', 11500.00
WHERE NOT EXISTS (SELECT 1 FROM cargo WHERE nome = 'Operador de Caixa');

INSERT INTO cargo (nome, salario_base)
SELECT 'Fiel de Armazém', 13500.00
WHERE NOT EXISTS (SELECT 1 FROM cargo WHERE nome = 'Fiel de Armazém');

INSERT INTO cargo (nome, salario_base)
SELECT 'Repositor de Stock', 9500.00
WHERE NOT EXISTS (SELECT 1 FROM cargo WHERE nome = 'Repositor de Stock');

INSERT INTO cidade (nome)
SELECT 'Maputo'
WHERE NOT EXISTS (SELECT 1 FROM cidade WHERE nome = 'Maputo');

INSERT INTO cidade (nome)
SELECT 'Matola'
WHERE NOT EXISTS (SELECT 1 FROM cidade WHERE nome = 'Matola');

INSERT INTO cidade (nome)
SELECT 'Boane'
WHERE NOT EXISTS (SELECT 1 FROM cidade WHERE nome = 'Boane');

INSERT INTO cidade (nome)
SELECT 'Marracuene'
WHERE NOT EXISTS (SELECT 1 FROM cidade WHERE nome = 'Marracuene');

INSERT INTO cidade (nome)
SELECT 'Xai-Xai'
WHERE NOT EXISTS (SELECT 1 FROM cidade WHERE nome = 'Xai-Xai');

INSERT INTO cidade (nome)
SELECT 'Beira'
WHERE NOT EXISTS (SELECT 1 FROM cidade WHERE nome = 'Beira');

INSERT INTO cidade (nome)
SELECT 'Nampula'
WHERE NOT EXISTS (SELECT 1 FROM cidade WHERE nome = 'Nampula');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Alto Maé', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Alto Maé');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Malhangalene', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Malhangalene');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Polana Cimento', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Polana Cimento');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Sommerschield', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Sommerschield');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Central', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Central');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Baixa', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Baixa');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Coop', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Coop');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Zimpeto', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Zimpeto');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Magoanine', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Magoanine');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Maxaquene', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Maxaquene');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Hulene', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Hulene');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Benfica', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Benfica');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Costa do Sol', c.id_cidade FROM cidade c WHERE c.nome = 'Maputo'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Costa do Sol');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Matola A', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Matola A');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Matola C', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Matola C');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Matola J', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Matola J');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Fomento', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Fomento');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Liberdade', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Liberdade');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Machava Sede', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Machava Sede');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Patrice Lumumba', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Patrice Lumumba');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Tsalala', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Tsalala');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Khongolote', c.id_cidade FROM cidade c WHERE c.nome = 'Matola'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Khongolote');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Boane Sede', c.id_cidade FROM cidade c WHERE c.nome = 'Boane'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Boane Sede');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Belo Horizonte', c.id_cidade FROM cidade c WHERE c.nome = 'Boane'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Belo Horizonte');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Marracuene Sede', c.id_cidade FROM cidade c WHERE c.nome = 'Marracuene'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Marracuene Sede');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Guava', c.id_cidade FROM cidade c WHERE c.nome = 'Marracuene'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Guava');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Praia de Xai-Xai', c.id_cidade FROM cidade c WHERE c.nome = 'Xai-Xai'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Praia de Xai-Xai');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Ponta Gêa', c.id_cidade FROM cidade c WHERE c.nome = 'Beira'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Ponta Gêa');

INSERT INTO bairro (nome, id_cidade)
SELECT 'Muhala Expansão', c.id_cidade FROM cidade c WHERE c.nome = 'Nampula'
AND NOT EXISTS (SELECT 1 FROM bairro WHERE nome = 'Muhala Expansão');

INSERT INTO categoria (nome)
SELECT 'Cereais e Grãos'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Cereais e Grãos');

INSERT INTO categoria (nome)
SELECT 'Farinhas e Massas'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Farinhas e Massas');

INSERT INTO categoria (nome)
SELECT 'Óleos, Azeites e Gorduras'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Óleos, Azeites e Gorduras');

INSERT INTO categoria (nome)
SELECT 'Açúcar, Sal e Condimentos'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Açúcar, Sal e Condimentos');

INSERT INTO categoria (nome)
SELECT 'Enlatados e Conservas'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Enlatados e Conservas');

INSERT INTO categoria (nome)
SELECT 'Lacticínios'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Lacticínios');

INSERT INTO categoria (nome)
SELECT 'Bebidas'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Bebidas');

INSERT INTO categoria (nome)
SELECT 'Águas, Sumos e Refrigerantes'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Águas, Sumos e Refrigerantes');

INSERT INTO categoria (nome)
SELECT 'Café, Chá e Achocolatados'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Café, Chá e Achocolatados');

INSERT INTO categoria (nome)
SELECT 'Bolachas, Doces e Snacks'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Bolachas, Doces e Snacks');

INSERT INTO categoria (nome)
SELECT 'Padaria e Pastelaria'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Padaria e Pastelaria');

INSERT INTO categoria (nome)
SELECT 'Talho, Aves e Congelados'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Talho, Aves e Congelados');

INSERT INTO categoria (nome)
SELECT 'Charcutaria e Frios'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Charcutaria e Frios');

INSERT INTO categoria (nome)
SELECT 'Frutas, Legumes e Hortícolas'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Frutas, Legumes e Hortícolas');

INSERT INTO categoria (nome)
SELECT 'Higiene e Limpeza'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Higiene e Limpeza');

INSERT INTO categoria (nome)
SELECT 'Higiene Pessoal e Perfumaria'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Higiene Pessoal e Perfumaria');

INSERT INTO categoria (nome)
SELECT 'Limpeza Doméstica e Lavandaria'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Limpeza Doméstica e Lavandaria');

INSERT INTO categoria (nome)
SELECT 'Bebé e Infantil'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Bebé e Infantil');

INSERT INTO categoria (nome)
SELECT 'Descartáveis e Utilidades'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Descartáveis e Utilidades');

INSERT INTO categoria (nome)
SELECT 'Papelaria, Pilhas e Iluminação'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Papelaria, Pilhas e Iluminação');

INSERT INTO categoria (nome)
SELECT 'Mercearia Geral'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'Mercearia Geral');


INSERT INTO pessoa (nome)
SELECT 'Administrador do Sistema'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100000001A');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100000001A', '1988-03-15', '2020-01-10', 'ATIVO', c.id_cargo
FROM pessoa p
CROSS JOIN cargo c
WHERE p.nome = 'Administrador do Sistema' AND c.nome = 'Administrador'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100000001A')
LIMIT 1;

INSERT INTO utilizador (username, password_hash, perfil, id_funcionario)
SELECT 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', f.id_funcionario
FROM funcionario f
WHERE f.bi = '110100000001A'
AND NOT EXISTS (SELECT 1 FROM utilizador WHERE username = 'admin')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 300 0001', f.id_pessoa FROM funcionario f WHERE f.bi = '110100000001A'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 04', 'Av. Eduardo Mondlane', '1240', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100000001A' AND b.nome = 'Alto Maé'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Carlos Alberto Mabjaia'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100234512B');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100234512B', '1986-07-22', '2021-03-01', 'ATIVO', c.id_cargo
FROM pessoa p CROSS JOIN cargo c
WHERE p.nome = 'Carlos Alberto Mabjaia' AND c.nome = 'Gerente'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100234512B')
LIMIT 1;

INSERT INTO utilizador (username, password_hash, perfil, id_funcionario)
SELECT 'carlos', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'GERENTE', f.id_funcionario
FROM funcionario f WHERE f.bi = '110100234512B'
AND NOT EXISTS (SELECT 1 FROM utilizador WHERE username = 'carlos')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 412 3456', f.id_pessoa FROM funcionario f WHERE f.bi = '110100234512B'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 12', 'Av. Samora Machel', '88', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100234512B' AND b.nome = 'Fomento'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Ana Paula Macie'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100345623C');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100345623C', '1995-11-08', '2022-02-15', 'ATIVO', c.id_cargo
FROM pessoa p CROSS JOIN cargo c
WHERE p.nome = 'Ana Paula Macie' AND c.nome = 'Vendedor'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100345623C')
LIMIT 1;

INSERT INTO utilizador (username, password_hash, perfil, id_funcionario)
SELECT 'ana', '$2a$10$.5QKVdvnRy0/eA9jn/hUPuNlhEmkUXqqMliRr60EFtItlPLH5DCIy', 'VENDEDOR', f.id_funcionario
FROM funcionario f WHERE f.bi = '110100345623C'
AND NOT EXISTS (SELECT 1 FROM utilizador WHERE username = 'ana')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '82 523 7890', f.id_pessoa FROM funcionario f WHERE f.bi = '110100345623C'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 09', 'Rua de Nachingwea', '42', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100345623C' AND b.nome = 'Malhangalene'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'João Ernesto Sitoe'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100456734D');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100456734D', '1993-04-19', '2022-06-01', 'ATIVO', c.id_cargo
FROM pessoa p CROSS JOIN cargo c
WHERE p.nome = 'João Ernesto Sitoe' AND c.nome = 'Vendedor'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100456734D')
LIMIT 1;

INSERT INTO utilizador (username, password_hash, perfil, id_funcionario)
SELECT 'joao', '$2a$10$.5QKVdvnRy0/eA9jn/hUPuNlhEmkUXqqMliRr60EFtItlPLH5DCIy', 'VENDEDOR', f.id_funcionario
FROM funcionario f WHERE f.bi = '110100456734D'
AND NOT EXISTS (SELECT 1 FROM utilizador WHERE username = 'joao')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 634 8901', f.id_pessoa FROM funcionario f WHERE f.bi = '110100456734D'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 18', 'Rua da Mozal', '115', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100456734D' AND b.nome = 'Matola A'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Maria de Lurdes Nhaca'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100567845E');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100567845E', '1997-09-12', '2023-01-10', 'ATIVO', c.id_cargo
FROM pessoa p CROSS JOIN cargo c
WHERE p.nome = 'Maria de Lurdes Nhaca' AND c.nome = 'Operador de Caixa'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100567845E')
LIMIT 1;

INSERT INTO utilizador (username, password_hash, perfil, id_funcionario)
SELECT 'maria', '$2a$10$.5QKVdvnRy0/eA9jn/hUPuNlhEmkUXqqMliRr60EFtItlPLH5DCIy', 'VENDEDOR', f.id_funcionario
FROM funcionario f WHERE f.bi = '110100567845E'
AND NOT EXISTS (SELECT 1 FROM utilizador WHERE username = 'maria')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '87 745 9012', f.id_pessoa FROM funcionario f WHERE f.bi = '110100567845E'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 06', 'Av. de Angola', '310', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100567845E' AND b.nome = 'Maxaquene'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Paulo Alexandre Cossa'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100678956F');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100678956F', '1991-01-25', '2021-08-20', 'ATIVO', c.id_cargo
FROM pessoa p CROSS JOIN cargo c
WHERE p.nome = 'Paulo Alexandre Cossa' AND c.nome = 'Fiel de Armazém'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100678956F')
LIMIT 1;

INSERT INTO utilizador (username, password_hash, perfil, id_funcionario)
SELECT 'paulo', '$2a$10$.5QKVdvnRy0/eA9jn/hUPuNlhEmkUXqqMliRr60EFtItlPLH5DCIy', 'GERENTE', f.id_funcionario
FROM funcionario f WHERE f.bi = '110100678956F'
AND NOT EXISTS (SELECT 1 FROM utilizador WHERE username = 'paulo')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 856 0123', f.id_pessoa FROM funcionario f WHERE f.bi = '110100678956F'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 21', 'Rua das Acácias', '54', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100678956F' AND b.nome = 'Liberdade'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Nélio Fernando Mondlane'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100789067G');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100789067G', '1998-05-14', '2024-02-01', 'ATIVO', c.id_cargo
FROM pessoa p CROSS JOIN cargo c
WHERE p.nome = 'Nélio Fernando Mondlane' AND c.nome = 'Repositor de Stock'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100789067G')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '86 912 3344', f.id_pessoa FROM funcionario f WHERE f.bi = '110100789067G'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 15', 'Rua do Comércio', '19', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100789067G' AND b.nome = 'Zimpeto'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Helga Cristina Matsinhe'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100890178H');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100890178H', '1996-10-30', '2024-04-15', 'ATIVO', c.id_cargo
FROM pessoa p CROSS JOIN cargo c
WHERE p.nome = 'Helga Cristina Matsinhe' AND c.nome = 'Supervisor de Loja'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100890178H')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 221 5566', f.id_pessoa FROM funcionario f WHERE f.bi = '110100890178H'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 03', 'Av. 24 de Julho', '742', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100890178H' AND b.nome = 'Polana Cimento'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Celso Tomás Bila'
WHERE NOT EXISTS (SELECT 1 FROM funcionario f WHERE f.bi = '110100901289I');

INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo)
SELECT p.id_pessoa, '110100901289I', '1994-12-03', '2022-09-01', 'SUSPENSO', c.id_cargo
FROM pessoa p CROSS JOIN cargo c
WHERE p.nome = 'Celso Tomás Bila' AND c.nome = 'Vendedor'
AND NOT EXISTS (SELECT 1 FROM funcionario WHERE bi = '110100901289I')
LIMIT 1;

INSERT INTO contacto (contacto, id_pessoa)
SELECT '82 339 7788', f.id_pessoa FROM funcionario f WHERE f.bi = '110100901289I'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = f.id_pessoa);

INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 08', 'Rua da Indústria', '204', b.id_bairro, f.id_pessoa
FROM funcionario f CROSS JOIN bairro b
WHERE f.bi = '110100901289I' AND b.nome = 'Machava Sede'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = f.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Consumidor Final'
WHERE NOT EXISTS (
    SELECT 1 FROM pessoa p
    INNER JOIN cliente cl ON cl.id_pessoa = p.id_pessoa
    WHERE LOWER(p.nome) = 'consumidor final'
);

INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p
WHERE LOWER(p.nome) = 'consumidor final'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa)
LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Amélia Francisca Tembe'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Amélia Francisca Tembe');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Amélia Francisca Tembe'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 210 1122', p.id_pessoa FROM pessoa p WHERE p.nome = 'Amélia Francisca Tembe'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 05', 'Av. Agostinho Neto', '214', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Amélia Francisca Tembe' AND b.nome = 'Central'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Benedito Samuel Chissano'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Benedito Samuel Chissano');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Benedito Samuel Chissano'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '82 315 4455', p.id_pessoa FROM pessoa p WHERE p.nome = 'Benedito Samuel Chissano'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 11', 'Rua Kwame Nkrumah', '89', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Benedito Samuel Chissano' AND b.nome = 'Sommerschield'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Cremilde Augusta Machel'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Cremilde Augusta Machel');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Cremilde Augusta Machel'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 512 8899', p.id_pessoa FROM pessoa p WHERE p.nome = 'Cremilde Augusta Machel'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 02', 'Av. Mao Tse Tung', '512', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Cremilde Augusta Machel' AND b.nome = 'Coop'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Dércio Vasco Guambe'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Dércio Vasco Guambe');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Dércio Vasco Guambe'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '87 640 2233', p.id_pessoa FROM pessoa p WHERE p.nome = 'Dércio Vasco Guambe'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 14', 'Rua das Palmeiras', '67', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Dércio Vasco Guambe' AND b.nome = 'Fomento'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Edna Patrícia Langa'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Edna Patrícia Langa');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Edna Patrícia Langa'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 778 9900', p.id_pessoa FROM pessoa p WHERE p.nome = 'Edna Patrícia Langa'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 07', 'Rua de Bagamoyo', '150', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Edna Patrícia Langa' AND b.nome = 'Baixa'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Filipe Armando Zandamela'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Filipe Armando Zandamela');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Filipe Armando Zandamela'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '86 445 6677', p.id_pessoa FROM pessoa p WHERE p.nome = 'Filipe Armando Zandamela'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 19', 'Av. Acordos de Lusaka', '930', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Filipe Armando Zandamela' AND b.nome = 'Magoanine'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Graça Beatriz Uamusse'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Graça Beatriz Uamusse');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Graça Beatriz Uamusse'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 901 3344', p.id_pessoa FROM pessoa p WHERE p.nome = 'Graça Beatriz Uamusse'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 10', 'Rua da Sé', '41', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Graça Beatriz Uamusse' AND b.nome = 'Matola C'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Hélder Domingos Mucavele'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Hélder Domingos Mucavele');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Hélder Domingos Mucavele'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '82 880 1234', p.id_pessoa FROM pessoa p WHERE p.nome = 'Hélder Domingos Mucavele'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 23', 'Rua Direita do Benfica', '302', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Hélder Domingos Mucavele' AND b.nome = 'Benfica'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Inês Vitória Nhampossa'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Inês Vitória Nhampossa');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Inês Vitória Nhampossa'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 332 7654', p.id_pessoa FROM pessoa p WHERE p.nome = 'Inês Vitória Nhampossa'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 01', 'Av. Marginal', '118', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Inês Vitória Nhampossa' AND b.nome = 'Costa do Sol'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Restaurante Sabores da Baixa'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Restaurante Sabores da Baixa');
INSERT INTO cliente (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Restaurante Sabores da Baixa'
AND NOT EXISTS (SELECT 1 FROM cliente cl WHERE cl.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 200 9500', p.id_pessoa FROM pessoa p WHERE p.nome = 'Restaurante Sabores da Baixa'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 02', 'Av. 25 de Setembro', '1420', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Restaurante Sabores da Baixa' AND b.nome = 'Baixa'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;


INSERT INTO pessoa (nome)
SELECT 'Companhia Industrial da Matola (CIM)'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Companhia Industrial da Matola (CIM)');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Companhia Industrial da Matola (CIM)'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 301 1000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Companhia Industrial da Matola (CIM)'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Zona Industrial', 'Av. das Indústrias', '100', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Companhia Industrial da Matola (CIM)' AND b.nome = 'Matola A'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Coca-Cola Sabco Moçambique'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Coca-Cola Sabco Moçambique');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Coca-Cola Sabco Moçambique'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 302 2000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Coca-Cola Sabco Moçambique'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Zona Industrial', 'Av. da União Africana', '450', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Coca-Cola Sabco Moçambique' AND b.nome = 'Machava Sede'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Tropigália Moçambique Distribuição'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Tropigália Moçambique Distribuição');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Tropigália Moçambique Distribuição'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 303 3000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Tropigália Moçambique Distribuição'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 06', 'Av. de Moçambique Km 6', '210', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Tropigália Moçambique Distribuição' AND b.nome = 'Zimpeto'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Maeva Moçambique (Óleos e Sabões)'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Maeva Moçambique (Óleos e Sabões)');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Maeva Moçambique (Óleos e Sabões)'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 304 4000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Maeva Moçambique (Óleos e Sabões)'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 15', 'Estrada Nacional N4', '780', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Maeva Moçambique (Óleos e Sabões)' AND b.nome = 'Matola C'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Parmalat Moçambique Lda'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Parmalat Moçambique Lda');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Parmalat Moçambique Lda'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '82 305 5000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Parmalat Moçambique Lda'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 03', 'Av. do Trabalho', '1540', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Parmalat Moçambique Lda' AND b.nome = 'Alto Maé'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Águas da Namaacha & Vumba Distribuidora'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Águas da Namaacha & Vumba Distribuidora');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Águas da Namaacha & Vumba Distribuidora'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 306 6000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Águas da Namaacha & Vumba Distribuidora'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 08', 'Estrada de Boane Km 12', '50', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Águas da Namaacha & Vumba Distribuidora' AND b.nome = 'Belo Horizonte'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Avícola Higest & Congelados Sul'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Avícola Higest & Congelados Sul');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Avícola Higest & Congelados Sul'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '87 307 7000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Avícola Higest & Congelados Sul'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 12', 'Estrada Velha da Matola', '320', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Avícola Higest & Congelados Sul' AND b.nome = 'Patrice Lumumba'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Delta Trading & Higiene Moçambique'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Delta Trading & Higiene Moçambique');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Delta Trading & Higiene Moçambique'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 308 8000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Delta Trading & Higiene Moçambique'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 04', 'Av. Guerra Popular', '890', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Delta Trading & Higiene Moçambique' AND b.nome = 'Baixa'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Açucareira de Xinavane & Maragra'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Açucareira de Xinavane & Maragra');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Açucareira de Xinavane & Maragra'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '82 309 9000', p.id_pessoa FROM pessoa p WHERE p.nome = 'Açucareira de Xinavane & Maragra'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 01', 'Estrada Nacional N1', '12', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Açucareira de Xinavane & Maragra' AND b.nome = 'Marracuene Sede'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;

INSERT INTO pessoa (nome)
SELECT 'Recheio Cash & Carry Maputo'
WHERE NOT EXISTS (SELECT 1 FROM pessoa WHERE nome = 'Recheio Cash & Carry Maputo');
INSERT INTO fornecedor (id_pessoa)
SELECT p.id_pessoa FROM pessoa p WHERE p.nome = 'Recheio Cash & Carry Maputo'
AND NOT EXISTS (SELECT 1 FROM fornecedor fn WHERE fn.id_pessoa = p.id_pessoa) LIMIT 1;
INSERT INTO contacto (contacto, id_pessoa)
SELECT '84 310 1010', p.id_pessoa FROM pessoa p WHERE p.nome = 'Recheio Cash & Carry Maputo'
AND NOT EXISTS (SELECT 1 FROM contacto WHERE id_pessoa = p.id_pessoa);
INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa)
SELECT 'Q. 09', 'Av. Julius Nyerere', '2100', b.id_bairro, p.id_pessoa
FROM pessoa p CROSS JOIN bairro b WHERE p.nome = 'Recheio Cash & Carry Maputo' AND b.nome = 'Polana Cimento'
AND NOT EXISTS (SELECT 1 FROM endereco WHERE id_pessoa = p.id_pessoa) LIMIT 1;


INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000011', 'Arroz Mariana 25kg', 1650.00, 'Saco de arroz branco longo tipo 1 Mariana 25kg', 'Saco', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000011');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000028', 'Arroz Bom Gosto 25kg', 1580.00, 'Saco de arroz agulha extra longo Bom Gosto 25kg', 'Saco', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000028');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000035', 'Arroz Basmati Royal 5kg', 680.00, 'Arroz Basmati aromático especial pacote 5kg', 'Pacote', 'ATIVO', 10, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000035');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000042', 'Arroz Agulha Mariana 5kg', 390.00, 'Pacote familiar de arroz agulha Mariana 5kg', 'Pacote', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000042');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000059', 'Arroz Agulha Mariana 1kg', 85.00, 'Pacote de arroz agulha Mariana 1kg', 'Pacote', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000059');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000066', 'Feijão Manteiga Seleccionado 1kg', 165.00, 'Feijão manteiga nacional de primeira qualidade 1kg', 'Kg', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000066');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000073', 'Feijão Nhemba Nacional 1kg', 140.00, 'Feijão nhemba seco tradicional moçambicano 1kg', 'Kg', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000073');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000080', 'Feijão Preto 1kg', 160.00, 'Feijão preto limpo embalado 1kg', 'Kg', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000080');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000097', 'Amendoim Cru Descascado 1kg', 180.00, 'Amendoim nacional para caril de amendoim e torra 1kg', 'Kg', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000097');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001001000103', 'Grão de Bico Seco 500g', 125.00, 'Grão de bico seleccionado pacote 500g', 'Pacote', 'ATIVO', 10, c.id_categoria
FROM categoria c WHERE c.nome = 'Cereais e Grãos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001001000103');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000010', 'Farinha de Milho Top Score 10kg', 590.00, 'Farinha de milho branca fortificada Top Score 10kg', 'Saco', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000010');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000027', 'Farinha de Milho Top Score 5kg', 310.00, 'Farinha de milho branca fortificada Top Score 5kg', 'Pacote', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000027');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000034', 'Farinha de Milho CIM 2.5kg', 165.00, 'Farinha de milho branca especial para xima 2.5kg', 'Pacote', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000034');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000041', 'Farinha de Trigo Flor de Trigo CIM 1kg', 75.00, 'Farinha de trigo tipo 1 sem fermento CIM 1kg', 'Pacote', 'ATIVO', 35, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000041');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000058', 'Farinha de Trigo Flor de Trigo CIM 5kg', 350.00, 'Farinha de trigo para pastelaria e panificação CIM 5kg', 'Pacote', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000058');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000065', 'Esparguete Pasta Nacional CIM 500g', 55.00, 'Massa alimentícia tipo esparguete CIM 500g', 'Pacote', 'ATIVO', 40, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000065');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000072', 'Massa Cotovelo CIM 500g', 55.00, 'Massa alimentícia tipo cotovelo CIM 500g', 'Pacote', 'ATIVO', 40, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000072');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000089', 'Massa Parafuso Bela 500g', 60.00, 'Massa alimentícia tipo parafuso Bela 500g', 'Pacote', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000089');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001002000096', 'Flocos de Aveia Jungle Oats 1kg', 220.00, 'Aveia em flocos tradicional Jungle Oats caixa 1kg', 'Caixa', 'ATIVO', 12, c.id_categoria
FROM categoria c WHERE c.nome = 'Farinhas e Massas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001002000096');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001003000019', 'Óleo Alimentar Maeva 1L', 145.00, 'Óleo vegetal refinado de soja Maeva garrafa 1L', 'Garrafa', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Óleos, Azeites e Gorduras' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001003000019');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001003000026', 'Óleo Alimentar Maeva 2L', 280.00, 'Óleo vegetal refinado Maeva garrafa 2L', 'Garrafa', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Óleos, Azeites e Gorduras' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001003000026');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001003000033', 'Óleo Alimentar Maeva 5L', 675.00, 'Garrafão económico de óleo alimentar Maeva 5L', 'Garrafão', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Óleos, Azeites e Gorduras' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001003000033');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001003000040', 'Óleo de Girassol Dona 2L', 295.00, 'Óleo puro de sementes de girassol Dona 2L', 'Garrafa', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Óleos, Azeites e Gorduras' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001003000040');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001003000057', 'Azeite Virgem Extra Gallo 500ml', 490.00, 'Azeite português virgem extra clássico Gallo 500ml', 'Garrafa', 'ATIVO', 10, c.id_categoria
FROM categoria c WHERE c.nome = 'Óleos, Azeites e Gorduras' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001003000057');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001003000064', 'Margarina Rama 500g', 165.00, 'Creme vegetal para barrar e cozinhar Rama 500g', 'Unidade', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Óleos, Azeites e Gorduras' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001003000064');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001003000071', 'Margarina Flora 250g', 95.00, 'Margarina vegetal leve Flora 250g', 'Unidade', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Óleos, Azeites e Gorduras' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001003000071');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001003000088', 'Maionese Crosse & Blackwell 750g', 240.00, 'Maionese tradicional cremosa frasco 750g', 'Frasco', 'ATIVO', 12, c.id_categoria
FROM categoria c WHERE c.nome = 'Óleos, Azeites e Gorduras' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001003000088');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000018', 'Açúcar Castanho Xinavane 1kg', 95.00, 'Açúcar castanho fortificado com vitamina A Xinavane 1kg', 'Pacote', 'ATIVO', 40, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000018');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000025', 'Açúcar Castanho Xinavane 2kg', 185.00, 'Açúcar castanho nacional Xinavane pacote 2kg', 'Pacote', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000025');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000032', 'Açúcar Branco Maragra 1kg', 110.00, 'Açúcar refinado branco Maragra 1kg', 'Pacote', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000032');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000049', 'Sal Refinado Iodado 1kg', 35.00, 'Sal marinho refinado e iodado de cozinha 1kg', 'Pacote', 'ATIVO', 50, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000049');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000056', 'Caldo Benny Galinha Caixa 24un', 120.00, 'Cubos de tempero sabor galinha Benny caixa com 24 unidades', 'Caixa', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000056');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000063', 'Caril em Pó Rajah Mild 100g', 95.00, 'Tempero de caril aromático Rajah caixa 100g', 'Caixa', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000063');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000070', 'Vinagre Branco de Mesa 750ml', 55.00, 'Vinagre branco para saladas e temperos 750ml', 'Garrafa', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000070');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000087', 'Molho Piripíri Nando''s Hot 250ml', 195.00, 'Molho picante tradicional de piripíri Nando''s 250ml', 'Garrafa', 'ATIVO', 12, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000087');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001004000094', 'Ketchup All Gold 700g', 185.00, 'Molho de tomate ketchup All Gold garrafa 700g', 'Garrafa', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Açúcar, Sal e Condimentos' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001004000094');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001005000017', 'Atum Ralado em Óleo 170g', 95.00, 'Conserva de atum ralado em óleo vegetal lata 170g', 'Lata', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Enlatados e Conservas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001005000017');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001005000024', 'Sardinha em Molho de Tomate Lucky Star 400g', 135.00, 'Pilchards em molho de tomate Lucky Star lata 400g', 'Lata', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Enlatados e Conservas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001005000024');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001005000031', 'Sardinha em Óleo Vegetal 125g', 65.00, 'Sardinha portuguesa em óleo vegetal lata 125g', 'Lata', 'ATIVO', 35, c.id_categoria
FROM categoria c WHERE c.nome = 'Enlatados e Conservas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001005000031');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001005000048', 'Feijão Cozido Baked Beans Koo 410g', 110.00, 'Feijão branco cozido em molho de tomate Koo 410g', 'Lata', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Enlatados e Conservas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001005000048');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001005000055', 'Polpa de Tomate All Gold 400g', 115.00, 'Tomate pelado e triturado para refogados All Gold 400g', 'Lata', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Enlatados e Conservas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001005000055');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001005000062', 'Leite Condensado Nestlé 385g', 145.00, 'Leite condensado açucarado Nestlé lata 385g', 'Lata', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Enlatados e Conservas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001005000062');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001005000079', 'Creme de Leite Nestlé 290g', 130.00, 'Natas / creme de leite esterilizado Nestlé lata 290g', 'Lata', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Enlatados e Conservas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001005000079');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001006000016', 'Leite UHT Meio Gordo Parmalat 1L', 110.00, 'Leite ultrapasteurizado meio gordo Parmalat pacote 1L', 'Litro', 'ATIVO', 40, c.id_categoria
FROM categoria c WHERE c.nome = 'Lacticínios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001006000016');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001006000023', 'Leite UHT Gordo Clover 1L', 115.00, 'Leite ultrapasteurizado inteiro Clover 1L', 'Litro', 'ATIVO', 35, c.id_categoria
FROM categoria c WHERE c.nome = 'Lacticínios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001006000023');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001006000030', 'Leite em Pó Nido Nestlé 400g', 360.00, 'Leite em pó instantâneo integral Nido lata 400g', 'Lata', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Lacticínios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001006000030');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001006000047', 'Leite em Pó Nido Nestlé 900g', 740.00, 'Leite em pó instantâneo integral Nido lata familiar 900g', 'Lata', 'ATIVO', 10, c.id_categoria
FROM categoria c WHERE c.nome = 'Lacticínios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001006000047');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001006000054', 'Iogurte Líquido Morango Parmalat 1L', 165.00, 'Iogurte líquido aromatizado de morango Parmalat 1L', 'Litro', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Lacticínios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001006000054');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001006000061', 'Iogurte Danone Tutti-Frutti Pack 6un', 155.00, 'Pack com 6 copos de iogurte cremoso Danone', 'Pacote', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Lacticínios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001006000061');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001006000078', 'Manteiga Com Sal Clover 500g', 320.00, 'Manteiga pasteurizada com sal Clover tablete 500g', 'Unidade', 'ATIVO', 12, c.id_categoria
FROM categoria c WHERE c.nome = 'Lacticínios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001006000078');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001006000085', 'Queijo Flamengo Fatiado 250g', 245.00, 'Queijo flamengo fatiado para sandes embalagem 250g', 'Pacote', 'ATIVO', 12, c.id_categoria
FROM categoria c WHERE c.nome = 'Lacticínios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001006000085');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000015', 'Água Mineral da Namaacha 1.5L', 45.00, 'Água mineral natural sem gás Namaacha garrafa 1.5L', 'Garrafa', 'ATIVO', 60, c.id_categoria
FROM categoria c WHERE c.nome = 'Águas, Sumos e Refrigerantes' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000015');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000022', 'Água Mineral Vumba 1.5L', 45.00, 'Água mineral natural de nascente Vumba garrafa 1.5L', 'Garrafa', 'ATIVO', 60, c.id_categoria
FROM categoria c WHERE c.nome = 'Águas, Sumos e Refrigerantes' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000022');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000039', 'Água Mineral da Namaacha 5L', 125.00, 'Garrafão familiar de água mineral da Namaacha 5L', 'Garrafão', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Águas, Sumos e Refrigerantes' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000039');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000046', 'Água Mineral Vumba 500ml', 25.00, 'Garrafa individual de água mineral Vumba 500ml', 'Garrafa', 'ATIVO', 80, c.id_categoria
FROM categoria c WHERE c.nome = 'Águas, Sumos e Refrigerantes' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000046');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000053', 'Refrigerante Coca-Cola 2L', 110.00, 'Refrigerante de cola garrafa PET familiar 2L', 'Garrafa', 'ATIVO', 50, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebidas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000053');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000060', 'Refrigerante Fanta Laranja 2L', 110.00, 'Refrigerante sabor laranja Fanta PET 2L', 'Garrafa', 'ATIVO', 40, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebidas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000060');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000077', 'Refrigerante Sprite 2L', 110.00, 'Refrigerante lima-limão Sprite PET 2L', 'Garrafa', 'ATIVO', 35, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebidas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000077');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000084', 'Refrigerante Sparletta Creme Soda 2L', 110.00, 'Refrigerante Creme Soda verde Sparletta 2L', 'Garrafa', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebidas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000084');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000091', 'Refrigerante Coca-Cola Lata 300ml', 45.00, 'Refrigerante Coca-Cola lata individual 300ml', 'Lata', 'ATIVO', 60, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebidas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000091');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000107', 'Sumo Ceres Néctar de Manga 1L', 145.00, 'Sumo 100% fruta sabor manga Ceres pacote 1L', 'Caixa', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Águas, Sumos e Refrigerantes' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000107');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000114', 'Sumo Compal Clássico Pêssego 1L', 150.00, 'Néctar de pêssego Compal embalagem Tetra Pak 1L', 'Caixa', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Águas, Sumos e Refrigerantes' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000114');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000121', 'Sumo Liqui Fruit Maçã 1L', 145.00, 'Sumo 100% maçã sem adição de açúcar Liqui Fruit 1L', 'Caixa', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Águas, Sumos e Refrigerantes' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000121');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000138', 'Bebida Energética Red Bull 250ml', 130.00, 'Energy drink Red Bull lata 250ml', 'Lata', 'ATIVO', 24, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebidas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000138');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001007000145', 'Sumo Concentrado Mazoe Laranja 2L', 230.00, 'Concentrado de laranja para diluir Mazoe Orange Crush 2L', 'Garrafa', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Águas, Sumos e Refrigerantes' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001007000145');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001008000014', 'Café Solúvel Nescafé Clássico 200g', 380.00, 'Café solúvel 100% puro Nescafé frasco de vidro 200g', 'Frasco', 'ATIVO', 12, c.id_categoria
FROM categoria c WHERE c.nome = 'Café, Chá e Achocolatados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001008000014');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001008000021', 'Café Solúvel Ricoffy 250g', 240.00, 'Mistura solúvel de café e chicória Ricoffy lata 250g', 'Lata', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Café, Chá e Achocolatados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001008000021');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001008000038', 'Chá Preto Five Roses 102 Saquetas', 210.00, 'Chá preto seleccionado Five Roses caixa com 102 saquetas', 'Caixa', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Café, Chá e Achocolatados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001008000038');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001008000045', 'Chá Rooibos Freshpak 80 Saquetas', 195.00, 'Chá natural sem cafeína Rooibos Freshpak 80 saquetas', 'Caixa', 'ATIVO', 12, c.id_categoria
FROM categoria c WHERE c.nome = 'Café, Chá e Achocolatados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001008000045');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001008000052', 'Achocolatado em Pó Milo Nestlé 400g', 340.00, 'Bebida energética de malte e cacau Milo lata 400g', 'Lata', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Café, Chá e Achocolatados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001008000052');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001009000013', 'Bolacha Maria Bakers 200g', 55.00, 'Bolacha Maria tradicional crocante pacote 200g', 'Pacote', 'ATIVO', 40, c.id_categoria
FROM categoria c WHERE c.nome = 'Bolachas, Doces e Snacks' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001009000013');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001009000020', 'Bolacha Água e Sal Cream Crackers 200g', 65.00, 'Bolacha salgada Cream Crackers pacote 200g', 'Pacote', 'ATIVO', 35, c.id_categoria
FROM categoria c WHERE c.nome = 'Bolachas, Doces e Snacks' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001009000020');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001009000037', 'Bolacha Recheada Oreo 154g', 95.00, 'Bolacha de cacau com recheio de baunilha Oreo 154g', 'Pacote', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Bolachas, Doces e Snacks' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001009000037');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001009000044', 'Batata Frita Simba Queijo e Cebola 120g', 95.00, 'Batata frita ondulada Simba Cheese & Onion 120g', 'Pacote', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Bolachas, Doces e Snacks' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001009000044');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001009000051', 'Snack de Milho NikNaks Queijo 135g', 65.00, 'Snack crocante de milho sabor queijo NikNaks 135g', 'Pacote', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Bolachas, Doces e Snacks' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001009000051');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001009000068', 'Chocolate Cadbury Dairy Milk 80g', 115.00, 'Tablete de chocolate de leite Cadbury 80g', 'Unidade', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Bolachas, Doces e Snacks' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001009000068');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001010000012', 'Pão de Forma Branco Fatiado 700g', 75.00, 'Pão de forma macio fatiado para sandes e torradas 700g', 'Unidade', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Padaria e Pastelaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001010000012');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001010000029', 'Pão de Forma Integral Fatiado 700g', 85.00, 'Pão de forma rico em fibras 700g', 'Unidade', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Padaria e Pastelaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001010000029');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001010000036', 'Ovos Frescos Grandes Favo 30un', 295.00, 'Bandeja / favo com 30 ovos frescos de aviário', 'Bandeja', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Mercearia Geral' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001010000036');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001010000043', 'Ovos Frescos Grandes Caixa 12un', 130.00, 'Dúzia de ovos frescos selecionados', 'Caixa', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Mercearia Geral' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001010000043');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001011000011', 'Frango Inteiro Congelado Higest 1.4kg', 285.00, 'Frango inteiro limpo e congelado Higest 1.4kg', 'Unidade', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Talho, Aves e Congelados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001011000011');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001011000028', 'Coxas de Frango Congeladas 2kg', 460.00, 'Saco familiar de coxas e sobrecoxas de frango IQF 2kg', 'Pacote', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Talho, Aves e Congelados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001011000028');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001011000035', 'Salsicha de Frango 340g', 95.00, 'Pacote de salsichas tipo viena de frango 340g', 'Pacote', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Charcutaria e Frios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001011000035');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001011000042', 'Chouriço Tradicional Defumado 250g', 190.00, 'Chouriço de carne de porco para feijoada e grelhados 250g', 'Unidade', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Charcutaria e Frios' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001011000042');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001011000059', 'Carapau Congelado 1kg', 210.00, 'Peixe carapau seleccionado congelado embalagem 1kg', 'Kg', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Talho, Aves e Congelados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001011000059');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001011000066', 'Batata Pré-Frita Congelada McCain 1kg', 225.00, 'Batata palito pré-frita congelada pronta para fritar 1kg', 'Pacote', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Talho, Aves e Congelados' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001011000066');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001012000010', 'Batata Reno Saco 10kg', 480.00, 'Saco de batata reno fresca seleccionada 10kg', 'Saco', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Frutas, Legumes e Hortícolas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001012000010');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001012000027', 'Cebola Roxa Saco 10kg', 520.00, 'Saco de cebola roxa seca seleccionada 10kg', 'Saco', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Frutas, Legumes e Hortícolas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001012000027');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001012000034', 'Tomate Fresco de Mesa 1kg', 95.00, 'Tomate maduro seleccionado por quilograma', 'Kg', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Frutas, Legumes e Hortícolas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001012000034');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001012000041', 'Alho em Rede 250g', 85.00, 'Cabeças de alho branco seleccionadas rede 250g', 'Pacote', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Frutas, Legumes e Hortícolas' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001012000041');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000019', 'Sabonete Antibacteriano Protex Herbal 150g', 65.00, 'Sabonete de protecção antibacteriana Protex 150g', 'Unidade', 'ATIVO', 40, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene Pessoal e Perfumaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000019');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000026', 'Sabonete Hidratante Lux Suave 125g', 50.00, 'Sabonete perfumado para banho Lux 125g', 'Unidade', 'ATIVO', 45, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene Pessoal e Perfumaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000026');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000033', 'Pasta Dentífrica Colgate Total 12 75ml', 115.00, 'Creme dental protecção completa 12h Colgate Total 75ml', 'Unidade', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene Pessoal e Perfumaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000033');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000040', 'Pasta Dentífrica Close-Up Mentol 100ml', 95.00, 'Gel dental refrescante Close-Up 100ml', 'Unidade', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene Pessoal e Perfumaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000040');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000057', 'Champô Sedal Ceramidas 340ml', 195.00, 'Champô brilho e força com ceramidas Sedal 340ml', 'Frasco', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene Pessoal e Perfumaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000057');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000064', 'Desodorizante Roll-On Nivea 50ml', 145.00, 'Antitranspirante 48h Nivea Roll-On 50ml', 'Unidade', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene Pessoal e Perfumaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000064');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000071', 'Loção Corporal Vaseline Intensive Care 400ml', 230.00, 'Creme hidratante corporal para pele seca Vaseline 400ml', 'Frasco', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene Pessoal e Perfumaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000071');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000088', 'Papel Higiénico Twinsaver Pack 9 Rolos', 240.00, 'Papel higiénico macio folha dupla pacote económico 9 rolos', 'Pacote', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene e Limpeza' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000088');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001013000095', 'Papel Higiénico Folha Dupla Pack 4 Rolos', 110.00, 'Papel higiénico folha dupla pacote com 4 rolos', 'Pacote', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene e Limpeza' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001013000095');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000018', 'Sabão em Pó OMO Multiactivo 1kg', 195.00, 'Detergente em pó para roupa OMO 1kg', 'Pacote', 'ATIVO', 35, c.id_categoria
FROM categoria c WHERE c.nome = 'Limpeza Doméstica e Lavandaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000018');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000025', 'Sabão em Pó OMO Multiactivo 2kg', 370.00, 'Detergente em pó para lavar roupa OMO familiar 2kg', 'Pacote', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Limpeza Doméstica e Lavandaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000025');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000032', 'Sabão em Pó MAQ 2kg', 310.00, 'Sabão em pó de lavagem manual e máquina MAQ 2kg', 'Pacote', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Limpeza Doméstica e Lavandaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000032');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000049', 'Sabão Azul em Barra Bingo 1kg', 110.00, 'Barra de sabão multiuso tradicional Bingo 1kg', 'Barra', 'ATIVO', 40, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene e Limpeza' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000049');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000056', 'Detergente Lava-Louça Sunlight Líquido 750ml', 135.00, 'Detergente líquido desengordurante com sumo de limão Sunlight 750ml', 'Garrafa', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Limpeza Doméstica e Lavandaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000056');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000063', 'Lixívia Jik Tradicional 750ml', 95.00, 'Alvejante e desinfectante Jik 750ml', 'Garrafa', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Limpeza Doméstica e Lavandaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000063');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000070', 'Desinfectante Domestos Pinho 750ml', 145.00, 'Gel desinfectante espesso multiuso Domestos 750ml', 'Garrafa', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Limpeza Doméstica e Lavandaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000070');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000087', 'Amaciador de Roupa Sta-Soft Lavanda 2L', 240.00, 'Amaciador perfumado de longa duração Sta-Soft 2L', 'Garrafa', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Limpeza Doméstica e Lavandaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000087');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000094', 'Insecticida Aerossol Doom 300ml', 185.00, 'Spray insecticida acção rápida contra mosquitos e baratas Doom 300ml', 'Lata', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Higiene e Limpeza' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000094');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001014000100', 'Esponja Lava-Louça Dupla Face Pack 3un', 65.00, 'Esponjas abrasivas multiuso pacote com 3 unidades', 'Pacote', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Limpeza Doméstica e Lavandaria' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001014000100');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001015000017', 'Fraldas Descartáveis Pampers M Pack 44un', 890.00, 'Fraldas descartáveis ultra absorventes tamanho médio 44 unidades', 'Pacote', 'ATIVO', 10, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebé e Infantil' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001015000017');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001015000024', 'Toalhitas Húmidas Huggies 80un', 165.00, 'Toalhitas suaves de limpeza infantil Huggies 80 unidades', 'Pacote', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebé e Infantil' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001015000024');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001015000031', 'Papa Infantil Cerelac Trigo e Leite 250g', 210.00, 'Farinha láctea nutritiva Cerelac Nestlé lata 250g', 'Lata', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Bebé e Infantil' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001015000031');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001016000016', 'Sacos de Lixo Reforçados 50L Rolo 20un', 120.00, 'Rolo com 20 sacos pretos resistentes para lixo doméstico 50L', 'Rolo', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Descartáveis e Utilidades' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001016000016');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001016000023', 'Guardanapos de Papel Folha Simples Pack 100un', 65.00, 'Maço com 100 guardanapos de mesa brancos', 'Pacote', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Descartáveis e Utilidades' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001016000023');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001016000030', 'Velas de Iluminação Brancas Pack 6un', 85.00, 'Pacote com 6 velas brancas domésticas de longa duração', 'Pacote', 'ATIVO', 25, c.id_categoria
FROM categoria c WHERE c.nome = 'Descartáveis e Utilidades' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001016000030');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001016000047', 'Fósforos de Segurança Lion Pack 10 Caixas', 45.00, 'Fardo com 10 caixas de fósforos de segurança Lion', 'Pacote', 'ATIVO', 30, c.id_categoria
FROM categoria c WHERE c.nome = 'Mercearia Geral' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001016000047');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001017000015', 'Pilhas Alcalinas AA Energizer Pack 4un', 195.00, 'Blister com 4 pilhas alcalinas AA Energizer Max', 'Pacote', 'ATIVO', 15, c.id_categoria
FROM categoria c WHERE c.nome = 'Papelaria, Pilhas e Iluminação' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001017000015');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001017000022', 'Lâmpada LED 9W Luz Branca E27', 95.00, 'Lâmpada económica LED 9W bivolt casquilho E27', 'Unidade', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Papelaria, Pilhas e Iluminação' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001017000022');

INSERT INTO produto (codigo_barras, nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria)
SELECT '6001017000039', 'Caderno Escolar 80 Folhas A4', 75.00, 'Caderno pautado capa dura 80 folhas formato A4', 'Unidade', 'ATIVO', 20, c.id_categoria
FROM categoria c WHERE c.nome = 'Papelaria, Pilhas e Iluminação' AND NOT EXISTS (SELECT 1 FROM produto WHERE codigo_barras = '6001017000039');


INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
INNER JOIN categoria cat ON cat.id_categoria = pr.id_categoria
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE cat.nome IN ('Farinhas e Massas', 'Padaria e Pastelaria')
  AND p.nome = 'Companhia Industrial da Matola (CIM)'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
INNER JOIN categoria cat ON cat.id_categoria = pr.id_categoria
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE cat.nome IN ('Cereais e Grãos', 'Enlatados e Conservas', 'Café, Chá e Achocolatados', 'Bolachas, Doces e Snacks', 'Açúcar, Sal e Condimentos')
  AND p.nome = 'Tropigália Moçambique Distribuição'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
INNER JOIN categoria cat ON cat.id_categoria = pr.id_categoria
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE cat.nome IN ('Óleos, Azeites e Gorduras', 'Higiene e Limpeza')
  AND p.nome = 'Maeva Moçambique (Óleos e Sabões)'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
INNER JOIN categoria cat ON cat.id_categoria = pr.id_categoria
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE cat.nome = 'Lacticínios'
  AND p.nome = 'Parmalat Moçambique Lda'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
INNER JOIN categoria cat ON cat.id_categoria = pr.id_categoria
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE cat.nome = 'Bebidas'
  AND p.nome = 'Coca-Cola Sabco Moçambique'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
INNER JOIN categoria cat ON cat.id_categoria = pr.id_categoria
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE cat.nome = 'Águas, Sumos e Refrigerantes'
  AND p.nome = 'Águas da Namaacha & Vumba Distribuidora'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
INNER JOIN categoria cat ON cat.id_categoria = pr.id_categoria
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE cat.nome IN ('Talho, Aves e Congelados', 'Charcutaria e Frios', 'Mercearia Geral')
  AND p.nome = 'Avícola Higest & Congelados Sul'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
INNER JOIN categoria cat ON cat.id_categoria = pr.id_categoria
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE cat.nome IN ('Higiene Pessoal e Perfumaria', 'Limpeza Doméstica e Lavandaria', 'Bebé e Infantil', 'Descartáveis e Utilidades', 'Papelaria, Pilhas e Iluminação')
  AND p.nome = 'Delta Trading & Higiene Moçambique'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE pr.codigo_barras IN ('6001004000018', '6001004000025', '6001004000032')
  AND p.nome = 'Açucareira de Xinavane & Maragra'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );

INSERT INTO fornecedor_produto (id_produto, id_fornecedor)
SELECT pr.id_produto, fn.id_fornecedor
FROM produto pr
CROSS JOIN fornecedor fn
INNER JOIN pessoa p ON p.id_pessoa = fn.id_pessoa
WHERE pr.codigo_barras IN (
    '6001001000011', '6001001000028', '6001002000010', '6001003000019',
    '6001003000026', '6001006000016', '6001007000053', '6001012000010',
    '6001012000027', '6001014000018'
)
  AND p.nome = 'Recheio Cash & Carry Maputo'
  AND NOT EXISTS (
      SELECT 1 FROM fornecedor_produto fp
      WHERE fp.id_produto = pr.id_produto AND fp.id_fornecedor = fn.id_fornecedor
  );


INSERT INTO estoque (quantidade, data_entrada, data_validade, id_produto, id_funcionario)
SELECT
    85,
    DATE_SUB(NOW(), INTERVAL 15 DAY),
    DATE_ADD(CURRENT_DATE, INTERVAL 240 DAY),
    pr.id_produto,
    f.id_funcionario
FROM produto pr
CROSS JOIN funcionario f
WHERE f.bi = '110100000001A'
  AND pr.codigo_barras NOT IN ('6001003000057', '6001008000014', '6001015000017', '6001006000047')
  AND NOT EXISTS (SELECT 1 FROM estoque e WHERE e.id_produto = pr.id_produto);

INSERT INTO estoque (quantidade, data_entrada, data_validade, id_produto, id_funcionario)
SELECT
    3,
    DATE_SUB(NOW(), INTERVAL 20 DAY),
    DATE_ADD(CURRENT_DATE, INTERVAL 180 DAY),
    pr.id_produto,
    f.id_funcionario
FROM produto pr
CROSS JOIN funcionario f
WHERE f.bi = '110100000001A'
  AND pr.codigo_barras IN ('6001003000057', '6001008000014', '6001015000017', '6001006000047')
  AND NOT EXISTS (SELECT 1 FROM estoque e WHERE e.id_produto = pr.id_produto);

INSERT INTO estoque (quantidade, data_entrada, data_validade, id_produto, id_funcionario)
SELECT
    40,
    DATE_SUB(NOW(), INTERVAL 30 DAY),
    DATE_ADD(CURRENT_DATE, INTERVAL 45 DAY),
    pr.id_produto,
    f.id_funcionario
FROM produto pr
CROSS JOIN funcionario f
WHERE f.bi = '110100678956F'
  AND pr.codigo_barras IN (
      '6001001000011', '6001002000010', '6001003000019',
      '6001004000018', '6001006000016', '6001007000053'
  )
  AND (SELECT COUNT(*) FROM estoque e WHERE e.id_produto = pr.id_produto) < 2;

INSERT INTO estoque (quantidade, data_entrada, data_validade, id_produto, id_funcionario)
SELECT
    18,
    DATE_SUB(NOW(), INTERVAL 60 DAY),
    DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY),
    pr.id_produto,
    f.id_funcionario
FROM produto pr
CROSS JOIN funcionario f
WHERE f.bi = '110100678956F'
  AND pr.codigo_barras IN ('6001006000054', '6001006000061', '6001010000012')
  AND (SELECT COUNT(*) FROM estoque e WHERE e.id_produto = pr.id_produto) < 2;

INSERT INTO estoque (quantidade, data_entrada, data_validade, id_produto, id_funcionario)
SELECT
    6,
    DATE_SUB(NOW(), INTERVAL 180 DAY),
    DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY),
    pr.id_produto,
    f.id_funcionario
FROM produto pr
CROSS JOIN funcionario f
WHERE f.bi = '110100678956F'
  AND pr.codigo_barras IN ('6001006000085', '6001011000035')
  AND (SELECT COUNT(*) FROM estoque e WHERE e.id_produto = pr.id_produto) < 2;


INSERT INTO venda (data, total, estado, id_cliente, id_funcionario)
SELECT
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    2560.00,
    'CONCLUIDA',
    cl.id_cliente,
    f.id_funcionario
FROM cliente cl
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN funcionario f
WHERE p.nome = 'Amélia Francisca Tembe' AND f.bi = '110100345623C'
  AND NOT EXISTS (SELECT 1 FROM venda v WHERE v.total = 2560.00 AND v.id_cliente = cl.id_cliente);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 1650.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Amélia Francisca Tembe' AND v.total = 2560.00 AND pr.codigo_barras = '6001001000011'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 2, 280.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Amélia Francisca Tembe' AND v.total = 2560.00 AND pr.codigo_barras = '6001003000026'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 350.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Amélia Francisca Tembe' AND v.total = 2560.00 AND pr.codigo_barras = '6001002000058'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO venda (data, total, estado, id_cliente, id_funcionario)
SELECT
    DATE_SUB(NOW(), INTERVAL 3 DAY),
    1475.00,
    'CONCLUIDA',
    cl.id_cliente,
    f.id_funcionario
FROM cliente cl
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN funcionario f
WHERE p.nome = 'Benedito Samuel Chissano' AND f.bi = '110100456734D'
  AND NOT EXISTS (SELECT 1 FROM venda v WHERE v.total = 1475.00 AND v.id_cliente = cl.id_cliente);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 590.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Benedito Samuel Chissano' AND v.total = 1475.00 AND pr.codigo_barras = '6001002000010'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 2, 285.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Benedito Samuel Chissano' AND v.total = 1475.00 AND pr.codigo_barras = '6001011000011'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 315.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Benedito Samuel Chissano' AND v.total = 1475.00 AND pr.codigo_barras = '6001010000036'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO venda (data, total, estado, id_cliente, id_funcionario)
SELECT
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    6420.00,
    'CONCLUIDA',
    cl.id_cliente,
    f.id_funcionario
FROM cliente cl
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN funcionario f
WHERE p.nome = 'Restaurante Sabores da Baixa' AND f.bi = '110100234512B'
  AND NOT EXISTS (SELECT 1 FROM venda v WHERE v.total = 6420.00 AND v.id_cliente = cl.id_cliente);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 2, 1650.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Restaurante Sabores da Baixa' AND v.total = 6420.00 AND pr.codigo_barras = '6001001000011'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 2, 675.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Restaurante Sabores da Baixa' AND v.total = 6420.00 AND pr.codigo_barras = '6001003000033'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 10, 110.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Restaurante Sabores da Baixa' AND v.total = 6420.00 AND pr.codigo_barras = '6001007000053'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 670.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Restaurante Sabores da Baixa' AND v.total = 6420.00 AND pr.codigo_barras = '6001012000027'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO venda (data, total, estado, id_cliente, id_funcionario)
SELECT
    DATE_SUB(NOW(), INTERVAL 2 HOUR),
    945.00,
    'CONCLUIDA',
    cl.id_cliente,
    f.id_funcionario
FROM cliente cl
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN funcionario f
WHERE p.nome = 'Cremilde Augusta Machel' AND f.bi = '110100567845E'
  AND NOT EXISTS (SELECT 1 FROM venda v WHERE v.total = 945.00 AND v.id_cliente = cl.id_cliente);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 3, 110.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Cremilde Augusta Machel' AND v.total = 945.00 AND pr.codigo_barras = '6001006000016'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 2, 195.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Cremilde Augusta Machel' AND v.total = 945.00 AND pr.codigo_barras = '6001014000018'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 225.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Cremilde Augusta Machel' AND v.total = 945.00 AND pr.codigo_barras = '6001011000066'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO venda (data, total, estado, id_cliente, id_funcionario)
SELECT
    DATE_SUB(NOW(), INTERVAL 1 HOUR),
    1185.00,
    'PENDENTE',
    cl.id_cliente,
    f.id_funcionario
FROM cliente cl
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN funcionario f
WHERE p.nome = 'Dércio Vasco Guambe' AND f.bi = '110100345623C'
  AND NOT EXISTS (SELECT 1 FROM venda v WHERE v.total = 1185.00 AND v.id_cliente = cl.id_cliente);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 390.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Dércio Vasco Guambe' AND v.total = 1185.00 AND pr.codigo_barras = '6001001000042'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 460.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Dércio Vasco Guambe' AND v.total = 1185.00 AND pr.codigo_barras = '6001011000028'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 1, 335.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Dércio Vasco Guambe' AND v.total = 1185.00 AND pr.codigo_barras = '6001008000052'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO venda (data, total, estado, id_cliente, id_funcionario)
SELECT
    DATE_SUB(NOW(), INTERVAL 35 MINUTE),
    755.00,
    'PENDENTE',
    cl.id_cliente,
    f.id_funcionario
FROM cliente cl
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN funcionario f
WHERE p.nome = 'Edna Patrícia Langa' AND f.bi = '110100456734D'
  AND NOT EXISTS (SELECT 1 FROM venda v WHERE v.total = 755.00 AND v.id_cliente = cl.id_cliente);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 2, 145.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Edna Patrícia Langa' AND v.total = 755.00 AND pr.codigo_barras = '6001003000019'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 2, 95.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Edna Patrícia Langa' AND v.total = 755.00 AND pr.codigo_barras = '6001004000018'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 5, 55.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Edna Patrícia Langa' AND v.total = 755.00 AND pr.codigo_barras = '6001002000065'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO venda (data, total, estado, id_cliente, id_funcionario)
SELECT
    DATE_SUB(NOW(), INTERVAL 4 DAY),
    540.00,
    'ANULADA',
    cl.id_cliente,
    f.id_funcionario
FROM cliente cl
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN funcionario f
WHERE p.nome = 'Filipe Armando Zandamela' AND f.bi = '110100567845E'
  AND NOT EXISTS (SELECT 1 FROM venda v WHERE v.total = 540.00 AND v.id_cliente = cl.id_cliente);

INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto)
SELECT 4, 135.00, 0.00, v.id_venda, pr.id_produto
FROM venda v
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN pessoa p ON p.id_pessoa = cl.id_pessoa
CROSS JOIN produto pr
WHERE p.nome = 'Filipe Armando Zandamela' AND v.total = 540.00 AND pr.codigo_barras = '6001005000024'
  AND NOT EXISTS (SELECT 1 FROM item_vendido iv WHERE iv.id_venda = v.id_venda AND iv.id_produto = pr.id_produto);

INSERT INTO reembolso (quantidade, valor, motivo, data, id_item, id_funcionario)
SELECT
    1,
    280.00,
    'Embalagem com tampa amassada trocada pelo cliente',
    DATE_SUB(NOW(), INTERVAL 4 DAY),
    iv.id_item,
    f.id_funcionario
FROM item_vendido iv
INNER JOIN venda v ON v.id_venda = iv.id_venda
INNER JOIN produto pr ON pr.id_produto = iv.id_produto
CROSS JOIN funcionario f
WHERE v.total = 2560.00
  AND pr.codigo_barras = '6001003000026'
  AND f.bi = '110100234512B'
  AND NOT EXISTS (SELECT 1 FROM reembolso r WHERE r.id_item = iv.id_item);
