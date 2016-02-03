import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;

public class Interpreter
{
	public static Scanner console = null;
	ArrayList<Int> ints = new ArrayList<Int>();
	ArrayList<Str> strs = new ArrayList<Str>();
	ArrayList<Bool> bools = new ArrayList<Bool>();
	
	ArrayList<String> lines = new ArrayList<String>();
	ExecutionHolder execution = new ExecutionHolder();
	int lnumber;
	boolean abortOnError = true;
	public Interpreter(){
	}
	public static void main (String[] args)
	{
		
		Interpreter interpreter = new Interpreter();
		if(args.length != 0){
			try{console = new Scanner(new File(args[0]));}catch(FileNotFoundException e){e.printStackTrace();}
			ArrayList<String> lines = new ArrayList<String>();
			for(String line = "";console.hasNextLine();lines.add(line))
				line = console.nextLine();
			for(String s : lines)
				interpreter.parse(s);
			interpreter.execution.execute();
		}
		console = new Scanner(System.in);
		System.out.println("INTERACTIVE MODE ON, COMMANDS = HELP, LIST, CLEAR, RUN, EXIT");
		for(String line = "";console.hasNextLine();interpreter.parse(line))
			line = console.nextLine();
		console.close();
	}
	public void error(String s){new Exception("[INTERPRETER][ERROR]: "+ s).printStackTrace();System.exit(0);}
	public void print(String s){System.out.println("[INTERPRETER][MSG]: "+ s);}
	public void parse(String line)
	{
		//ignore empty lines, AKA press enter because fun.
		if(line.length() == 0)
			return;
		if(line.startsWith(";"))
			return;
		line = line.toUpperCase();
		
		if(Utils.isCommand(line)){
			executeCommand(line);
			return;
		}
		//line syntax =  <TYPE> <NAME> = {EXPR}
		StringBuilder build = new StringBuilder();
		String name = "";
		String type = "";
		int i = 0;
		//check if the line starts with a letter, we expect a Type declaration, made of letters :P
		if(!Utils.isLetter(line.charAt(i)))
			error("LETTER EXPECTED, NOT " + line.charAt(i));	
		
		char c = line.charAt(i); //current character to analyze.
		
		while(i < line.length() && Utils.isLetter(c))
		{
			//build.append(c);
			c = line.charAt(i); 	
			i++;		
		}
		//space char expected before the type declaration.
		if(!Utils.isSpace(c) && i < line.length())
			error("INVALID CHARACTER \" " + c + "\" ");
		
		//here we obtain the type.
		//String t = build.toString();
		String t = line.substring(0, i-1);
		if(Utils.isValidType(t))
		{
			type = t;
		}
		else
			error(t + " IS NOT A VALID TYPE.");
		
		//re-init stringbuilder.
		build = new StringBuilder();	
		
		//ignore all whitespace chars.
		while(i < line.length() && Utils.isSpace(line.charAt(i)))
			i++;
		
		//if name is not present, but the index reached the " = " sign, throw an Exception.
		if(line.charAt(i) == '=')
			error("NAME NOT FOUND, WHAT THE HECK ARE YOU TIPYING???");
		
		//Names MUST start with a letter.
		if(!Utils.isLetter(line.charAt(i)))
			error("LETTER EXPECTED, NOT " + line.charAt(i));
		
		//update the c char.
		c = line.charAt(i);
		
		//now we must obtain the name token.
		while(i < line.length() && Utils.isLetter(c))
		{
			build.append(c);
			i++;
			c = line.charAt(i); 			
		}
		//here we have reached the edge of the name, this MUST be followed by a space char.
		if(i < line.length() && Utils.isSpace(c))
		{
			String n = build.toString();
			if(t.length() > 8)
				error("NAME LENGTH CAN'T BE GREATER THAN 8 CHARS");
			if(t.length() == 8)
				System.out.println("name token hit the length limit, please use short names.");
			name = n;
		}
		else
			error("ERROR IN CHARACTER \" " + c + " \", SPACE EXPECTED.");
		
		//re-init stringbuilder.
		build = new StringBuilder();
		
		//ignore all whitespace chars.
		while(i < line.length() && Utils.isSpace(line.charAt(i)))
			i++;
		
		//here we expect a " = " character.
		if(line.charAt(i) != '=')
			error("MISSING \" = \" SIMBOL");
		
		//advance to next char, idk why, but without this, the compiler crashes.
		i++;
		
		//ignore all whitespace chars.
		while(i < line.length() && Utils.isSpace(line.charAt(i)))
			i++;
		
		//get expr text.
		String e = line.substring(i);
		//solve any reference to other vars, only for basic types.
		if(Utils.containRefs(e) && Utils.isBasicType(type))
			e = solveRefs(e);
		e = e.trim();
		if(type.equals("INTEGER"))
			ints.add(new Int(name, solveMath(e)));
		if(type.equals("STRING"))
			strs.add(new Str(name,e));
		if(type.equals("BOOLEAN"))
			bools.add(new Bool(name,Boolean.parseBoolean(e)));
		if(Utils.isFuncType(type))
		{}
		if(Utils.isCallType(type))
		{
			Executable ex = null;
			if(type.equals("CALLX"))	
				ex = solveCallx(name, e);
			if(type.equals("RETURN"))
				ex = solveReturn(name, e);
			execution.executionList.add(ex);
		}
		lnumber += 10;
		lines.add(line);
	}
	public String solveRefs(String s)
	{
		ArrayList<String> refs = new ArrayList<String>();
		int i = 0;
		String solved = s;
		//get all reference names.
		while(i < s.length())
		{
			char c = s.charAt(i);
			if(c == '$')
			{
				i++;
				int ii = i;
				while(ii < s.length() && Utils.isLetter(s.charAt(ii)))
					ii++;
				String r = s.substring(i, ii);
				refs.add(r);
				i = ii;
			}
			i++;
		}
		for(String ref : refs)
		{
			for(Int in : ints)
				if(in.name.equals(ref))
					solved = replace(s, in.name,""+in.value);
			for(Str st : strs)
				if(st.name.equals(ref))
					solved = replace(s, st.name, st.value);
			for(Bool bo : bools)
				if(bo.name.equals(ref))
					solved = replace(s, bo.name, ""+bo.value);
		}
		if(Utils.containRefs(solved))
			error("ONE OR MORE REFERENCES CANNOT BE SOLVED.");
		return solved;
	}
	
