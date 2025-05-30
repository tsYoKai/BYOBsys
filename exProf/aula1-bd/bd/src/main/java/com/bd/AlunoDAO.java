package com.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    // Autentica aluno pelo RA e senha (não usa id_aluno)
    public boolean autenticar(Aluno aluno) {
        String sql = "SELECT * FROM aluno WHERE ra = ? AND senha = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getRa());
            stmt.setString(2, aluno.getSenha());

            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Se encontrou registro, retorna true
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar: " + e.getMessage());
            return false;
        }
    }

    // Retorna a lista completa de alunos do banco
    public List<Aluno> listar() {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT id_aluno, nome, ra, nota, telefone, senha FROM aluno";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Para cada linha, cria um objeto Aluno e adiciona à lista
            while (rs.next()) {
                Aluno aluno = new Aluno(
                        rs.getInt("id_aluno"),
                        rs.getString("nome"),
                        rs.getString("ra"),
                        rs.getDouble("nota"),
                        rs.getString("telefone"),
                        rs.getString("senha")
                );
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }
        return alunos;
    }

    // Insere um novo aluno (id_aluno é auto incrementado no banco)
    public boolean adicionar(Aluno aluno) {
        String sql = "INSERT INTO aluno (nome, ra, nota, telefone, senha) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getRa());
            stmt.setDouble(3, aluno.getNota());
            stmt.setString(4, aluno.getTelefone());
            stmt.setString(5, aluno.getSenha());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar aluno: " + e.getMessage());
            return false;
        }
    }

    // Atualiza os dados de um aluno pelo id_aluno
    public boolean editar(Aluno aluno) {
        String sql = "UPDATE aluno SET nome = ?, ra = ?, nota = ?, telefone = ?, senha = ? WHERE id_aluno = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getRa());
            stmt.setDouble(3, aluno.getNota());
            stmt.setString(4, aluno.getTelefone());
            stmt.setString(5, aluno.getSenha());
            stmt.setInt(6, aluno.getIdAluno());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao editar aluno: " + e.getMessage());
            return false;
        }
    }

    // Exclui um aluno pelo id_aluno
    public boolean excluir(int idAluno) {
        String sql = "DELETE FROM aluno WHERE id_aluno = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAluno);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir aluno: " + e.getMessage());
            return false;
        }
    }
}
