package core;

public class Interpreter
{
	String text;
	int pos = 0;
	Token current;
	char cur;
	public Interpreter(String s){
		text = s;// client string input, e.g. "3+5"
	}
	public static boolean isNumber(char c)
	{
		char[] numbers = {'0','1','2','3','4','5','6','7','8','9'};
		for(char d : numbers)
			if(c == d)
				return true;
		return false;
	}
	public void error(){new Exception("Error parsing input");}
	
	/* Lexical analyzer (also known as scanner or tokenizer)
    This method is responsible for breaking a sentence
    apart into tokens. One token at a time.
	*/
	public Token nextToken()
	{
		//is self.pos index past the end of the self.text ?
		//if so, then return EOF token because there is no more
		//input left to convert into tokens
		if(pos >= text.length())
			return new Token(Type.EOF, null);
		cur = text.charAt(pos);
		while(Character.isWhitespace(text.charAt(pos)))
            advance();
		//if the character is a digit then convert it to
        //integer, create an INTEGER token, increment self.pos
        //index to point to the next character after the digit,
        //and return the INTEGER token
        if(isNumber(cur)){
            Token t = new Token(Type.INTEGER, getInt());
            return t;
        }
        if(cur == '+'){
            Token t = new Token(Type.PLUS, text.charAt(pos));
            advance();
            return t;
        }
        if(cur == '-'){
            Token t = new Token(Type.MINUS, text.charAt(pos));
            advance();
            return t;
        }
        if(cur == '*')
        {
        	Token t = new Token(Type.MULT, text.charAt(pos));
        	advance();
        	return t;
        }
        if(cur == '/')
        {
        	Token t = new Token(Type.DIVS, text.charAt(pos));
        	advance();
        	return t;
        }
        error();
		return null;
	}
	//compare the current token type with the passed token
    //type and if they match then "eat" the current token
    //and assign the next token to the self.current_token,
    //otherwise raise an exception.
	public void eat(Type type){
		if(current.type == type)
			current = nextToken();
	    else
	    	error();
	}
	
	public void advance()
	{
		//Advance the 'pos' pointer and set the 'current_char' variable."""
		pos++;
		if(pos >= text.length())
			current = new Token(Type.EOF, null);
		else
			cur = text.charAt(pos);
	}
	
	public int getInt(){
		//Return a (multidigit) integer consumed from the input."""
        StringBuilder result = new StringBuilder();
        while(pos < text.length() && isNumber(cur))
        {
            //System.out.println("Appending " + cur);
        	result.append(cur);
            advance();
        }
        return Integer.parseInt(result.toString());
	}
	
	
	public Object expr(){
        //expr -> INTEGER PLUS INTEGER
        //set current token to the first token taken from the input
        current = nextToken();
        //we expect the current token to be a single-digit integer
        Token left = current;
        eat(Type.INTEGER);
        //we expect the current token to be a '+' token
        Token op = current;
        if(op.type == Type.PLUS)
        	eat(Type.PLUS);
        if(op.type == Type.MINUS)
        	eat(Type.MINUS);
        if(op.type == Type.MULT)
        	eat(Type.MULT);
        if(op.type == Type.DIVS)
        	eat(Type.DIVS);
        //we expect the current token to be a single-digit integer
        Token right = current;
        eat(Type.INTEGER);
        //after the above call the self.current_token is set to EOF token
        //at this point INTEGER PLUS INTEGER sequence of tokens
        //has been successfully found and the method can just
        //return the result of adding two integers, thus
        //effectively interpreting client input
        int result = 0;
        if(op.type == Type.PLUS)
        	result = (int)left.value + (int)right.value;
        if(op.type == Type.MINUS)
        	result = (int)left.value - (int)right.value;
        if(op.type == Type.MULT)
        	result = (int)left.value * (int)right.value;
        if(op.type == Type.DIVS)
        	result = (int)left.value / (int)right.value;
        return result;
	}
	
}