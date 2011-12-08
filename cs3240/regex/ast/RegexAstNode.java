package cs3240.regex.ast;
import cs3240.regex.automaton.NFA;

/**
 * This abstract class represents the implementation 
 * details for any node in an Abstract Syntax Tree (AST). 
 * The AST will represent the structure of a regular expression.
 * 
 * Any node describing an element or operation in the regular 
 * expression must implement this class, and its abstract methods.
 * 
 * @author Dilan Manatunga
 *
 */
public abstract class RegexAstNode {
	/**
	 * The left child node of the given node
	 */
	protected RegexAstNode leftChild;
	/**
	 * The right child node of the given node
	 */
	protected RegexAstNode rightChild;
	/**
	 * A string value which can be used to store data about the regex.
	 */
	protected String value = "";
	
	/**
	 * A default constructor for the RegexAstNode.
	 * In this constructor, each child is set to a
	 * null, and the value is set to an empty string.
	 */
	public RegexAstNode() {
		this.leftChild = null;
		this.rightChild = null;
		this.value = "";
	}
	
	/**
	 * A constructor that creates a RegexAstNode and 
	 * sets its left and right child to the given parameters.
	 * The value of the node is set to an empty string.
	 * 
	 * @param leftChild the left child node for the node to construct
	 * @param rightChild the right child node for the node to construct
	 */
	public RegexAstNode(RegexAstNode leftChild, RegexAstNode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.value = "";
	}
	
	
	/**
	 * Returns the left child node for the node
	 * 
	 * @return the left child RegexAstNode
	 */
	public RegexAstNode getLeftChild() {
		return leftChild;
	}
	
	/**
	 * Set a new left child node for the node
	 * 
	 * @param leftChild a new left child RegexAstNode
	 */
	public void setLeftChild(RegexAstNode leftChild) {
		this.leftChild = leftChild;
	}
	
	/**
	 * Returns the right child node for the node
	 * 
	 * @return the right child RegexAstNode
	 */
	public RegexAstNode getRightChild() {
		return rightChild;
	}
	
	/**
	 * Sets a new right child node for the node
	 * 
	 * @param rightChild the new right child RegexAstNode
	 */
	public void setRightChild(RegexAstNode rightChild) {
		this.rightChild = rightChild;
	}
	
	/**
	 * Get the string value stored in the node
	 * 
	 * @return the string data stored in the node
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the new string value for the node
	 * 
	 * @param value the new string value for the node
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * This method generates the NFA that represents 
	 * the regex given by the AST, where the given node 
	 * acts as the root of the AST. 
	 * 
	 * The NFA returned by this node should only be used 
	 * if it is returned from the root node of the tree.
	 * Any NFAs returned by interior nodes are liable to be
	 * modified by their parent nodes. However, a subsequent 
	 * call to this method will always return a new NFA object.
	 * 
	 * This method should be implemented by all subclasses. 
	 * @return the NFA represented by the node
	 */
	public abstract NFA generateNFA();
}
