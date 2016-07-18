package BD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


// Adicionar as libraries MySQL JDBC Driver e PostgreSQL JDBC Driver

public class Conexao { 
    private Connection conexao; 
    private ResultSet resultado;
    
    public Conexao(String sgbd, String bdnome, String usuario, String senha, String host){
        
        try{
            String driver = "", url = "";
            if(sgbd.equalsIgnoreCase("postgresql")){
                driver = "org.postgresql.Driver";
                url = "jdbc:postgresql://" + host + "/" + bdnome;
            }else if(sgbd.equalsIgnoreCase("mysql")){
                driver = "com.mysql.jdbc.Driver";
                url = "jdbc:mysql://" + host + "/" + bdnome;
            }

            Class.forName(driver);
            conexao = DriverManager.getConnection(url, usuario, senha);
            
            System.out.println("Conexão efetuada ao banco de dados " + bdnome);
        }catch(ClassNotFoundException ex){
            System.err.println("Excessão Classe não encontrada: " + ex.getMessage());
        }catch(SQLException ex){
            System.err.println("Excessão SQL: " + ex.getMessage());
        }catch(Exception ex){
            System.err.println("Excessão genérica: " + ex.getMessage());
        }
    }
    
    public ResultSet executar(String sql){
        try{
            resultado = conexao.prepareStatement(sql).executeQuery();
        }catch(SQLException ex){
            System.err.println("Excessão SQL: " + ex.getMessage());
        }
        
        return resultado;
    }
}
