/**
 * 
 */
package cs3240.regex.ast;

import cs3240.regex.automaton.NFA;
import cs3240.regex.automaton.NFAState;

/**
 * An epsilon node simply represents the
 * case of an NFA that transitions states
 * without consuming any characters. It is
 * used in conjunction with the union
 * operator to create a regular expression
 * with 0 or one instances of a given
 * expression.
 * 
 * @author Dilan Manatunga
 *
 */
public class EpsilonNode extends RegexAstNode {
	/**
	 * Generates an NFA that transitions to the 
	 * terminating state without consuming characters. 
	 * 
	 * @return an NFA which transitions states without consuming characters
	 * 
	 * @see RegexAstNode#generateNFA()
	 */
	@Override
	public NFA generateNFA() {
		NFA nfa = new NFA();
		// Create start state and end state
		NFAState startState = new NFAState();
		NFAState endState = new NFAState();
		// Add epsilon transition from start to end state
		startState.addEpsilonTransition(endState);
		// Set start state for NFA and add add terminatings state to NFA
		nfa.setStartState(startState);
		nfa.addTerminatingState(endState);
		return nfa;
	}

}
