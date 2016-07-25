package BD;

/**
 * Classe Principal.
 * Desenvolvido em Java 1.8. NetBeans IDE 8.1.
 */
public class Principal {

    public static void main(String[] args) throws Exception {
        // Recuperação de usuario e senha em arquivo.
        RecuperadorLogin login = new RecuperadorLogin();
        String usuario = login.usuario;
        String senha = login.senha;
//        print(usuario + ":" + senha);
        // Criação do objeto conexão.
        Conexao con = new Conexao("postgresql", "bd_contas", usuario, senha, "localhost");
        
        new FrmPessoa(con).setVisible(true);
    }

    
    public static void println(Object msg){
        System.out.println(msg);
    }
    
    public static void print(Object msg){
        System.out.print(msg);
    }
    
}
