/* Generated By:JJTree&JavaCC: Do not edit this line. JassParserConstants.java */
package jass.parser;

public interface JassParserConstants {

  int EOF = 0;
  int WhiteSpaceHor = 1;
  int WhiteSpaceVer = 2;
  int REQUIRE = 3;
  int INVARIANT = 4;
  int ENSURE = 5;
  int VARIANT = 6;
  int CHECK = 7;
  int RESCUE = 8;
  int ASS_END = 12;
  int CHANGEONLY = 13;
  int FORALL = 14;
  int EXISTS = 15;
  int RANGE = 16;
  int RETRY = 17;
  int FORALL_SEP = 18;
  int TRACE = 19;
  int ASSERTION_COMMENT = 21;
  int SINGLE_LINE_COMMENT = 23;
  int FORMAL_COMMENT = 24;
  int MULTI_LINE_COMMENT = 25;
  int TRACE_CALL_OPERATOR = 27;
  int TRACE_CHAOS_OPERATOR = 28;
  int TRACE_CHOICE_OPERATOR = 29;
  int TRACE_COMPLEMENT_OPERATOR = 30;
  int TRACE_EXECUTE_OPERATOR = 31;
  int TRACE_CONDITION = 32;
  int TRACE_DEADLOCK_OPERATOR = 33;
  int TRACE_ELSE_OPERATOR = 34;
  int TRACE_IF_OPERATOR = 35;
  int TRACE_PARALLEL_OPERATOR = 36;
  int TRACE_PREFIX_OPERATOR = 37;
  int TRACE_TERMINATION_OPERATOR = 38;
  int ABSTRACT = 39;
  int ASSERT = 40;
  int BOOLEAN = 41;
  int BREAK = 42;
  int BYTE = 43;
  int CASE = 44;
  int CATCH = 45;
  int CHAR = 46;
  int CLASS = 47;
  int CONST = 48;
  int CONTINUE = 49;
  int _DEFAULT = 50;
  int DO = 51;
  int DOUBLE = 52;
  int ELSE = 53;
  int EXTENDS = 54;
  int FALSE = 55;
  int FINAL = 56;
  int FINALLY = 57;
  int FLOAT = 58;
  int FOR = 59;
  int GOTO = 60;
  int IF = 61;
  int IMPLEMENTS = 62;
  int IMPORT = 63;
  int INSTANCEOF = 64;
  int INT = 65;
  int INTERFACE = 66;
  int LONG = 67;
  int NATIVE = 68;
  int NEW = 69;
  int NULL = 70;
  int PACKAGE = 71;
  int PRIVATE = 72;
  int PROTECTED = 73;
  int PUBLIC = 74;
  int RETURN = 75;
  int SHORT = 76;
  int STATIC = 77;
  int SUPER = 78;
  int SWITCH = 79;
  int SYNCHRONIZED = 80;
  int THIS = 81;
  int THROW = 82;
  int THROWS = 83;
  int TRANSIENT = 84;
  int TRUE = 85;
  int TRY = 86;
  int VOID = 87;
  int VOLATILE = 88;
  int WHILE = 89;
  int STRICTFP = 90;
  int INTEGER_LITERAL = 91;
  int DECIMAL_LITERAL = 92;
  int HEX_LITERAL = 93;
  int OCTAL_LITERAL = 94;
  int FLOATING_POINT_LITERAL = 95;
  int EXPONENT = 96;
  int CHARACTER_LITERAL = 97;
  int STRING_LITERAL = 98;
  int IDENTIFIER = 99;
  int LETTER = 100;
  int DIGIT = 101;
  int LPAREN = 102;
  int RPAREN = 103;
  int LBRACE = 104;
  int RBRACE = 105;
  int LBRACKET = 106;
  int RBRACKET = 107;
  int SEMICOLON = 108;
  int COMMA = 109;
  int DOT = 110;
  int ASSIGN = 111;
  int GT = 112;
  int LT = 113;
  int BANG = 114;
  int TILDE = 115;
  int HOOK = 116;
  int COLON = 117;
  int EQ = 118;
  int LE = 119;
  int GE = 120;
  int NE = 121;
  int SC_OR = 122;
  int SC_AND = 123;
  int INCR = 124;
  int DECR = 125;
  int PLUS = 126;
  int MINUS = 127;
  int STAR = 128;
  int SLASH = 129;
  int BIT_AND = 130;
  int BIT_OR = 131;
  int XOR = 132;
  int REM = 133;
  int LSHIFT = 134;
  int RSIGNEDSHIFT = 135;
  int RUNSIGNEDSHIFT = 136;
  int PLUSASSIGN = 137;
  int MINUSASSIGN = 138;
  int STARASSIGN = 139;
  int SLASHASSIGN = 140;
  int ANDASSIGN = 141;
  int ORASSIGN = 142;
  int XORASSIGN = 143;
  int REMASSIGN = 144;
  int LSHIFTASSIGN = 145;
  int RSIGNEDSHIFTASSIGN = 146;
  int RUNSIGNEDSHIFTASSIGN = 147;

