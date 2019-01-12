package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import Util.ClientTCP;
import Util.User;
import log.LoggerUtility;

public class UserSpace extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LoggerUtility.getLogger(UserSpace.class);
	
	private ClientTCP clientTCP;
	private User user;
	
	private JPanel contentPane;
	private JPanel menuPanel;
	private JPanel searchPanel;
	private JPanel accountPanel;
	
	private JTextField searchField;
	private JList<String> resultList;
	private JComboBox<String> prixComboBox;
	private JTextPane accountDetails;
	private JTextField tfupdate;
	
	private ButtonGroup radioBtnG;
	private JRadioButton rdbtnMail;
	private JRadioButton rdbtnPaswd;
	private JRadioButton rdbtnAdresse;
	private JRadioButton rdbtnTelephone;
		
	public UserSpace(String[]userInfos)
	{		
		super("Espace utilisateur");
		clientTCP = ClientTCP.getInstance();
        user = new User(userInfos);
		initComponents();
		showAccountDetails();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 540, 380);
		setResizable(false);
		setLocationRelativeTo(null);
        setVisible(true);
	}
	
	private void initComponents()
	{	
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		// menu:
		
		menuPanel = new JPanel();
		menuPanel.setBackground(Color.DARK_GRAY);
		
		JButton matButn = new JButton("Matériel");
		matButn.addActionListener(this);
		menuPanel.add(matButn);
		
		JButton updButn = new JButton("Mon compte");
		updButn.addActionListener(this);
		menuPanel.add(updButn);

		
		JButton contButn = new JButton("Contacts");
		contButn.addActionListener(this);
		menuPanel.add(contButn);
		
		JButton decButn = new JButton("Déconnexion");
		decButn.addActionListener(this);
		menuPanel.add(decButn);
		
		// Search:
		
		searchPanel = new JPanel();
		searchPanel.setBackground(Color.LIGHT_GRAY);
		searchPanel.setLayout(null);
		
		JLabel lblRechercherUnMatriel = new JLabel("Recherche du matériel");
		lblRechercherUnMatriel.setFont(new Font("Dialog", Font.BOLD, 18));
		lblRechercherUnMatriel.setBounds(136, 28, 245, 36);
		searchPanel.add(lblRechercherUnMatriel);
		
		searchField = new JTextField();
		searchField.setBounds(26, 76, 355, 26);
		searchPanel.add(searchField);
		searchField.setColumns(10);
		
		JButton btnRechercher = new JButton("Rechercher");
		btnRechercher.setBounds(386, 76, 114, 25);
		btnRechercher.addActionListener(this);
		searchPanel.add(btnRechercher);
		
		JLabel lblPrix = new JLabel("Prix:");
		lblPrix.setBounds(41, 119, 38, 15);
		searchPanel.add(lblPrix);
		
		prixComboBox = new JComboBox<String>();
		prixComboBox.addItem("Croissant");
		prixComboBox.addItem("Décroissant");		
		prixComboBox.setBounds(82,114, 121, 24);
		searchPanel.add(prixComboBox);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(25, 150, 475, 110);
		searchPanel.add(scrollPane);
		
		resultList = new JList<String>();
		scrollPane.setViewportView(resultList);
		resultList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				logger.info("<DÉTAILS MATERIEL>");
				showMaterialDetails();
			}
		});
		
		// account:
		
		accountPanel = new JPanel();
		accountPanel.setBackground(Color.LIGHT_GRAY);
		accountPanel.setLayout(null);
		
		JLabel lblMesInformations = new JLabel("Mes informations");
		lblMesInformations.setFont(new Font("Dialog", Font.BOLD, 18));
		lblMesInformations.setBounds(190, 12, 189, 15);
		accountPanel.add(lblMesInformations);
		
		accountDetails = new JTextPane();
		accountDetails.setBounds(68, 37, 403,142);
		accountDetails.setEditable(false);
		accountDetails.setFont(new Font("Dialog", Font.BOLD,12));
		accountPanel.add(accountDetails);
		
		JLabel lbMisajr = new JLabel("Mises à jours");
		lbMisajr.setFont(new Font("Dialog", Font.BOLD, 18));
		lbMisajr.setBounds(211, 178, 144, 36);
		accountPanel.add(lbMisajr);
		
		JLabel lbNewValue = new JLabel("Nouveau:");
		lbNewValue.setBounds(68, 253, 101, 15);
		accountPanel.add(lbNewValue);
		
		tfupdate = new JTextField();
		tfupdate.setBounds(144, 251, 267, 19);
		tfupdate.setColumns(10);
		accountPanel.add(tfupdate);
		
		JButton updateInfosBtn = new JButton("OK");
		updateInfosBtn.setActionCommand("update");
		updateInfosBtn.addActionListener(this);
		updateInfosBtn.setBounds(418, 250, 54, 20);
		accountPanel.add(updateInfosBtn);
		
		rdbtnMail = new JRadioButton("Mail");
		rdbtnMail.setActionCommand("mail");
		rdbtnMail.setBounds(68, 220, 54, 23);
		rdbtnMail.setSelected(true);
		accountPanel.add(rdbtnMail);
		
		rdbtnPaswd = new JRadioButton("Mot de passe");
		rdbtnPaswd.setActionCommand("mot_de_passe");
		rdbtnPaswd.setBounds(137, 220, 121, 23);
		accountPanel.add(rdbtnPaswd);
		
		rdbtnAdresse = new JRadioButton("Adresse");
		rdbtnAdresse.setActionCommand("adresse");
		rdbtnAdresse.setBounds(272, 220, 83, 23);
		accountPanel.add(rdbtnAdresse);
		
		rdbtnTelephone = new JRadioButton("Téléphone");
		rdbtnTelephone.setActionCommand("telephone");
		rdbtnTelephone.setBounds(370, 220, 101, 23);
		accountPanel.add(rdbtnTelephone);
		
		radioBtnG = new ButtonGroup();
		radioBtnG.add(rdbtnMail);
		radioBtnG.add(rdbtnPaswd);
		radioBtnG.add(rdbtnAdresse);
		radioBtnG.add(rdbtnTelephone);
		
		// add to content pane:
		
		contentPane.add(menuPanel, BorderLayout.NORTH);
		contentPane.add(searchPanel,BorderLayout.CENTER);
        
	}
	
	private void togglePanels(JPanel newPanel)
	{
		contentPane.removeAll();
		contentPane.add(menuPanel, BorderLayout.NORTH);
		contentPane.add(newPanel, BorderLayout.CENTER);
		validate();
		repaint();
	}
	
	public void showAccountDetails()
	{
		accountDetails.setText(user.toString());
		validate();
		repaint();
	}
	
	public String updateAccountString()
	{
		return user.getID()+":"+ 
			   radioBtnG.getSelection().getActionCommand()+":"+
			   tfupdate.getText();
	}
	
	public void updateAccount() throws SocketTimeoutException,IOException,ArrayIndexOutOfBoundsException
	{
		String[] infos = clientTCP.getData("UPDATE","DATA",updateAccountString());
    	if(infos!=null) {
    		if(infos[0].equals("OK")){
                if(rdbtnMail.isSelected()) user.setMail(tfupdate.getText());
                if(rdbtnPaswd.isSelected()) user.setPassword(tfupdate.getText());
                if(rdbtnAdresse.isSelected()) user.setAdresse(tfupdate.getText());
                if(rdbtnTelephone.isSelected()) user.setTelephone(tfupdate.getText());
    			JOptionPane.showMessageDialog(this,"Mise à jour réussie","Détails du compte",JOptionPane.INFORMATION_MESSAGE);
                showAccountDetails();
            }
    		else if(infos[0].equals("NO")){
    			JOptionPane.showMessageDialog(this,infos[1],"Détails du compte",JOptionPane.ERROR_MESSAGE);
            }
    		else {
	        	logger.error("trame invalide envoyée par le serveur");
            	JOptionPane.showMessageDialog(this,"trame invalide envoyée par le serveur","Détails du compte",JOptionPane.ERROR_MESSAGE);
        	}
    	}
    	else {
        	JOptionPane.showMessageDialog(this,"réponse de serveur inattendue.","Détails du compte",JOptionPane.ERROR_MESSAGE);
    	}
	}
	
	public String searchMatString()
	{
		String sort = prixComboBox.getSelectedItem().toString();
		if(sort.equals("Croissant")) {
			sort = "ASC";
		}
		else {
			sort = "DESC";
		}
		return searchField.getText()+":"+sort;
	}
	
	public void searchMat() throws SocketTimeoutException,IOException,ArrayIndexOutOfBoundsException
	{	
		String[] infos = clientTCP.getData("SEARCH","TEXT",searchMatString());
    	if(infos!=null) {
    		if(infos[0].equals("OK")) {
    			DefaultListModel<String> jModel = new DefaultListModel<String>();
	       		for(int i=1;i<infos.length;i++) {
        			if(infos[i].contains("alerte"))
        				JOptionPane.showMessageDialog(this,infos[i],"Alerte",JOptionPane.INFORMATION_MESSAGE);
        			else
        				jModel.addElement(infos[i]);
	       		}
	       		resultList.setModel(jModel);
        	}
        	else if(infos[0].equals("NOT FOUND")) {
    			JOptionPane.showMessageDialog(this,"Aucun résultat trouvé!","Recherche",JOptionPane.INFORMATION_MESSAGE);
        	}
        	else {
	        	logger.error("trame invalide envoyée par le serveur");
            	JOptionPane.showMessageDialog(this,"trame invalide envoyée par le serveur","Recherche",JOptionPane.ERROR_MESSAGE);
        	}
    	}
    	else {
        	JOptionPane.showMessageDialog(this,"réponse de serveur inattendue.","Recherche",JOptionPane.ERROR_MESSAGE);
    	}
	}
	
	public void showMaterialDetails()
	{		
		try {
			String[] infos = clientTCP.getData("DETAILS","MATERIEL",resultList.getSelectedValue().toString());
			if(infos!=null) {
        		String matDetails = "Nom: "+infos[0]+"\n"+
    					"Prix: "+infos[1]+" $/jour\n"+
    					"Description: "+infos[2]+"\n"+
    					"Prochaine disponibilité: "+infos[3]+"\n"+
    					"Quantité en stock: "+infos[4];
				JOptionPane.showMessageDialog(this,matDetails,"Recherche",JOptionPane.INFORMATION_MESSAGE);
          	}
        	else {
            	JOptionPane.showMessageDialog(this,"réponse de serveur inattendue.","Recherche",JOptionPane.ERROR_MESSAGE);
        	}
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
	@Override
	public void actionPerformed(ActionEvent event)
	{	
		try {
			switch (event.getActionCommand())
			{		
				case "Matériel":
					togglePanels(searchPanel);
					break;
					
				case "Rechercher":
					logger.info("<RECHERCHE>");
					searchMat();
					break;
					
				case "Mon compte":
					togglePanels(accountPanel);
					break;
				
				case "update":
					logger.info("<UPDATE ACCOUNT>");
					updateAccount();
					break;
				
				case "Contacts":
					JOptionPane.showMessageDialog(null,
							"Email: rassem.contact@gmail.com \n"+
							"Téléphone: 0784910653 \n"+
							"Site web: https://servicesbatiment.wordpress.com"
							,"Contacts"
							,JOptionPane.INFORMATION_MESSAGE);
					break;
				
				case "Déconnexion":
					new LogoutDialog(this,user);
					break;
			}
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

}
