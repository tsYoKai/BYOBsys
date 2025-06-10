package com.bd;

public class Produto {
    private int idProd;
    private String nome;
    private double preco;
 
    public Produto(int idProd, String nome, double preco) {
        this.idProd = idProd;
        this.nome = nome;
        this.preco = preco;
    }
 
    public int getIdProd() {
        return idProd;
    }
 
    public void setIdProd(int idProd) {
        this.idProd = idProd;
    }
 
    public String getNome() {
        return nome;
    }
 
    public void setNome(String nome) {
        this.nome = nome;
    }
 
    public double getPreco() {
        return preco;
    }
 
    public void setPreco(double preco) {
        this.preco = preco;
    }
 @Override
    public String toString() {
        return nome + " (R$ " + String.format("%.2f", preco) + ")";
    }
}
 