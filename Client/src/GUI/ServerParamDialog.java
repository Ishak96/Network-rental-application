package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import Util.ClientTCP;
import Util.ServerParameters;
 
public class ServerParamDialog extends JDialog implements ActionListener {
	
 
	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JPanel btnPane;
	
	private JTextField tfUsername;
    private JTextField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnLogin;
    private JButton btnCancel;
 
    public ServerParamDialog(Frame parent) {
        super(parent, "Les paramètres du serveur", true);
        initComponents();
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        setVisible(true);
        
    }
    
    private void addToMainPanel(Component comp, int x,int y,int width)
    {
    	GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.gridx = x;
        cs.gridy = y;
        cs.gridwidth = width;
        mainPanel.add(comp,cs);
    }
    
    private void initComponents()
    {
    	mainPanel = new JPanel(new GridBagLayout());
    	mainPanel.setBorder(new LineBorder(Color.GRAY));
        
        lbUsername = new JLabel("Adresse IP du serveur: ");
        addToMainPanel(lbUsername,0,0,1);
         
        tfUsername = new JTextField(20);
        addToMainPanel(tfUsername,1,0,2);

 
        lbPassword = new JLabel("Le port: ");
        addToMainPanel(lbPassword,0,1,1);
 
        pfPassword = new JTextField(20);
        addToMainPanel(pfPassword,1,1,2);
        
        btnPane = new JPanel();
        
        btnLogin = new JButton("Connexion");
        btnLogin.addActionListener(this);
        
        btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(this);

        btnPane.add(btnLogin);
        btnPane.add(btnCancel);
 
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(btnPane, BorderLayout.PAGE_END);
    }

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Connexion")) {
			ServerParameters.setServ_addr(tfUsername.getText().trim());
			ServerParameters.setPort(Integer.parseInt(pfPassword.getText().trim()));
			ClientTCP.setInstance(ServerParameters.getServ_addr(),ServerParameters.getPort());
			dispose();
		}
		if(e.getActionCommand().equals("Annuler")) {
			dispose();
		}		
	}
	
}

