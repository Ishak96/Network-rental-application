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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

import Util.ClientTCP;
import log.LoggerUtility;
 
public class RegisterDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LoggerUtility.getLogger(RegisterDialog.class);

	private ClientTCP clientTCP;
	
	private JPanel mainPanel;
	private JPanel btnPane;
	
	private JLabel lbNom;
	private JTextField tfNom;
	private JLabel lbPrenom;
	private JTextField tfPrenom;
	private JLabel lbAdress;
	private JTextField tfAdress;
	private JLabel lbTelephone;
	private JTextField tfTelephone;
	private JLabel lbMetier;
	private JTextField tfMetier;
	private JLabel lbDiplome;
	private JTextField tfDiplome;
	private JLabel lbExp;
	private JTextField tfExp;
	private JLabel lbMail;
	private JTextField tfMail;
	private JLabel lbPaswd;
    private JPasswordField pfPaswd;
    
    private JCheckBox checkPro;
    private JButton btnLogin;
    private JButton btnCancel;
 
    public RegisterDialog(Frame parent) {
        super(parent, "Inscription", true);
        clientTCP = ClientTCP.getInstance();
        verifyConnexion();
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
        
        lbNom = new JLabel("Nom: "); 
        tfNom = new JTextField(20);
        addToMainPanel(lbNom,0,0,1);
        addToMainPanel(tfNom,1,0,2);

 
        lbPrenom = new JLabel("Prenom: ");
        tfPrenom = new JTextField(20);
        addToMainPanel(lbPrenom,0,1,1);
        addToMainPanel(tfPrenom,1,1,2);
        
        lbAdress = new JLabel("Adresse: ");
        tfAdress = new JTextField(30);
        addToMainPanel(lbAdress,0,2,1);
        addToMainPanel(tfAdress,1,2,2);
        
        lbTelephone = new JLabel("Telephone: ");
        tfTelephone = new JTextField(15);
        addToMainPanel(lbTelephone,0,3,1);
        addToMainPanel(tfTelephone,1,3,2);
        
        lbMail = new JLabel("Mail: ");
        tfMail = new JTextField(30);
        addToMainPanel(lbMail,0,4,1);
        addToMainPanel(tfMail,1,4,2);
        
        lbPaswd = new JLabel("Mot de passe: ");
        pfPaswd = new JPasswordField(20);
        addToMainPanel(lbPaswd,0,5,1);
        addToMainPanel(pfPaswd,1,5,2);
        
        checkPro = new JCheckBox("Professionnel",false);
        checkPro.addActionListener(this);
        addToMainPanel(checkPro,1,6,2);
        
        lbMetier = new JLabel("Métier: ");
        tfMetier = new JTextField(20);
        
        lbDiplome = new JLabel("Diplome: ");
        tfDiplome = new JTextField(20);
        
        lbExp = new JLabel("Experience: ");
        tfExp = new JTextField(2);
        
        
        btnPane = new JPanel();
        
        btnLogin = new JButton("Inscription");
        btnLogin.addActionListener(this);
        
        btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(this);

        btnPane.add(btnLogin);
        btnPane.add(btnCancel);
 
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(btnPane, BorderLayout.PAGE_END);
    }
    
    private void toggleProFields()
    {   	
    	if(checkPro.isSelected()) {
    		addToMainPanel(lbMetier,0,7,1);
            addToMainPanel(tfMetier,1,7,2);
            addToMainPanel(lbDiplome,0,8,1);
            addToMainPanel(tfDiplome,1,8,2);
            addToMainPanel(lbExp,0,9,1);
            addToMainPanel(tfExp,1,9,2);
    	}
    	else{
    		mainPanel.remove(lbMetier);
    		mainPanel.remove(tfMetier);
    		mainPanel.remove(lbDiplome);
    		mainPanel.remove(tfDiplome);
    		mainPanel.remove(lbExp);
    		mainPanel.remove(tfExp);
    	}       
        pack();
    }
    
    public String registerString()
    {	
    	String infos = tfNom.getText() +":"+
    				   tfPrenom.getText() +":"+
    				   tfAdress.getText() +":"+
    				   tfTelephone.getText() +":"+
    				   tfMail.getText() +":"+
    				   new String(pfPaswd.getPassword());			
    			
    	if(checkPro.isSelected()) {
    		return "professionnels:"+infos+":"+
    				tfMetier.getText() +":"+
    				tfDiplome.getText() +":"+
    				tfExp.getText();
    	}
    	else {
    		return "clients:"+infos;
    	}
    }
    
    public void register() throws SocketTimeoutException,IOException,ArrayIndexOutOfBoundsException
    {
    	String[] infos = clientTCP.getData("SINGIN","DATA",registerString());
    	if(infos!=null) {
    		if(infos[0].equals("OK")) {
    			dispose();
				JOptionPane.showMessageDialog(this,"Inscription réussie!","Inscription",JOptionPane.INFORMATION_MESSAGE);
        	}
        	else if(infos[0].equals("NO")) {
    			JOptionPane.showMessageDialog(this,infos[1],"Inscription",JOptionPane.ERROR_MESSAGE);
        	}
        	else {
	        	logger.error("trame invalide envoyée par le serveur");
            	JOptionPane.showMessageDialog(this,"trame invalide envoyée par le serveur","Inscription",JOptionPane.ERROR_MESSAGE);
        	}
    	}
    	else {
        	JOptionPane.showMessageDialog(this,"réponse de serveur inattendue.","Inscription",JOptionPane.ERROR_MESSAGE);
    	}
    }

	@Override
	public void actionPerformed(ActionEvent event)
	{	
		switch (event.getActionCommand())
		{		
			case "Inscription":
				try {
					logger.info("<INSCRIPTION>");
					register();
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
				break;
				
			case "Annuler":
				dispose();
				break;
			
			case "Professionnel":
				toggleProFields();
				break;
		}
		
	}
	
}

