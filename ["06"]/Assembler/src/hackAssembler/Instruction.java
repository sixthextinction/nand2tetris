package hackAssembler;

public class Instruction 
{
	//definition for an Instruction
	private String content;
	private String comp;
	private String dest;
	private String jump;
	private String resolvedAddress;
	private int type;
	
	public Instruction()	
	{
		
	}
	
	public String setContent(String content)
	{	
		this.content = content;
		this.comp = null;
		this.dest = null;
		this.jump = null;
		this.resolvedAddress = null;
		return content;
	}//fucking LOL.
	
	public void setComp(String comp)							{	this.comp = comp;		}
	public void setDest(String dest)							{	this.dest = dest;		}
	public void setJump(String jump)							{	this.jump = jump;		}
	public void setType(int type)								{	this.type = type;		}
	public void setresolvedAddress(String resolvedAddress)		{	this.resolvedAddress = resolvedAddress;		}
	
	public String getContent()									{	return content;			}
	public String getComp()										{	return comp;			}
	public String getDest()										{	return dest;			}
	public String getJump()										{	return jump;			}
	public int getType()										{	return type;			}
	public String getresolvedAddress()							{	return resolvedAddress;	}
	
	

}
