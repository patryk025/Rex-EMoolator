package pl.genschu.bloomooemulator.loader.v2;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.OneBreakException;
import pl.genschu.bloomooemulator.interpreter.factories.LegacyVariableFactory;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.ConditionVariable;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static pl.genschu.bloomooemulator.interpreter.v1.util.VariableHelper.getVariableFromObject;

/**
 * CNVParserV2 - Improved CNV file parser with cleaner signal handling.
 *
 * Key improvements over v1:
 * 1. Structured signal definitions (SignalDefinition) instead of messy closures
 * 2. Cleaner separation of concerns
 * 3. Better error handling and logging
 * 4. Same functionality, cleaner code
 *
 * What stays the same:
 * - CNV file format parsing
 * - VariableFactory usage
 * - INI loading with TOINI support
 * - Initialization order
 * - Context integration (even though it's a "god class")
 */
public class CNVParserV2 {

    /**
     * Parses a CNV file and populates the context with variables.
     */
    public void parseFile(File file, Context context) throws IOException {
        try {
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            StringBuilder content = new StringBuilder();
            boolean decipher = false;
            int offset = 0;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("{<")) {
                    String[] tmpParam = line.replace("{<", "").replace(">}", "").split(":");
                    offset = Integer.parseInt(tmpParam[1]);
                    if (tmpParam[0].equalsIgnoreCase("D")) {
                        offset *= -1;
                    }
                    decipher = true;
                } else {
                    content.append(line).append("\n");
                }
            }

