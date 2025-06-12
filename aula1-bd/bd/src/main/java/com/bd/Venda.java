package com.bd;

import java.util.ArrayList;
import java.util.List;

public class Venda {
    private int idVenda;
    private String cpf;
    private Operador operador;
    private List<Item> itens; // A lista de itens da venda

    // --- Construtor Padrão (Sem Parâmetros) ---
    // Essencial para quando você cria um objeto Venda sem o ID do banco ainda (ex: nova venda)
    public Venda() {
        this.itens = new ArrayList<>(); // <-- MUITO IMPORTANTE: Inicialize a lista aqui!
    }

    // --- Construtor para Nova Venda (com CPF e Operador, ID será gerado) ---
    public Venda(String cpf, Operador operador) {
        this(); // <-- Chama o construtor padrão para garantir que 'itens' seja inicializado
        this.cpf = cpf;
        this.operador = operador;
    }

    // --- Construtor para Vendas Existentes (com ID do banco) ---
    // Usado ao buscar vendas do banco de dados
    public Venda(int idVenda, String cpf, Operador operador) {
        this(cpf, operador); // <-- Chama o construtor acima para inicializar 'itens' e setar cpf/operador
        this.idVenda = idVenda; // Seta o ID da venda
    }

    // --- Métodos Getters e Setters ---
    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    public List<Item> getItens() {
        return itens;
    }

    // Método para adicionar produtos à lista de itens da venda
    public void adicionarProduto(Produto produto, int quantidade) {
        // A iteração agora será segura, pois 'this.itens' não será mais null
        boolean found = false;
        for (Item item : this.itens) { // <-- Linha 69 (provavelmente) onde a exceção ocorria
            if (item.getProduto().getIdProd() == produto.getIdProd()) {
                item.setQtd(item.getQtd() + quantidade);
                found = true;
                break;
            }
        }
        if (!found) {
            this.itens.add(new Item(produto, quantidade));
        }
    }

    // --- Classe Interna Item (se ainda não a tiver) ---
    // (Mantida como estava, apenas para referência)
    public static class Item {
        private Produto produto;
        private int qtd;

        public Item(Produto produto, int qtd) {
            this.produto = produto;
            this.qtd = qtd;
        }

        public Produto getProduto() {
            return produto;
        }

        public void setProduto(Produto produto) {
            this.produto = produto;
        }

        public int getQtd() {
            return qtd;
        }

        public void setQtd(int qtd) {
            this.qtd = qtd;
        }
    }
}