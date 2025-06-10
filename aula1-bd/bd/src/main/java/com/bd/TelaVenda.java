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

// Adicione um ProdutoDAO como atributo da classe TelaVenda
private ProdutoDAO produtoDAO;

// No construtor da TelaVenda, inicialize-o:
  public TelaVenda(Connection connection, Operador operadorLogado) {

    this.connection = connection;

    this.operadorLogado = operadorLogado;

   

    setTitle("Lista de Vendas - Operador: " + operadorLogado.getNome() + " (ID: " + operadorLogado.getIdOp() + ")");
    setSize(1000, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

        vendaDAO = new VendaDAO(connection);
        produtoDAO = new ProdutoDAO(connection);

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

    painelBotoes.setLayout(new GridLayout(4, 1, 5, 5));



    JButton botaoAdicionar = new JButton("Nova Venda");

    JButton botaoDetalhes = new JButton("Ver Detalhes");

    JButton botaoExcluir = new JButton("Excluir Venda");

    JButton botaoAtualizar = new JButton("Atualizar Tabela");



    painelBotoes.add(botaoAdicionar);

    painelBotoes.add(botaoDetalhes);

    painelBotoes.add(botaoExcluir);

    painelBotoes.add(botaoAtualizar);



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



    setVisible(true);

  }



  private void atualizarTabelaVendas() {

    try {

      List<Venda> vendas = vendaDAO.listarTodas();

      modeloTabelaVendas.setRowCount(0); // limpa linhas



      for (Venda venda : vendas) {

        double totalVenda = 0.0;

        for (Venda.Item item : venda.getItens()) {

          totalVenda += item.getQtd() * item.getProduto().getPreco();

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


// ... (métodos existentes)

private void abrirDialogoNovaVenda() {
    JTextField campoCpf = new JTextField(15);

    // --- Componentes para adicionar produtos ---
    JComboBox<Produto> comboProdutos = new JComboBox<>();
    JTextField campoQuantidade = new JTextField("1", 5); // Default 1
    JButton btnAdicionarItem = new JButton("Adicionar Produto à Venda");

    // Modelo para a tabela de itens que serão adicionados à venda
    DefaultTableModel modeloItensVendaEmConstrucao = new DefaultTableModel(new Object[]{"ID", "Produto", "Qtd", "Preço Unit.", "Subtotal"}, 0);
    JTable tabelaItensVendaEmConstrucao = new JTable(modeloItensVendaEmConstrucao);
    JScrollPane scrollItens = new JScrollPane(tabelaItensVendaEmConstrucao);
    scrollItens.setPreferredSize(new Dimension(450, 150));

    // Carregar produtos no JComboBox
    try {
        List<Produto> produtos = produtoDAO.getAll(); // Você precisará deste método no ProdutoDAO
        for (Produto p : produtos) {
            comboProdutos.addItem(p);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
        return; // Sai se não conseguir carregar produtos
    }

    // Painel principal do diálogo de nova venda
    JPanel painelNovaVenda = new JPanel(new BorderLayout(10, 10));
    JPanel painelInfoVenda = new JPanel(new GridLayout(0, 2, 5, 5));
    painelInfoVenda.setBorder(BorderFactory.createTitledBorder("Informações da Venda"));

    painelInfoVenda.add(new JLabel("CPF do Cliente (opcional):"));
    painelInfoVenda.add(campoCpf);
    painelInfoVenda.add(new JLabel("Operador da Venda:"));
    painelInfoVenda.add(new JLabel(operadorLogado.getNome() + " (ID: " + operadorLogado.getIdOp() + ")"));

    JPanel painelAdicionarProduto = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    painelAdicionarProduto.setBorder(BorderFactory.createTitledBorder("Adicionar Itens"));
    painelAdicionarProduto.add(new JLabel("Produto:"));
    painelAdicionarProduto.add(comboProdutos);
    painelAdicionarProduto.add(new JLabel("Quantidade:"));
    painelAdicionarProduto.add(campoQuantidade);
    painelAdicionarProduto.add(btnAdicionarItem);

    // Adiciona os painéis ao diálogo principal
    painelNovaVenda.add(painelInfoVenda, BorderLayout.NORTH);
    painelNovaVenda.add(painelAdicionarProduto, BorderLayout.CENTER);
    painelNovaVenda.add(scrollItens, BorderLayout.SOUTH); // Tabela de itens

    // Crie um objeto Venda temporário para armazenar os itens antes de salvar
    Venda novaVendaEmConstrucao = new Venda();
    novaVendaEmConstrucao.setOperador(operadorLogado); // Atribui o operador desde já

    btnAdicionarItem.addActionListener(e -> {
        Produto produtoSelecionado = (Produto) comboProdutos.getSelectedItem();
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

        if (produtoSelecionado != null) {
            // Verifica se o produto já está na lista e atualiza a quantidade
            boolean itemExistente = false;
            for (int i = 0; i < modeloItensVendaEmConstrucao.getRowCount(); i++) {
                // Recuperar o ID do produto da linha da tabela (coluna 0)
                int idProdutoNaTabela = (int) modeloItensVendaEmConstrucao.getValueAt(i, 0); 
                if (idProdutoNaTabela == produtoSelecionado.getIdProd()) {
                    int qtdAtual = (int) modeloItensVendaEmConstrucao.getValueAt(i, 2);
                    modeloItensVendaEmConstrucao.setValueAt(qtdAtual + quantidade, i, 2); // Atualiza a quantidade
                    modeloItensVendaEmConstrucao.setValueAt(
                        String.format("%.2f", (qtdAtual + quantidade) * produtoSelecionado.getPreco()), i, 4
                    ); // Atualiza o subtotal
                    
                    // Também atualize o item dentro do objeto Venda
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
    
    // --- MUDANÇA AQUI: Chame adicionarProduto em vez de addItem ---
    novaVendaEmConstrucao.adicionarProduto(produtoSelecionado, quantidade); 
}

        }
        campoQuantidade.setText("1"); // Resetar campo de quantidade
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
            // 1. Inserir a venda principal e obter o ID gerado
            int idVendaGerada = vendaDAO.inserir(novaVendaEmConstrucao);
            novaVendaEmConstrucao.setIdVenda(idVendaGerada); // Atribui o ID gerado à venda

            // 2. Inserir os itens da venda
            // Você precisará de um método em VendaDAO para inserir os itens
            for (Venda.Item item : novaVendaEmConstrucao.getItens()) {
                vendaDAO.inserirItemVenda(item, idVendaGerada); // Método a ser criado no VendaDAO
            }

            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso! ID: " + idVendaGerada);
            atualizarTabelaVendas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar nova venda: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

// *** IMPORTANTE: Você precisará de uma classe ProdutoDAO (como ProdutoDAO.java)
// *** E de um método inserirItemVenda(Venda.Item item, int idVenda) no VendaDAO.java
// *** E um método listarTodos() no ProdutoDAO para popular o JComboBox.
}