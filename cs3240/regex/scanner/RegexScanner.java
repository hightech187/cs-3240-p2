package cs3240.regex.scanner;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import cs3240.regex.scanner.token.RegexToken;
import cs3240.regex.scanner.token.RegexTokenType;

/**
 * This class represents a scanner
 * that will scan a string regular 
 * expression and return the Tokens
 * that make up the expression.
 * 
 * @author Dilan Manatunga
 *
 */
public class RegexScanner {
	/**
	 * The string regular expression to scan
	 */
	private String regex;
	/**
	 * A collection of the names of the valid Character Classes
	 */
	private Collection<String> charClasses;
	/**
	 * A mapping containing regular expression keywords and their types
	 */
	private HashMap<String, RegexTokenType> keywords;
	
	private HashMap<String, RegexTokenType> regex_keywords;
	
	/**
	 * The type for the characters when scanning, that allows the scanner
	 * to know what should be a special character or not (should only be RE_CHAR or SET_CHAR)
	 */
	private RegexTokenType regexCharType = RegexTokenType.RE_CHAR;
	/**
	 * A boolean on whether to ignore whitespace when scanning
	 */
	private boolean ignoreWhitespace = true;
	/**
	 * A boolean indicating if the end of file has been reached
	 */
	private boolean EOS = false;
	/**
	 * An integer indicating the current scanning location
	 */
	private int curPos = 0;
	
	private int curPosInLine = 0;
	
	/**
	 * An integer indicating the line number for the string
	 */
	private int line_num = 0;
	
	/**
	 * A queue that contains tokens that have already been identified
	 */
	private Queue<RegexToken> tokenBuffer = new LinkedList<RegexToken>();
	
	
	private boolean inRegex = false;
	private boolean endRegexOnQuote = true;
	
	
		
	
	/**
	 * The base constructor for the RegexScanner that takes in the
	 * regular expression to scan and the collection of valid
	 * character class names.
	 * 
	 * @param regex a string regular expression to scan
	 * @param charClasses a collection of strings which are the names of the valid character classes
	 * @param line_num the line number the string occurs at
	 */
	public RegexScanner(String regex) {
		this.regex = regex;
		this.line_num = 1;
		// Initialize the keywords mapping, and add the IN keyword
		this.keywords = new HashMap<String, RegexTokenType>();
		this.regex_keywords = new HashMap<String, RegexTokenType>();
		
		this.keywords.put("(", RegexTokenType.OPEN_PAR);
		this.keywords.put(")", RegexTokenType.CLOSE_PAR);
		this.keywords.put("=", RegexTokenType.EQUALS_OP);
		this.keywords.put("#", RegexTokenType.POUND_OP);
		this.keywords.put(">!", RegexTokenType.PIPE_OP);
		this.keywords.put(",", RegexTokenType.COMMA);
		this.keywords.put(";", RegexTokenType.SEMICOLON);
		this.keywords.put("begin", RegexTokenType.BEGIN_OP);
		this.keywords.put("end", RegexTokenType.END_OP);
		this.keywords.put("replace", RegexTokenType.REPLACE_OP);
		this.keywords.put("recursivereplace", RegexTokenType.RECURSIVE_REPLACE_OP);
		this.keywords.put("union", RegexTokenType.UNION_OP);
		this.keywords.put("inters", RegexTokenType.INTERS_OP);
		this.keywords.put("print", RegexTokenType.PRINT_OP);
		this.keywords.put("with", RegexTokenType.WITH_OP);
		this.keywords.put("in", RegexTokenType.IN_OP);
		this.keywords.put("find", RegexTokenType.FIND_OP);
		this.keywords.put("diff", RegexTokenType.DIFF_OP);
		this.keywords.put("maxfreqstring", RegexTokenType.MAXFREQSTRING_OP);
		
		this.regex_keywords.put("IN", RegexTokenType.IN_OP);
	}
	
