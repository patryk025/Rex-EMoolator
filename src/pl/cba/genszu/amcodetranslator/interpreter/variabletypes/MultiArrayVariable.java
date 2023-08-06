package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;

import java.util.*;

public class MultiArrayVariable extends Variable {
	List<ArrayList<Variable>> dimensions;

	public MultiArrayVariable() {
		super(null, null);
		//zabezpieczenie przed NullPointerem
		this.dimensions = new ArrayList<>();
	}
	
	public MultiArrayVariable(String name, Object value) {
		super(name, value);
	}
	
	/***
	   * Ustawia ilość wymiarów w tablicy (tutaj list w listach)
	   * W tym momencie czyści ją przy zmianie (do sprawdzenia jak się zachowuje silnik)
	 ***/
	public void setDIMENSIONS(int dimensions) {
		this.dimensions.clear();
		
		for(int i = 0; i < dimensions; i++) {
			this.dimensions.add(new ArrayList<Variable>());
		}
	}
}
