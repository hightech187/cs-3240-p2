package cs3240.regex.ast;
import cs3240.regex.automaton.NFA;

/**
 * This class represents the one or more repetition
 * operator in the regular expression language.
 * Specifically, this class will take in a regular 
 * expression node, and then generate an NFA 
 * that matches to any string containing one or 
 * more repetitions of the regular expression.
 * 
 * @author Dilan Manatunga
 *
 */
public class OneOrMoreRepNode extends RegexAstNode {
	/**
	 * The main constructor for the OneOrModeRepNode.
	 * It takes in a RegexAstNode, and sets that node
	 * as the lone child node. It also sets a string 
	 * representation of what the regular expression should be.
	 * 
	 * @param node the RegexAstNode whose regular expression is to be repeated one or more times
	 */
	public OneOrMoreRepNode(RegexAstNode node) {
		super();
		// Set the given node as the lone left child
		this.leftChild = node;
		/*
		 * Set a string representation of what the regular expression
		 * would be if it were written out
		 */
		this.value = node.getValue() + "+";
	}

	/**
	 * Generates an NFA which matches to one or
	 * more repetitions of the regular expression
	 * represented by the child NFA.
	 * 
	 * @return an NFA that matches to one or more repetitions of the child NFA
	 * 
	 * @see RegexAstNode#generateNFA()
	 */
	@Override
	public NFA generateNFA() {
		// Get the child NFA
		NFA nfa = this.leftChild.generateNFA();
		nfa.allowOneOrMoreRep(); // Allow one or more repetitions for the child NFA
		return nfa;
	}
}
