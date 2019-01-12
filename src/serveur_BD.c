/*compilation gcc serveur_BD.c -I -L -l pq -o serveur_BD*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include "libpq-fe.h"
#include "serveur_BD.h"
#include "utile.h"

#define DEBUG 0
#define couleur(param) printf("\033[%sm",param)
#define GREY "37"
#define RED "31"

void quit(PGconn * conn) {
    PQfinish(conn);
    exit(1);
}
PGconn *connexion(){
	const char *conninfo;
   	conninfo = "host = 127.0.0.1 port = 5432 dbname = postgres user = postgres password = ISHAKAYAD21";
   	PGconn *conn = PQconnectdb(conninfo);
    if (PQstatus(conn) == CONNECTION_OK) {
        printf("serveur C (connexion)OK BDD.\n");
   	}
    else {
        printf("serveur C (connexion)NON BDD.\n");
    }	
	return conn;
}
PGresult *requete(PGconn *conn, const char *command) {
    PGresult *res;
    res = PQexec(conn, command);
    if (PQresultStatus(res) != PGRES_TUPLES_OK) {
        printf("serveur C (NON)-> BDD.\n");
        PQclear(res);
    }
    else {
        printf("serveur C (OK)-> BDD.\n");
    }
	return res;
}
int INSERT(PGconn *conn, char *values, char *table){
	PGresult *res;
    char str[500];
	strcpy(str,"INSERT INTO ");
    strcat(str,table);
	strcat(str," VALUES (");
    strcat(str,values);
    strcat(str,")");
	res = PQexec(conn, str);
	int result=1;
	if ( PQresultStatus(res) !=  PGRES_COMMAND_OK){
        result=0;
		PQclear(res);
    }
	return result;
}
int UPDATE(PGconn *conn, char *table, char *etat1, char *values1, char *etat2, char *values2){
	PGresult *res;
	char str[900];
	int result=1;

	strcpy(str,"UPDATE ");
	if(buffering_check(str,table,900))
		strcat(str,table);
	else{
		result=0;
		printf("buffering attack! -> %s\n", table);
	}
	
	strcat(str," SET ");
	if(buffering_check(str,etat1,900))
		strcat(str,etat1);
	else{
		result=0;
		printf("buffering attack! -> %s\n", etat1);
	}
	
	strcat(str," ='");
	if(buffering_check(str,values1,900))
		strcat(str,values1);
	else{
		result=0;
		printf("buffering attack! -> %s\n", values1);
	}

	strcat(str,"' WHERE ");
	if(buffering_check(str,etat2,900))
		strcat(str,etat2);
	else{
		result=0;
		printf("buffering attack! -> %s\n", etat2);
	}
	
	strcat(str," ='");
	if(buffering_check(str,values2,900))
		strcat(str,values2);
	else{
		result=0;
		printf("buffering attack! -> %s\n", values2);
	}
	
	strcat(str,"'");
	if(result){
		res = PQexec(conn, str);
		if ( PQresultStatus(res) !=  PGRES_COMMAND_OK){
			result=0;
			PQclear(res);
	    }
	}
	return result;
}
int existe_USER(PGconn* conn, char* id){
	char str[150]; 
	
	strcpy(str,"SELECT * FROM utilisateurs WHERE mail='");
	strcat(str,id);
	strcat(str,"'");

	PGresult *res=requete(conn,str);;
	return PQntuples(res)>0;
}
char *login_USER(PGconn* conn, char** tokens, struct sockaddr_in cli_addr){
	char str[150],str1[550];
	char* id,* password;

	id=*(tokens + 0);
    password=*(tokens + 1);
	strcpy(str,"SELECT * FROM utilisateurs WHERE mail='");
	if(buffering_check(str,id,150))
		strncat(str,id,150);
	else
		printf("buffering attack par le client d'adresse IP %s!\n",inet_ntoa(cli_addr.sin_addr));
	
	strncat(str,"' AND mot_de_passe='",150);
	if(buffering_check(str,password,150))
		strncat(str,password,150);
	else
		printf("buffering attack par le client d'adresse IP %s!\n",inet_ntoa(cli_addr.sin_addr));
	
	strncat(str,"'",150);
	PGresult *res=requete(conn,str);
	char* result="NO:Inscrivez vous à notre application";
	if (PQntuples(res)>0 && !strcmp(PQgetvalue(res, 0, 9),"t")){
		if (contains(PQgetvalue(res, 0, 0),"pro"))
		{
			strcpy(str1,"SELECT metier,diplome,annees_exp,note FROM utilisateurs INNER JOIN professionnels ON utilisateurs.no_util=professionnels.no_util_pro WHERE no_util_pro='");
			strcat(str1,PQgetvalue(res, 0, 0));
			strcat(str1,"'");
			PGresult *res1=requete(conn,str1);

			sprintf(str,"OK:Professionnel:%s:%s:%s:%s:%s:%s:%s:%s:%s:%s:%s",PQgetvalue(res, 0, 0),PQgetvalue(res, 0, 1),PQgetvalue(res, 0, 2),PQgetvalue(res, 0, 3),PQgetvalue(res, 0, 4),PQgetvalue(res, 0, 5),PQgetvalue(res, 0, 6)
											  		  						   ,PQgetvalue(res1, 0, 0),PQgetvalue(res1, 0, 1),PQgetvalue(res1, 0, 2),PQgetvalue(res1, 0, 3));			
		}
		else
			sprintf(str,"OK:Client:%s:%s:%s:%s:%s:%s:%s",PQgetvalue(res, 0, 0),PQgetvalue(res, 0, 1),PQgetvalue(res, 0, 2),PQgetvalue(res, 0, 3),
											  		  		PQgetvalue(res, 0, 4),PQgetvalue(res, 0, 5),PQgetvalue(res, 0, 6));
		result=str;
	}
	else{
		if (PQntuples(res)>0 && strcmp(PQgetvalue(res, 0, 9),"t")){
			strcpy(str,"NO:Veuillez valider votre compte");
			result=str;
		}
	}
	return result;
}
char *singin_USER(PGconn* conn, char** tokens){
	char values1[300],values2[300];
	char* type,* id,* password,* nom,* prenom,* adresse,* telephone,* metier,* diplome,* no_util;
	int annees_exp;
	char *result="OK";

	type=*(tokens + 0);
	nom=*(tokens + 1);
	prenom=*(tokens + 2);
	adresse=*(tokens + 3);
	telephone=*(tokens + 4);
	id=*(tokens + 5);
    password=*(tokens + 6);

    if (!strcmp(type,"professionnels")){
    	if(tokens_ok("pro",tokens)){
	    	no_util=gener_aleat_id("pro_",30);
	    	metier=*(tokens + 7);
	    	diplome=*(tokens + 8);
	    	annees_exp=atoi(*(tokens + 9));
			sprintf(values2,"'%s','%s','%s',%d,%d",no_util,metier,diplome,0,annees_exp);
    		sprintf(values1,"'%s','%s','%s','%s','%s','%s','%s',%s,%s,%s",no_util,nom,prenom,adresse,telephone,id,password,"null","null","true");
    	    if (buffering_check("",values1,300) && buffering_check("",values2,300)){
	    	    if(!existe_USER(conn,id)){
	    	    	if(!INSERT(conn,values1,"utilisateurs") || !INSERT(conn,values2,type))
	        			result="NO:Inscription impossible vérifier vos informations d'identification";
	    	    }
	        	else{
	        		result="NO:Cette adresse e-mail est déjà utilisée";
	        	}	
    	    }
    	    else{
    	    	printf("buffering attack! -> singin_USER\n");
    	    	result="NO:Chaine de caractere trop langage";
    	    }
    	}
    	else{
    		result="NO:remplissez toutes les cases";
    	}
    }
    else{
    	no_util=gener_aleat_id("cli_",30);
    	if (tokens_ok("cli",tokens)){
			sprintf(values2,"'%s'",no_util);
    		sprintf(values1,"'%s','%s','%s','%s','%s','%s','%s',%s,%s,%s",no_util,nom,prenom,adresse,telephone,id,password,"null","null","true");
    	    if (buffering_check("",values1,300) && buffering_check("",values2,300)){
	    	    if(!existe_USER(conn,id)){
	    	    	if(!INSERT(conn,values1,"utilisateurs") || !INSERT(conn,values2,type))
	    		    	result="NO:Inscription impossible vérifier vos informations d'identification";
	    	    }
	    		else{
	    			result="NO:Cette adresse e-mail est déjà utilisée";
	    		}
	    	}
	    	else{
    	    	printf("buffering attack! -> singin_USER\n");
    	    	result="NO:Chaine de caractere trop langage";	    		
	    	}
    	}
    	else{
    		result="NO:remplissez toutes les cases";
    	}
    }
    return result;
}
char *update_USER(PGconn* conn, char** tokens){
	char* new_value,* attribut,* id_user;
	char *result="OK";

	attribut=*(tokens + 1);
	new_value=*(tokens + 2);
	id_user=*(tokens + 0);

	if (!strcmp(attribut,"mail")){
		if (!existe_USER(conn,new_value)){
			if (!UPDATE(conn,"utilisateurs",attribut,new_value,"no_util",id_user)){
				result="NO:Update impossible vérifier vos informations";				
			}
		}
		else{
			result="NO:Cette adresse e-mail est déjà utilisée";
		}
	}
	else{
		if (!UPDATE(conn,"utilisateurs",attribut,new_value,"no_util",id_user)){
			result="NO:Update impossible vérifier vos informations";				
		}	
	}
	return result;
}
char *info_MAT(PGconn* conn, char* id){
	char command[150],str[1500];

	PQexec(conn, "drop view if exists mat;");
	PQexec(conn, "drop view if exists desp;");
	PQexec(conn, "drop view if exists al_mat;");

	strcpy(command,"create view mat as select distinct * from materiels where nom_materiel='");
	strcat(command,id);
	strcat(command,"';");
	PQexec(conn,command);

	PQexec(conn,"create view desp as select no_materiel from materiels except select no_materiel from locations;");
	PGresult* res=requete(conn,"select count(*) from mat inner join desp on mat.no_materiel=desp.no_materiel;");

	strcpy(command,"select distinct description,prix_loc from materiels where nom_materiel='");
	strcat(command,id);
	strcat(command,"'");
	PGresult* res1=requete(conn,command);

	PQexec(conn,"create view al_mat as select * from materiels;");

	strcpy(command,"select *,DATE(NOW()) from al_mat inner join desp on al_mat.no_materiel=desp.no_materiel where nom_materiel='");
	strcat(command,id);
	strcat(command,"';");
	PGresult* res2=requete(conn,command);

	if (PQntuples(res2)>0){
		sprintf(str,"%s:%s:%s:%s:%s",id,PQgetvalue(res1,0,1),PQgetvalue(res1,0,0),PQgetvalue(res2,0,6),PQgetvalue(res,0,0));
	}else{
		strcpy(command,"select MIN(date_fin) from locations loc natural join materiels mat where mat.nom_materiel ='");
		strcat(command,id);
		strcat(command,"'");
		strcat(command,"and DATE(NOW())<loc.date_fin");
		PGresult* res3=requete(conn,command);

		sprintf(str,"%s:%s:%s:%s:%s",id,PQgetvalue(res1,0,1),PQgetvalue(res1,0,0),PQgetvalue(res3,0,0),PQgetvalue(res,0,0));
	}

	char* result=str;

	return result;
}
char *recherche_MAT(PGconn* conn, char** tokens){
	char command[100],str[50000];
	char *nom,* order;
	int tuple=0;
	char* result="NOT FOUND";

	nom=*(tokens + 0);
	order=*(tokens + 1);

	sprintf(command,"SELECT distinct nom_materiel,prix_loc FROM materiels order by prix_loc %s",order);

	PGresult *res=requete(conn,command);
	if (PQntuples(res)>0){
		for (int i = 0 ; i < PQntuples(res) ; i++) {
			if (contains(PQgetvalue(res,i,0),nom)){
				if(tuple==0)
					strcpy(str,"OK");

				strcat(str,":");
				strcat(str,PQgetvalue(res,i,0));
				tuple++;
			}
		}
		if(tuple>0)
			result=str;
	}
	printf("\n%s,%d\n", result, tuple);
	return result;
}
char* alerte_USER(PGconn* conn){
  int alerte=0;
  char str[150],str1[250];
  char* data_alerte="NO";
  strcpy(str,"select distinct nom_materiel,prix_loc from materiels where prix_loc in (select MIN(prix_loc) from materiels)");

  PGresult* res=requete(conn,str);
  if(PQntuples(res)>0){
    if(atoi(PQgetvalue(res,0,1))<=20){
      alerte=1;
      sprintf(str1,"alerte le materiel %s est a prix bas %s$,pour plus d'informations effectué une recherche.", PQgetvalue(res,0,0), PQgetvalue(res,0,1));
      data_alerte=str1;
    }
  }
  return data_alerte;
}