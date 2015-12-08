## Direction ##

At present, the main aim is to arrive at a stable, tested set of interfaces and implementations for byte matching and searching, covering single bytes, sequences of byte matchers, multi byte-matcher-sequences and finite-state automata. Once that has been achieved, there are two further goals:
  * a regular expression like matching and searching object which automatically selects the most efficient underlying algorithms to use.
  * a fast form of regular expression matching, transforming `.*` expressions into sub-linear searches for necessary factors of the remaining part of the expression.  The idea is both to speed up the searches and to save memory, since `.*` expressions cause exponential growth in deterministic finite state automata.

## Current status ##
  * Matching and searching for single bytes and sequences of bytes is stable.
  * Parsing the regular-expression syntax is stable and has been rewritten as a customer parser, removing the dependency on ANTLR.
  * Compiling the syntax for bytes, sequences and automata is stable.
  * Cached readers from byte arrays, strings, streams and files is stable.
  * Multi-sequence matching and searching are stable.
  * Sequence and multi sequence searcher variants are stable.

## Immediate goals ##
  * JUnit tests for all public classes.
  * Automata matching and searching needs a bit more work.

## Things I'd like to do ##
  * Possibly implement parsers for other regular expression syntaxes, such as a  Java regex parser.
  * Aho Corasick multi sequence search algorithm.
  * Empty transitions for automata
  * Explore automata which transition on sequences of bytes.
  * Explore automata with additional control structures - e.g. a stack
  * Explore fast search for regular expression automata by using necessary sequence factors that can be located using a sub-linear search like Horspool.