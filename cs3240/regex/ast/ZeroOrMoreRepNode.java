package cs3240.regex.ast;
import cs3240.regex.automaton.NFA;


/**
 * This class represents the zero or more repetition
 * operator in the regular expression language.
 * Specifically, this class will take in a regular 
 * expression node, and then generate an NFA 
 * that matches to any string containing zero or 
 * more repetitions of the regular expression.
 * 
 * @author Dilan Manatunga
 *
 */
public class ZeroOrMoreRepNode extends RegexAstNode {

	/**
	 * The main constructor for the ZeroOrModeRepNode.
	 * It takes in a RegexAstNode, and sets that node
	 * as the lone child node. It also sets a string 
	 * representation of what the regular expression should be.
	 * 
	 * @param node the RegexAstNode whose regular expression is to be repeated zero or more times
	 */
	public ZeroOrMoreRepNode(RegexAstNode node) {
		super();
		this.leftChild = node;
	}

	/**
	 * Generates an NFA which matches to zero or
	 * more repetitions of the regular expression
	 * represented by the child NFA.
	 * 
	 * @return an NFA that matches to zero or more repetitions of the child NFA
	 * 
	 * @see RegexAstNode#generateNFA()
	 */
	@Override
	public NFA generateNFA() {
		// Get the child NFA
		NFA nfa = this.leftChild.generateNFA();
		nfa.allowZeroOrMoreRep(); // Allow zero or more repetitions of the NFA
		return nfa;
	}

}
