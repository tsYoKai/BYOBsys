package com.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {
    private static String urlBase = "jdbc:mysql://localhost:3306/"; 
    private static String urlComBd = "jdbc:mysql://localhost:3306/byobbd?useTimezone=true&serverTimezone=UTC"; 
    private static String usuario = "root";
    private static String senha = "usjt"; 
    private static boolean schemaInitialized = false;

    public static Connection getConnection() {
        if (!schemaInitialized) {
            initializeDatabaseSchema();
            schemaInitialized = true;
        }

        try {
            Connection conexao = DriverManager.getConnection(urlComBd, usuario, senha);
            System.out.println("Conexão estabelecida com sucesso com byobbd.");
            return conexao;
        } catch (SQLException e) {
            System.err.println("Erro ao estabelecer conexão com byobbd: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void initializeDatabaseSchema() {
        System.out.println("Verificando e inicializando esquema do banco de dados...");
        try (Connection conn = DriverManager.getConnection(urlBase, usuario, senha);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = conn.getMetaData().getCatalogs();
            boolean dbExists = false;
            while (rs.next()) {
                String databaseName = rs.getString(1);
                if (databaseName.equalsIgnoreCase("byobbd")) {
                    dbExists = true;
                    break;
                }
            }
            rs.close();

            if (!dbExists) {
                System.out.println("Banco de dados 'byobbd' não encontrado. Criando...");
                stmt.executeUpdate("CREATE DATABASE byobbd");
                System.out.println("Banco de dados 'byobbd' criado.");
            } else {
                System.out.println("Banco de dados 'byobbd' já existe.");
            }

            try (Connection bdConn = DriverManager.getConnection(urlComBd, usuario, senha);
                 Statement bdStmt = bdConn.createStatement()) {

                if (!tableExists(bdConn, "operador")) {
                    System.out.println("Tabela 'operador' não encontrada. Criando...");
                    bdStmt.executeUpdate("CREATE TABLE operador (" +
                                        "id_operador INT PRIMARY KEY NOT NULL," +
                                        "op_nome VARCHAR(15)" +
                                        ")");
                    System.out.println("Inserindo operador 'adm'...");
                    bdStmt.executeUpdate("INSERT INTO operador (id_operador, op_nome) VALUES (123, 'adm')");
                    System.out.println("Tabela 'operador' criada e 'adm' inserido.");
                } else {
                    System.out.println("Tabela 'operador' já existe.");
                }
                if (!tableExists(bdConn, "venda")) {
                    System.out.println("Tabela 'venda' não encontrada. Criando...");
                    bdStmt.executeUpdate("CREATE TABLE venda (" +
                                        "id_venda INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                                        "cpf VARCHAR(20)," +
                                        "id_operador INT," +
                                        "FOREIGN KEY (id_operador) REFERENCES operador(id_operador)" +
                                        ")");
                    System.out.println("Tabela 'venda' criada.");
                } else {
                    System.out.println("Tabela 'venda' já existe.");
                }
                if (!tableExists(bdConn, "produto")) {
                    System.out.println("Tabela 'produto' não encontrada. Criando...");
                    bdStmt.executeUpdate("CREATE TABLE produto (" +
                                        "id_prod INT PRIMARY KEY NOT NULL," +
                                        "prod_nome VARCHAR(15)," +
                                        "prod_preco DOUBLE" +
                                        ")");
                    System.out.println("Tabela 'produto' criada.");
                } else {
                    System.out.println("Tabela 'produto' já existe.");
                }
                if (!tableExists(bdConn, "item_venda")) {
                    System.out.println("Tabela 'item_venda' não encontrada. Criando...");
                    bdStmt.executeUpdate("CREATE TABLE item_venda (" +
                                        "id_item INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                                        "id_venda INT," +
                                        "id_prod INT," +
                                        "item_preco DOUBLE," +
                                        "item_qtd INT," +
                                        "FOREIGN KEY (id_venda) REFERENCES venda(id_venda) ON DELETE CASCADE," + 
                                        "FOREIGN KEY (id_prod) REFERENCES produto(id_prod)" +
                                        ")");
                    System.out.println("Tabela 'item_venda' criada com ON DELETE CASCADE.");
                } else {
                    System.out.println("Tabela 'item_venda' já existe.");
                }

            } 
        } catch (SQLException e) {
            System.err.println("Erro na inicialização do esquema do banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet tables = conn.getMetaData().getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexão fechada com sucesso.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
