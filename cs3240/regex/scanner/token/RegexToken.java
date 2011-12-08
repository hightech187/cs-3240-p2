package cs3240.regex.scanner.token;

/**
 * This class represents tokens found 
 * when scanning a regular expression. 
 * Each token has a string value, and a 
 * type which will be one of an enumerated
 * set of values.
 * 
 * @author Dilan Manatunga
 *
 */
public class RegexToken {
	/**
	 * The type of the regex token
	 */
	private RegexTokenType type;
	/**
	 * The string value of the regex token
	 */
	private String value;
	/**
	 * The line number the token was found at 
	 */
	int line_num;
	/**
	 * The position in the line the token was found at
	 */
	int line_pos;
	
	/**
	 * The base constructor for a RegexToken object.
	 * It sets the string value for the token, and the
	 * type of the token
	 * 
	 * @param value the string value for the token
	 * @param type the type for the token
	 * @param line_num the line number the token was found at
	 * @param line_pos the line position the token was found at
	 */
	public RegexToken(String value, RegexTokenType type, int line_num, int line_pos) {
		this.value = value;
		this.type = type;
		this.line_num = line_num;
		this.line_pos = line_pos;
	}

	/**
	 * A secondary constructor that simply creates a
	 * RegexToken and sets the type for that token
	 * as the inputed type. The value for the token
	 * defaults to an empty string.
	 * 
	 * @param type the type for the token
	 * @param line_num the line number the token was found at
	 * @param line_pos the line position the token was found at
	 */
	public RegexToken(RegexTokenType type, int line_num, int line_pos) {
		this.type = type;
		this.value = "";
		this.line_num = line_num;
		this.line_pos = line_pos;
	}

	/**
	 * Return the current string value for the RegexToken
	 * 
	 * @return the string value of the Token
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Set a new string value for the RegexToken
	 * 
	 * @param value the string value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Returns the type of the RegexToken
	 * 
	 * @return the type of the token
	 */
	public RegexTokenType getType() {
		return type;
	}
	
	/**
	 * Set the type of the RegexToken
	 * 
	 * @param type the new token type to set
	 */
	public void setType(RegexTokenType type) {
		this.type = type;
	}

	/**
	 * Returns the line number the token was found at
	 * 
	 * @return the line number the token was found at
	 */
	public int getLineNumber() {
		return line_num;
	}

	/**
	 * Set the line number the token was found at
	 * 
	 * @param line_num the line number to set
	 */
	public void setLineNumber(int line_num) {
		this.line_num = line_num;
	}

	/**
	 * Returns the line position the token was found at
	 * 
	 * @return the line position the token was found at
	 */
	public int getLinePosition() {
		return line_pos;
	}

	/**
	 * Set the line position the token was found at
	 * 
	 * @param line_pos the line position value to set
	 */
	public void setLinePosition(int line_pos) {
		this.line_pos = line_pos;
	}
	
}
