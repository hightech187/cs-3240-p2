package cs3240.regex.ast;
import cs3240.regex.automaton.NFA;
import cs3240.regex.automaton.NFAState;

/**
 * This class represent the base regular expression
 * unit of a matching to a single character. 
 * 
 * @author Dilan Manatunga
 *
 */
public class ReCharNode extends RegexAstNode {

	/**
	 * The main constructor which takes in the character
	 * to match too and stores it as a string in the 
	 * value field.
	 * 
	 * @param character a character stored as a string 
	 */
	public ReCharNode(String character) {
		super();
		// Save the character to match too
		this.value = character;
	}

	/**
	 * Generates an NFA that matches to the character
	 * stored in the value field.
	 * 
	 * @return an NFA which matches to the specified character
	 * 
	 * @see RegexAstNode#generateNFA()
	 */
	@Override
	public NFA generateNFA() {
		// Get out the character to match to
		char c = value.charAt(0);
		// Create the start and end states for the NFA
		NFAState start_state = new NFAState();
		NFAState end_state = new NFAState();
		
		// Add a transition from the start state to end state for the specified character
		start_state.addTransition(c, end_state);
		
		// Create the NFA by setting the start state and adding the end state
		NFA nfa = new NFA();
		nfa.setStartState(start_state);
		nfa.addTerminatingState(end_state);
		return nfa;
	}

}
