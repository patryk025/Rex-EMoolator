package pl.genschu.bloomooemulator.interpreter.variable.types;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.variable.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SystemVariable extends GlobalVariable {
    public SystemVariable(String name, Context context) {
        super(name, context);
    }

    @Override
    protected void setMethods() {
        super.setMethods();

        this.setMethod("GETDATE", new Method(
                List.of(),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                Calendar calendar = Calendar.getInstance();
                // date is integer value, where date = (year-2000) * 10000 + month * 100 + day
                return new IntegerVariable("", (calendar.get(Calendar.YEAR) - 2000) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH), context);
            }
        });
        this.setMethod("GETMHZ", new Method(
                List.of(),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                SystemInfo si = new SystemInfo();
                HardwareAbstractionLayer hal = si.getHardware();
                long cpuFrequencyHz = hal.getProcessor().getMaxFreq();
                int cpuFrequencyMHz = (int) (cpuFrequencyHz / 1_000_000);
                return new IntegerVariable("", cpuFrequencyMHz, context);
            }
        });
        this.setMethod("GETSYSTEMTIME", new Method(
                List.of(),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                int uptimeInSeconds = (int) (new SystemInfo().getOperatingSystem().getSystemUptime());
                int milliseconds = (int) (new Date().getTime() % 1000); // little hack to get milliseconds
                return new IntegerVariable("", uptimeInSeconds * 1000 + milliseconds, context);
            }
        });
    }

    @Override
    public String getType() {
        return "SYSTEM";
    }

    @Override
    public void setAttribute(String name, Attribute attribute) {
        // no known attributes
    }

}
