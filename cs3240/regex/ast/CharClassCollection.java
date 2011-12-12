package cs3240.regex.ast;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class represents a collection
 * of character classes. A character class
 * is composed of a name, and a CharacterSet
 * which represents the set of characters
 * that exist in the character class. 
 * 
 * @author Dilan Manatunga
 *
 */
public class CharClassCollection {
	/**
	 * A mapping between the string of the Character Class name and CharacterSet for said class
	 */
	private HashMap<String, CharacterSet> charClassTable;
	
	/**
	 * The default constructor for a CharClassCollection,
	 * which simply initializes the necessary data structures.
	 */
	public CharClassCollection() {
		// Initialize the HashMap
		this.charClassTable = new HashMap<String, CharacterSet>();
	}
	
	/**
	 * Add a character class with the inputed name
	 * and its representing character set to the collection.
	 * 
	 * @param name the string name of the character class
	 * @param set the CharacterSet 
	 */
	public void addCharClass(String name, CharacterSet set) {
		charClassTable.put(name, set); // Add the (name, set) pair to the hash map
	}
	
	/**
	 * Get the character set that represents the
	 * character class with the inputed name that is
	 * in the collection. If a character class with the 
	 * given name is not in the collection, then null
	 * is returned.
	 * 
	 * @param name the name of the character class
	 * @return the CharacterSet that represents the character class with the given name. Will return null if a class with the inputed name does not exist.
	 */
	public CharacterSet getCharClassSet(String name) {
		return charClassTable.get(name); // Get the character set associated with the given name
	}
	
	/**
	 * Get a list of the names for each character class
	 * in the collection.
	 * 
	 * @return a collection of the names of each class
	 */
	public Collection<String> getClassNames() {
		return charClassTable.keySet(); // Return the keys (the string class names) in the hash map
	}
	
	/**
	 * Get a list of the CharacterSets that represent
	 * each character class in the collection.
	 * 
	 * @return a collection of the CharacterSets that represent each character class
	 */
	public Collection<CharacterSet> getCharClasses() {
		return charClassTable.values(); // Return the values (the CharacterSets) in the hash map
	}
}
