package pl.cba.genszu.amcodetranslator.interpreter;
import java.util.*;
import pl.cba.genszu.amcodetranslator.lexer.tree.*;

public class InstructionsList
{
	public List<BinaryTree> instr;
	
	public InstructionsList() {
		this.instr = new ArrayList<>();
	}
	
	public void addInstruction(BinaryTree tree) {
		this.instr.add(tree);
	}
	
	/*public Node getRoot() {
		//new Exception().printStackTrace();
		if(this.instr.size() > 1) 
			System.out.println("WARNING: number of instructions greater than one, posible data loss...");
		return this.instr.get(0).root;
	}*/
}
