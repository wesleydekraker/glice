static void test_006()
{
    double * data;
    /* Initialize data */
    data = NULL;
    if(GLOBAL_CONST_TRUE)
    {
        /* FIX: Using sizeof the data type in malloc() */
        data = (double *)malloc(sizeof(*data));
        *data = 1.7E300;
    }
    /* POTENTIAL FLAW: Attempt to use data, which may not have enough memory allocated */
    printDoubleLine(*data);
    free(data);
}