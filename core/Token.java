package core;


public class Token
{
	public Type type;
	public Object value;
	public Token(Type t, Object v)
	{
		type = t;
		value = v;
	}
	public String toString()
	{
		return "TOKEN TYPE{" + type + "} - VALUE{" + value + "}";
	}
}
