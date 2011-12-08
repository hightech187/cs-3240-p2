package cs3240.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cs3240.regex.automaton.DFATable;

/**
 * This class represent a driver program
 * that will use a table-driven DFA to 
 * analyze a code file for different types
 * of tokens.
 * 
 * @author Dilan Manatunga
 *
 */
public class Driver {
	/**
	 * The file name of the code file
	 */
	private String filename;
	/**
	 * The DFA Table used to identify the type of the tokens in the file 
	 */
	private DFATable dfa;
	/**
	 * A list of the tokens from the code file
	 */
	private ArrayList<Driver.Token> tokens;
	
	/**
	 * The main constructor for the Driver program
	 * that takes in the file name for the code
	 * file as well as the DFA table used to identify
	 * the type of tokens in the code file.
	 * 
	 * @param filename a string containing the file path of the code file
	 * @param dfa a DFA Table which contains token type specifications
	 */
	public Driver(String filename, DFATable dfa) {
		// Set the filename and DFA Table
		this.filename = filename;
		this.dfa = dfa;
		this.tokens = new ArrayList<Driver.Token>(); // Initialize the list for Tokens
	}
	
	/**
	 * Analyzes the contents of the code file and attempt 
	 * to identify the type of token for each of 
	 * the white-space seperated tokens in the file.
	 * 
	 * @throws IOException exception may occur from either file not existing or during reading of the file
	 */
	public void run() throws IOException {
		// Open a new reader for the file
		BufferedReader fileReader = new BufferedReader(new FileReader(filename));

		String line = fileReader.readLine(); // Read the first file of the line
		// Continue processing the file till the end of the file is reached
		while (line != null) {
			int cur_pos = 0; // A variable that points the current scanning location in the loop  
			// Continue scanning the line until the end of the line is reached 
			while (cur_pos < line.length()) {
				// Loop through until a non-whitespace character is found
				while (cur_pos < line.length() &&
						Character.isWhitespace(line.charAt(cur_pos))) {
					++cur_pos;
				}
				
				// Initialize a string builder to hold the valid characters
				StringBuilder tokString = new StringBuilder(10);
				// Loop through until the read of the line is reached or a white-space character is found
				while (cur_pos < line.length()) {
					char cur_char = line.charAt(cur_pos); // Get the character at the current position
					if (Character.isWhitespace(cur_char)) {
						/*
						 *  If the character is whitespace, then increment the current scanning location
						 *  and break out of the loop
						 */
						++cur_pos;
						break;
					} else {
						if (cur_char == '\\') {
							/*
							 * If the character is the escape character, then add the
							 * character at the next position
							 */
							tokString.append(line.charAt(++cur_pos));
						} else {
							// Add the non-whitespace character to the string
							tokString.append(cur_char);
						} 
						++cur_pos; // Increment the current scanning location to the next position
					}
				}
				
				// Convert the built-string to a string
				String tok = tokString.toString();
				/*
				 * If the token string is not empty, then process it
				 * and add the identified token to the list
				 */
				if (!tok.isEmpty()) {
					tokens.add(processString(tok));
				}
			}
			line = fileReader.readLine(); // Read the next line in the file
		}
	}
	
	/**
	 * Takes in a string representing a possible token, and then 
	 * attempts to identify the type of the token using the DFA table. 
	 * It then returns a Token object which contains the value and 
	 * type of the token.
	 * 
	 * If the type of the token could not be identified using the DFA,
	 * the type of the token is set as the string "INVALID".
	 * 
	 * @param value a string representing a possible token
	 * @return a token object containing the value of the token and the type of the token. If a type could not be identified using the DFA, the type will be set as the string "INVALID".
	 */
	public Driver.Token processString(String value) {
		// Get the start and error state IDs for the DFA
		int cur_id = dfa.getStartID();  
		int error_id = dfa.getErrorStateID();
		
		// Loop through the characters in the string
		for (int i = 0; i < value.length(); ++i) {
			// Follow the transition from the given state with the given character
			cur_id = dfa.followTransition(cur_id, value.charAt(i));
			if (cur_id == error_id) {
				// If the followed transition led to the error state, break out of the loop
				break;
			}
		}
		
		// If the current state is the error state, then return the INVALID token
		if (cur_id == error_id) {
			return new Driver.Token(value, "INVALID");
		}
		
		/*
		 * Since current state a valid id, then identify whether the current state 
		 * is terminating, if it is, the type associated with it.
		 */
		String type = dfa.isTerminatingState(cur_id); 
		if (type == null) {
			// If the current state is not a terminating state, then set the type to INVALID
			type = "INVALID";
		}
		// Return a token with the given value and type
		return new Driver.Token(value, type);
	}
	
	/**
	 * Prints the analyzed token data to the console
	 */
	public void printStats() {
		/*
		 *  Loop through the identified tokens and 
		 *  print their representations to the console
		 */
		for (Driver.Token token: tokens) {
			System.out.println(token.toString());
		}
	}
	
	/**
	 * Prints the analyzed token data to a 
	 * file with the given name
	 * 
	 * @param filename the file name for the token data to file
	 */
	public void printStatsToFile(String filename) {
		try {
			// Open a buffered writer
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			/*
			 *  Loop through the identified tokens and 
			 *  print their representations to the file
			 */
			for (Driver.Token token: tokens) {
				writer.write(String.format("%s\n", token));
			}
			writer.close();
		} catch (IOException e) {
			System.err.format("IOException: %s", e);
		}	
	}

	/**
	 * Set the file name of the code file
	 * 
	 * @return the file name of the code file
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Set the file name of the code file
	 * 
	 * @param filename the file name of the code file
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * An private inner-class that is used to 
	 * provide a representation for tokens identified
	 * in the code file. Each token simply has
	 * a value and type associated with it.
	 * 
	 * @author Dilan Manatunga
	 *
	 */
	private class Token {
		/**
		 * The value of the token
		 */
		private String value;
		/**
		 * The type of the token
		 */
		private String type;
		
		/**
		 * The main constructor for Token which creates
		 * a Token with the inputed value and type.
		 * 
		 * @param value the string value for the token
		 * @param type the string type for the token
		 */
		public Token(String value, String type) {
			this.value = value;
			this.type = type;
		}

		
		/**
		 * Returns a string representation of the Token
		 * of the token value and the type associated with
		 * said value.
		 * 
		 * @return the string representation of the Token
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("\"%s\" => %s", value, type);
		}
	}
}
