//package AdvJava; for use in my complier

import java.io.*;

public class IOFile 
{
	
	//blank constructor, I don't think this class has any variables
	public void IOFile()
	{
		
	}
	
	//I'm not done with this yet, but it is to get file names
	public boolean getNames(String args[], String ioname[])
	{
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		boolean dontQuit = true;
		
		switch (args.length)
		{
			case 0:
				do
				{
					System.out.println("Please enter your input file: ");
					try
					{
						ioname[0] = stdin.readLine();
					
					}
					catch(IOException e)
					{
						System.out.println("Error: IOException");
					}
				}while(!ioname.equals("") && !FileExist(ioname[0]));
			case 1:
				System.out.println("Please enter your output file: ");
				try
				{
					ioname[1] = stdin.readLine();
				}
				catch(IOException e)
				{
					System.out.println("Error: IOException");
				}
			case 2:
				try
				{
					ioname[0] = args[0];
					ioname[1] = args[1];
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					
				}
		}
		
		return dontQuit;
		
	}
	
	//returns true if a file already exists, returns false otherwise
	public boolean FileExist(String name)
	{
		return (new File(name)).isFile();
	}
	
	//create a new backup file with a specified extension
	public void FileBackup(String name, String ext)
	{
		String newname;
		
		File old = new File(name);
		newname = name.substring(0, name.lastIndexOf('.')) + "." + ext;
		File backup = new File(newname);
		
		if(FileExist(newname))
		{
			backup.delete();
		}
		old.renameTo(backup);
	}
	
	//get the extension of a file
	public String FileExtension(String name)
	{
		return name.substring(name.lastIndexOf('.') + 1);
	}
	
	//get the name of the file (between the last \ and the last .)
	public String FileName(String name)
	{
		return name.substring(name.lastIndexOf('\\') + 1, name.lastIndexOf('.'));
	}
	
	// get the file path (between the first : and the last \)
	public String FilePath(String name)
	{
		return name.substring(name.indexOf(':') + 1, name.lastIndexOf('\\'));
	}
	
	//create a bufferedreader to read from a file
	BufferedReader openInputFile(String name)
	{
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(name));
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
		return in;
	}
	
	//Create a printwriter to write to a file
	PrintWriter openOutputFile(String name)
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new FileWriter(name));
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
		return out;
	}
}
