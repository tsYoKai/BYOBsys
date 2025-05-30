package com.bd;

import javax.swing.*;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaLogin extends JFrame {

    private JTextField campoRa;
    private JPasswordField campoSenha;
    private JButton botaoLogin;

    public TelaLogin() {
        setTitle("Login do Aluno");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 5, 5));

        JLabel labelRa = new JLabel("RA:");
        campoRa = new JTextField();

        JLabel labelSenha = new JLabel("Senha:");
        campoSenha = new JPasswordField();

        botaoLogin = new JButton("Entrar");

        add(labelRa);
        add(campoRa);
        add(labelSenha);
        add(campoSenha);
        add(new JLabel()); // espaço vazio
        add(botaoLogin);

        botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ra = campoRa.getText();
                String senha = new String(campoSenha.getPassword());

                Aluno aluno = new Aluno(ra, senha);
                AlunoDAO dao = new AlunoDAO();

                if (dao.autenticar(aluno)) {
                    JOptionPane.showMessageDialog(null, "Login bem-sucedido!");

                    dispose(); // Fecha a tela de login
                    // Aqui poderia abrir nova janela ou dashboard
                    new TelaUsuarios();
                } else {
                    JOptionPane.showMessageDialog(null, "RA ou senha inválidos.");
                }
            }
        });

        setVisible(true);
    }
}
