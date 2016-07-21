
package BD;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RecuperadorLogin {
    // ref. http://www.devmedia.com.br/lendo-dados-de-txt-com-java/23221
    String usuario, senha, path = "/home/paulo/pc/usuarioSenhaTeste";
    
    public RecuperadorLogin(){
        try{
            FileReader arq = new FileReader(path);
            BufferedReader lerArq = new BufferedReader(arq);
            usuario = lerArq.readLine();
            senha = lerArq.readLine();
        }catch(IOException ex){
            System.err.printf("Erro ao abrir arquivo: %s\n", ex.getMessage());
        }
    }

}
