/**
 * CET 350 - Program2 - Group 5
 * 
 * @author Robert Krency, kre1188@calu.edu
 * @author Auston Rigdon, rig4833@calu.edu
 * @author Kevin Reisch, rei3819@calu.edu
 * 
 */

 import java.io.PrintWriter;

public class Word
{
	private String word;
	private int quant;
	
	//constructor
	//initialize word and set it's quant to 1
	public Word(String word)
	{
		this.word = word;
		quant = 1;
	}
	
	//returns the quant
	public int getCount()
	{
		return quant;
	}
	
	//returns the word
	public String getWord()
	{
		return word;
	}
	
	//returns true if the parameter and the object's word are the same
	public boolean isWord(String word)
	{
		return word.equals(this.word);
	}
	
	//returns true if the parameter and the object's word are the same, ignoring case
	public boolean isWordIgnoreCase(String word)
	{
		return word.equalsIgnoreCase(this.word);
	}
	
	//increments quant
	public void addOne()
	{
		quant++;
	}
	
	//print the word and it's count to the printwriter
	public void print(PrintWriter out)
	{
		out.println(word + "\t" + quant);
	}
	
	public static int findWord(Word[] words, String word, int count)
	{
		for(int index = 0 ; index < count; index++)
			if (words[index].getWord().equalsIgnoreCase(word))
				return index;

		return -1;
	}
	
}