	public String replace(String line, String reg, String repl)
	{
		String result = "";
		String regex = "$"+reg;
		regex = Matcher.quoteReplacement(regex);
		result = line.replaceAll(regex, repl);
		//System.out.println("Line = " + line + " Regex = " + reg + " Result = " + result);
		return result;
	}
	public int solveMath(String s)
	{
		StringBuilder sb = new StringBuilder();
		int result = 0;
		int i = 0;
		if(!Utils.isNumber(s.charAt(i)))
			error("CANT SOLVE \"" + s + "\", " + s.charAt(i) + " IS NOT A NUMBER.");
		while(i < s.length() && Utils.isNumber(s.charAt(i)))
		{
			sb.append(s.charAt(i));
			i++;
		}
		result = Integer.parseInt(sb.toString());
		sb = new StringBuilder();
		if(s.length() == i)
			return result;
		while(i < s.length() && Utils.isSpace(s.charAt(i)))
			i++;
		if(!Utils.isOperator(s.charAt(i)))
			error("OPERATOR [ + | - | * | / ] EXPECTED AFTER FIRST NUMBER.");
		while(i < s.length() && Utils.isOperator(s.charAt(i)))
		{
			char op = s.charAt(i);
			i++;
			while(Utils.isSpace(s.charAt(i)))
				i++;
			if(!Utils.isNumber(s.charAt(i)))
				error("NUMBER EXPECTED AFTER " + op + " OPERATOR, NOT \" " + s.charAt(i) + " \"");
			while(i < s.length() && Utils.isNumber(s.charAt(i)))
			{
				sb.append(s.charAt(i));
				i++;
			}
			int left = Integer.parseInt(sb.toString());
			sb = new StringBuilder();
			switch(op)
			{
				case '+':
					result = result + left;break;
				case '-':
					result = result - left;break;
				case '*':
					result = result * left;break;
				case '/':
					result = result / left;break;
			}
			while(i < s.length() && Utils.isSpace(s.charAt(i)))
				i++;
		}
		return result;
	}
	public Executable solveCallx(String name, String body)
	{
		if(name.equals("PRINT"))
			return new Print(body);
		error("CAN'T SOLVE CALLX " + name + ", BODY: " + body);
		return null;
	}
	public Executable solveReturn(String name, String body)
	{
		if(name.equals("INPUT"))
			return new Input(body);
		error("CAN'T SOLVE RETURN " + name + ", BODY: " + body);
		return null;
	}
	public void executeCommand(String s)
	{
		if(s.equals("EXIT")){new Print("BYE...").execute();System.exit(0);}
		if(s.equals("LIST"))
			dropLines();
		if(s.equals("CLEAR"))
		{
			ints = new ArrayList<Int>();
			strs = new ArrayList<Str>();
			bools = new ArrayList<Bool>();
			execution.clear();
			lines = new ArrayList<String>();
			new Print("CODE CACHE, VARS LIST, AND LINE HISTORY DELETED.").execute();
		}
		if(s.equals("HELP"))
		{
			new Print("SYNTAX:[TYPE] [NAME] = {BODY}").execute();
			new Print("BASIC TYPES: [BOOLEAN - INTEGER - STRING]").execute();
			new Print("BOOL		DEAD	=	FALSE").execute();
			new Print("INT		SCORE	=	1").execute();
			new Print("STRING 	NAME	=	GUEST").execute();
				parse("STRING	NAME	=	GUEST");
			new Print("GLOBALS FUNCS: [PRINT - INPUT]").execute();
			new Print("CALLX 	PRINT	=	HELLO WORLD!").execute();
			new Print("RETURN 	INPUT	=	NAME").execute();
			new Print("CALLX 	PRINT	=	HI, $NAME , NICE TO MEET YOU!").execute();
			new Print("NOTICE ABOUT \"HI GUEST, NICE TO MEET YOU!\"").execute();
			System.out.println("The original text is:");
			System.out.println("CALLX 	PRINT 	= 	HI, $NAME , NICE TO MEET YOU!");
			System.out.println("But this help text actually uses the PRINT function.");
			System.out.println("PRINT, first solves any reference to STRING and then");
			System.out.println("prints his contents, so the trick is...");
			System.out.println("I have declared a STRING after that PRINT.");
			System.out.println("So you can check it with LIST now :D");
			new Print("ABOUT:").execute();
			new Print("HEAVY - SIMPLE. BASIC-LIKE Syntax, but Java Powered :P").execute();
			new Print("https://github.com/sanguchi/heavy-simple").execute();
		}
		if(s.equals("RUN"))
		{
			execution.execute();
		}
	}
	public void dropLines()
	{
		if(lines.size() == 0){new Print("CODE CACHE EMPTY!.").execute();return;}
		new Print("LISTING CODE:").execute();;
		int i = 10;
		for(String l : lines)
		{
			System.out.println(i + " " + l);
			i += 10;
		}
		new Print("END OF LIST").execute();;
	}
	class Input extends Return
	{
		public Input(String refs)
		{
			super(refs, null);
			// TODO Auto-generated constructor stub
		}
		public void execute()
		{
			for(Str st : strs)
				if(st.name.equals(name)){
					st.value = console.nextLine();
					return;}
			error("CAN'T FIND STRING " + name + ", REFS FAILED IN RETURN INPUT.");
		}
	}
	class Voidx extends Executable
	{
		public Voidx(String n,String b)
		{
			name = n;
			body = b;
		}
		public void execute()
		{}
	}
	class Print extends Voidx
	{
		public Print(String text)
		{
			super(null, text);
		}
		public void execute()
		{
			if(Utils.containRefs(body))
				body = solveRefs(body);
			System.out.println("->"+body);
		}
	}
	class Return extends Executable
	{
		public Return(String n, String b)
		{
			name = n;
			body = b;
			id = lnumber;
		}
		public Return()
		{
			id = lnumber;
		}
		public void execute(String n)
		{}
	}
	class ExecutionHolder
	{
		public ArrayList<Executable> executionList = new ArrayList<Executable>();
		public ExecutionHolder()
		{
			
		}
		public void execute()
		{
			if(executionList.size() == 0)
				new Print("NOTHING TO RUN IN CACHE, PLEASE TYPE SOMETHING... INTERESTING.").execute();
			for(Executable ex : executionList)
				ex.execute();
		}
		public void clear(){executionList = new ArrayList<Executable>();}
	}
	class Executable
	{
		int id;
		String name;
		String body;
		public Executable()
		{
			id = lnumber;
		}
		public void execute()
		{}
	}
}

