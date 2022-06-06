package pl.cba.genszu.amcodetranslator.AMObjects;

import java.util.ArrayList;
import java.util.List;

public class Struct {
    private List<Object> FIELDS = new ArrayList<>();
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
                    FIELDS.add(new IntegerAM());
                    break;
                case "STRING":
                    FIELDS.add(new StringAM());
                    break;
                case "BOOL":
                    FIELDS.add(new Bool());
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
		((Bool) FIELDS.get(index)).SET(val);
    }

    public void setField(int index, int val) {
		((IntegerAM) FIELDS.get(index)).SET(val);
    }

    public void setField(int index, String val) {
		((StringAM) FIELDS.get(index)).SET(val);
    }
}
