package pl.genschu.bloomooemulator.interpreter.variable;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * SystemVariable provides system-level information (date, CPU, uptime).
 * This is a global variable — not bound to a scene context.
 */
public record SystemVariable(
    String name,
    Map<String, SignalHandler> signals
) implements Variable {

    public SystemVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public SystemVariable(String name) {
        this(name, Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.SYSTEM;
    }

    @Override
    public Variable withValue(Value newValue) {
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
        return new SystemVariable(name, newSignals);
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETDATE", MethodSpec.of((self, args, ctx) -> {
            Calendar calendar = Calendar.getInstance();
            // date = (year-2000) * 10000 + month * 100 + day
            int date = (calendar.get(Calendar.YEAR) - 2000) * 10000
                    + (calendar.get(Calendar.MONTH) + 1) * 100
                    + calendar.get(Calendar.DAY_OF_MONTH);
            return MethodResult.returns(new IntValue(date));
        })),

        Map.entry("GETMHZ", MethodSpec.of((self, args, ctx) -> {
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            long cpuFrequencyHz = hal.getProcessor().getMaxFreq();
            int cpuFrequencyMHz = (int) (cpuFrequencyHz / 1_000_000);
            return MethodResult.returns(new IntValue(cpuFrequencyMHz));
        })),

        Map.entry("GETSYSTEMTIME", MethodSpec.of((self, args, ctx) -> {
            int uptimeInSeconds = (int) (new SystemInfo().getOperatingSystem().getSystemUptime());
            int milliseconds = (int) (new Date().getTime() % 1000);
            return MethodResult.returns(new IntValue(uptimeInSeconds * 1000 + milliseconds));
        }))
    );

    @Override
    public String toString() {
        return "SystemVariable[" + name + "]";
    }
}
