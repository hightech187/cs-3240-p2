package cs3240.regex.parser;
import cs3240.regex.ast.CharClassCollection;
import cs3240.regex.ast.CharacterSet;
import cs3240.regex.scanner.RegexScanner;
import cs3240.regex.scanner.token.RegexToken;
import cs3240.regex.scanner.token.RegexTokenType;


/**
 * This class represents a Character Class
 * definition parser that will scan 
 * a Character Set definition and then
 * construct the corresponding character
 * set if the definition is valid.
 * 
 * @author Dilan Manatunga
 *
 */
public class CharClassParser {
	/**
	 * A string containing character set regular expression definition 
	 */
	private String regex;
	/**
	 * The string name for the character class
	 */
	private String className;
	/**
	 * The scanner for the regular expression definition
	 */
	private RegexScanner scanner;
	/**
	 * The collection of currently defined character classes
	 */
	private CharClassCollection charClasses;
	/**
	 * The current token that has been received from the scanner
	 */
	private RegexToken token;

	/**
	 * The main constructor for the CharClassParser class
	 * that creates a parser for the given class name
	 * and its corresponding definition.
	 * 
	 * @param name the string name of the character class
	 * @param regex the string character set regular expression definition for the class
	 * @param charClasses the currently defined set of Character Class
	 * @param line_num the line number the definition occurred at
	 */
	public CharClassParser(String name, String regex, CharClassCollection charClasses, int line_num) {
		this.regex = regex;
		this.className = name;
		// Create a scanner for the character set regular expression definition
		this.scanner = new RegexScanner(regex, charClasses.getClassNames(), line_num);
		this.charClasses = charClasses;
	}

	/**
	 * This method parses the character set definition
	 * and returns a character set made up of only the
	 * characters specified by the definition, as well
	 * as no other invalid characters after the definition.
	 * 
	 * @return a character set that corresponds to the definition
	 * @throws Exception occurs if non-ASCII printable character used to define character set or if definition not syntactically correct 
	 */
	public CharacterSet parseDefinition() throws Exception {
		// Call on the RE() method to parse the character regular expression
		CharacterSet set = RE();
		// Match to the end of string 
		match(RegexTokenType.EOS);
		// Set the name for the character set with the class name
		set.setName(className);
		return set;
	}

	/**
	 * This method parses the character set definition 
	 * and returns a character set made up of only the
	 * characters specified by the definition.
	 * 
	 * @return a character set that corresponds to the definition
	 * @throws Exception occurs if non-ASCII printable character used to define character set or if definition not syntactically correct 
	 */
	private CharacterSet RE() throws Exception {
		// The first character should be the open bracket
		match(RegexTokenType.OPEN_BRACKET);
		// Indicate the scanner should be in SET_CHAR scanning mode
		scanner.setRegexCharacterType(RegexTokenType.SET_CHAR);
		scanner.ignoreWhitespace(false); // whitespace should not be ignored
		return set(); // Call the set() method to parse the set definition
	}

	/**
	 * This method identifies whether the character set 
	 * definition is defined using a negative or regular set.
	 * It then calls the appropriate method depending on the
	 * set definition type.
	 *  
	 * @return a character set that corresponds to the definition
	 * @throws Exception occurs if non-ASCII printable character used to define character set or if definition not syntactically correct 
	 */
	private CharacterSet set() throws Exception {
		// Check to see if set is defined as a negative set
		if (peek() == RegexTokenType.NEGATIVE_SET) {
			return negativeSet(); // Identify the negative set 
		} else {
			CharacterSet set = setItems(); // Identify the items in the set
			match(RegexTokenType.CLOSE_BRACKET); // Match to the closing bracket
			// Set the scanner back ignoring white-space and RE_CHAR mode
			scanner.setRegexCharacterType(RegexTokenType.RE_CHAR);
			scanner.ignoreWhitespace(true);
			return set;
		}
	}

	/**
	 * This method identifies the set when defined
	 * using the negative set notation of:
	 *  ^set1] IN char_class or ^set1] IN [set2]
	 *  
	 * @return a character set that corresponds to the definition
	 * @throws Exception occurs if non-ASCII printable character used to define character set or if definition not syntactically correct 
	 */
	private CharacterSet negativeSet() throws Exception { 
		match(RegexTokenType.NEGATIVE_SET); // Match to the negative set indicator ^
		// Identify the set of characters to exclude
		CharacterSet negSet = setItems(); 

		match(RegexTokenType.CLOSE_BRACKET);
		// Set the scanner back ignoring white-space and RE_CHAR mode
		scanner.setRegexCharacterType(RegexTokenType.RE_CHAR);
		scanner.ignoreWhitespace(true);

		match(RegexTokenType.IN_OP); // match to the IN keyword
		// Identify the positive set
		CharacterSet positiveSet = negativeSet_tail();

		// Exclude the negative set characters from the positive set
		positiveSet.excludeSet(negSet);

		return positiveSet;
	}

