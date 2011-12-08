package cs3240.regex.automaton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

// TODO: Auto-generated Javadoc
/**
 * This class represents a Non-Deterministic
 * Finite Automaton (NFA). The NFA is expressed
 * through a graph made up of NFAState objects.
 * The NFA class only contains the start state
 * from which to traverse the automaton, as well
 * as a list of the terminating states in
 * the automaton.
 * 
 * @author Dilan Manatunga
 *
 */
public class NFA {
	
	/** The ASCII value of the first ASCII printable character. */
	private static final char PRINTABLE_CHAR_START_VAL = 32;
	
	/** The ASCII value of the last ASCII printable character. */
	private static final char PRINTABLE_CHAR_END_VAL = 126;
	
	/** The start state in the automaton. */
	private NFAState startState;
	
	/** A list of the terminating states in the automaton. */
	private ArrayList<NFAState> terminatingStates;
	
	/**
	 * The main constructor for an NFA 
	 * that simply creates an empty automaton. 
	 */
	public NFA() {
		this.terminatingStates = new ArrayList<NFAState>();
	}

	/**
	 * This method modifies the NFA
	 * to be a concatenation of itself with
	 * the inputed NFA.
	 * 
	 * @param nfa2 the NFA to concatenate on to the current NFA instance
	 */
	public void concatenate(NFA nfa2) {
		// Get the start state from the second NFA
		NFAState nfa2_start = nfa2.getStartState();
		/*
		 * Add an epsilon transition from all the terminating
		 * states in the NFA to the start state in NFA2. Also,
		 * all the terminating states should no longer be terminating. 
		 */
		for (NFAState state: terminatingStates) {
			state.addEpsilonTransition(nfa2_start); // Add epsilon transition to start state
			state.setTerminating(false); // Set the state to non-terminating
		}
		
		terminatingStates.clear(); // Clear the current terminating states
		// Add the terminating states from NFA2 as the new terminating states 
		terminatingStates.addAll(nfa2.getTerminatingStates());
	}
	
	/**
	 * This method modifies the NFA
	 * to be a union of itself with
	 * the inputed NFA.
	 * 
	 * @param nfa2 the NFA to union with the current NFA instance
	 */
	public void union(NFA nfa2) {
		// Create what will be the new start state
		NFAState new_start = new NFAState();
		/*
		 * Add an epsilon transition from the new start state
		 * to the original start state and the second NFA's start
		 * state
		 */
		new_start.addEpsilonTransition(startState);
		new_start.addEpsilonTransition(nfa2.getStartState());
		
		startState = new_start; // Set the newly created state as the start state
		// Add the terminating states from NFA2 to the terminating states list
		terminatingStates.addAll(nfa2.getTerminatingStates());
	}
	
	/**
	 * This method modifies the NFA to create
	 * an NFA identical to the original except
	 * it allows a sequence of one or more repetitions of 
	 * any string that matched the original NFA.
	 */
	public void allowOneOrMoreRep() {
		// Add epsilon transitions from the terminating states to the start state
		for (NFAState state: terminatingStates) {
			state.addEpsilonTransition(startState);
		}
	}
	
	/**
	 * This method modifies the NFA to create
	 * an NFA identical to the original except
	 * it allows a sequence of zero or more repetitions of 
	 * any string that matched the original NFA.
	 */
	public void allowZeroOrMoreRep() {
		// Create what will be the new start state
		NFAState new_start = new NFAState(); 
		// Add epsilon transitions from the terminating states to the start state
		for (NFAState state: this.terminatingStates) {
			state.addEpsilonTransition(this.startState);
		}
		// Add an epsilon transition from the new start state 
		new_start.addEpsilonTransition(this.startState);
		new_start.setTerminating(true); // Set the new start state as terminating
		
		/*
		 * Add the new start state to the terminating list
		 * and set it as the new start state
		 */
		addTerminatingState(new_start);
		startState = new_start;
		
	}
	
	/**
	 * Add a new terminating state to the NFA, 
	 * and also sets that state as terminating.
	 * 
	 * @param state the NFAState to add as a terminating state to the NFA
	 */
	public void addTerminatingState(NFAState state) {
		state.setTerminating(true);
		terminatingStates.add(state);
	}
	
	/**
	 * Sets the type of terminating state for
	 * all the terminating states, overriding any 
	 * type the terminating states may have originally had.
	 *  
	 * @param type the string type each terminating state should be
	 */
	public void setTerminatingStatesType(String type) {
		// Loop through the terminating states and set their type
		for (NFAState state: terminatingStates) {
			state.setType(type);
		}
	}
	
