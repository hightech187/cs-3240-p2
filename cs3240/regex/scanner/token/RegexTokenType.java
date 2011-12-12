package cs3240.regex.scanner.token;

/**
 * An enumeration class that defines the different
 * types of tokens in a regular expression. For example, 
 * the UNION_OP means a token representing the
 * union operation (typically the '|' character)
 * has been found. 
 * 
 * 
 * @author Dilan Manatunga
 *
 */
public enum RegexTokenType {
	RE_CHAR,					// Match Single Character
	SET_CHAR,					// Set Character
	CHAR_CLASS,					// Character Class
	INVALID_CHAR_CLASS,			// Invalid Character Class
	OPEN_PAR,					// Open parenthesis "("
	CLOSE_PAR,					// Close Parenthesis ")"
	OPEN_BRACKET,				// Open Bracket "["
	CLOSE_BRACKET,				// Close Bracket "]"
	ANY_CHAR,					// Any character indicator
	ZERO_OR_MORE_REP,			// Zero or more repetition operator
	ONE_OR_MORE_REP,			// One or more repetition operator
	UNION_OP,					// Union operator
	NEGATIVE_SET,				// Negative Set indicator
	RANGE_OP,					// Set Range operator
	IN_OP,						// IN operator
	EOS,						// End of Regex String Token
	INVALID_ESCAPE,				// If an invalid character escape has occurred
	OTHER,						// OTHER or INVALID type of Token
	EQUALS_OP,
	POUND_OP,
	PIPE_OP,
	COMMA,
	SEMICOLON,
	BEGIN_OP,
	END_OP,
	REPLACE_OP,
	RECURSIVE_REPLACE_OP,
	INTERS_OP,
	PRINT_OP,
	WITH_OP,
	FIND_OP,
	DIFF_OP,
	MAXFREQSTRING_OP,
	ASCIISTR,
	ID,
	START_REGEX,
	END_REGEX,
	START_ASCII,
	INVALID_TOKEN;

	/**
	 * Returns an English description for the token
	 * 
	 * @param token a RegexToken instance
	 * @return a string description for the token
	 */
	public static String getTokenDescription(RegexToken token) {
		switch (token.getType()) {
			case RE_CHAR:
			case SET_CHAR:
				return token.getValue();
			case CHAR_CLASS:
				return "$" + token.getValue();
			case INVALID_CHAR_CLASS:
				return "Invalid Character Class Name";
			case OPEN_PAR:
				return "(";
			case CLOSE_PAR:
				return ")";
			case OPEN_BRACKET:
				return "[";
			case CLOSE_BRACKET:
				return "]";
			case ANY_CHAR:
				return ".";
			case ZERO_OR_MORE_REP:
				return "*";
			case ONE_OR_MORE_REP:
				return "+";
			case UNION_OP:
				return "|";
			case NEGATIVE_SET:
				return "^";
			case IN_OP:
				return "keyword IN";
			case EOS:
				return "End Of String";
			case INVALID_ESCAPE:
				return "Invalid Escape Sequence";	
			case ID:
				return "ID";
			case ASCIISTR:
				return "ASCII-STR";
			case EQUALS_OP:
				return "keyword =";
			case POUND_OP:
				return "keyword #";
			case PIPE_OP:
				return "keyword >!";
			case COMMA:
				return "keyword ,";
			case SEMICOLON:
				return "keyword ;";
			case BEGIN_OP:
				return "keyword begin";
			case END_OP:
				return "keyword end";
			case REPLACE_OP:
				return "keyword replace";
			case RECURSIVE_REPLACE_OP:
				return "keyword recursivereplace";
			case INTERS_OP:
				return "keyword inters";
			case PRINT_OP:
				return "keyword print";
			case WITH_OP:
				return "keyword with";
			case FIND_OP:
				return "keyword find";
			case DIFF_OP:
				return "keyword diff";
			case MAXFREQSTRING_OP:
				return "keyword maxfreqinstring";
			case INVALID_TOKEN:
				return "invalid token";
			case START_REGEX:
				return "start of regex";
			case START_ASCII:
				return "start of ascii";
			case END_REGEX:
				return "end of regex";
			
			
			default:
				return token.getType().toString();
		}
	}
	
	/**
	 * Returns an English description for the token type.
	 * 
	 * @param type a RegexTokenType value
	 * @return a string description for the token type
	 */
	public static String getTokenDescription(RegexTokenType type) {
		switch (type) {
		case RE_CHAR:
		case SET_CHAR:
			return "CHARACTER";
		case CHAR_CLASS:
			return "$CHAR_CLASS";
		case INVALID_CHAR_CLASS:
			return "Invalid Character Class Name";
		case OPEN_PAR:
			return "(";
		case CLOSE_PAR:
			return ")";
		case OPEN_BRACKET:
			return "[";
		case CLOSE_BRACKET:
			return "]";
		case ANY_CHAR:
			return ".";
		case ZERO_OR_MORE_REP:
			return "*";
		case ONE_OR_MORE_REP:
			return "+";
		case UNION_OP:
			return "|";
		case NEGATIVE_SET:
			return "^";
		case IN_OP:
			return "keyword IN";
		case EOS:
			return "End Of String";
		case INVALID_ESCAPE:
			return "Invalid Escape Sequence";	
		case ID:
			return "ID";
		case ASCIISTR:
			return "ASCII-STR";
		case EQUALS_OP:
			return "keyword =";
		case POUND_OP:
			return "keyword #";
		case PIPE_OP:
			return "keyword >!";
		case COMMA:
			return "keyword ,";
		case SEMICOLON:
			return "keyword ;";
		case BEGIN_OP:
			return "keyword begin";
		case END_OP:
			return "keyword end";
		case REPLACE_OP:
			return "keyword replace";
		case RECURSIVE_REPLACE_OP:
			return "keyword recursivereplace";
		case INTERS_OP:
			return "keyword inters";
		case PRINT_OP:
			return "keyword print";
		case WITH_OP:
			return "keyword with";
		case FIND_OP:
			return "keyword find";
		case DIFF_OP:
			return "keyword diff";
		case MAXFREQSTRING_OP:
			return "keyword maxfreqinstring";
		case INVALID_TOKEN:
			return "invalid token";
		case START_REGEX:
			return "start of regex";
		case START_ASCII:
			return "start of ascii";
		case END_REGEX:
			return "end of regex";
			default:
				return "INVALID Token";
		}
	}
}
