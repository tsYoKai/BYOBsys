CREATE DATABASE IF NOT EXISTS byobbd;
USE byobbd;
CREATE TABLE IF NOT EXISTS operador(
id_operador INT PRIMARY KEY NOT NULL,
op_nome VARCHAR(15)
);
CREATE TABLE IF NOT EXISTS venda(
id_venda INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
ven_cpf VARCHAR(20),
id_operador INT,
FOREIGN KEY (id_operador) REFERENCES operador(id_operador)
);
CREATE TABLE IF NOT EXISTS produto(
id_prod INT PRIMARY KEY NOT NULL,
prod_nome VARCHAR(15),
prod_preco DOUBLE
);
CREATE TABLE IF NOT EXISTS vendaitem(
id_item INT PRIMARY KEY NOT NULL,
id_venda INT,
id_prod INT,
item_preco DOUBLE,
item_qtd INT,
FOREIGN KEY (id_venda) REFERENCES venda(id_venda),
FOREIGN KEY (id_prod) REFERENCES produto(id_prod)
);