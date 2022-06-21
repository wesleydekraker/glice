#define SRC_STRING L"AAAAAAAAAA"

// CWE124_Buffer_Underwrite__malloc_char_cpy_41.c
static void test_003()
{
    char * data;
    data = NULL;
    {
        char * dataBuffer = (char *)malloc(100*sizeof(char));
        memset(dataBuffer, 'A', 100-1);
        dataBuffer[100-1] = '\0';
        /* FIX: Set data pointer to the allocated memory buffer */
        data = dataBuffer;
    }
    CWE124_Buffer_Underwrite__malloc_char_cpy_41_goodG2BSink(data);
}

static void CWE124_Buffer_Underwrite__malloc_char_cpy_41_goodG2BSink(char * data) {}