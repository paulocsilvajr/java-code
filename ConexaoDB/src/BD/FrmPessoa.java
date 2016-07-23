
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
import java.util.List;
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
    private List<Pessoa> lista;
    private Conexao con;
    private PessoaDAO pes;
    
    private boolean exibirConsulta = false;
    private final int X_FORM = 460, Y_FORM = 170;
    
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
                lista = pes.gerarListaPessoas();
                if(exibirConsulta)
                    alimentarTable();
            }
        });
        
        txtPesquisa = new JTextField();
        txtPesquisa.setBounds(10, 170, 320, 20);
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt){
                pesquisar(evt);
            }  
        });
        
        cbxPesquisa = new JComboBox();
        cbxPesquisa.setBounds(330, 170, 110, 20);
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
                
                int coluna = tblPesquisar.getSelectedRow();
                txtCpf.setText((String)tblPesquisar.getValueAt(coluna, 0));
                txtRg.setText((String)tblPesquisar.getValueAt(coluna, 1));
                txtNome.setText((String)tblPesquisar.getValueAt(coluna, 2));
                lblDataInclusao.setText(tblPesquisar.getValueAt(coluna, 3).toString());
                
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
        scrollPane.setBounds(10, 200, 440, 220);
        
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
        
        txtPesquisa.setEnabled(exibirConsulta);
        cbxPesquisa.setEnabled(exibirConsulta);
        tblPesquisar.setEnabled(exibirConsulta);
        scrollPane.setEnabled(exibirConsulta);
    }
    
    public MaskFormatter mascara(String Mascara){
        // ref. http://www.guj.com.br/t/mascara-em-um-jtextfield/30077/4
        MaskFormatter F_Mascara = new MaskFormatter();
        
        try{
            F_Mascara.setMask(Mascara); //Atribui a mascara
            F_Mascara.setPlaceholderCharacter(' '); //Caracter para preencimento 
        }
        catch (Exception excecao) {
            excecao.printStackTrace();
        } 
        
        return F_Mascara;
    }
    
    private void exibirConsulta(){
        exibirConsulta = !exibirConsulta;
                
        if(exibirConsulta)
            setSize(X_FORM, Y_FORM + 260);
        else
            setSize(X_FORM,Y_FORM);
        
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
        for(Pessoa peslist: lista){
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
            p.setRg(txtRg.getText());
            p.setNome(txtNome.getText());
            
            boolean alterar = false;
            for(Pessoa ps: lista){
                if(ps.getCpf().equals(txtCpf.getText()))
                    alterar = true;
            }
            
            String acao;
            if(alterar){
                pes.atualizar(p);
                acao = "Atualização";
            }else{
                pes.inserir(p);
                acao = "Cadastro";
            }
                
            JOptionPane.showMessageDialog(FrmPessoa.this, acao + " realizado com sucesso", 
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            
            limpar();   
        }
        
    }
    
    private void limpar(){
        txtCpf.setText("");
        txtRg.setText("");
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
