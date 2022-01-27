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
        String inputFileName = "", outputFileName = "";
        BufferedReader inputFile = BufferedReader.nullReader();
        BufferedWriter outputFile = BufferedWriter.nullWriter();

        if (args.length >= 1) { // detect the input file name
            inputFileName = args[0];
        }

        if (args.length >= 2) { // detect the output file name
            outputFileName = args[1];
        }

        /* Attempt to open the input file
        *  If it does not exist, ask for a different name
        * If the user enters nothing, exit the program
        */


        
        /* 
        * Attempt to open the output file
        * If the output file exists, ask the user:
        *       1. If they want to enter a different file name
        *       2. If they want to backup the old file and then overwrite
        *       3. If they want to just overwrite
        *       4. If they want to just quit
        */
        


        /**
         * If the Input and Output Files are opened, read in and tokenize all the words
         * from the input file. For each word, see if it exists in the array already.
         *  - If it exists, increment the count
         *  - If it does not exist, add it to the array
         */
        String inputLine;
        Word[] words = new Word[100];
        
        

        /**
         * Output the count of unique words, and the total number of words
         * summed from the counts. 
         */
        int uniqueWords = 0, totalCount = 0;
        for (Word w : words)
        {
            uniqueWords++;
            totalCount += w.getCount();
        }
        
        System.out.println("Unique Words: " + uniqueWords);
        System.out.println("Total Count: " + totalCount);

        // Exit the program
        System.out.println("Exiting...");

    }


    /**
     * A Word is a representation of a string and the number of occurrences.
     */
    private class Word {

        private String word;
        private int count;

        public Word(String w)
        {
            this. word = w;
            this.count = 0;
        }

        public String getWord() {
            return this.word;
        }

        public int getCount() {
            return this.count;
        }

        public void addCount(int c) {
            this.count += c;
        }

        /**
         * equals(Object obj)
         * Compares an object with the Word. Words are not case sensitive.
         * 
         * @param obj the object to compare this Word with
         * @return True if the Words have the same strings
         * 
         */
        @Override
        public bool equals(Object obj) {

            // It's equivalent to itself
            if (obj == this)
                return true;

            // If the object is a different type, it's not equivalent
            if (!(obj instanceof Word))
                return false;

            // If the internal word is the same, they are equivalent
            return this.word.equals(((Word)obj).word);

        }

    }
    
}
