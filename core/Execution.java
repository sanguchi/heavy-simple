package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Execution
{
	int line = 10;
	Statement[] stats;
	public Execution(Statement[] sts)
	{
		msg("RUNNING:");
		stats = sts;
		for(Statement st : stats)
		{
			execute(st);
		}
		msg("END OF EXECUTION");
	}
	
	public void print(String s)
	{
		System.out.println("->" + s);
	}
	public void input(Statement s)
	{
		if(!isRefs(s.expr.value.toString()))
			error("NO NAME PROVIDED.");
		Statement target = getReference(s);
		String val = Chip.console.nextLine();
		for(Statement st : stats)
			if(st.equals(target))
			{
				st.expr.value = "{"+val+"}";
				return;
			}
		error("INVALID REFERENCE FOR INPUT.");
	}
	public void exvoid()
	{}
	public void error(String e)
	{
		new Exception("[RUNTIME][ERROR]LINE " + line + ": " + e).printStackTrace();
		System.exit(0);
	}
	public void msg(String s)
	{
		System.out.println("[RUNTIME][MSG]:" + s);
	}
	public void execute(Statement st)
	{
				//System.out.println("Exec line " + st.line + 
				//", type = " + st.type.type + 
				//", name = " + st.name.value +
				//", expr = " + st.expr.value);
		Type type = st.type.type;
		//String name = (String) st.name.value;
		String expr = (String) st.expr.value;
		if(isRefs(expr))
		{
			//System.out.println("REFS DETECTED");
			solveReferences(st);
		}
		if(isBasicType(type))
		{
			//System.out.println("BASIC TYPE DETECTED");
			
		}
		if(isCallType(type))
		{
			call(st);
		}
		if(isAdvancedType(type))
		{}
				
		
	}
	public void call(Statement st)
	{
		String method = st.name.value.toString();
		String val = st.expr.value.toString();
		String pval = val.substring(1, val.length() - 1);
		if(method.equals("PRINT")){
			print(pval);return;}
		if(method.equals("INPUT")){
			input(st);return;}
		error("UNDEFINED " + st.type.type + " \"" + st.name.value + "\"");
	}
	public void solveReferences(Statement st)
	{
		String expr = st.expr.value.toString();
		//if statement doesn't contain any reference, then return.
		if(!expr.contains("$"))
			return;
	
		ArrayList<String> refs = new ArrayList<String>();
		int i = 0;
		while(i < expr.length())
		{
			char c = expr.charAt(i);
			if(c == '$')
			{
				i++;
				int ii = i;
				while(Compiler.isLetter(expr.charAt(ii)))
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
		if(isCallType(st.type.type))
			expected = Compiler.solveCallType(st);
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
						st = Compiler.replaceValues(st, sta);
				}
			}
		}
	}
	
	public Statement getReference(Statement st)
	{
		String s = (String) st.expr.value;
		String refs = "";
		int i = 0;
		while(i < s.length())
		{
			char c = s.charAt(i);
			if(c == '$')
			{
				i++;
				int ii = i;
				while(Compiler.isLetter(s.charAt(ii)))
					ii++;
				//msg("REFS FOUND : " + r);
				refs = s.substring(i, ii);
				break;
			}
			i++;
		}
		for(Statement sta : stats)
		{
			String name = sta.name.value.toString();
			if(name.equals(refs))
				return sta;
		}
		error("STATEMENT " + refs + "NOT FOUND.");
		return null;
		//System.exit(0);
		//for(Statement st : stats)
	}
	public boolean isCallType(Type t)
	{
		Type[] calls = {Type.RETURN, Type.RETURNX, Type.CALL, Type.CALLX};
		for(Type c : calls)
			if(c == t)
				return true;
		return false;
	}
	public boolean isBasicType(Type t)
	{
		Type[] basic = {Type.BOOLEAN, Type.INTEGER, Type.STRING};
		for(Type b : basic)
			if(b == t)
				return true;
		return false;
	}
	
	public boolean isAdvancedType(Type t)
	{
		Type[] advanced = {Type.ARRAY, Type.TABLE};
		for(Type b : advanced)
			if(b == t)
				return true;
		return false;
	}
	public boolean isRefs(String s)
	{
		if(s.contains("$"))
			return true;
		return false;
	}
	
}
