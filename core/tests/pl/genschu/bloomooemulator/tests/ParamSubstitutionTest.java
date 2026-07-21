package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.ast.BlockNode;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.ApplicationVariable;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.MethodContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.genschu.bloomooemulator.builders.MethodHelper.createMethodContext;

/**
 * Characterization tests for $N parameter substitution.
 *
 * <p>The original engine substitutes a behaviour's arguments <em>textually</em>
 * and re-parses the body, so a $N token is read as a bare reference — it denotes
 * the variable it names if one exists, otherwise the literal text. We splice the
 * argument text, and these tests pin the positions where that difference surfaces:
 * <ol>
 *   <li>target position — {@code $1^METHOD} and concatenation {@code PREFIX$1^METHOD}</li>
 *   <li>value position — {@code VARNR^SET($1)} where $1 names another variable</li>
 *   <li>RUN indirection — a temp STRING holding the caller's name, forwarded via $1</li>
 * </ol>
 */
public class ParamSubstitutionTest {
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder()
                .withVariable("INTEGER", "VARNR", 0)
                .withVariable("STRING", "RESULT", "")
                .build();
    }

    private void defineBehaviour(String name, String script) {
        BehaviourVariable beh = (BehaviourVariable) new BehaviourVariable(
                name, new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript(script);
        ctx.setVariable(name, beh);
    }

    private void runTop(String topScript, Object... args) {
        BehaviourVariable top = (BehaviourVariable) new BehaviourVariable(
                "TOP", new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript(topScript);
        top.callMethod("RUN", List.of(), createMethodContext(ctx));
    }

    private int readInt(String name) {
        return ((Number) ctx.getVariable(name).value().unwrap()).intValue();
    }

    /**
     * Target position: $1 holds a variable name, and {@code $1^SET(5)} must set
     * the variable that name denotes — not fail on the literal "VARNR".
     */
    @Test
    void targetPositionResolvesParamAsVariableName() {
        defineBehaviour("CHILD", "{$1^SET(5);}");
        runTop("{CHILD^RUN(\"VARNR\");}");
        assertEquals(5, readInt("VARNR"));
    }

    /**
     * Target concatenation: {@code VARN$1^SET(7)} with $1="R" must build the name
     * VARNR by splicing the param's text into the identifier.
     */
    @Test
    void targetPositionResolvesConcatenatedParamName() {
        defineBehaviour("CHILD", "{VARN$1^SET(7);}");
        runTop("{CHILD^RUN(\"R\");}");
        assertEquals(7, readInt("VARNR"));
    }

    /**
     * Value position: $1="VARSRC" names an INTEGER holding 42. {@code VARNR^SET($1)}
     * must dereference one level and store 42 — not the literal string "VARSRC".
     */
    @Test
    void valuePositionDereferencesParamNamingAVariable() {
        ctx.setVariable("VARSRC", new pl.genschu.bloomooemulator.interpreter.variable.IntegerVariable("VARSRC", 42));
        defineBehaviour("CHILD", "{VARNR^SET($1);}");
        runTop("{CHILD^RUN(\"VARSRC\");}");
        assertEquals(42, readInt("VARNR"));
    }

    /**
     * Value position, plain value: $1=9 (a number, not a variable name) must store
     * the literal 9 — the bare-token rule falls through to the value for params
     * that name no variable.
     */
    @Test
    void valuePositionKeepsPlainNumericParam() {
        defineBehaviour("CHILD", "{VARNR^SET($1);}");
        runTop("{CHILD^RUN(9);}");
        assertEquals(9, readInt("VARNR"));
    }

    @Test
    void numericParamRemainsNumericInArithmetic() {
        defineBehaviour("CHILD", "{VARNR^SET([$1+1]);}");
        runTop("{CHILD^RUN(3);}");
        assertEquals(4, readInt("VARNR"));
    }

    /**
     * Quoted parameters are still textually substituted. DARRAY.CLASS uses
     * exactly this form to forward its filename: DBFIELD^LOAD("$1").
     */
    @Test
    void quotedValuePositionInterpolatesParamText() {
        defineBehaviour("CHILD", "{RESULT^SET(\"$1\");}");
        runTop("{CHILD^RUN(\"$COMMON/BOICHO.DTA\");}");
        assertEquals("$COMMON/BOICHO.DTA", ctx.getVariable("RESULT").value().toDisplayString());
    }

    /**
     * RUN indirection through a temp STRING: TOP stashes the real target name
     * "VARNR" in VARSTEMP1 and forwards <em>VARSTEMP1</em> via $1 to APP^RUN. The
     * $1 must resolve to "VARNR" (one deref) before RUN runs SET on it.
     */
    @Test
    void runIndirectionThroughTempStringResolvesToObject() {
        ctx.setVariable("APP", new ApplicationVariable("APP"));
        ctx.setVariable("VARSTEMP1", new pl.genschu.bloomooemulator.interpreter.variable.StringVariable("VARSTEMP1", "VARNR"));
        defineBehaviour("MID", "{APP^RUN($1,\"SET\",99);}");
        runTop("{MID^RUN(\"VARSTEMP1\");}");
        assertEquals(99, readInt("VARNR"));
    }

    @Test
    void substitutesMethodNameBeforeParsing() {
        defineBehaviour("CHILD", "{VARNR^$1(5);}");
        runTop("{CHILD^RUN(\"SET\");}");
        assertEquals(5, readInt("VARNR"));
    }

    @Test
    void parameterSelectorConsumesExactlyOneDigit() {
        defineBehaviour("CHILD", "{RESULT^SET(\"$10\");}");
        runTop("{CHILD^RUN(\"A\");}");
        assertEquals("A0", ctx.getVariable("RESULT").value().toDisplayString());
    }

    @Test
    void zeroIsNotAParameterSelector() {
        defineBehaviour("CHILD", "{RESULT^SET(\"$0\");}");
        runTop("{CHILD^RUN(\"A\");}");
        assertEquals("$0", ctx.getVariable("RESULT").value().toDisplayString());
    }

    @Test
    void missingParameterDoesNotLeakFromCallingBehaviour() {
        defineBehaviour("CHILD", "{RESULT^SET(\"$2\");}");
        defineBehaviour("MID", "{CHILD^RUN();}");
        runTop("{MID^RUN(\"FIRST\",\"LEAK\");}");
        assertEquals("$2", ctx.getVariable("RESULT").value().toDisplayString());
    }

    @Test
    void concatenatedParameterCanChangeValueTokenIntoVariableReference() {
        ctx.setVariable("VARNR2", new pl.genschu.bloomooemulator.interpreter.variable.IntegerVariable("VARNR2", 77));
        defineBehaviour("CHILD", "{RESULT^SET(VARN$1);}");
        runTop("{CHILD^RUN(\"R2\");}");
        assertEquals("77", ctx.getVariable("RESULT").value().toDisplayString());
    }

    @Test
    void stripsAsciiSpacesInsideQuotedLiterals() {
        runTop("{RESULT^SET(\"A B C\");}");
        assertEquals("ABC", ctx.getVariable("RESULT").value().toDisplayString());
    }

    @Test
    void wildSubstitutionCanChooseTargetMethodAndArity() {
        defineBehaviour("TEST_BEH", "{$1^$2($3);}");

        runTop("{TEST_BEH^RUN(\"RESULT\",\"ADD\",\"TEST\");"
            + "TEST_BEH^RUN(\"VARNR\",\"ADD\",1);"
            + "TEST_BEH^RUN(\"VARNR\",\"INC\");}");

        assertEquals("TEST", ctx.getVariable("RESULT").value().toDisplayString());
        assertEquals(2, readInt("VARNR"));
    }
}
