# heavy-simple
BASIC-Like Syntax, but java powered :P
#### ENGINE SYNTAX:
```xml
;this is a comment :P
<TYPE>	  <NAME>	=	  <EXPRESSION>
```

#### BASIC TYPES:
```
BOOL    NAME = BOOLEAN				    
INT     NAME = INTEGER
STR     NAME = STRING
```
#### GLOBALS FUNCS:
```
PRINT	  VOIDX	=	STRING|NAME
INPUT	  FUNC	=	NAME
```
#### HELLO WORLD:
```
CALLX   PRINT = HELLO WORLD!
STRING  NAME  = NO_NAME
CALLX   PRINT = PUT YOUR NAME
RETURN  INPUT = $NAME
CALLX   PRINT = HI!, $NAME
```
#### Interpreter session example:
```
$ java Interpreter 
INTERACTIVE MODE, COMMANDS = HELP, LIST, CLEAR, RUN, EXIT
string age = 18
;now we define a default name
string name = default_name
callx print = Hi! please tell me your name c:
return input = name
callx print = Oh, so you are $name
callx print = How old you are?
return input = age
callx print = $age ?, Great!  
run
->HI! PLEASE TELL ME YOUR NAME C:
TERMINATOR
->OH, SO YOU ARE TERMINATOR
->HOW OLD YOU ARE?
99
->99 ?, GREAT!
list
->LISTING CODE:
10 STRING AGE = 18
20 STRING NAME = DEFAULT_NAME
30 CALLX PRINT = HI! PLEASE TELL ME YOUR NAME C:
40 RETURN INPUT = NAME
50 CALLX PRINT = OH, SO YOU ARE $NAME
60 CALLX PRINT = HOW OLD YOU ARE?
70 RETURN INPUT = AGE
80 CALLX PRINT = $AGE ?, GREAT!
->END OF LIST
clear
->CODE CACHE, VARS LIST, AND LINE HISTORY DELETED.
list
->CODE CACHE EMPTY!.
run 
->NOTHING TO RUN IN CACHE, PLEASE TYPE SOMETHING... INTERESTING.
exit
->BYE...

```
#### Using with files:
```
Not fully implemented yet.
```
