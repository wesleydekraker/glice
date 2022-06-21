#define CHAR_ARRAY_SIZE (3 * sizeof(data) + 2)

static int test_005()
{
    int data;
    void  ( *funcPtr) (int) = CWE121_Stack_Based_Buffer_Overflow__CWE129_fgets_65b_goodB2GSink;
    /* Initialize data */
    data = -1;
    {
        char inputBuffer[CHAR_ARRAY_SIZE] = "";
        /* POTENTIAL FLAW: Read data from the console using fgets() */
        if (fgets(inputBuffer, CHAR_ARRAY_SIZE, stdin) != NULL)
        {
            /* Convert to int */
            data = atoi(inputBuffer);
        }
        else
        {
            printLine("fgets() failed.");
        }
    }
    funcPtr(data);
}