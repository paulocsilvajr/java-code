
package BD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Classe Pessoa Data Access Object.
 * Fornece uma interface para manipulação dos dados da tabela pessoa e 
 * possue um objeto lista(invocado e alimentado por gerarListaPessoas)
 * contendo os dados da tabela citada. 
 * @author Paulo C. Silva Jr.
 * Baseado em: http://pt.stackoverflow.com/questions/63778/conex%C3%A3o-ao-banco-de-dados-mysql-e-java
 */
public class PessoaDAO {
    // DAO = Data Access Object, Objeto de Acesso a Dados.
    private final Conexao CON;
    private List<Pessoa> lista = new ArrayList<>();
    private ResultSet resultado;
            
    /**
     * Construtor da classe de acesso a objetos Pessoa. 
     * List lista é alimentado na construção do objeto com os dados da tabela pessoa.
     * @param con Conexão com banco de dados, previamente criada.
     */
    public PessoaDAO(Conexao con){
        this.CON = con;
        gerarListaPessoas();
    }
    
    /**
     * Retorna objetos pessoa contidos em lista.
     * @return List de pessoas.
     */
    public List<Pessoa> getListaPessoas(){
        return lista;
    }
    
    /**
     * Retorna ResultSet resultante de consulta.
     * @return ResultSet de pessoas.
     */
    public ResultSet getResultado(){
        return resultado;
    }
    
    /**
     * Gerador de lista de pessoas a partir de consulta à tabela pessoas.
     * @return List contendo todos os registro da tabela citada.
     */
    public List<Pessoa> gerarListaPessoas(){
        lista.clear();
        try{
            resultado = CON.consultar(Pessoa.NOME_TABELA);
            
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
    
    /**
     * Retorna objeto pessoa referente ao parametro chave(cpf) informado.
     * @param cpf CPF da pessoa.
     * @return Pessoa obtida da consulta.
     */
    public Pessoa consultar(String cpf){
        Pessoa pessoa = new Pessoa();
        try{
            resultado = CON.consultar(
                    Pessoa.NOME_TABELA, "*", String.format(" cpf = \'%s\'", cpf), "cpf", 1);
            
            while(resultado.next()){
                pessoa.setCpf(resultado.getString(Pessoa.NOME_CAMPOS[0]));
                pessoa.setRg(resultado.getString(Pessoa.NOME_CAMPOS[1]));
                pessoa.setNome(resultado.getString(Pessoa.NOME_CAMPOS[2]));
                pessoa.setData_inclusao(resultado.getTimestamp(Pessoa.NOME_CAMPOS[3]));
            }
        }catch(SQLException ex){
            System.err.println("Excessão SQL: " + ex.getMessage());
        }
        
        return pessoa;
    }
    
    /**
     * Gerador de lista contendo pessoas de acordo com parametros informados.
     * Exibe todos as colunas, mas somente os registro filtrados.
     * @param filtro Qualificação dos registros. Usar =, <>, like '%texto%', ...
     * @param ordenacao Ordem dos registros. Usar and caso seja informado mais de uma coluna.
     * @param quant Quantidade de registros resgatados.
     * @return List de pessoas.
     */
    public List<Pessoa> consultar(String filtro, String ordenacao, int quant){
        lista.clear();
        try{
            resultado = CON.consultar(Pessoa.NOME_TABELA, "", filtro, ordenacao, quant);
            
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
    
    /**
     * Inserção de novos registro no banco de dados e no objeto lista, 
     * descartando a necessidade de consulta para a realimentar a lista.
     * @param p Objeto Pessoa contendo os dados que serão gravados.
     */
    public void inserir(Pessoa p){
        String campos = String.format("%s, %s, %s", 
                Pessoa.NOME_CAMPOS[0], Pessoa.NOME_CAMPOS[1], Pessoa.NOME_CAMPOS[2]);
        
        String valores = p.getRg().isEmpty()?
                String.format("\'%s\', NULL, \'%s\'", p.getCpf(), p.getNome()):
                String.format("\'%s\', \'%s\', \'%s\'", p.getCpf(), p.getRg(), p.getNome()); 
        
        CON.inserir(Pessoa.NOME_TABELA, campos, valores);
        
        // Atribuindo timestamp para o objeto pessoa, que será adicionado a lista.
        // Essa atribuição é somente ao objeto pessoa inserido na lista, o registro de 
        // pessoa inserido no banco de dados é automaticamente atribuido
        // pelo campo default da tabela, portando ocorrerá diferença entre os objetos.
        Date agora = new Date();
        p.setData_inclusao(new Timestamp(agora.getTime()));
        lista.add(p);
    }
    
    /**
     * Alteração de Pessoa informada.
     * O objeto lista é atualizado juntamente com a base de dados, portanto
     * não é necessário fazer uma nova consulta para visualizar a lista de pessoas 
     * com o registro alterado.
     * @param p Pessoa com os dados alterados.
     */
    public void atualizar(Pessoa p){
        String campos = "", valores = "", filtro;
        filtro = String.format("%s = \'%s\'", Pessoa.NOME_CAMPOS[0], p.getCpf());
        
        if(p.getAltValores()[0]){
            campos +=  String.format("%s, ", Pessoa.NOME_CAMPOS[0]);
            valores += String.format("\'%s\', ", p.getCpf());
        }
        
        if(p.getAltValores()[1]){
            campos += String.format("%s, ", Pessoa.NOME_CAMPOS[1]);
            if(p.getRg().isEmpty())
                valores += "NULL, ";
            else
                valores += String.format("\'%s\', ", p.getRg());
        }
        
        if(p.getAltValores()[2]){
            campos +=  String.format("%s, ", Pessoa.NOME_CAMPOS[2]);
            valores += String.format("\'%s\', ", p.getNome());
        }
        
        campos = campos.substring(0, (campos.length() - 2));
        valores = valores.substring(0, (valores.length() - 2));
        
        CON.atualizar(Pessoa.NOME_TABELA, campos, valores, filtro);
        
        // Procura-se a pessoa na lista, remove e para o laço de pesquisa.
        for(Pessoa pl: lista){
            if(pl.getCpf().equals(p.getCpf())){
                lista.remove(pl);
                break;
            }
                
        }
        // Adiciona a nova pessoa no fim da lista.
        lista.add(p);
        
        p.zerarAltValores();
    }
    
    /**
     * Exclusão de pessoa, tanto no banco de dados, quanto no objeto lista.
     * @param p Pessoa cadastrada.
     */
    public void excluir(Pessoa p){
        String filtro = String.format("cpf = \'%s\'", p.getCpf());
        CON.excluir(Pessoa.NOME_TABELA, filtro);
        
        if(lista.contains(p))
            lista.remove(p);
    }
}
