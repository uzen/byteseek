# Byteseek 1.x Syntax #

Byteseek has its own regular expression parser, with a byte-oriented syntax. The syntax is mostly the same as standard regular expressions. The fundamental atoms are bytes written as hexadecimal numbers, although you can also specify ASCII text.

Note that Byteseek 2.0 will differ slightly from the 1.x syntax.  In particular, sets become a way to group different expressions together, but do not have a special internal syntax of their own.  For example, any byte value can be inverted by directly prepending with the ^ symbol, without needing set brackets around it.  This syntax will be more fully documented at the time of release.


## General ##

  * Put comments on a line using the # symbol.  Everything after a # up to the end of the line is a comment and is ignored, unless they are inside a quoted piece of text.

  * All spaces, tabs, new lines and carriage returns are also ignored, unless they are inside a quoted piece of text.

## Bytes ##

Bytes are written as 2 digit hexadecimal numbers (any case allowed):
```
00 FF 1a dE
```

Any byte can be matched using the full stop:
```
.
```

## Text ##
Text (ASCII only) is delimited using single quotes:
```
'testing testing 123'
```

Case-insensitive text can be written delimited with back-ticks:
```
`HtMl public`
```

## Alternative sequences ##
Alternatives are written separated by a pipe character:
```
'this' | 'that' | 00 FF 1a
```

## Sets of bytes ##
Match more than one possible byte value with square brackets:
```
[09 0A 0D 20]          # whitespace - tab, newline, carriage return, space
['0'-'9']              # the digits 0 to 9
[09 0A 0D 20 '0'-'9']  # whitespace or digits 
```

Match anything but the specified bytes, using the ^ symbol which must appear immediately after the opening square bracket:
```
[^ 'a'-'z']            # anything except 'a' to 'z' 
[^ 09 0A 0d 20]        # any byte except whitespace
```

Sets of bytes can be written nested inside other sets.  Nothing stops you from just writing all the bytes out in one set, but it is legal to do so:
```
[09 0a ['a'-'z'] 0d 20]  # whitespace and 'a' to 'z'
```

## Shorthands ##
For convenience, some common groups of bytes have names:
```
ascii     # all ASCII chars                         [00-127]
print     # all printable chars including space     [' '-'~']
graph     # all visible chars (not including space) ['!'-'~']
word      # all characters, digits & underscore     ['0'-'9' 'a'-'z' 'A'-'Z' '_']
alnum     # all characters & digits                 ['0'-'9' 'a'-'z' 'A'-'Z']
alpha     # all alphabetic characters               ['a'-'z' 'A'-'Z']
upper     # upper case characters only              ['A'-'Z']
lower     # lower case characters only              ['a'-'z']
punct     # all punctuation                         ['!'-'/' ':'-'@' '['-'`' '{'-'~']
xdigit    # a hexadecimal digit                     ['0'-'9' 'a'-'f' 'A'-'F']
digit     # a digit                                 ['0'-'9']
ws        # space, tab newline & return             [' ' 09 0a 0d]
blank     # space & tab                             [' ' 09]
space     # space                                   ' '
tab       # tab                                     09
newline   # newline                                 0a
return    # carriage return                         0d
ctrl      # ASCII control characters                [00-1f 7f]
```

Standard regular-expression shorthands are also defined:
```
\t	# tab			09
\n	# newline		0a
\v	# vertical tab		0b
\f	# form feed		0c
\r	# carriage return	0d
\e	# escape		1b
\d	# digit                 [  '0'-'9']
\D	# not digit		[^ '0'-'9']
\w	# word character	[  'a'-'z' 'A'-'Z' '0'-'9' '_']
\W	# not word character	[^ 'a'-'z' 'A'-'Z' '0'-'9' '_']
\s	# white space		[  09 0a 0d 20]
\S	# not white space	[^ 09 0a 0d 20]
```

All of these shorthands can also be used inside or outside of square brackets:
```
01 02 \t 03 04 newline space \D 7f 80 [ascii 80 81 82]
```

However, you can't use the shorthands inside text strings - you must put them outside the quotes:
```
'looking for' \t 'this text after a tab' newline
```

## Bitmasks ##
Bitmasks match bytes based on whether all or some of the bits in the bitmask match a byte.  They can appear inside or outside sets.

All Bitmasks (all the specified bits must match) are written:
```
&7F     # match all these bits 01111111
&0F     # match all these bits 00001111
&81     # match all these bits 10000001
```

Any Bitmasks (any of the specified bits can match) are written:
```
~7F     # match any of these bits 01111111
~0F     # match any of these bits 00001111
~81     # match any of these bits 10000001
```

## Quantifiers ##
Quantifiers: specify how many of the preceding expression must match:
```
Optional  	'that'?             0-1
None or more  	[09 0a 0d 20]*      0-*
One or more   	[09 0a 0d 20]+      1-*
Exactly   	(fe ff){4}          n
Between   	ff{3-8}             n-m
At least  	ff{5-*}             n-*
```

## Grouping ##
Grouping: round brackets are used to group sub-expressions:
```
('NUM:' digit+ )? ('XYZ' | '123' | 01 02 03)
```