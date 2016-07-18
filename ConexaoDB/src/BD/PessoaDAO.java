
package BD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {
    private Conexao con;
    public PessoaDAO(Conexao con){
        this.con = con;
    }
    
    public List<Pessoa> listagem(){
        List<Pessoa> lista = new ArrayList<Pessoa>();
        try{
            ResultSet resultado = con.executar("SELECT * FROM pessoas");

            while(resultado.next()){
                Pessoa linha = new Pessoa();
                linha.setCpf(resultado.getString("cpf"));
                linha.setRg(resultado.getString("rg"));
                linha.setNome(resultado.getString("nome"));
                linha.setData_inclusao(resultado.getDate("data_inclusao"));
                 
                lista.add(linha);
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return lista;
    }
}
