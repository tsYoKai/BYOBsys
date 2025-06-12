package com.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {
    private Connection connection;

    public VendaDAO(Connection connection) {
        this.connection = connection;
    }


    public int inserir(Venda venda) throws SQLException {
        String sqlVenda = "INSERT INTO venda (cpf, id_operador) VALUES (?, ?)";
        String sqlItem = "INSERT INTO item_venda (id_venda, id_prod, item_qtd) VALUES (?, ?, ?)";
        int idVendaGerado = -1;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtVenda = connection.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmtVenda.setString(1, venda.getCpf());
                stmtVenda.setInt(2, venda.getOperador().getIdOp());

                stmtVenda.executeUpdate();

                try (ResultSet generatedKeys = stmtVenda.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idVendaGerado = generatedKeys.getInt(1); 
                        venda.setIdVenda(idVendaGerado); 
                    } else {
                        throw new SQLException("Falha ao obter ID da venda gerado pelo banco de dados.");
                    }
                }
            } 

            try (PreparedStatement stmtItem = connection.prepareStatement(sqlItem)) {
                for (Venda.Item item : venda.getItens()) {
                    stmtItem.setInt(1, idVendaGerado); 
                    stmtItem.setInt(2, item.getProduto().getIdProd());
                    stmtItem.setInt(3, item.getQtd());
                    stmtItem.addBatch(); 
                }
                stmtItem.executeBatch(); 
            }


            connection.commit();
            return idVendaGerado; 
        } catch (SQLException e) {

            connection.rollback();
            System.err.println("Erro durante a transação de venda. Rollback realizado: " + e.getMessage());
            e.printStackTrace();
            throw e; 
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    public Venda buscarPorId(int idVenda) throws SQLException {
        String sqlVenda = "SELECT v.id_venda, v.cpf, v.id_operador, o.op_nome AS nome_operador " +
                            "FROM venda v " +
                            "JOIN operador o ON v.id_operador = o.id_operador " +
                            "WHERE v.id_venda = ?";

        String sqlItens = "SELECT iv.*, p.prod_nome as nome_produto, p.prod_preco " +
                            "FROM item_venda iv " +
                            "JOIN produto p ON iv.id_prod = p.id_prod " +
                            "WHERE iv.id_venda = ?";

        Venda venda = null;
        OperadorDAO operadorDAO = new OperadorDAO(connection);
        ProdutoDAO produtoDAO = new ProdutoDAO(connection);

        try (PreparedStatement stmtVenda = connection.prepareStatement(sqlVenda)) {
            stmtVenda.setInt(1, idVenda);
            ResultSet rsVenda = stmtVenda.executeQuery();

            if (rsVenda.next()) {
                String cpf = rsVenda.getString("cpf");
                int idOperador = rsVenda.getInt("id_operador");
                Operador operador = operadorDAO.buscarPorId(idOperador);
                venda = new Venda(idVenda, cpf, operador); 

                try (PreparedStatement stmtItens = connection.prepareStatement(sqlItens)) {
                    stmtItens.setInt(1, idVenda);
                    ResultSet rsItens = stmtItens.executeQuery();

                    while (rsItens.next()) {
                        int idProduto = rsItens.getInt("id_prod");
                        int quantidade = rsItens.getInt("item_qtd");
                        Produto produto = produtoDAO.getById(idProduto);

                        venda.adicionarProduto(produto, quantidade);
                    }
                }
            }
        }

        return venda;
    }

    public List<Venda> listarTodas() throws SQLException {
        String sql = "SELECT id_venda FROM venda ORDER BY id_venda DESC"; 
        List<Venda> vendas = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idVenda = rs.getInt("id_venda");
                Venda venda = buscarPorId(idVenda);
                if (venda != null) {
                    vendas.add(venda);
                }
            }
        }

        return vendas;
    }
    public boolean atualizar(Venda venda) throws SQLException {
        return false;
    }

    public boolean excluir(int idVenda) throws SQLException {
        String sql = "DELETE FROM venda WHERE id_venda = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public void inserirItemVenda(Venda.Item item, int idVenda) throws SQLException {
        String sql = "INSERT INTO item_venda (id_venda, id_prod, item_qtd) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            stmt.setInt(2, item.getProduto().getIdProd());
            stmt.setInt(3, item.getQtd());
            stmt.executeUpdate();
        }
    }
}