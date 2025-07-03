package pl.genschu.bloomooemulator.interpreter;

import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.util.GlobalVariables;
import pl.genschu.bloomooemulator.interpreter.variable.GlobalVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.engine.Game;

import java.util.*;

public class Context {
    private Map<String, Variable> variables = new LinkedHashMap<>();
    private Context parentContext;
    private Object returnValue;
    private Game game;
    private final List<Context> additionalContexts = new ArrayList<>(); // loaded by CNVLoader

    private final Map<String, Variable> graphicsVariables; // cache for faster drawing
    private final Map<String, Variable> buttonsVariables;
    private final Map<String, Variable> timerVariables;
    private final Map<String, Variable> soundVariables;
    private final Map<String, Variable> classInstances;
    private final Map<String, Variable> textVariables;
    private MouseVariable mouseVariable;
    private KeyboardVariable keyboardVariable;

    Variable thisVariable = null;

    public Context() {
        this.parentContext = null;
        this.game = null;
        this.graphicsVariables = new LinkedHashMap<>();
        this.buttonsVariables = new LinkedHashMap<>();
        this.timerVariables = new LinkedHashMap<>();
        this.soundVariables = new LinkedHashMap<>();
        this.classInstances = new LinkedHashMap<>();
        this.textVariables = new LinkedHashMap<>();
        this.mouseVariable = null;
        this.keyboardVariable = null;
    }

    public Context(Context parentContext, Game game) {
        this.parentContext = parentContext;
        this.game = game;
        this.graphicsVariables = new LinkedHashMap<>();
        this.buttonsVariables = new LinkedHashMap<>();
        this.timerVariables = new LinkedHashMap<>();
        this.soundVariables = new LinkedHashMap<>();
        this.classInstances = new LinkedHashMap<>();
        this.textVariables = new LinkedHashMap<>();
        this.mouseVariable = null;
        this.keyboardVariable = null;
    }

    public Variable getVariable(String name) {
        if (name.equals("THIS")) {
            if (thisVariable != null) {
                return thisVariable;
            }
        }

        if (name.endsWith("_CURSOR")) {
            String baseName = name.substring(0, name.length() - 7);
            Variable var = getVariable(baseName);
            if (var instanceof DatabaseVariable) {
                return ((DatabaseVariable) var).getCursor();
            }
        }

        // check if it is global variable
        Variable globalVariable = GlobalVariables.getVariable(name, this);
        if (globalVariable != null) {
            return globalVariable;
        }

        if (name.matches(".*_\\d+$")) {
            String baseName = name.substring(0, name.lastIndexOf('_'));
            int cloneNumber = Integer.parseInt(name.substring(name.lastIndexOf('_') + 1));

            if (cloneNumber == 0) {
                // first, check if variable with that name already exists (it could be not a clone)
                Variable variable = variables.get(name);
                if (variable != null) {
                    return variable;
                }

                Variable baseVariable = getVariable(baseName);
                if(baseVariable instanceof StringVariable) {
                    if(!baseName.equals(((StringVariable) baseVariable).GET())) {
                        return baseVariable;
                    }
                }
                else {
                    return baseVariable;
                }
            } else {
                Variable variable = variables.get(baseName);
                if (variable != null) {
                    // find in clones
                    for (Variable clone : variable.getClones()) {
                        if (clone.getName().equals(name)) {
                            return clone;
                        }
                    }
                }
            }
        }

        Variable variable = variables.get(name);
        if (variable == null) {
            if (parentContext != null) {
                return parentContext.getVariable(name);
            }

            variable = VariableFactory.createVariable("STRING", name, name, this);
        }
        return variable;
    }

    public Map<String, Variable> getGraphicsVariables() {
        return getGraphicsVariables(true);
    }

    public Map<String, Variable> getGraphicsVariables(boolean includeParent) {
        Map<String, Variable> result = new LinkedHashMap<>();
        // Adding graphics from class instances
        for(Variable classInstance : classInstances.values()) {
            result.putAll(classInstance.getContext().getGraphicsVariables(false));
        }

        // Adding graphics from CNVLoader
        for(Context context : additionalContexts) {
            result.putAll(context.getGraphicsVariables(false));
        }
        result.putAll(graphicsVariables);
        if (includeParent && parentContext != null) {
            result.putAll(parentContext.getGraphicsVariables());
        }
        return result;
    }

    public Map<String, Variable> getButtonsVariables() {
        return getButtonsVariables(true);
    }

    public Map<String, Variable> getButtonsVariables(boolean includeParent) {
        Map<String, Variable> result = new LinkedHashMap<>();
        // Adding buttons from class instances
        for(Variable classInstance : classInstances.values()) {
            result.putAll(classInstance.getContext().getButtonsVariables(false));
        }

        // Adding buttons from CNVLoader
        for(Context context : additionalContexts) {
            result.putAll(context.getButtonsVariables(false));
        }
        result.putAll(buttonsVariables);
        if (includeParent && parentContext != null) {
            result.putAll(parentContext.getButtonsVariables());
        }
        return result;
    }

    public Map<String, Variable> getTimerVariables() {
        return getTimerVariables(true);
    }

    public Map<String, Variable> getTimerVariables(boolean includeParent) {
        Map<String, Variable> result = new HashMap<>();
        // Adding timers from class instances
        for(Variable classInstance : classInstances.values()) {
            result.putAll(classInstance.getContext().getTimerVariables(false));
        }

        // Adding timers from CNVLoader
        for(Context context : additionalContexts) {
            result.putAll(context.getTimerVariables(false));
        }
        result.putAll(timerVariables);
        if (includeParent && parentContext != null) {
            result.putAll(parentContext.getTimerVariables());
        }
        return result;
    }

