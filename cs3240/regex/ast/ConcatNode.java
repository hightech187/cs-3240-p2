package cs3240.regex.ast;
import cs3240.regex.automaton.NFA;

/**
 * This class represents the concatenation 
 * operation between two regular expressions. 
 * The regular expressions to be concatenated 
 * are stored in the left and right child nodes, 
 * and the operation is Left-to-Right associative.
 * 
 * @author Dilan Manatunga
 *
 */
public class ConcatNode extends RegexAstNode {
	/**
	 * The main constructor for the ConcatNode. It takes
	 * in the two RegexAstNodes that each represent the 
	 * two regular expressions to concatenate. 
	 * 
	 * @param leftChild a node representing the left-side regular expression to concatenate
	 * @param rightChild a node representing the right-side regular expression to concatenate
	 */
	public ConcatNode(RegexAstNode leftChild, RegexAstNode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.value = leftChild.getValue() + rightChild.getValue();
	}

	/**
	 * Generates an NFA that is the concatenation of the
	 * two regular expressions represented by the 
	 * left and right child nodes. 
	 * 
	 * @return an NFA which matches to the concatenation of the NFAs representing the left and right child nodes
	 * 
	 * @see RegexAstNode#generateNFA()
	 */
	@Override
	public NFA generateNFA() {
		// Get the NFA represented by the left child
		NFA nfa = leftChild.generateNFA();
		/*
		 * Get the NFA represented by the right child, and 
		 * then concatenate the left child NFA with the 
		 * right child NFA.
		 */
		nfa.concatenate(rightChild.generateNFA());
		return nfa;
	}

}
