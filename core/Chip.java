package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Chip
{
	File file;
	public static Scanner console = new Scanner(System.in);
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		new Chip("ROM");
	}
	public Chip(String rom)
	{
		file = new File(rom);
		ArrayList<String> lines = new ArrayList<String>();
		try
		{
			Scanner scanner = new Scanner(file);
			int i = 0;
			System.out.println("SOURCE CODE:");
			for(String line = "";scanner.hasNextLine();System.out.println(i+ " " +line)){
				line = scanner.nextLine();
				lines.add(line);
				i += 10;
			}
			scanner.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nCOMPILER:");
		new Compiler(lines);
		System.out.println("SWITCH TO INTERPRETER MODE:");
		for(String line = "";console.hasNextLine();){
			line = console.nextLine();
			System.out.println((int) new Interpreter(line).expr());
		}
		//scanner.close();
	}
}
