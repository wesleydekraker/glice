#include <string.h>

int bad()
{
  char src[18];
  char buf[10];

  memset(src, 'A', 18);
  src[18 - 1] = '\0';

  /*  BAD  */
  strncpy(buf, src, 18);


  return 0;
}