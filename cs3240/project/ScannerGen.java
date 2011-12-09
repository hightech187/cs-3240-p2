package cs3240.project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
	public void run() throws IOException, Exception {
		processFileDefinitons(); // Process the character class and token definitions in file
		if (parsingError) {
			throw new Exception("There were errors were parsing the file.\nScanner Geneartion Terminatng");
		}
	}

	/**
	 * This method processes the file containing the 
	 * lexical specification for Character Class
	 * definitions and Token definitions.
	 * @throws Exception 
	 */
	public void processFileDefinitons() throws Exception {
		// Open the file that is to be scanned
		BufferedReader fileReader = new BufferedReader(new FileReader(filename));
		
		String line = fileReader.readLine(); // Read the first line from the file
		String newLine = "notNull";
		while (newLine != null) {
			newLine = fileReader.readLine();
			if (newLine != null)
				line.concat("\n" + newLine);
		}
		
		RegexParser parser = new RegexParser(line);
		
		parser.MiniREProgram();
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
