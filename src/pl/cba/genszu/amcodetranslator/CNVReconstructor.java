package pl.cba.genszu.amcodetranslator;
import pl.cba.genszu.amcodetranslator.interpreter.util.*;
import java.util.*;
import java.lang.reflect.*;
import pl.cba.genszu.amcodetranslator.utils.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class CNVReconstructor
{
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
						
						for(String key : instr.getInstr().keySet()) {
							System.out.print(field.getName() + (key != "" ? "." + key : "") + " = "); 
							System.out.print("(" + type +") ");
							System.out.println(instr.getInstr().get(key));
							sb.append(variableName).append(":").append(field.getName()).append("^").append(key).append("=").append(instr.getInstr().get(key)).append("\n");
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
