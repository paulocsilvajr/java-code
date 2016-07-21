package BD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

// Adicionar as libraries MySQL JDBC Driver e PostgreSQL JDBC Driver

public class Conexao { 
    private Connection conexao; 
    private String dml;
    public ResultSet resultado;
    private String sgbd;
    
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
            
            this.sgbd = sgbd;
            
            System.out.println("Conexão efetuada ao banco de dados " + bdnome);
        }catch(ClassNotFoundException ex){
            System.err.println("Excessão Classe não encontrada: " + ex.getMessage());
        }catch(SQLException ex){
            System.err.println("Excessão SQL: " + ex.getMessage());
        }catch(Exception ex){
            System.err.println("Excessão genérica: " + ex.getMessage());
        }
    }
    
    public String dml(){
        return dml;
    }
    
    public ResultSet executar(String sql){
        try{
            dml = sql;
            resultado = conexao.prepareStatement(sql).executeQuery();
//            conexao.commit();  // Não é necessário porque o autocommit está ativo.
        }catch(SQLException ex){            
            System.err.println("Excessão SQL: " + ex.getMessage());
        }
        
        return resultado;
    }
    
    public void executarSemRetorno(String sql){
        try{
            dml = sql;
            conexao.prepareStatement(sql).executeUpdate();
        }catch(SQLException ex){            
            System.err.println("Excessão SQL: " + ex.getMessage());
        }
    }
    
    public ResultSet consultar(String tabela){
        return executar(String.format(
                "SELECT * FROM %s", tabela));
    }
    
    public ResultSet consultar(String tabela, String campos){
        return executar(String.format(
                "SELECT %s FROM %s", campos, tabela));
    }
    
    public ResultSet consultar(String tabela, String campos, String filtro){
        return executar(String.format(
                "SELECT %s FROM %s WHERE %s", 
                campos, tabela, filtro));
    }
    
    public ResultSet consultar(
            String tabela, String campos, String filtro, String ordenacao){
        return executar(String.format(
                "SELECT %s FROM %s WHERE %s ORDER BY %s",
                campos, tabela, filtro, ordenacao));
    }
    
    public ResultSet consultar(
            String tabela, String campos, String filtro, String ordenacao, int quant){
        return executar(String.format(
                "SELECT %s FROM %s WHERE %s ORDER BY %s LIMIT %d",
                campos, tabela, filtro, ordenacao, quant));
    }
    
    public void inserir(String tabela, String campos, String valores){
        executarSemRetorno(String.format(
                "INSERT INTO %s(%s) VALUES(%s)", tabela, campos, valores));
    }
    
    private String formatarCampos(String campos, String valores){
        String[] vCampos = campos.split(",");
        String[] vValores = valores.split(",");
        String temp = "";
        
        for(int i=0; i<vCampos.length; i++){
            temp += vCampos[i] + " = " + vValores[i];
            if(i < (vCampos.length - 1))
                temp += ",";
        }
        
        return temp;
    }
    
    public void atualizar(String tabela, String campos, String valores){       
        executarSemRetorno(String.format(
                "UPDATE %s SET %s", tabela, formatarCampos(campos, valores)));
    }
    
    public void atualizar(String tabela, String campos, String valores, String filtro){
        executarSemRetorno(String.format(
                "UPDATE %s SET %s WHERE %s", tabela, formatarCampos(campos, valores), filtro));
    }
    
    public void excluir(String tabela){
        executarSemRetorno(String.format(
                "DELETE FROM %s", tabela));
    } 
    
    public void excluir(String tabela, String filtro){
        executarSemRetorno(String.format(
                "DELETE FROM %s WHERE %s", tabela, filtro));
    }
            
            
}
