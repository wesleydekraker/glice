#define SRC_STRING L"AAAAAAAAAA"

// CWE122_Heap_Based_Buffer_Overflow__c_CWE805_int64_t_memmove_10_bad
void test_004()
{
    int64_t * data;
    data = NULL;
    if(globalTrue)
    {
        /* FLAW: Allocate and point data to a small buffer that is smaller than the large buffer used in the sinks */
        data = (int64_t *)malloc(50*sizeof(int64_t));
    }
    {
        int64_t source[100] = {0}; /* fill with 0's */
        /* POTENTIAL FLAW: Possible buffer overflow if data < 100 */
        memmove(data, source, 100*sizeof(int64_t));
        printLongLongLine(data[0]);
        free(data);
    }
}