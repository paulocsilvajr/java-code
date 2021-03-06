
package BD;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

/**
 * Classe do formulário de gerenciamento de pessoas.
 * @author Paulo C. Silva Jr.
 */
public class FrmPessoa extends JFrame{
    private JLabel lblCpf, lblRg, lblNome, lblData, lblDataInclusao;
    private JButton btnSalvar, btnLimpar, btnExcluir, btnConsultar;
    private JComboBox cbxPesquisa;
    private JTextField txtNome, txtPesquisa;
    private JFormattedTextField txtCpf, txtRg;
    private JTable tblPesquisar;
    private DefaultTableModel defaultTableModel;
    private JScrollPane scrollPane;
    private TableRowSorter sorter;
    
    private Pessoa p;
    private Conexao con;
    // Para a listagem de pessoas é utilizado o atributo lista contido em 
    // PessoaDAO(usado nos métodos alimentarTable e salvar)
    private PessoaDAO pes;
    
    private boolean exibirConsulta = false;
    private final int X_FORM = 460, Y_FORM = 200;
    
    FrmPessoa(Conexao con){
        this.con = con;
        pes = new PessoaDAO(con);
        initComponent();
    }
    
    private void initComponent(){
        setTitle("Cadastro de Pessoas");
        setSize(X_FORM,Y_FORM);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(null);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width / 2) - (X_FORM / 2), 0);
        setLayout(null);
        
        lblCpf = new JLabel("CPF");
        lblCpf.setBounds(10, 10, 110, 20);
        
        txtCpf = new JFormattedTextField(mascara("###.###.###-##"));
        txtCpf.setBounds(120, 10, 150, 20);
        
        lblRg = new JLabel("RG");
        lblRg.setBounds(10, 40, 110, 20);
        
        txtRg = new JFormattedTextField(mascara("##.###.###-#"));
        txtRg.setBounds(120, 40, 150, 20);
        
        lblNome = new JLabel("Nome");
        lblNome.setBounds(10, 70, 110, 20);
        
        txtNome = new JTextField();
        txtNome.setBounds(120, 70, 330, 20);
        
        lblData = new JLabel("Data inclusão");
        lblData.setBounds(10, 100, 110, 20);
        
        lblDataInclusao = new JLabel("  -  -     :  :  .    ");
        lblDataInclusao.setBounds(120, 100, 200, 20);
        
        btnSalvar = new JButton("Salvar");
        btnSalvar.setMnemonic('s');
        btnSalvar.setBounds(10, 130, 100, 30);
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvar();
            }
        });
        
        btnLimpar = new JButton("Limpar");
        btnLimpar.setMnemonic('l');
        btnLimpar.setBounds(120, 130, 100, 30);
        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpar();
            }
        });
        
        btnExcluir = new JButton("Excluir");
        btnExcluir.setMnemonic('e');
        btnExcluir.setBounds(230, 130, 100, 30);
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluir();
            }
        });
        
        btnConsultar = new JButton("Consultar");
        btnConsultar.setMnemonic('c');
        btnConsultar.setBounds(340, 130, 110, 30);
        btnConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirConsulta();
                // O método gerarListaPessoas consulta o BD e alimenta a lista com pessoas.
                // Internamente ele é invocado no construtor de PessoaDAO.
                // As operações e inserir, excluir e alterar são feitas tanto na 
                // tabela do BD, quanto no objeto lista. Portanto não é necessário 
                // a invocação para uma consulta, mas executando esse método, 
                // garantirá a similaridade entre BD e lista, causando custo de 
                // transferência de dados a cada execução.
//                pes.gerarListaPessoas();
                
                if(exibirConsulta)
                    alimentarTable();
            }
        });
        
        txtPesquisa = new JTextField();
        txtPesquisa.setBounds(10, 200, 320, 20);
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt){
                pesquisar(evt);
            }  
        });
        
        cbxPesquisa = new JComboBox();
        cbxPesquisa.setBounds(330, 200, 110, 20);
        cbxPesquisa.addItem("cpf");
        cbxPesquisa.addItem("rg");
        cbxPesquisa.addItem("nome");
        
        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("cpf");
        defaultTableModel.addColumn("rg");
        defaultTableModel.addColumn("nome");
        defaultTableModel.addColumn("data_inclusao");
        
        tblPesquisar = new JTable(defaultTableModel);
        tblPesquisar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblPesquisar.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblPesquisar.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblPesquisar.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblPesquisar.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblPesquisar.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                limpar();
                
                int linha = tblPesquisar.getSelectedRow();
                txtCpf.setText((String)tblPesquisar.getValueAt(linha, 0));
                txtRg.setText((String)tblPesquisar.getValueAt(linha, 1));
                txtNome.setText((String)tblPesquisar.getValueAt(linha, 2));
                lblDataInclusao.setText(tblPesquisar.getValueAt(linha, 3).toString());
                
                exibirConsulta();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        
        sorter = new TableRowSorter(defaultTableModel);
        tblPesquisar.setRowSorter(sorter);
        
        scrollPane = new JScrollPane(tblPesquisar);
        scrollPane.setBounds(10, 230, 440, 220);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                con.fechar();
            }
        });
        
        add(lblCpf);
        add(txtCpf);
        add(lblRg);
        add(txtRg);
        add(lblNome);
        add(txtNome);
        add(lblData);
        add(lblDataInclusao);
        add(btnSalvar);
        add(btnLimpar);
        add(btnExcluir);
        add(btnConsultar);
        add(txtPesquisa);
        add(cbxPesquisa);
