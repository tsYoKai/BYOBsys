package com.bd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent;  
import java.sql.Connection;     
import java.sql.SQLException;    

public class TelaLogin extends JFrame {
  
  private JTextField campoNome;
  private JPasswordField campoID;
  private JButton botaoLogin;
  private Connection connection; 
  private Operador operadorLogado;

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
    add(new JLabel()); 
    add(botaoLogin);

    botaoLogin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String nome = campoNome.getText();
        String idTXT = new String(campoID.getPassword());
        int id;

        try {
          id = Integer.parseInt(idTXT);
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(TelaLogin.this, "O ID deve ser um número válido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
          return; 
        }

        
        try {
          connection = ConnectionFactory.getConnection(); 
          if (connection == null || connection.isClosed()) {
            JOptionPane.showMessageDialog(TelaLogin.this, "Falha ao obter conexão com o banco de dados.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            return; 
          }

          OperadorDAO operadorDAO = new OperadorDAO(connection);
          Operador operadorParaLogin = new Operador(id, nome); 
          
          Operador operadorAutenticado = operadorDAO.logar(operadorParaLogin); 

          if (operadorAutenticado != null) { 
            JOptionPane.showMessageDialog(TelaLogin.this, "Login bem-sucedido!");

            dispose(); 
            new TelaVenda(connection, operadorAutenticado);

          } else {
            JOptionPane.showMessageDialog(TelaLogin.this, "Nome de usuário ou ID inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            ConnectionFactory.closeConnection(connection);
            connection = null; 
          }

        } catch (SQLException ex) {
          JOptionPane.showMessageDialog(TelaLogin.this, "Erro de banco de dados durante o login: " + ex.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
          ConnectionFactory.closeConnection(connection);
          connection = null;
        }
      }
    });

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        ConnectionFactory.closeConnection(connection);
      }
    });

    setVisible(true);
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {
    }

    SwingUtilities.invokeLater(TelaLogin::new);
  }
}