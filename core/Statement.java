package core;

public class Statement
{
	public Token type;
	public Token name;
	public Token expr;
	public int line = -1;
	public Statement(Token t, Token n, Token e)
	{
		type = t;
		name = n;
		expr = e;
	}
	public void solve()
	{}
	public void eval()
	{}
}
