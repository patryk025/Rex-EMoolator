package pl.cba.genszu.amcodetranslator.interpreter;
import java.util.*;
import pl.cba.genszu.amcodetranslator.lexer.*;

public class InstructionsBlock
{
	Map<String, InstructionsList> instr;
	
	public InstructionsBlock(InstructionsList instr) {
		this.instr = new HashMap<>();
		this.instr.put("", instr);
	}
	
	public InstructionsBlock() {
		this.instr = new HashMap<>();
	}
	
	/*mainly for Animo*/
	public void addListenerParam(String param, String code) {
		try
		{
			if(this.instr == null)
				this.instr = new HashMap<>();
			this.instr.put(param, Lexer.parseCode(code));
			//System.out.println(this.instr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addInstructions(InstructionsList list) {
		addInstruction("", list);
	}
	
	public void addInstruction(String param, InstructionsList list) {
		if(this.instr.get(param) == null) {
			this.instr.put(param, list);
		}
	}

	@Override
	public String toString()
	{
		String ret = "";
		for(String key : this.instr.keySet()) {
			ret += "Branch "+key+" -> "+this.instr.get(key).instr.size()+" instructions\n";
		}
		return ret;
	}
}
