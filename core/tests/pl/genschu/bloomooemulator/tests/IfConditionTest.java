package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.ast.BlockNode;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.MethodContext;
import pl.genschu.bloomooemulator.interpreter.variable.MethodResult;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static pl.genschu.bloomooemulator.builders.MethodHelper.createMethodContext;

public class IfConditionTest {
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
        //TestEnvironment.enableLogs();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder()
                .withVariable("INTEGER", "LICZNIK", 0)
                .withVariable("INTEGER", "OBJ", 2)
                .withVariable("STRING", "OBJ2", "1")
                .withVariable("STRING", "TEST_RESULTS", "")
                .build();

        BehaviourVariable behaviour = (BehaviourVariable) new BehaviourVariable("T", new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript("{TEST_RESULTS^ADD($1);@INT(\"RET\",1);@RETURN(RET);}");
        ctx.setVariable("T", behaviour);

        behaviour = (BehaviourVariable) new BehaviourVariable("F", new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript("{TEST_RESULTS^ADD($1);@INT(\"RET\",0);@RETURN(RET);}");
        ctx.setVariable("F", behaviour);

        behaviour = (BehaviourVariable) new BehaviourVariable("METODA_ZMIENIAJACA_LICZNIK", new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript("{LICZNIK^INC();TEST_RESULTS^ADD(\"v\");@RETURN(LICZNIK);}");
        ctx.setVariable("METODA_ZMIENIAJACA_LICZNIK", behaviour);
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                arguments("{@IF(\"1'1||1'2&&1'2\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"1'1||1'1&&1'2\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"1'2&&1'1||1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"1'2||1'1&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"1'1||METODA_ZMIENIAJACA_LICZNIK^RUN()'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"1'2&&METODA_ZMIENIAJACA_LICZNIK^RUN()'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", false, ""),
                arguments("{@IF(\"1'2||METODA_ZMIENIAJACA_LICZNIK^RUN()'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, "v"),
                arguments("{@IF(\"1'1&&METODA_ZMIENIAJACA_LICZNIK^RUN()'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, "v"),
                arguments("{@IF(\"2+3*4'20&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", false, ""),
                arguments("{@IF(\"2+3*4'14&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", false, ""),
                arguments("{@IF(\"[2+3*4]'20&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"[2+3*4]'14&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", false, ""),
                arguments("{@IF(\"5+5*2'20||1'2&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"10>'5&&3<'10||1'2\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"5!'5||8>'10&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"1'1&&2<'3||4>'5\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"OBJ^GET()'42&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", false, ""),
                arguments("{@IF(\"1'1||OBJ^SET(999)'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"OBJ^GET()'OBJ2^GET()&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", false, ""),
                arguments("{@IF(\"1'1||1'2&&1'3||1'4\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, ""),
                arguments("{@IF(\"1'2&&1'3||1'4&&1'5\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", false, ""),
                arguments("{@IF(\"T^RUN(\"A\")'1||T^RUN(\"B\")'1||T^RUN(\"C\")'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, "A"),
                arguments("{@IF(\"F^RUN(\"A\")'0&&F^RUN(\"B\")'0&&F^RUN(\"C\")'0\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, "ABC"),
                arguments("{@IF(\"F^RUN(\"A\")'0||T^RUN(\"B\")'1||T^RUN(\"C\")'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, "A"),
                arguments("{@IF(\"T^RUN(\"A\")'1&&F^RUN(\"B\")'0&&T^RUN(\"C\")'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");}", true, "ABC")
        );
    }

    @ParameterizedTest(name = "{index} ⇒ {0}")
    @MethodSource("cases")
    void evals(String script, Object expected, String sideEffects) {
        BehaviourVariable behaviour = (BehaviourVariable) new BehaviourVariable("SCRIPT", new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript(script);
        MethodContext methodCtx = createMethodContext(ctx);
        MethodResult result = behaviour.callMethod("RUN", List.of(), methodCtx);

        Value returnValue = result.returnValue();

        assertEquals(expected, returnValue.toBool().value());

        assertEquals(sideEffects, ctx.getVariable("TEST_RESULTS").value().toDisplayString());
    }
}
