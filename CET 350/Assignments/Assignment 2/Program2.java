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
        String currentToken, currentWord;
        StringTokenizer tokenizer;

        BufferedReader inputReader;
        PrintWriter outputWriter;

        Word[] words = new Word[100];
        int wordCount = 0, curIndex, total = 0;
        char curChar;

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
                        currentToken = tokenizer.nextToken();
                        currentWord = "";
                        curIndex = 0;

                        while ( curIndex < currentToken.length() ) {
                            curChar = currentToken.charAt(curIndex);

                            // If the current character is a number it gets appended in all cases
                            // Check if the previous character was a '-' to check if this number should be negative
                            if ( Character.isDigit(curChar) ) {
                                currentWord += curChar;

                                // Check for negative
                                if ( curIndex > 0 && isInt(currentWord)
                                    && (currentToken.charAt(curIndex - 1) == '-') )
                                    currentWord = "-" + currentWord; 
                            }

                            // If it's not a letter
                            else {

                                // If the word is a number, we just found the end of the number and need to add
                                // the number to the total, and start a new word
                                if( isInt(currentWord) ) {
                                    try {
                                        total += Integer.parseInt(currentWord);
                                    } catch (NumberFormatException eNF) {}
                                    currentWord = "";
                                }

                                // If it's a letter, or the word isn't empty, we just append the character
                                if (Character.isLetter(curChar) || !currentWord.isEmpty())
                                    currentWord += curChar;

                            }

                            curIndex++;

                        }
                        
                        // If the current word is a number, add it to the total
                        if( isInt(currentWord) ) {
                            try {
                                total += Integer.parseInt(currentWord);
                            } catch (NumberFormatException eNF) {}
                        }

                        // Add this word to the words list
                        curIndex = Word.findWord(words, currentWord, wordCount);
                        if (curIndex >= 0)
                            words[curIndex].addOne();
                        else if(!isInt(currentWord) && !currentWord.isEmpty()){
                            words[wordCount] = new Word(currentWord);
                            wordCount++;
                        }

                    }
                }

                // Write stuff to file
                for(int i = 0; i < wordCount; i++)
                    words[i].print(outputWriter);
                outputWriter.println("Unique words: " + wordCount);
                outputWriter.println("Integer Sum: " + total);

                // Close the Files
                inputReader.close();
                outputWriter.close();

            } catch (IOException eIO) {
                System.err.println(eIO.toString());
            }
            
        }

        System.out.println("Exiting...");

    }

    public static boolean isInt(String word) {

        try {
            Integer.parseInt(word);
            return true;
        } catch(NumberFormatException eNF) {
            
        }

        return false;

    }

}