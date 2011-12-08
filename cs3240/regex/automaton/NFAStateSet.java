package cs3240.regex.automaton;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * This class is used to represent
 * a set of NFAStates. The set follows
 * the mathematical definition of a set
 * in that there will be no two NFA
 * states that are considered equal to 
 * each other.
 * 
 * @author Dilan Manatunga
 *
 */
public class NFAStateSet {
	
	/** A set that will contain the NFA states. */
	private Set<NFAState> states;

	/**
	 * The default constructor for an NFAStateSet
	 * that creates an empty set.
	 */
	public NFAStateSet() {
		this.states = new LinkedHashSet<NFAState>();
	}
	
	/**
	 * A secondary constructor for an NFAStateSet
	 * that creates a set only containing the 
	 * inputed NFAState.
	 * 
	 * @param state an NFAState that should be contained in the set
	 */
	public NFAStateSet(NFAState state) {
		this(); // Call the default constructor
		this.addState(state); // Add the state
	}
	
	/**
	 * Adds an NFAState to the set.
	 *
	 * @param state an NFAState to add to the set
	 */
	public void addState(NFAState state) {
		this.states.add(state);
	}
	
	/**
	 * Adds the states contained in the inputed
	 * NFAStateSet to the set.
	 *
	 * @param set an NFAStateSet that contains a set of NFAStates
	 */
	public void addStates(NFAStateSet set) {
		this.states.addAll(set.getNFAStates());
	}
	
	/**
	 * Returns a collection of the NFAStates in the set.
	 *
	 * @return a collection of the NFAStates in the set
	 */
	public Collection<NFAState> getNFAStates() {
		return states;
	}
	
	/**
	 * Indicates whether the inputed state exists in the set.
	 *
	 * @param state an NFAState to see if it is in the set
	 * @return true if the state is in the set
	 */
	public boolean hasState(NFAState state) {
		return states.contains(state);
	}
	
	/**
	 * Creates a copy of the NFAStateSet. Note,
	 * this is not a deep-copy. The copy and the 
	 * original set will share the same references 
	 * to the NFAState objects
	 * 
	 * @return a copy of the NFAStateSet
	 */
	public NFAStateSet copy() {
		NFAStateSet set = new NFAStateSet(); // Create a new NFA state
		set.addStates(this); // Add the states from this set to the copy
		return set;
	}
	
	/**
	 * Returns a true or false on whether the set is empty.
	 *
	 * @return true if the set is empty
	 */
	public boolean isEmpty() {
		return states.isEmpty();
	}
	
	

	/**
	 * Returns an integer hash for a NFAStateSet instance. 
	 * The hash code of a set is defined to be the sum of the 
	 * hash codes of the elements in the set, where the hash 
	 * code of a null element is defined to be zero. This 
	 * ensures that s1.equals(s2) implies that 
	 * s1.hashCode()==s2.hashCode() for any two sets s1 and s2.
	 * 
	 * @return the hash code value for this set
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// Eclipse's implementation for the equals method
		final int prime = 31;
		int result = 1;
		result = prime * result + ((states == null) ? 0 : states.hashCode());
		return result;
	}

	/**
	 * Identifies whether the inputed object is
	 * considered to be equal to the NFAState instance.
	 * In this case, two instances are considered equal
	 * if each set contains the same states.
	 *
	 * @param obj the obj
	 * @return true if the objects are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// Eclipse's implementation for the equals method
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NFAStateSet)) {
			return false;
		}
		NFAStateSet other = (NFAStateSet) obj;
		if (states == null) {
			if (other.states != null) {
				return false;
			}
		} else if (!states.equals(other.states)) {
			return false;
		}
		return true;
	}
	
	

	/**
	 * Returns a string representation of the NFAStateSet.
	 * The string will show a list of the NFAState objects
	 * contained in the set
	 * 
	 * @return the string representation of the NFAStateSet
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s =  "NFAStateSet [";
		// Loop through and add the NFAState string representation to the string
		for (NFAState state: getNFAStates()) {
			s += "("+ state.toString() + "),";
		}
		s = s.substring(0, s.length()-1) + "]";
		return s;
	}
}
