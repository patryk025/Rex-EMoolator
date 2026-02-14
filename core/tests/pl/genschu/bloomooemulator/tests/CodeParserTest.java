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
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.MethodResult;
import pl.genschu.bloomooemulator.interpreter.variable.StructVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;
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
        ctx = new ContextBuilder()
                .withVariable("INTEGER", "TEST_NO", 1)
                .withVariable("INTEGER", "TEST_VALUE_1", 5)
                .withVariable("STRING", "TEST_VALUE_NAME", "TEST_VALUE_1")
                .build();

        BehaviourVariable behaviour = (BehaviourVariable) new BehaviourVariable("TEST_BEH", new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript("{@RETURN(2);}");
        ctx.setVariable("TEST_BEH", behaviour);

        StructVariable struct = new StructVariable("TEST_STRUCT", List.of(
                "NAME",
                "VAL"
        ), List.of(
                "STRING",
                "INTEGER"
        ), List.of(
                new StringValue("PIERWSZA"),
                new IntValue(5)
        ));
        ctx.setVariable("TEST_STRUCT", struct);
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                arguments("{@RETURN([1+2*3]);}", 9),
                arguments("{@RETURN([[10-3]*[2+1]]);}", 21),
                arguments("{@RETURN([10%3]);}", 1),
                arguments("{@RETURN([2+2*2]);}", 8),
                arguments("{@RETURN([1.2+2.3]);}", 3.5),
                arguments("{@INT(\"A\", 5); @RETURN([A + 2]);}", 7),
                // three arguments IFs
                arguments("{" +
                            "@STRING(\"TEST\", \"\");" +
                            "@IF(\"1'1\",\"{" +
                                "TEST^SET(\"OK\");" +
                            "}\",\"{" +
                                "TEST^SET(\"BAD\");" +
                            "}\");" +
                            "@RETURN(TEST);" +
                          "}", "OK"),
                arguments("{" +
                            "@STRING(\"TEST\", \"\");" +
                            "@IF(\"TEST_BEH^RUN()'2\",\"{" +
                                "TEST^SET(\"OK\");" +
                            "}\",\"{" +
                                "TEST^SET(\"BAD\");" +
                            "}\");" +
                            "@RETURN(TEST);" +
                          "}", "OK"),
                arguments("{" +
                            "@STRING(\"TEST\", \"\");" +
                            "@IF(\"TEST_BEH'2\",\"{" +
                                "TEST^SET(\"OK\");" +
                            "}\",\"{" +
                                "TEST^SET(\"BAD\");" +
                            "}\");" +
                            "@RETURN(TEST);" +
                           "}", "BAD"),
                arguments("{" +
                            "@STRING(\"TEST\", \"\");" +
                            "@IF(\"TEST_STRUCT|VAL'5\",\"{" +
                                "TEST^SET(\"OK\");" +
                            "}\",\"{" +
                                "TEST^SET(\"BAD\");" +
                            "}\");" +
                            "@RETURN(TEST);" +
                          "}", "OK"),
                // five arguments IFs
                arguments("{" +
                            "@STRING(\"TEST\", \"\");" +
                            "@IF(\"1\",\"_\",\"1\",\"{" +
                                "TEST^SET(\"OK\");" +
                            "}\",\"{" +
                                "TEST^SET(\"BAD\");" +
                            "}\");" +
                            "@RETURN(TEST);" +
                          "}", "OK"),
                arguments("{" +
                            "@STRING(\"TEST\", \"\");" +
                            "@IF(\"TEST_BEH^RUN()\",\"_\",\"2\",\"{" +
                                "TEST^SET(\"OK\");" +
                            "}\",\"{" +
                                "TEST^SET(\"BAD\");" +
                            "}\");" +
                            "@RETURN(TEST);" +
                          "}", "OK"),
                arguments("{" +
                            "@STRING(\"TEST\", \"\");" +
                            "@IF(\"TEST_BEH\",\"_\",\"2\",\"{" +
                                "TEST^SET(\"OK\");" +
                            "}\",\"{" +
                                "TEST^SET(\"BAD\");" +
                            "}\");" +
                            "@RETURN(TEST);" +
                          "}", "BAD"),
                arguments("{" +
                            "@STRING(\"TEST\", \"\");" +
                            "@IF(\"TEST_STRUCT|VAL\",\"_\",\"5\",\"{" +
                                "TEST^SET(\"OK\");" +
                            "}\",\"{" +
                                "TEST^SET(\"BAD\");" +
                            "}\");" +
                            "@RETURN(TEST);" +
                          "}", "OK"),
                arguments("{" +
                            "@INT(\"A\",1);" +
                            "@INT(\"A\",[A+1]); " +
                            "@RETURN(A);" +
                          "}", 1), // probably redeclaration and using own value in that way is not permitted so engine returns 1, not 2
                arguments("{" +
                            "@INT(\"A\",1);" +
                            "@INT(\"A\",2); " +
                            "@RETURN(A);" +
                          "}", 1), // yup, redeclaration does not work.
                arguments("{" +
                            "@INT(\"A\",1);" +
                            "@INT(\"B\",[A+1]); " +
                            "@RETURN(B);" +
                          "}", 2),
                // fibonacci with little subtle changes
                arguments("{" +
                            "@INT(\"A\",0);" +
                            "@INT(\"B\",1);" +
                            "@INT(\"I\",2);" +
                            "@INT(\"N\",10);" +
                            "@INT(\"TMP\",0);" +
                            "@LOOP(\"{" +
                                "TMP^SET(B);" +
                                "B^SET([A+B]);" +
                                "A^SET(TMP);" +
                            "}\",I,[N-I+1],1);" +
                            "@RETURN(B);" +
                        "}", 55),
                arguments("{" +
                            "@INT(\"A\",0);" +
                            "@INT(\"B\",1);" +
                            "@INT(\"TMP\",0);" +
                            "@LOOP(\"{" +
                                "TMP^SET(B);" +
                                "B^SET([A+B]);" +
                                "A^SET(TMP);" +
                            "}\",2,9,1);" +
                            "@RETURN(B);" +
                           "}", 55),
                // access to variables by reference
                arguments("{@RETURN(*TEST_VALUE_NAME^GET());}", 5), // access by STRING variable
                arguments("{@RETURN(*[\"TEST_VALUE_\"+TEST_NO]^GET());}", 5), // access by arithmetic operation
                arguments("{@RETURN(*[\"TEST_VALUE_\"+TEST_NO+1]^GET());}", "TEST_VALUE_11"), // access to non-existent variable

                arguments("{@INT(\"B\",0);@RETURN(B);B^SET(1);}", 0), // RETURN only sets value to return, is not exiting the behaviour
                arguments("{@INT(\"B\",0);@RETURN(B);B^SET(1);@RETURN(B);B^SET(2);}", 1) // and every next call of @RETURN overrides previous value
        );
    }

    @ParameterizedTest(name = "{index} ⇒ {0}")
    @MethodSource("cases")
    void evals(String script, Object expected) {
        // TODO: Fix BehaviourVariable, as currently it retuns null for any method call, so all tests with method calls will fail. It sends effect no result
        BehaviourVariable behaviour = (BehaviourVariable) new BehaviourVariable("SCRIPT", new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript(script);
        MethodResult result = behaviour.callMethod("RUN");

        Value returnValue = result.returnValue();

        assertEquals(expected, returnValue.unwrap());
    }
}
