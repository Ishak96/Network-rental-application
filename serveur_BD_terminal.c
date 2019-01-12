/*compilation gcc serveur_BD.c -I -L -l pq -o serveur_BD*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "libpq-fe.h"

void quit(PGconn * conn);
PGconn *connexion();
void writeCSV(PGresult *res);
char *columnLayout(PGresult *res, char list_nom[]);
PGresult *requete(PGconn *conn, const char *command);
int getRowNumb(PGconn *conn, char table[]);
void SELECT_WHERE(PGconn *conn, char select[], char table[],char where[], char where_cond[]);
void SELECT(PGconn *conn, char select[], char table[]);
char *getNomCol(PGconn *conn, char str[], char table[]);
char *insert_Requete(PGconn *conn, char table[]);
char *INSERT(PGconn *conn, char values[], char table[]);
char *DELETE(PGconn *conn, char table[], char etat[], char values[]);
char *UPDATE(PGconn *conn, char table[], char etat1[], char values1[], char etat2[], char values2[]);

int main(int argc,char *argv[]) {
	PGconn *conn = connexion();
	char *result ="";
	switch (*argv[1]){
		case 's':
			if(argc==4){
				SELECT(conn, argv[2], argv[3]);
			}
			else{
				printf("Verifiez les arguments ( SELECT ... FROM ...)\n");
			}
			break;
		case 'w':
			if(argc==6){
				SELECT_WHERE(conn,argv[2],argv[3],argv[4],argv[5]);
			}
			else{
				printf("Verifiez les arguments ( SELECT ... FROM ... WHERE ...)\n");
			}
			break;
		case 'i':
			if(argc==4){
				result = INSERT(conn,argv[2],argv[3]);
				printf("%s\n",result);
			}
			else{
				printf("Verifiez les arguments\n");
			}
			break;
		case 'd':
			if(argc==5){
				result = DELETE(conn,argv[2],argv[3],argv[4]);
				printf("%s\n",result);
			}
			else{
				printf("Verifiez les arguments\n");
			}
			break;
		case 'u':
			if(argc==7){
				result = UPDATE(conn,argv[2],argv[3],argv[4],argv[5], argv[6]);
				printf("%s\n",result);
			}
			else{
				printf("Verifiez les arguments\n");
			}
			break;		
		default:
			printf("Mauvais argument\n");
			break;
	}  
	PQfinish(conn);    	
	return 0;
}


void quit(PGconn * conn) {
    PQfinish(conn);
    exit(1);
}

PGconn *connexion(){
	const char *conninfo;
   	conninfo = "host = 127.0.0.1 port = 5432 dbname = postgres user = postgres password = ISHAKAYAD21";
   	PGconn *conn = PQconnectdb(conninfo);
    	if (PQstatus(conn) == CONNECTION_OK) {
    	    printf("Connecté.\n");
   	}
    	else {
    	    printf("Erreur de connexion.\n");
    	}	
	return conn;
}

void writeCSV(PGresult *res){
	FILE* fichier = NULL;
	fichier = fopen("req.csv", "w");
	char *val;
	if(fichier != NULL){
		for (int i = 0 ; i < PQntuples(res) ; i++) {
    			for (int j = 0 ; j < PQnfields(res) ; j++) {
				val = PQgetvalue(res, i, j);
				fprintf(fichier,"%s;",val);
			}
			fprintf(fichier,"\r");
    	}
	}
	fclose(fichier);
}

char *columnLayout(PGresult *res, char list_nom[]){
	FILE* fichier = NULL;
	fichier = fopen("req.csv", "r");
	char *val;
	strcat(list_nom,"(");
	if(fichier != NULL){
		for (int i = 0 ; i < PQntuples(res) ; i++) {
    			for (int j = 0 ; j < PQnfields(res) ; j++) {
				val = PQgetvalue(res, i, j);
				strcat(list_nom,val);
				if(i != PQntuples(res) -1){
					strcat(list_nom,", ");
				}
			}
    	}
	}
	strcat(list_nom,")");
	fclose(fichier);
	return list_nom;
}


PGresult *requete(PGconn *conn, const char *command) {
    PGresult *res;
    res = PQexec(conn, command);
    if (PQresultStatus(res) != PGRES_TUPLES_OK) {
        printf("Command failed.\n");
        PQclear(res);
    }
    else {
        printf("Commande validée : %s\n", command);
    }
	return res;
}

int getRowNumb(PGconn *conn, char table[]){
	char str[150];
	strcat(str,"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='");
	strcat(str,table);
	char *command = strcat(str, "'");
	PGresult *res = requete(conn, command);
	char *val = PQgetvalue(res,0 ,0);
	int nbr = *val - '0';
	return nbr;
}

void SELECT_WHERE(PGconn *conn, char select[], char table[],char where[], char where_cond[]){
	char str[150]; 
	strcat(str,"SELECT ");
	strcat(str,select);
	strcat(str," FROM ");
	strcat(str,table);	
	strcat(str, " WHERE ");
	strcat(str, where);
	strcat(str, "='");
	strcat(str, where_cond);
	char *command = strcat(str, "'");
	printf("%s\n",command);
    	PGresult *res = requete(conn, command);
	writeCSV(res);
}

void SELECT(PGconn *conn, char select[], char table[]){
	char str[150]; 
	strcat(str,"SELECT ");
	strcat(str,select);
	strcat(str," FROM ");
	strcat(str,table);	
	char *command = str;
    	PGresult *res = requete(conn, command);
	writeCSV(res);
}


char *getNomCol(PGconn *conn, char str[], char table[]){
	strcat(str,"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name='");
	strcat(str,table);
	strcat(str,"'");
	return str;
}

char *insert_Requete(PGconn *conn, char table[]){
	char str[75]="", nomT[100]="";
	char *command = getNomCol(conn,str,table);
	PGresult *res = requete(conn, command);
	char *val = columnLayout(res, nomT);
	return val;

}

char *INSERT(PGconn *conn, char values[], char table[]){
	char *result="SUCCESS";
	PGresult *res;
	char *nomTable = insert_Requete(conn, table);
        char str[150];
	strcat(str,"INSERT INTO ");
        strcat(str,table);
	strcat(str,"");
        strcat(str,nomTable);
	strcat(str," VALUES (");
        strcat(str,values);
        char *command = strcat(str,")");
	printf("%s\n", command);
	res = PQexec(conn, command);
	if ( PQresultStatus(res) !=  PGRES_COMMAND_OK){
        result="FAILURE";
		PQclear(res);
    }
	else {
		printf("Commande validée : %s\n", command);
	}
	return result;
}

char *DELETE(PGconn *conn, char table[], char etat[], char values[]){
	char *result="SUCCESS";
	PGresult *res;
	char str[150];
	strcat(str,"DELETE FROM ");
	strcat(str,table);
	strcat(str," WHERE ");
	strcat(str,etat);
	strcat(str," = '");
	strcat(str,values);
	strcat(str,"'");
	char *command = str;
	res = PQexec(conn, command);
	if ( PQresultStatus(res) !=  PGRES_COMMAND_OK){
                result="FAILURE";
		PQclear(res);
        }
	else {
		printf("Commande validée : %s\n", command);
	}
	return result;
}

char *UPDATE(PGconn *conn, char table[], char etat1[], char values1[], char etat2[], char values2[]){
	char *result="SUCCESS";
	PGresult *res;
	char str[150];
	strcat(str,"UPDATE ");
	strcat(str,table);
	strcat(str," SET ");
	strcat(str,etat1);
	strcat(str," = '");
	strcat(str,values1);
	strcat(str,"' WHERE ");
	strcat(str,etat2);
	strcat(str," = '");
	strcat(str,values2);
	strcat(str,"'");
	char *command = str;
	res = PQexec(conn, command);
	if ( PQresultStatus(res) !=  PGRES_COMMAND_OK){
                result="FAILURE";
		PQclear(res);
        }
	else {
		printf("Commande validée : %s\n", command);
	}
	return result;
}