    public Map<String, Variable> getTextVariables() {
        return getTextVariables(true);
    }

    public Map<String, Variable> getTextVariables(boolean includeParent) {
        Map<String, Variable> result = new HashMap<>();
        // Adding texts from class instances
        for(Variable classInstance : classInstances.values()) {
            result.putAll(classInstance.getContext().getTextVariables(false));
        }

        // Adding texts from CNVLoader
        for(Context context : additionalContexts) {
            result.putAll(context.getTextVariables(false));
        }
        result.putAll(textVariables);
        if (includeParent && parentContext != null) {
            result.putAll(parentContext.getTextVariables());
        }
        return result;
    }

    public void setVariable(String name, Variable variable) {
        variables.put(name, variable);

        GlobalVariable globalVariable = GlobalVariables.getVariable(name, this);
        if (globalVariable != null) {
            if(globalVariable instanceof MouseVariable) {
                mouseVariable = (MouseVariable) globalVariable;
            }
            else if(globalVariable instanceof KeyboardVariable) {
                keyboardVariable = (KeyboardVariable) globalVariable;
            }
            globalVariable.addSignalsMap(variable.getContext(), variable);
            return;
        }

        if(variable.getType().equals("ANIMO") || variable.getType().equals("IMAGE") || variable.getType().equals("SEQUENCE")) {
            graphicsVariables.put(name, variable);
        }
        else if(variable.getType().equals("BUTTON")) {
            buttonsVariables.put(name, variable);
        }
        else if(variable.getType().equals("TIMER")) {
            timerVariables.put(name, variable);
        }
        else if(variable.getType().equals("SOUND")) {
            soundVariables.put(name, variable);
        }
        else if(variable.getType().equals("MOUSE")) {
            mouseVariable = (MouseVariable) variable;
        }
        else if(variable.getType().equals("KEYBOARD")) {
            keyboardVariable = (KeyboardVariable) variable;
        }
        else if(variable.getType().equals("INSTANCE")) {
            classInstances.put(name, variable);
        }
        else if(variable.getType().equals("TEXT")) {
            textVariables.put(name, variable);
        }
    }

    public boolean hasVariable(String name) {
        // Use a set to track visited contexts to prevent infinite loops
        Set<Context> visitedContexts = new HashSet<>();
        return hasVariableInternal(name, visitedContexts);
    }

    private boolean hasVariableInternal(String name, Set<Context> visitedContexts) {
        // Mark this context as visited
        if (!visitedContexts.add(this)) {
            return false; // If already visited, avoid further checks
        }

        // Check in class instances
        for(Variable classInstance : classInstances.values()) {
            if(classInstance.getContext().hasVariableInternal(name, visitedContexts))
                return true;
        }

        for(Context context : additionalContexts) { // look in additionalContexts
            if(context.hasVariableInternal(name, visitedContexts))
                return true;
        }

        if (variables.containsKey(name)) {
            return true;
        }
        return parentContext != null && parentContext.hasVariableInternal(name, visitedContexts);
    }

    public void removeVariable(String name) {
        for(Context context : additionalContexts) { // first look in additionalContexts
            context.removeVariable(name);
        }

        variables.remove(name);

        graphicsVariables.remove(name);
        buttonsVariables.remove(name);
        timerVariables.remove(name);
        soundVariables.remove(name);
        classInstances.remove(name);
        textVariables.remove(name);
    }

    public void addButtonVariable(Variable variable) {
        buttonsVariables.put(variable.getName(), variable);
    }

    public void removeButtonVariable(Variable variable) {
        buttonsVariables.remove(variable.getName());
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public Context getParentContext() {
        return parentContext;
    }

    public void setParentContext(Context parentContext) {
        this.parentContext = parentContext;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        if(game != null)
            return game;

        if(parentContext != null)
            return parentContext.getGame();

        return null;
    }

    public void addContext(Context context) {
        additionalContexts.add(context);
    }

    public void removeContext(Context context) {
        additionalContexts.remove(context);
    }

    public Map<String, Variable> getVariables() {
        return getVariables(false);
    }

    public Map<String, Variable> getVariables(boolean includeParent) {
        Map<String, Variable> variables = new LinkedHashMap<>();

        for(Variable classInstance : classInstances.values()) {
            variables.putAll(classInstance.getContext().getVariables(false));
        }

        for(Context context : additionalContexts) {
            variables.putAll(context.getVariables(includeParent));
        }

        if(includeParent && parentContext != null) {
            variables.putAll(parentContext.getVariables());
        }
        variables.putAll(this.variables);
        return variables;
    }

    public Variable getThisVariable() {
        if(parentContext != null)
            return parentContext.getThisVariable();

        return this.thisVariable;
    }

    public void setThisVariable(Variable thisVariable) {
        if(parentContext != null)
            parentContext.setThisVariable(thisVariable);
        else
            this.thisVariable = thisVariable;
    }

    public Map<String, Variable> getSoundVariables() {
        return soundVariables;
    }

    public MouseVariable getMouseVariable() {
        if(mouseVariable != null)
            return mouseVariable;
        else {
            return parentContext != null ? parentContext.getMouseVariable() : (MouseVariable) GlobalVariables.getVariable("MOUSE", this);
        }
    }

    public void setMouseVariable(MouseVariable mouseVariable) {
        this.mouseVariable = mouseVariable;
    }

    public KeyboardVariable getKeyboardVariable() {
        if(keyboardVariable != null)
            return keyboardVariable;
        else {
            return parentContext != null ? parentContext.getKeyboardVariable() : (KeyboardVariable) GlobalVariables.getVariable("KEYBOARD", this);
        }
    }

    public void setKeyboardVariable(KeyboardVariable keyboardVariable) {
        this.keyboardVariable = keyboardVariable;
    }
}