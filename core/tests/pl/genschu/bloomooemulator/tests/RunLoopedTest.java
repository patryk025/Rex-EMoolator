package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.ast.BlockNode;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.MethodContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.genschu.bloomooemulator.builders.MethodHelper.createMethodContext;

/**
 * Regression tests for BEHAVIOUR^RUNLOOPED argument binding.
 *
 * <p>Mirrors the "curtain grid" scene that exposed two bugs at once:
 * <ol>
 *   <li>RUNLOOPED dropped the step argument when forwarding params, so the loop
 *       counter was $1 but the first forwarded arg landed on $2 — the script's
 *       $3 was never bound</li>
 *   <li>A quoted deferred expression like "[80*$1]" was passed through as a
 *       verbatim string instead of being evaluated in the calling frame.</li>
 * </ol>
 */
public class RunLoopedTest {
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder()
                .withVariable("STRING", "RES", "")
                .build();
    }

    private void defineBehaviour(String name, String script) {
        BehaviourVariable beh = (BehaviourVariable) new BehaviourVariable(
                name, new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript(script);
        ctx.setVariable(name, beh);
    }

    private String runAndReadRes(String topScript) {
        BehaviourVariable top = (BehaviourVariable) new BehaviourVariable(
                "TOP", new BlockNode(List.of(), SourceLocation.UNKNOWN), null)
                .withScript(topScript);
        MethodContext methodCtx = createMethodContext(ctx);
        top.callMethod("RUN", List.of(), methodCtx);
        return (String) ctx.getVariable("RES").value().unwrap();
    }

    /**
     * Nested RUNLOOPED: TOP loops MID (outer $1), MID loops CHILD passing the
     * quoted "[10*$1]". CHILD records its own counter ($1, inner) and the
     * forwarded value ($3). The quoted expression must evaluate in MID's frame,
     * so $3 == 10 * outer (constant across the inner loop) — a 2x2 grid:
     * outer 0 -> (0,0)(1,0); outer 1 -> (0,10)(1,10).
     */
    @Test
    void nestedRunLoopedEvaluatesQuotedExprInCallerFrame() {
        defineBehaviour("CHILD", "{RES^SET([RES+\"(\"+$1+\",\"+$3+\")\"]);}");
        defineBehaviour("MID", "{CHILD^RUNLOOPED(0,2,1,\"[10*$1]\");}");

        String res = runAndReadRes("{MID^RUNLOOPED(0,2);}");

        assertEquals("(0,0)(1,0)(0,10)(1,10)", res);
    }

    /**
     * Direct mapping: the loop counter is $1, the step (arg index 2) is re-exposed
     * as $2, and the trailing arg (index 3) becomes $3.
     */
    @Test
    void runLoopedExposesStepAndExtrasAsParams() {
        defineBehaviour("CHILD", "{RES^SET([RES+\"[\"+$1+\":\"+$2+\":\"+$3+\"]\"]);}");

        String res = runAndReadRes("{CHILD^RUNLOOPED(0,3,1,7);}");

        // counter 0,1,2 ; step 1 -> $2 ; extra 7 -> $3
        assertEquals("[0:1:7][1:1:7][2:1:7]", res);
    }

    /**
     * A non-trivial step is still honoured for iteration while also surfacing as $2.
     */
    @Test
    void runLoopedHonoursStepWhileExposingIt() {
        defineBehaviour("CHILD", "{RES^SET([RES+\"(\"+$1+\",\"+$2+\")\"]);}");

        String res = runAndReadRes("{CHILD^RUNLOOPED(0,6,2);}");

        // i = 0,2,4 (i < start+len = 6, step 2); $2 == step == 2
        assertEquals("(0,2)(2,2)(4,2)", res);
    }
}
