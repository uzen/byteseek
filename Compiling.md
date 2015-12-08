The byteseek library provides several different compilers for single byte matchers, sequences of byte matchers and finite state automata.

  * ByteMatcherCompiler
  * SequenceMatcherCompiler
  * RegexCompiler

All compilers implement the Compiler interface, which takes a String expression and produces an object of type T:

```
public interface Compiler<T> {

    /**
     * Compiles an expression into an object of type T.
     *
     * @param expression The expression to compile.
     * @return An compiled object of type T.
     * @throws ParseException if the expression could not be parsed.
     */
    public T compile(final String expression) throws ParseException;

}
```