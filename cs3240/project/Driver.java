package cs3240.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cs3240.regex.automaton.DFATable;
import cs3240.regex.scanner.token.RegexToken;

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
	
	private RegexToken regextoken;
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
	
	public Driver(RegexToken rt, DFATable dfa) {
		this.regextoken = rt;
		this.dfa = dfa;
		this.tokens = new ArrayList<Driver.Token>(); // Initialize the list for Tokens
	}
	
	/**
	 * Analyzes the contents of the code file and attempt 
	 * to identify the type of token for each of 
	 * the white-space separated tokens in the file.
	 * 
	 * @throws IOException exception may occur from either file not existing or during reading of the file
	 */
	public ArrayList<Driver.Token> run() throws IOException, Exception {
		
		if (regextoken != null) {
			Driver.Token candidate = processString(regextoken.getValue());
			if (candidate.type == "INVALID" || candidate.type == "YOKEL") {
				return null;
			}
			throw new Exception("String cannot match regex pattern");
		}
		
		// Open a new reader for the file
		BufferedReader fileReader = new BufferedReader(new FileReader("src/" + filename));

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
				
				int temp_pos = cur_pos; // A variable that serves a similar purpose to cur_pos
				Driver.Token oldCandidate = null; // Candidate for inclusion as a Token 
				
				// Initialize a string builder to hold the valid characters
				StringBuilder tokString = new StringBuilder(10);
				// Loop through until the read of the line is reached or a white-space character is found
				while (temp_pos < line.length()) {
					char cur_char = line.charAt(temp_pos); // Get the character at the current position
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
					
					String tok = tokString.toString();

					// If the token string is not empty, then process it
					if (!tok.isEmpty()) {
						// Identify the token string as a candidate 
						Driver.Token candidate = processString(tok);
						/*
						 * If the token is invalid, check to see if the previous token
						 * was valid. If so, add it to the list of tokens.
						 */
						if (candidate.type == "INVALID") {
							if (oldCandidate != null) {
								tokens.add(oldCandidate);
								cur_pos = temp_pos;
							}
							oldCandidate = null;
							break;
						/*
						 * If the token is valid, and the end of the file/line has been
						 * reached, add candidate to the list of tokens.
						 */
						} else if (temp_pos == line.length() - 1 && candidate.type != "YOKEL") {
							tokens.add(candidate);
							cur_pos = temp_pos;
							break;
						// If the token is acceptable, save as a candidate token.
						} else if (candidate.type != "YOKEL") {
							oldCandidate = candidate;
						}
					}

					++temp_pos; // Increment the token scanning location to the next position

				}
				
				++cur_pos; // Increment the file scanning location to the next position
			}
			line = fileReader.readLine(); // Read the next line in the file
		}
		return tokens;
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
			// If the current state is not a terminating state, then set the type to YOKEL
			type = "YOKEL";
		}
		// Return a token with the given value and type
		return new Driver.Token(value, type);
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
	public class Token {
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

		public String getValue() {
			return value;
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
