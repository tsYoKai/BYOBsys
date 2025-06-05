package com.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OperadorDAO {
    
    public boolean logar(Operador operador) {
        String sql = "SELECT * FROM operador WHERE id_operador = ? AND op_nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, operador.getIdOp());
            stmt.setString(2, operador.getNome());

            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Se encontrou registro, retorna true
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar: " + e.getMessage());
            return false;
        }
    }


}

