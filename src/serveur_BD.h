#ifndef __SERVEUR_BDD_H_
#define __SERVEUR_BDD_H_

/*effectué les différents operations sur une base de données en utilisatnt libpq
*"connéxion a une base de données, insertion, suppression, selection, projection, jointure"
*sur les différents table de la base de données 
*/
#include "libpq-fe.h"
void quit(PGconn * conn);
PGconn *connexion();
PGresult *requete(PGconn *conn, const char *command);
int INSERT(PGconn *conn, char *values, char *table);
int UPDATE(PGconn *conn, char *table, char *etat1, char *values1, char *etat2, char *values2);
int existe_USER(PGconn *conn, char* id);
char *login_USER(PGconn* conn, char** tokens, struct sockaddr_in cli_addr);
char *singin_USER(PGconn* conn, char** tokens);
char *update_USER(PGconn* conn, char** tokens);
char *info_MAT(PGconn* conn, char* id);
char *recherche_MAT(PGconn* conn, char** tokens);
char* alerte_USER(PGconn* conn);
#endif /* __SERVEUR_BDD_H_ */