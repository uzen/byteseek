## Latest code ##

The most up to date code can be found in the [mercurial repository](http://code.google.com/p/byteseek/source/browse/), although this code may not be stable.

## Version 1.2 ##

Note: this release introduces a few compatibility breaking changes. Mostly these changes are fairly minor: some classes have been renamed and the packages reorganised, but the interfaces are the same. However, the automata classes have been substantially changed (and should now be regarded as approaching beta code).

  * REFACTOR: some packages and classes have been renamed and moved for better consistency.
  * NEW: Deterministic Finite State (DFA) compiler for efficient regular expression matching.
  * REFACTOR: automata classes simplified.
  * NEW: fixed gaps in byte sequences, e.g.:
```
'A gap of seven' .{7} 'bytes between this text'
```
  * NEW: fixed repeats in byte sequences, e.g.:
```
'repeatme'{5}
```
  * NEW: repeated expressions in byte sequences, e.g.:
```
('repeat' 01 02 03){3}
```
  * FIX: Any-bitmask matcher did not match byte values over 128 (signed byte issue).
  * Javadoc on all public (and some private) methods.
  * JUnit tests for byte matchers.

## Version 1.1 ##

  * FIX: signed bytes not converted properly to unsigned values in byte sets.

## Version 1.0 ##

  * Initial release of byteseek.