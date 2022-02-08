//package AdvJava; for use in my complier

import java.io.*;

public class IOFile 
{
	
	//I'm not done with this yet, but it is to get file names
	public boolean getNames(String cmdArgs[], String fileNames[])
	{
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
		boolean dontQuit = true;

		// Ensure the file names are not null
		if( fileNames[0] == null )
			fileNames[0] = "";
		if( fileNames[1] == null )
			fileNames[1] = "";
		
		// Check if any of the file names were provided on the command line
        switch (cmdArgs.length) {

			case 1:
				fileNames[0] = cmdArgs[0];
				break;
			
			case 2:
				fileNames[0] = cmdArgs[0];
				fileNames[1] = cmdArgs[1];
				break;

			default:
				break;

		}

		// If the input file name is not valid, ask the user for a new name or to quit
		while ( dontQuit && !this.FileExist(fileNames[0])) {

			System.out.println("\nInvalid input file name. To quit, leave input empty.");
			System.out.println("Please input a new input file name: ");

			try {

				fileNames[0] = inputReader.readLine();
				if ( fileNames[0].isEmpty() )
					dontQuit = false;

			} catch (IOException eIO) {
				System.err.println("\nError getting input:\n" + eIO.toString());
			}
		}

		// If the output file name is not valid, ask the user for a new name or to quit
		boolean continueMenuInput, validOutput = false;
		while ( dontQuit && !validOutput) {

			// The file name is valid, but already exists
			if (!fileNames[0].equals(fileNames[1]) && this.FileExist(fileNames[1])) {
				continueMenuInput = true;
				while (continueMenuInput) {
				
					System.out.println("\nFile already exists. Please enter a number from the below menu: ");
					System.out.println("\t1. Input new name.");
					System.out.println("\t2. Back up file, then overwrite.");
					System.out.println("\t3. Overwrite file.");
					System.out.println("\t4. Quit.");

					try {
						switch(Integer.parseInt(inputReader.readLine())) {

							case 1: // User wants to enter a new name
								fileNames[1] = "";
								break;

							case 2: // User wants to backup the file then overwrite
								this.FileBackup(fileNames[1], "bak");
								validOutput = true;
								break;

							case 3: // User just wants to overwrite the file
								validOutput = true;
								break;

							case 4: // User wants to quit
								dontQuit = false;
								break;

							default:
								System.err.println("Invalid entry, try again.");

						}
						continueMenuInput = false;
					} catch (IOException eIO) {
						System.out.println("Error getting input:\n" + eIO.toString());
					} catch (NumberFormatException eNumForm) {
						System.err.println("Error: Invalid number. Try again.");
					}
		
				}
			}
			
			// The output file name is valid and the file does not exist
			else if (!fileNames[0].equals(fileNames[1]) && !this.FileExist(fileNames[1]) && !fileNames[1].isEmpty()) {
				dontQuit = true;
				validOutput = true;
			} 

			// The output file name is not a valid file name
			else {
				System.out.println("\nInvalid output file name. To quit, leave input empty.");
				System.out.println("Please input a new output name.");

				try {

					fileNames[1] = inputReader.readLine();
					if ( fileNames[1].isEmpty() )
						dontQuit = false;

				} catch (IOException eIO) {
					System.err.println("\nError getting input:\n" + eIO.toString());
				}
			}
		}
		
		return dontQuit;	
	}
	
	//returns true if a file already exists, returns false otherwise
	public boolean FileExist(String name)
	{
		return name != null && (new File(name)).isFile();
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
