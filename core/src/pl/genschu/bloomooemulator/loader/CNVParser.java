package pl.genschu.bloomooemulator.loader;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.Interpreter;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaLexer;
import org.antlr.v4.runtime.CharStreams;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.genschu.bloomooemulator.interpreter.ast.ASTBuilderVisitor;
import pl.genschu.bloomooemulator.interpreter.ast.Node;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.types.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SoundVariable;

import java.io.*;
import java.util.*;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

public class CNVParser {
    String[] tmpParam = null;

    public void parseFile(File plik, Context context) throws IOException {
        try {
            FileReader reader = new FileReader(plik);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            StringBuilder content = new StringBuilder();
            boolean decypher = false;
            int offset = 0;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("{<")) {
                    String[] tmpParam = line.replace("{<", "").replace(">}", "").split(":");
                    offset = Integer.parseInt(tmpParam[1]);
                    if (tmpParam[0].equalsIgnoreCase("D")) offset *= -1;
                    decypher = true;
                } else {
                    content.append(line).append("\n");
                }
            }
            if (decypher) {
                Gdx.app.log("CNVParser", "Decyphering " + plik.getName() + "...");
                parseString(ScriptDecypher.decode(content.toString(), offset), context);
            } else {
                parseString(content.toString(), context);
            }
        } catch (FileNotFoundException e) {
            Gdx.app.error("CNVParser", "File not found: " + plik.getName());
        }
        assignSignals(context);
    }

    public void parseString(String string, Context context) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(string));
        String line;
        Map<String, Map<String, String>> objects = new HashMap<>();
        String currentObjectName = null;
        Map<String, String> currentObject = null;
        
        String separator;
        
        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#")) continue;

            if(line.contains(" = ")) separator = " = ";
            else separator = "=";
            if (line.startsWith("OBJECT"+separator)) {
                if (currentObjectName != null) {
                    objects.put(currentObjectName, currentObject);
                }
                currentObjectName = line.split(separator)[1];
                currentObject = new HashMap<>();
            } else if (currentObjectName != null) {
                String[] parts = line.split(separator, 2);
                if (parts.length == 2) {
                    currentObject.put(parts[0], parts[1]);
                }
            }
        }
        if (currentObjectName != null) {
            objects.put(currentObjectName, currentObject);
        }

        for (Map.Entry<String, Map<String, String>> entry : objects.entrySet()) {
            String objectName = entry.getKey();
            Map<String, String> properties = entry.getValue();
            processObject(objectName, properties, context);
        }
    }

    private void processObject(String objectName, Map<String, String> properties, Context context) {
        String type = properties.get(objectName + ":TYPE");
        if (type == null) {
            Gdx.app.error("CNVParser", "Missing TYPE for object " + objectName);
            return;
        }

        Gdx.app.log("CNVParser", "Processing object " + objectName + " of type " + type);

        Object value;

        if(type.equals("BEHAVIOUR"))
            value = properties.get(objectName + ":CODE");
        else
            value = properties.get(objectName + ":VALUE");

        try {
            Variable variable = VariableFactory.createVariable(type, objectName, value, context);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                if (
                       !property.getKey().equals(objectName + ":TYPE")
                ) {
                    if(!property.getKey().startsWith(objectName + ":ON")) {
                        variable.setAttribute(property.getKey().replace(objectName + ":", ""), property.getValue());
                    }
                    else {
                        // Store signal information for later processing
                        String signalName = property.getKey().replace(objectName + ":", "");
                        String signalCode = property.getValue();
                        variable.addPendingSignal(signalName, signalCode);
                    }
                }
            }

            if(type.equals("SEQUENCE")) {
                ((SequenceVariable) variable).loadSequence();
            }

            context.setVariable(objectName, variable);
        } catch (IllegalArgumentException e) {
            Gdx.app.error("CNVParser", "Failed to create variable " + objectName + ": " + e.getMessage());
        }
    }

    private void assignSignals(Context context) {
        for (Variable variable : context.getVariables().values()) {
            for (Map.Entry<String, String> entry : variable.getPendingSignals().entrySet()) {
                String signalName = entry.getKey();
                String signalCode = entry.getValue();

                // Process the event code and retrieve the parameters
                SignalAndParams signalAndParams = processEventCode(signalCode, context);

                if (signalAndParams == null || signalAndParams.behaviourVariable == null) {
                    Gdx.app.error("CNVParser", "Failed to get behaviour variable for signal " + signalName);
                    continue;
                }

                variable.setSignal(signalName, new Signal() {
                    @Override
                    public void execute(Object argument) {
                        List<Object> arguments = new ArrayList<>();
                        if(signalAndParams.params != null)
                            for(String param : signalAndParams.params) {
                                arguments.add(getVariableFromObject(param, context));
                            }
                        signalAndParams.behaviourVariable.getMethod("RUN", Collections.singletonList("mixed"))
                                .execute(!arguments.isEmpty() ? arguments : null);
                        Gdx.app.log("Signal", "Signal " + signalName + " done");
                    }
                });
            }
            variable.emitSignal("ONINIT");
        }
    }

    private SignalAndParams processEventCode(String code, Context context) {
        // First, check if it's a code block
        if (code.startsWith("{") && code.endsWith("}")) {
            if (code.endsWith(":}")) {
                code = code.substring(0, code.length() - 2) + ";}"; // Fix the code format
            }
            return new SignalAndParams(new BehaviourVariable("", code, context), null); // Return the behaviour variable with no params
        }

        // Check if it has parameters in parentheses
        String[] params = null;
        if (code.matches(".*\\(.*\\)")) {
            String[] tmp = code.split("\\(");
            code = tmp[0];
            params = tmp[1].substring(0, tmp[1].length() - 1).split(",");
        }

        // Retrieve the behaviour variable
        Variable behaviourVariable = context.getVariable(code, null);
        if (behaviourVariable != null && behaviourVariable.getType().equals("BEHAVIOUR")) {
            return new SignalAndParams((BehaviourVariable) behaviourVariable, params);
        } else {
            Gdx.app.error("CNVParser", "Variable " + code + " is not a BEHAVIOUR variable");
            return null;
        }
    }

    // Helper class to store the behaviour variable and its parameters
    private static class SignalAndParams {
        BehaviourVariable behaviourVariable;
        String[] params;

        SignalAndParams(BehaviourVariable behaviourVariable, String[] params) {
            this.behaviourVariable = behaviourVariable;
            this.params = params;
        }
    }
}