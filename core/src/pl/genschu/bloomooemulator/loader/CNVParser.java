package pl.genschu.bloomooemulator.loader;

import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.exceptions.OneBreakException;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.utils.SignalAndParams;

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
        runOnInitOnVariables(context);
    }

    public void parseString(String string, Context context) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(string));
        String line;
        Map<String, Map<String, String>> objects = new LinkedHashMap<>();
        String currentObjectName = null;
        Map<String, String> currentObject = null;
        
        String separator;
        
        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#")) continue;

            if(line.contains(" = ")) separator = " = ";
            else separator = "=";
            if (line.startsWith("OBJECT"+separator)) {
                if (currentObjectName != null) {
                    if(objects.containsKey(currentObjectName)) {
                        Gdx.app.log("CNVParser", "Redefinition of object: " + currentObjectName + ". Overwriting events..."); // TODO: check if other parameters are overwritten
                        Map<String, String> oldObject = objects.get(currentObjectName);
                        oldObject.putAll(currentObject);
                    }
                    else {
                        objects.put(currentObjectName, currentObject);
                    }
                }
                currentObjectName = line.split(separator)[1];
                currentObject = new LinkedHashMap<>();
            } else if (currentObjectName != null) {
                String[] parts = line.split(separator, 2);
                if (parts.length == 2) {
                    currentObject.put(parts[0], parts[1]);
                }
            }
        }
        if (currentObjectName != null) {
            if(objects.containsKey(currentObjectName)) {
                Gdx.app.log("CNVParser", "Redefinition of object: " + currentObjectName + ". Overwriting events..."); // TODO: check if other parameters are overwritten
                Map<String, String> oldObject = objects.get(currentObjectName);
                oldObject.putAll(currentObject);
            }
            else {
                objects.put(currentObjectName, currentObject);
            }
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
        String iniSection = context.getGame().getCurrentScene().toUpperCase();

        if(type.equals("BEHAVIOUR"))
            value = properties.get(objectName + ":CODE");
        else {
            value = properties.get(objectName + ":VALUE");
            try {
                String valueFromIni = context.getGame().getGameINI().get(context.getGame().getApplicationVariable().getName().toUpperCase(), objectName.toUpperCase());
                if (valueFromIni != null) {
                    properties.put(objectName + ":INIT_VALUE", properties.get(objectName + ":VALUE"));
                    properties.put(objectName + ":VALUE", valueFromIni);
                    iniSection = context.getGame().getApplicationVariable().getName().toUpperCase();
                } else {
                    valueFromIni = context.getGame().getGameINI().get(context.getGame().getCurrentEpisode().toUpperCase(), objectName.toUpperCase());
                    if (valueFromIni != null) {
                        properties.put(objectName + ":INIT_VALUE", properties.get(objectName + ":VALUE"));
                        properties.put(objectName + ":VALUE", valueFromIni);
                        iniSection = context.getGame().getCurrentEpisode().toUpperCase();
                    }
                    else {
                        // one last chance
                        valueFromIni = context.getGame().getGameINI().get(context.getGame().getCurrentScene().toUpperCase(), objectName.toUpperCase());
                        if (valueFromIni != null) {
                            properties.put(objectName + ":INIT_VALUE", properties.get(objectName + ":VALUE"));
                            properties.put(objectName + ":VALUE", valueFromIni);
                            iniSection = context.getGame().getCurrentScene().toUpperCase();
                        }
                    }
                }
            } catch (NullPointerException ignored) {}
        }

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

            variable.setIniSection(iniSection);

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

                        Variable oldThis = signalAndParams.behaviourVariable.getContext().getThisVariable();
                        if(!(variable instanceof ConditionVariable)) { // little hack to not making conditions as true
                            signalAndParams.behaviourVariable.getContext().setThisVariable(variable);
                        }

                        if(signalAndParams.params != null)
                            for(String param : signalAndParams.params) {
                                arguments.add(getVariableFromObject(param, context));
                            }

                        try {
                            signalAndParams.behaviourVariable.getMethod(signalAndParams.behaviourVariable.getAttribute("CONDITION") != null ? "RUNC" : "RUN", Collections.singletonList("mixed"))
                                    .execute(!arguments.isEmpty() ? arguments : null);
                        } catch (BreakException | OneBreakException ignored) {}

                        signalAndParams.behaviourVariable.getContext().setThisVariable(oldThis);
                        Gdx.app.log("Signal", "Signal " + signalName + " done");
                    }
                });
            }
        }
    }

    private void runOnInitOnVariables(Context context) {
        List<Variable> variables = new ArrayList<>(context.getVariables().values());

        List<String> typeOrder = Arrays.asList(
                "BEHAVIOUR",           // 1. Procedury
                "INTEGER|STRING|DOUBLE|BOOLEAN",   // 2. Prymitywy
                "ARRAY",               // 3. Tablice
                "CONDITION",           // 4. Warunki
                "ANIMO",               // 5. Animacje
                "IMAGE",               // 6. Obrazy
                "SOUND",               // 7. Dźwięki
                "FONT",                // 8. Fonty
                "BUTTON",              // 9. Przyciski
                "TEXT",                // 10. Teksty
                "SEQUENCE",            // 11. Sekwencje
                "MOUSE", "KEYBOARD"    // 12. Urządzenia wejściowe
        );

        variables.sort((v1, v2) -> {
            String var1Type = v1.getType();
            String var2Type = v2.getType();

            if(var1Type.matches("INTEGER|STRING|DOUBLE|BOOLEAN")) {
                var1Type = "INTEGER|STRING|DOUBLE|BOOLEAN";
            }

            if(var2Type.matches("INTEGER|STRING|DOUBLE|BOOLEAN")) {
                var2Type = "INTEGER|STRING|DOUBLE|BOOLEAN";
            }

            int index1 = typeOrder.indexOf(var1Type);
            int index2 = typeOrder.indexOf(var2Type);

            index1 = (index1 == -1) ? Integer.MAX_VALUE : index1;
            index2 = (index2 == -1) ? Integer.MAX_VALUE : index2;
            return Integer.compare(index1, index2);
        });

        // load every buttons before INIT phase
        for (Variable variable : variables) {
            if(variable instanceof ButtonVariable) {
                ButtonVariable buttonVariable = (ButtonVariable) variable;
                buttonVariable.load();
                buttonVariable.getRect();
            }
        }

        for (Variable variable : variables) {
            Gdx.app.log("CNVParser", "ONINIT for " + variable.getName() + " (" + variable.getType() + ")");
            variable.init();
            variable.emitSignal("ONINIT");
        }
    }

    public SignalAndParams processEventCode(String code, Context context) {
        // First, check if it's a code block
        code = code.trim();
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
        Variable behaviourVariable = context.getVariable(code);
        if (behaviourVariable != null && behaviourVariable.getType().equals("BEHAVIOUR")) {
            return new SignalAndParams((BehaviourVariable) behaviourVariable, params);
        } else {
            Gdx.app.error("CNVParser", "Variable " + code + " is not a BEHAVIOUR variable");
            return null;
        }
    }
}