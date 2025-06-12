package com.bd;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TelaVenda extends JFrame {

    private JTable tabelaVendas;
    private DefaultTableModel modeloTabelaVendas;
    private VendaDAO vendaDAO;
    private Connection connection;
    private Operador operadorLogado; 

    private ProdutoDAO produtoDAO;

    public TelaVenda(Connection connection, Operador operadorLogado) {

        this.connection = connection;
        this.operadorLogado = operadorLogado;
        
        setTitle("Lista de Vendas - Operador: " + operadorLogado.getNome() + " (ID: " + operadorLogado.getIdOp() + ")");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        vendaDAO = new VendaDAO(connection);
        produtoDAO = new ProdutoDAO(connection); // Inicializa ProdutoDAO

        String[] colunasVendas = {"ID Venda", "CPF Cliente", "ID Operador", "Nome Operador", "Total Venda (R$)"};

        modeloTabelaVendas = new DefaultTableModel(null, colunasVendas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaVendas = new JTable(modeloTabelaVendas);
        tabelaVendas.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(tabelaVendas);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(5, 1, 5, 5));

        JButton botaoAdicionar = new JButton("Nova Venda");
        JButton botaoDetalhes = new JButton("Ver Detalhes");
        JButton botaoExcluir = new JButton("Excluir Venda");
        JButton botaoAtualizar = new JButton("Atualizar Tabela");
        JButton botaoProdutos = new JButton("Gerenciar Produtos");

        painelBotoes.add(botaoAdicionar);
        painelBotoes.add(botaoDetalhes);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoProdutos);

        setLayout(new BorderLayout(10, 10));
        add(scroll, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.EAST);

        atualizarTabelaVendas();

        botaoAdicionar.addActionListener(e -> abrirDialogoNovaVenda());

        botaoDetalhes.addActionListener(e -> {
            int linhaSelecionada = tabelaVendas.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecione uma venda para ver os detalhes.");
                return;
            }
            int idVenda = (int) modeloTabelaVendas.getValueAt(tabelaVendas.convertRowIndexToModel(linhaSelecionada), 0);
            exibirDetalhesVenda(idVenda);
        });

        botaoExcluir.addActionListener(e -> {
            int linhaSelecionada = tabelaVendas.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecione uma venda para excluir.");
                return;
            }
            int idVenda = (int) modeloTabelaVendas.getValueAt(tabelaVendas.convertRowIndexToModel(linhaSelecionada), 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Confirma exclusão da Venda ID " + idVenda + "? Isso também excluirá os itens relacionados.", "Excluir Venda", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (vendaDAO.excluir(idVenda)) {
                        JOptionPane.showMessageDialog(this, "Venda excluída com sucesso.");
                        atualizarTabelaVendas();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao excluir venda.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro de banco de dados ao excluir venda: " + ex.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        botaoAtualizar.addActionListener(e -> atualizarTabelaVendas());

        botaoProdutos.addActionListener(e -> {
            TelaProduto telaProduto = new TelaProduto(connection);
            telaProduto.setVisible(true);
        });

        setVisible(true);
    }

    private void atualizarTabelaVendas() {
        try {
            List<Venda> vendas = vendaDAO.listarTodas();
            modeloTabelaVendas.setRowCount(0); // limpa linhas

            for (Venda venda : vendas) {
                double totalVenda = 0.0;
                if (venda.getItens() != null) {
                    for (Venda.Item item : venda.getItens()) {
                        if (item != null && item.getProduto() != null) {
                            totalVenda += item.getQtd() * item.getProduto().getPreco();
                        }
                    }
                }

                Object[] linha = {
                    venda.getIdVenda(),
                    venda.getCpf(),
                    venda.getOperador() != null ? venda.getOperador().getIdOp() : "N/A",
                    venda.getOperador() != null ? venda.getOperador().getNome() : "N/A",
                    String.format("%.2f", totalVenda)
                };
                modeloTabelaVendas.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas: " + e.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void exibirDetalhesVenda(int idVenda) {
        try {
            Venda venda = vendaDAO.buscarPorId(idVenda);
            if (venda == null) {
                JOptionPane.showMessageDialog(this, "Venda não encontrada.", "Detalhes da Venda", JOptionPane.WARNING_MESSAGE);
                return;
            }

            StringBuilder detalhes = new StringBuilder();
            detalhes.append("ID da Venda: ").append(venda.getIdVenda()).append("\n");
            detalhes.append("CPF do Cliente: ").append(venda.getCpf()).append("\n");
            detalhes.append("Operador (ID): ").append(venda.getOperador() != null ? venda.getOperador().getIdOp() : "N/A").append("\n");
            detalhes.append("Operador (Nome): ").append(venda.getOperador() != null ? venda.getOperador().getNome() : "N/A").append("\n\n");
            detalhes.append("Itens da Venda:\n");

            DefaultTableModel modeloItens = new DefaultTableModel(new Object[]{"Produto", "Quantidade", "Preço Unit.", "Subtotal"}, 0);
            double totalGeral = 0.0;

            if (venda.getItens().isEmpty()) {
                detalhes.append(" Nenhum item nesta venda.\n");
            } else {
                for (Venda.Item item : venda.getItens()) {
                    String nomeProduto = item.getProduto() != null ? item.getProduto().getNome() : "Produto Desconhecido";
                    double precoUnitario = item.getProduto() != null ? item.getProduto().getPreco() : 0.0;
                    double subtotal = item.getQtd() * precoUnitario;
                    totalGeral += subtotal;

                    modeloItens.addRow(new Object[]{
                        nomeProduto,
                        item.getQtd(),
                        String.format("%.2f", precoUnitario),
                        String.format("%.2f", subtotal)
                    });
                }
            }
            detalhes.append("\nTotal Geral: R$ ").append(String.format("%.2f", totalGeral));

            JTable tabelaItens = new JTable(modeloItens);
            tabelaItens.setEnabled(false);
            JScrollPane scrollItens = new JScrollPane(tabelaItens);
            scrollItens.setPreferredSize(new Dimension(400, 200));

            JPanel painelDetalhes = new JPanel(new BorderLayout(5, 5));
            JTextArea textAreaDetalhes = new JTextArea(detalhes.toString());
            textAreaDetalhes.setEditable(false);
            painelDetalhes.add(textAreaDetalhes, BorderLayout.NORTH);
            painelDetalhes.add(scrollItens, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(this, painelDetalhes, "Detalhes da Venda ID: " + idVenda, JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar detalhes da venda: " + e.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void abrirDialogoNovaVenda() {
        JTextField campoCpf = new JTextField(15);

        // --- Componentes para adicionar produtos (AGORA POR ID) ---
        JTextField campoIdProduto = new JTextField(10); // Campo para digitar o ID do produto
        JButton btnBuscarProduto = new JButton("Buscar Produto"); // Botão para buscar o produto
        JLabel labelNomeProduto = new JLabel("Nome: "); // Exibe o nome do produto encontrado
        JLabel labelPrecoProduto = new JLabel("Preço: "); // Exibe o preço do produto encontrado
        
        // Variável temporária para armazenar o produto selecionado/encontrado
        final Produto[] produtoEncontrado = {null}; // Array para permitir modificação dentro do lambda

        JTextField campoQuantidade = new JTextField("1", 5); // Default 1
        JButton btnAdicionarItem = new JButton("Adicionar Produto à Venda");

        // Modelo para a tabela de itens que serão adicionados à venda
        DefaultTableModel modeloItensVendaEmConstrucao = new DefaultTableModel(new Object[]{"ID", "Produto", "Qtd", "Preço Unit.", "Subtotal"}, 0);
        JTable tabelaItensVendaEmConstrucao = new JTable(modeloItensVendaEmConstrucao);
        JScrollPane scrollItens = new JScrollPane(tabelaItensVendaEmConstrucao);
        scrollItens.setPreferredSize(new Dimension(450, 150));

        // Painel principal do diálogo de nova venda
        JPanel painelNovaVenda = new JPanel(new BorderLayout(10, 10));
        JPanel painelInfoVenda = new JPanel(new GridLayout(0, 2, 5, 5));
        painelInfoVenda.setBorder(BorderFactory.createTitledBorder("Informações da Venda"));

        painelInfoVenda.add(new JLabel("CPF do Cliente (opcional):"));
        painelInfoVenda.add(campoCpf);
        painelInfoVenda.add(new JLabel("Operador da Venda:"));
        painelInfoVenda.add(new JLabel(operadorLogado.getNome() + " (ID: " + operadorLogado.getIdOp() + ")"));

        // Painel para adicionar produto por ID
        JPanel painelAdicionarProduto = new JPanel(new GridBagLayout()); // Usando GridBagLayout para maior controle
        painelAdicionarProduto.setBorder(BorderFactory.createTitledBorder("Adicionar Itens"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painelAdicionarProduto.add(new JLabel("ID do Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        painelAdicionarProduto.add(campoIdProduto, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        painelAdicionarProduto.add(btnBuscarProduto, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        painelAdicionarProduto.add(labelNomeProduto, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        painelAdicionarProduto.add(labelPrecoProduto, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        painelAdicionarProduto.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.5;
        painelAdicionarProduto.add(campoQuantidade, gbc);
        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0;
        painelAdicionarProduto.add(btnAdicionarItem, gbc);


        // Adiciona os painéis ao diálogo principal
        painelNovaVenda.add(painelInfoVenda, BorderLayout.NORTH);
        painelNovaVenda.add(painelAdicionarProduto, BorderLayout.CENTER);
        painelNovaVenda.add(scrollItens, BorderLayout.SOUTH); // Tabela de itens

        // Crie um objeto Venda temporário para armazenar os itens antes de salvar
        Venda novaVendaEmConstrucao = new Venda();
        novaVendaEmConstrucao.setOperador(operadorLogado); // Atribui o operador desde já

        // --- AÇÃO DO BOTÃO BUSCAR PRODUTO ---
        btnBuscarProduto.addActionListener(e -> {
            produtoEncontrado[0] = null; // Reseta o produto encontrado
            labelNomeProduto.setText("Nome: ");
            labelPrecoProduto.setText("Preço: ");

            try {
                int idBusca = Integer.parseInt(campoIdProduto.getText());
                Produto p = produtoDAO.getById(idBusca); // Busca o produto pelo ID

                if (p != null) {
                    produtoEncontrado[0] = p; // Armazena o produto encontrado
                    labelNomeProduto.setText("Nome: " + p.getNome());
                    labelPrecoProduto.setText("Preço: R$ " + String.format("%.2f", p.getPreco()));
                } else {
                    JOptionPane.showMessageDialog(this, "Produto com ID " + idBusca + " não encontrado.", "Produto Não Encontrado", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID do Produto inválido. Digite um número inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao buscar produto: " + ex.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // --- AÇÃO DO BOTÃO ADICIONAR ITEM ---
        btnAdicionarItem.addActionListener(e -> {
            // Usa o produto que foi encontrado pela busca
            Produto produtoSelecionado = produtoEncontrado[0]; 

            if (produtoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Primeiro, busque um produto pelo ID.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantidade;
            try {
                quantidade = Integer.parseInt(campoQuantidade.getText());
                if (quantidade <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verifica se o produto já está na lista e atualiza a quantidade
            boolean itemExistente = false;
            for (int i = 0; i < modeloItensVendaEmConstrucao.getRowCount(); i++) {
                int idProdutoNaTabela = (int) modeloItensVendaEmConstrucao.getValueAt(i, 0); 
                if (idProdutoNaTabela == produtoSelecionado.getIdProd()) {
                    int qtdAtual = (int) modeloItensVendaEmConstrucao.getValueAt(i, 2);
                    modeloItensVendaEmConstrucao.setValueAt(qtdAtual + quantidade, i, 2); // Atualiza a quantidade
                    modeloItensVendaEmConstrucao.setValueAt(
                        String.format("%.2f", (qtdAtual + quantidade) * produtoSelecionado.getPreco()), i, 4
                    ); // Atualiza o subtotal
                    
                    // Também atualiza o item dentro do objeto Venda
                    for (Venda.Item item : novaVendaEmConstrucao.getItens()) {
                        if (item.getProduto().getIdProd() == produtoSelecionado.getIdProd()) {
                            item.setQtd(item.getQtd() + quantidade);
                            break;
                        }
                    }
                    itemExistente = true;
                    break;
                }
            }

            if (!itemExistente) {
                modeloItensVendaEmConstrucao.addRow(new Object[]{
                produtoSelecionado.getIdProd(), // ID do produto
                produtoSelecionado.getNome(),
                quantidade,
                String.format("%.2f", produtoSelecionado.getPreco()),
                String.format("%.2f", quantidade * produtoSelecionado.getPreco())
                });
            
                novaVendaEmConstrucao.adicionarProduto(produtoSelecionado, quantidade); 
            }
            campoQuantidade.setText("1"); // Resetar campo de quantidade
            // Limpa o campo de ID e as labels do produto para a próxima adição
            campoIdProduto.setText("");
            labelNomeProduto.setText("Nome: ");
            labelPrecoProduto.setText("Preço: ");
            produtoEncontrado[0] = null; // Limpa o produto encontrado
        });


        // Exibir o diálogo
        int option = JOptionPane.showConfirmDialog(this, painelNovaVenda, "Registrar Nova Venda", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String cpf = campoCpf.getText();
            if (cpf.isEmpty()) {
                cpf = null;
            }
            novaVendaEmConstrucao.setCpf(cpf); // Define o CPF na venda em construção

            if (novaVendaEmConstrucao.getItens().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A venda deve conter pelo menos um item.", "Erro", JOptionPane.ERROR_MESSAGE);
                return; // Impede a gravação se não houver itens
            }

            try {
                int idVendaGerada = vendaDAO.inserir(novaVendaEmConstrucao);
                JOptionPane.showMessageDialog(this, "Venda registrada com sucesso! ID: " + idVendaGerada);
                atualizarTabelaVendas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao registrar nova venda: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
