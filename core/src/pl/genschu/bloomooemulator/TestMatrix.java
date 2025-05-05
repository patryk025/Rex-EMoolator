package pl.genschu.bloomooemulator;

import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ArrayVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.MatrixVariable;

import java.util.List;

public class TestMatrix {
    public static void main(String[] args)
    {
        MatrixVariable test = new MatrixVariable("test", null);

        test.setAttribute("SIZE", new Attribute("STRING", "20,11"));

        ArrayVariable data = test.getData();

        List<Variable> elements = data.getElements();

        String[] rawData = "6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v2v0v0v2v2v2v2v2v2v2v2v2v2v2v6v6v6v6v6v6v2v2v0v2v2v2v2v2v2v2v2v2v2v2v6v6v6v6v6v6v2v2v2v2v2v2v2v2v2v2v2v2v2v2v6v6v6v6v6v6v2v2v0v2v2v2v2v2v2v2v2v2v2v2v6v6v6v6v0v1v1v1v2v1v1v1v1v1v1v1v1v1v1v1v1v1v6v6v0v1v1v4v0v1v1v1v4v1v1v1v1v1v1v4v1v1v6v6v0v1v4v1v2v4v1v1v1v1v1v1v1v1v1v1v1v1v6v6v0v1v1v1v2v1v1v1v1v1v4v4v1v1v4v1v1v1v6v6v0v99v0v0v2v1v1v1v1v1v1v1v1v1v1v1v1v1v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6v6".split("v");

        elements.clear();

        for (String value : rawData) {
            elements.add(new IntegerVariable("", Integer.parseInt(value), null));
        }

        test.fireMethod("TICK");

        for(int i = 0; i < test.getPendingMoves().size(); i++) {
            int[] move = test.getPendingMoves().get(i);
            System.out.println(move[0] + " " + move[1] + " " + move[2]);
        }
    }
}
