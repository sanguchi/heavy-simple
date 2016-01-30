# heavy-simple
BASIC-Like Syntax, but java powered :P
# ENGINE SYNTAX:
<TYPE>	  <NAME>	=	  {EXPRESSION}

# BASIC TYPES:
BOOL	  NAME	=	{BOOLEAN}					    //	BOOL	DEAD	=	{FALSE}
INT	  	NAME	=	{INTEGER}				    	//	INT		SCORE	=	{1}
STR		  NAME	=	{STRING}				    	//	STRING	LEVEL	=	{TUTORIAL}

# ADVANCE TYPES:(not implemented)
ARRAY	  NAME	=	{TYPE}					  	  //	ARRAY 	ARR		=	{INTEGER} -> PUT
TABLE	  NAME	=	{TYPE, TYPE} 			  	//	TABLE	NAMELIST=	{STRING, INTEGER}

# PSEUDO TYPES:(not implemented)
RETURN	FUNC	=	{NAME}						    //	RETURN	GETEXP	=	{$EXP}
RETURNX	FUNCX	=	{NAME, VALUE...}			//	RETURNX	DOUBLE	=	{$SCORE, $SCORE}
CALL	  VOID									        //	CALL	LEFT
CALLX	  VOIDX	=	{TYPE, VALUE...}			//	CALLX	SETSCORE=	{INTEGER, 99}

# GLOBALS FUNCS:
PRINT	  VOIDX	=	{STRING|NAME}				  //	CALLX	PRINT	=	{HELLO WORLD}|{YOUR SCORE IS $SCORE}
INPUT	  FUNC	=	{NAME}						    // 	RETURN	INPUT	=	{$PLAYERNAME}

# HELLO WORLD:
CALLX   PRINT = {HELLO WORLD!}
STRING  NAME  = {NO NAME}
CALLX   PRINT = {PUT YOUR NAME}
RETURN  INPUT = {$NAME}
CALLX   PRINT = {HI!, $NAME}
