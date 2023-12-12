package io.shiftleft.slice.models

import io.shiftleft.codepropertygraph.generated.{ControlStructureTypes, Operators}

object NodeType {
  val MEMBER = "member"
  val METHOD = "method"
  val BLOCK = "block"
  val IDENTIFIER = "identifier"
  val METHOD_PARAMETER_IN = "method_parameter_in"
  val METHOD_PARAMETER_OUT = "method_parameter_out"
  val METHOD_CALL = "method_call"
  val LOCAL = "local"
  val TYPE = "type"
  val POINTER_TYPE = "pointer_type"
  val METHOD_RETURN = "method_return"
  val STRING_LITERAL = "string_literal"
  val NUMERIC_LITERAL = "numeric_literal"
  val RETURN = "return"
  val JUMP_TARGET = "jump_target"
  val FIELD_IDENTIFIER = "field_identifier"
  val FUNCTION_POINTER_IDENTIFIER = "function_pointer_identifier"
  val TYPE_DECL = "type_decl"
  val PROBLEM_STATEMENT = "problem_statement"
  val PROBLEM_EXPRESSION = "problem_expression"
  val PROBLEM_DECLARATION = "problem_declaration"
  val ASM_DECLARATION = "asm_declaration"
  val ARRAY_RANGE = "array_range"
  val UNKNOWN = "unknown"
  val OPERATOR_ADDITION = "operator_addition"
  val OPERATOR_SUBTRACTION = "operator_subtraction"
  val OPERATOR_MULTIPLICATION = "operator_multiplication"
  val OPERATOR_DIVISION = "operator_division"
  val OPERATOR_EXPONENTIATION = "operator_exponentiation"
  val OPERATOR_MODULO = "operator_modulo"
  val OPERATOR_SHIFT_LEFT = "operator_shift_left"
  val OPERATOR_LOGICAL_SHIFT_RIGHT = "operator_logical_shift_right"
  val OPERATOR_ARITHMETIC_SHIFT_RIGHT = "operator_arithmetic_shift_right"
  val OPERATOR_NOT = "operator_not"
  val OPERATOR_AND = "operator_and"
  val OPERATOR_OR = "operator_or"
  val OPERATOR_XOR = "operator_xor"
  val OPERATOR_ASSIGNMENT_PLUS = "operator_assignment_plus"
  val OPERATOR_ASSIGNMENT_MINUS = "operator_assignment_minus"
  val OPERATOR_ASSIGNMENT_MULTIPLICATION = "operator_assignment_multiplication"
  val OPERATOR_ASSIGNMENT_DIVISION = "operator_assignment_division"
  val OPERATOR_ASSIGNMENT_EXPONENTIATION = "operator_assignment_exponentiation"
  val OPERATOR_ASSIGNMENT_MODULO = "operator_assignment_modulo"
  val OPERATOR_ASSIGNMENT_SHIFT_LEFT = "operator_assignment_shift_left"
  val OPERATOR_ASSIGNMENT_LOGICAL_SHIFT_RIGHT = "operator_assignment_logical_shift_right"
  val OPERATOR_ASSIGNMENT_ARITHMETIC_SHIFT_RIGHT = "operator_assignment_arithmetic_shift_right"
  val OPERATOR_ASSIGNMENT_AND = "operator_assignment_and"
  val OPERATOR_ASSIGNMENT_OR = "operator_assignment_or"
  val OPERATOR_ASSIGNMENT_XOR = "operator_assignment_xor"
  val OPERATOR_ASSIGNMENT = "operator_assignment"
  val OPERATOR_MINUS = "operator_minus"
  val OPERATOR_PLUS = "operator_plus"
  val OPERATOR_PRE_INCREMENT = "operator_pre_increment"
  val OPERATOR_PRE_DECREMENT = "operator_pre_decrement"
  val OPERATOR_POST_INCREMENT = "operator_post_increment"
  val OPERATOR_POST_DECREMENT = "operator_post_decrement"
  val OPERATOR_LOGICAL_NOT = "operator_logical_not"
  val OPERATOR_LOGICAL_OR = "operator_logical_or"
  val OPERATOR_LOGICAL_AND = "operator_logical_and"
  val OPERATOR_EQUALS = "operator_equals"
  val OPERATOR_NOT_EQUALS = "operator_not_equals"
  val OPERATOR_GREATER_THAN = "operator_greater_than"
  val OPERATOR_LESS_THAN = "operator_less_than"
  val OPERATOR_GREATER_EQUALS_THAN = "operator_greater_equals_than"
  val OPERATOR_LESS_EQUALS_THAN = "operator_less_equals_than"
  val OPERATOR_INSTANCE_OF = "operator_instance_of"
  val OPERATOR_MEMBER_ACCESS = "operator_member_access"
  val OPERATOR_INDIRECT_MEMBER_ACCESS = "operator_indirect_member_access"
  val OPERATOR_COMPUTED_MEMBER_ACCESS = "operator_computed_member_access"
  val OPERATOR_INDIRECT_COMPUTED_MEMBER_ACCESS = "operator_indirect_computed_member_access"
  val OPERATOR_INDIRECTION = "operator_indirection"
  val OPERATOR_DELETE = "operator_delete"
  val OPERATOR_CONDITIONAL = "operator_conditional"
  val OPERATOR_ELVIS = "operator_elvis"
  val OPERATOR_CAST = "operator_cast"
  val OPERATOR_COMPARE = "operator_compare"
  val OPERATOR_ADDRESS_OF = "operator_address_of"
  val OPERATOR_SIZE_OF = "operator_size_of"
  val OPERATOR_FIELD_ACCESS = "operator_field_access"
  val OPERATOR_INDIRECT_FIELD_ACCESS = "operator_indirect_field_access"
  val OPERATOR_INDEX_ACCESS = "operator_index_access"
  val OPERATOR_INDIRECT_INDEX_ACCESS = "operator_indirect_index_access"
  val OPERATOR_POINTER_SHIFT = "operator_pointer_shift"
  val OPERATOR_GET_ELEMENT_PTR = "operator_get_element_ptr"
  val OPERATOR_FORMAT_STRING = "operator_format_string"
  val OPERATOR_FORMATTED_VALUE = "operator_formatted_value"
  val OPERATOR_RANGE = "operator_range"
  val OPERATOR_IN = "operator_in"
  val OPERATOR_NOT_IN = "operator_not_in"
  val OPERATOR_IS = "operator_is"
  val OPERATOR_IS_NOT = "operator_is_not"
  val OPERATOR_NOT_NULL_ASSERT = "operator_not_null_assert"
  val OPERATOR_LENGTH_OF = "operator_length_of"
  val OPERATOR_SAFE_NAVIGATION = "operator_safe_navigation"
  val OPERATOR_CAST_TYPE_ID = "operator_cast_type_id"
  val OPERATOR_TYPE_ID = "operator_type_id"
  val OPERATOR_ARRAY_INITIALIZER = "operator_array_initializer"
  val CONTROL_STRUCTURE_BREAK = "control_structure_break"
  val CONTROL_STRUCTURE_CONTINUE = "control_structure_continue"
  val CONTROL_STRUCTURE_WHILE = "control_structure_while"
  val CONTROL_STRUCTURE_DO = "control_structure_do"
  val CONTROL_STRUCTURE_FOR = "control_structure_for"
  val CONTROL_STRUCTURE_GOTO = "control_structure_goto"
  val CONTROL_STRUCTURE_IF = "control_structure_if"
  val CONTROL_STRUCTURE_ELSE = "control_structure_else"
  val CONTROL_STRUCTURE_SWITCH = "control_structure_switch"
  val CONTROL_STRUCTURE_CASE = "control_structure_case"
  val CONTROL_STRUCTURE_DEFAULT = "control_structure_default"
  val CONTROL_STRUCTURE_TRY = "control_structure_try"
  val CONTROL_STRUCTURE_THROW = "control_structure_throw"

  val OPERATOR_MAP: Map[String, String] = Map(
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

  val CONTROL_STRUCTURE_MAP: Map[String, String] = Map(
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
}
