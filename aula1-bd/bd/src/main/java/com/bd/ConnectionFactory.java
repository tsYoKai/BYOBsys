package com.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
     private static String url =
 "jdbc:mysql://localhost:3306/byobbd?useTimezone=true&server=UTC";
  private static String usuario = "root";
  private static String senha = "usjt";   
  
  public static Connection getConnection(){
      try{
  Connection conexao = DriverManager.getConnection(url, usuario, senha);
          System.out.println("Conexão estabelecida com sucesso");
          return conexao;
      }catch(SQLException e) {
          System.err.println(e.getMessage());
          return null;
      }
  }

  public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
                // Em um ambiente de produção, você faria um log mais robusto aqui
            }
        }
    }

}
