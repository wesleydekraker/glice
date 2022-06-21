#define SRC_STRING L"X %s \0 %d"

// CWE121_Stack_Based_Buffer_Overflow__CWE193_wchar_t_alloca_memcpy_53d_badSink
void test_001(wchar_t * data)
{
    {
        wchar_t source[10+1] = SRC_STRING;
        /* Copy length + 1 to include NUL terminator from source */
        /* POTENTIAL FLAW: data may not have enough space to hold source */
        memcpy(data, source, (wcslen(source) + 1) * sizeof(wchar_t));
        printWLine(data);
    }
}