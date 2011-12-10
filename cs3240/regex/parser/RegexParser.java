package cs3240.regex.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import cs3240.project.Driver;
import cs3240.regex.ast.AnyCharNode;
import cs3240.regex.ast.ConcatNode;
import cs3240.regex.ast.EpsilonNode;
import cs3240.regex.ast.NegativeSetNode;
import cs3240.regex.ast.OneOrMoreRepNode;
import cs3240.regex.ast.ReCharNode;
import cs3240.regex.ast.RegexAstNode;
import cs3240.regex.ast.SetNode;
import cs3240.regex.ast.UnionNode;
import cs3240.regex.ast.ZeroOrMoreRepNode;
import cs3240.regex.automaton.DFATable;
import cs3240.regex.automaton.NFA;
import cs3240.regex.scanner.RegexScanner;
import cs3240.regex.scanner.token.RegexToken;
import cs3240.regex.scanner.token.RegexTokenType;

/**
 * This class represents a Regular Expression
 * parser that will scan a regular expression
 * and then construct the corresponding NFA
 * to the regular expression.
 * 
 * @author Dilan Manatunga
 *
 */
public class RegexParser {
	/**
	 * A string containing the regular expression definition
	 */
	private String regex;
	
	private String output = "";
	/**
	 * The scanner for the regular expression definition
	 */
	private RegexScanner scanner;
	
	private Hashtable<String, ArrayList<String>> matchLists = new Hashtable<String, ArrayList<String>>();

	private Hashtable<String, Integer> integers;
	/**
	 * The current token that has been received from the scanner
	 */
	private RegexToken token;
	
	/**
	 * The main constructor for the RegexParser class
	 * that creates a parser for the given regular
	 * expression definition
	 * 
	 * @param regex the string regular expression definition for the class
	 * @param charClasses the currently defined set of Character Class
	 * @param line_num the line number the definition occurred at
	 */
	public RegexParser(String regex) {
		this.regex = regex;
		// Create a scanner for the regular expression definition
		this.scanner = new RegexScanner(regex);
	}
	
	public void MiniREProgram() throws Exception {
		match(RegexTokenType.BEGIN_OP);
		statementList();
		match(RegexTokenType.END_OP);
	}
	
	public void statementList() throws Exception {
		statement();
		statementListTail();
	}
	
