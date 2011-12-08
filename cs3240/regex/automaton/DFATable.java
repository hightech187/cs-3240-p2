package cs3240.regex.automaton;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


// TODO: Auto-generated Javadoc
/**
 * This class represents a Deterministic
 * Finite Automaton (DFA) as table indexed
 * by the state id and the transition 
 * character. The class contains the necessary 
 * methods to create a new DFA, traverse the
 * DFA from a given state, and for minimizing
 * the DFA to its minimal state.
 * 
 * @author Dilan Manatunga
 *
 */
public class DFATable {
	
	/** A constant for the number of ASCII printable characters. */
	private static final int NUM_PRINTABLE_CHARS = 95;
	
	/** The ASCII value of the first ASCII printable character. */
	private static final char PRINTABLE_CHAR_START_VAL = 32;
	
	/** The ASCII value of the last ASCII printable character. */
	private static final char PRINTABLE_CHAR_END_VAL = 126;

	/** The table. */
	private ArrayList<int[]> table;
	
	/** The id for the start state of the DFA. */
	private int start_id = 0;
	
	/** The id for the error state of the DFA. */
	private int error_state_id = -1;
	
	/** The next id value to use when creating a DFA state. */
	private int next_id = start_id;
	/**
	 * A mapping between the terminating state ids and a string data associated with 
	 * the terminating state. This string will typically be the type of the token
	 * if it reaches that state in the DFA.
	 */
	private HashMap<Integer, String> terminatingStates;
	
	/** The character to use to separate states and transitions when printing the DFA to a string or file. */
	private char delimiter = '|';
	
	/** The character to use to represent transitions that do not exist for a given state, or transitions to the error state. */
	private char empty_space = '-';

	/**
	 * The base DFATable constructor that initializes
	 * the table, as well as a list of the terminating
	 * states and the data associated with each terminating
	 * state.
	 */
	public DFATable() {
		this.table = new ArrayList<int[]>();  // Initialize the table
		this.terminatingStates = new HashMap<Integer, String>(); // Initialize the terminating states map
	}
	
	/**
	 * Creates a new state in the DFA. The newly created state 
	 * will have all of its transitions leading to the error 
	 * state by default. 
	 * 
	 * @return the integer id for the newly created state
	 */
	public int newDFAState() {
		// Create the array for transitions
		int[] transitions = new int[NUM_PRINTABLE_CHARS];
		// For each transition, set the destination state as the error state
		for (int i = 0; i < NUM_PRINTABLE_CHARS; ++i) {
			transitions[i] = error_state_id;
		}
		this.table.add(next_id, transitions); // Add the state to the table
		// Return the id of the state, and increment the next_id to the next valid value
		return next_id++; 
	}

	/**
	 * Adds a transition in the DFA from the start state to the 
	 * end state for the inputed character.
	 * 
	 * @param start_id an integer representing the start state id
	 * @param dest_id an integer representing the destination state id
	 * @param transition the character representing the transition from the start state to the end state
	 */
	public void addTransition(int start_id, int dest_id, char transition) {
		int[] startState = table.get(start_id); // Get the transitions array for the start state id
		/*
		 *  Shift the given character to its matching index, 
		 *  and then set the transition for that character to
		 *  the destination id.
		 */
		startState[transition-PRINTABLE_CHAR_START_VAL] = dest_id; 
	}
	
	/**
	 * Sets the given state as a terminating state and
	 * associates the given string data with the state.
	 * 
	 * @param state_id the integer id of the state to set as terminating
	 * @param data the string data to associate with the state
	 */
	public void setTerminatingState(int state_id,  String data) {
		// Add the terminating state and its data to the HashMap
		terminatingStates.put(Integer.valueOf(state_id), data);
	}
	
	/**
	 * Checks to see if the inputed state is a terminating state, 
	 * and if it is, it then returns the data associated with the 
	 * terminating state. If the state is not terminating, then 
	 * null is returned.
	 * 
	 * @param state_id the id of the state to check if it is terminating
	 * @return the data associated with the state if it is terminating, or null if the state is not terminating
	 */
	public String isTerminatingState(int state_id) {
		/*
		 * Get the data associated with the terminating state if it is in the mapping.
		 * If it is not, the HashMap will automatically return null
		 */
		return terminatingStates.get(Integer.valueOf(state_id));
	}