	/**
	 * Returns the next ASCII-STR, not including any quotes
	 * @return next available ASCII-STR
	 */
	public RegexToken getASCIIToken() {
		
		int beginningPos = curPosInLine;
		
		// If the end of the string has been reached, return an EOS token
		if (EOS) {
			int oldPosInLine = curPosInLine;
			curPosInLine = 0;
			return new RegexToken(RegexTokenType.EOS, line_num++, oldPosInLine);
		}
		
		RegexToken token = null;		
		char cur_char;
		StringBuilder tokenValue = new StringBuilder();
		
		boolean done = false;
		while (!done) {
			cur_char = regex.charAt(curPos);
			if (cur_char == '\\') {
				if (regex.length() > curPos + 1) {		// at least one character following
					if (regex.charAt(curPos+1) == '"') {
						tokenValue.append("\"");
						curPosInLine += 2;
						curPos += 2;					// move ahead of both \ and "
					} else {							// nothing being escaped, simply add \
						tokenValue.append("\\");
						curPos++;
						curPosInLine++;
					}
				} else {
					this.EOS = true;
					curPos++;			// TODO: increment curPos here? vvv I think he does down there...
					int oldPosInLine = curPosInLine;
					curPosInLine++;
					return new RegexToken(RegexTokenType.INVALID_ESCAPE, line_num, oldPosInLine);
				}
			} else {
				if (cur_char == '"') {		// done building ASCI-STR
					token = new RegexToken(tokenValue.toString(), RegexTokenType.ASCIISTR, line_num, beginningPos);
					curPos++;
					curPosInLine++;
					done = true;
				} else {
					tokenValue.append(cur_char);
					curPos++;
					curPosInLine++;
				}
			}
			
			if (curPos >= regex.length())
				this.EOS = true;
		}
		
		return token;
	}
	
