A Java library implementing various methods of matching and searching for bytes.

See the [road map](RoadMap.md) to learn where byteseek is going, and the [release history](ReleaseHistory.md) to see where it's been. Ideas for algorithms to be implemented, feedback on the design or implementation, and any other suggestions are welcome.  You can contact me using a gmail dot com address with the name mattpalms, or add an issue if you have a problem.

The next release will now be byteseek 2.0, which is sufficiently different - and hopefully better - that it will exist in a new namespace: net.byteseek.

### Update 9 October 2014 ###
More testing remaining, and progress slower than I'd hoped, but the code is getting there.  The project repository will now be hosted on GitHub at: https://github.com/nishihatapalmer/byteseek.

### Update 27 January  2014 ###

I hope to get the 2.0 code in a releasable state sometime in 2014, hopefully before the summer.  I will now prioritise only functionality that existed in earlier versions, in order to get to an actual release, although I am polishing some of the existing functionality along the way.  Javadoc is now about 95% complete, and unit tests are about 40% complete of the entire code base.  Following the initial release of 2.0, I'll add tests for the newer features and make further releases as necessary.

I'll post updates on progress here as I pass significant milestones up to the release.

## Byteseek 2.0 Features ##

Byteseek 2.0 contains full implementations of its interfaces, so reading from files, byte arrays, strings or input streams is easy.  Caching strategies can be plugged into any of the readers giving both flexible and efficient I/O.  The readers themselves may be of independent interest outside of any matching or searching provided by byteseek.

The parser has been rewritten as a custom recursive-descent regular expression parser, removing the runtime dependency on ANLTR.  The compilers have been simplified, and the finite state automata classes are completely usable.  [Trie](http://en.wikipedia.org/wiki/Trie) structures can be constructed using them for multi sequence matching and searching. Non-deterministic Glushkov automata can be constructed directly from a regular expression parse tree, using the [Champarnaud](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.50.5883) method.  Deterministic automata can be produced from any non-deterministic automata by the usual [subset](http://en.wikipedia.org/wiki/Powerset_construction) construction.

Fast, sub-linear search algorithms include [Boyer-Moore-Horspool](http://en.wikipedia.org/wiki/Boyer%E2%80%93Moore%E2%80%93Horspool_algorithm) and Sunday for sequence searching and Set Horspool (an obvious adaptation of Horspool to match multiple sequences) and Wu-Manber for multi-sequence searching. Naive searchers that just test for a match in each position are also provided, although these are not usually the fastest choice for searching.  They can be faster for short, one-off searches, since they do not have to calculate anything at all before searching can begin.

It should be mentioned that all of the search algorithms have been modified to work with sets of bytes in any position in the search string - not just fixed sequences of bytes.  This immediately makes them more powerful than traditional implementations of these algorithms.  However, it should be noted that using large sets of bytes can adversely affect performance - generally if they appear towards the end of the search expression.  If you do not use sets of bytes, there is no impact at all on the performance, as the performance impact of byte sets is entirely due to how the algorithms fundamentally work - not on the implementation.

Any different kind of matcher can be plugged into the search algorithms, giving a lot of flexibility in constructing fast searches.  For example, using a naive multi-sequence searcher with a Trie-based matcher may outperform Wu-Manber and Set Horspool for searches with at least one very short sequence to match (which strongly limits their performance).


## Byteseek 1.x ##

### Matching ###
There are classes for [matching](Matcher.md) individual bytes, sequences, multiple sequences, and regular expressions.

  * Bytes can be matched on any byte, sets of bytes, case sensitive and insensitive ASCII text, and bit-masks.
  * Sequences of any combination of byte matchers can be matched, and can include fixed gaps in the sequence.
  * Multi-sequence matchers are in development, using a [Trie](http://en.wikipedia.org/wiki/Trie) data structure.
  * Finite-state automata (both deterministic and non-deterministic) can be produced for full regular expression matching.

### Compiling ###
All of these can be [compiled](Compiling.md) using a [regular-expression syntax](Syntax.md).  The regular expression parser is generated using an [ANTLR](http://www.antlr.org) grammar. There is a runtime dependency on the ANTLR 3.2 runtime java library if any byteseek compilers are used.

### Searching ###
Any sequence or sequences of byte matchers can be [searched](Searcher.md) for using several different search algorithms. Currently implemented are:
  * MatcherSearcher - tries every position in turn until it finds a match.  This will work with any type of matcher: single byte, sequence, multi-sequence or automata-based matchers.
  * [BoyerMooreHorspoolSearcher](http://en.wikipedia.org/wiki/Boyer-Moore-Horspool_algorithm) - a fast, sub-linear, search which skips over bytes which can't possibly match the search sequence.

Other search algorithms are in development, including multi-pattern matching using [Wu-Manber](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.108.7791&rank=4), the Sunday quick searcher and a modification of the Horspool algorithm to work with multi-sequences.

The byteseek project is using the excellent [Yourkit profiler](http://www.yourkit.com/) to locate performance hot spots and optimise the algorithm implementations.