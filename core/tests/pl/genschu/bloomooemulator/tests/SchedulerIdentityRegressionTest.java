package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.time.LegacyClock;
import pl.genschu.bloomooemulator.engine.update.UpdateManager;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.KolorowankaVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SignalHandler;
import pl.genschu.bloomooemulator.interpreter.variable.TimerVariable;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.loader.PtrLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class SchedulerIdentityRegressionTest {

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void schedulingViewKeepsEqualNamesInStableContextTraversalOrder() {
        Context parent = context(null);
        Context scene = context(parent);
        Context firstAdditional = context(null);
        Context secondAdditional = context(null);
        scene.addAdditionalContext(firstAdditional);
        scene.addAdditionalContext(secondAdditional);

        TimerVariable first = new TimerVariable("CLOCK", 0L);
        TimerVariable second = new TimerVariable("CLOCK", 0L);
        TimerVariable local = new TimerVariable("CLOCK", 0L);
        TimerVariable inherited = new TimerVariable("CLOCK", 0L);
        firstAdditional.setVariable("CLOCK", first);
        secondAdditional.setVariable("CLOCK", second);
        scene.setVariable("CLOCK", local);
        parent.setVariable("CLOCK", inherited);

        assertEquals(
            List.of(first, second, local, inherited),
            scene.getTimerVariablesForScheduling()
        );

        // Script lookup views remain name-keyed and retain their old shadowing rules.
        assertEquals(1, scene.getTimerVariables().size());
        assertSame(inherited, scene.getTimerVariables().get("CLOCK"));
    }

    @Test
    void schedulingViewDeduplicatesTheSameObjectByIdentity() {
        Context scene = context(null);
        Context firstAdditional = context(null);
        Context secondAdditional = context(null);
        scene.addAdditionalContext(firstAdditional);
        scene.addAdditionalContext(secondAdditional);

        TimerVariable shared = new TimerVariable("SHARED", 0L);
        firstAdditional.setVariable("SHARED", shared);
        secondAdditional.setVariable("SHARED", shared);

        assertEquals(List.of(shared), scene.getTimerVariablesForScheduling());
    }

    @Test
    void timerManagerTicksEveryEqualNamedTimerFromItsInitialSnapshot() {
        List<String> callbackOrder = new ArrayList<>();
        Context parent = context(null);
        Context scene = context(parent);
        Context additional = context(null);
        scene.addAdditionalContext(additional);

        TimerVariable inherited = timer("parent", callbackOrder, null);
        TimerVariable local = timer("scene", callbackOrder, null);
        TimerVariable loaded = timer(
            "additional",
            callbackOrder,
            () -> parent.removeVariable("CLOCK")
        );
        additional.setVariable("CLOCK", loaded);
        scene.setVariable("CLOCK", local);
        parent.setVariable("CLOCK", inherited);

        LegacyClock clock = () -> 0L;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);

        new UpdateManager.TimerManager(game).updateTimers(clock);

        assertEquals(List.of("additional", "scene", "parent"), callbackOrder);
        assertEquals(1, loaded.currentTickCount());
        assertEquals(1, local.currentTickCount());
        assertEquals(1, inherited.currentTickCount(),
            "removing a later timer during a callback must not mutate this pass's snapshot");
    }

    @Test
    void timerManagerSamplesTheClockSeparatelyForEachTimer() {
        Context scene = context(null);
        AtomicLong now = new AtomicLong(100L);
        TimerVariable first = new TimerVariable("FIRST", 100L, true, 0, 0L, 0, Map.of());
        first = (TimerVariable) first.withSignal(
                "ONTICK^1", (variable, signal, args) -> now.set(150L));
        TimerVariable second = new TimerVariable("SECOND", 40L, true, 0, 100L, 0, Map.of());
        scene.setVariable("FIRST", first);
        scene.setVariable("SECOND", second);

        LegacyClock clock = now::get;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);

        new UpdateManager.TimerManager(game).updateTimers(clock);

        assertEquals(1, first.currentTickCount());
        assertEquals(1, second.currentTickCount(),
                "a long earlier callback can make a later timer due in the same manager pass");
    }

    @Test
    void timerManagerUsesCanonicalWrapperForACnvLoaderSharedState() {
        Context scene = context(null);
        Context loadedContext = context(scene);
        scene.addAdditionalContext(loadedContext);
        AtomicInteger staleTicks = new AtomicInteger();
        AtomicInteger currentTicks = new AtomicInteger();

        TimerVariable original = new TimerVariable("CLOCK", 0L, true, 0, 0L, 0, Map.of());
        TimerVariable stale = (TimerVariable) original.withSignal(
                "ONTICK^1", (variable, signal, args) -> staleTicks.incrementAndGet());
        TimerVariable current = (TimerVariable) stale.withSignal(
                "ONTICK^1", (variable, signal, args) -> currentTicks.incrementAndGet());
        loadedContext.setVariable("CLOCK", stale);
        scene.setVariable("CLOCK", current);

        LegacyClock clock = () -> 0L;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);

        new UpdateManager.TimerManager(game).updateTimers(clock);

        assertEquals(1, current.currentTickCount());
        assertEquals(1, currentTicks.get());
        assertEquals(0, staleTicks.get());
    }

    @Test
    void animationManagerRegistersEveryEqualNamedAnimo() {
        Context parent = context(null);
        Context scene = context(parent);
        Context additional = context(null);
        scene.addAdditionalContext(additional);

        AnimoVariable loaded = new AnimoVariable("CLOCK_ANIMO");
        AnimoVariable local = new AnimoVariable("CLOCK_ANIMO");
        AnimoVariable inherited = new AnimoVariable("CLOCK_ANIMO");
        additional.setVariable("CLOCK_ANIMO", loaded);
        scene.setVariable("CLOCK_ANIMO", local);
        parent.setVariable("CLOCK_ANIMO", inherited);

        LegacyClock clock = () -> 123L;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);

        new UpdateManager.AnimationManager(game).updateAnimations(clock);

        assertEquals(123L, loaded.state().lastTickAtMs);
        assertEquals(123L, local.state().lastTickAtMs);
        assertEquals(123L, inherited.state().lastTickAtMs);
    }

    @Test
    void cloneRegistersItsAnimationClockAtAddTime() {
        Context scene = context(null);
        LegacyClock clock = () -> 123L;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);
        scene.setGame(game);
        scene.setVariable("TEMPLATE", new AnimoVariable("TEMPLATE"));

        MethodHelper.callWithContext(scene, "TEMPLATE", "CLONE");

        AnimoVariable clone = (AnimoVariable) scene.getVariable("TEMPLATE_1");
        assertEquals(123L, clone.state().lastTickAtMs);
    }

    @Test
    void animationManagerFollowsAReplacementWrapperWithTheSamePlaybackState() {
        Context scene = context(null);
        AtomicInteger laterTicks = new AtomicInteger();

        AnimoVariable first = playingAnimo("FIRST");
        AnimoVariable later = playingAnimo("LATER");
        first = (AnimoVariable) first.withSignal("ONFRAMECHANGED", (variable, signal, args) -> {
            AnimoVariable currentLater = (AnimoVariable) scene.getVariable("LATER");
            scene.setVariable("LATER", currentLater.withSignal(
                    "ONFRAMECHANGED",
                    (laterVariable, laterSignal, laterArgs) -> laterTicks.incrementAndGet()));
        });
        scene.setVariable("FIRST", first);
        scene.setVariable("LATER", later);

        LegacyClock clock = () -> 100L;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);

        new UpdateManager.AnimationManager(game).updateAnimations(clock);

        assertEquals(1, laterTicks.get(),
                "withSignal must not make a due logical ANIMO look unregistered");
        assertEquals(1, ((AnimoVariable) scene.getVariable("LATER")).getCurrentFrameNumber());
    }

    @Test
    void animationManagerUsesCanonicalWrapperForACnvLoaderSharedState() {
        Context scene = context(null);
        Context loadedContext = context(scene);
        scene.addAdditionalContext(loadedContext);
        AtomicInteger staleTicks = new AtomicInteger();
        AtomicInteger currentTicks = new AtomicInteger();

        AnimoVariable original = playingAnimo("CLOCK_ANIMO");
        AnimoVariable stale = (AnimoVariable) original.withSignal(
                "ONFRAMECHANGED", (variable, signal, args) -> staleTicks.incrementAndGet());
        AnimoVariable current = (AnimoVariable) stale.withSignal(
                "ONFRAMECHANGED", (variable, signal, args) -> currentTicks.incrementAndGet());
        loadedContext.setVariable("CLOCK_ANIMO", stale);
        scene.setVariable("CLOCK_ANIMO", current);

        LegacyClock clock = () -> 100L;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);

        new UpdateManager.AnimationManager(game).updateAnimations(clock);

        assertEquals(1, current.getCurrentFrameNumber());
        assertEquals(1, currentTicks.get());
        assertEquals(0, staleTicks.get());
    }

    @Test
    void animationManagerUsesCanonicalKolorowankaWrapperOnlyOnce() {
        Context scene = context(null);
        Context loadedContext = context(scene);
        scene.addAdditionalContext(loadedContext);
        AtomicInteger staleTicks = new AtomicInteger();
        AtomicInteger currentTicks = new AtomicInteger();

        KolorowankaVariable original = finishingKolorowanka("PAINTER");
        KolorowankaVariable stale = (KolorowankaVariable) original.withSignal(
                "ONFINISHED", (variable, signal, args) -> staleTicks.incrementAndGet());
        KolorowankaVariable current = (KolorowankaVariable) stale.withSignal(
                "ONFINISHED", (variable, signal, args) -> currentTicks.incrementAndGet());
        loadedContext.setVariable("PAINTER", stale);
        scene.setVariable("PAINTER", current);

        LegacyClock clock = () -> 100L;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);

        new UpdateManager.AnimationManager(game).updateAnimations(clock);

        assertEquals(1, currentTicks.get());
        assertEquals(0, staleTicks.get());
    }

    @Test
    void animationManagerUsesOneObservationForTheEntireDueScan() {
        Context scene = context(null);
        AtomicLong now = new AtomicLong(100L);

        AnimoVariable first = playingAnimo("FIRST");
        AnimoVariable later = playingAnimo("LATER");
        later.state().lastTickAtMs = 60L;
        first = (AnimoVariable) first.withSignal(
                "ONFRAMECHANGED", (variable, signal, args) -> now.set(500L));
        scene.setVariable("FIRST", first);
        scene.setVariable("LATER", later);

        LegacyClock clock = now::get;
        Game game = new Game(null, null, clock);
        game.setCurrentSceneContext(scene);

        new UpdateManager.AnimationManager(game).updateAnimations(clock);

        assertEquals(1, first.getCurrentFrameNumber());
        assertEquals(0, later.getCurrentFrameNumber(),
                "callback time cannot make an object due after the manager-wide scan");
    }

    private static Context context(Context parent) {
        return new Context(new ExecutionContext(), parent);
    }

    private static TimerVariable timer(String id, List<String> callbackOrder, Runnable mutation) {
        TimerVariable timer = new TimerVariable("CLOCK", 0L, true, 0, 0L, 0, Map.of());
        SignalHandler handler = (variable, signal, args) -> {
            callbackOrder.add(id);
            if (mutation != null) {
                mutation.run();
            }
        };
        return (TimerVariable) timer.withSignal("ONTICK^1", handler);
    }

    private static AnimoVariable playingAnimo(String name) {
        Image image = mock(Image.class);
        Event event = new Event();
        event.setName("TICK");
        event.setFramesCount(2);
        event.setFramesNumbers(List.of(0, 0));
        event.setFrames(List.of(image, image));
        event.setFrameData(List.of(new FrameData(), new FrameData()));

        AnimoVariable.AnimoData data = new AnimoVariable.AnimoData(
                List.of(event), List.of(image), 1, 1,
                16, 15, 255, 0, 0, "", "");
        AnimoVariable animo = new AnimoVariable(name).withData(data);
        animo.registerAnimationClock(0L);
        animo.callMethod("PLAY", new StringValue("TICK"));
        return animo;
    }

    private static KolorowankaVariable finishingKolorowanka(String name) {
        KolorowankaVariable kolorowanka = new KolorowankaVariable(name);
        KolorowankaVariable.KolorowankaState state = kolorowanka.state();
        state.ptr = new PtrLoader.PtrFile();
        state.palette16 = new int[]{0xFFFF};
        state.currentColorId = 0;
        state.fadeFrom16 = 0;
        state.fadeTo16 = 0xFFFF;
        state.fadeStep = 9;
        state.lastFadeTimeMs = 0L;

        PtrLoader.Region region = new PtrLoader.Region();
        region.left = 0;
        region.top = 0;
        region.right = 1;
        region.bottom = 1;
        region.rows = new int[][]{{0, 1}};
        PtrLoader.Field definition = new PtrLoader.Field();
        definition.name = "FIELD";
        definition.requiredColorId = 0;
        definition.fillRegion = region;
        KolorowankaVariable.Field field = new KolorowankaVariable.Field();
        field.def = definition;
        state.fields.add(field);
        state.pendingFields.add(field);
        return kolorowanka;
    }
}
