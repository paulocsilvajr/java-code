
/**
 * Implementação de jogador para batalha naval.
 */
public class Jogador {
    private String nome;
    private int MAXDERROTAS = 20; // Default=20, para testes diminuir
    private int acertos = 0;
    private int perdas = 0;
    private int derrotas = 0;
    
    void setNome(String nome){
        this.nome = nome;
    }
    
    String getNome(){
        return nome;
    }
    
    void setAcertos(){
        acertos += 1;
    }

    void setDerrotas(){
        derrotas += 1;
    }
    
    void setPerdas(){
        perdas += 1;
    }
    
    int getAcertos(){
        return acertos;
    }
    
    int getDerrotas(){
        return derrotas;
    }
    
    int getPerdas(){
        return perdas;
    }
    
    int getMaxDerrotas(){
        return MAXDERROTAS;
    }
    
    boolean ehDerrota(){
        return derrotas >= MAXDERROTAS;
    }
    
    @Override
    public String toString(){
        return String.format("Nome: %s\nAcertos: %d\nDerrotas: %d",
                             nome, acertos, derrotas);
    }
}