	/**
	 * Returns the start state of the NFA.
	 *
	 * @return the start state for the NFA
	 */
	public NFAState getStartState() {
		return startState;
	}

	/**
	 * Sets a new start state for the NFA.
	 *
	 * @param startState the NFAState that is to be the new start state
	 */
	public void setStartState(NFAState startState) {
		this.startState = startState;
	}

	/**
	 * Return a collection of the terminating states in the NFA.
	 *
	 * @return a collection of the terminating states in the NFA
	 */
	public Collection<NFAState> getTerminatingStates() {
		return terminatingStates;
	}

	/**
	 * This method returns a DFA that is created
	 * by converting the NFA instance to a DFA.
	 * 
	 * @return the DFATable from converting the NFA to a DFA 
	 */
	public DFATable toDFA() {
		DFATable dfa = new DFATable(); // Create the DFA to store the converted NFA in
		// A mapping between a set NFA states and their corresponding DFA state
		HashMap<NFAStateSet, Integer> dfaStates = new HashMap<NFAStateSet, Integer>();
		
		// A queue of the NFA state sets that need to be analyzed
		Queue<NFAStateSet> stateQueue = new LinkedList<NFAStateSet>();
		/*
		 * Identify the start state in the DFA by doing an epsilon closure
		 * on the NFA start state to identify the start state set
		 */
		NFAStateSet start_set = epsilon_closure(new NFAStateSet(startState));
		
		// Create the start state for the DFA
		int start_id = dfa.newDFAState();
		// Map the start state set to the DFA start state
		dfaStates.put(start_set, Integer.valueOf(start_id));
		stateQueue.add(start_set); // Add the start state state set to the DFA
		
		// Check to see if the start state set has a terminating state
		NFAState termState = hasTerminatingState(start_set);
		if (termState != null) {
			/*
			 * If it has a terminating state, then set the
			 * corresponding DFA state as terminating, and
			 * store the type of the NFA state in the corresponding
			 * DFA state.
			 */
			dfa.setTerminatingState(start_id, termState.getType());
		}
		
		// Continue looping until all necessary NFA state sets have been processed 
		while (!stateQueue.isEmpty()) {
			// Get the next state set to analyze as well as the id of the corresponding DFA state
			NFAStateSet state_set = stateQueue.poll();  
			int state_id = dfaStates.get(state_set).intValue();
			// Loop through all the possible transitions
			for (char c = PRINTABLE_CHAR_START_VAL; c <= PRINTABLE_CHAR_END_VAL; ++c) {
				/*
				 * For each transition, perform a move on the state set, and then
				 * an epsilon closure on the state set resulting from the move. 
				 * This leads to a new state that set that will contain all the states
				 * that can be reached by following the given transition from one
				 * or more of the NFAStates in the state_set that is being processed. 
				 */
				NFAStateSet nextState_set = epsilon_closure(move(state_set, c));
				// Check to see that state set is not empty
				if (!nextState_set.isEmpty()) {
					// See if the state set already has a corresponding DFA state
					Integer dfaStateID = dfaStates.get(nextState_set);
					int nextState_id;
					if (dfaStateID == null) {
						/*
						 * The state set does not have a corresponding DFA state,
						 * so create a state in the DFA for this state set 
						 */
						nextState_id = dfa.newDFAState();
						// Add a mapping between this state set and the id of the DFA state
						dfaStates.put(nextState_set, Integer.valueOf(nextState_id));
						/*
						 * Add this state set to the queue, since we know it has never
						 * been seen before because we had to create a DFA state for it.
						 */
						stateQueue.add(nextState_set);

						// Check to see if the start state set has a terminating state
						termState = hasTerminatingState(nextState_set);
						if (termState != null) {
							/*
							 * If it has a terminating state, then set the
							 * corresponding DFA state as terminating, and
							 * store the type of the NFA state in the corresponding
							 * DFA state.
							 */
							dfa.setTerminatingState(nextState_id, termState.getType());
						}
					} else {
						/*
						 * If the state set has been seen before, then simply get out the
						 * id of the DFA state that corresponds to the state set.
						 */
						nextState_id = dfaStateID.intValue();
					}
					
					/*
					 * Add a transition for the given character from the DFA state
					 * for the current state set to the DFA state for the state 
					 * set been by following the transition for the character. 
					 */
					dfa.addTransition(state_id, nextState_id, c);
				}
			}
		}
		return dfa;
	}
	
