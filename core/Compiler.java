package core;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class Compiler
{
	static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static String numbers = "0123456789";
	int line = 10;
	int tcount;
	ArrayList<Statement> stats = new ArrayList<Statement>();
	public Compiler(ArrayList<String> data)
	{
		for(String s : data){
			Statement st = parse(s);
			st.line = this.line;
			line += 10;
			//st = solveReferences(st);
			stats.add(st);
		}
		print(stats.size() + " LINES PROCESSED.");
		//print("RESULT:");
		//for(Statement st : stats)
		//{
			//System.out.println(st.line + " " + st.type.type + " " + st.name.value + " = " + st.expr.value);
		//}
		print("SWITCHING TO EXECUTION MODE:");
		System.out.println();
		Statement[] a = new Statement[stats.size()];
		new Execution(stats.toArray(a));
	}
	
	public void error(String e)
	{
		new Exception("[COMPILER][ERROR]: LINE " + line + ": " + e).printStackTrace();
		System.exit(0);
	}
	public void print(String s){System.out.println("[COMPILER][MSG]:" + s);}
	public Statement parse(String s)
	{
		//line syntax =  <TYPE> <NAME> = {EXPR}
		StringBuilder build = new StringBuilder();
		Token type = null;
		Token name = null;
		Token expr = null;
		int i = 0;
		//check if the line starts with a letter, we expect a Type declaration, made of letters :P
		if(!isLetter(s.charAt(0)))
			error("LETTER EXPECTED, NOT " + s.charAt(0));	
		//here we obtain the type token.
		while(type == null)
		{
			char c = s.charAt(i); 			//current character to analyze.
			if(isLetter(c))
			{
				build.append(c);
				i++;
			}
			else
			{
				String t = build.toString();
				if(isType(t))
				{
					Type ty = getType(t);
					type = new Token(ty, null);
					
				}
				else
					error(t + " IS NOT A TYPE.");
				break;
			}
		}
		//print("SOLVED = " + type.toString());
		//re-init stringbuilder.
		build = new StringBuilder();
		
		//ignore all whitespace chars.
		while(isSpace(s.charAt(i)))
			i++;
		//if name is not present, but the index reached the " = " sign, throw an Exception.
		if(s.charAt(i) == '=')
			error("NAME NOT FOUND, WHAT THE HECK ARE YOU TIPYING???");
		//Name can't start with a number.
		if(!isLetter(s.charAt(i)))
			error("LETTER EXPECTED, NOT " + s.charAt(0));
		//now we must obtain the name token.
		while(name == null)
		{
			char c = s.charAt(i);
			if(isLetter(c))
			{
				build.append(c);
				i++;
			}
			//Names can't use numbers.
			if(isNumber(c))
				error("NAMES CAN'T CONTAIN NUMBERS, DON'T USE > " + s.charAt(0));
			//here we reached the edge of the name.
			if(isSpace(c))
			{
				String t = build.toString();
				if(t.length() > 8)
					error("NAME LENGTH CAN'T BE GREATER THAN 8 CHARS");
				if(t.length() == 8)
					print("name token hit the length limit, please use short names.");
				name = new Token(Type.NAME, t);
				break;
			}
		}
		//print("SOLVED = " + name.toString());
		//re-init stringbuilder.
		build = new StringBuilder();
		
		//ignore all whitespace chars.
		while(isSpace(s.charAt(i)))
			i++;
		
		//here we expect a " = " character.
		if(s.charAt(i) != '=')
			error("MISSING \" = \" SIMBOL");
		
		//advance to next char, idk why, but without this, the compiler crashes.
		i++;
		
		//ignore all whitespace chars.
		while(isSpace(s.charAt(i))){
			i++;
			//print("CURRENT CHAR = [" + s.charAt(i) + "]");
		}
		
		// " { " expected.
		if(s.charAt(i) != '{')
			error("EXPRESSION MUST START WITH \" { \".");
		
		// we expect a " } " at the end.
		if(!s.endsWith("}"))
			error("EXPRESSION MUST END WITH \" } \". ");
		String e = s.substring(i);
		//print(" E = " + e);
		expr = new Token(Type.EXPR, e);
		return new Statement(type, name ,expr);
	}
	
	//Here we solve any reference at expr token. {$NAME}
	public Statement solveReferences(Statement st)
	{
		String expr = st.expr.value.toString();
		//if statement doesn't contain any reference, then return.
		if(!expr.contains("$"))
			return st;
		
		ArrayList<String> refs = new ArrayList<String>();
		int i = 0;
		while(i < expr.length())
		{
			char c = expr.charAt(i);
			if(c == '$')
			{
				i++;
				int ii = i;
				while(isLetter(expr.charAt(ii)))
					ii++;
				String r = expr.substring(i, ii);
				refs.add(r);
				i = ii;
			}
			i++;
		}
		//Type we refs with $NAME
		Type referenced;
		//Expected type by Statement.Type.
		Type expected;
		if(isCallType(st.type.type)){
			expected = solveCallType(st);
			if(expected == Type.UNDEFINED)
				error("CAN'T SOLVE EXPECTED TYPE FOR " + st.name.value.toString());
		}
		else
			expected = st.type.type;
		for(String r : refs)
		{
			for(Statement sta : stats)
			{
				String na = sta.name.value.toString();
				if(na.equals(r))
				{
					referenced = sta.type.type;
					if(referenced == expected)
						st = replaceValues(st, sta);
				}
			}
		}
		return st;
	}
	public static boolean isSpace(char c)
	{
		return(Character.isWhitespace(c));
	}
	public static boolean isLetter(char c)
	{
		for(int i = 0; i < letters.length(); i++)
			if(letters.charAt(i) == c)
				return true;
		return false;
	}
	public static boolean isNumber(char c)
	{
		for(int i = 0; i < numbers.length(); i++)
			if(numbers.charAt(i) == c)
				return true;
		return false;
	}
	public boolean isType(String s)
	{
		for(Type t : Type.values())
			if(t.toString().equals(s))
				return true;
		return false;
	}
	public Type getType(String s)
	{
		for(Type t : Type.values())
			if(t.toString().equals(s))
				return t;
		error("UNDEFINED TYPE DECLARATOR.");
		return Type.UNDEFINED;
	}
	public boolean isCallType(Type t)
	{
		Type[] calls = {Type.RETURN, Type.RETURNX, Type.CALL, Type.CALLX};
		for(Type c : calls)
			if(c == t)
				return true;
		return false;
	}
	public static Type solveCallType(Statement st)
	{
		//String ty = st.type.value.toString();
		String na = st.name.value.toString();
		String[] names = {"PRINT", "INPUT", "PUT", "GET"};
		Type[] needs ={Type.STRING, Type.NAME, Type.ARRAY, Type.INTEGER};
		for(int i = 0; i < names.length; i++)
		{
			if(names[i].equals(na))
				return needs[i];
		}
		return Type.UNDEFINED;
	}
	public static Statement replaceValues(Statement toReplace, Statement refs)
	{
		Token ex = toReplace.expr;
		String result = ex.value.toString();
		String regex = "$"+refs.name.value.toString();
		regex = Matcher.quoteReplacement(regex);
		String val = refs.expr.value.toString().substring(1, refs.expr.value.toString().length()-1);
		//print("REPLACING \""+regex+"\" WITH " + val + " - RESULT = " + result.replaceAll(regex, val));
		result = result.replaceAll(regex, val);
		Statement res = toReplace;
		res.expr = new Token(Type.EXPR,result);
		return res;
	}
}
