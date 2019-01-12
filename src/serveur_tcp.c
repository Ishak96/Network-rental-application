/** Programme serveur TCP :
 *    - on ouvre une socket d'écoute sur le port 5000
 *    - on attend qu'un client vienne s'y connecter
 *    - une fois le client connecté
 *    - on verifie ce que le cient demande
 *    - on effectue un traitement selon la demande du client (authentifications+identifications,fermeture propre,inscription)
 *    - on referme les sockets
 *    - on termine le programme
 *    @author AYAD,Ishak
 *    université de cergy pontoise 2018/2019
 *    Licence 3 Informatique
 */
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include "gerer_client.h"
#include "libpq-fe.h"

#define default_port 5000
#define DEBUG 0
#define couleur(param) printf("\033[%sm",param)
#define GREY "37"
#define RED "31"

/*
 * On crée une socket d'écoute sur le port donné avec un nombre maximum de 
 * nb_max_clients de clients en file d'attente.
 */
int cree_serveur_tcp (int port, int nb_max_clients) {
  int s_ecoute ;
  struct sockaddr_in serv_addr ;

  /* on designe l'adresse+port qu'on va ouvrir */
  serv_addr.sin_family = AF_INET ;
  serv_addr.sin_addr.s_addr = htons(INADDR_ANY) ; /* on attend sur toutes nos adresses */
  serv_addr.sin_port = htons (port) ; 
  memset (&serv_addr.sin_zero, 0, sizeof(serv_addr.sin_zero));

  /* on cree la socket d'ecoute et on l'associe au couple (adresse,port) defini ci-dessus */
  s_ecoute = socket (PF_INET, SOCK_STREAM, 0) ;
  if (s_ecoute == -1) {
    couleur(RED);
    printf("Error: unable to open a socket\n");
    exit(1);
    couleur(GREY);
  }
  if ((bind (s_ecoute, (struct sockaddr *)&serv_addr, sizeof serv_addr)) == -1) {
    couleur(RED);
    printf("Error: unable to bind\n");
    printf("Error code: %d\n", errno);
    exit(1);
    couleur(GREY);
  }
  /* on definit la socket s_ecoute, comme etant une socket d'ecoute*/
  if ((listen (s_ecoute, 5)) == -1) {
    couleur(RED);
    printf("Error: unable to listen for connections\n");
    printf("Error code: %d\n", errno);
    exit(1);
    couleur(GREY);
  }
  return s_ecoute ;
}
/* 
 * Le processus qui gere le client recupere par la socket ce que lui raconte le
 * client, les messages envoyé par les cients peuvent le renvoyé vers différents dialogues
 */
void gerer_client(int s_dial, struct sockaddr_in cli_addr) {
  char buf[80];
  char* buf_alerte;
  int quit,connect,send_alerte,tmp;
  send_alerte=connect=quit=tmp=0;
  bzero (buf, 80);
   do{

     bzero (buf, 80);
     read (s_dial, buf, 80);
     strtok(buf, "\n");

     if(!strcmp(buf,"LOGIN"))
      connect=login_user(s_dial,cli_addr);
     else if(!strcmp(buf,"LOGOUT") && connect)
      quit=logout_user(s_dial);
     else if(!strcmp(buf,"SINGIN") && !connect)
      singin_user(s_dial);
     else if(!strcmp(buf,"UPDATE") && connect)
      update_user(s_dial);
     else if(!strcmp(buf,"DETAILS") && connect)
      info_mat(s_dial);
     else if(!strcmp(buf,"SEARCH") && connect){
      recherche_mat(s_dial);
      if (!send_alerte){
       send_alerte=alerte(s_dial);
      }
     }
     else if(!strcmp(buf,"") && tmp==0){
      printf("déconnexion/coupure client IP=%s détecté\n", inet_ntoa(cli_addr.sin_addr));
      tmp++;
     }
     
     else
      error(s_dial);
       
     strcpy(buf,"\n");
     write(s_dial, buf, strlen(buf));
   }while(!quit);
}

int main (){
  int s_ecoute, s_dial, cli_len ;
  struct sockaddr_in serv_addr, cli_addr ;
  int so_reuseaddr = 1;
  int nb_max_clients,port_use;
  char port[10]="";

  /* On crée une socket d'écoute et on peut mettre 
     jusqu'à 5 clients en file d'attente */
  printf("quel est le nombre de client maximum?\n");
  scanf("%d",&nb_max_clients);
  printf("quel est le port a utilisé?\n");
  scanf("%s",port);
  if (!strcmp(port,"DEFAULT"))
    port_use=default_port;
  else
    port_use=atoi(port);

  s_ecoute = cree_serveur_tcp (port_use, nb_max_clients);
  setsockopt(s_ecoute, SOL_SOCKET,SO_REUSEADDR,&so_reuseaddr, sizeof so_reuseaddr);
  cli_len = sizeof cli_addr ;

  while (1) {
  	/* On se met en ecoute sur la socket. C'est une fonction blocante qui ne
     se debloque que lorsqu'un client vient se connecter sur cette socket 
     d'ecoute. La valeur de retour est alors le descripteur de la socket 
     de connexion permettant  de dialoguer avec CE client. */
    s_dial = accept (s_ecoute, (struct sockaddr *) &cli_addr, (socklen_t *) &cli_len);
    #if(DEBUG > 0)
        couleur(RED);
        printf ("Le client d'adresse IP %s s'est connecté depuis son port %d\n",\
                 inet_ntoa(cli_addr.sin_addr), ntohs(cli_addr.sin_port));
        couleur(GREY);
    #endif

  	if (fork() == 0) {
  	    gerer_client(s_dial, cli_addr);
  	    return(1);
  	}
      close (s_dial);
  }
  close (s_ecoute) ;
  return 0 ;
}