
package BD;

import java.sql.Timestamp;

public class Pessoa {
    public static final String NOME_TABELA = "pessoas";
    public static final String[] NOME_CAMPOS = {"cpf", "rg", "nome", "data_inclusao"};
    private String cpf = "", rg = "", nome = "";
    private Timestamp data_inclusao;
    private boolean[] altValores = {false, false, false, false};
    
    public Pessoa(){};

    public Pessoa(String cpf, String nome){
        setCpf(cpf);
        setNome(nome);
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        altValores[0] = !this.cpf.equals(cpf);
        this.cpf = cpf;
    }

    public String getRg() {
        return rg != null?rg:"";
    }

    public void setRg(String rg) {
        altValores[1] = !this.cpf.equals(rg);
        this.rg = rg;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        altValores[2] = !this.nome.equals(nome);
        this.nome = nome;
    }
    
    public Timestamp getData_inclusao() {
        return data_inclusao;
    }

    public void setData_inclusao(Timestamp data_inclusao) {
        altValores[3] = this.data_inclusao != data_inclusao;
        this.data_inclusao = data_inclusao;
    }    
    
    @Override
    public String toString(){
        return String.format("CPF: %s\nRG: %s\nNome: %s\nData inclusão: %s",
                getCpf(), getRg(), getNome(), getData_inclusao());
    }

    public boolean[] getAltValores() {
        return altValores;
    }    
    
    public void zerarAltValores(){
        for(int i=0; i < altValores.length; i++){
            altValores[i] = false;
        }
    }
}