	/**
	 * This method returns the next identified token
	 * when scanning the regular expression string.
	 * 
	 * @return the next RegexToken
	 */
	public RegexToken getToken() {

		RegexToken token = null;
		
		if (inRegex) {

			// If the token buffer is not empty, then return the token at the head of the buffer 
			if (!tokenBuffer.isEmpty()) {
				return tokenBuffer.poll();
			}

			// If the end of the string has been reached, return an EOS token
			if (EOS) {
				int oldPos = curPosInLine;
				curPosInLine = 0;
				return new RegexToken(RegexTokenType.EOS, line_num++, oldPos);
			}

			// If whitespace is being ignored, loop until a non-whitespace character is identified
			if (ignoreWhitespace) {
				// Loop through until a non-whitepsace character is found, or until the end of the string is reached
				while (curPos < regex.length() && Character.isWhitespace(regex.charAt(curPos))) {
					curPos++;
					curPosInLine++;
				}
				// If the end of string was reached, then set EOS to true and return an EOS token
				if (curPos >= regex.length()) {
					EOS = true;
					int pos = curPosInLine;
					curPosInLine = 0;
					return new RegexToken(RegexTokenType.EOS, line_num++, pos);
				}
			}


			// Get the character at the current scanning position
			char cur_char = regex.charAt(curPos);

			if (endRegexOnQuote && cur_char == '\'') {
				token = new RegexToken("'", RegexTokenType.END_REGEX, line_num, curPosInLine);
				inRegex = false;
				curPos++;
				curPosInLine++;
			} else if (cur_char == '\\') {
				// If current character is escape character, then move to the next scanning position
				curPos++;
				curPosInLine++;

				if (curPos >= regex.length()) {
					/*
					 *  If there was no character after the escape character set EOS to true
					 *  and return an INVALID_ESCAPE token
					 */
					this.EOS = true;
					return new RegexToken(RegexTokenType.INVALID_ESCAPE, line_num, curPosInLine);
				}

				if (regexCharType == RegexTokenType.RE_CHAR) {
					// If the current scanning type is RE_CHAR, see if a keyword exist
					token = identifyRegexKeywordToken();
				}

				if (token == null) {
					// If a keyword was not found, then set the token with the value of the current character
					String value = this.regex.substring(curPos, curPos+1);
					token = new RegexToken(value, regexCharType, line_num, curPosInLine);
					curPos++; // move the current position to the next location
					curPosInLine++;
				} else {
					/*
					 *  If a keyword was found, then break the keyword down
					 *  to it's individual characters and add those tokens
					 *  to the token buffer
					 */
					String value = token.getValue();
					// Break down the string into its individual tokens and add them to the token buffer
					for (int i = 1; i < value.length(); ++i) {
						tokenBuffer.add(new RegexToken(String.valueOf(value.charAt(i)), regexCharType, line_num, curPosInLine+i));
					}
					// Set the current token as the character at the first position in the string
					token.setValue(String.valueOf(value.charAt(0)));
					token.setType(regexCharType);
					token.setLineNumber(line_num);
					token.setLinePosition(curPosInLine);
					// Move the current position scanner past the escaped keyword
					this.curPos += value.length();
					curPosInLine += value.length();

				}
			} else if (cur_char == '$') {
				// If the character is the character class identifier, try to identify the character class
				curPos++;
				curPosInLine++;
				token = identifyCharClassToken(); // Identify the indicated character class
				if (token == null) {
					/* 
					 * If the token was null, then a character class could not be identified, 
					 * so return an invalid character class token.  
					 */
					token = new RegexToken(RegexTokenType.INVALID_CHAR_CLASS, line_num, curPosInLine);
				} else {
					// If a valid character class, then move the scanning cursor past the character class
					curPos += token.getValue().length();
					curPosInLine += token.getValue().length();
				}
			} else {

				if (regexCharType == RegexTokenType.RE_CHAR) {
					// If the current scanning type is RE_CHAR, see if a keyword exist
					token = identifyRegexKeywordToken();
				}
				if (token == null) {
					// If keyword was not identified, then identify the type of the current character
					token = identifyCharacterToken(cur_char);
					
					if (token.getValue().equals("]"))
						endRegexOnQuote = true;
					else if (token.getValue().equals("["))
						endRegexOnQuote = false;
					
					curPos++;
					curPosInLine++;
				} else {
					// If a keyword was identified, move the scanning cursor past the keyword
					curPos += token.getValue().length();
					curPosInLine += token.getValue().length();
				}
			}

			if (curPos >= regex.length()) {
				// If scanning cursor past the length of string, set end-of-sting to true
				EOS = true;
			}
			
		} else {		// **NOT in a Regex**
			
			// If the end of the string has been reached, return an EOS token
			if (EOS) {
				int oldPosInLine = curPosInLine;
				curPosInLine = 0;
				return new RegexToken(RegexTokenType.EOS, line_num++, oldPosInLine);
			}

			// Loop through until a non-whitepsace character is found, or until the end of the string is reached
			while (curPos < regex.length() && regex.charAt(curPos) != '\n' && Character.isWhitespace(regex.charAt(curPos))) {
				curPos++;
				curPosInLine++;
			}

		
			
			// If end of current line, consume \n, update cur_line, and reset curPos
			if (curPos + 2 <= regex.length()) {
				char current = regex.charAt(curPos);
				
				char next = regex.charAt(curPos+1);
				if (current == '\n') {		// newline found
					curPos++;
					curPosInLine = 0;
					line_num++;
				}
				if (next == '\r') {
					curPos++;
				}
			}
			
			
			// If the end of string was reached, then set EOS to true and return an EOS token
			if (curPos >= regex.length()) {
				EOS = true;
				return new RegexToken(RegexTokenType.EOS, line_num, curPosInLine);
			}


			// Get the character at the current scanning position
			char cur_char = regex.charAt(curPos);
			StringBuilder sb = new StringBuilder();
			
			token = identifyKeywordToken();
			if (token == null) {		// keyword not identified -- check if ID
				if (Character.isLetter(cur_char)) {
					int beginPos = curPosInLine;
					do {
						sb.append(cur_char);
						curPos++;
						curPosInLine++;
						
						if (curPos >= regex.length()) {
							this.EOS = true;
							break;
						}
						
						cur_char = regex.charAt(curPos);
					} while (curPos < regex.length() && (Character.isLetter(cur_char) || Character.isDigit(cur_char) || cur_char == '_'));
										
					String possibleID = sb.toString();
					if (possibleID.length() > 10) {
						token = new RegexToken("ID too long", RegexTokenType.INVALID_TOKEN, line_num, beginPos);
					} else if (EOS) {	// TODO: maybe reset line here too?
						curPosInLine = 0;
						token = new RegexToken(sb.toString(), RegexTokenType.ID, line_num++, beginPos);
					} else if (!Character.isWhitespace(regex.charAt(curPos)) && cur_char != ')' && cur_char != ';') {	// ID contains something other than letter, digit, or _
						token = new RegexToken("ID contains invalid characters", RegexTokenType.INVALID_TOKEN, line_num, beginPos);
					} else {	// valid ID found
						token = new RegexToken(sb.toString(), RegexTokenType.ID, line_num, beginPos);
					}
				} else {	// Not an ID or keyword
					if (cur_char == '\'') {
						token = new RegexToken("'", RegexTokenType.START_REGEX, line_num, curPosInLine);
						inRegex = true;
					} else if (cur_char == '"') {
						token = new RegexToken("\"", RegexTokenType.START_ASCII, line_num, curPosInLine);
					} else {
						token = new RegexToken("Expected ID or keyword", RegexTokenType.INVALID_TOKEN, line_num, curPosInLine);
					}
					curPos++;
					curPosInLine++;
				}
			} else {
				// If a keyword was identified, move the scanning cursor past the keyword
				curPos += token.getValue().length();
				curPosInLine += token.getValue().length();
			}
		}
		
		return token;
	}
	
	
	/**
	 * This method takes in a character and then identifies
	 * the corresponding RegexToken that represents the 
	 * character.
	 * 
	 * @param character a character part of the regular expression string 
	 * @return a RegexToken that corresponds to the inputed character
	 */
	private RegexToken identifyCharacterToken(char character) {
		// Identify which character type state we are in 
		switch (regexCharType) {
			case RE_CHAR:
				/*
				 * If in the RE_CHAR state, then we must check to see
				 * if the character is one of the following special
				 * characters: ., (, ), [, ], *, +, |
				 * If it is not, then we will simply return a token
				 * containing the inputed character value. 
				 */
				switch (character) {
					case '.':
						return new RegexToken(".", RegexTokenType.ANY_CHAR, line_num, curPosInLine);
					case '(':
						return new RegexToken("(", RegexTokenType.OPEN_PAR, line_num, curPosInLine);
					case ')':
						return new RegexToken(")", RegexTokenType.CLOSE_PAR, line_num, curPosInLine);
					case '[':
						return new RegexToken("[", RegexTokenType.OPEN_BRACKET, line_num, curPosInLine);
					case ']':
						return new RegexToken("]", RegexTokenType.CLOSE_BRACKET, line_num, curPosInLine);
					case '*':
						return new RegexToken("*", RegexTokenType.ZERO_OR_MORE_REP, line_num, curPosInLine);
					case '+':
						return new RegexToken("+", RegexTokenType.ONE_OR_MORE_REP, line_num, curPosInLine);
					case '|':
						return new RegexToken("|", RegexTokenType.UNION_OP, line_num, curPosInLine);
					default:
						return new RegexToken(String.valueOf(character), RegexTokenType.RE_CHAR, line_num, curPosInLine);
				}
			case SET_CHAR:
				/*
				 * If in the SET_CHAR state, then we must check to see
				 * if the character is one of the following special
				 * characters: ^,-, [, ]
				 * If it is not, then we will simply return a token
				 * containing the inputed character value. 
				 */
				switch (character) {
					case '^':
						return new RegexToken("^", RegexTokenType.NEGATIVE_SET, line_num, curPosInLine);
					case '-':
						return new RegexToken("-", RegexTokenType.RANGE_OP, line_num, curPosInLine);
					case '[':
						return new RegexToken("[", RegexTokenType.OPEN_BRACKET, line_num, curPosInLine);
					case ']':
						return new RegexToken("]", RegexTokenType.CLOSE_BRACKET, line_num, curPosInLine);
					default:
						return new RegexToken(String.valueOf(character), RegexTokenType.SET_CHAR, line_num, curPosInLine);
				}
		}
		return new RegexToken(RegexTokenType.OTHER, line_num, curPosInLine);
	}
	
