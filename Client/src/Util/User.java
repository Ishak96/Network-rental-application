package Util;
@SuppressWarnings("unused")

public class User
{	
	private String userType;
	private String userID;	
	private String nom;
	private String prenom;
	private String adresse;
	private String telephone;
	private String mail;
	private String password;	
	private String metier;
	private String diplome;
	private String experience;
	
	public User(String[]infos) {
		this(infos[1],infos[2],infos[3],infos[4],infos[5],infos[6],infos[7],infos[8]);
		if(userType.equals("Professionnel")){
			setProFields(infos[9],infos[10],infos[11]);
		}		
	}
	
	public User(String userType, String userID, String nom, String prenom, String adresse, String telephone,String mail, String password) {
		this.userType = userType;
		this.userID = userID;
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.telephone = telephone;
		this.mail = mail;
		this.password = password;
	}
	
	public void setProFields(String metier, String diplome, String experience) {
		this.metier = metier;
		this.diplome = diplome;
		this.experience = experience;
	}
	
	public String getID() {
		return userID;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String toString()
	{
		String infos = "Nom: "+nom+"\n"+
					   "Prénom: "+prenom +"\n"+
					   "Adresse: "+adresse+"\n"+
					   "Téléphone: "+telephone+"\n"+
					   "Mail: "+mail+"\n"+
					   "Mot de passe: "+password+"\n";
		if(userType.equals("Professionnel")) {
			infos += "Métier: "+metier+"\n"+
					 "Diplome: "+diplome+"\n"+
					 "Années d'expérience: "+experience+"\n";
		}
		return infos;		
	}
}

// OK:Professionnel:pro_00001:HACHOUD:Rassem:3 rue des ecoles:0622377435:rassem@gmail.com:123456:plombier:aucun:5

