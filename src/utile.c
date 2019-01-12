#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <time.h>
#include <ctype.h>
#include <malloc.h>
#include "utile.h"

char** str_split(char* a_str, const char a_delim){
    char** result = 0;
    size_t count = 0;
    char* tmp = a_str;
    char* last_comma = 0;
    char delim[2];
    delim[0] = a_delim;
    delim[1] = 0;

    while (*tmp)
    {
        if (a_delim == *tmp)
        {
            count++;
            last_comma = tmp;
        }
        tmp++;
    }
    count += last_comma < (a_str + strlen(a_str) - 1);
    count++;

    result = malloc(sizeof(char*) * count);
    if (result)
    {
        size_t idx  = 0;
        char* token = strtok(a_str, delim);

        while (token)
        {
            assert(idx < count);
            *(result + idx++) = strdup(token);
            token = strtok(0, delim);
        }
        assert(idx == count - 1);
        *(result + idx) = 0;
    }
    return result;
}

int tokens_ok(char* type_user, char** tokens){
 int i,verif,som;
    som=verif=0;
    if(tokens){
        for (i = 0; *(tokens + i); i++){
            som+=1;
        }
        if(!strcmp(type_user,"pro") && som==10)
            verif=1;
        else if(!strcmp(type_user,"cli") && som==7)
            verif=1;
    }
    return verif;
}

int contains(char* str_in, char* str_cont){
	return strstr(str_in,str_cont)!=NULL;
}

char* gener_aleat_id(char* prefix, int size){
    srand(time(NULL));
    int rand1,rand2,rand3;
    char* c1=(char *)malloc(sizeof(char)*50);
    char* c2=(char *)malloc(sizeof(char)*50);

    strcpy(c2,prefix);
    for (int i = 0; i < size/3; i++)
    {
        rand1=rand()%(122-97)+97;
        rand2=rand()%50;
        rand3=rand()%(122-97)+97;
        sprintf(c1,"%c%d%c",toupper(rand3),rand2,(char)rand1);
        strcat(c2,c1);
    }
    return c2;
}

int buffering_check(char* buf1, char* buf2, int len_buf){
    return (strlen(buf1)+strlen(buf2))<=len_buf;
}