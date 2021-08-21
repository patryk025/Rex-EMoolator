package pl.cba.genszu.amcodetranslator.AMObjects;

import java.util.ArrayList;
import java.util.List;

public class Group {
    List<String> nwm = new ArrayList<>();
    /*TODO: check meaning*/

    public void ADD(String nazwa) {
        nwm.add(nazwa);
    }
}
