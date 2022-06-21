executable=$(basename $1 .c)
gcc -Wall -w $1 -o $executable

./$executable