            if (decipher) {
                Gdx.app.log("CNVParserV2", "Deciphering " + file.getName() + "...");
                parseString(ScriptDecypher.decode(content.toString(), offset), context);
            } else {
                parseString(content.toString(), context);
            }
        } catch (FileNotFoundException e) {
            Gdx.app.error("CNVParserV2", "File not found: " + file.getName());
            throw e;
        }

        assignSignals(context);
        runOnInitOnVariables(context);
    }

    /**
     * Parses a CNV string and populates the context with variables.
     */
    public void parseString(String string, Context context) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(string));
        String line;
        Map<String, Map<String, String>> objects = new LinkedHashMap<>();
        String currentObjectName = null;
        Map<String, String> currentObject = null;

        String separator;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) continue;

            separator = line.contains(" = ") ? " = " : "=";

            if (line.startsWith("OBJECT" + separator)) {
                if (currentObjectName != null) {
                    mergeOrAddObject(objects, currentObjectName, currentObject);
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
            mergeOrAddObject(objects, currentObjectName, currentObject);
        }

        for (Map.Entry<String, Map<String, String>> entry : objects.entrySet()) {
            String objectName = entry.getKey();
            Map<String, String> properties = entry.getValue();
            processObject(objectName, properties, context);
        }
    }

    /**
     * Merges or adds an object to the objects map.
     * Handles redefinition by merging properties.
     */
    private void mergeOrAddObject(Map<String, Map<String, String>> objects,
                                   String objectName,
                                   Map<String, String> objectProperties) {
        if (objects.containsKey(objectName)) {
            Gdx.app.log("CNVParserV2", "Redefinition of object: " + objectName + ". Merging properties...");
            Map<String, String> existing = objects.get(objectName);
            existing.putAll(objectProperties);
        } else {
            objects.put(objectName, objectProperties);
        }
    }

    /**
     * Processes a single object definition from CNV file.
     */
    private void processObject(String objectName, Map<String, String> properties, Context context) {
        String type = properties.get(objectName + ":TYPE");
        if (type == null) {
            Gdx.app.error("CNVParserV2", "Missing TYPE for object " + objectName);
            return;
        }

        Gdx.app.log("CNVParserV2", "Processing object " + objectName + " of type " + type);

        Object value = extractValue(objectName, properties, type, context);
        String iniSection = determineIniSection(objectName, context);

        try {
            Variable variable = LegacyVariableFactory.createVariable(type, objectName, value, context);
            applyProperties(variable, objectName, properties);
            variable.setIniSection(iniSection);
            context.setVariable(objectName, variable);
        } catch (IllegalArgumentException e) {
            Gdx.app.error("CNVParserV2", "Failed to create variable " + objectName + ": " + e.getMessage());
        }
    }

    /**
     * Extracts the value for a variable, checking INI file if needed.
     */
    private Object extractValue(String objectName, Map<String, String> properties, String type, Context context) {
        // BEHAVIOUR uses CODE instead of VALUE
        if (type.equals("BEHAVIOUR")) {
            return properties.get(objectName + ":CODE");
        }

        Object value = properties.get(objectName + ":VALUE");

        // Check INI file for override
        try {
            if (context.getGame() != null && context.getGame().getGameINI() != null) {
                String foundSection = context.getGame().findINISectionForVariable(objectName.toUpperCase());

                if (foundSection != null) {
                    String valueFromIni = context.getGame().getGameINI().get(foundSection, objectName.toUpperCase());

                    if (valueFromIni != null) {
                        // Store original value as INIT_VALUE
                        properties.put(objectName + ":INIT_VALUE", properties.get(objectName + ":VALUE"));
                        properties.put(objectName + ":VALUE", valueFromIni);
                        value = valueFromIni;
                        Gdx.app.log("CNVParserV2", "Loaded value for " + objectName + " from INI section " + foundSection);
                    }
                }
            }
        } catch (NullPointerException e) {
            Gdx.app.error("CNVParserV2", "Error while checking INI for " + objectName, e);
        }

        return value;
    }

    /**
     * Determines the INI section for a variable.
     */
    private String determineIniSection(String objectName, Context context) {
        try {
            if (context.getGame() != null && context.getGame().getGameINI() != null) {
                String foundSection = context.getGame().findINISectionForVariable(objectName.toUpperCase());
                if (foundSection != null) {
                    return foundSection;
                }
            }
            // Default to current scene
            if (context.getGame() != null && context.getGame().getCurrentScene() != null) {
                return context.getGame().getCurrentScene().toUpperCase();
            }
        } catch (NullPointerException e) {
            Gdx.app.error("CNVParserV2", "Error determining INI section for " + objectName, e);
        }
        return "DEFAULT";
    }

    /**
     * Applies properties to a variable.
     * Separates regular attributes from signal definitions.
     */
    private void applyProperties(Variable variable, String objectName, Map<String, String> properties) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            String key = property.getKey();
            String value = property.getValue();

            // Skip TYPE property
            if (key.equals(objectName + ":TYPE")) {
                continue;
            }

            // Check if this is a signal (starts with ON)
            if (key.startsWith(objectName + ":ON")) {
                String signalName = key.replace(objectName + ":", "");
                variable.addPendingSignal(signalName, value);
            } else {
                // Regular attribute
                String attributeName = key.replace(objectName + ":", "");
                variable.setAttribute(attributeName, value);
            }
        }
    }

    /**
     * Assigns signals to variables.
     *
     * This is the IMPROVED version - cleaner than v1!
     *
     * Instead of creating messy closures with captured state,
     * we use SignalDefinition and a cleaner handler pattern.
     */
    private void assignSignals(Context context) {
        for (Variable variable : context.getVariables().values()) {
            Map<String, String> pendingSignals = variable.getPendingSignals();

            for (Map.Entry<String, String> entry : pendingSignals.entrySet()) {
                String signalName = entry.getKey();
                String signalCode = entry.getValue();

                try {
                    SignalDefinition definition = parseSignalCode(signalName, signalCode, context);
                    attachSignal(variable, definition, context);
                } catch (Exception e) {
                    Gdx.app.error("CNVParserV2", "Failed to attach signal " + signalName + " to " + variable.getName(), e);
                }
            }
        }
    }

    /**
     * Parses signal code into a structured SignalDefinition.
     *
     * Examples:
     * - "MyBehaviour" → SignalDefinition(name, MyBehaviour, null)
     * - "MyBehaviour(p1, p2)" → SignalDefinition(name, MyBehaviour, [p1, p2])
     * - "{@PRINT(\"hi\");}" → SignalDefinition(name, anonymous BehaviourVariable, null)
     */
    private SignalDefinition parseSignalCode(String signalName, String code, Context context) {
        code = code.trim();

        // Check if it's an inline code block
        if (code.startsWith("{") && code.endsWith("}")) {
            // Fix format if needed
            if (code.endsWith(":}")) {
                code = code.substring(0, code.length() - 2) + ";}";
            }
            BehaviourVariable anonymous = new BehaviourVariable("", code, context);
            return new SignalDefinition(signalName, anonymous, null);
        }

        // Check if it has parameters
        String[] params = null;
        if (code.matches(".*\\(.*\\)")) {
            String[] tmp = code.split("\\(", 2);
            code = tmp[0];
            String paramsString = tmp[1].substring(0, tmp[1].length() - 1);
            if (!paramsString.isEmpty()) {
                params = paramsString.split(",");
                // Trim whitespace from params
                for (int i = 0; i < params.length; i++) {
                    params[i] = params[i].trim();
                }
            }
        }

        // Retrieve the behaviour variable
        Variable behaviourVariable = context.getVariable(code);
        if (behaviourVariable == null || !behaviourVariable.getType().equals("BEHAVIOUR")) {
            throw new IllegalArgumentException("Variable " + code + " is not a BEHAVIOUR variable");
        }

        return new SignalDefinition(signalName, (BehaviourVariable) behaviourVariable, params);
    }

    /**
     * Attaches a signal handler to a variable.
     *
     * This is CLEANER than v1 because:
     * 1. SignalDefinition is explicit (not captured in closure)
     * 2. Handler logic is in a separate method (not inline)
     * 3. Easier to test and debug
     */
    private void attachSignal(Variable variable, SignalDefinition definition, Context context) {
        variable.setSignal(definition.signalName(), new Signal() {
            @Override
            public void execute(Object argument) {
                executeSignal(variable, definition, context, argument);
            }
        });
    }

    /**
     * Executes a signal handler.
     *
     * Extracted from v1's anonymous class for clarity.
     */
    private void executeSignal(Variable targetVariable,
                                SignalDefinition definition,
                                Context context,
                                Object argument) {
        BehaviourVariable behaviour = definition.behaviourVariable();

        // Prepare arguments
        List<Object> arguments = new ArrayList<>();
        if (definition.hasParams()) {
            for (String param : definition.params()) {
                arguments.add(getVariableFromObject(param, context));
            }
        }

        // Save old 'this' context
        Variable oldThis = behaviour.getContext().getThisVariable();

        // Set 'this' to target variable (unless it's a ConditionVariable - little hack from v1)
        if (!(targetVariable instanceof ConditionVariable)) {
            behaviour.getContext().setThisVariable(targetVariable);
        }

        try {
            // Determine which method to call (RUN or RUNC)
            String methodName = behaviour.getAttribute("CONDITION") != null ? "RUNC" : "RUN";

            // Execute the behaviour
            behaviour.getMethod(methodName, Collections.singletonList("mixed"))
                    .execute(behaviour, !arguments.isEmpty() ? arguments : null);

        } catch (BreakException | OneBreakException e) {
            // Silently ignore break exceptions in signal handlers (v1 behavior)
        } finally {
            // Restore old 'this' context
            behaviour.getContext().setThisVariable(oldThis);
            Gdx.app.log("Signal", "Signal " + definition.signalName() + " executed on " + targetVariable.getName());
        }
    }

    /**
     * Initializes variables and runs ONINIT signals.
     *
     * Variables are initialized in a specific order to avoid dependency issues.
     */
    private void runOnInitOnVariables(Context context) {
        List<Variable> variables = new ArrayList<>(context.getVariables().values());

        // Sort variables by type priority
        variables.sort(new VariableTypeComparator());

        // Initialize all variables
        for (Variable variable : variables) {
            variable.init();
        }

        // Then emit ONINIT signals
        for (Variable variable : variables) {
            Gdx.app.log("CNVParserV2", "ONINIT for " + variable.getName() + " (" + variable.getType() + ")");
            variable.emitSignal("ONINIT");
        }
    }

    /**
     * Comparator for sorting variables by type priority.
     *
     * Initialization order matters! Behaviours first, then primitives, then complex types.
     */
    private static class VariableTypeComparator implements Comparator<Variable> {
        private static final List<String> TYPE_ORDER = Arrays.asList(
                "BEHAVIOUR",                                                      // 1. Procedures
                "INTEGER|STRING|DOUBLE|BOOLEAN",                                  // 2. Primitives
                "ARRAY|CONDITION",                                                // 3. Arrays and conditions
                "ANIMO|IMAGE|SOUND|VECTOR|FONT",                                  // 4. Media types
                "BUTTON|TEXT|SEQUENCE|GROUP|TIMER|MOUSE|KEYBOARD|CANVAS_OBSERVER" // 5. UI and input
        );

        @Override
        public int compare(Variable v1, Variable v2) {
            int index1 = findTypeIndex(v1.getType());
            int index2 = findTypeIndex(v2.getType());
            return Integer.compare(index1, index2);
        }

        private int findTypeIndex(String type) {
            return IntStream.range(0, TYPE_ORDER.size())
                    .filter(i -> Pattern.compile(TYPE_ORDER.get(i)).matcher(type).matches())
                    .findFirst()
                    .orElse(Integer.MAX_VALUE);
        }
    }
}
