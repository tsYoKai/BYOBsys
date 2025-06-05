package com.bd;

public class Operador {
    private int idOp;
    private String nome;
 
    public Operador(int idOp, String nome) {
        this.idOp = idOp;
        this.nome = nome;
    }
 
    public int getIdOp() {
        return idOp;
    }
 
    public void setIdOp(int idOp) {
        this.idOp = idOp;
    }
 
    public String getNome() {
        return nome;
    }
 
    public void setNome(String nome) {
        this.nome = nome;
    }
}
 