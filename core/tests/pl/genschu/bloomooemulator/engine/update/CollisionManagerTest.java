package pl.genschu.bloomooemulator.engine.update;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.geometry.spartial.QuadTree;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;

import java.util.ArrayList;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CollisionManagerTest {
    @Test
    void releasesPreviousPartnerThatIsNoLongerReturnedByQuadTree() {
        Game game = mock(Game.class);
        QuadTree quadTree = mock(QuadTree.class);
        ImageVariable moving = image("MOVING", new CanvasRect(0, 0, 10, 10));
        ImageVariable previousPartner = image("PARTNER", new CanvasRect(500, 500, 510, 510));

        when(game.getCollisionMonitoredVariables()).thenReturn(Set.of(moving));
        when(game.getQuadTree()).thenReturn(quadTree);
        when(quadTree.retrieve(anyList(), same(moving))).thenReturn(new ArrayList<>());
        when(game.getCollidingWith(moving)).thenReturn(Set.of(previousPartner));
        when(game.isColliding(moving, previousPartner)).thenReturn(true);

        new UpdateManager.CollisionManager(game).checkCollisions(moving);

        verify(game).releaseColliding(moving, previousPartner);
    }

    private static ImageVariable image(String name, CanvasRect rect) {
        ImageVariable image = new ImageVariable(name);
        image.state().rect = rect;
        return image;
    }

}
