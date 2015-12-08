A [compiler](Compiling.md) which creates a SequenceMatcher object.  Only [expressions](Syntax.md) which define a single sequence of byte matchers can be parsed, otherwise a ParseException is thrown.

```
SequenceMatcherCompiler compiler = new SequenceMatcherCompiler();

// Create a 'Hello world' matcher:
SequenceMatcher helloMatcher = compiler.compile("'Hello world'");

// Create a matcher for a zip file header:
SequenceMatcher zipFileHeader = compiler.compile("'PK' 03 04");

// Create a complex sequence of matchers:
SequenceMatcher sequence = compiler.compile("01 [lower]{4} [digit] \t 'version' [ff fe]");

// Throws a CompileException - results in more than one sequence to be matched:
SequenceMatcher bad = compiler.compile("'this' | 'that' | 'the other'");
```