package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class HomeGUI extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		new HomeGUI();
	}

	public HomeGUI() {
		super("Services Batiment");
		initComponents();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 540, 380);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initComponents() {
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		contentPane.add(panel, BorderLayout.NORTH);

		JButton btnInscription = new JButton("Inscription");
		btnInscription.addActionListener(this);
		panel.add(btnInscription);

		JButton btnConnexion = new JButton("Connexion");
		btnConnexion.addActionListener(this);
		panel.add(btnConnexion);

		JButton btnPropos = new JButton("À propos");
		btnPropos.addActionListener(this);
		panel.add(btnPropos);

		JButton btnContacts = new JButton("Contacts");
		btnContacts.addActionListener(this);
		panel.add(btnContacts);

		JButton btnServParam = new JButton("Serveur");
		btnServParam.addActionListener(this);
		panel.add(btnServParam);

		JLabel labelImage = new JLabel("");
		labelImage.setIcon(new ImageIcon(HomeGUI.class.getResource("/images/home.png")));

		JPanel mainContent = new JPanel();
		mainContent.add(labelImage);
		contentPane.add(mainContent, BorderLayout.CENTER);

		pack();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
		case "Inscription":
			new RegisterDialog(this);
			break;

		case "Connexion":
			new LoginDialog(this);
			break;

		case "À propos":
			JOptionPane.showMessageDialog(null,
					"Université de Cergy-Pontoise\n" + "Projet Réseau L3 Informatique\n\n" + "Fonctionnalités:\n"
							+ "Identification + Authentification\n" + "Mise à jour de tables de la base de données\n"
							+ "Consultation d'une partie de la base de données\n" + "Alerte serveur vers le client\n"
							+ "Demande de fermeture propre\n\n" + "Réalisé le 31 Décembre 2018",
					"À propos", JOptionPane.INFORMATION_MESSAGE);
			break;

		case "Contacts":
			JOptionPane.showMessageDialog(null,
					"Email: rassem.contact@gmail.com \n" + "Téléphone: 0784910653 \n"
							+ "Site web: https://servicesbatiment.wordpress.com",
					"Contacts", JOptionPane.INFORMATION_MESSAGE);
			break;

		case "Serveur":
			new ServerParamDialog(this);
			break;
		}
	}
}
