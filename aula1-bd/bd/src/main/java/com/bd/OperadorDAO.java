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

        public Operador logar(Operador operador) { // MUDANÇA AQUI: Tipo de retorno agora é 'Operador'
    // Adapte esta query para as colunas reais da sua tabela, se forem diferentes de id_operador e op_nome
    String sql = "SELECT id_operador, op_nome FROM operador WHERE id_operador = ? AND op_nome = ?"; 

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, operador.getIdOp());
        stmt.setString(2, operador.getNome());

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                // Se encontrou um registro, significa que o login foi bem-sucedido.
                // Agora, vamos extrair os dados do ResultSet para criar um novo objeto Operador
                // com as informações do banco de dados (que podem ser mais completas ou confirmadas).
                int idOp = rs.getInt("id_operador"); // Use o nome da coluna correto do seu BD
                String nome = rs.getString("op_nome"); // Use o nome da coluna correto do seu BD
                
                // Retorne o objeto Operador completo.
                return new Operador(idOp, nome); // MUDANÇA CRUCIAL: Retornar o objeto!
            } else {
                // Se não encontrou nenhum registro, as credenciais estão incorretas.
                // Retorne null para indicar falha no login.
                return null; // MUDANÇA CRUCIAL: Retornar null em caso de falha!
            }
        }
    } catch (SQLException e) {
        System.out.println("Erro ao autenticar: " + e.getMessage());
        // Em caso de erro de SQL, também retorne null e imprima o erro para depuração.
        return null; // MUDANÇA CRUCIAL: Retornar null em caso de exceção!
    }


}


    public Operador buscarPorId(int idOperador) {
        Operador operador = null;
        String sql = "SELECT id_operador, op_nome FROM operador WHERE id_operador = ?"; // Ajuste os nomes das colunas conforme seu BD

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idOperador); // Define o valor do parâmetro 'id' na query

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Se houver um resultado, preenche o objeto Operador
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

