package com.bd;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma Venda no sistema, com seus itens.
 */
public class Venda {
    private int idVenda;
    private String cpf; // CPF do cliente (pode ser null)
    private Operador operador; // Operador que registrou a venda
    private List<Item> itens; // Lista de itens da venda

    public Venda() {
        this.itens = new ArrayList<>();
    }

    public Venda(String cpf, Operador operador) {
        this.cpf = cpf;
        this.operador = operador;
        this.itens = new ArrayList<>();
    }

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

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }

    /**
     * Adiciona um produto à lista de itens da venda. Se o produto já existir,
     * atualiza a quantidade.
     * @param produto O produto a ser adicionado.
     * @param quantidade A quantidade do produto.
     */
    public void adicionarProduto(Produto produto, int quantidade) {
        for (Item item : this.itens) {
            if (item.getProduto().getIdProd() == produto.getIdProd()) {
                item.setQtd(item.getQtd() + quantidade);
                return;
            }
        }
        // Se não encontrou o item existente, adiciona um novo
        this.itens.add(new Item(produto, quantidade));
    }

    /**
     * Classe interna que representa um item de venda (produto e quantidade).
     */
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
