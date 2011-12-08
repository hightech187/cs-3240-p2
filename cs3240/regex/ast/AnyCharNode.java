package cs3240.regex.ast;
import cs3240.regex.automaton.NFA;
import cs3240.regex.automaton.NFAState;

/**
 * This class represents when the ANY_CHAR
 * token is seen in an regular expression. 
 * This token is usually a "." character,
 * and represents a regular expression 
 * which matches to any valid printable
 * character.
 * 
 * @author Dilan Manatunga
 *
 */
public class AnyCharNode extends RegexAstNode {
	/**
	 * The ASCII value of the first ASCII printable character
	 */
	private static final char PRINTABLE_CHAR_START_VAL = 32;
	/**
	 * The ASCII value of the last ASCII printable character
	 */
	private static final char PRINTABLE_CHAR_END_VAL = 126;

	/**
	 * The default constructor for an AnyCharNode.
	 * Only purpose is to specify the value of the node
	 * as the AnyCharNode representation character of a '.'. 
	 */
	public AnyCharNode() {
		super();
		this.value = ".";
	}
	
	
	/**
	 * Generates an NFA that represents the ANY_CHAR
	 * regular expression, aka an NFA that matches to 
	 * any ASCII printable character
	 * 
	 * @return an NFA which matches to any ASCII printable character 
	 * 
	 * @see RegexAstNode#generateNFA()
	 */
	@Override
	public NFA generateNFA() {
		// Create a new start and end state
		NFAState start_state = new NFAState();
		NFAState end_state = new NFAState();
		end_state.setTerminating(true); // Set the end state to terminating
		
		// Loop through all the printable character values and add
		// a transition from the start state to the end state
		for (char c = PRINTABLE_CHAR_START_VAL; c <= PRINTABLE_CHAR_END_VAL; ++c) {
			start_state.addTransition(c, end_state);
		}
		
		NFA nfa = new NFA(); // Create a new NFA
		// Set the start and terminating state for the NFA
		nfa.setStartState(start_state);
		nfa.addTerminatingState(end_state);
		return nfa;
	}

}
