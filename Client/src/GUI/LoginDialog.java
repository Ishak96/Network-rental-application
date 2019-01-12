package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

import Util.ClientTCP;
import Util.ServerParameters;
import log.LoggerUtility;
 
public class LoginDialog extends JDialog implements ActionListener {
	
 
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LoggerUtility.getLogger(LoginDialog.class);
	
	private ClientTCP clientTCP;
	
	private Frame parent;
	
	private JPanel mainPanel;
	private JPanel btnPane;
	
	private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnLogin;
    private JButton btnCancel;
 
    public LoginDialog(Frame parent) {
        super(parent, "Connexion", true);
        clientTCP = ClientTCP.getInstance();
        verifyConnexion();
        this.parent = parent;
        initComponents();
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        setVisible(true);
        
    }
    
    private void verifyConnexion()
    {
    	if(clientTCP.portFailed()) {
			JOptionPane.showMessageDialog(this,"Echec de la connexion au serveur\n"
					+ "problème de ports\n"
					+"Veuillez redémmarer l'application.","Erreur",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
        }
    	else if(clientTCP.unreachableHost()) {
			JOptionPane.showMessageDialog(this,"Echec de la connexion au serveur\n"
					+ "IP non atteignable\n"
					+"Veuillez redémmarer l'application.","Erreur",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
    	}
    	else if(clientTCP.connexionFailed()) {
			JOptionPane.showMessageDialog(this,"Echec de la connexion au serveur\n"
					+ "DNS non résolu\n"
					+"Veuillez redémmarer l'application.","Erreur",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
        }
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
        
        lbUsername = new JLabel("Adresse mail: ");
        addToMainPanel(lbUsername,0,0,1);
         
        tfUsername = new JTextField(20);
        addToMainPanel(tfUsername,1,0,2);

 
        lbPassword = new JLabel("Mot de passe: ");
        addToMainPanel(lbPassword,0,1,1);
 
        pfPassword = new JPasswordField(20);
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
 
    
    public String loginString()
    {
    	return tfUsername.getText().trim() +":"+
    		   new String(pfPassword.getPassword());
    }

    
    private void login() throws SocketTimeoutException,IOException,ArrayIndexOutOfBoundsException
    {	
    	String[] infos = clientTCP.getData("LOGIN","DATA",loginString());
    	if(infos!=null) {
    		if(infos[0].equals("OK")) {
        		new UserSpace(infos);
                parent.dispose();
        	}
        	else if(infos[0].equals("NO")) {
    			JOptionPane.showMessageDialog(this,infos[1],"Connexion",JOptionPane.ERROR_MESSAGE);
        	}
        	else {
	        	logger.error("trame invalide envoyée par le serveur");
            	JOptionPane.showMessageDialog(this,"trame invalide envoyée par le serveur","Connexion",JOptionPane.ERROR_MESSAGE);
        	}
    	}
    	else {
        	JOptionPane.showMessageDialog(this,"réponse de serveur inattendue.","Connexion",JOptionPane.ERROR_MESSAGE);
    	}
    }
 

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Connexion")) {
	    	try {
				logger.info("<CONNEXION>");
				login();
	    	} catch (ArrayIndexOutOfBoundsException e1) {
	        	logger.error("trame invalide envoyée par le serveur");
	        	JOptionPane.showMessageDialog(this,"trame invalide envoyée par le serveur","Erreur",JOptionPane.ERROR_MESSAGE);
			} catch (SocketTimeoutException e2) {
				logger.error("Délai d'attente dépassé");
				JOptionPane.showMessageDialog(this,"Délai d'attente dépassé","Erreur",JOptionPane.ERROR_MESSAGE);
			} catch (IOException e3) {
				if(e3.getMessage().equals("SERVER_DOWN")) {
					logger.error("Connexion interrompue avec le serveur");
					JOptionPane.showMessageDialog(this,"Connexion interrompue avec le serveur","Erreur",JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
				else {
					logger.error("erreur d'entrée/sortie");
					JOptionPane.showMessageDialog(this,"erreur d'entrée/sortie","Erreur",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if(e.getActionCommand().equals("Annuler")) {
			dispose();
		}		
	}
	
}

