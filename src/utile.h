#ifndef __UTILE_H_
#define __UTILE_H_

char** str_split(char* a_str, const char a_delim);
int tokens_ok(char* type_user, char** tokens);
int contains(char* str_in, char* str_cont);
char* gener_aleat_id(char* prefix, int size);
int buffering_check(char* buf1, char* buf2, int len_buf);
#endif /* __UTILE_H_ */