class Int
{
	public String name;
	public int value;
	public Int(String n, int v)
	{
		name = n;
		value = v;
	}
}
class Str
{
	public String name;
	public String value;
	public Str(String n, String v)
	{
		name = n;
		value = v;
	}
}
class Bool
{
	public String name;
	public boolean value;
	public Bool(String n, boolean v)
	{
		name = n;
		value = v;
	}
}


class Utils
{
	public static boolean isSpace(char c)
	{
		return(Character.isWhitespace(c));
	}
	public static boolean isLetter(char c)
	{
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for(int i = 0; i < letters.length(); i++)
			if(letters.charAt(i) == c)
				return true;
		return false;
	}
	public static boolean isNumber(char c)
	{
		String numbers = "0123456789";
		for(int i = 0; i < numbers.length(); i++)
			if(numbers.charAt(i) == c)
				return true;
		return false;
	}
	public static boolean isOperator(char c)
	{
		String operators = "+-*/";
		for(int i = 0; i < operators.length(); i++)
			if(operators.charAt(i) == c)
				return true;
		return false;
	}
	public static boolean isCommand(String s)
	{
		String[] commands = {"RUN", "LIST", "EXIT", "CLEAR", "HELP"};
		for(String c : commands)
			if(c.equals(s.toUpperCase()))
				return true;
		return false;
	}
	public static boolean isValidType(String s)
	{
		String[] types = {"BOOL", "INT", "STRING", "CALL", "CALLX", "RETURN", "RETURNX"};
		for(String t : types)
			if(t.equals(s.toUpperCase()))
				return true;
		return false;
	}
	public static boolean isBasicType(String s)
	{
		String[] basic = {"BOOLEAN", "INTEGER", "STRING"};
		for(String b : basic)
			if(b.equals(s))
				return true;
		return false;
	}
	public static boolean isCallType(String s)
	{
		String[] calls = {"CALL", "CALLX", "RETURN", "RETURNX"};
		for(String c : calls)
			if(c.equals(s.toUpperCase()))
				return true;
		return false;
	}
	public static boolean isFuncType(String s)
	{
		String[] funcs = {"FUNC", "FUNCX", "VOID", "VOIDX"};
		for(String f : funcs)
			if(f.equals(s.toUpperCase()))
				return true;
		return false;
	}
	public static boolean containRefs(String s)
	{
		if(s.contains("$"))
			return true;
		return false;
	}
}