	private RegexToken identifyKeywordToken() {
		int i = curPos; // Save the current scanning position
		int str_length = regex.length(); // the overall length of the regex
		// Loop through all the keywords in the language
		for (Map.Entry<String, RegexTokenType> entry: this.keywords.entrySet()) {
			String word = entry.getKey(); // get the keyword value
			// Check to see that the keyword can fit from the current position
			if ((i + word.length()) <= str_length) {
				boolean match = true; 
				// Loop through the sequence of characters and see if it matches the keyword
				for (int ind = 0; ind < word.length(); ++ind) {
					if (regex.charAt(i + ind) != word.charAt(ind)) {
						match = false;
						break;
					}
				}
				
				if (match && word.equals("in")) {	// check if keywork is actually 'intersec' || 'inters'
					if (i + 3 <= str_length) {
						if (regex.charAt(i + 2) == 't') {
							match = false;
						}
					}
				}
				
				if (match) {
					/*
					 * If the keyword matched the sequence of characters at the 
					 * current position, then return the representing RegexToken
					 */
					
					RegexTokenType rtt = RegexTokenType.INVALID_TOKEN;	// Initialize rtt to arbitrary type
					
					if (word.equals("inters") || word.equals("intersec"))
						rtt = RegexTokenType.INTERS_OP;
					else if (word.equals("replace"))
						rtt = RegexTokenType.REPLACE_OP;
					else {
						switch (word.charAt(0)) {
						
						case 'b':
							rtt = RegexTokenType.BEGIN_OP; break;
						case 'e':
							rtt = RegexTokenType.END_OP; break;
						case '=':
							rtt = RegexTokenType.EQUALS_OP; break;
						case 'r':
							rtt = RegexTokenType.RECURSIVE_REPLACE_OP; break;
						case '(':
							rtt = RegexTokenType.OPEN_PAR; break;
						case ')':
							rtt = RegexTokenType.CLOSE_PAR; break;
						case 'u':
							rtt = RegexTokenType.UNION_OP; break;
						case 'p':
							rtt = RegexTokenType.PRINT_OP; break;
						case 'w':
							rtt = RegexTokenType.WITH_OP; break;
						case 'i':
							rtt = RegexTokenType.IN_OP; break;
						case '#':
							rtt = RegexTokenType.POUND_OP; break;
						case 'f':
							rtt = RegexTokenType.FIND_OP; break;
						case 'd':
							rtt = RegexTokenType.DIFF_OP; break;
						case 'm':
							rtt = RegexTokenType.MAXFREQSTRING_OP; break;
						case '>':
							rtt = RegexTokenType.PIPE_OP; break;
						case ',':
							rtt = RegexTokenType.COMMA; break;
						case ';':
							rtt = RegexTokenType.SEMICOLON; break;
						default:
								System.err.println("Impossible!");
						
						}
					}
					return new RegexToken(word, rtt, line_num, curPosInLine);
				}
			}
			
		}
		// If a match is not found, return null
		return null;
	}
	
