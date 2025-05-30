package com.bd;

public class Aluno {
    private int idAluno;
    private String nome;
    private String ra;
    private double nota;
    private String telefone;
    private String senha;

    public Aluno(int idAluno, String nome, String ra, double nota, String telefone, String senha) {
        this.idAluno = idAluno;
        this.nome = nome;
        this.ra = ra;
        this.nota = nota;
        this.telefone = telefone;
        this.senha = senha;
    }

    // Construtor sem ID (para novos cadastros, o banco gera o ID automaticamente)
    public Aluno(String nome, String ra, double nota, String telefone, String senha) {
        this.nome = nome;
        this.ra = ra;
        this.nota = nota;
        this.telefone = telefone;
        this.senha = senha;
    }

    public Aluno(String ra, String senha) {
        this.ra = ra;
        this.senha = senha;
    }

    public int getIdAluno() {
        return idAluno;
    }

    public String getNome() {
        return nome;
    }

    public String getRa() {
        return ra;
    }

    public double getNota() {
        return nota;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setIdAluno(int idAluno) {
        this.idAluno = idAluno;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
