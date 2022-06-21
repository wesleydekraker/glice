package io.shiftleft.slice.models

import io.shiftleft.codepropertygraph.generated.{ControlStructureTypes, Operators}

object NodeType {
  val MEMBER = "member"
  val METHOD = "method"
  val BLOCK = "block"
  val IDENTIFIER = "identifier"
  val METHOD_PARAMETER_IN = "method-parameter-in"
  val METHOD_PARAMETER_OUT = "method-parameter-out"
  val METHOD_CALL = "method-call"
  val LOCAL = "local"
  val TYPE = "type"
  val POINTER_TYPE = "pointer-type"
  val METHOD_RETURN = "method-return"
  val STRING_LITERAL = "string-literal"
  val STRING_NULL_CHARACTER = "string-null-character"
  val STRING_FORMAT_SPECIFIER = "string-format-specifier"
  val STRING_LENGTH = "string-length"
  val NUMERIC_LITERAL = "int-literal"
  val RETURN = "return"
  val JUMP_TARGET = "jump-target"
  val FIELD_IDENTIFIER = "field-identifier"
  val FUNCTION_POINTER_IDENTIFIER = "function-pointer-identifier"
  val TYPE_DECL = "type-decl"
  val PROBLEM_STATEMENT = "problem-statement"
  val PROBLEM_EXPRESSION = "problem-expression"
  val PROBLEM_DECLARATION = "problem-declaration"
  val ASM_DECLARATION = "asm-declaration"
  val ARRAY_RANGE = "array-range"
  val UNKNOWN = "unknown"
  val OPERATOR_ADDITION = "operator-addition"
  val OPERATOR_SUBTRACTION = "operator-subtraction"
  val OPERATOR_MULTIPLICATION = "operator-multiplication"
  val OPERATOR_DIVISION = "operator-division"
  val OPERATOR_EXPONENTIATION = "operator-exponentiation"
  val OPERATOR_MODULO = "operator-modulo"
  val OPERATOR_SHIFT_LEFT = "operator-shift-left"
  val OPERATOR_LOGICAL_SHIFT_RIGHT = "operator-logical-shift-right"
  val OPERATOR_ARITHMETIC_SHIFT_RIGHT = "operator-arithmetic-shift-right"
  val OPERATOR_NOT = "operator-not"
  val OPERATOR_AND = "operator-and"
  val OPERATOR_OR = "operator-or"
  val OPERATOR_XOR = "operator-xor"
  val OPERATOR_ASSIGNMENT_PLUS = "operator-assignment-plus"
  val OPERATOR_ASSIGNMENT_MINUS = "operator-assignment-minus"
  val OPERATOR_ASSIGNMENT_MULTIPLICATION = "operator-assignment-multiplication"
  val OPERATOR_ASSIGNMENT_DIVISION = "operator-assignment-division"
  val OPERATOR_ASSIGNMENT_EXPONENTIATION = "operator-assignment-exponentiation"
  val OPERATOR_ASSIGNMENT_MODULO = "operator-assignment-modulo"
  val OPERATOR_ASSIGNMENT_SHIFT_LEFT = "operator-assignment-shift-left"
  val OPERATOR_ASSIGNMENT_LOGICAL_SHIFT_RIGHT = "operator-assignment-logical-shift-right"
  val OPERATOR_ASSIGNMENT_ARITHMETIC_SHIFT_RIGHT = "operator-assignment-arithmetic-shift-right"
  val OPERATOR_ASSIGNMENT_AND = "operator-assignment-and"
  val OPERATOR_ASSIGNMENT_OR = "operator-assignment-or"
  val OPERATOR_ASSIGNMENT_XOR = "operator-assignment-xor"
  val OPERATOR_ASSIGNMENT = "operator-assignment"
  val OPERATOR_MINUS = "operator-minus"
  val OPERATOR_PLUS = "operator-plus"
  val OPERATOR_PRE_INCREMENT = "operator-pre-increment"
  val OPERATOR_PRE_DECREMENT = "operator-pre-decrement"
  val OPERATOR_POST_INCREMENT = "operator-post-increment"
  val OPERATOR_POST_DECREMENT = "operator-post-decrement"
  val OPERATOR_LOGICAL_NOT = "operator-logical-not"
  val OPERATOR_LOGICAL_OR = "operator-logical-or"
  val OPERATOR_LOGICAL_AND = "operator-logical-and"
  val OPERATOR_EQUALS = "operator-equals"
  val OPERATOR_NOT_EQUALS = "operator-not-equals"
  val OPERATOR_GREATER_THAN = "operator-greater-than"
  val OPERATOR_LESS_THAN = "operator-less-than"
  val OPERATOR_GREATER_EQUALS_THAN = "operator-greater-equals-than"
  val OPERATOR_LESS_EQUALS_THAN = "operator-less-equals-than"
  val OPERATOR_INSTANCE_OF = "operator-instance-of"
  val OPERATOR_MEMBER_ACCESS = "operator-member-access"
  val OPERATOR_INDIRECT_MEMBER_ACCESS = "operator-indirect-member-access"
  val OPERATOR_COMPUTED_MEMBER_ACCESS = "operator-computed-member-access"
  val OPERATOR_INDIRECT_COMPUTED_MEMBER_ACCESS = "operator-indirect-computed-member-access"
  val OPERATOR_INDIRECTION = "operator-indirection"
  val OPERATOR_DELETE = "operator-delete"
  val OPERATOR_CONDITIONAL = "operator-conditional"
  val OPERATOR_ELVIS = "operator-elvis"
  val OPERATOR_CAST = "operator-cast"
  val OPERATOR_COMPARE = "operator-compare"
  val OPERATOR_ADDRESS_OF = "operator-address-of"
  val OPERATOR_SIZE_OF = "operator-size-of"
  val OPERATOR_FIELD_ACCESS = "operator-field-access"
  val OPERATOR_INDIRECT_FIELD_ACCESS = "operator-indirect-field-access"
  val OPERATOR_INDEX_ACCESS = "operator-index-access"
  val OPERATOR_INDIRECT_INDEX_ACCESS = "operator-indirect-index-access"
  val OPERATOR_POINTER_SHIFT = "operator-pointer-shift"
  val OPERATOR_GET_ELEMENT_PTR = "operator-get-element-ptr"
  val OPERATOR_FORMAT_STRING = "operator-format-string"
  val OPERATOR_FORMATTED_VALUE = "operator-formatted-value"
  val OPERATOR_RANGE = "operator-range"
  val OPERATOR_IN = "operator-in"
  val OPERATOR_NOT_IN = "operator-not-in"
  val OPERATOR_IS = "operator-is"
  val OPERATOR_IS_NOT = "operator-is-not"
  val OPERATOR_NOT_NULL_ASSERT = "operator-not-null-assert"
  val OPERATOR_LENGTH_OF = "operator-length-of"
  val OPERATOR_SAFE_NAVIGATION = "operator-safe-navigation"
  val OPERATOR_CAST_TYPE_ID = "operator-cast-type-id"
  val OPERATOR_TYPE_ID = "operator-type-id"
  val OPERATOR_ARRAY_INITIALIZER = "operator-array-initializer"
  val CONTROL_STRUCTURE_BREAK = "control-structure-break"
  val CONTROL_STRUCTURE_CONTINUE = "control-structure-continue"
  val CONTROL_STRUCTURE_WHILE = "control-structure-while"
  val CONTROL_STRUCTURE_DO = "control-structure-do"
  val CONTROL_STRUCTURE_FOR = "control-structure-for"
  val CONTROL_STRUCTURE_GOTO = "control-structure-goto"
  val CONTROL_STRUCTURE_IF = "control-structure-if"
  val CONTROL_STRUCTURE_ELSE = "control-structure-else"
  val CONTROL_STRUCTURE_SWITCH = "control-structure-switch"
  val CONTROL_STRUCTURE_CASE = "control-structure-case"
  val CONTROL_STRUCTURE_DEFAULT = "control-structure-default"
  val CONTROL_STRUCTURE_TRY = "control-structure-try"
  val CONTROL_STRUCTURE_THROW = "control-structure-throw"

