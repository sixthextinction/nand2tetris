package hackAssembler;

public class Translator 
{
	/**
	 * Once again, we're using the model of a public "wrapper" function calling the private work functions as needed.
	 * Fairly self explanatory; an Instruction object is received, and a string of translated machine code is returned.
	 * C and A instructions are handled differently by the overloaded translate() function.
	 * Loop labels (the else part) are ignored.
	 * 
	 * @param ins	 : Instruction object, a line of assembly instruction.
	 * @return		 : String, the line of translated machine code for this instruction.
	 */
	public static String translate(Instruction ins)
	{
		
		
		if		(ins.getType() == 0)			{	return translate(ins.getDest(),ins.getComp(),ins.getJump());	}
		else if	(ins.getType() == -1)			{	return translate(ins, true); }
		else									{ 	return null;	}
		
	}
//#############################################################################################################
	/**
	 * This is the private work function for A instructions, and returns a string of translated machine code.
	 * The A-instruction object's resolved memory address (a field of said object) is read into an integer, 
	 * 		which in turn, is converted into binary and stored as a double.
	 * We don't want exponential representation of the double, so we're formatting the binary memAddress string appropriately.
	 * HACK being a 16-bit architecture, needs 16 bit machine codes. Padding with the necessary # of zeroes is thus necessary, and done.
	 * 
	 * @param ins	 : Instruction object, a line of assembly instruction.
	 * @param b		 : Fake boolean to invoke this function from wrapper.
	 * @return		 : String, the line of translated machine code for this instruction.
	 */
	
	private static String translate(Instruction ins, boolean b)
	  {
		  int memAddress = 0;		
		  int i = 1;		//dumb mistake to have this = 0
		  String appendToLeft = "";
		  String retString = ""; // retString = "0" + appendToLeft + memAddress, basically.
		
		memAddress = Integer.parseInt(ins.getresolvedAddress());
		double binaryMemAddress = toBinary(memAddress);//this is a double, receive it as a formatted string to avoid exp. representation
		String memAddressString = String.format("%.0f", binaryMemAddress);
		retString = ("" + memAddressString);
		int difference = 0 ;
		if( (difference = 15 - retString.length()) != 0)
		{
			
			//append 0's to left
			while (i <= difference)
			{
				appendToLeft += "0";
				i++;
			}
		}
	      retString = "0" + appendToLeft + memAddressString;		
		  return retString;
		  
	  }
//#############################################################################################################
	/**
	 * This is the private work function for C-instructions, and returns a string of translated machine code.
	 * The C-instruction object's different fields (dest, comp, jump) from Parser, are received as Strings,
	 * 		and their binary opcode equivalents (destBin, compBin, jumpBin) are decided based on the content of said Strings.
	 * The 13th bit (from LSB) (called 'a') is decided by the presence or absence of 'M' in the comp string.
	 * 
	 * @param ins	 : Instruction object, a line of assembly instruction.
	 * @param dest	 : The Destination part of this instruction.
	 * @param comp	 : The Compute part of this instruction.
	 * @param jump	 : The Jump part of this instruction.
	 * @return		 : String, the line of translated machine code for this instruction.
	 */
	  
	private static String translate(String dest, String comp, String jump)
	  {
		  String destBin = null, compBin = null, jumpBin = null;		
		  String a = "0";
		  
		  String retString = ""; // retString = "111" + a + comp + dest + jump
		  
		  for(int i = 0; i < comp.length() ; i++)
		  {
			  if(comp.charAt(i) == 'M') {	a = "1";	}
		  }
		  
		  //comp 
		  if		(comp.equals("0")==true)									{	compBin = "101010";	}
		  else if 	(comp.equals("1")==true)									{	compBin = "111111";	}
		  else if 	(comp.equals("-1")==true)									{	compBin = "111010";	}
		  else if 	(comp.equals("D")==true)									{	compBin = "001100";	}
	  	  else if 	(comp.equals("A")==true	|| comp.equals("M")==true)			{	compBin = "110000";	}
		  else if 	(comp.equals("!D")==true)									{	compBin = "001101";	}
		  else if 	(comp.equals("!A")==true 	|| comp.equals("!M")==true)		{	compBin = "110001";	}
		  else if 	(comp.equals("-D")==true)									{	compBin = "001111";	}
		  else if 	(comp.equals("-A")==true 	|| comp.equals("-M")==true)		{	compBin = "110011";	}
		  else if 	(comp.equals("D+1")==true)									{	compBin = "011111";	}
		  else if 	(comp.equals("A+1")==true 	|| comp.equals("M+1")==true)	{	compBin = "110111";	}
		  else if 	(comp.equals("D-1")==true)									{	compBin = "001110";	}
		  else if 	(comp.equals("A-1")==true 	|| comp.equals("M-1")==true)	{	compBin = "110010";	}
		  else if 	(comp.equals("D+A")==true 	|| comp.equals("D+M")==true)	{	compBin = "000010";	}
		  else if 	(comp.equals("D-A")==true	|| comp.equals("D-M")==true)	{	compBin = "010011";	}
		  else if 	(comp.equals("A-D")==true	|| comp.equals("M-D")==true)	{	compBin = "000111";	}
		  else if 	(comp.equals("D&A")==true 	|| comp.equals("D&M")==true)	{	compBin = "000000";	}
		  else if 	(comp.equals("D|A")==true 	|| comp.equals("D|M")==true)	{	compBin = "010101";	}
		  else																	{	compBin = "FALSE";	}	
		  
		  //dest
		  if 		(dest 				== null)								{	destBin = "000"; 	}
		  else if 	(dest.equals("M") 	== true)								{	destBin = "001";	}
		  else if 	(dest.equals("D") 	== true)								{	destBin = "010";	}
		  else if 	(dest.equals("MD") 	== true)								{	destBin = "011";	}
		  else if 	(dest.equals("A") 	== true)								{	destBin = "100";	}
		  else if 	(dest.equals("AM") 	== true)								{	destBin = "101";	}
		  else if 	(dest.equals("AD") 	== true)								{	destBin = "110";	}
		  else if 	(dest.equals("AMD") == true)								{	destBin = "111";	}
		  
		  //jump
		  if 		(jump 				== null)								{	jumpBin = "000";	}
		  else if	(jump.equals("JGT") == true)								{	jumpBin = "001";	}
		  else if	(jump.equals("JEQ") == true)								{	jumpBin = "010";	}
		  else if	(jump.equals("JGE") == true)								{	jumpBin = "011";	}
		  else if	(jump.equals("JLT") == true)								{	jumpBin = "100";	}
		  else if	(jump.equals("JNE") == true)								{	jumpBin = "101";	}
		  else if	(jump.equals("JLE") == true)								{	jumpBin = "110";	}
		  else if	(jump.equals("JMP") == true)								{	jumpBin = "111";	}
		  
		  retString = 111 + a + compBin + destBin + jumpBin;
		  // where a = 1 if comp contains 'M' anywhere
		  return retString;
	  }
//#############################################################################################################
	/**
	 * Run of the mill function to convert a received decimal number into its binary equivalent.  
	 * @param decimal	: integer, received decimal
	 * @return			: Double, converted binary
	 */
	public static double toBinary(int decimal)
	  {
		  double power = 0;
		  double binary = 0;
		  
		  while(decimal > 0)
		  {
			  binary += (decimal % 2) * (Math.pow(10, power));  //constructing a discrete number (binary) from individual digits.
			  decimal = decimal/2;
			  power++;											//need to increment power when moving towards MSB (to the left) 
		  }
		  return binary;
	  }
//#############################################################################################################
}
