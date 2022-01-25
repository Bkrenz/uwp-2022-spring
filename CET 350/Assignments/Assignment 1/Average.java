/**
 * Group 5
 * @author Robert Krency, kre1188@calu.edu
 * @author Auston Rigdon, rig4833@calu.edu
 * @author Kevin Reisch, rei3819@calu.edu
 * 
 * Assignment 1 - Average
 * CET 350 - Pyzdrowski
 *
 */

import java.io.*;

public class Average {
    
    public static void main(String[] args) {

        // Initialize;
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        Double inputNumber, sum = 0.0, average;
        int count = 0;

        // Get inputs
        do {
            System.out.println("Enter a valid number between 0.0 and 100.0, inclusive: ");

            // Valid inputs are doubles between 0.0 and 100.0
            try {
                inputNumber = Double.parseDouble(inputReader.readLine());
                if ( inputNumber < 0.0 || inputNumber > 100.0 || inputNumber.isNaN() ) 
                    break;
                count++;
                sum += inputNumber;
            } catch(IOException ioException) {
                System.err.println("Error getting input.");
                break;
            } catch(NumberFormatException numberFormatException) {
                System.err.println("Improper number format.");
                break;
            }

        } while (true);

        // Calculate average
        average = sum / count;
        if (average.isNaN()) {
            System.out.println("Invalid number of inputs, cannot divide by zero.");
        } else {
            System.out.println("Sum: " + sum + "\nAverage: " + average + "\nCount: " + count );
        }

    }
}


