package BD;

import java.util.List;


public class Principal {

    public static void main(String[] args) throws Exception {
//         ref: http://pt.stackoverflow.com/questions/63778/conex%C3%A3o-ao-banco-de-dados-mysql-e-java
        RecuperadorLogin login = new RecuperadorLogin();
        String usuario = login.usuario;
        String senha = login.senha;
//        print(usuario + ":" + senha);
        
        Conexao con = new Conexao("postgresql", "bd_contas", usuario, senha, "localhost");

        PessoaDAO pes = new PessoaDAO(con);
        
        for (Pessoa p : pes.listagem()) {
            println(p + "\n");
        }
        

    }

    
    public static void println(Object msg){
        System.out.println(msg);
    }
    
    public static void print(Object msg){
        System.out.print(msg);
    }
    
}
