/**
 * CET 350 - Program2 - Group 5
 * 
 * @author Robert Krency, kre1188@calu.edu
 * @author Auston Rigdon, rig4833@calu.edu
 * @author Kevin Reisch, rei3819@calu.edu
 * 
 */

import java.io.*;
import java.util.StringTokenizer;

public class Program2 {

    public static void main(String[] args)
    {
        String[] fileNames = new String[2];
        StringTokenizer tokenizer;

        BufferedReader inputReader;
        PrintWriter outputWriter;

        Word[] words = new Word[100];
        

        if( IOFile.getNames(args, fileNames) )
        {
            inputReader = IOFile.openInputFile(fileNames[0]);
            outputWriter = IOFile.openOutputFile(fileNames[1]);

            try {

                // For each line, read it in and parse the tokens
                while (inputReader.ready()) {
                    tokenizer = new StringTokenizer(inputReader.readLine(), "\t\n\r ");
                    while(tokenizer.hasMoreTokens())
                    {
                        outputWriter.println(tokenizer.nextToken());
                    }
                }

                // Close the Files
                inputReader.close();
                outputWriter.close();

            } catch (IOException eIO) {
                System.err.println(eIO.toString());
            }
            
        }

        System.out.println("Exiting...");

    }
}