	/**
	 * This method identifies set2 for negative
	 * set definitions of the following form: 
	 *  ^set1] IN char_class or ^set1] IN [set2]
	 *  
	 * @return a character set that corresponds to the second set in a negative set definition
	 * @throws Exception occurs if non-ASCII printable character used to define character set or if definition not syntactically correct 
	 */
	private CharacterSet negativeSet_tail() throws Exception {
		CharacterSet set;
		switch (peek()) {
		case CHAR_CLASS:
			/* 
			 * If next token is a character class, then
			 * return the set representing a 
			 */
			RegexToken token = match(RegexTokenType.CHAR_CLASS);
			// Get the set for the character class name stored in the token
			set = charClasses.getCharClassSet(token.getValue());
			// Return a copy of the set
			return set.copy();
		case OPEN_BRACKET:
			/*
			 * If next token is an open bracket, then parse
			 * the set definition in the same way a positive set
			 * definition is parsed.
			 */
			match(RegexTokenType.OPEN_BRACKET);
			// Indicate the scanner should be in SET_CHAR scanning mode
			scanner.setRegexCharacterType(RegexTokenType.SET_CHAR);
			scanner.ignoreWhitespace(false); // Tell the scanner to ignore white space

			set = setItems(); // Identify the set

			match(RegexTokenType.CLOSE_BRACKET);
			// Set the scanner back ignoring white-space and RE_CHAR mode
			scanner.setRegexCharacterType(RegexTokenType.RE_CHAR);
			scanner.ignoreWhitespace(true);
			return set;
		case INVALID_CHAR_CLASS:
			throw new Exception(String.format("Line %d (col %d): Invalid Character Class Name", this.token.getLineNumber(), this.token.getLinePosition()));
		default:
			throw new Exception(String.format("Line %d (col %d): Character Class Definition Parsing Error has occurred", this.token.getLineNumber(), this.token.getLinePosition()));
		}
	}

	/**
	 * This method identifies the set of characters
	 * defined within the bracket-set-notation for a regular
	 * expression.
	 * 
	 * set  = [setItems]
	 * setItems = setItem setItems
	 * setItem = character OR character_range
	 * 
	 * @return a character set that corresponds to the definition
	 * @throws Exception occurs if non-ASCII printable character used to define character set or if definition not syntactically correct 
	 */
	private CharacterSet setItems() throws Exception {
		// Indicate the scanner should be in SET_CHAR scanning mode
		scanner.setRegexCharacterType(RegexTokenType.SET_CHAR);
		scanner.ignoreWhitespace(false);
		// Create the character set
		CharacterSet set = new CharacterSet();
		// Keep identifying set items till next token is not a set char
		while (peek() == RegexTokenType.SET_CHAR) {
			// Identify the the set item and add it to the set
			setItem(set);
		}
		// Set the scanner back ignoring white-space and RE_CHAR mode
		scanner.setRegexCharacterType(RegexTokenType.RE_CHAR);
		scanner.ignoreWhitespace(true);
		return set;
	}

	/**
	 * This method identifies a character or character 
	 * range definition in the set definition, and then adds
	 * the corresponding characters to the set.
	 * 
	 * @param set the character set to add new characters too
	 * @throws Exception occurs if non-ASCII printable character used to define character set or if definition not syntactically correct 
	 */
	private void setItem(CharacterSet set) throws Exception {
		// Identify the first character in the set item
		RegexToken token = match(RegexTokenType.SET_CHAR);
		char c1 = token.getValue().charAt(0);
		if (peek() == RegexTokenType.RANGE_OP) {
			/*
			 * If the next character is the range operator,
			 * then identify the end character in the range,
			 * and add those characters to the set.
			 */
			match(RegexTokenType.RANGE_OP);
			token = match(RegexTokenType.SET_CHAR);
			char c2 = token.getValue().charAt(0);
			set.addCharacterRange(c1, c2);
		} else {
			// Only single character defined, so add character to set
			set.addCharacter(c1);
		}
	}

	/**
	 * Returns the type for the currently held token
	 *  
	 * @return the type for the current token
	 */
	private RegexTokenType peek() {
		// If the token is not defined, get the next token
		if (token == null) {
			getNextToken();
		}
		return token.getType();
	}

	/**
	 * This method checks to see if the 
	 * currently held token's type matches to 
	 * the expected input type. If it does, the 
	 * token is returned. If it doesn't, an 
	 * exception is thrown. 
	 * 
	 * @param type the type the currently held token is expected to be
	 * @return the currently held RegexToken
	 * @throws Exception if the input type and token type do not match
	 */
	private RegexToken match(RegexTokenType type) throws Exception {
		// If the token is not defined, get the next token
		if (token == null) {
			getNextToken();
		}
		// Check to see if token types match
		if (token.getType() == type) {
			RegexToken ret_token = token;
			token = null;
			return ret_token;
		} else {
			// throw an exception since they don't match
			throw new Exception(String.format("Line %d (col %d): Expecting %s NOT %s", token.getLineNumber(),token.getLinePosition(), RegexTokenType.getTokenDescription(type), RegexTokenType.getTokenDescription(token)));
		}
	}

	/**
	 * Calls the scanner to get the next token
	 */
	private void getNextToken() {
		token = scanner.getToken(); // Get the next token from the scanner
	}

	/**
	 * Returns the character set regular expression definition
	 * 
	 * @return the character set definition
	 */
	public String getDefinition() {
		return regex;
	}

	/**
	 * Set a character set regular expression definition
	 * 
	 * @param regex a character set regular expression definition to set 
	 */
	public void setDefinition(String regex) {
		this.regex = regex;
	}

	/**
	 * Returns the name of the character class
	 * 
	 * @return the name of the character class
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Set the name for the character class
	 * 
	 * @param className a string for the name of the character class
	 */
	public void setClassName(String className) {
		this.className = className;
	}
}
