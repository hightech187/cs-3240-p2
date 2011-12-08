package cs3240.project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


import cs3240.regex.ast.CharClassCollection;
import cs3240.regex.ast.CharacterSet;
import cs3240.regex.automaton.DFATable;
import cs3240.regex.automaton.NFA;
import cs3240.regex.parser.CharClassParser;
import cs3240.regex.parser.RegexParser;

/**
 * This class represents a Scanner Generator.
 * The Scanner Generator is a program that
 * takes in a lexical specification file that
 * contains definitions for Character Classes
 * and regular expressions that define various
 * Token types. It then will return a DFA Table
 * that can be used to analyze possible tokens.
 * 
 * @author Dilan Manatunga
 *
 */
public class ScannerGen {
	/**
	 * The name of the file containing the lexical specification
	 */
	private String filename;
	/**
	 * A collection of the defined character classes
	 */
	private CharClassCollection charClasses;
	/**
	 * A mapping between the token type and the NFA used to match to that token
	 */
	private HashMap<String, NFA> tokenNFAs;
	/**
	 * A boolean if a parsing error occurred when parsing character class or token definitions
	 */
	private boolean parsingError = false;
	
	/**
	 * The main constructor for a scanner generator
	 * that takes in the file name of lexical 
	 * specification file to process.
	 * 
	 * @param filename the file name of the lexical specification file
	 */
	public ScannerGen(String filename) {
		this.setFilename(filename);
		// Initialize the char class collection and the token to NFAs mapping
		this.charClasses = new CharClassCollection();
		this.tokenNFAs = new HashMap<String, NFA>();
	}

	/**
	 * This method processes the lexical specification 
	 * file and then returns a DFA Table that can be
	 * used to identify tokens that are defined
	 * in the lexical specification file
	 * 
	 * @return a DFA Table that can be used to identify tokens described in the file
	 * @throws IOException occurs if file not found or if error occurs when reading file
	 * @throws Exception occurs if no token definition were identified in the file, or if errors occurred when processing file
	 */
	public DFATable run() throws IOException, Exception {
		processFileDefinitons(); // Process the character class and token definitions in file
		if (tokenNFAs.isEmpty()) {
			throw new Exception("No Token Definitions were Identified in the File");
		}
		if (parsingError) {
			throw new Exception("There were errors were parsing the file.\nScanner Geneartion Terminatng");
		}
		return createDFA(); // Create and return the DFA Table
	}

	/**
	 * This method processes the file containing the 
	 * lexical specification for Character Class
	 * definitions and Token definitions.
	 * 
	 * @throws IOException occurs if file not found or if error occurs when reading file
	 */
	public void processFileDefinitons() throws IOException {
		// Open the file that is to be scanned
		BufferedReader fileReader = new BufferedReader(new FileReader(filename));
		
		String line = fileReader.readLine(); // Read the first line from the file
		
		boolean charClassDefSection = false; // If in the character definition section of the file
		boolean tokenDefSection = false; // If in the token definition section of the file

		int line_num = 1; // The line number of the line being scanned
		while (line != null) {
			int cur_pos = 0;
			// Trim the beginning white space from the line
			while (cur_pos < line.length() && Character.isWhitespace(line.charAt(cur_pos))) {
				cur_pos++;
			}
			line = line.substring(cur_pos);
			// Check to see if first two characters are the %% markers for section definitions
			if (line.length() >= 2 && line.charAt(0) == '%' && line.charAt(1) == '%') {
				if (!charClassDefSection) {
					// If char class section has not been found, then we are now in char class section 
					System.out.println("- Parsing Character Class Definitions...");
					charClassDefSection = true;
				} else {
					// Char class section has been found, so we are now in token definition section
					System.out.println("- Parsing Token Definitions...");
					tokenDefSection = true;
					charClassDefSection = false;
				}
			} else if (charClassDefSection) {
				// In character class section, so process the line as a character class definition
				if (!line.isEmpty()) {
					processCharClassDef(line, line_num);
				}
			} else if (tokenDefSection) {
				// In the token definition section, so process the line as a token definition
				if (!line.isEmpty()) {
					processTokenDef(line, line_num);
				}
			}
			// Read the next line and increment line number counter
			line = fileReader.readLine(); 
			line_num++;
		}
	}
	
	
	/**
	 * This method process a line possibly containing a valid character class 
	 * definition. If the definition is valid, it will extract
	 * the name of character class and its character set definition. 
	 * It will then add that class to the Character Class collection.
	 * 
	 * @param definition a string containing the definition of the class in the form of "$Class     [definition]"
	 * @param line_num the line number the definition was found at
	 */
	private void processCharClassDef(String definition, int line_num) {
		
		int startInd = 0;
		// Loop through the string until the class definition indicator is found
		while (startInd < definition.length() && definition.charAt(startInd) != '$') {
			if (!Character.isWhitespace(definition.charAt(startInd))) {
				// Found non-whitespace character before $
				parsingError = true;
				System.err.format("Line %d: Character Class definitions must be of form \"$<CLASS-NAME> <CHARACTER-SET>\"\n", line_num);
				return;
			}
			startInd++;
		}
		// If the end of the line was reached without finding the '$', then just return out
		if (startInd >= definition.length()) {
			return;
		}
		
		int endInd = startInd+1;
		
		// Find the first whitespace after the class name indicator character '$'
		while (endInd < definition.length()  && !Character.isWhitespace(definition.charAt(endInd))) {
			endInd++;
		}
		
		
		// Identify the character class name
		String name = definition.substring(startInd+1, endInd).trim();
		if (name.isEmpty()) {
			// The name of the class is empty, so a parsing error has occurred
			parsingError = true;
			System.err.format("java.lang.Exception: Line %d: Character Class definition does not have a valid class name\n", line_num);
			return;
		}
		// The character set definition will be the rest of the string
		definition = definition.substring(endInd);
		startInd = 0;
		// Trim any initial whitespace from the front of the string
		while (startInd < definition.length() && Character.isWhitespace(definition.charAt(startInd))) {
			startInd++;
		}
		
		if (startInd >= definition.length()) {
			// There is no character set definition for the class, so a parsing error has occurred
			parsingError = true;
			System.err.format("java.lang.Exception: Line %d: Character Class does not have a set definition\n", line_num);
			return;
		}
		definition = definition.substring(startInd); 

		// Create the character class parser
		CharClassParser parser = new CharClassParser(name, definition, charClasses, line_num);

		try {
			// Parse the definition
			CharacterSet set = parser.parseDefinition();
			charClasses.addCharClass(name, set);
		} catch (Exception e) {
			parsingError = true;
			// Print exception out
			System.err.println(e);
		}
	}
	