  val OPERATOR_MAP = Map(
    Operators.addition -> OPERATOR_ADDITION,
    Operators.subtraction -> OPERATOR_SUBTRACTION,
    Operators.multiplication -> OPERATOR_MULTIPLICATION,
    Operators.division -> OPERATOR_DIVISION,
    Operators.exponentiation -> OPERATOR_EXPONENTIATION,
    Operators.modulo -> OPERATOR_MODULO,
    Operators.shiftLeft -> OPERATOR_SHIFT_LEFT,
    Operators.logicalShiftRight -> OPERATOR_LOGICAL_SHIFT_RIGHT,
    Operators.arithmeticShiftRight -> OPERATOR_ARITHMETIC_SHIFT_RIGHT,
    Operators.not -> OPERATOR_NOT,
    Operators.and -> OPERATOR_AND,
    Operators.or -> OPERATOR_OR,
    Operators.xor -> OPERATOR_XOR,
    Operators.assignmentPlus -> OPERATOR_ASSIGNMENT_PLUS,
    Operators.assignmentMinus -> OPERATOR_ASSIGNMENT_MINUS,
    Operators.assignmentMultiplication -> OPERATOR_ASSIGNMENT_MULTIPLICATION,
    Operators.assignmentDivision -> OPERATOR_ASSIGNMENT_DIVISION,
    Operators.assignmentExponentiation -> OPERATOR_ASSIGNMENT_EXPONENTIATION,
    Operators.assignmentModulo -> OPERATOR_ASSIGNMENT_MODULO,
    Operators.assignmentShiftLeft -> OPERATOR_ASSIGNMENT_SHIFT_LEFT,
    Operators.assignmentLogicalShiftRight -> OPERATOR_ASSIGNMENT_LOGICAL_SHIFT_RIGHT,
    Operators.assignmentArithmeticShiftRight -> OPERATOR_ASSIGNMENT_ARITHMETIC_SHIFT_RIGHT,
    Operators.assignmentAnd -> OPERATOR_ASSIGNMENT_AND,
    Operators.assignmentOr -> OPERATOR_ASSIGNMENT_OR,
    Operators.assignmentXor -> OPERATOR_ASSIGNMENT_XOR,
    Operators.assignment -> OPERATOR_ASSIGNMENT,
    Operators.minus -> OPERATOR_MINUS,
    Operators.plus -> OPERATOR_PLUS,
    Operators.preIncrement -> OPERATOR_PRE_INCREMENT,
    Operators.preDecrement -> OPERATOR_PRE_DECREMENT,
    Operators.postIncrement -> OPERATOR_POST_INCREMENT,
    Operators.postDecrement -> OPERATOR_POST_DECREMENT,
    Operators.logicalNot -> OPERATOR_LOGICAL_NOT,
    Operators.logicalOr -> OPERATOR_LOGICAL_OR,
    Operators.logicalAnd -> OPERATOR_LOGICAL_AND,
    Operators.equals -> OPERATOR_EQUALS,
    Operators.notEquals -> OPERATOR_NOT_EQUALS,
    Operators.greaterThan -> OPERATOR_GREATER_THAN,
    Operators.lessThan -> OPERATOR_LESS_THAN,
    Operators.greaterEqualsThan -> OPERATOR_GREATER_EQUALS_THAN,
    Operators.lessEqualsThan -> OPERATOR_LESS_EQUALS_THAN,
    Operators.instanceOf -> OPERATOR_INSTANCE_OF,
    Operators.memberAccess -> OPERATOR_MEMBER_ACCESS,
    Operators.indirectMemberAccess -> OPERATOR_INDIRECT_MEMBER_ACCESS,
    Operators.computedMemberAccess -> OPERATOR_COMPUTED_MEMBER_ACCESS,
    Operators.indirectComputedMemberAccess -> OPERATOR_INDIRECT_COMPUTED_MEMBER_ACCESS,
    Operators.indirection -> OPERATOR_INDIRECTION,
    Operators.delete -> OPERATOR_DELETE,
    Operators.conditional -> OPERATOR_CONDITIONAL,
    Operators.elvis -> OPERATOR_ELVIS,
    Operators.cast -> OPERATOR_CAST,
    Operators.compare -> OPERATOR_COMPARE,
    Operators.addressOf -> OPERATOR_ADDRESS_OF,
    Operators.sizeOf -> OPERATOR_SIZE_OF,
    Operators.fieldAccess -> OPERATOR_FIELD_ACCESS,
    Operators.indirectFieldAccess -> OPERATOR_INDIRECT_FIELD_ACCESS,
    Operators.indexAccess -> OPERATOR_INDEX_ACCESS,
    Operators.indirectIndexAccess -> OPERATOR_INDIRECT_INDEX_ACCESS,
    Operators.pointerShift -> OPERATOR_POINTER_SHIFT,
    Operators.getElementPtr -> OPERATOR_GET_ELEMENT_PTR,
    Operators.formatString -> OPERATOR_FORMAT_STRING,
    Operators.formattedValue -> OPERATOR_FORMATTED_VALUE,
    Operators.range -> OPERATOR_RANGE,
    Operators.in -> OPERATOR_IN,
    Operators.notIn -> OPERATOR_NOT_IN,
    Operators.is -> OPERATOR_IS,
    Operators.isNot -> OPERATOR_IS_NOT,
    Operators.notNullAssert -> OPERATOR_NOT_NULL_ASSERT,
    Operators.lengthOf -> OPERATOR_LENGTH_OF,
    Operators.safeNavigation -> OPERATOR_SAFE_NAVIGATION,
    "<operator>.arrayInitializer" -> OPERATOR_ARRAY_INITIALIZER
  )