	/**
	 * This method starts at the current scanning position,
	 * and attempts to identify if their exists a sequence of 
	 * characters from this position that represent a keyword.
	 * If there is, the method returns a RegexToken representing 
	 * a keyword. If a valid keyword did not exist, then null is 
	 * returned.
	 * 
	 * @return the RegexToken representing the identified keyword, or null if a keyword was not found
	 */
	private RegexToken identifyRegexKeywordToken() {
		int i = curPos; // Save the current scanning position
		int str_length = regex.length(); // the overall length of the regex
		// Loop through all the keywords in the language
		for (Map.Entry<String, RegexTokenType> entry: this.regex_keywords.entrySet()) {
			String word = entry.getKey(); // get the keyword value
			// Check to see that the keyword can fit from the current position
			if ((i + word.length()) <= str_length) {
				boolean match = true; 
				// Loop through the sequence of characters and see if it matches the keyword
				for (int ind = 0; ind < word.length(); ++ind) {
					if (regex.charAt(i + ind) != word.charAt(ind)) {
						match = false;
						break;
					}
				}
				
				if (match) {
					/*
					 * If the keyword matched the sequence of characters at the 
					 * current position, then return the representing RegexToken
					 */
					return new RegexToken(word, entry.getValue(), line_num, curPosInLine);
				}
			}
			
		}
		// If a match is not found, return null
		return null;
	}
	