	/**
	 * Returns the next state found by following the transition 
	 * for the inputed character from the inputed state.
	 * 
	 * @param state_id the id of the a state in the DFA to transition from
	 * @param c the character transition to follow
	 * @return an integer for the id of state seen by following the transition
	 */
	public int followTransition(int state_id, char c) {
		// If the given state is the error state, simple return back the error state
		if (state_id == error_state_id) {
			return error_state_id;
		}
		// If not an error state, then get the transitions for the inputed state
		int[] transitions = table.get(state_id);
		// Get the id of the transition state
		return transitions[c-PRINTABLE_CHAR_START_VAL];
	}

	/**
	 * Returns the start state id for the DFA table.
	 *
	 * @return the start state id
	 */
	public int getStartID() {
		return start_id;
	}
		
	/**
	 * Returns the error state id for the DFA table.
	 *
	 * @return the error state id
	 */
	public int getErrorStateID() {
		return error_state_id;
	}

	/**
	 * Returns the size of the table, which
	 * is also the number of states in the
	 * table.
	 * 
	 * @return the size of the table/number of states in the table
	 */
	public int getTableSize() {
		return table.size();
		
	}
	
	/**
	 * Returns the minimal DFA for the DFA Table.
	 * 
	 * @return the minimal DFA of the current DFA Table
	 */
	public DFATable toMinimalDFA() {
		int num_states = getTableSize(); // Identify the number of states (N) in the DFA
		/*
		 * Create an NxN table of booleans in the DFA, where if the index i, j
		 * in the table is true, then that means the state i and state j are 
		 * considered to be distinct.
		 */
		boolean isDistinct[][] = new boolean[num_states][num_states];
		
		// Loop through the states in the  in the DFA table
		for (int i = 0; i < num_states; ++i) {
			// Check to see if the state i is terminating
			String iTermData = isTerminatingState(i);
			// Loop through the states with an id greater than state i
			for (int j = i+1; j < num_states; ++j) {
				// Check to see if state j is terminating
				String jTermData = isTerminatingState(j);
				if (iTermData == null && jTermData != null) {
					/*
					 *  If state i is not terminating and state j is terminating,
					 *  then states i and j are distinct
					 */
					isDistinct[i][j] = true;
					isDistinct[j][i] = true;
				} else if (jTermData == null || !iTermData.equals(jTermData)) {
					/*
					 * At this stage, we know sate i is terminating, so if state j 
					 * is not terminating or if state i and state j contain different
					 * data, then states i an j are distinct 
					 */
					isDistinct[i][j] = true;
					isDistinct[j][i] = true;
				}
			}
		}
		
		boolean changes; // A variable to hold whether any changes have been made to the table or not
		/*
		 *  Loop through and attempt to identify distinct states until no 
		 *  changes have been made to the isDistinct table 
		 */
		do {
			changes = false; // Set any changes made to false
			// Loop through the states in the table
			for (int i = 0; i < num_states; ++i) {
				// Loop through the staes with an id greater than stae i
				for (int j = i+1; j < num_states; ++j) {
					if (!isDistinct[i][j]) {
						/*
						 *  Note: To be efficient we could have moved the transitions loop outside
						 *	the j-states loop in-order to only identify i_trans once
						 */
						// Loop through all the possible transition characters
						for (char c = PRINTABLE_CHAR_START_VAL; c <= PRINTABLE_CHAR_END_VAL; ++c) {
							// Follow the transition for character c from both states and j
							int i_trans = followTransition(i, c); 
							int j_trans = followTransition(j, c);
							// Check to see the states after transition are unique
							if (i_trans != j_trans) {
								/*
								 * The two states i,j are unique if either:
								 * 	1. the i transition led to an error state
								 *  2. the j transition led to an error state
								 *  3. the state seen by following the i transition and the 
								 *     state seen by following the j transition are distinct
								 *  
								 *  Note: we know both transitions can't lead to an error state
								 *  since i_trans is not equal to j_trans at this pint
								 */
								if (i_trans == error_state_id || j_trans == error_state_id || isDistinct[i_trans][j_trans]) {
									// Set states i and j to being distinct
									isDistinct[i][j] = true;
									isDistinct[j][i] = true;
									changes = true; // Indicate a change has been made to the able
								}
							}
						}
					}
				}
			}
		} while (changes);
		
		boolean[] beenMerged = new boolean[num_states]; // A boolean array indicating if the given state has been merged
		

		DFATable minimalDFA = new DFATable(); // Create the new minimal DFA
		
		 // A mapping that maps a state id in the original DFA to its corresponding state id in the minimal DFA
		int[] origToNewidtable = new int[num_states];
		 // A mapping that maps a state id in the minimal DFA to its corresponding state id in the original DFA
		int[] newidtoOrigTable = new int[num_states];
		int minimal_numStates = 0; // The number of states in the minimal DFA
		// Loop through the states in the original DFA
		for (int i = 0; i < num_states; ++i) {
			if (!beenMerged[i]) {
				// If the state has not been merged, add it to the minimal DFA
				int new_id = minimalDFA.newDFAState();
				/*
				 *  Check to see if the state is terminating, and if so set 
				 *  it as terminating in the minimal DFA
				 */
				String data = isTerminatingState(i);
				if (data != null) {
					minimalDFA.setTerminatingState(new_id, data);
				}
				
				// Save that the new_id in the minimal DFA corresponds to state i in the original DFA
				newidtoOrigTable[new_id] = i; 
				++minimal_numStates;
				// Loop through the original states
				for (int j = 0; j < num_states; ++j) {
					/*
					 *  If states i and j are not distinct, then state
					 *  j also correspond to the newly created state in
					 *  the minimal DFA
					 */
					if (!isDistinct[i][j]) {
						beenMerged[j] = true; // Indicate state j has been added to minimal DFA
						origToNewidtable[j] = new_id; // Save that state j translates to the new_id in the minimal DFA
					}
				}
			}
		}
		
		// Loop through the states in the minimal DFA
		for (int i = 0; i < minimal_numStates; ++i) {
			// Identify which state in the original DFA the minimal DFA state corresponds to
			int orig_id = newidtoOrigTable[i];
			// Loop through the possible character transition values
			for (char c = PRINTABLE_CHAR_START_VAL; c <= PRINTABLE_CHAR_END_VAL; ++c) {
				// Follow the transition for the given character c
				int next_id = followTransition(orig_id, c);
				if (next_id != error_state_id) {
					/*
					 *  If the following state is not an error state, then identify
					 *  the minimal DFA state that corresponds to the original DFA state
					 *  that is met when following the transition in the original DFA
					 */
					int new_Nextid = origToNewidtable[next_id];
					// Add the transition to the minimal DFA
					minimalDFA.addTransition(i, new_Nextid, c);
				}
			}
		}
		
		return minimalDFA;
	}
	
