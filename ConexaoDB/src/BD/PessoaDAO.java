
package BD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {
    // DAO = Data Access Object, Objeto de Acesso a Dados.
    private Conexao con;
    private List<Pessoa> lista = new ArrayList<>();
    private ResultSet resultado;
            
    public PessoaDAO(Conexao con){
        this.con = con;
    }
    
    public List<Pessoa> listagem(){
        lista.clear();
        try{
            resultado = con.consultar(Pessoa.NOME_TABELA);
            
            while(resultado.next()){
                Pessoa linha = new Pessoa();
                linha.setCpf(resultado.getString(Pessoa.NOME_CAMPOS[0]));
                linha.setRg(resultado.getString(Pessoa.NOME_CAMPOS[1]));
                linha.setNome(resultado.getString(Pessoa.NOME_CAMPOS[2]));
                linha.setData_inclusao(resultado.getTimestamp(Pessoa.NOME_CAMPOS[3]));
                 
                lista.add(linha);
            }
        }catch(SQLException ex){
            System.err.println("Excessão SQL: " + ex.getMessage());
        }
        
        return lista;
    }
    
    public void inserir(Pessoa p){
        String campos = String.format("%s, %s, %s", 
                Pessoa.NOME_CAMPOS[0], Pessoa.NOME_CAMPOS[1], Pessoa.NOME_CAMPOS[2]);
        
        String valores = p.getRg().isEmpty()?
                String.format("\'%s\', NULL, \'%s\'", p.getCpf(), p.getNome()):
                String.format("\'%s\', \'%s\', \'%s\'", p.getCpf(), p.getRg(), p.getNome()); 
        
        con.inserir(Pessoa.NOME_TABELA, campos, valores);
    }
    
    public void atualizar(Pessoa p){
        String campos = "", valores = "", filtro = "";
        filtro = String.format("%s = \'%s\'", Pessoa.NOME_CAMPOS[0], p.getCpf());
        
        if(p.getAltValores()[0]){
            campos +=  String.format("%s, ", Pessoa.NOME_CAMPOS[0]);
            valores += String.format("\'%s\', ", p.getCpf());
        }
        
        if(p.getAltValores()[1]){
            campos += String.format("%s, ", Pessoa.NOME_CAMPOS[1]);
            if(p.getRg().isEmpty())
                valores += "NULL";
            else
                valores += String.format("\'%s\', ", p.getRg());
        }
        
        if(p.getAltValores()[2]){
            campos +=  String.format("%s, ", Pessoa.NOME_CAMPOS[2]);
            valores += String.format("\'%s\', ", p.getNome());
        }
        
        campos = campos.substring(0, (campos.length() - 2));
        valores = valores.substring(0, (valores.length() - 2));
        
        con.atualizar(Pessoa.NOME_TABELA, campos, valores, filtro);
        
        p.zerarAltValores();
    }
}