  int DEFAULT = 0;
  int ASSERTION = 1;
  int IN_ASSERTION_COMMENT = 2;
  int IN_SINGLE_LINE_COMMENT = 3;
  int IN_FORMAL_COMMENT = 4;
  int IN_MULTI_LINE_COMMENT = 5;
  int TRACEASSERTION = 6;

  String[] tokenImage = {
    "<EOF>",
    "<WhiteSpaceHor>",
    "<WhiteSpaceVer>",
    "<REQUIRE>",
    "<INVARIANT>",
    "<ENSURE>",
    "<VARIANT>",
    "<CHECK>",
    "<RESCUE>",
    "\"//\"",
    "<token of kind 10>",
    "\"/*\"",
    "\"**/\"",
    "\"changeonly\"",
    "\"forall\"",
    "\"exists\"",
    "\"..\"",
    "\"retry\"",
    "\"#\"",
    "\"trace\"",
    "\"/#\"",
    "\"#/\"",
    "<token of kind 22>",
    "<SINGLE_LINE_COMMENT>",
    "\"*/\"",
    "\"*/\"",
    "<token of kind 26>",
    "\"CALL\"",
    "\"ANY\"",
    "\"<|>\"",
    "\"EXCEPT\"",
    "\"EXECUTE\"",
    "\"WHERE\"",
    "\"STOP\"",
    "\"ELSE\"",
    "\"IF\"",
    "\"|<>|\"",
    "\"->\"",
    "\"TERM\"",
    "\"abstract\"",
    "\"assert\"",
    "\"boolean\"",
    "\"break\"",
    "\"byte\"",
    "\"case\"",
    "\"catch\"",
    "\"char\"",
    "\"class\"",
    "\"const\"",
    "\"continue\"",
    "\"default\"",
    "\"do\"",
    "\"double\"",
    "\"else\"",
    "\"extends\"",
    "\"false\"",
    "\"final\"",
    "\"finally\"",
    "\"float\"",
    "\"for\"",
    "\"goto\"",
    "\"if\"",
    "\"implements\"",
    "\"import\"",
    "\"instanceof\"",
    "\"int\"",
    "\"interface\"",
    "\"long\"",
    "\"native\"",
    "\"new\"",
    "\"null\"",
    "\"package\"",
    "\"private\"",
    "\"protected\"",
    "\"public\"",
    "\"return\"",
    "\"short\"",
    "\"static\"",
    "\"super\"",
    "\"switch\"",
    "\"synchronized\"",
    "\"this\"",
    "\"throw\"",
    "\"throws\"",
    "\"transient\"",
    "\"true\"",
    "\"try\"",
    "\"void\"",
    "\"volatile\"",
    "\"while\"",
    "\"strictfp\"",
    "<INTEGER_LITERAL>",
    "<DECIMAL_LITERAL>",
    "<HEX_LITERAL>",
    "<OCTAL_LITERAL>",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<CHARACTER_LITERAL>",
    "<STRING_LITERAL>",
    "<IDENTIFIER>",
    "<LETTER>",
    "<DIGIT>",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\";\"",
    "\",\"",
    "\".\"",
    "\"=\"",
    "\">\"",
    "\"<\"",
    "\"!\"",
    "\"~\"",
    "\"?\"",
    "\":\"",
    "\"==\"",
    "\"<=\"",
    "\">=\"",
    "\"!=\"",
    "\"||\"",
    "\"&&\"",
    "\"++\"",
    "\"--\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"&\"",
    "\"|\"",
    "\"^\"",
    "\"%\"",
    "\"<<\"",
    "\">>\"",
    "\">>>\"",
    "\"+=\"",
    "\"-=\"",
    "\"*=\"",
    "\"/=\"",
    "\"&=\"",
    "\"|=\"",
    "\"^=\"",
    "\"%=\"",
    "\"<<=\"",
    "\">>=\"",
    "\">>>=\"",
  };

}
