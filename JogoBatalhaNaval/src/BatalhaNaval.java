
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Classe que implementa jogo de batalha naval em rede, todos contra todos, 
 * usando thread para recebimento de pacotes.
 * @author Paulo C. Silva Jr.
 */
public class BatalhaNaval {
    private static final int PORTA = 9876;
    private static final int T = 50;
    
    private static DatagramSocket conexao;
    private static byte[] mensagemBytes;
    private static String mensagemRecebimento;
    private static String protocolo = "";
    
    private static Jogador jogador;
    private static Tabuleiro tab_defesa;
    private static Tabuleiro tab_ataque;
    
    private static String ipLocal = "";
    private static String ipDestino = "";
    private static InetAddress enderecoBroadcast;
    
    private static Scanner teclado;
    private static String[] parametros;
    private static Thread principal;
    
    public static void main(String[] args) throws Exception{
        parametros = args;
        
        jogador = new Jogador();
        tab_defesa = new Tabuleiro();
        
        principal = new Thread(new Principal()); 
        principal.start();
        
    }
    
    private static class Principal implements Runnable{
        @Override
        public void run(){
            try{
                teclado = new Scanner(System.in);

                char linha; 
                int coluna;

                // Criação de objeto InetAdress com endereço de envio de pacotes por broadcast.
                enderecoBroadcast = InetAddress.getByName("255.255.255.255");//"127.0.0.1");//

                // capturando ip local no windows
                if(System.getProperty("os.name").toLowerCase().contains("win")){
                    ipLocal = InetAddress.getLocalHost().getHostAddress();
                    //print(ipLocal);
                // capturando ip local em outros sistemas(linux, ...)
                }else{
                    ArrayList<String> lista = new ArrayList<>();
                    Enumeration nis = null;
                    try{
                        nis = NetworkInterface.getNetworkInterfaces();
                    }catch(SocketException se){
                        se.printStackTrace();
                    }
                    while(nis.hasMoreElements()){
                        NetworkInterface ni = (NetworkInterface)nis.nextElement();
                        Enumeration ias = ni.getInetAddresses();
                        while(ias.hasMoreElements()){
                            InetAddress ia = (InetAddress)ias.nextElement();
                            lista.add(ia.getHostAddress());
                            //println(ni.getName()+"->"+ia.getHostAddress()+"->"+ia.getHostName() );
                        }
                    }

                    for(String item: lista){
                        if(!(item.startsWith("127") ||
                             item.startsWith("0"))){
                            if(item.contains(".")){
                                ipLocal = item;
                            }
                        }
                    }
                }
                // fim da captura de IP local
                //teste
                println("::: " + ipLocal + " :::\n" + 
                        "::: " + enderecoBroadcast + " :::\n");
                //

                // Capturando argumento(ip e/ou broadcast) 
                // passado na linha de comando para testes
                if(parametros.length > 0){
                    ipLocal = parametros[0];
                    if(parametros.length > 1){
                        enderecoBroadcast = InetAddress.getByName(parametros[1]);
                    }

                    println("IP local: " + ipLocal + "\n"
                            + "IP broadcast: " + enderecoBroadcast);
                }

                conexao = new DatagramSocket(PORTA);

                println("Jogo: Batalha Naval\n");

                print("Informe seu nome: ");
                jogador.setNome(teclado.nextLine());

                while(!protocolo.equals("f")){
                    println("Tabuleiro defesa:\n" + tab_defesa);

                    for(int i=0; i<tab_defesa.getMaxNavios(); i++){
                        while(true){
                            try{
                                print("\nInforme " + (i+1) + 
                                      "ª posição de navio\nlinha: ");
                                linha = (teclado.nextLine()).charAt(0);
                                print("coluna: ");
                                coluna = Integer.valueOf(teclado.nextLine());

                                if(tab_defesa.estaOcupada(linha, coluna)){
                                    print("Posição Ocupada\n");
                                }else if(tab_defesa.ehPosicaoValida(linha, coluna)){
                                    tab_defesa.marcarNavio(linha, coluna);
                                    break;
                                }else{
                                    print("Posição inválida\n");
                                }
                            }catch(java.lang.NumberFormatException e){
                                println("Posição inválida\n");
                            }catch(java.lang.StringIndexOutOfBoundsException e){
                                println("Valor inválido\n");
                            }
                        }
                    }

                    println("Tabuleiro defesa:\n" + tab_defesa);

                    tab_ataque = new Tabuleiro("ataque");
                    println("Inicio dos ataques\nTabuleiro ataque:\n" + tab_ataque);      

                    Thread tr = new Thread(new RecebimentoPacote()); 
                    tr.start();

                    while(!tab_defesa.estaDestruido()){
                        while(true){
                            try{
                                print("\nInforme uma ataque"
                                    + "\nlinha: ");
                                linha = (teclado.nextLine()).charAt(0);
                                print("coluna: ");
                                coluna = Integer.valueOf(teclado.nextLine());


                                if(tab_ataque.ehPosicaoValida(linha, coluna)){
                                    tab_ataque.atacarNavio(linha, coluna);
                                    tab_defesa.setTotalTiros();
                                    break;
                                }else{
                                    println("Posição inválida\n");
                                }                        
                            }catch(java.lang.NumberFormatException e){
                                println("Posição inválida\n");
                            }
                        }

                        println(tab_ataque);

                        int[] pos = tab_ataque.retornarPosicao(linha, coluna);

                        String mensagemEnvio = "T" + pos[0] + pos[1];
                        mensagemBytes = new byte[T];
                        mensagemBytes = mensagemEnvio.getBytes();

                        DatagramPacket pacote = new DatagramPacket(
                            mensagemBytes, mensagemBytes.length, enderecoBroadcast, PORTA);
                        conexao.send(pacote);

                    }

                    tab_defesa.zerar();

                    println("\nPlacar:     " + 
                            "\n  Acertos:  " + jogador.getAcertos() + 
                            "\n  Erros:    " + (tab_defesa.getTotalTiros() - jogador.getAcertos()) + 
                            "\n  Perdas:   " + jogador.getPerdas() + 
                            "\n  Derrotas: " + jogador.getDerrotas() + " (MAX: "+jogador.getMaxDerrotas()+")\n\n");    
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Implementação de classe para thread de recebimento de pacotes.
     */
    private static class RecebimentoPacote implements Runnable{
        private DatagramPacket pacote;
        public String ipOrigem;
        private boolean mensagemDerrotaEnviada = false;
        private Timer temporizador;
        private final int segundos = 10; 
        
        // Subclasse para tarefa de fechar o programa por falta de recebimento de pacotes.
        // Usado em um Timer.schedule().
        class FecharPrograma extends TimerTask{
            public void run(){
                println("\n\tFechando programa por falta de recebimento "
                        + "de pacotes\n");
                System.exit(0);
            }
        }
        
        @Override
        public void run(){            
            while(true){
                // Try{}catch para tratar exceções da conexão.
                try{
                    mensagemBytes = new byte[T];
                    pacote = new DatagramPacket(mensagemBytes, mensagemBytes.length);                
                    
                    do{
                        println("\n\tEsperando mensagem...");
                        
                        // Temporizador para o protocolo "E".
                        // Fechar o programa por falta de recebimento de pacote por n segundos.
                        if(protocolo.equals("E")){
                            temporizador = new Timer();
                            temporizador.schedule(new FecharPrograma(), segundos * 1000);
                        }
                        
                        conexao.receive(pacote);
                        
                        // Cancelamento do protocolo se recebido pacotes com protocolo "E".
                        if(protocolo.equals("E")){
                            temporizador.cancel();
                        }                        
                        
                        mensagemRecebimento = (new String(pacote.getData())).trim();
                        protocolo = mensagemRecebimento.substring(0, 1).toUpperCase();                       
                        
                        //teste
                        println("\n\t::: msg=" + mensagemRecebimento + " :::");
                        //
                        ipOrigem = pacote.getAddress().toString();

                        ipDestino = "/" + ipLocal;
                        
                        //teste
                        println("\n\t::: prot=" + protocolo + " :::");
                        //                        
                    }while( !(  (protocolo.equals("T")) ||
                                (protocolo.equals("A")) ||
                                (protocolo.equals("F")) ||
                                (protocolo.equals("E"))  ) || 
                                ipOrigem.equals(ipDestino) );
                    
                    int linha; 
                    int coluna;
                    
                    if(jogador.ehDerrota() && mensagemDerrotaEnviada == false){                        
                        mensagemBytes = new byte[T];
                        mensagemBytes = ("F"+jogador.getNome()
                            +"T"+jogador.getAcertos()+"P"+jogador.getDerrotas()).getBytes();
                        
                        DatagramPacket pacoteEnvio = new DatagramPacket(
                            mensagemBytes, mensagemBytes.length, enderecoBroadcast, PORTA);
                        conexao.send(pacoteEnvio);
                        
                        println("\n\tVOCÊ PERDEU O JOGO\n\tDerrotas: " + jogador.getDerrotas()+"\n\n");
                        
                        mensagemDerrotaEnviada = true;
                        //System.exit(0);
                                
                    }else if(protocolo.equals("T")){
                        linha = Integer.parseInt(mensagemRecebimento.substring(1, 2));
                        coluna = Integer.parseInt(mensagemRecebimento.substring(2, 3));
                        
                        // Se vc levou um tiro, portanto perdeu um barco...
                        if(tab_defesa.receberAtaque(linha, coluna)){
                            println("\n\tAdversário("+pacote.getAddress()+
                                    ") acertou seu barco \n\tna posição " +
                                    tab_defesa.retornarLinha(linha) +
                                    tab_defesa.retornarColuna(coluna) + "\n");
                            
                            println("\n\tTabuleiro defesa\n " + tab_defesa);
                            
                            mensagemBytes = new byte[T];
                            mensagemBytes = ("A"+linha+coluna).getBytes();
                            DatagramPacket pacoteEnvio = new DatagramPacket(
                                mensagemBytes, mensagemBytes.length, pacote.getAddress(), pacote.getPort());
                            conexao.send(pacoteEnvio);
                            
                            jogador.setPerdas();

                            if(tab_defesa.estaDestruido()){
                                jogador.setDerrotas();
                            }
                            
                            println("\n\tEnviando confirmação de ataque \n\tpara adversário("+
                                    pacote.getAddress()+")");
                        // ...senão
                        }
                    }else if(protocolo.equals("A")){
                        linha = Integer.parseInt(mensagemRecebimento.substring(1, 2));
                        coluna = Integer.parseInt(mensagemRecebimento.substring(2, 3));
                        
                        jogador.setAcertos();
                        
                        println("\n\tVocê acertou um barco do adversário("+pacote.getAddress()+")\n\tna posição " +
                                tab_defesa.retornarLinha(linha) + tab_defesa.retornarColuna(coluna));
                    // Se recebido o protocolo "F" ou "E" é impresso o conteúdo do mesmo.
                    }else if((protocolo.equals("F")) || (protocolo.equals("E"))){
                        
                        String nome, acertos, derrotas;
                        nome = mensagemRecebimento.substring(
                            1, mensagemRecebimento.indexOf("T"));
                        acertos = mensagemRecebimento.substring(
                            (mensagemRecebimento.indexOf("T") + 1),
                            mensagemRecebimento.indexOf("P"));
                        derrotas = mensagemRecebimento.substring(
                            (mensagemRecebimento.indexOf("P") + 1),
                            mensagemRecebimento.length());
                        
                        print("\n" + (protocolo.equals("E")?"\tEstatísticas: ":"") + "\tNome: " + nome + " Acertos: " + acertos + 
                              " Derrotas: " + derrotas +"\n\n");
                        
                        if(protocolo.equals("F")){
                            println("\n\tEtapa de ataque finalizada.\n");
                            principal.stop();
                        }
                        
                        // Se o protocolo recebido for "F" é enviado por broadcast
                        // uma mensagem com o protocolo "E" com as estatísticas.
                        if(protocolo.equals("F")){
                            mensagemBytes = new byte[T];
                            mensagemBytes = ("E"+jogador.getNome()
                                +"T"+jogador.getAcertos()+"P"+jogador.getDerrotas()).getBytes();

                            DatagramPacket pacoteEnvio = new DatagramPacket(
                                mensagemBytes, mensagemBytes.length, enderecoBroadcast, PORTA);
                            conexao.send(pacoteEnvio);
                        }
                    }
                }catch(Exception e){}
            }
        } 
    }
    
    /**
     * Abreviação para o método System.out.println
     * @param m Qualquer tipo de objeto.
     */
    private static void print(Object m){
        System.out.print(m);
    }
    
    /**
     * Abreviação para o método System.out.println
     * @param m Qualquer tipo de objeto.
     */
    private static void println(Object m){
        System.out.println(m);
    }
    
    
}
