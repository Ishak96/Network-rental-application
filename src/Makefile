all : serveur_tcp

utile.o : utile.c utile.h
	gcc -c utile.c

serveur_BD.o : serveur_BD.c serveur_BD.h utile.h
	gcc -c -I -L -l pq serveur_BD.c

gerer_client.o : gerer_client.c gerer_client.h serveur_BD.h utile.h
	gcc -c -I -L -l pq gerer_client.c

serveur_tcp.o : serveur_tcp.c gerer_client.h
	gcc -c -I -L -l pq serveur_tcp.c

serveur_tcp : utile.o serveur_BD.o gerer_client.o serveur_tcp.o
	gcc serveur_tcp.o gerer_client.o serveur_BD.o utile.o -g -I -L -l pq -o serveur_tcp

clean :
	rm -f serveur_tcp serveur_tcp.o serveur_BD.o utile.o gerer_client.o