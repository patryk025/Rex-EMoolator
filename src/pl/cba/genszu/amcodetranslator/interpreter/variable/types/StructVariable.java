package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class StructVariable extends Variable {
	public StructVariable(String name, Object value) {
		super(name);
	}

    private List<Variable> FIELDS = new ArrayList<>();
    private List<String> FIELDSNAMES = new ArrayList<>();
    private List<String> FIELDSTYPES= new ArrayList<>();

    public void addFIELDS(String fields) {
        /*TODO: do zoptymalizowania*/

        //prototyp generowania struktury
        //przykłady:
        //X<INTEGER>,Y<INTEGER>
        //POSX<INTEGER>,POSY<INTEGER>,POSZ<INTEGER>,INITEVT<STRING>,HASFALL<BOOL>,FALLWAIT<INTEGER>
        //SCENE<STRING>,JACHOWICZ<STRING>,BROSZ<STRING>
        String[] pola = fields.split(",");
        for(String pole : pola) {
            String[] podz = pole.split("<");
            podz[1] = podz[1].replace(">", "");

            FIELDSNAMES.add(podz[0]);
            FIELDSTYPES.add(podz[1]);
            switch(podz[1]){
                case "INTEGER":
                    FIELDS.add(new IntegerVariable());
                    break;
                case "STRING":
                    FIELDS.add(new StringVariable());
                    break;
                case "BOOL":
                    FIELDS.add(new BoolVariable());
                    break;
            }
        }
    }

    private int getIndex(String fieldName) {
        return FIELDSNAMES.lastIndexOf(fieldName);
    }

    public void setField(String fieldName, boolean val) {
        setField(this.getIndex(fieldName), val);
    }

    public void setField(String fieldName, int val) {
        setField(this.getIndex(fieldName), val);
    }

    public void setField(String fieldName, String val) {
        setField(this.getIndex(fieldName), val);
    }


    public void setField(int index, boolean val) {
		((BoolVariable) FIELDS.get(index)).SET(val);
    }

    public void setField(int index, int val) {
		((IntegerVariable) FIELDS.get(index)).SET(val);
    }

    public void setField(int index, String val) {
		((StringVariable) FIELDS.get(index)).SET(val);
    }

    public Variable GETFIELD(String fieldName) {
        return FIELDS.get(this.getIndex(fieldName));
    }
}