	/**
	 * Returns a string representation of the DFA.
	 * 
	 * @return the string representation of the DFA
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// Create a new string builder and begin it with an empty space
		StringBuilder builder = new StringBuilder();
		builder.append(empty_space).append(delimiter); 
		
		// Print out all the possible character transitions in the DFA as the header row
		for (char c = PRINTABLE_CHAR_START_VAL; c <= PRINTABLE_CHAR_END_VAL; ++c) {
			builder.append(c).append(delimiter);
		}
		builder.append("TERM_DATA\n");
		
		// Loop through the states in the DFA
		for (int i = start_id; i < table.size(); ++i) {
			builder.append(i).append(delimiter); // Print the state id
			int[] transitions = table.get(i); // Get the transitions for the given state
			// Loop through all the possible transitions
			for (int j = 0; j < NUM_PRINTABLE_CHARS; ++j) {
				int next_state = transitions[j]; // Get the state seen by following the transition
				if (next_state == error_state_id) {
					// If the next state is an error state, print out an empty space
					builder.append(empty_space).append(delimiter);
				} else {
					// If the next state is not an error state, print out its id
					builder.append(next_state).append(delimiter);
				}
			}
			// Identify if the current state is terminating
			String data = isTerminatingState(i);
			if (data == null) {
				// If it is not terminating, print out an empty space
				builder.append(empty_space).append("\n");
			} else {
				/*
				 *  If it is terminating, print out the the data associated 
				 *  with the terminating state.
				 */
				builder.append(data).append('\n');
			}
		}
		return builder.toString();
	}
	
	/**
	 * Print the text representation of the DFA
	 * to the specified file.
	 * 
	 * @param filename the name of the file to print the DFA representation to
	 */
	public void printToFile(String filename) {
		try {
			// Open a buffered writer
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(this.toString()); // Write out the string representation of the DFA
			writer.close();
		} catch (IOException e) {
			System.err.format("IOException: %s", e);
		}	
	}
}