	public void statementListTail() throws Exception {
		if (peek() != RegexTokenType.END_OP) {
			statement();
			statementListTail();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void statement() throws Exception {
		switch(peek()) {
			case ID:
				String ID = match(RegexTokenType.ID).getValue();
				match(RegexTokenType.EQUALS_OP);
				switch(peek()) {
					case POUND_OP:
						match(RegexTokenType.POUND_OP);
						integers.put(ID, ((ArrayList<Integer>) exp()).size());
						break;
					case MAXFREQSTRING_OP:
						match(RegexTokenType.OPEN_PAR);
						ArrayList<String> temp = matchLists.get(match(RegexTokenType.ID).getValue());
						Hashtable<String, Integer> tmp = new Hashtable<String, Integer>();
						for (String i : temp) {
							if (!tmp.containsKey(temp)) {
								tmp.put(i, 1);
							} else {
								tmp.put(i, tmp.get(i) + 1);
							}
						}
						
						Set<String> set = tmp.keySet();
						Iterator<String> itr = set.iterator();
						
						String maxString = "";
						int maxInt = 0;
						while (itr.hasNext()) {
							String chkstr = itr.next();
							if (tmp.get(chkstr) > maxInt) {
								maxString = chkstr;
								maxInt = tmp.get(chkstr);
							}
						}
						
						ArrayList<String> l = new ArrayList<String>();
						l.add(maxString);
						matchLists.put(ID, l); 
						
						match(RegexTokenType.CLOSE_PAR);
						break;
					default:
						Object variable = exp();
						if (variable instanceof Integer) {
							integers.put(ID, (Integer) variable);
						} else {
							matchLists.put(ID, (ArrayList<String>) variable);
						}
				}
				break;
			case REPLACE_OP:
				match(RegexTokenType.REPLACE_OP);
				DFATable regex = parseRegex().toDFA();
				match(RegexTokenType.WITH_OP);
				match(RegexTokenType.START_ASCII);
				RegexToken tok = matchASCII();
				match(RegexTokenType.IN_OP);
				String[] files = filenames();
				if (files[0] != files[1]) {
					replaceString(regex, tok, files);
				} else {
					throw new Exception("Thou art an idiot! " + files[0] + " = " + files[1] + "! This should not be!");
				}
				break;
			case RECURSIVE_REPLACE_OP:
				match(RegexTokenType.REPLACE_OP);
				DFATable regex2 = parseRegex().toDFA();
				match(RegexTokenType.WITH_OP);
				match(RegexTokenType.START_ASCII);
				RegexToken tok2 = matchASCII();
				match(RegexTokenType.IN_OP);
				String[] files2 = filenames();
				if (files2[0] != files2[1]) {
					while (replaceString(regex2, tok2, files2));
				} else {
					throw new Exception("Thou art an idiot! " + files2[0] + " = " + files2[1] + "! This should not be!");
				}
				break;
			case PRINT_OP:
				match(RegexTokenType.PRINT_OP);
				match(RegexTokenType.OPEN_PAR);
				output = output.concat(expList());
				match(RegexTokenType.CLOSE_PAR);
				break;
			case INVALID_CHAR_CLASS:
				throw new Exception(String.format("Line %d (col %d): Invalid Character Class Name", this.token.getLineNumber(), this.token.getLinePosition()));
		}
		match(RegexTokenType.SEMICOLON);
	}
	
	public String[] filenames() throws Exception {
		String names[] = new String[2];
		names[0] = sourceFile();
		match(RegexTokenType.PIPE_OP);
		names[1] = destinationFile();
		return names;
	}
	
	public String sourceFile() throws Exception {
		match(RegexTokenType.START_ASCII);
		return matchASCII().getValue();
	}
	
	public String destinationFile() throws Exception {
		match(RegexTokenType.START_ASCII);
		return matchASCII().getValue();
	}
	
	@SuppressWarnings("unchecked")
	public String expList() throws Exception {
		Object temp = exp();
		String retVal = "";
		
		if (temp instanceof Integer) {
			retVal = temp.toString();
		} else {
			for (String i : (ArrayList<String>) temp) {
				retVal = retVal.concat(i);
			}
		}
		
		return retVal = retVal.concat(expListTail());
	}
	
	@SuppressWarnings("unchecked")
	public String expListTail() throws Exception {
		if (peek() != RegexTokenType.COMMA) {
			return "";
		}
		
		String retVal = match(RegexTokenType.COMMA).getValue();
		Object temp = exp();

		if (temp instanceof Integer) {
			retVal = temp.toString();
		} else {
			for (String i : (ArrayList<String>) temp) {
				retVal = retVal.concat(i);
			}
		}
		
		return retVal.concat(expListTail());
	}
	
	@SuppressWarnings("unchecked")
	public Object exp() throws Exception {
		switch(peek()) {
			case OPEN_PAR:
				match(RegexTokenType.OPEN_PAR);
				Object o = exp();
				match(RegexTokenType.CLOSE_PAR);
				return o;
			case FIND_OP:
				ArrayList<String> strings = term();
				ArrayList<Object> binaryOperation = expTail();
				
				if (binaryOperation == null) {
					return strings;
				}
				
				binaryOperation.add(0, strings);
				
				while (binaryOperation.size() > 1) {
					ArrayList<String> term = (ArrayList<String>) binaryOperation.remove(0);
					RegexToken binOp = (RegexToken) binaryOperation.remove(0);
					ArrayList<String> tail = (ArrayList<String>) binaryOperation.remove(0);
					
					switch (binOp.getType()) {
						case DIFF_OP:
							for (String i : tail) {
								if (term.contains(i)) {
									term.remove(i);
								}
							}
							break;
						case UNION_OP:
							for (String i : tail) {
								if (!term.contains(i)) {
									term.add(i);
								}
							}
							break;
						case INTERS_OP:
							for (String i : term) {
								if (!(tail.contains(i))) {
									term.remove(i);
								}
							}
					}
					
					binaryOperation.add(term);
				}
				
				return binaryOperation.remove(0);
			default:
				String ID = match(RegexTokenType.ID).getValue();
				Set<String> set = integers.keySet();
				Iterator<String> itr = set.iterator();
				
				while (itr.hasNext()) {
					if (itr.next() == ID) {
						return integers.get(ID);
					}
				}
				
				while (itr.hasNext()) {
					if (itr.next() == ID) {
						return integers.get(ID);
					}
				}
				
				throw new Exception("The ID value " + ID + " isn't a thing. Try again.");
		}
	}
	
	public ArrayList<Object> expTail() throws Exception {
		RegexToken binOp;
		
		binOp = binOp();
		
		if (binOp == null) {
			return null;
		}
		
		ArrayList<String> strings = term();
		ArrayList<Object> binaryOperation = expTail();

		binaryOperation.add(0, strings);
		binaryOperation.add(0, binOp);
		
		return binaryOperation;
	}
	
	public ArrayList<String> term() throws Exception {
		match(RegexTokenType.FIND_OP);
		DFATable regex = parseRegex().toDFA();
		match(RegexTokenType.IN_OP);
		String filename = filename();
		return findString(regex, filename);
	}
	
	public String filename() throws Exception {
		match(RegexTokenType.START_ASCII);
		return matchASCII().getValue();
	}
	
	public RegexToken binOp() throws Exception {
		switch(peek()) {
			case DIFF_OP:
				return match(RegexTokenType.DIFF_OP);
			case UNION_OP:
				return match(RegexTokenType.UNION_OP);
			case INTERS_OP:
				return match(RegexTokenType.INTERS_OP);
			default:
				return null;
		}
	}
	
	/**
	 * This method parses the regular expression 
	 * and then if the expression is valid, it 
	 * generates the corresponding an NFA.
	 * 
	 * @return the NFA corresponding to the regular expression
	 * @throws Exception if regular expression is not syntactically correct
	 */
	public NFA parseRegex() throws Exception {
		
		match(RegexTokenType.START_REGEX);
		
		RegexAstNode root = RE(); // Build up an Abstract-Syntax Tree representing the regex
		// Match to the end of string 
		
		match(RegexTokenType.END_REGEX);
		
		// Generate the NFA represented by the root RegexAST Node
		NFA nfa = root.generateNFA();
		nfa.setTerminatingStatesType("Go to Hell!");
		return nfa;
	}
	
	/**
	 * This method parses the regular expression and then
	 * creates an Abstract Syntax Tree representing the
	 * expression. It begins by identifying all of the 
	 * regular expression up to the first non-nested union operator.
	 * 
	 * This method matches to this grammar rule:
	 * RE => SIMPLE_RE RE_PRIME
	 * 
	 * @return the root node of the abstract syntax tree representing the expression
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private RegexAstNode RE() throws Exception {
		// Identify a simple regular expression unit
		RegexAstNode leftNode = simpleRE();
		// Continue parsing the rest of the string
		return RE_prime(leftNode);
	}
	
	/**
	 * This method continues parsing the regular expression and
	 * accounts for the following grammar rule, which deals with
	 * union operations in the regular expression:
	 * RE_PRIME => '|' SIMPLE_RE RE_PRIME | epsilon
	 * 
	 * @param leftNode the node representing the expression that may be involved in a union operation
	 * @return the root node of the abstract syntax tree representing the expression
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private RegexAstNode RE_prime (RegexAstNode leftNode) throws Exception {
		// Check to see if there is a union operator token
		if (peek() == RegexTokenType.UNION_OP) {
			match(RegexTokenType.UNION_OP);
			// Identify the next simple regular expression unit
			RegexAstNode rightNode = simpleRE();
			// Create a union node between the two units
			RegexAstNode node = new UnionNode(leftNode, rightNode);
			// Continue parsing the rest of the string
			return RE_prime(node);
		}
		// RE_PRIME => epsilon case
		return leftNode;
	}
	
	/**
	 * This method continues parsing the regular expression and
	 * accounts for the following grammar rule:
	 * SIMPLE_RE => BASIC_RE SIMPLE_RE_PRIME | epsilon
	 * 
	 * @return the root node of the abstract syntax tree representing the expression
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private RegexAstNode simpleRE() throws Exception {
		switch (peek()) {
			case UNION_OP:
			case CLOSE_PAR:
			case END_REGEX:
			case EOS:
				// Follow characters not part of BASIC_RE, so return epsilon node
				return new EpsilonNode();
			default:
				// Identify a basic regular expression unit
				RegexAstNode node = basicRE();
				// Continue parsing the rest of the string
				return simpleRE_prime(node);
		}
	}
	
	/**
	 * This method continues parsing the regular expression and
	 * accounts for the following grammar rule, which represents
	 * the concatenation between regular expressions:
	 * SIMPLE_RE_PRIME => BASIC_RE SIMPLE_RE_PRIME | epsilon
	 * 
	 * @param leftNode the node representing the expression to possible be concatenated
	 * @return the root node of the abstract syntax tree representing the expression
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private RegexAstNode simpleRE_prime(RegexAstNode leftChild) throws Exception {
		switch (peek()) {
			case UNION_OP:
			case CLOSE_PAR:
			case END_REGEX:
			case EOS:
				// Follow characters not part of BASIC_RE, so just return node
				return leftChild;
			default:
				// Identify a basic regular expression unit
				RegexAstNode rightChild = basicRE();
				// Express the concatenation between the two regular expression units
				RegexAstNode node = new ConcatNode(leftChild, rightChild);
				// Continue parsing the rest of the string
				return simpleRE_prime(node);
		}
	}
	
	/**
	 * This method continues parsing the regular expression and
	 * accounts for the following grammar rule:
	 * BASIC_RE => ELEMENTARY_RE REPETITION
	 * 
	 * @return the root node of the abstract syntax tree representing the expression
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private RegexAstNode basicRE() throws Exception {
		// Identify the elementary regular expression
		RegexAstNode node = elementaryRE();
		// Identify if there is repetition of the elementary expression
		return repitition(node);
	}
	
	/**
	 * This method continues parsing the regular expression and
	 * accounts for the following grammar rule, which represents
	 * the concatenation between regular expressions:
	 * SIMPLE_RE_PRIME => BASIC_RE SIMPLE_RE_PRIME | epsilon
	 * 
	 * @param leftNode the node representing the expression to possibly be repeated
	 * @return the root node of the abstract syntax tree representing the expression
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private RegexAstNode repitition(RegexAstNode node) throws Exception {
		switch (peek()) {
			case ZERO_OR_MORE_REP:
				// If the next token is the zero or more operator, create that node
				match(RegexTokenType.ZERO_OR_MORE_REP);
				return new ZeroOrMoreRepNode(node);
			case ONE_OR_MORE_REP:
				// If the next token is the one or more operator, create that node
				match(RegexTokenType.ONE_OR_MORE_REP);
				return new OneOrMoreRepNode(node);
			default:
				// Next token is not a repetition operator, so just return node
				return node;
		}
	}
	
	/**
	 * This method continues parsing the regular expression and
	 * accounts for the following grammar rule:
	 * ELEMENTARY_RE => '(' RE ')' | '.' | RE_CHAR | CHAR_CLASS | '[' SET
	 * 
	 * @return the root node of the abstract syntax tree representing the expression
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private RegexAstNode elementaryRE() throws Exception {
		RegexToken tok; // A variable to hold the token returned from match
		// Based on the next token type, identify the possible regular expression
		switch (peek()) {
			case OPEN_PAR:
				match(RegexTokenType.OPEN_PAR);
				// A grouping operator which means a new regular expression must be parsed
				RegexAstNode node = RE();
				match(RegexTokenType.CLOSE_PAR);
				return node;
			case RE_CHAR:
				// A single character expression
				tok = match(RegexTokenType.RE_CHAR);
				// Create the single character node
				return new ReCharNode(tok.getValue());
			case ANY_CHAR:
				// Any character expression
				match(RegexTokenType.ANY_CHAR);
				// Create the any character node
				return new AnyCharNode();
			case OPEN_BRACKET:
				match(RegexTokenType.OPEN_BRACKET);
				// Indicate the scanner should be in SET_CHAR scanning mode
				scanner.setRegexCharacterType(RegexTokenType.SET_CHAR);
				scanner.ignoreWhitespace(false); // Tell scanner to ignore whitespace
				// Identify the defined set
				return set();
			case INVALID_CHAR_CLASS:
				throw new Exception(String.format("Line %d (col %d): Invalid Character Class Name", this.token.getLineNumber(), this.token.getLinePosition()));
			default:
				throw new Exception(String.format("Line %d (col %d): Regular Expression Parsing Error has occurred", this.token.getLineNumber(), this.token.getLinePosition()));

		}
	}
	
	/**
	 * This method continues parsing the regular expression for
	 * the character set definition described by the following
	 * grammar rule:
	 * SET => SET_ITEMS ']' | '^' NEGATIVE_SET
	 * 
	 * @return the SetNode representing the identified set
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private SetNode set() throws Exception {
		// Check to see if set is defined as a negative set
		if (peek() == RegexTokenType.NEGATIVE_SET) {
			match(RegexTokenType.NEGATIVE_SET);
			return negativeSet(); // Identify the negative set
		} else {
			SetNode node = setItems(); // Identify the items in the set
			match(RegexTokenType.CLOSE_BRACKET);
			// Set the scanner back ignoring white-space and RE_CHAR mode
			scanner.setRegexCharacterType(RegexTokenType.RE_CHAR);
			scanner.ignoreWhitespace(true);
			return node;
		}
	}
	
	/**
	 * This method continues parsing the regular expression for
	 * the negative character set definition described by the 
	 * following grammar rule:
	 * NEGATIVE_SET => SET_ITEMS ']' IN NEGATIVE_SET_TAIL
	 * 
	 * @return the SetNode representing the identified set
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private SetNode negativeSet() throws Exception { 
		SetNode leftChild = setItems(); // Identify the set of characters to exclude

		match(RegexTokenType.CLOSE_BRACKET);
		// Set the scanner back ignoring white-space and RE_CHAR mode
		scanner.setRegexCharacterType(RegexTokenType.RE_CHAR);
		scanner.ignoreWhitespace(true);

		match(RegexTokenType.IN_OP); // match to the IN keyword
		// Identify the positive set
		SetNode rightChild = negativeSet_tail();
		// Create a negative set node that excludes the negative set characters from the positive set
		return new NegativeSetNode(leftChild, rightChild);
	}
	
	/**
	 * This method continues parsing the regular expression for
	 * the tail of the negative set character set definition
	 * described by the following grammar rule:
	 * NEGATIVE_SET => CHAR_CLASS | '[' SET_ITEMS ']'
	 * 
	 * @return the SetNode representing the identified set
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private SetNode negativeSet_tail() throws Exception {
		SetNode node;
		// peek to char class
		switch (peek()) {
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
				
				node = setItems(); // Identify the set

				match(RegexTokenType.CLOSE_BRACKET);
				// Set the scanner back ignoring white-space and RE_CHAR mode
				scanner.setRegexCharacterType(RegexTokenType.RE_CHAR);
				scanner.ignoreWhitespace(true);
				return node;
			case INVALID_CHAR_CLASS:
				throw new Exception(String.format("Line %d (col %d): Invalid Character Class Name", this.token.getLineNumber(), this.token.getLinePosition()));
			default:
				throw new Exception(String.format("Line %d (col %d): Regular Expression Parsing Error has occurred", this.token.getLineNumber(), this.token.getLinePosition()));

		}
	}
	
	/**
	 * This method continues parsing the regular expression for
	 * the character set definition described by the following
	 * grammar rule:
	 * SET_ITEMS => SET_ITEM { SET_ITEM}
	 * 
	 * @return the SetNode representing the identified set
	 * @throws Exception if regular expression is not syntactically correct
	 */
	private SetNode setItems() throws Exception {
		// Indicate the scanner should be in SET_CHAR scanning mode
		scanner.setRegexCharacterType(RegexTokenType.SET_CHAR);
		scanner.ignoreWhitespace(false);
		// Create the set node
		SetNode node = new SetNode();
		// Keep identifying set items till next token is not a set char
		while (peek() == RegexTokenType.SET_CHAR) {
			// Identify the set item and add it to the set node
			setItem(node);
		}
		// Set the scanner back ignoring white-space and RE_CHAR mode
		scanner.setRegexCharacterType(RegexTokenType.RE_CHAR);
		scanner.ignoreWhitespace(true);
		return node;
	}
	
	/**
	 * This method identifies a character or character 
	 * range definition in the set definition, and then adds
	 * the corresponding characters to the set node.
	 * 
	 * @param set the set node to add new characters too
	 * @throws Exception occurs if non-ASCII printable character used to define character set or if definition not syntactically correct 
	 */
	private void setItem(SetNode node) throws Exception {
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
			node.addCharacterRange(c1, c2);
		} else {
			// Only single character defined, so add character to set
			node.addCharacter(c1);
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
		if (token.getType() == type) {
			// Check to see if token types match
			RegexToken ret_token = token;
			token = null;
			return ret_token;
		} else {
			// throw an exception since they don't match
			throw new Exception(String.format("Line %d (col %d): Expecting %s NOT %s", token.getLineNumber(),token.getLinePosition(), RegexTokenType.getTokenDescription(type), RegexTokenType.getTokenDescription(token)));
		}
	}
	
	private RegexToken matchASCII() throws Exception {
		return scanner.getASCIIToken();
	}
	
	/**
	 * Calls the scanner to get the next token
	 */
	private void getNextToken() {
		token = scanner.getToken(); // Get the next token from the scanner
	}


	/**
	 * Returns the regular expression definition
	 * 
	 * @return the regular expression definition
	 */
	public String getRegex() {
		return regex;
	}


	/**
	 * Set a regular expression definition
	 * 
	 * @param regex the regular expression definition to set
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	private ArrayList<String> findString(DFATable regex, String file) throws IOException {
		Driver driver = new Driver(file, regex);
		ArrayList<Driver.Token> tokens = driver.run();
		ArrayList<String> strings = new ArrayList<String>();
		
		for (Driver.Token i : tokens) {
			if (!strings.contains(i)) strings.add(i.getValue());
		}
		
		return strings;
	}
	
	private boolean replaceString(DFATable regex, RegexToken ASCII, String[] files) throws Exception {
		Driver driver = new Driver(files[0], regex);
		ArrayList<Driver.Token> tokens = driver.run();
		
		if (tokens.size() == 0) return false;

		BufferedReader inputFileReader = new BufferedReader(new FileReader(files[0]));
		
		String line = inputFileReader.readLine();
		String oldLine = line;
		for (Driver.Token token : tokens) {
			line.replace(token.getValue(), ASCII.getValue());
		}
		
		if (oldLine == line)
			throw new Exception("Thou art an idiot! " + ASCII.getValue() + " = " + ASCII.getValue() + "! This should not be!");
		
		PrintWriter out = new PrintWriter(new FileWriter(files[1]));
		
		out.println(line);
		out.close();
		
		return true;
	}
}