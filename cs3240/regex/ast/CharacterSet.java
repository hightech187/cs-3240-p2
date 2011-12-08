package cs3240.regex.ast;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a set which will
 * only contain ASCII printable characters.
 *
 * @author Dilan Manatunga
 *
 */
public class CharacterSet {
	/**
	 * A constant for the number of ASCII printable characters
	 */
	private static final int NUM_PRINTABLE_CHARS = 95;
	/**
	 * The ASCII value of the first ASCII printable character
	 */
	private static final char PRINTABLE_CHAR_START_VAL = 32;
	/**
	 * The ASCII value of the last ASCII printable character
	 */
	private static final char PRINTABLE_CHAR_END_VAL = 126;
	/**
	 * An array of booleans that specifies if the character associated 
	 * with the given index exists within the set
	 */
	private boolean[] inSet;  
	/**
	 * The name associated with the CharacterSet
	 */
	private String name;
	
	/**
	 * The default constructor for a CharacterSet that 
	 * creates an empty set.
	 */
	public CharacterSet() {
		this.name = "";
		this.inSet = new boolean[NUM_PRINTABLE_CHARS];
	}
	
	/**
	 * Adds a character to the character set. 
	 * 
	 * @param c the character to add to the set
	 * @throws Exception If the character is not an ASCII printable character
	 */
	public void addCharacter(char c) throws Exception {
		// Check to see if character ASCII printable character
		if (c < PRINTABLE_CHAR_START_VAL || c > PRINTABLE_CHAR_END_VAL) {
			throw new Exception("Character is not an ASCII Printable Character");
		}
		// Set inSet index for character to true
		this.inSet[c-PRINTABLE_CHAR_START_VAL] = true;
	}
	
	/**
	 * Exclude the character from the character set
	 * 
	 * @param c the character to exclude from the set
	 * @throws Exception If the character is not an ASCII printable character
	 */
	public void excludeCharacter(char c) throws Exception {
		// Check to see if character ASCII printable character
		if (c < PRINTABLE_CHAR_START_VAL || c > PRINTABLE_CHAR_END_VAL) {
			throw new Exception("Character is not an ASCII Printable Character");
		}
		// Set inSet index for character to false
		this.inSet[c-PRINTABLE_CHAR_START_VAL] = false;
	}
	
	/**
	 * Adds the characters in the range from the start 
	 * character to the end character to the set
	 * 
	 * @param startChar the start character in the range
	 * @param endChar the end character in the range
	 * @throws Exception If the start or end character are not ASCII printable characters
	 */
	public void addCharacterRange(char startChar, char endChar) throws Exception {
		// Check to see if start char in range is ASCII printable character
		if (startChar < PRINTABLE_CHAR_START_VAL || startChar > PRINTABLE_CHAR_END_VAL) {
			throw new Exception("Character is not an ASCII Printable Character");
		}
		
		// Check to see if end char in range is ASCII printable character
		if (endChar < PRINTABLE_CHAR_START_VAL || endChar > PRINTABLE_CHAR_END_VAL) {
			throw new Exception("Character is not an ASCII Printable Character");
		}
		// Loop through the range and indicate each character is in the set
		for ( ;startChar <= endChar; ++startChar) {
			this.inSet[startChar-PRINTABLE_CHAR_START_VAL] = true;
		}
	}
	
	/**
	 * Excludes the characters in the range from the start 
	 * character to the end character from the set
	 * 
	 * @param startChar the start character in the range
	 * @param endChar the end character in the range
	 * @throws Exception If the start or end character are not ASCII printable characters
	 */
	public void excludeCharacterRange(char startChar, char endChar) throws Exception {
		// Check to see if start char in range is ASCII printable character
		if (startChar < PRINTABLE_CHAR_START_VAL || startChar > PRINTABLE_CHAR_END_VAL) {
			throw new Exception("Character is not an ASCII Printable Character");
		}

		// Check to see if end char in range is ASCII printable character
		if (endChar < PRINTABLE_CHAR_START_VAL || endChar > PRINTABLE_CHAR_END_VAL) {
			throw new Exception("Character is not an ASCII Printable Character");
		}
		
		// Loop through the range and indicate each character is not in the set
		for ( ;startChar <= endChar; ++startChar) {
			this.inSet[startChar-PRINTABLE_CHAR_START_VAL] = false;
		}
	}

	/**
	 * Exclude the characters from the set that exist
	 * in the inputed CharacterSet
	 * 
	 * @param set a CharacterSet of characters to exclude
	 */
	public void excludeSet(CharacterSet set) {
		boolean arr[] = set.inSet; // Get the inSet for the inputed CharacterSet
		/*
		 *  Loop through all the positions and set an inSet position to true 
		 *  only if the position originally contained a true value, and if
		 *  inputed CharacterSet's inSet has false at the position. Otherwise,
		 *  set that position to false.
		 */
		for (int i = 0; i < NUM_PRINTABLE_CHARS; ++i) {
			this.inSet[i] = this.inSet[i] && !arr[i];
		}
	}
	
	/**
	 * Add all the printable characters to the set
	 */
	public void addPrintableCharacters() {
		/*
		 *  Loop through all the printable characters and set their 
		 *  corresponding inSet positions to true. 
		 */
		for (int i  = 0; i < NUM_PRINTABLE_CHARS; ++i) {
			this.inSet[i] = true;
		}
	}
	
	/**
	 * Return a collection of the characters that are in the character set
	 * @return a collection of the characters in the set
	 */
	public Collection<Character> getCharactersInSet() {
		ArrayList<Character> set = new ArrayList<Character>(); // Create a collection to hold the characters
		// Loop through the possible characters in the set
		for (int i  = 0; i < NUM_PRINTABLE_CHARS; ++i) {
			// If the character's corresponding inSet is true, then add that character to the collection
			if (this.inSet[i]) {
				set.add(Character.valueOf((char) (PRINTABLE_CHAR_START_VAL + i)));
			}
		}
		return set;
	}
	
	/**
	 * Creates a deep-copy of the CharacterSet.
	 * @return a copy of the CharacterSet
	 */
	public CharacterSet copy() {
		// Call the private constructor that takes in the character set
		return new CharacterSet(this);
	}
	
	/**
	 * A private constructor that takes in a CharacterSet
	 * and returns a deep-copy of it.
	 * 
	 * @param set a CharacterSet to copy
	 */
	private CharacterSet(CharacterSet set) {
		this.name = set.getName(); // Copy the name from the character set
		boolean inSet[] = set.inSet;
		this.inSet = new boolean[NUM_PRINTABLE_CHARS]; // Create the in-set for the copy
		// Loop through the inSet, and copy the value stored in the inputed inSet
		for (int i = 0; i < NUM_PRINTABLE_CHARS; ++i) {
			this.inSet[i] = inSet[i];
		}
	}
	
	/**
	 * Returns a string representation of the CharacterSet
	 * with the name of the set, and a list of the
	 * characters within the set.
	 * 
	 * @return the string representation of the CharacterSet
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = "CharacterSet [name=" + name + ", set: ";
		for (Character c : getCharactersInSet()) {
			s += c.toString() + ",";
		}
		s = s.substring(0, s.length()-1) + "]";
		return s;	
	}
	
	/**
	 * Returns the name of the character set
	 * @return the string name for the character
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set a new name for the CharacterSet
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
