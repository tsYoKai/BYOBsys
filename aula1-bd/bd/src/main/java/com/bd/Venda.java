package com.bd;

import java.util.ArrayList;
import java.util.List;

public class Venda {
    private int idVenda;
    private String cpf;
    private Operador operador;
    private List<Item> itens; 

    public Venda() {
        this.itens = new ArrayList<>(); 
    }

    public Venda(String cpf, Operador operador) {
        this(); 
        this.cpf = cpf;
        this.operador = operador;
    }

    public Venda(int idVenda, String cpf, Operador operador) {
        this(cpf, operador); 
        this.idVenda = idVenda;
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

    public void adicionarProduto(Produto produto, int quantidade) {
        boolean found = false;
        for (Item item : this.itens) { 
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