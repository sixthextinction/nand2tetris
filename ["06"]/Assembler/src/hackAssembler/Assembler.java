package hackAssembler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
public class Assembler {
	
	/**
	 * DESCRIPTION 	: Assembler for the HACK computer, as seen in Nand2Tetris.
	 * 
	 * USAGE 		: Hardcoded path to .asm file as input, generates .hack file of same name as output.
	 * 
	 * MODULES		:	1) 	Parser  	Parses input file, resolving instruction types, address allocation, and splitting an instruction into its various components.
	 * 									Strips white and tabspaces.	
	 * 									Called twice. 
	 * 									First pass 	- Detects loop labels, stores them and their associated memAddresses (with allocation, if not already present) into a SymbolTable.
	 * 									Second pass - Differentiates and detects A and C instructions, breaks them into their components for the later Translation module.
	 * 					2)	Translator	Receives each component of the current instruction from Parser's 2nd pass, and translates them to machine code via predefined OpCode ruleset.
	 * 					3)	SymbolTable	java Hashtable containing a list of seen symbols and their associated memAddresses at any given point of time, as a set of key:value pairs.
	 * 									Init'd with a predefined set of key:value pairs, with any additional entries treated as user variables needing mem Allocation.
	 * 
	 * 
	 * NOTES		:	1) A loop label declaration (things in parantheses)'s "memAddress" for its SymbolTable entry, will simply be its line number in the .asm file, after comments have been stripped. 
	 * 					2) Instructions are implemented as each an object of the Instruction class, for ease of use.
	 *						-Instruction
	 *						--String content 			- Content of the instruction being processed. "@352", "D=D+M" etc.	 
	 *						--String dest				- Destination component. Used for C-instruction only, null for A
	 *						--String comp				- Computation component. Used for C-instruction only, null for A
	 *						--String jump				- Jump component. Used for C-instruction only, null for A
	 *						--String resolvedAddress	- Resolved memAddress of this instruction. "352" for "@352", "0" for "@R0", "24576" for "@KBD", or something allocated for "@ <user var> ". Used for A-instruction only. null for C.
	 *						--int type					- Type of the instruction being processed. -1 for A, 0 for C, 1 for invalid, and 2 for labels.
	 * 							 
	 */
	
	public static void main(String[] args) throws IOException, FileNotFoundException 
	  {
		//a variable to keep track of present line number, needed for loop label memAddr allocation for symbolTable. 
		//See NOTES.
		int linePosition = 0;
		
		SymbolTable s = new SymbolTable();	
	 
		//TODO : Implement command line invocation. Format : "java <filename>.asm"	
		//For now, the filepath is hardcoded.
	    String filename = "L:\\Books & ProInt\\Coursera\\nand2tetris\\projects\\06\\Rect\\Rect.asm";
	    
	    //String to store machineCode translation for current working line. 
	    //This string is one line in our output.
	    String machineCode = "";
	    
	    //We're storing instructions as objects with fields for handling/manipulation simplicity. 
	    //See NOTES.
	    Instruction ins = new Instruction();
	    
	    BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(filename));	
	    BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter("L:\\Books & ProInt\\Coursera\\nand2tetris\\projects\\06\\Rect\\Rect.hack")); //replace .asm in filename with .hack
		
		//FIRST PASS : Do nothing but grab loop labels and add them to SymbolTable.
		while((ins.setContent(reader.readLine()))!= null)
		{
			  //this will strip whitespace/tabs from actual label text too. Hmm. Is that per spec?
			  ins.setContent(Parser.strip(ins.getContent())); 		  
			  if(ins.getContent().length() > 0) 
			  {	
				  ins = Parser.storeLabels(ins, s, linePosition);				  
				  //Update line number tracker
				  if(ins.getContent().charAt(0)!='(')	{	 linePosition++;	} 
			  }		  
		}
		// END OF FIRST PASS
	
		//reset linePosition
		linePosition = 0;
		
		reader.close();
		reader = new BufferedReader(new FileReader(filename));//Yes, I know this is a bit ugly.
		
		//SECOND PASS : Parser does the rest of its work.
		  while( (ins.setContent(reader.readLine()))!= null)
		  {
			  //strip whitespaces & inline comments
			  ins.setContent(Parser.strip2ndPass(ins.getContent())); //this strip now needs to get rid of labels, too.
			  if(ins.getContent().length() > 0) 
			  {	
				  ins = Parser.parse(ins,s); //And this is why we needed SymbolTable to not be static, but instanced.		  
				  machineCode = Translator.translate(ins)+"\n"; //Adds a newline too, how helpful.		 
				  System.out.println(ins.getContent()+"\t\t\t"+machineCode);//This won't make it to output .hack file, just for visualization/verification.
				  writer.write(machineCode);				
			  }
		  }
		//and done
		  reader.close();
		  writer.close();	    
	  }
}
