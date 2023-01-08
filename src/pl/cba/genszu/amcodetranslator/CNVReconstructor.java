package pl.cba.genszu.amcodetranslator;
import java.lang.reflect.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.util.*;
import pl.cba.genszu.amcodetranslator.lexer.*;
import pl.cba.genszu.amcodetranslator.lexer.tree.*;

public class CNVReconstructor
{
	private static List<String> logicalOperators = Arrays.asList(new String[] {Constants.OR, Constants.AND});
	private static List<String> compareOperators = Arrays.asList(new String[] {Constants.GEQ, Constants.GTR, Constants.EQU, Constants.NEQ, Constants.LEQ, Constants.LSS});
	private static List<String> arithmeticOperators = Arrays.asList(new String[] {Constants.PLUS, Constants.MINUS, Constants.MUL, Constants.DIV, Constants.MOD});
	
	private static void traverseInOrder(List<Token> nodes, Node node) {
		if (node != null) {
			traverseInOrder(nodes, node.left);
			nodes.add(node.value);
			traverseInOrder(nodes, node.right);
		}
	}
	
	private static String instrObjToString(InstructionsList list) {
		StringBuilder sb = new StringBuilder();
		
		for(BinaryTree tree : list.instr) {
			sb.append(instrObjToString(tree.root));
		}
		
		return sb.toString();
	}
	
	private static String instrObjToString(Node node) {
		if(node == null) return "";
		
		StringBuilder sb = new StringBuilder();
		
		Token token = node.value;
		
		if(token.type == Constants.BRANCH) { //@IF
			Branch branch = (Branch) node;
			
			String onTrue = instrObjToString(branch.left);
			String onFalse = instrObjToString(branch.right);
			String condition = instrObjToString(branch.condition);
			
			sb.append("@IF(\"").append(condition).append("\",\"").append(onTrue).append("\",\"").append(onFalse).append("\");");
		}
		else if(token.type == Constants.WHILE) { //@WHILE
			Branch branch = (Branch) node;

			String code = instrObjToString(branch.left);
			String condition = instrObjToString(branch.condition);
			
			sb.append("@WHILE(\"").append(condition).append("\",\"").append(code).append("\");");
		}
		else if(token.type == Constants.LOOP) { //@LOOP
			Branch branch = (Branch) node;

			String code = instrObjToString(branch.left);
			String condition = instrObjToString(branch.condition);
			String incrCode = instrObjToString(branch.loopIncrement);
			
			String[] tmp = condition.split("&&");
			String startVal = tmp[0].replace("_I_>'", "");
			String stopVal = tmp[1].replace("_I_<", "");
			String incr = incrCode.replace("_I_+", "");

			sb.append("@LOOP(\"").append(code).append("\",\"").append(startVal).append("\",\"").append(stopVal).append("\",\"").append(incr).append("\");");
		}
		else if(token.type == Constants.VARNAME) { //
			sb.append(token.value);
			sb.append(instrObjToString(node.left));
		}
		else if(token.type == Constants.FIRE) { //^
			sb.append("^");
			sb.append(node.left.value.value); //FUNCNAME
			sb.append("(");
			if(node.right != null) {
				if(node.right.value.value != null)
					sb.append(node.right.value.value);
			}
			sb.append(");");
		}
		else if(logicalOperators.contains(token.type)) {
			List<Token> nodes = new ArrayList<>();
			
			traverseInOrder(nodes, node);
			
			for(Token n : nodes) {
				System.out.println(n);
			}
		}
		else if(compareOperators.contains(token.type)) {
			List<Token> nodes = new ArrayList<>();

			traverseInOrder(nodes, node);

			for(Token n : nodes) {
				System.out.println(n);
			}
		}
		else if(arithmeticOperators.contains(token.type)) {
			List<Token> nodes = new ArrayList<>();

			traverseInOrder(nodes, node);

			for(Token n : nodes) {
				System.out.println(n);
			}
		}
		else {
			sb.append(node.value.value);
		}
		
		return sb.toString();
	}
	
	
	//for tests
	public static String reconstruct(List<Variable> code) {
		StringBuilder sb = new StringBuilder();
		
		for(Variable var : code) {
			String variableName = var.getName();
			sb.append("OBJECT=").append(variableName).append("\n");
			sb.append(variableName).append(":TYPE=").append(var.getType().toUpperCase()).append("\n");
			
			Object obj = var.getClassObj();
			Class<? extends Object> cls = obj.getClass();
			System.out.println(cls.getCanonicalName());
			System.out.println("Variable name: "+variableName);

			Field[] fields = cls.getDeclaredFields();
			for(Field field : fields) {
				if(field.getName().startsWith("adrt")) continue;
				field.setAccessible(true);
				try
				{
					Object field_val = field.get(obj);
					if(field_val == null) continue;
					String type = field_val.getClass().getCanonicalName();
					if(type.endsWith("InstructionsBlock")){
						InstructionsBlock instr = (InstructionsBlock) field_val;
						Map<String, InstructionsList> map_instr_list = instr.getInstr();
						//InstructionsList instr_list;

						for (String key : map_instr_list.keySet()) {
							//instr_list = map_instr_list.get(key);
							
							System.out.print(field.getName() + (key != "" ? "." + key : "") + " = "); 
							System.out.print("(" + type + ") ");
							System.out.println(instrObjToString(map_instr_list.get(key)));
							sb.append(variableName).append(":").append(field.getName());
							if(key != "")
								sb.append("^").append(key);
							sb.append("=").append(instrObjToString(map_instr_list.get(key))).append("\n");
						}
					}
					else {
						System.out.print(field.getName() + " = "); 
						System.out.print("(" + type +") ");
						System.out.println(field_val);
						sb.append(variableName).append(":").append(field.getName()).append("=").append(field_val).append("\n");
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					System.out.println("Error, no co zrobisz?");
				}
			}
			System.out.println();
			sb.append("\n");
		}

		return sb.toString();
	}
}
