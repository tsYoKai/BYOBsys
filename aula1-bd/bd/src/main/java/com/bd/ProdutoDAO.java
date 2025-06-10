package com.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class ProdutoDAO {
 
    private Connection connection;
 
    public ProdutoDAO(Connection connection) {
        this.connection = connection;
    }
    public void save(Produto produto, boolean atualizacao) throws SQLException {
        String sql;
        if (atualizacao){
             sql = "UPDATE produto SET nome=?, preco=? WHERE id_prod=?";
        }else{
             sql = "INSERT INTO produto (id_prod, nome, preco) VALUES (?, ?, ?)";
        }
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, produto.getIdProd());
        stmt.setString(2, produto.getNome());
        stmt.setDouble(3, produto.getPreco());
        stmt.execute();
        stmt.close();
    }
/*
    public void insert(Produto produto) throws SQLException {
        String sql = "INSERT INTO produto (id_prod, nome, preco) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, produto.getIdProd());
        stmt.setString(2, produto.getNome());
        stmt.setDouble(3, produto.getPreco());
        stmt.execute();
        stmt.close();
    }
 
    public void update(Produto produto) throws SQLException {
        String sql = "UPDATE produto SET nome=?, preco=? WHERE id_prod=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, produto.getNome());
        stmt.setDouble(2, produto.getPreco());
        stmt.setInt(3, produto.getIdProd());
        stmt.execute();
        stmt.close();
    }
 */
    public void delete(int idProd) throws SQLException {
        String sql = "DELETE FROM produto WHERE id_prod=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, idProd);
        stmt.execute();
        stmt.close();
    }
 
    public Produto getById(int idProd) throws SQLException {
        String sql = "SELECT * FROM produto WHERE id_prod=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, idProd);
        ResultSet rs = stmt.executeQuery();
 
        Produto produto = null;
        if (rs.next()) {
            produto = new Produto(
                rs.getInt("id_prod"),
                rs.getString("prod_nome"),
                rs.getDouble("prod_preco")
            );
        }
        rs.close();
        stmt.close();
        return produto;
    }
 
    public List<Produto> getAll() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto";
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
 
        while (rs.next()) {
            Produto produto = new Produto(
                rs.getInt("id_prod"),
                rs.getString("prod_nome"),
                rs.getDouble("prod_preco")
            );
            produtos.add(produto);
        }
 
        rs.close();
        stmt.close();
        return produtos;
    }
/*
    public Produto buscarPorId(int idProduto) {
        Produto produto = null;
        String sql = "SELECT id, nome, preco, descricao FROM produtos WHERE id = ?"; // Ajuste os nomes das colunas conforme seu BD

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idProduto); // Define o valor do parâmetro 'id' na query

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Se houver um resultado, preenche o objeto Produto
                    produto = new Produto();
                    produto.setId(rs.getInt("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setPreco(rs.getDouble("preco")); // Ou float, dependendo do seu tipo de dado
                    produto.setDescricao(rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por ID: " + e.getMessage());
            // Você pode adicionar um log ou relançar uma exceção personalizada aqui
        }
        return produto;
    }*/
    
}