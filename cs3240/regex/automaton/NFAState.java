package cs3240.regex.automaton;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * This class represent an NFAState
 * that exists in an NFA graph. The
 * class uses adjacency lists to
 * maintain the transitions to 
 * other NFAStates.
 * 
 * @author Dilan Manatunga
 *
 */
public class NFAState {
	
	/** A static variable containing the id last used when creating an NFAState instance. */
	private static int last_used_id = -1;
	
	/** A mapping between a transition character and the set of states reached by following the transition. */
	private HashMap<Character, NFAStateSet> transitions;
	
	/** A set of NFAStates seen when following the epsilon transition. */
	private NFAStateSet epsilon_transitions;
	
	/** A boolean on whether the state is a terminating state. */
	private boolean terminating = false;
	
	/** A string indicating the type of state. */
	private String type;
	
	/** An integer that represent a unique ID for the state. */
	private int id;
	
	/**
	 * The main constructor for an NFAState that
	 * creates a non-terminating instance that has
	 * no transitions. The ID of the state will be
	 * the next available ID.
	 */
	public NFAState() {
		// Increment the last_used_id to the id value of the state
		NFAState.last_used_id++; 
		// Set the ID and initialize the transitions
		this.id = NFAState.last_used_id; 
		this.transitions = new HashMap<Character, NFAStateSet>();
		this.epsilon_transitions = new NFAStateSet();
	}
	
	/**
	 * Adds a transition from this instance to the inputed
	 * NFAState for the inputed character.
	 * 
	 * @param c the character to transition on
	 * @param state the NFAState to transition to
	 */
	public void addTransition(char c, NFAState state) {
		Character c_obj = Character.valueOf(c);
		// Attempt to get the state set for the given transition
		NFAStateSet set = transitions.get(c_obj);
		if (set == null) {
			// If the state set is null, create a new one
			set = new NFAStateSet();
			// Put the newly create set in the transitions table
			transitions.put(c_obj, set);
		}
		// Add the state to the set
		set.addState(state);
	}
	
	/**
	 * Adds an epsilon transition  to the inputed
	 * NFAState for the inputed character.
	 * 
	 * @param state the NFAState seen by following the epsilon transition
	 */
	public void addEpsilonTransition(NFAState state) {
		epsilon_transitions.addState(state);
	}
	
	/**
	 * Follow the transition for the inputed character
	 * from the NFAState instance. It will return 
	 * an NFAStateSet of the states seen by following the
	 * transition, or null if the transition for the
	 * character does not exist.
	 * 
	 * @param c the character to transition on
	 * @return a set of states seen by following the transition. Returns null if no states seen by following the transition, or the transition for the character does not exist.
	 */
	public NFAStateSet followTransition(char c) {
		return transitions.get(Character.valueOf(c));
	}
	
	/**
	 * Returns the state set seen by following
	 * an epsilon transition.
	 *
	 * @return a set of the states seen by following the epsilon transition
	 */
	public NFAStateSet getEpsilonTransitions() {
		return epsilon_transitions;
	}

	/**
	 * Returns a boolean on whether the state is
	 * a terminating state.
	 *
	 * @return true if the state is terminating
	 */
	public boolean isTerminating() {
		return terminating;
	}

	/**
	 * Sets a boolean on whether the state is a
	 * terminating state.
	 *
	 * @param terminating a boolean on if the state should be terminating
	 */
	public void setTerminating(boolean terminating) {
		this.terminating = terminating;
	}

	/**
	 * Returns the ID of the state.
	 *
	 * @return the integer id of the state
	 */
	public int getID() {
		return id;
	}

	/**
	 * Return the string type for the state.
	 *
	 * @return the string type for the state
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set a new string type for the state.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Returns an integer hash for the NFAState instance.
	 * 
	 * @return hash code for the NFAState
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// Eclipse's standard implementation for hash-code 
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + (terminating ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * Identifies whether the inputed object is
	 * considered to equal the NFAState instance.
	 *
	 * @param obj the obj
	 * @return true if the objects are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// Eclipse's standard implementation for hash-code 
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NFAState)) {
			return false;
		}
		NFAState other = (NFAState) obj;
		if (id != other.id) {
			return false;
		}
		if (terminating != other.terminating) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a string representation of the NFAState.
	 * Currently, this representation contains the
	 * ID of the state and the type for the state.
	 * 
	 * @return the string representation of the Token
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (type == null){
			if (terminating) {
				return "NFAState [id=" + id + ", type=TERM]";
			}
			return "NFAState [id=" + id + ", type=NON-TERM]";
		} else {
			return "NFAState [id=" + id + ", type=" + type + "]";
		}
	}
}
