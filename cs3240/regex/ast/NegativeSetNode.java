package cs3240.regex.ast;

/**
 * This class represents the negative set
 * operator in a regular expression in which
 * the regular expression matches to any 
 * character not within the first set but
 * does exist in the second set.
 * 
 * @author Dilan Manatunga
 *
 */
public class NegativeSetNode extends SetNode {
	
	/**
	 * The basic constructor for a NegativeSetNode
	 * which takes in a SetNode (which will be the left 
	 * child) containing the set of characters to exclude, 
	 * and a SetNode (which will be the right child) containing
	 * the base set of characters to exclude from.
	 * 
	 * @param leftChild a SetNode containing set of characters to exclude from the base character set
	 * @param rightChild a SetNode containing the base set of characters 
	 */
	public NegativeSetNode(SetNode leftChild, SetNode rightChild) {
		// Set the left and right child node
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		// Specify a string value to describe the exclude set
		this.value = String.format("[^%s] IN %s", this.leftChild.getValue(), this.rightChild.getValue());
		this.set = rightChild.getSet().copy(); // Copy the right child set and store it as the current set
		//  Exclude the characters specified by the left child set
		this.set.excludeSet(leftChild.getSet());
	}
}
