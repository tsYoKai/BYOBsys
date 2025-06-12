package com.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {
    private static String urlBase = "jdbc:mysql://localhost:3306/"; // URL sem o nome do BD
    private static String urlComBd = "jdbc:mysql://localhost:3306/byobbd?useTimezone=true&serverTimezone=UTC"; // URL com o nome do BD
    private static String usuario = "root";
    private static String senha = "usjt"; 
    
    // Flag para controlar se o esquema já foi verificado/criado
    private static boolean schemaInitialized = false;

    public static Connection getConnection() {
        if (!schemaInitialized) {
            // Tenta inicializar o esquema do banco de dados
            initializeDatabaseSchema();
            schemaInitialized = true; // Marca como inicializado para não rodar de novo
        }

        try {
            // Tenta obter a conexão com o banco de dados 'byobbd'
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

            // 1. Verificar e criar o banco de dados 'byobbd'
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

            // 2. Conectar ao banco de dados 'byobbd' para criar tabelas
            try (Connection bdConn = DriverManager.getConnection(urlComBd, usuario, senha);
                 Statement bdStmt = bdConn.createStatement()) {

                // Verificar e criar a tabela 'operador'
                if (!tableExists(bdConn, "operador")) {
                    System.out.println("Tabela 'operador' não encontrada. Criando...");
                    bdStmt.executeUpdate("CREATE TABLE operador (" +
                                        "id_operador INT PRIMARY KEY NOT NULL," +
                                        "op_nome VARCHAR(15)" +
                                        ")");
                    // Inserir o operador 'adm' após a criação da tabela
                    System.out.println("Inserindo operador 'adm'...");
                    bdStmt.executeUpdate("INSERT INTO operador (id_operador, op_nome) VALUES (123, 'adm')");
                    System.out.println("Tabela 'operador' criada e 'adm' inserido.");
                } else {
                    System.out.println("Tabela 'operador' já existe.");
                    // Opcional: Garantir que o operador 'adm' existe se a tabela já existia
                    // checkAndInsertAdminOperator(bdConn);
                }


                // Verificar e criar a tabela 'venda'
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

                // Verificar e criar a tabela 'produto'
                if (!tableExists(bdConn, "produto")) {
                    System.out.println("Tabela 'produto' não encontrada. Criando...");
                    bdStmt.executeUpdate("CREATE TABLE produto (" +
                                        "id_prod INT PRIMARY KEY NOT NULL," + // Manter sem AUTO_INCREMENT se usuário digita
                                        "prod_nome VARCHAR(15)," +
                                        "prod_preco DOUBLE" +
                                        ")");
                    System.out.println("Tabela 'produto' criada.");
                } else {
                    System.out.println("Tabela 'produto' já existe.");
                }

                // Verificar e criar a tabela 'item_venda'
                if (!tableExists(bdConn, "item_venda")) {
                    System.out.println("Tabela 'item_venda' não encontrada. Criando...");
                    bdStmt.executeUpdate("CREATE TABLE item_venda (" +
                                        "id_item INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                                        "id_venda INT," +
                                        "id_prod INT," +
                                        "item_preco DOUBLE," +
                                        "item_qtd INT," +
                                        "FOREIGN KEY (id_venda) REFERENCES venda(id_venda) ON DELETE CASCADE," + // ON DELETE CASCADE
                                        "FOREIGN KEY (id_prod) REFERENCES produto(id_prod)" +
                                        ")");
                    System.out.println("Tabela 'item_venda' criada com ON DELETE CASCADE.");
                } else {
                    System.out.println("Tabela 'item_venda' já existe.");
                }

            } // bdConn e bdStmt são fechados automaticamente
        } catch (SQLException e) {
            System.err.println("Erro na inicialização do esquema do banco de dados: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro na inicialização do esquema, pode ser necessário sair ou tratar mais severamente
            // Por exemplo, throw new RuntimeException("Falha crítica ao inicializar o banco de dados", e);
        }
    }

    // Método auxiliar para verificar se uma tabela existe
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet tables = conn.getMetaData().getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }

    // Método auxiliar para verificar e inserir operador 'adm' se necessário (opcional)
    /*
    private static void checkAndInsertAdminOperator(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM operador WHERE id_operador = 123";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Operador 'adm' (ID 123) não encontrado. Inserindo...");
                conn.createStatement().executeUpdate("INSERT INTO operador (id_operador, op_nome) VALUES (123, 'adm')");
            }
        }
    }
    */

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
