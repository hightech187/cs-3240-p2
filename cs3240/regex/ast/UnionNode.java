package cs3240.regex.ast;
import cs3240.regex.automaton.NFA;

/**
 * This class represents the union 
 * operation between two regular expressions,
 * where each regular expression is expressed
 * by the left child and right child nodes.
 *  
 * @author Dilan Manatunga
 *
 */
public class UnionNode extends RegexAstNode {
	/**
	 * The main constructor for the UnionNode class. 
	 * It takes in two RegexAstNodes, which each node
	 * representing one of the regular expressions
	 * for the union operation 
	 * 
	 * @param leftChild a node representing the left regular expression in the union operation
	 * @param rightChild a node representing the right regular expression in the union operation
	 */
	public UnionNode(RegexAstNode leftChild, RegexAstNode rightChild) {
		// Store each node as the left child and right child
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		// Specify the string representing the operation
		this.value = leftChild.getValue() + "|" + rightChild.getValue();
	}

	/**
	 * Generates an NFA that is the union of the
	 * two regular expressions represented by the 
	 * left and right child nodes. 
	 * 
	 * @return an NFA which matches to the union of the NFAs representing the left and right child nodes
	 * 
	 * @see RegexAstNode#generateNFA()
	 */
	@Override
	public NFA generateNFA() {
		// Get the left child NFA
		NFA nfa = this.leftChild.generateNFA();
		// Create the union of the left-child NFA and the right-child NFA
		nfa.union(this.rightChild.generateNFA());
		return nfa;
	}
}
