package com.bd;

import javax.swing.*;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaLogin extends JFrame {

    private JTextField campoNome;
    private JPasswordField campoID;
    private JButton botaoLogin;

    public TelaLogin() {
        setTitle("Login do Operador");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 5, 5));

        JLabel labelNome = new JLabel("Nome:");
        campoNome = new JTextField();

        JLabel labelID = new JLabel("ID:");
        campoID = new JPasswordField();

        botaoLogin = new JButton("Entrar");

        add(labelNome);
        add(campoNome);
        add(labelID);
        add(campoID);
        add(new JLabel()); // espaço vazio
        add(botaoLogin);

        botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = campoNome.getText();
                //Transformando senha ((char[])) em int, ______ SERÁ SÓ O ID no campo senha
                char[] charID =campoID.getPassword();
                String idTXT = new String(charID);
                int id = Integer.parseInt(idTXT);
                //-----------------------------------------------
                Operador operador = new Operador(id, nome);
                OperadorDAO dao = new OperadorDAO();

                if (dao.logar(operador)) {
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
