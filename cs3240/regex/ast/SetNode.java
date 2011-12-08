package cs3240.regex.ast;
import cs3240.regex.automaton.NFA;
import cs3240.regex.automaton.NFAState;

/**
 * This class is a special sub-class of 
 * a RegexAstNode, that represents a
 * definition of a Character Set in the
 * regular expression.
 * 
 * @author Dilan Manatunga
 *
 */
public class SetNode extends RegexAstNode {
	/**
	 * The set of characters defined in the regular expression
	 */
	protected CharacterSet set;
	/**
	 * A string builder that contains that characters and ranges that have been added to this set
	 */
	protected StringBuilder setStringBuilder;
	
	/**
	 * The default constructor for a Set Node that simply
	 * creates a SetNode with an empty character set
	 */
	public SetNode() {
		// Create a new character set
		this.set = new CharacterSet();
		// Initialize the string builder
		this.setStringBuilder = new StringBuilder(10);
	}

	/**
	 * A secondary constructor for the set node that 
	 * takes in a pre-defined character set
	 * 
	 * @param set a CharacterSet to associate with the node
	 */
	public SetNode(CharacterSet set) {
		this.set = set; // Set the Character Set
		this.setStringBuilder = new StringBuilder(10); // Initialize the string builder

	}
	
	/**
	 * Adds the inputed character to the character set, 
	 * only if the character is an ASCII printable character
	 * 
	 * @param c the character to add to the set
	 * @throws Exception If the character is not an ASCII printable character
	 */
	public void addCharacter(char c) throws Exception {
		set.addCharacter(c); // Add the character to the set
		setStringBuilder.append(c);  // Add the specified character to the end of the string
	}
	
	/**
	 * Adds the range of characters specified by the two 
	 * inputs to the character set
	 * 
	 * @param startChar the start character in the range
	 * @param endChar the end character in the range
	 * @throws Exception If the start or end character are not ASCII printable characters
	 */
	public void addCharacterRange(char startChar, char endChar) throws Exception {
		this.set.addCharacterRange(startChar, endChar);  // Add the range of characters to the set
		// Add the character range to the end of the string
		this.setStringBuilder.append(startChar).append('-').append(endChar);
	}
	


	/**
	 * Generates an NFA that matches to any character
	 * that exists in the character set
	 * 
	 * @return an NFA which matches to characters that exist in the character set
	 * 
	 * @see RegexAstNode#generateNFA()
	 */
	@Override
	public NFA generateNFA() {
		// Create a new star and end state
		NFAState start_state = new NFAState();
		NFAState end_state = new NFAState();
		end_state.setTerminating(true);
		/*
		 * For each character in the set, add a transition
		 * from the start state to the end state for that
		 * character.
		 */
		for (Character c: this.set.getCharactersInSet()) {
			start_state.addTransition(c.charValue(), end_state);
		}
		
		// Create a new NFA and the start and end state to that NFA
		NFA nfa = new NFA();
		nfa.setStartState(start_state);
		nfa.addTerminatingState(end_state);
		return nfa;
	}
	
	
	/**
	 * Returns the CharacterSet stored in the node
	 * 
	 * @return the CharacterSet stored in the node
	 */
	public CharacterSet getSet() {
		return set;
	}

	/**
	 * Set a CharacterSet to store in the node
	 * 
	 * @param set a new CharacterSet for the node
	 */
	public void setSet(CharacterSet set) {
		this.set = set;
	}

	/* (non-Javadoc)
	 * @see cs3240.regex.ast.RegexAstNode#getValue()
	 */
	@Override
	public String getValue() {
		// Construct the string stored in the string builder and store it as the value
		this.value = this.setStringBuilder.toString();
		return super.getValue();
	}

	/* (non-Javadoc)
	 * @see cs3240.regex.ast.RegexAstNode#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		setStringBuilder = new StringBuilder(value);
		super.setValue(value);
	}

}