	/**
	 * This method performs an epsilon closure operation
	 * on a given set of states. The epsilon closure
	 * operation takes in a set of NFA states
	 * and then returns a new set of NFA states which 
	 * are all the states that can be reached by following 
	 * epsilon transitions. This set will always at least
	 * contain the states in the inputed NFA state set.
	 * 
	 * @param set a set of NFAState instances
	 * @return the NFAStateSet found by performing an epsilon-closure on the inputed set
	 */
	private NFAStateSet epsilon_closure(NFAStateSet set) {
		// Create the closure set and the original states to the set
		NFAStateSet closureSet = new NFAStateSet(); 
		closureSet.addStates(set);
		
		/*
		 * Create a queue of states in which we need to follow their
		 * epsilon transitions.
		 */
		Queue<NFAState> closureQueue = new LinkedList<NFAState>();
		// Add the original states to the queue
		closureQueue.addAll(set.getNFAStates());
		// Continue looping till we have processed all the states that exist in the queue
		while (!closureQueue.isEmpty()) {
			// Remove the next state to process
			NFAState state = closureQueue.poll();
			// Loop through the states seen by following the epsilon transition
			for (NFAState e_state: state.getEpsilonTransitions().getNFAStates()) {
				if (!closureSet.hasState(e_state)) {
					/*
					 * If the closure set does not already have a state in the 
					 * epsilon transition set, then add that state to the closure
					 * set as well as the queue, since we must also follow
					 * that states epsilon transition.
					 */
					closureSet.addState(e_state);
					closureQueue.add(e_state);
				}
			}
		}
		return closureSet;
	}
	
	/**
	 * This method performs a move operation
	 * on a set of NFA states. The move operation
	 * takes in a set of NFA states and a character
	 * representing a transition, and then identifies
	 * all the NFA states that can be reached by
	 * following the given character transition
	 * for each state in the set.
	 * 
	 * @param set a set of NFA states
	 * @param c the character to transition on 
	 * @return a set of NFA states that can be reached by following the transition for each state in the inputed set
	 */
	private NFAStateSet move(NFAStateSet set, char c) {
		NFAStateSet moveSet = new NFAStateSet(); // Create the move set
		// Loop through each NFA state in the inputed set
		for (NFAState state: set.getNFAStates()) {
			// Follow the transition from the state in the set for the inputed character
			NFAStateSet transitionStates = state.followTransition(c);
			// Check to see if any states are seen by following the transition
			if (transitionStates != null && !transitionStates.isEmpty()) {
				// Add the states seen by following the transition to the move set
				moveSet.addStates(transitionStates);
			}
		}
		return moveSet;
	}
	
	/**
	 * Checks to see if the set contains a terminating
	 * state. If it does, then the first terminating 
	 * seen is returned. If it doesn't contain a 
	 * terminating state, then null is returned.
	 * 
	 * @param set a set of NFA states
	 * @return the first identified terminating NFAState, or null if no terminating state is seen
	 */
	private NFAState hasTerminatingState(NFAStateSet set) {
		// Loop through states in the set
		for (NFAState state: set.getNFAStates()) {
			if (state.isTerminating()) {
				// If the state is terminating, return it
				return state;
			}
		}
		// If no terminating state was seen, return null
		return null;
	}
	
	/**
	 * Adds the collection of terminating states to 
	 * the NFA. This method assumes that the states
	 * in the collection have already been set to 
	 * terminating. 
	 * 
	 * @param states a collection of terminating NFAStates
	 */
	private void addTerminatingStates(Collection<NFAState> states) {
		terminatingStates.addAll(states);
	}
	
	
	/**
	 * This static method takes in a collection of NFAs 
	 * and then returns a new NFA that is the union
	 * of all the NFAs within the collection.
	 * 
	 * @param nfas the NFAs to union together
	 * @return a new NFA that is the union of all the NFAs in the collection
	 */
	public static NFA union(Collection<NFA> nfas) {
		NFA nfa = new NFA(); // Create the NFA that will be returned
		// Create and set the start state for the new NFA
		NFAState startState = new NFAState();
		nfa.setStartState(startState);
		/*
		 * Loop through each NFA in the collection and
		 * epsilon transition from the start state to the 
		 * start state of the NFA in the collection. Also,
		 * add the terminating states from the NFA in 
		 * the collection to the terminating states list
		 * for the NFA to return
		 */
		for (NFA addNFA: nfas) {
			// Add an epsilon transition from the start state to the NFA in the collection
			startState.addEpsilonTransition(addNFA.getStartState());
			// Add the terminating states from the NFA in the collection to the NFA that will be returned
			nfa.addTerminatingStates(addNFA.getTerminatingStates());
		}
		return nfa;
	}
	
}
