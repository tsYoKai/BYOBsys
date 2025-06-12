package com.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {
    private Connection connection;

    public VendaDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para inserir uma nova venda no banco de dados
    public int inserir(Venda venda) throws SQLException {
        String sqlVenda = "INSERT INTO venda (cpf, id_operador) VALUES (?, ?)";
        String sqlItem = "INSERT INTO item_venda (id_venda, id_prod, item_qtd) VALUES (?, ?, ?)"; // SQL dos itens

        int idVendaGerado = -1; // Variável para armazenar o ID gerado e retorná-lo

        try {
            // Inicia transação para garantir atomicidade (ou tudo vai, ou nada vai)
            connection.setAutoCommit(false);

            // 1. Insere a venda principal e obtém o ID gerado
            // O try-with-resources garante que o stmtVenda seja fechado corretamente
            try (PreparedStatement stmtVenda = connection.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmtVenda.setString(1, venda.getCpf());
                stmtVenda.setInt(2, venda.getOperador().getIdOp());

                stmtVenda.executeUpdate(); // Executa a inserção da venda

                // Obtém o ResultSet com as chaves geradas
                try (ResultSet generatedKeys = stmtVenda.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idVendaGerado = generatedKeys.getInt(1); // Obtém o ID
                        venda.setIdVenda(idVendaGerado); // Define o ID no objeto Venda em memória
                    } else {
                        // Se não conseguiu obter o ID gerado, lança uma exceção
                        throw new SQLException("Falha ao obter ID da venda gerado pelo banco de dados.");
                    }
                }
            } // stmtVenda é fechado automaticamente aqui

            // 2. Insere os itens da venda, usando o ID recém-gerado
            // Use o idVendaGerado obtido no passo anterior
            try (PreparedStatement stmtItem = connection.prepareStatement(sqlItem)) {
                for (Venda.Item item : venda.getItens()) {
                    stmtItem.setInt(1, idVendaGerado); // Usa o ID da venda recém-criada
                    stmtItem.setInt(2, item.getProduto().getIdProd());
                    stmtItem.setInt(3, item.getQtd());
                    stmtItem.addBatch(); // Adiciona ao lote para inserção eficiente
                }
                stmtItem.executeBatch(); // Executa todas as inserções de itens do lote
            } // stmtItem é fechado automaticamente aqui

            // Confirma a transação se tudo deu certo
            connection.commit();
            return idVendaGerado; // Retorna o ID da venda gerado
        } catch (SQLException e) {
            // Em caso de qualquer erro, faz rollback para desfazer tudo
            connection.rollback();
            System.err.println("Erro durante a transação de venda. Rollback realizado: " + e.getMessage());
            e.printStackTrace();
            throw e; // Relança a exceção para que a TelaVenda a trate
        } finally {
            // Sempre volta o auto-commit para true, independente de erro ou sucesso
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    // --- Seus outros métodos (buscarPorId, listarTodas, excluir, inserirItemVenda) estão OK ---

    // Método para buscar uma venda por ID (já está bom!)
    public Venda buscarPorId(int idVenda) throws SQLException {
        String sqlVenda = "SELECT v.id_venda, v.cpf, v.id_operador, o.op_nome AS nome_operador " +
                            "FROM venda v " +
                            "JOIN operador o ON v.id_operador = o.id_operador " +
                            "WHERE v.id_venda = ?";

        String sqlItens = "SELECT iv.*, p.prod_nome as nome_produto, p.prod_preco " +
                            "FROM item_venda iv " +
                            "JOIN produto p ON iv.id_prod = p.id_prod " +
                            "WHERE iv.id_venda = ?";

        Venda venda = null;
        // Instancia os DAOs necessários. Mantenha eles aqui ou passe por construtor se preferir
        // mas para buscarPorId() é comum instanciar localmente se não forem muito pesados
        OperadorDAO operadorDAO = new OperadorDAO(connection);
        ProdutoDAO produtoDAO = new ProdutoDAO(connection);

        try (PreparedStatement stmtVenda = connection.prepareStatement(sqlVenda)) {
            stmtVenda.setInt(1, idVenda);
            ResultSet rsVenda = stmtVenda.executeQuery();

            if (rsVenda.next()) {
                String cpf = rsVenda.getString("cpf");
                int idOperador = rsVenda.getInt("id_operador");
                // Recupera o operador completo pelo ID
                Operador operador = operadorDAO.buscarPorId(idOperador);

                // Instancia a venda com o CPF e o operador
                venda = new Venda(idVenda, cpf, operador); // Crie um construtor em Venda que aceite idVenda

                // Busca os itens da venda
                try (PreparedStatement stmtItens = connection.prepareStatement(sqlItens)) {
                    stmtItens.setInt(1, idVenda);
                    ResultSet rsItens = stmtItens.executeQuery();

                    while (rsItens.next()) {
                        int idProduto = rsItens.getInt("id_prod");
                        int quantidade = rsItens.getInt("item_qtd");
                        // Recupera o produto completo pelo ID
                        Produto produto = produtoDAO.getById(idProduto);

                        // Adiciona o item à lista de itens da venda
                        venda.adicionarProduto(produto, quantidade);
                    }
                }
            }
        }

        return venda;
    }

    // Método para listar todas as vendas (já está bom!)
    public List<Venda> listarTodas() throws SQLException {
        String sql = "SELECT id_venda FROM venda ORDER BY id_venda DESC"; // Ordena para ver as mais novas primeiro
        List<Venda> vendas = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idVenda = rs.getInt("id_venda");
                // Reutiliza o buscarPorId para carregar a venda completa com seus itens
                Venda venda = buscarPorId(idVenda);
                if (venda != null) {
                    vendas.add(venda);
                }
            }
        }

        return vendas;
    }

    // Método para atualizar uma venda (se necessário) - (já está bom!)
    public boolean atualizar(Venda venda) throws SQLException {
        // Implementação dependendo dos requisitos
        // Normalmente vendas não são atualizadas, apenas inseridas
        return false;
    }

    // Método para excluir uma venda (já está bom!)
    public boolean excluir(int idVenda) throws SQLException {
        // Você provavelmente precisará de CASCADE DELETE no seu banco de dados
        // ou excluir os itens da venda primeiro aqui no código,
        // para evitar erros de chave estrangeira.
        // Por exemplo:
        // String sqlDeleteItens = "DELETE FROM item_venda WHERE id_venda = ?";
        // try (PreparedStatement stmtItens = connection.prepareStatement(sqlDeleteItens)) {
        //     stmtItens.setInt(1, idVenda);
        //     stmtItens.executeUpdate();
        // }

        String sql = "DELETE FROM venda WHERE id_venda = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Este método não precisa ser chamado separadamente da TelaVenda,
    // pois a lógica de inserção dos itens já está dentro do método inserir(Venda venda)
    // No entanto, se você o chama de outro lugar ou tem um fluxo diferente, ele está correto.
    public void inserirItemVenda(Venda.Item item, int idVenda) throws SQLException {
        String sql = "INSERT INTO item_venda (id_venda, id_prod, item_qtd) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            stmt.setInt(2, item.getProduto().getIdProd());
            stmt.setInt(3, item.getQtd());
            stmt.executeUpdate();
        }
    }
}