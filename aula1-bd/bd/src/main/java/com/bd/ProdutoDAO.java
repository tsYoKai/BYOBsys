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
                rs.getString("nome"),
                rs.getDouble("preco")
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
                rs.getString("nome"),
                rs.getDouble("preco")
            );
            produtos.add(produto);
        }
 
        rs.close();
        stmt.close();
        return produtos;
    }
}