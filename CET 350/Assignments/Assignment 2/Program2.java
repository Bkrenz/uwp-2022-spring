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
         * We know the size of this array will be 2, and the first index is the input file name
         * and the second index is the output file name. Initializing in this fashion instead of
         * using "new String[2]" is a less verbose way of initializating this array with empty strings,
         * helping to prevent null errors.
         */
        String[] fileNames = {"", ""};
        
        
        IOFile ioFileHandler = new IOFile();

        if( ioFileHandler.getNames(args, fileNames) )
        {
            
        }

        System.out.println("Exiting...");

    }
}