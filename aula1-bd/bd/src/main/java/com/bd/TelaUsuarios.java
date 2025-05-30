package com.bd;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TelaUsuarios extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private AlunoDAO dao;

    public TelaUsuarios() {
        setTitle("Lista de Alunos");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        dao = new AlunoDAO();

        // Definindo colunas da tabela
        String[] colunas = {"ID", "Nome", "RA", "Nota", "Telefone", "Senha"};
        modeloTabela = new DefaultTableModel(null, colunas) {
            // Evita edição direta na tabela
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabela);

        // Painel dos botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(4, 1, 5, 5));

        JButton botaoAdicionar = new JButton("Adicionar");
        JButton botaoEditar = new JButton("Editar");
        JButton botaoExcluir = new JButton("Excluir");
        JButton botaoAtualizar = new JButton("Atualizar");

        painelBotoes.add(botaoAdicionar);
        painelBotoes.add(botaoEditar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoAtualizar);

        // Layout principal
        setLayout(new BorderLayout(10, 10));
        add(scroll, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.EAST);

        // Carrega dados inicialmente
        atualizarTabela();

        // Ações dos botões
        botaoAdicionar.addActionListener(e -> abrirDialogoAluno(null));

        botaoEditar.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um aluno para editar.");
                return;
            }
            Aluno alunoSelecionado = getAlunoDaLinha(linhaSelecionada);
            abrirDialogoAluno(alunoSelecionado);
        });

        botaoExcluir.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um aluno para excluir.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Confirma exclusão?", "Excluir", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Aluno alunoSelecionado = getAlunoDaLinha(linhaSelecionada);
                if (dao.excluir(alunoSelecionado.getIdAluno())) {
                    JOptionPane.showMessageDialog(this, "Aluno excluído com sucesso.");
                    atualizarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir aluno.");
                }
            }
        });

        botaoAtualizar.addActionListener(e -> atualizarTabela());

        setVisible(true);
    }

    // Atualiza a tabela puxando dados do banco
    private void atualizarTabela() {
        List<Aluno> alunos = dao.listar();
        modeloTabela.setRowCount(0); // limpa linhas

        for (Aluno aluno : alunos) {
            Object[] linha = {
                    aluno.getIdAluno(),
                    aluno.getNome(),
                    aluno.getRa(),
                    aluno.getNota(),
                    aluno.getTelefone(),
                    aluno.getSenha()
            };
            modeloTabela.addRow(linha);
        }
    }

    // Retorna um objeto Aluno da linha selecionada da tabela
    private Aluno getAlunoDaLinha(int linha) {
        int id = (int) modeloTabela.getValueAt(linha, 0);
        String nome = (String) modeloTabela.getValueAt(linha, 1);
        String ra = (String) modeloTabela.getValueAt(linha, 2);
        double nota = (double) modeloTabela.getValueAt(linha, 3);
        String telefone = (String) modeloTabela.getValueAt(linha, 4);
        String senha = (String) modeloTabela.getValueAt(linha, 5);

        return new Aluno(id, nome, ra, nota, telefone, senha);
    }

    // Dialogo para adicionar ou editar aluno
    private void abrirDialogoAluno(Aluno aluno) {
        // Se aluno for null → adicionar; senão editar
        boolean editar = aluno != null;

        JTextField campoNome = new JTextField();
        JTextField campoRa = new JTextField();
        JTextField campoNota = new JTextField();
        JTextField campoTelefone = new JTextField();
        JTextField campoSenha = new JTextField();

        if (editar) {
            campoNome.setText(aluno.getNome());
            campoRa.setText(aluno.getRa());
            campoNota.setText(String.valueOf(aluno.getNota()));
            campoTelefone.setText(aluno.getTelefone());
            campoSenha.setText(aluno.getSenha());
        }

        Object[] campos = {
                "Nome:", campoNome,
                "RA:", campoRa,
                "Nota:", campoNota,
                "Telefone:", campoTelefone,
                "Senha:", campoSenha
        };

        int opcao = JOptionPane.showConfirmDialog(this, campos, editar ? "Editar Aluno" : "Adicionar Aluno", JOptionPane.OK_CANCEL_OPTION);

        if (opcao == JOptionPane.OK_OPTION) {
            try {
                String nome = campoNome.getText();
                String ra = campoRa.getText();
                double nota = Double.parseDouble(campoNota.getText());
                String telefone = campoTelefone.getText();
                String senha = campoSenha.getText();

                if (nome.isEmpty() || ra.isEmpty() || telefone.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
                    return;
                }

                if (editar) {
                    Aluno alunoEditado = new Aluno(aluno.getIdAluno(), nome, ra, nota, telefone, senha);
                    if (dao.editar(alunoEditado)) {
                        JOptionPane.showMessageDialog(this, "Aluno atualizado com sucesso.");
                        atualizarTabela();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar aluno.");
                    }
                } else {
                    Aluno novoAluno = new Aluno(nome, ra, nota, telefone, senha);
                    if (dao.adicionar(novoAluno)) {
                        JOptionPane.showMessageDialog(this, "Aluno adicionado com sucesso.");
                        atualizarTabela();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao adicionar aluno.");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nota deve ser um número válido.");
            }
        }
    }

    public static void main(String[] args) {
        // Ajusta Look and Feel para sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(TelaUsuarios::new);
    }
}
