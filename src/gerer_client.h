#ifndef __GERER_CLIENT_H_
#define __GERER_CLIENT_H_
/*
*les dialogues des authentifications+identifications,fermeture propre,inscription des utilisateurs
*qui sont traite par ses fonctions
*/
int login_user(int s_dial, struct sockaddr_in cli_addr);
int logout_user(int s_dial);
void singin_user(int s_dial);
void update_user(int s_dial);
void info_mat(int s_dial);
void recherche_mat(int s_dial);
void error(int s_dial);
int alerte(int s_dial);
#endif /* __GERER_CLIENT_H_ */