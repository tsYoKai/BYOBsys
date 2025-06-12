package com.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class ProdutoDAO {
 
    private Connection connection;
 
    public ProdutoDAO(Connection connection) {
        this.connection = connection;
    }

    // Método save unificado para inserção e atualização
    public void save(Produto produto, boolean atualizacao) throws SQLException {
        String sql;
        PreparedStatement stmt = null; // Inicialize stmt fora do try-with-resources se for usar no finally
        try {
            if (atualizacao){
                sql = "UPDATE produto SET prod_nome = ?, prod_preco = ? WHERE id_prod = ?";
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, produto.getNome());
                stmt.setDouble(2, produto.getPreco());
                stmt.setInt(3, produto.getIdProd()); // ID é o último parâmetro para UPDATE
            } else {
                sql = "INSERT INTO produto (id_prod, prod_nome, prod_preco) VALUES (?, ?, ?)";
                stmt = connection.prepareStatement(sql);
                stmt.setInt(1, produto.getIdProd());
                stmt.setString(2, produto.getNome());
                stmt.setDouble(3, produto.getPreco());
            }
            stmt.executeUpdate(); // Use executeUpdate para INSERT, UPDATE, DELETE
        } finally { // Use finally para garantir que o stmt seja fechado
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    // --- Métodos de CRUD individuais (descomente ou mantenha o save) ---
    /*
    public void insert(Produto produto) throws SQLException {
        String sql = "INSERT INTO produto (id_prod, prod_nome, prod_preco) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, produto.getIdProd());
            stmt.setString(2, produto.getNome());
            stmt.setDouble(3, produto.getPreco());
            stmt.executeUpdate();
        }
    }

    public void update(Produto produto) throws SQLException {
        String sql = "UPDATE produto SET prod_nome = ?, prod_preco = ? WHERE id_prod = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getIdProd());
            stmt.executeUpdate();
        }
    }
    */
 
    public boolean delete(int idProd) throws SQLException { // Mudei para boolean para indicar sucesso
        String sql = "DELETE FROM produto WHERE id_prod=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idProd);
            int affectedRows = stmt.executeUpdate(); // Use executeUpdate() para DELETE
            return affectedRows > 0; // Retorna true se alguma linha foi afetada
        }
    }
 
    // --- MÉTODO getById CORRIGIDO ---
    public Produto getById(int idProd) throws SQLException {
        String sql = "SELECT id_prod, prod_nome, prod_preco FROM produto WHERE id_prod = ?"; // Colunas corretas
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idProd); // <<< Definir o parâmetro ANTES de executar a query
            try (ResultSet rs = stmt.executeQuery()) { // <<< Executar a query AQUI
                if (rs.next()) {
                    return new Produto(
                        rs.getInt("id_prod"),
                        rs.getString("prod_nome"), // Coluna correta
                        rs.getDouble("prod_preco") // Coluna correta
                    );
                }
            }
        }
        return null;
    }
 
    public List<Produto> getAll() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id_prod, prod_nome, prod_preco FROM produto"; // Colunas corretas
        try (Statement stmt = connection.createStatement(); // Use Statement para queries sem parâmetros
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = new Produto(
                    rs.getInt("id_prod"),
                    rs.getString("prod_nome"), // Coluna correta
                    rs.getDouble("prod_preco") // Coluna correta
                );
                produtos.add(produto);
            }
        }
        return produtos;
    }
}
