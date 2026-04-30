package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.loader.CNVParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassVariable represents a CLASS definition.
 */
public record ClassVariable(
    String name,
    String defPath,                      // Path to class definition file (DEF attribute)
    String basePath,                     // Base class (BASE attribute) - not yet used
    Map<String, SignalHandler> signals
) implements Variable {

    public ClassVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructor
    public ClassVariable(String name, String defPath) {
        this(name, defPath, null, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // Class value is its definition path
        return new StringValue("CLASS[" + (defPath != null ? defPath : "undefined") + "]");
    }

    @Override
    public VariableType type() {
        return VariableType.CLASS;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Class doesn't support direct value assignment
        // DEF and BASE are set via attributes during parsing
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new ClassVariable(name, defPath, basePath, newSignals);
    }

    // ========================================
    // CLASS-SPECIFIC OPERATIONS
    // ========================================

    /**
     * Sets the DEF attribute (class definition file path).
     *
     * @param path Path to .cnv file
     * @return New ClassVariable with updated path
     */
    public ClassVariable withDefPath(String path) {
        return new ClassVariable(name, path, basePath, signals);
    }

    /**
     * Sets the BASE attribute (base class for inheritance).
     *
     * @param base Base class name
     * @return New ClassVariable with updated base
     */
    public ClassVariable withBasePath(String base) {
        return new ClassVariable(name, defPath, base, signals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("NEW", MethodSpec.of((self, args, ctx) -> {
            ClassVariable thisVar = (ClassVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("NEW requires at least 1 argument (varName)");
            }
            if (ctx == null) {
                throw new IllegalArgumentException("CLASS.NEW requires MethodContext");
            }
            if (thisVar.defPath == null || thisVar.defPath.isBlank()) {
                throw new IllegalStateException("CLASS.NEW requires DEF path");
            }

            String varName = ArgumentHelper.getString(args.get(0));
            Context parentContext = ctx.context();
            Context classContext = new Context(new ExecutionContext(), parentContext);
            classContext.setGame(ctx.getGame());

            Game game = ctx.getGame();
            String vfsPath = resolveDefinitionVfsPath(thisVar.defPath);
            CNVParser cnvParser = new CNVParser();

            if (game != null && game.getVfs().exists(vfsPath)) {
                try (InputStream is = game.getVfs().openRead(vfsPath)) {
                    cnvParser.parse(is, vfsPath, classContext);
                } catch (IOException e) {
                    Gdx.app.error("ClassVariable", "Error while loading class " + vfsPath, e);
                    throw new RuntimeException(e);
                }
            } else {
                Gdx.app.error("ClassVariable", "Class definition " + vfsPath + " doesn't exist. Instance will be empty.");
            }

            InstanceVariable instance = new InstanceVariable(varName, classContext);
            ctx.setVariable(varName, instance);

            Variable constructorBehaviour = classContext.store().get("CONSTRUCTOR");
            if (constructorBehaviour instanceof BehaviourVariable behaviour) {
                ExecutionResult ctorResult = new ASTInterpreter(classContext)
                        .runBehaviour("CONSTRUCTOR:" + varName, instance, behaviour, args);
                return MethodResult.fromExecution(ctorResult);
            }

            return MethodResult.noReturn();
        })),

        Map.entry("DELETE", MethodSpec.of((self, args, ctx) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DELETE requires at least 1 argument (varName)");
            }
            if (ctx == null) {
                throw new IllegalArgumentException("CLASS.DELETE requires MethodContext");
            }

            String varName = ArgumentHelper.getString(args.get(0));
            Variable instanceVariable = ctx.getVariable(varName);
            if (!(instanceVariable instanceof InstanceVariable instance)) {
                Gdx.app.error("ClassVariable", "Variable is not an INSTANCE: " + varName);
                return MethodResult.noReturn();
            }

            Variable destructorBehaviour = instance.instanceContext().store().get("DESTRUCTOR");
            ExecutionResult dtorResult = null;
            if (destructorBehaviour instanceof BehaviourVariable behaviour) {
                dtorResult = new ASTInterpreter(instance.instanceContext())
                        .runBehaviour("DESTRUCTOR:" + varName, instance, behaviour, args);
            }

            ctx.removeVariable(varName);
            if (dtorResult != null) {
                return MethodResult.fromExecution(dtorResult);
            }
            return MethodResult.noReturn();
        }))
    );

    private static String resolveDefinitionVfsPath(String definitionPath) {
        String classPath = definitionPath.startsWith("$")
                ? definitionPath
                : "$COMMON/classes/" + definitionPath;
        return classPath.substring(1).replaceFirst("^[/\\\\]+", "").replace('\\', '/');
    }

    @Override
    public String toString() {
        return "Class[" + name + " def=" + (defPath != null ? defPath : "undefined") + "]";
    }
}