  val CONTROL_STRUCTURE_MAP = Map(
    ControlStructureTypes.BREAK -> CONTROL_STRUCTURE_BREAK,
    ControlStructureTypes.CONTINUE -> CONTROL_STRUCTURE_CONTINUE,
    ControlStructureTypes.WHILE -> CONTROL_STRUCTURE_WHILE,
    ControlStructureTypes.DO -> CONTROL_STRUCTURE_DO,
    ControlStructureTypes.FOR -> CONTROL_STRUCTURE_FOR,
    ControlStructureTypes.GOTO -> CONTROL_STRUCTURE_GOTO,
    ControlStructureTypes.IF -> CONTROL_STRUCTURE_IF,
    ControlStructureTypes.ELSE -> CONTROL_STRUCTURE_ELSE,
    ControlStructureTypes.SWITCH -> CONTROL_STRUCTURE_SWITCH,
    ControlStructureTypes.TRY -> CONTROL_STRUCTURE_TRY,
    ControlStructureTypes.THROW -> CONTROL_STRUCTURE_THROW
  )

  val OPERATOR_LEFT_MAP = Map(
    OPERATOR_PLUS -> "+",
    OPERATOR_MINUS -> "-",
    OPERATOR_LOGICAL_NOT -> "!",
    OPERATOR_PRE_INCREMENT -> "++",
    OPERATOR_PRE_DECREMENT -> "--",
    OPERATOR_ADDRESS_OF -> "&",
    OPERATOR_INDIRECTION -> "*",
    OPERATOR_NOT -> "~"
  )

