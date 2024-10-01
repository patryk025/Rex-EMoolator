package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;

import java.util.*;

public class GlobalVariable extends Variable {
    private Map<Context, Variable> contextVariableInstances = new HashMap<>();

    public GlobalVariable(String name, Context context) {
        super(name, context);
    }

    public void addSignalsMap(Context context, Variable variable) {
        if(!contextVariableInstances.containsKey(context)) {
            contextVariableInstances.put(context, variable);

            List<Context> contextHierarchy = new ArrayList<>();
            contextHierarchy.add(context);

            Context tmpContext = context;
            while(tmpContext != null) {
                contextHierarchy.add(tmpContext);
                tmpContext = tmpContext.getParentContext();
            }
            Collections.reverse(contextHierarchy);
        }
    }

    @Override
    public void emitSignal(String name, Object argument) {
        Gdx.app.log("GlobalVariable", "Emitting signal " + name + " from top level with argument " + argument);

        List<Context> contextHierarchy = new ArrayList<>();
        contextHierarchy.add(context.getGame().getCurrentApplicationContext());
        contextHierarchy.add(context.getGame().getCurrentEpisodeContext());
        contextHierarchy.add(context.getGame().getCurrentSceneContext());

        Variable oldThis = context.getThisVariable();
        context.setThisVariable(this);

        for(Context context : contextHierarchy) {
            if(contextVariableInstances.containsKey(context)) {
                try {
                    String signalName = name;
                    if (argument != null) {
                        signalName += "^" + argument;
                    }
                    Signal signal = contextVariableInstances.get(context).getSignal(signalName);
                    if (signal != null) {
                        Gdx.app.log(this.getClass().getSimpleName(), "Executing signal " + signalName + "...");
                        signal.execute(argument);
                    } else {
                        signal = contextVariableInstances.get(context).getSignal(name);
                        if (signal != null) {
                            Gdx.app.log(this.getClass().getSimpleName(), "Executing signal " + name + "...");
                            signal.execute(null);
                        }
                    }
                } catch (BreakException ignored) {} // simple break
            }
        }

        context.setThisVariable(oldThis);
    }
}
