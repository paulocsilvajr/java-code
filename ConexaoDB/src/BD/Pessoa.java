
package BD;

import java.sql.Date;

public class Pessoa {
    private String cpf, rg, nome;
    private Date data_inclusao;

    public Date getData_inclusao() {
        return data_inclusao;
    }

    public void setData_inclusao(Date data_inclusao) {
        this.data_inclusao = data_inclusao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    @Override
    public String toString(){
        return String.format("CPF: %s\nRG: %s\nNome: %s\nData inclusão: %s",
                cpf, rg, nome, data_inclusao);
    }
}
