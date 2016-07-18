
/**
 * Implementação de tabuleiro para batalha naval.
 */
public class Tabuleiro {
    private char[][] tabuleiro;
    private final int T = 6;
    private final char NAVIO = 'N';
    private final int MAXNAVIOS = 5;
    private int totalTiros = 0;
    private int naviosMarcados;
    private int naviosDestruidos = 0;
    private final char DESTRUICAO = '*'; // Navio destruido
    private final char ATAQUE = 'T';
    private String tipo;
    
    Tabuleiro(){
        inicializar();
    }
    
    Tabuleiro(String tipo){
        inicializar();
        
        if (tipo.equalsIgnoreCase("ataque")){
            this.tipo = tipo.toLowerCase();
        }
    }
    
    private void inicializar(){
        tabuleiro = new char[T][T];
        zerar();        
    }
    
    public void zerar(){
        for(int i = 0; i < T; i++){
            for(int j = 0; j < T; j++){
                tabuleiro[i][j] = ' ';
            }
        }
        tipo = "defesa";
        naviosMarcados = 0;
    }
    
    @Override
    public String toString(){
        String linha, idlinha, idcoluna, t;
        int cont = 0;
        
        linha = "  +---+---+---+---+---+---+";
        idlinha = "    1   2   3   4   5   6  \n";
        idcoluna = "A B C D E F ";
        
        t = "\n" + idlinha + linha + "\n";
        for(int i=0; i < T; i++){
            t += idcoluna.substring(cont, cont+2);
            cont += 2;
            
            for(int j=0; j<T;j++){
                t += String.format("| %c ", tabuleiro[i][j]);
                if(j == (T-1)) t += "|\n";
            }
            
            if(i < T) t += linha + "\n";
        }
        
        return t;
    }
    
    public int getTotalTiros(){
        return totalTiros;
    }
    
    public void setTotalTiros(){
        totalTiros += 1;
    }
    
    public int getNaviosMarcados(){
        return naviosMarcados; 
    }
    
    public int getMaxNavios(){
        return MAXNAVIOS;
    }
    
    public int getNaviosDestruidos(){
        return naviosDestruidos;
    }
    
    int[] retornarPosicao(char linha, int coluna){
        int pos[] = new int[2];
        
        linha = Character.toUpperCase(linha);
        
        if(ehPosicaoValida(linha, coluna)){
            // A == 65, B == 66, ...
            pos[0] = (int)linha - 65;

            pos[1] = coluna - 1;
        }else{
            pos[0] = -1;
            pos[1] = -1;
        }
        return pos;
    }
    
    char retornarLinha(int linha){
        return (char)(linha + 65);
    }
    
    int retornarColuna(int coluna){
        return coluna + 1;
    }
    
    boolean ehPosicaoValida(char linha, int coluna){
        linha = Character.toUpperCase(linha);
        return ((linha >= 'A' && linha <= 'F') && (coluna >= 1 && coluna <= 6));
    }
    
    boolean ehPosicaoValida(int linha, int coluna){
        return ((linha >=0 && linha <= 5) &&(coluna >=0 && coluna <=5));
    }
    
    boolean estaOcupada(char linha, int coluna){
        if(ehPosicaoValida(linha, coluna)){
            int[] pos = retornarPosicao(linha, coluna);
            
            return tabuleiro[pos[0]][pos[1]] == NAVIO;
        }
        return false;
    }
    
    boolean estaOcupada(int linha, int coluna){
        if(ehPosicaoValida(linha, coluna)){            
            return tabuleiro[linha][coluna] == NAVIO;
        }
        return false;
    }
    
    boolean marcarNavio(char linha, int coluna){
        if(tipo.equals("defesa") && !estaOcupada(linha, coluna) && naviosMarcados < MAXNAVIOS){
            int[] pos = retornarPosicao(linha, coluna);
            naviosMarcados += 1;
            tabuleiro[pos[0]][pos[1]] = NAVIO;
            return true;
        }
        return false;
    }
    
    boolean atacarNavio(char linha, int coluna){
        if(tipo.equals("ataque")){
            int[] pos = retornarPosicao(linha, coluna);
            if (tabuleiro[pos[0]][pos[1]] != ATAQUE){
                tabuleiro[pos[0]][pos[1]] = ATAQUE;
                return true;
            }
            
            //totalTiros +=1;
        }
        return false;
    }
    
    boolean receberAtaque(char linha, int coluna){
        if(tipo.equals("defesa") && estaOcupada(linha, coluna)){
            int[] pos = retornarPosicao(linha, coluna);
            tabuleiro[pos[0]][pos[1]] = DESTRUICAO;
            naviosDestruidos += 1;
            return true;
        }
        return false;
    }
    
    boolean receberAtaque(int linha, int coluna){
        if(tipo.equals("defesa") && estaOcupada(linha, coluna)){
            tabuleiro[linha][coluna] = DESTRUICAO;
            naviosDestruidos += 1;
            return true;
        }
        return false;
    }
    
    boolean ehDerrota(){
        return naviosDestruidos == MAXNAVIOS;
    }
    
    String legenda(){
        return String.format("%c = Navio\n%c = Ataque\n%c = Navio Destruido",
                             NAVIO, ATAQUE, DESTRUICAO);
    }
    
    boolean estaDestruido(){
        int cont = 0;
        for(int i = 0; i < T; i++){
            for(int j = 0; j < T; j++){
                if(tabuleiro[i][j] == DESTRUICAO)
                    cont++;
            }
        }
        return cont == MAXNAVIOS;
    }
}
