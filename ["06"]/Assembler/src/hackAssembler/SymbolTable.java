package hackAssembler;

import java.util.Hashtable;

public class SymbolTable 
{
	/**
	 * The SymbolTable is a regular Java Hashtable with String keys and String values.
	 * This SymbolTable implementation stores a number of predefined symbols and their memory addresses on init,
	 * 		and (after making room) tacks on loop labels on the first pass, and user variables on the third.
	 * 
	 * NOTE	: According to HACK convention, new memory allocations only take place from 16 onwards.
	 */
	private static Hashtable<String, String> symbolTable = new Hashtable<String, String>();
	private int nextAvailableMem = 16;
	private int retVal = 0;
	
	public SymbolTable()
	{
		symbolTable.put("R0", "0");
		symbolTable.put("R1", "1");
		symbolTable.put("R2", "2");
		symbolTable.put("R3", "3");
		symbolTable.put("R4", "4");
		symbolTable.put("R5", "5");
		symbolTable.put("R6", "6");
		symbolTable.put("R7", "7");
		symbolTable.put("R8", "8");
		symbolTable.put("R9", "9");
		symbolTable.put("R10", "10");
		symbolTable.put("R11", "11");
		symbolTable.put("R12", "12");
		symbolTable.put("R13", "13");
		symbolTable.put("R14", "14");
		symbolTable.put("R15", "15");
		symbolTable.put("SCREEN", "16384");
		symbolTable.put("KBD", "24576");
		symbolTable.put("SP", "0");
		symbolTable.put("LCL", "1");
		symbolTable.put("ARG", "2");
		symbolTable.put("THIS", "3");
		symbolTable.put("THAT", "4");
		symbolTable.put("WRITE", "18");
		symbolTable.put("END", "22");
	}
//#################################################################################################################
	/**
	 * Returns the next available memory address for allocation, when called.
	 * Increments the next available memory location, for the next allocation.
	 * 
	 * @return	retVal	:	integer, an available memory address.
	 */
	public int getnextAvailableMem()	
	{	
		retVal = nextAvailableMem;
		nextAvailableMem++;
		return retVal; 
	}
//#################################################################################################################
	/**
	 * Returns the stored memory address (if present in SymbolTable) of the given symbol.
	 * 
	 * @param symbol	:	String, the received symbol.
	 * @return	s		:	String, the memory address of the received symbol.
	 */
	public String getAddress(String symbol)				
	{	
		String s = symbolTable.get(symbol); //
		return s;
	}
//#################################################################################################################
	/**
	 * Adds the given symbol:address pair to the hashtable.
	 * 
	 * @param symbol	:	String, the received symbol.
	 * @param address	:	String, the received memory Address of that symbol.
	 */
	public void addSymbol(String symbol, String address)	{	symbolTable.put(symbol, address);	}
//#################################################################################################################
	/**
	 * Checks for presence of given symbol in the Hashtable.
	 * 
	 * @param symbol	:	String, the received symbol.
	 * @return			:	true/false booleans, indicating presence/absence.
	 */
	public boolean isPresent(String symbol)
	{
		if(symbolTable.get(symbol)!=null) 	{	return true;	}
		else								{ 	return false;	}	
	}
//#################################################################################################################
	/**
	 * Mostly a debug/visualization function for clarity. Unnecessary for actual output.
	 * Prints out the contents of the Hashtable.
	 * 
	 * @param str		:	String, a number count of which pass this is (first, second, or third)
	 */
	public void print(String str)
	{
		System.out.println("=========== Contents of SymbolTable "+str+"=========================");//after/before
		for(String key : symbolTable.keySet())
		{
			System.out.println(key+"\t"+symbolTable.get(key));
		}
		System.out.println("=========================================================");
	}
//#################################################################################################################
}
