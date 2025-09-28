package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.BehaviourVariable;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CodeParserTest {
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
        //TestEnvironment.enableLogs();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                arguments("{@RETURN([1+2*3]);}", 7),
                arguments("{@RETURN([[10-3]*[2+1]]);}", 21),
                arguments("{@RETURN([10%3]);}", 1),
                arguments("{@RETURN([1.2+2.3]);}", 3.5),
                arguments("{@INT(\"A\", 5); @RETURN([A + 2]);}", 7),
                arguments("{@STRING(\"TEST\", \"\");@IF(\"1'1\",\"{TEST^SET(\"OK\");}\",\"{TEST^SET(\"BAD\");}\");}", "OK"),
                arguments("{@INT(\"A\",1); @INT(\"A\",[A+1]); @RETURN(A);}", 2),
                // fibonacci with little subtle changes
                arguments("{@INT(\"A\",0);@INT(\"B\",1);@INT(\"I\",2);@INT(\"N\",10);@INT(\"TMP\",0);@LOOP(\"{TMP^SET(B);B^SET([A+B]);A^SET(TMP);}\",I,[N-I+1],1);@RETURN(B);}", 55),
                arguments("{@INT(\"A\",0);@INT(\"B\",1);@INT(\"TMP\",0);@LOOP(\"{TMP^SET(B);B^SET([A+B]);A^SET(TMP);}\",2,9,1);@RETURN(B);}", 55)

        );
    }

    @ParameterizedTest(name = "{index} ⇒ {0}")
    @MethodSource("cases")
    void evals(String script, Object expected) {
        BehaviourVariable behaviour = new BehaviourVariable("SCRIPT", script, ctx);
        Variable result = behaviour.fireMethod("RUN");

        assertEquals(expected, result.getValue());
    }
}
