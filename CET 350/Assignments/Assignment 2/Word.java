/**
 * A Word is a representation of a string and the number of occurrences.
 */
public class Word {

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
    public boolean equals(Object obj) {

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