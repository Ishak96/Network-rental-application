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
import Util.User;
import log.LoggerUtility;
 
public class LogoutDialog extends JDialog implements ActionListener {
	
	private static Logger logger = LoggerUtility.getLogger(LogoutDialog.class);
 
	private static final long serialVersionUID = 1L;
	
	private ClientTCP clientTCP;
	
	@SuppressWarnings("unused")
	private Frame parent;
	private User user;
	
	private JPanel mainPanel;
	private JPanel btnPane;
	
	private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JButton btnLogin;
    private JButton btnCancel;
 
    public LogoutDialog(Frame parent,User user) {
        super(parent, "Deconnexion", true);
        clientTCP = ClientTCP.getInstance();
        verifyConnexion();
        this.parent = parent;
        this.user=user;
        initComponents();
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        setVisible(true);
        
    }
    
    private void verifyConnexion()
    {
    	if(clientTCP.connexionFailed()) {
			JOptionPane.showMessageDialog(this,"Echec de la connexion au serveur\n"
					+ "Veuillez redémmarer l'application.","Erreur",JOptionPane.ERROR_MESSAGE);
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
        
        lbUsername = new JLabel("Voulez vous vraiment quitez votre session ?");
        addToMainPanel(lbUsername,0,0,1);
        
        btnPane = new JPanel();
        
        btnLogin = new JButton("Oui");
        btnLogin.addActionListener(this);
        
        btnCancel = new JButton("Non");
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
	public void logOut() throws SocketTimeoutException,IOException,ArrayIndexOutOfBoundsException
	{
		String[] infos = clientTCP.getData("LOGOUT","ID",user.getID());
    	if(infos!=null) {
			clientTCP.closeConnexion();
    	}
    	else {
        	JOptionPane.showMessageDialog(this,"réponse de serveur inattendue.","déconnexion",JOptionPane.ERROR_MESSAGE);
    	}		
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Oui")) {
			try {
				logger.info("<DÉCONNEXION>");
				logOut();
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
		if(e.getActionCommand().equals("Non")) {
			dispose();
		}		
	}	
}