	/**
	 * This method starts at the current scanning position,
	 * and attempts to identify if their exists a sequence of 
	 * characters from this position that represent a character class name.
	 * If there is, the method returns a RegexToken representing 
	 * the character class name. If a valid character class name did not 
	 * exist, then null is returned.
	 * 
	 * @return the RegexToken representing the identified Character class name, or null if a character class was not found
	 */
	private RegexToken identifyCharClassToken() {
		int i = curPos; // Save the current scanning position
		int str_length = regex.length(); // the overall length of the regex
		// Loop through all the character class names
		for (String word: charClasses) {
			// Check to see that the character class name can fit from the current position
			if ((i + word.length()) <= str_length) {
				boolean match = true;
				// Loop through the sequence of characters and see if it matches the character class
				for (int ind = 0; ind < word.length(); ++ind) {
					if (regex.charAt(i + ind) != word.charAt(ind)) {
						match = false;
						break;
					}
				}
				
				if (match) {
					/*
					 * If the character class name matched the sequence of characters at the 
					 * current position, then return the matching RegexToken
					 */
					return new RegexToken(word, RegexTokenType.CHAR_CLASS, line_num, curPosInLine);
				}
			}
		}
		// If a match is not found, return null
		return null;
	}
	
	/**
	 * Set the type any non-special characters should be 
	 * set as during the scanning process. This type will 
	 * affect what is considered to be a special character or
	 * not. For example, in the RE_CHAR type, the * is a special 
	 * character, but it is not in the SET_CHAR type. 
	 * 
	 * If an invalid type is passed, the type will not change.
	 * 
	 * @param type a RegexTokenType that should be either RE_CHAR or SET_CHAR
	 */
	public void setRegexCharacterType(RegexTokenType type) {
		if (type == RegexTokenType.RE_CHAR || type == RegexTokenType.SET_CHAR) {
			this.regexCharType = type;
		}
	}
	
	/**
	 * Indicate to the scanner on whether to ignoreWhitespace 
	 * when scanning.
	 *  
	 * @param state a true or false on whether to ignore whitespace
	 */
	public void ignoreWhitespace(boolean state) {
		this.ignoreWhitespace = state;
	}
	
	
	
	
	
	/**
	 * Test to see if this all works
	 * @param args
	 */
	public static void main(String[] args) {
		String input = "matches = (find) '(REGEX [a-f\'])*'  \n notRegex  \n\"";	// *NOTE*: last " signals the start of an ASCII-Char
																// (switch to getASCII-Token() next token when tokenType == START_ASCII
																// the ending " is consumed (never returned)
		RegexScanner rs = new RegexScanner(input);
		RegexToken rt;
		System.out.println("Testing getToken() on: " + input);
		do {
			rt = rs.getToken();
			if (rt != null)
				System.out.println(RegexTokenType.getTokenDescription(rt) + ": \"" + rt.getValue() + "\" at line(" + rt.getLineNumber() + ") pos(" + rt.getLinePosition() + ")");
		} while (rt != null && rt.getType() != RegexTokenType.EOS);
		System.out.println();
		
		String input2 = " H3RP my D3RP \"";
		System.out.println("Testing getASCIIToken() on: " + input2);
		RegexScanner rs2 = new RegexScanner(input2);
		do {
			rt = rs2.getASCIIToken();
			if (rt != null)
				System.out.println(RegexTokenType.getTokenDescription(rt) + ": \"" + rt.getValue() + "\" at line(" + rt.getLineNumber() + ") pos(" + rt.getLinePosition() + ")");
		} while (rt != null && rt.getType() != RegexTokenType.EOS);
	}
}