//        add(tblPesquisar); // Não é necessário add a tabela porque está anexada ao scrollpane.
        add(scrollPane);
        
        alterarStatusCompPesquisa();
    }
    
    public MaskFormatter mascara(String mascara){
        // ref. http://www.guj.com.br/t/mascara-em-um-jtextfield/30077/4
        MaskFormatter F_Mascara = new MaskFormatter();
        
        try{
            F_Mascara.setMask(mascara); //Atribui a mascara
            F_Mascara.setPlaceholderCharacter(' '); //Caracter para preencimento 
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        } 
        
        return F_Mascara;
    }
    
    private void exibirConsulta(){
        exibirConsulta = !exibirConsulta;
                
        if(exibirConsulta)
            setSize(X_FORM, Y_FORM + 290);
        else
            setSize(X_FORM,Y_FORM);
        
        alterarStatusCompPesquisa();
        txtPesquisa.requestFocus();
    }
    
    private void alterarStatusCompPesquisa(){
        txtPesquisa.setEnabled(exibirConsulta);
        cbxPesquisa.setEnabled(exibirConsulta);
        tblPesquisar.setEnabled(exibirConsulta);
        scrollPane.setEnabled(exibirConsulta);
    }
    
    private void alimentarTable(){
        if(defaultTableModel.getRowCount() > 0){
            for(int i=(defaultTableModel.getRowCount() - 1); i>=0; i--){
                defaultTableModel.removeRow(i);
            }
        }
        
        String cpf, rg, nome;
        Timestamp data_inclusao;
        for(Pessoa peslist: pes.getListaPessoas()){
            cpf = peslist.getCpf();
            rg = peslist.getRg();
            nome = peslist.getNome();
            data_inclusao = peslist.getData_inclusao();
            
            defaultTableModel.addRow(new Object[]{cpf, rg, nome, data_inclusao});
        }
    }
    
    private void pesquisar(KeyEvent evt){
        if(txtPesquisa.getText().isEmpty())
            sorter.setRowFilter(null);
        else{
            int coluna = cbxPesquisa.getSelectedIndex();
            
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtPesquisa.getText(), coluna));
        }
    }
    
    private void salvar(){
        if(txtCpf.getText().isEmpty()){
            JOptionPane.showMessageDialog(FrmPessoa.this, "CPF vazio");
            txtCpf.requestFocus();
        }else if(txtNome.getText().isEmpty()){
            JOptionPane.showMessageDialog(FrmPessoa.this, "Nome vazio");
            txtNome.requestFocus();
        }else{
            p = new Pessoa();
            p.setCpf(txtCpf.getText());
            p.setRg(txtRg.getText().equals("  .   .   - ")?"":txtRg.getText());
            p.setNome(txtNome.getText());
            
            // Procura-se se tem uma pessoa com o cpf informado, caso encontre é 
            // setado true na variável de alteração(alterar), incluído a data 
            // de inclusão na pessoa e parado o laço de pesquisa.
            boolean alterar = false;
            for(Pessoa ps: pes.getListaPessoas()){
                if(ps.getCpf().equals(txtCpf.getText())){
                    alterar = true;
                    p.setData_inclusao(ps.getData_inclusao());
                    break;
                }
            }
            
            // teste
//            System.out.println(p);
            //
            
            String acao;
            if (alterar){
                // Alteração somente se existir essa pessoa(CPF) na lista.
                pes.atualizar(p);
                acao = "Atualização realizada";
            }else{
                // Somente se é uma nova pessoa(novo CPF).
                pes.inserir(p);
                acao = "Cadastro realizado";
            }
                
            JOptionPane.showMessageDialog(FrmPessoa.this, acao + " com sucesso", 
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            
            limpar();   
        }
        
    }
    
    private void limpar(){
        // Para limpar jFormatedTextField  deve ser usado o método setValue(null).
        txtCpf.setValue(null);
        txtRg.setValue(null);
        txtNome.setText("");
        lblDataInclusao.setText("  -  -     :  :  .    ");
        
        txtCpf.requestFocus();
    }
    
    private void excluir(){
        if(txtCpf.getText().isEmpty()){
            JOptionPane.showMessageDialog(FrmPessoa.this, "Pesquise uma pessoa para excluir", 
                    "Atenção", JOptionPane.ERROR_MESSAGE);
            txtCpf.requestFocus();
        }else{
            if(JOptionPane.showConfirmDialog(FrmPessoa.this, "Deseja realmente excluir esta pessoa?", 
                    "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0){
                p = new Pessoa();
                p.setCpf(txtCpf.getText());
                p.setRg(txtRg.getText());
                p.setNome(txtNome.getText());

                pes.excluir(p);

                JOptionPane.showMessageDialog(FrmPessoa.this, "Excluido pessoa com sucesso", 
                        "Atenção", JOptionPane.ERROR_MESSAGE);

                limpar();
            }
        }
    }
}