  val OPERATOR_CENTER_MAP = Map(
    OPERATOR_ADDITION -> "+",
    OPERATOR_SUBTRACTION -> "-",
    OPERATOR_MULTIPLICATION -> "*",
    OPERATOR_DIVISION -> "/",
    OPERATOR_MODULO -> "%",
    OPERATOR_SHIFT_LEFT -> "<<",
    OPERATOR_ARITHMETIC_SHIFT_RIGHT -> ">>",
    OPERATOR_AND -> "&",
    OPERATOR_OR -> "|",
    OPERATOR_XOR -> "^",
    OPERATOR_ASSIGNMENT_PLUS -> "+=",
    OPERATOR_ASSIGNMENT_MINUS -> "-=",
    OPERATOR_ASSIGNMENT_MULTIPLICATION -> "*=",
    OPERATOR_ASSIGNMENT_DIVISION -> "/=",
    OPERATOR_ASSIGNMENT_EXPONENTIATION -> "?=",
    OPERATOR_ASSIGNMENT_MODULO -> "%=",
    OPERATOR_ASSIGNMENT_SHIFT_LEFT -> "<<=",
    OPERATOR_ASSIGNMENT_LOGICAL_SHIFT_RIGHT -> ">>=",
    OPERATOR_ASSIGNMENT_ARITHMETIC_SHIFT_RIGHT -> ">>=",
    OPERATOR_ASSIGNMENT_AND -> "&=",
    OPERATOR_ASSIGNMENT_OR -> "|=",
    OPERATOR_ASSIGNMENT_XOR -> "^=",
    OPERATOR_ASSIGNMENT -> "=",
    OPERATOR_LOGICAL_OR -> "||",
    OPERATOR_LOGICAL_AND -> "&&",
    OPERATOR_EQUALS -> "==",
    OPERATOR_NOT_EQUALS -> "!=",
    OPERATOR_GREATER_THAN -> ">",
    OPERATOR_LESS_THAN -> "<",
    OPERATOR_GREATER_EQUALS_THAN -> ">=",
    OPERATOR_LESS_EQUALS_THAN -> "<=",
  )

  val OPERATOR_RIGHT_MAP = Map(
    OPERATOR_POST_INCREMENT -> "++",
    OPERATOR_POST_DECREMENT -> "--",
  )
}
