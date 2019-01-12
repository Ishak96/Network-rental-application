#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "serveur_BD.h"
#include "utile.h"
#include "libpq-fe.h"

#define DEBUG 1
#define couleur(param) printf("\033[%sm",param)
#define GREY "37"
#define RED "31"

int login_user(int s_dial, struct sockaddr_in cli_addr){
  char buf[500];
  char** tokens;
  int quit;
  time_t start, end;
  PGconn *conn = connexion();
  #if(DEBUG > 0) 
    couleur(RED); printf("login_user -> gerer_client.c:16.\n");
  #endif
   bzero (buf, 500) ;
   quit=0;
   do {
     strcpy(buf,"DATA\n");
     write(s_dial, buf, strlen(buf));

     bzero (buf, 500);
     time(&start);
      read (s_dial, buf, 500);
     time(&end); 
     strtok(buf, "\n");
     #if(DEBUG > 0)
      if((int)(end - start)>10)
        printf("[%d] le client a mis beaucoup de temps pour repondre %dS\n", getpid(), (int)(end - start));
      printf("[%d] buf client: %s\n", getpid(), buf);
     #endif
     if (contains(buf,":")){
       quit=1;
       tokens = str_split(buf, ':');
       strncpy(buf,login_USER(conn, tokens, cli_addr),500);
       write(s_dial, buf, strlen(buf));
       tokens = str_split(buf, ':');
     }
   }while(!quit);
  PQfinish(conn);
  #if(DEBUG > 0) 
    printf("end of login_user\n"); couleur(GREY);
  #endif
   return !strcmp(*(tokens + 0),"OK");
}
int logout_user(int s_dial){
  char buf[80];
  int quit;
  time_t start, end;
  PGconn *conn = connexion();
  #if(DEBUG > 0) 
    couleur(RED); printf("logout_user -> gerer_client.c:50.\n");
  #endif
   bzero (buf, 80) ;
   quit=0;
   strcpy(buf,"ID\n");
   write(s_dial, buf, strlen(buf));

   bzero (buf, 80) ;
   time(&start); 
    read (s_dial, buf, 80);
   time(&end);
   strtok(buf, "\n");
   #if(DEBUG > 0)
    if((int)(end - start)>10)
      printf("[%d] le client a mis beaucoup de temps pour repondre %dS\n", getpid(), (int)(end - start));
    printf("[%d] buf client: %s\n", getpid(), buf);
   #endif
   quit=existe_USER(conn,buf);
   if (quit){
     strcpy(buf,"FIN");
     write(s_dial, buf, strlen(buf));
   }
  PQfinish(conn);
  #if(DEBUG > 0) 
    printf("end of logout_user\n"); couleur(GREY);
  #endif
  return quit;
}
void singin_user(int s_dial){
  char buf[250];
  char** tokens;
  int quit;
  time_t start, end;
  PGconn *conn = connexion();
  #if(DEBUG > 0) 
    couleur(RED); printf("singin_user -> gerer_client.c:79.\n");
  #endif
  bzero(buf, 250);
  quit=0;
   do {
     strcpy(buf,"DATA\n");
     write(s_dial, buf, strlen(buf));

     bzero (buf, 250) ;
     time(&start); 
      read (s_dial, buf, 250);
     time(&end);
     strtok(buf, "\n");
     #if(DEBUG > 0)
      if((int)(end - start)>10)
        printf("[%d] le client a mis beaucoup de temps pour repondre %dS\n", getpid(), (int)(end - start));
      printf("[%d] buf client: %s\n", getpid(), buf);
     #endif
     if (contains(buf,":")){
       quit=1;
       tokens = str_split(buf, ':');
       strncpy(buf,singin_USER(conn, tokens),250);
       write(s_dial, buf, strlen(buf));
     }
   }while(!quit);
  PQfinish(conn);
  #if(DEBUG > 0) 
    printf("end of singin_user\n"); couleur(GREY);
  #endif
}
void update_user(int s_dial){
  char buf[250];
  char** tokens;
  int quit;
  time_t start, end;
  PGconn *conn = connexion();
  #if(DEBUG > 0) 
    couleur(RED); printf("update_user -> gerer_client.c:111.\n");
  #endif
  bzero (buf, 250);
  quit=0;
   do {
     strcpy(buf,"DATA\n");
     write(s_dial, buf, strlen(buf));

     bzero (buf, 250);
     time(&start); 
      read (s_dial, buf, 250);
     time(&end);
     strtok(buf, "\n");
     #if(DEBUG > 0)
      if((int)(end - start)>10)
        printf("[%d] le client a mis beaucoup de temps pour repondre %dS\n", getpid(), (int)(end - start));
      printf("[%d] buf client: %s\n", getpid(), buf);
     #endif
     if (contains(buf,":")){
       quit=1;
       tokens = str_split(buf, ':');
       strncpy(buf,update_USER(conn, tokens),250);
       write(s_dial, buf, strlen(buf));
     }
   }while(!quit);
  PQfinish(conn);
  #if(DEBUG > 0) 
    printf("end of update_user\n"); couleur(GREY);
  #endif
}
void info_mat(int s_dial){
  char buf[150];
  time_t start, end;
  PGconn *conn = connexion();
  #if(DEBUG > 0) 
    couleur(RED); printf("info_mat -> gerer_client.c:143.\n");
  #endif
  bzero (buf, 150);
   strcpy(buf,"MATERIEL\n");
   write(s_dial, buf, strlen(buf));

   bzero (buf, 150);
   time(&start); 
    read (s_dial, buf, 150);
   time(&end);
   strtok(buf, "\n");
   #if(DEBUG > 0)
    if((int)(end - start)>10)
      printf("[%d] le client a mis beaucoup de temps pour repondre %dS\n", getpid(), (int)(end - start));
    printf("[%d] buf client: %s\n", getpid(), buf);
   #endif 
   strncpy(buf,info_MAT(conn, buf),150);
   write(s_dial, buf, strlen(buf));
   
  PQfinish(conn);
  #if(DEBUG > 0) 
    printf("end of info_mat\n"); couleur(GREY);
  #endif
}
void recherche_mat(int s_dial){
  char buf[5000];
  char** tokens;
  int quit=0;
  time_t start, end;
  PGconn *conn = connexion();
  #if(DEBUG > 0) 
    couleur(RED); printf("recherche_mat -> gerer_client.c:197.\n");
  #endif
  bzero (buf, 5000);
  do {
   strcpy(buf,"TEXT\n");
   write(s_dial, buf, strlen(buf));

   time(&start); 
    read (s_dial, buf, 5000);
   time(&end);
   strtok(buf, "\n");
   #if(DEBUG > 0)
    if((int)(end - start)>10)
      printf("[%d] le client a mis beaucoup de temps pour repondre %dS\n", getpid(), (int)(end - start));
    printf("[%d] buf client: %s\n", getpid(), buf);
   #endif
   if (contains(buf,":")){
     quit=1;
     tokens=str_split(buf, ':');
     strncpy(buf,recherche_MAT(conn, tokens),5000);
     write(s_dial, buf, strlen(buf));
   }
  }while(!quit);
  PQfinish(conn);
  #if(DEBUG > 0)
    printf("end of recherche_mat\n"); couleur(GREY);
  #endif
}
int alerte(int s_dial){
  char* data_alerte="";
  char buf[250];
  int send_alerte=0;
  PGconn *conn = connexion();
  bzero(buf, 250);
  #if(DEBUG > 0) 
    couleur(RED); printf("alerte -> gerer_client.c:233.\n");
  #endif
    data_alerte=alerte_USER(conn);
    if (!(!strcmp(data_alerte,"NO"))){
       send_alerte=1;
       strncpy(buf,data_alerte,250);
       write(s_dial, buf, strlen(buf));
    }
  PQfinish(conn);
  #if(DEBUG > 0)
    printf("end of alerte\n"); couleur(GREY);
  #endif
  return send_alerte;
}
void error(int s_dial){
  char buf[80];
  strcpy(buf,"ERROR RETRY.");
  write(s_dial, buf, strlen(buf));
}