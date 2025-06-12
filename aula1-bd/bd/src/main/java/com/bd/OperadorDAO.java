package com.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OperadorDAO {
    private Connection connection;
    public OperadorDAO(Connection connection) {
        this.connection = connection;
    }

        public Operador logar(Operador operador) { 
    String sql = "SELECT id_operador, op_nome FROM operador WHERE id_operador = ? AND op_nome = ?"; 

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, operador.getIdOp());
        stmt.setString(2, operador.getNome());

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int idOp = rs.getInt("id_operador"); 
                String nome = rs.getString("op_nome");
                
                return new Operador(idOp, nome); 
            } else {
                return null; 
            }
        }
    } catch (SQLException e) {
        System.out.println("Erro ao autenticar: " + e.getMessage());
        return null;
    }


}


    public Operador buscarPorId(int idOperador) {
        Operador operador = null;
        String sql = "SELECT id_operador, op_nome FROM operador WHERE id_operador = ?"; 

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idOperador); 

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    
                    operador = new Operador();
                    operador.setIdOp(rs.getInt("id_operador"));
                    operador.setNome(rs.getString("op_nome"));
                    
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar operador por ID: " + e.getMessage());
        }
        return operador;
    }



}

