#define SRC_STRING L"AAAAAAAAAA"
#define globalTrue 0

// CWE122_Heap_Based_Buffer_Overflow__c_CWE805_int64_t_memmove_10_bad
void test_011()
{
    int data = 0;
    int *dataPointer = &data;
    data = *dataPointer;

    if (globalTrue)
    {
        data = 1;
    }
    else if (10 == 1)
    {
        data = 10 == 1;
    }
    else
    {
        data = 2;
        if (globalTrue)
            data = 1;
    }

    if (!globalTrue)
    {
        data = !1 + -2 - 3 * 4 / 5 % +6;
        data++;
        data--;
        ++data;
        --data;

        data += 1;
        data -= 1;
        data *= 1;
        data /= 1;
        data %= 1;

        data = 1 == 2 & 3 > 4 | 5 < ~6 || 7 != 8 && 9 >= 10 << 11 <= 12;

        1 <= 2;
    }

    int a;
    if (a = 2)
    {
        a = 1;
    }
}