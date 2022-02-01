/**
 * CET 350 - Program2 - Group 5
 * 
 * @author Robert Krency, kre1188@calu.edu
 * @author Auston Rigdon, rig4833@calu.edu
 * @author Kevin Reisch, rei3819@calu.edu
 * 
 */

import java.io.*;

public class Program2 {

    public static void main(String[] args)
    {
        /**
         * File names are provided either by command line, or by prompting
         * the user at runtime. If the input file does not exist, output file
         * does not exist error and prompt the user to enter a new name or quit.
         */
        String[] fileNames = new String[2];
        BufferedReader inputFile;
        PrintWriter outputFile;
        IOFile io = new IOFile();
        boolean quit;

        if (args.length >= 1) { // detect if the input file name is given
            fileNames[0] = args[0];
        }

        if (args.length == 2) { // detect if the output file name is given
            fileNames[1] = args[1];
        }

        if( io.getNames(args, fileNames) )
        {
            String inputLine;
            Word[] words = new Word[100];
            
            inputFile = io.openInputFile(fileNames[0]);
            outputFile = io.openOutputFile(fileNames[1]);

            try {
                while (inputFile.ready())
                {
                    inputLine = inputFile.readLine();
                }

                int uniqueWords = 0, totalCount = 0;
                for (Word w : words)
                {
                    uniqueWords++;
                    totalCount += w.getCount();
                }
                
                outputFile.println("Unique Words: " + uniqueWords);
                outputFile.println("Total Count: " + totalCount);                

                inputFile.close();
                outputFile.close();
            }
            catch (IOException eIO) 
            {
                System.err.println(eIO.toString());
            }
        }

        System.out.println("Exiting...");

    }
}