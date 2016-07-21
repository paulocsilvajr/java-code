
package BD;

import java.util.Date;
import java.sql.Timestamp;

public class FuncoesUteis {
    public static String type(Object obj){
        return obj.getClass().toString();
    }
    
    public static Date agora(){
        return new Date();
    }
    
    public static Timestamp agoraBD(){
        return new Timestamp(agora().getTime());
    }
}