	/**
	 * This method process a line that possibly contains a valid token
	 * definition. If the definition is valid, it will extract
	 * the name of token and its regular expression definition. It
	 * then creates an NFA representing the regular expression 
	 * definition, and then adds the token and its corresponding
	 * NFA to the mapping table.
	 * 
	 * @param definition a string containing the definition of the token in the form of "$Token     regex-definition"
	 * @param line_num the line number the definition was found at
	 */
	private void processTokenDef(String definition, int line_num) {
		
		int startInd = 0;
		// Loop through the string until the token definition indicator is found
		while (startInd < definition.length() && definition.charAt(startInd) != '$') {
			if (!Character.isWhitespace(definition.charAt(startInd))) {
				// Found non-whitespace character before $
				parsingError = true;
				System.err.format("java.lang.Exception: Line %d: Token definitions must be of form \"$<TOKEN-NAME> <REGEX>\"\n", line_num);
				return;
			}
			startInd++;
		}
		// If the end of the line was reached without finding the '$', then just return out
		if (startInd >= definition.length()) {
			return;
		}
		
		int endInd = startInd+1;
		
		// Find the first whitespace after the token name indicator character '$'
		while (endInd < definition.length()  && !Character.isWhitespace(definition.charAt(endInd))) {
			endInd++;
		}
		
		
		// Identify the token  name
		String tokenName = definition.substring(startInd+1, endInd).trim();
		if (tokenName.isEmpty()) {
			// The name of the token is empty, so a parsing error has occurred
			parsingError = true;
			System.err.format("Line %d: Token definition does not have a valid token name\n", line_num);
			return;
		}
		// The character set definition will be the rest of the string
		definition = definition.substring(endInd);
		startInd = 0;
		// Trim any initial whitespace from the front of the string
		while (startInd < definition.length() && Character.isWhitespace(definition.charAt(startInd))) {
			startInd++;
		}
		
		if (startInd >= definition.length()) {
			// There is no regular expression definition for the token, so a parsing error has occurred
			parsingError = true;
			System.err.format("java.lang.Exception: Line %d: Token does not have a regular expression definition\n", line_num);
			return;
		}
		definition = definition.substring(startInd); 
		// Create a regular expression parse for the definition
		RegexParser parser = new RegexParser(definition, charClasses, line_num);
		
		try {
			// Parse the regex and get the corresponding NFA
			NFA regexNFA = parser.parseRegex();
			// Set the terminating state type for this NFA to the token Name
			regexNFA.setTerminatingStatesType(tokenName);
			// Add the token name and its NFA to the mapping table
			tokenNFAs.put(tokenName, regexNFA);
		} catch (Exception e) {
			// An exception has occurred so some parsing error has occurred
			parsingError = true;
			// Print exception to console
			System.err.println(e);
		}
	}
	
	/**
	 * Combines the NFAs that describe each token and then
	 * creates one overall DFA Table that can be used to
	 * identify if a string matches to one of the defined 
	 * tokens.
	 * 
	 * @return a DFA Table that can be used to identify if a string matches to one of the defined tokens
	 */
	public DFATable createDFA() {
		// Combine the NFAs into one overall NFA
		NFA combinedNFA = NFA.union(tokenNFAs.values());
		// Convert the NFA to a DFA and return it
		return combinedNFA.toDFA();
	}
	
	/**
	 * Prints out the identified character classes 
	 * to the console.
	 */
	public void printCharClasses() {
		// Loop through the character classes and print them to the command line
		for (CharacterSet c: charClasses.getCharClasses()) {
			System.out.println(c.toString());
		}
	}

	/**
	 * Returns the file name of the lexical specification file
	 * 
	 * @return the file name of the lexical specification file
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Set the file name for the lexical specification file
	 * 
	 * @param filename the file name of the lexical specification file
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
