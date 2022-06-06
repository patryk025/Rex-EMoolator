package pl.cba.genszu.amcodetranslator.AMObjects;

import java.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.util.*;

public class MultiArray
{
	List<ArrayList<Variable>> dimensions;
	
	public MultiArray() {
		//zabezpieczenie przed NullPointerem
		dimensions = new ArrayList<>();
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
