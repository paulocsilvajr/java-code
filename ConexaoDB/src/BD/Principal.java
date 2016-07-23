package BD;

public class Principal {

    public static void main(String[] args) throws Exception {
        RecuperadorLogin login = new RecuperadorLogin();
        String usuario = login.usuario;
        String senha = login.senha;
//        print(usuario + ":" + senha);
        
        Conexao con = new Conexao("postgresql", "bd_contas", usuario, senha, "localhost");

//        PessoaDAO pes = new PessoaDAO(con);
//        
//        for (Pessoa p : pes.getListaPessoas()) {
//            println(p + "\n");
//        }
//
//        con.fechar();
        new FrmPessoa(con).setVisible(true);
    }

    
    public static void println(Object msg){
        System.out.println(msg);
    }
    
    public static void print(Object msg){
        System.out.print(msg);
    }
    
}
