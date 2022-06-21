package io.shiftleft.slice

import io.shiftleft.semanticcpg.language.toNodeTypeStarters
import io.shiftleft.slice.services.{GraphBuilder, SourceCodeGenerator}

class SourceCodeGeneratorTests extends BaseTests {
  "Get graph" should "return a joern graph of the test-001 file" in {
    val method = cpg.method.find(m => m.name == "test_001" && m.filename.endsWith("test_001.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method(wchar_t* variable_0)
                                   |{
                                   |  {
                                   |    wchar_t[11] variable_1;
                                   |    variable_1 = SRC_STRING({"%s%d\0"[10]});
                                   |    memcpy(variable_0, variable_1, wcslen(variable_1) + 1 * sizeof(wchar_t));
                                   |    printWLine(variable_0);
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-002 file" in {
    val method = cpg.method.find(m => m.name == "test_002" && m.filename.endsWith("test_002.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  wchar_t* variable_0;
                                   |  wchar_t* variable_1;
                                   |  variable_1 = (wchar_t*) ALLOCA(100 * sizeof(wchar_t));
                                   |  wmemset(variable_1, "A", 100 - 1);
                                   |  variable_1[100 - 1] = "\0";
                                   |  if (globalTrue)
                                   |  {
                                   |    variable_0 = variable_1 - 8;
                                   |  }
                                   |  {
                                   |    wchar_t[100] variable_2;
                                   |    wmemset(variable_2, "C", 100 - 1);
                                   |    variable_2[100 - 1] = "\0";
                                   |    wcscpy(variable_0, variable_2);
                                   |    printWLine(variable_0);
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-003 file" in {
    val method = cpg.method.find(m => m.name == "test_003" && m.filename.endsWith("test_003.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  char* variable_0;
                                   |  variable_0 = NULL;
                                   |  {
                                   |    char* variable_1;
                                   |    variable_1 = (char*) malloc(100 * sizeof(char));
                                   |    memset(variable_1, "A", 100 - 1);
                                   |    variable_1[100 - 1] = "\0";
                                   |    variable_0 = variable_1;
                                   |  }
                                   |  CWE124_Buffer_Underwrite__malloc_char_cpy_41_goodG2BSink(variable_0);
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-004 file" in {
    val method = cpg.method.find(m => m.name == "test_004" && m.filename.endsWith("test_004.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int64_t* variable_0;
                                   |  variable_0 = NULL;
                                   |  if (globalTrue)
                                   |  {
                                   |    variable_0 = (int64_t*) malloc(50 * sizeof(int64_t));
                                   |  }
                                   |  {
                                   |    int64_t[100] variable_1;
                                   |    variable_1 = {0};
                                   |    memmove(variable_0, variable_1, 100 * sizeof(int64_t));
                                   |    printLongLongLine(variable_0[0]);
                                   |    free(variable_0);
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-005 file" in {
    val method = cpg.method.find(m => m.name == "test_005" && m.filename.endsWith("test_005.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """int method()
                                   |{
                                   |  int variable_0;
                                   |  void funcPtr(int) = CWE121_Stack_Based_Buffer_Overflow__CWE129_fgets_65b_goodB2GSink;
                                   |  variable_0 = -1;
                                   |  {
                                   |    char[] variable_1;
                                   |    variable_1 = ""[0];
                                   |    if (fgets(variable_1, CHAR_ARRAY_SIZE({3 * sizeof(variable_0) + 2}), stdin) != NULL)
                                   |    {
                                   |      variable_0 = atoi(variable_1);
                                   |    }
                                   |    else
                                   |    {
                                   |      printLine(""[15]);
                                   |    }
                                   |  }
                                   |  funcPtr(variable_0);
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-006 file" in {
    val method = cpg.method.find(m => m.name == "test_006" && m.filename.endsWith("test_006.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  double* variable_0;
                                   |  variable_0 = NULL;
                                   |  if (GLOBAL_CONST_TRUE)
                                   |  {
                                   |    variable_0 = (double*) malloc(sizeof(*variable_0));
                                   |    *variable_0 = 1.7E300;
                                   |  }
                                   |  printDoubleLine(*variable_0);
                                   |  free(variable_0);
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-007 file" in {
    val method = cpg.method.find(m => m.name == "test_007" && m.filename.endsWith("test_007.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method(void* variable_0)
                                   |{
                                   |  wchar_t** variable_1;
                                   |  variable_1 = (wchar_t**) variable_0;
                                   |  wchar_t* variable_2;
                                   |  variable_2 = *variable_1;
                                   |  {
                                   |    wchar_t[50] variable_3;
                                   |    variable_3 = ""[0];
                                   |    size_t variable_4;
                                   |    size_t variable_5;
                                   |    variable_5 = wcslen(variable_2);
                                   |    for (variable_4 = 0; variable_4 < variable_5; variable_4++)
                                   |    {
                                   |      variable_3[variable_4] = variable_2[variable_4];
                                   |    }
                                   |    variable_3[50 - 1] = "\0";
                                   |    printWLine(variable_2);
                                   |    free(variable_2);
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-008 file" in {
    val method = cpg.method.find(m => m.name == "test_008" && m.filename.endsWith("test_008.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """wchar_t method(wchar_t* variable_0)
                                   |{
                                   |  variable_0 = (wchar_t*) malloc(50 * sizeof(wchar_t));
                                   |  variable_0[0] = "\0";
                                   |  return variable_0;
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-009 file" in {
    val method = cpg.method.find(m => m.name == "test_009" && m.filename.endsWith("test_009.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int variable_0;
                                   |  variable_0 = -1;
                                   |  {
                                   |    int variable_1;
                                   |    sockaddr_in variable_2;
                                   |    int variable_3;
                                   |    variable_3 = -1;
                                   |    int variable_4;
                                   |    variable_4 = -1;
                                   |    char[] variable_5;
                                   |    do
                                   |    {
                                   |      variable_3 = socket(2, SOCK_STREAM, IPPROTO_TCP);
                                   |      if (variable_3 == -1)
                                   |      {
                                   |        break;
                                   |      }
                                   |      memset(&variable_2, 0, sizeof(variable_2));
                                   |      variable_2.sin_family = 2;
                                   |      variable_2.sin_addr.s_addr = (in_addr_t) 0x00000000;
                                   |      variable_2.sin_port = htons(27015);
                                   |      if (bind(variable_3, (struct sockaddr*) &variable_2, sizeof(variable_2)) == -1)
                                   |      {
                                   |        continue;
                                   |      }
                                   |      if (listen(variable_3, 5) == -1)
                                   |      {
                                   |        break;
                                   |      }
                                   |      variable_4 = accept(variable_3, (void*) 0, (void*) 0);
                                   |      if (variable_4 == -1)
                                   |      {
                                   |        break;
                                   |      }
                                   |      variable_1 = recv(variable_4, variable_5, 3 * sizeof(variable_0) + 2 - 1, 0);
                                   |      if (variable_1 == -1 || variable_1 == 0)
                                   |      {
                                   |        break;
                                   |      }
                                   |      variable_5[variable_1] = "\0";
                                   |      variable_0 = atoi(variable_5);
                                   |    }
                                   |    while (0);
                                   |    if (variable_3 != -1)
                                   |    {
                                   |      close(variable_3);
                                   |    }
                                   |    if (variable_4 != -1)
                                   |    {
                                   |      close(variable_4);
                                   |    }
                                   |  }
                                   |  CWE122_Heap_Based_Buffer_Overflow__c_CWE129_listen_socket_22_goodB2G2Global = 1;
                                   |  CWE122_Heap_Based_Buffer_Overflow__c_CWE129_listen_socket_22_goodB2G2Sink(variable_0);
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test-010 file" in {
    val method = cpg.method.find(m => m.name == "test_010" && m.filename.endsWith("test_010.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  char* variable_0;
                                   |  variable_0 = NULL;
                                   |  while (1)
                                   |  {
                                   |    variable_0 = (char*) malloc(50 * sizeof(char));
                                   |    variable_0[0] = "\0";
                                   |    break;
                                   |  }
                                   |  {
                                   |    char[100] variable_1;
                                   |    memset(variable_1, "C", 100 - 1);
                                   |    variable_1[100 - 1] = "\0";
                                   |    strncpy(variable_0, variable_1, 100 - 1);
                                   |    variable_0[100 - 1] = "\0";
                                   |    printLine(variable_0);
                                   |    free(variable_0);
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_011 file" in {
    val method = cpg.method.find(m => m.name == "test_011" && m.filename.endsWith("test_011.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int variable_0;
                                   |  variable_0 = 0;
                                   |  int* variable_1;
                                   |  variable_1 = &variable_0;
                                   |  variable_0 = *variable_1;
                                   |  if (globalTrue({0}))
                                   |  {
                                   |    variable_0 = 1;
                                   |  }
                                   |  else if (10 == 1)
                                   |  {
                                   |    variable_0 = 10 == 1;
                                   |  }
                                   |  else
                                   |  {
                                   |    variable_0 = 2;
                                   |    if (globalTrue({0}))
                                   |      variable_0 = 1;
                                   |  }
                                   |  if (!globalTrue({0}))
                                   |  {
                                   |    variable_0 = !1 + -2 - 3 * 4 / 5 % +6;
                                   |    variable_0++;
                                   |    variable_0--;
                                   |    ++variable_0;
                                   |    --variable_0;
                                   |    variable_0 += 1;
                                   |    variable_0 -= 1;
                                   |    variable_0 *= 1;
                                   |    variable_0 /= 1;
                                   |    variable_0 %= 1;
                                   |    variable_0 = 1 == 2 & 3 > 4 | 5 < ~6 || 7 != 8 && 9 >= 10 << 11 <= 12;
                                   |    1 <= 2;
                                   |  }
                                   |  int variable_2;
                                   |  if (variable_2 = 2)
                                   |  {
                                   |    variable_2 = 1;
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_012 file" in {
    val method = cpg.method.find(m => m.name == "test_012" && m.filename.endsWith("test_012.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  twoIntsStruct* variable_0;
                                   |  variable_0 = NULL;
                                   |  variable_0 = (twoIntsStruct*) malloc(sizeof(variable_0));
                                   |  variable_0->intOne = 1;
                                   |  variable_0->intTwo = 2;
                                   |  CWE122_Heap_Based_Buffer_Overflow__sizeof_struct_53b_badSink(variable_0);
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_013 file" in {
    val method = cpg.method.find(m => m.name == "test_013" && m.filename.endsWith("test_013.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int variable_0;
                                   |  variable_0 = -1;
                                   |  switch(6)
                                   |  {
                                   |    case:
                                   |    6;
                                   |    variable_0 = 7;
                                   |    break;
                                   |    default:
                                   |    printLine(""[20]);
                                   |    break;
                                   |  }
                                   |  switch(7)
                                   |  {
                                   |    case:
                                   |    7;
                                   |    {
                                   |      int variable_1;
                                   |      int* variable_2;
                                   |      variable_2 = (int*) malloc(10 * sizeof(int));
                                   |      for (variable_1 = 0; variable_1 < 10; variable_1++)
                                   |      {
                                   |        variable_2[variable_1] = 0;
                                   |      }
                                   |      if (variable_0 >= 0)
                                   |      {
                                   |        variable_2[variable_0] = 1;
                                   |        for (variable_1 = 0; variable_1 < 10; variable_1++)
                                   |        {
                                   |          printIntLine(variable_2[variable_1]);
                                   |        }
                                   |      }
                                   |      else
                                   |      {
                                   |        printLine(""[31]);
                                   |      }
                                   |      free(variable_2);
                                   |    }
                                   |    break;
                                   |    default:
                                   |    printLine(""[20]);
                                   |    break;
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_014 file" in {
    val method = cpg.method.find(m => m.name == "test_014" && m.filename.endsWith("test_014.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  wchar_t* variable_0;
                                   |  variable_0 = NULL;
                                   |  goto source;
                                   |  source:
                                   |  variable_0 = (wchar_t*) malloc(10 * sizeof(wchar_t));
                                   |  {
                                   |    wchar_t[11] variable_1;
                                   |    variable_1 = SRC_STRING;
                                   |    wcsncpy(variable_0, variable_1, wcslen(variable_1) + 1);
                                   |    printWLine(variable_0);
                                   |    free(variable_0);
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_015 file" in {
    val method = cpg.method.find(m => m.name == "test_015" && m.filename.endsWith("test_015.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int variable_0;
                                   |  variable_0 = 0;
                                   |  return;
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_016 file" in {
    val method = cpg.method.find(m => m.name == "test_016" && m.filename.endsWith("test_016.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method(void)
                                   |{
                                   |  int variable_0;
                                   |  variable_0 = 0;
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_017 file" in {
    val method = cpg.method.find(m => m.name == "test_017" && m.filename.endsWith("test_017.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  void funcPtr(int);
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_018 file" in {
    val method = cpg.method.find(m => m.name == "test_018" && m.filename.endsWith("test_018.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int variable_0;
                                   |  variable_0 = 0;
                                   |  variable_0 = variable_0 ? 1 : 2;
                                   |  variable_0 = variable_0 ? 3 : ;
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_019 file" in {
    val method = cpg.method.find(m => m.name == "test_019" && m.filename.endsWith("test_019.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  tag_directives;
                                   |  tag_directives = {0, 0, 0};
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_020 file" in {
    val method = cpg.method.find(m => m.name == "test_020" && m.filename.endsWith("test_020.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int variable_0;
                                   |  variable_0 = 0;
                                   |  for (; variable_0 < 10; variable_0++)
                                   |  {
                                   |    test(variable_0);
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_021 file" in {
    val method = cpg.method.find(m => m.name == "test_021" && m.filename.endsWith("test_021.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int variable_0;
                                   |  variable_0 = 0;
                                   |  for (; ; )
                                   |  {
                                   |    test(variable_0);
                                   |  }
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_022 file" in {
    val method = cpg.method.find(m => m.name == "test_022" && m.filename.endsWith("test_022.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  while (1);
                                   |}
                                   |""".stripMargin)
  }

  "Get graph" should "return a joern graph of the test_023 file" in {
    val method = cpg.method.find(m => m.name == "test_023" && m.filename.endsWith("test_023.cpp"))
    val graph = new GraphBuilder(cpg).build(method.get)

    val generator = new SourceCodeGenerator(graph.get)
    assert(generator.toCode() == """void method()
                                   |{
                                   |  int* variable_0;
                                   |  variable_0 = NULL;
                                   |  delete variable_0;
                                   |}
                                   |""".stripMargin)
  }
}
