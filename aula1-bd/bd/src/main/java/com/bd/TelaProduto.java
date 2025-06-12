package com.bd;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TelaProduto extends JFrame {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabelaProdutos;
    private ProdutoDAO produtoDAO;
    private Connection connection; // Adicione esta conexão para passar para o ProdutoDAO

    public TelaProduto(Connection connection) {
        this.connection = connection; // Armazena a conexão
        this.produtoDAO = new ProdutoDAO(connection); // Inicializa o DAO com a conexão

        setTitle("Gerenciar Produtos");
        setSize(800, 500);
        setLocationRelativeTo(null); // Centraliza a janela
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Fecha apenas esta janela, não o aplicativo

        // --- Configuração da Tabela de Produtos ---
        String[] colunasProdutos = {"ID", "Nome do Produto", "Preço (R$)"};
        modeloTabelaProdutos = new DefaultTableModel(null, colunasProdutos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis diretamente na tabela
            }
        };
        tabelaProdutos = new JTable(modeloTabelaProdutos);
        tabelaProdutos.setAutoCreateRowSorter(true); // Permite ordenar a tabela
        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);

        // --- Configuração dos Botões ---
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(4, 1, 10, 10)); // 4 linhas, 1 coluna, com espaçamento

        JButton btnAdicionar = new JButton("Adicionar Produto");
        JButton btnAlterar = new JButton("Alterar Produto");
        JButton btnExcluir = new JButton("Excluir Produto");
        JButton btnAtualizar = new JButton("Atualizar Lista");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);

        // --- Layout da Janela ---
        setLayout(new BorderLayout(10, 10)); // Espaçamento entre os componentes
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.EAST);

        // --- Ações dos Botões ---
        btnAdicionar.addActionListener(e -> abrirDialogoProduto(null)); // null para adicionar novo
        btnAlterar.addActionListener(e -> {
            int linhaSelecionada = tabelaProdutos.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um produto para alterar.");
                return;
            }
            // Converte o índice da linha da visão para o índice do modelo, importante para ordenação
            int idProd = (int) modeloTabelaProdutos.getValueAt(tabelaProdutos.convertRowIndexToModel(linhaSelecionada), 0);
            try {
                Produto produto = produtoDAO.getById(idProd);
                if (produto != null) {
                    abrirDialogoProduto(produto); // Passa o produto existente para o diálogo
                } else {
                    JOptionPane.showMessageDialog(this, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao buscar produto: " + ex.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        btnExcluir.addActionListener(e -> {
            int linhaSelecionada = tabelaProdutos.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
                return;
            }
            int idProd = (int) modeloTabelaProdutos.getValueAt(tabelaProdutos.convertRowIndexToModel(linhaSelecionada), 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Confirma exclusão do Produto ID " + idProd + "?", "Excluir Produto", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (produtoDAO.delete(idProd)) {
                        JOptionPane.showMessageDialog(this, "Produto excluído com sucesso.");
                        atualizarTabelaProdutos(); // Atualiza a tabela após a exclusão
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao excluir produto. Pode haver vendas associadas.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    // Cuidado com exclusão de produtos que estão em vendas (FK constraint)
                    JOptionPane.showMessageDialog(this, "Erro de banco de dados ao excluir produto: " + ex.getMessage() + "\nVerifique se o produto está associado a alguma venda.", "Erro de SQL", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        btnAtualizar.addActionListener(e -> atualizarTabelaProdutos());

        // Carrega os produtos na tabela ao iniciar a tela
        atualizarTabelaProdutos();
    }

    private void atualizarTabelaProdutos() {
        modeloTabelaProdutos.setRowCount(0); // Limpa todas as linhas existentes
        try {
            List<Produto> produtos = produtoDAO.getAll(); // Método para obter todos os produtos
            for (Produto p : produtos) {
                modeloTabelaProdutos.addRow(new Object[]{p.getIdProd(), p.getNome(), String.format("%.2f", p.getPreco())});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void abrirDialogoProduto(Produto produto) {
        JTextField campoId = new JTextField(10);
        JTextField campoNome = new JTextField(20);
        JTextField campoPreco = new JTextField(10);
    
        // Se estiver alterando, preenche os campos com os dados do produto existente
        if (produto != null) {
            campoId.setText(String.valueOf(produto.getIdProd()));
            campoId.setEditable(false); // ID não pode ser alterado
            campoNome.setText(produto.getNome());
            campoPreco.setText(String.format("%.2f", produto.getPreco()));
        } else {
            campoId.setEditable(true); // ID pode ser editado para nova inserção
        }
    
        JPanel painelDialogo = new JPanel(new GridLayout(0, 2, 5, 5));
        painelDialogo.add(new JLabel("ID do Produto:"));
        painelDialogo.add(campoId);
        painelDialogo.add(new JLabel("Nome do Produto:"));
        painelDialogo.add(campoNome);
        painelDialogo.add(new JLabel("Preço:"));
        painelDialogo.add(campoPreco);
    
        int option = JOptionPane.showConfirmDialog(this, painelDialogo,
                produto == null ? "Adicionar Novo Produto" : "Alterar Produto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        if (option == JOptionPane.OK_OPTION) {
            try {
                int idProd = Integer.parseInt(campoId.getText());
                String nome = campoNome.getText().trim();
                double preco = Double.parseDouble(campoPreco.getText().replace(",", ".")); // Aceita ',' como separador decimal
    
                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nome do produto não pode ser vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (preco <= 0) {
                    JOptionPane.showMessageDialog(this, "Preço deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                Produto novoOuAlteradoProd = new Produto(idProd, nome, preco);
    
                if (produto == null) { // Adicionar
                    produtoDAO.save(novoOuAlteradoProd, false); // false = não é atualização
                    JOptionPane.showMessageDialog(this, "Produto adicionado com sucesso!");
                } else { // Alterar
                    produtoDAO.save(novoOuAlteradoProd, true); // true = é atualização
                    JOptionPane.showMessageDialog(this, "Produto alterado com sucesso!");
                }
                atualizarTabelaProdutos(); // Atualiza a tabela após a operação
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID ou Preço inválido. Certifique-se de usar números válidos.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro de banco de dados: " + ex.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
}