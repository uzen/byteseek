A [compiler](Compiling.md) which creates a ByteMatcher object.  Only [expressions](Syntax.md) which define a byte matcher can be parsed, otherwise a CompileException is thrown.

```
ByteMatcherCompiler compiler = new ByteMatcherCompiler();

// Create a matcher for whitespace tab, newline, return and space bytes:
ByteMatcher whitespace = compiler.compile("[09 0a 0d 20]");

// Create a matcher for bits 01010101 = 0x55:
ByteMatcher bitPattern = compiler.compile("&55");

// Throws a ParseException - a sequence of bytes needs to be matched:
ByteMatcher bad = compiler.compile("01 02 03");
```