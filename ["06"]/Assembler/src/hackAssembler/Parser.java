package hackAssembler;

import java.io.*;

public class Parser 
{
	

//#############################################################################################################
	/**
	 * a public Wrapper function to call the private function which actually does the heavy lifting. 
	 * this public Wrapper function is pretty much the entry and exit from the Parser module.
	 *   
	 * @param Instruction object, a line of assembly instruction.
	 * @param s	- SymbolTable object, the specific instance of the SymbolTable we're working with.
	 * @return	- Instruction received, but modified
	 * @throws IOException
	 */
	public static Instruction parse(Instruction ins, SymbolTable s) throws IOException 
	  {
		  return parse(ins, s, true);
	  }
//#############################################################################################################
	  /**
	   * Receives an Instruction object, detects and sets the type of instruction it is,
	   * and sends it over for splitting into its components. 
	   * Receives augmented Instruction back from split(), and returns it up the call stack.
	   * 
	   * @param ins - Instruction object, a line of assembly instruction.
	   * @param s	- SymbolTable object, the specific instance of the SymbolTable we're working with.	
	   * @param b 	- fake boolean to invoke current private function from public wrapper
	   * @return	- same Instruction object we received, but augmented.
	   * @throws IOException
	   */
	  private static Instruction parse(Instruction ins, SymbolTable s, boolean b) throws IOException 
	  {	  			
				  //identify instructions
				  ins.setType(typeOfInstruction(ins.getContent().charAt(0)));
				  if(ins.getType() != 1)
				  {					  
					  ins = split(ins, s);				  				  
				  }			  
		  return ins;	
	  }//end parse()
 
//############################################################################################################# 
	  /**
	   * Receives an Instruction object which presently has nothing but its content field set, and...
	   * a) if it's a C-type instruction, 
	   * 	extracts comp, dest, jump parts from its content,
	   * 	and sets the object's comp, dest, jump fields to these.
	   * b) if it's an A-type instruction,
	   * 	if it's content field doesnt  have a 0-9 digit after the @ sign, (i.e. it's a symbol)
	   * 		if symbol present in symbolTable
	   * 			set instruction's associated resolvedAddress value = memAddress associated with this symbol, from the symbolTable
	   * 		else
	   * 			allocate memory for this symbol in user memory space (add new key:value pair in symbolTable)
	   * 	else (it is indeed a 0-9 digit after the @ sign)
	   * 		set instruction's resolvedAddress value = anything after @ sign
	   * c) if it's a loop label
	   * 		ignore (set instruction.content = null)
	   * 		
	   * @param ins - Instruction object, a line of assembly instruction.
	   * @param s	- SymbolTable object, the specific instance of the SymbolTable we're working with.
	   * @return ins - The same Instruction object we received, augmented with comp, dest, jump, resolvedAddress fields as req.
	   */
	  private static Instruction split(Instruction ins, SymbolTable s)
	  {
		  if(ins.getType() == 0)
		  {
			  //is C-instruction
			  //FORMAT :  dest = comp;jump
			  //needs splitting unlike A-instruction
			  //delimiters are : '=', ';'
			  //also this section needs a second if (nested) to resolve "0;jmp" like instructions.(DEST = NULL for these)
					  if(ins.getContent().indexOf('=')!=-1)// an equals sign *IS* present (i.e. dest part isnt null)
					  {
						  ins.setDest(ins.getContent().substring(0,  ins.getContent().indexOf('='))); 
								
						  //Check for jumps, set comp and jump substrings accordingly.
								  switch(jumpPresent(ins.getContent()))
								  {
								  case 1 :
									  ins.setComp(ins.getContent().substring(ins.getContent().indexOf('=')+1,  ins.getContent().indexOf(';')));
									  ins.setJump(ins.getContent().substring(ins.getContent().indexOf(';')+1, ins.getContent().length()));
								  case 0 :
									  ins.setComp(ins.getContent().substring(ins.getContent().indexOf('=')+1,  ins.getContent().length()));
									  ins.setJump(null);
								  }
					  }
					  else // an equals sign ISNT present (i.e. dest part will be null - D;JGT, 0;JMP-like instructions
					  {
								  //set dest = null for these. Jump will never be null for these instructions, no need to check.
								ins.setDest(null);
								ins.setComp(ins.getContent().substring(0,  ins.getContent().indexOf(';')));
								ins.setJump(ins.getContent().substring(ins.getContent().indexOf(';')+1, ins.getContent().length()));
					  }
			  }
	
		  else if(ins.getType() == -1)
		  {
				//is A-instruction
				//FORMAT :  @<address or symbol>
				//needs no splitting.
				//Does need checking to see if the referenced thing is a memory address (@234, @125), or a symbol.(@i, @R2, @LCL)
				
			  char x = ins.getContent().charAt(1);
			  String content = ins.getContent();
			  boolean b = false;
			  b = (x < 48 || x > 57)? true:false;
			  if(b == true)	//check if first char after the @ (2nd char overall) not a number
			  {
							// Would mean this is a symbol. Need to check if in symboLTable already, or an user variable that needs allocation.
						  //check if substring(1, ins.getContent.length()) is present in symbolTable (this is for @R2, @LCL, etc)
						  boolean isPresent = s.isPresent(content.substring(1, content.length()));
						 if(isPresent == true)
							 {
								 String addr = s.getAddress(content.substring(1, content.length()));
								 ins.setresolvedAddress(addr);
							 }
						 else
						 {
								//if absent, set ins.resolvedAddress to nextAvailableMem (converted to string) (this will be for user vars, @i, @j etc.)
								 String nextAvailableMem = ""+s.getnextAvailableMem();
								 //add the entry to SymbolTable
								 s.addSymbol(content.substring(1,content.length()), nextAvailableMem);
								 //is this needed? I dont think so...
								 ins.setresolvedAddress(nextAvailableMem);	
						 }
			  }
			  else	// it is a number indeed.
			  {
				  // Would mean this is the simplest a-instruction
				  ins.setresolvedAddress(content.substring(1, content.length()));
			  }
		  }//end of checks for A-instructions
		
		  else if(ins.getType() == 2) //This section must specify what the parser will set the ins object's fields for a "(" encounter. getResolvedADdress willbe null oterwise.
		  {
			  //IGNORE THEM LOL. 
			  ins.setContent("");    
		  }
		  return ins;  
	  }

//#############################################################################################################
	  /**
	   * Receives an Instruction object, scans its first char. to see if it's '('
	   * If so, strips the open and close paranthesis symbols and stores the substring in between, if absent in symbolTable.
	   * Memory allocation (for creation of a new key:value pair in symbolTable) is basically the line number of this instruction in the .asm file.
	   * 
	   * @param ins			- Instruction object, a line of assembly instruction.
	   * @param s			- SymbolTable object, the specific instance of the SymbolTable we're working with.
	   * @param linePosition - integer, line number being processed (of the .asm file)
	   * @return ins		- The same Instruction object we received, but augmented with a resolvedAddress (if the symbol was present already)
	   * 
	   * NOTE : Really, the main thing this function does is adding a key:value pair to the SymbolTable for unseen symbols. Used in First Pass.
	   */
	  public static Instruction storeLabels(Instruction ins, SymbolTable s, int linePosition)
	  {
		  if(ins.getContent().charAt(0) == '(')
		  {
			  //this is for loop labels, anything enclosed inside parentheses
			  String label = ins.getContent();
			  label = label.substring(1, label.indexOf(")"));
			  
			  if(s.isPresent(label))
			  {
				  //symbol is present, grab resolvedAddress and set
				  String address = s.getAddress(label);
				  ins.setresolvedAddress(address);
			  }
			  else
			  {
				  //absent, allocate mem (line number)
				  String linePos = ""+(linePosition);
				  s.addSymbol(label, linePos);
			  }	  
		  }
		  return ins;
	  }
//############################################################################################################# 
						//BEYOND HERE BE YE OLDE UTILITY FUNCTIONS
//############################################################################################################# 
	 /**
	  * Scans an instruction String for the "@" symbol. Presence indicates a jump directive in this instruction
	  * .
	  * @param instruction	- String, a line of assembly Instruction.
	  * @return	1 or 0		- flags representing presence or absence of a jump.
	  */
	 private static int jumpPresent(String instruction)
	  {
		  for(int i = 0; i < instruction.length() ; i++)
		  {
			  if(instruction.charAt(i) == ';')	{	return 1;	}
		  }
		  return 0;
	  }
//#############################################################################################################  
	  /**
	   * Receives the first character of an assembly instruction String, decides and returns which type of instruction it is.
	   * 
	   * @param ch 			- first character of the assembly instruction string 
	   * @return retCode 	- integer, code for the type of instruction. -1 = A, 0 = C, 1 = ignore, 2 = labels.
	   */
	  private static int typeOfInstruction(char ch)
	  {
		  int retCode = 0;
		  if		(ch == '@')																								{	retCode = -1; }	//A-instruction
		  else if	(ch == 'A' || ch== 'M' || ch== 'D' || ch == '0')				{	retCode = 0;	}	//C-instruction
		  else if	(ch == '(')																							{	retCode = 2;	}	// labels
		  else																														{	retCode = 1;	}	//ignore
		  return retCode;
	  }
	 
//#############################################################################################################
	  /**
	   * Strips white and tabspaces from received String var, send back a rebuilt string free from these components.
	   * If on second pass, remove loop labels, too.
	   * 
	   * @param str		-  String, a line of assembly instruction.
	   * @return rebuilt - a rebuilt String object free of whitespaces (and, if needed, loop labels too)
	   */
	  
	  public static String strip(String str)
	  {
		  String rebuilt = "";
		  for(int i = 0 ; i < str.length() ; i++)
		  {
			  if(str.charAt(i)!= ' ' && str.charAt(i)!= '\t')//account for tab spaces too
			  {
				  if(str.charAt(i)== '/')	{	break;	}					//get rid of inline comments
				  else						{	rebuilt+=str.charAt(i);	}
				  
			  }
		  }
		  return rebuilt;
	  }
	  public static String strip2ndPass(String str)
	  {
		  String rebuilt = "";
		  for(int i = 0 ; i < str.length() ; i++)
		  {
			  if(str.charAt(i)!= ' ' && str.charAt(i)!= '\t')//account for tab spaces too
			  {
				  if(str.charAt(i)== '/' || str.charAt(i)== '(')	{	break;	}	//get rid of inline comments AND loop labels
				  else						{	rebuilt+=str.charAt(i);	}
				  
			  }
		  }
		  return rebuilt;
	  }
//#############################################################################################################
}
