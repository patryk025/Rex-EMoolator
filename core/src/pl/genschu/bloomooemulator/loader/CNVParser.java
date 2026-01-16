package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.interpreter.variable.db.DatabaseState;

import java.io.*;
import java.util.*;

/**
 * CNVParser - Parses CNV files and populates Context.
 *
 * Status: WORK IN PROGRESS - Basic structure only
 * TODO: Signal handling, BEHAVIOUR parsing, method initialization
 */
public class CNVParser {

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
                Gdx.app.log("CNVParser", "Deciphering " + file.getName() + "...");
                parseString(ScriptDecypher.decode(content.toString(), offset), context);
            } else {
                parseString(content.toString(), context);
            }
        } catch (FileNotFoundException e) {
            Gdx.app.error("CNVParser", "File not found: " + file.getName());
            throw e;
        }

        assignSignals(context);
        runOnInit(context);
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
            Gdx.app.log("CNVParser", "Redefinition of object: " + objectName + ". Merging properties...");
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
            Gdx.app.error("CNVParser", "Missing TYPE for object " + objectName);
            return;
        }

        Gdx.app.log("CNVParser", "Processing object " + objectName + " of type " + type);

        // Extract value (checking INI if needed)
        Object rawValue = extractValue(objectName, properties, type, context);

        // Store all attributes in context
        storeAttributes(objectName, properties, context);

        // Create variable
        try {
            Variable variable = createVariable(type, objectName, rawValue, properties, context);
            if (variable != null) {
                context.setVariable(objectName, variable);
            }
        } catch (Exception e) {
            Gdx.app.error("CNVParser", "Failed to create variable " + objectName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the value for a variable, checking INI file if needed.
     */
    private Object extractValue(String objectName, Map<String, String> properties, String type, Context context) {
        // BEHAVIOUR uses CODE instead of VALUE
        if (type.equalsIgnoreCase("BEHAVIOUR")) {
            return properties.get(objectName + ":CODE");
        }

        Object value = properties.get(objectName + ":VALUE");

        // TODO: Check INI file for override (needs Game reference)
        // For now, just return the value from CNV

        return value;
    }

    /**
     * Stores all properties as attributes in context.
     */
    private void storeAttributes(String objectName, Map<String, String> properties, Context context) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            String key = property.getKey();
            String value = property.getValue();

            // Skip TYPE property (already used)
            if (key.equals(objectName + ":TYPE")) {
                continue;
            }

            // Remove object prefix from attribute name
            String attributeName = key.replace(objectName + ":", "");
            context.setAttribute(objectName, attributeName, value);
        }
    }

    /**
     * Creates a v2 Variable from type, name, value, and properties.
     */
    private Variable createVariable(String type, String objectName, Object rawValue,
                                      Map<String, String> properties, Context context) {
        String normalizedType = type.toUpperCase();

        return switch (normalizedType) {
            case "INTEGER", "INT" -> {
                Value value = VariableFactory.createValueWithAutoType(objectName, rawValue);
                yield VariableFactory.createVariable("INTEGER", objectName, value);
            }
            case "DOUBLE" -> {
                Value value = VariableFactory.createValueWithAutoType(objectName, rawValue);
                yield VariableFactory.createVariable("DOUBLE", objectName, value);
            }
            case "STRING" -> {
                Value value = VariableFactory.createValueWithAutoType(objectName, rawValue);
                yield VariableFactory.createVariable("STRING", objectName, value);
            }
            case "BOOLEAN", "BOOL" -> {
                Value value = VariableFactory.createValueWithAutoType(objectName, rawValue);
                yield VariableFactory.createVariable("BOOLEAN", objectName, value);
            }
            case "DATABASE" -> {
                // Create DatabaseVariable with MODEL reference
                String modelName = properties.get(objectName + ":MODEL");
                DatabaseState state = new DatabaseState();
                yield new DatabaseVariable(objectName, state);
            }
            case "STRUCT" -> {
                // Create StructVariable
                // TODO: Parse FIELDS attribute and create field list
                yield new StructVariable(objectName, List.of());
            }
            case "CLASS" -> {
                // Create ClassVariable with DEF path
                String defPath = properties.get(objectName + ":DEF");
                String basePath = properties.get(objectName + ":BASE");
                yield new ClassVariable(objectName, defPath, basePath, Map.of());
            }
            case "CNVLOADER" -> {
                yield new CNVLoaderVariable(objectName);
            }
            case "BEHAVIOUR" -> {
                // Parse CODE attribute to AST
                String code = (String) rawValue;
                if (code == null || code.trim().isEmpty()) {
                    Gdx.app.log("CNVParser", "Empty BEHAVIOUR code for: " + objectName);
                    code = "{}";  // Empty block
                }

                ASTNode ast = BehaviourCodeParser.parseCode(code, objectName);
                yield new BehaviourVariable(objectName, ast, Map.of());
            }
            case "ARRAY" -> {
                // TODO: Create ArrayVariable
                Gdx.app.log("CNVParser", "ARRAY type not yet fully implemented: " + objectName);
                yield null;
            }
            default -> {
                Gdx.app.error("CNVParser", "Unsupported variable type: " + type + " for " + objectName);
                yield null;
            }
        };
    }

    // ========================================
    // SIGNAL HANDLING
    // ========================================

    /**
     * Assigns signals to all variables in context.
     *
     * Signals are stored as attributes with names starting with "ON"
     * (e.g., ONINIT, ONCLICK, ONCHANGED).
     *
     * Each signal points to either:
     * - A BEHAVIOUR variable name (e.g., "MyBehaviour")
     * - A BEHAVIOUR with parameters (e.g., "MyBehaviour(param1, param2)")
     * - Inline code block (e.g., "{@RETURN("hello");}")
     */
    private void assignSignals(Context context) {
        Map<String, Variable> variables = context.getVariables(false);

        for (Map.Entry<String, Variable> entry : variables.entrySet()) {
            String varName = entry.getKey();
            Variable variable = entry.getValue();

            // Get all attributes for this variable
            Map<String, String> attrs = context.attributes().getAll(varName);

            // Find signal attributes (starting with "ON")
            for (Map.Entry<String, String> attr : attrs.entrySet()) {
                String attrName = attr.getKey();
                String attrValue = attr.getValue();

                if (attrName.startsWith("ON")) {
                    try {
                        attachSignal(varName, variable, attrName, attrValue, context);
                    } catch (Exception e) {
                        Gdx.app.error("CNVParser", "Failed to attach signal " + attrName + " to " + varName, e);
                    }
                }
            }
        }
    }

    /**
     * Attaches a signal handler to a variable.
     *
     * @param varName Variable name
     * @param variable Variable instance
     * @param signalName Signal name (e.g., "ONINIT")
     * @param signalCode Signal code (behaviour name or inline code)
     * @param context Context for resolving behaviours
     */
    private void attachSignal(String varName, Variable variable, String signalName,
                               String signalCode, Context context) {
        signalCode = signalCode.trim();

        // Parse signal code
        BehaviourVariable behaviour;
        String[] params = null;

        // Check if inline code block
        if (signalCode.startsWith("{") && signalCode.endsWith("}")) {
            // Inline anonymous behaviour
            ASTNode ast = BehaviourCodeParser.parseCode(signalCode, varName + "." + signalName);
            behaviour = new BehaviourVariable("", ast, Map.of());
        } else {
            // Reference to existing behaviour
            // Check if has parameters: BehaviourName(param1, param2)
            if (signalCode.contains("(") && signalCode.endsWith(")")) {
                int parenIndex = signalCode.indexOf('(');
                String behaviourName = signalCode.substring(0, parenIndex);
                String paramsString = signalCode.substring(parenIndex + 1, signalCode.length() - 1);

                if (!paramsString.isEmpty()) {
                    params = paramsString.split(",");
                    for (int i = 0; i < params.length; i++) {
                        params[i] = params[i].trim();
                    }
                }

                Variable behVar = context.getVariable(behaviourName);
                if (behVar == null || behVar.type() != VariableType.BEHAVIOUR) {
                    Gdx.app.error("CNVParser", "Signal " + signalName + " references non-existent BEHAVIOUR: " + behaviourName);
                    return;
                }
                behaviour = (BehaviourVariable) behVar;
            } else {
                // Simple behaviour reference
                Variable behVar = context.getVariable(signalCode);
                if (behVar == null || behVar.type() != VariableType.BEHAVIOUR) {
                    Gdx.app.error("CNVParser", "Signal " + signalName + " references non-existent BEHAVIOUR: " + signalCode);
                    return;
                }
                behaviour = (BehaviourVariable) behVar;
            }
        }

        // Create signal handler
        final BehaviourVariable finalBehaviour = behaviour;
        final String[] finalParams = params;

        SignalHandler handler = (var, signal, args) -> {
            // TODO: Execute behaviour with proper context and arguments
            // This requires ASTInterpreter integration
            Gdx.app.log("CNVParser", "Signal " + signal + " triggered on " + var.name() + " - BEHAVIOUR execution not yet implemented");
        };

        // Attach handler to variable
        Variable updatedVar = variable.withSignal(signalName, handler);
        context.setVariable(varName, updatedVar);

        Gdx.app.log("CNVParser", "Attached signal " + signalName + " to " + varName);
    }

    /**
     * Runs ONINIT signal on all variables.
     *
     * Variables should be initialized in specific order to avoid dependency issues.
     * Order: BEHAVIOUR -> primitives -> complex types
     */
    private void runOnInit(Context context) {
        Map<String, Variable> variables = context.getVariables(false);

        // Sort by type priority
        List<Map.Entry<String, Variable>> sortedVars = new ArrayList<>(variables.entrySet());
        sortedVars.sort((e1, e2) -> {
            int p1 = getTypePriority(e1.getValue().type());
            int p2 = getTypePriority(e2.getValue().type());
            return Integer.compare(p1, p2);
        });

        // Emit ONINIT signals
        for (Map.Entry<String, Variable> entry : sortedVars) {
            String varName = entry.getKey();
            Variable variable = entry.getValue();

            Gdx.app.log("CNVParser", "ONINIT for " + varName + " (" + variable.type() + ")");
            variable.emitSignal("ONINIT");
        }
    }

    /**
     * Returns initialization priority for a variable type.
     * Lower number = initialized first.
     */
    private int getTypePriority(VariableType type) {
        return switch (type) {
            case BEHAVIOUR -> 0;  // Behaviours first
            case INTEGER, DOUBLE, STRING, BOOLEAN -> 1;  // Primitives
            case ARRAY, CONDITION, COMPLEXCONDITION, DATABASE, EXPRESSION, MULTIARRAY, STRUCT -> 2;  // Logic and structures
            case ANIMO, CLASS, FONT, IMAGE, SOUND, VIRTUALGRAPHICSOBJECT, VECTOR -> 3;  // Classes, sounds and graphics
            case BUTTON, CANVAS_OBSERVER, FILTER, GROUP, JOYSTICK, KEYBOARD, MOUSE,
                 PATTERN, SEQUENCE, STATICFILTER, TEXT, TIMER -> 4;  // UI and input
            default -> 5;  // Everything else
        };
    }
}
