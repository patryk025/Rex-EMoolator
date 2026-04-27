package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.saver.ImageSaver;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * CanvasObserverVariable observes the canvas (screen) and provides methods
 * for querying graphics at positions, managing backgrounds, and saving screenshots.
 */
public record CanvasObserverVariable(
    String name,
    Map<String, SignalHandler> signals
) implements Variable {

    public CanvasObserverVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public CanvasObserverVariable(String name) {
        this(name, Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.CANVAS_OBSERVER;
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
        return new CanvasObserverVariable(name, newSignals);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    private static Box2D getRect(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.getRect();
        if (variable instanceof AnimoVariable animo) return animo.getRect();
        return null;
    }

    private static Image getImage(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.getImage();
        if (variable instanceof AnimoVariable animo) return animo.getCurrentImage();
        return null;
    }

    private static int getPriority(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.state().priority;
        if (variable instanceof AnimoVariable animo) return animo.getPriority();
        return 0;
    }

    private static boolean isVisible(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.isVisible();
        if (variable instanceof AnimoVariable animo) return animo.isVisible();
        return false;
    }

    public static void flipPixmapVertically(Pixmap p) {
        int w = p.getWidth();
        int h = p.getHeight();
        int hold;
        p.setBlending(Pixmap.Blending.None);
        for (int y = 0; y < h / 2; y++) {
            for (int x = 0; x < w; x++) {
                hold = p.getPixel(x, y);
                p.drawPixel(x, y, p.getPixel(x, h - y - 1));
                p.drawPixel(x, h - y - 1, hold);
            }
        }
        p.setBlending(Pixmap.Blending.SourceOver);
    }

    private static byte[] pixmapToByteArray(Pixmap pixmap) {
        ByteBuffer buffer = pixmap.getPixels();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return byteArray;
    }

    // ========================================
    // METHODS
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args, ctx) -> {
            // TODO: ADD(animoVar)
            throw new UnsupportedOperationException("CANVAS_OBSERVER.ADD not implemented");
        })),

        Map.entry("ENABLENOTIFY", MethodSpec.of((self, args, ctx) -> {
            // TODO: ENABLENOTIFY(enable)
            throw new UnsupportedOperationException("CANVAS_OBSERVER.ENABLENOTIFY not implemented");
        })),

        Map.entry("GETGRAPHICSAT", MethodSpec.of((self, args, ctx) -> {
            int posX = ArgumentHelper.getInt(args.get(0));
            int posY = ArgumentHelper.getInt(args.get(1));
            boolean onlyVisible = args.size() > 2 && ArgumentHelper.getBoolean(args.get(2));
            int minZ = args.size() > 3 ? ArgumentHelper.getInt(args.get(3)) : Integer.MIN_VALUE;
            int maxZ = args.size() > 4 ? ArgumentHelper.getInt(args.get(4)) : Integer.MAX_VALUE;
            boolean includeAlpha = args.size() > 5 && ArgumentHelper.getBoolean(args.get(5));

            List<EngineVariable> drawList = new ArrayList<>(ctx.getGame().getCurrentSceneContext().getGraphicsVariables().values());

            drawList.sort((v1, v2) -> Integer.compare(getPriority(v2), getPriority(v1)));

            for (EngineVariable variable : drawList) {
                if (onlyVisible && !isVisible(variable)) continue;

                int z = getPriority(variable);
                if (z < minZ || z > maxZ) continue;

                Box2D rect = getRect(variable);
                if (rect == null) continue;

                boolean containsPoint;
                if (includeAlpha) {
                    containsPoint = rect.contains(posX, posY);
                } else {
                    if (rect.contains(posX, posY)) {
                        Image image = getImage(variable);
                        int relativeX = posX - rect.getXLeft();
                        int relativeY = posY - rect.getYTop();
                        int alpha = 255;

                        if (image != null && image.getImageTexture() != null) {
                            TextureData textureData = image.getImageTexture().getTextureData();
                            if (!textureData.isPrepared()) textureData.prepare();
                            Pixmap pixmap = textureData.consumePixmap();
                            int pixel = pixmap.getPixel(relativeX, relativeY);
                            alpha = (pixel & 0xFF);
                            if (textureData.disposePixmap()) pixmap.dispose();
                        }

                        containsPoint = alpha > 0;
                    } else {
                        containsPoint = false;
                    }
                }

                if (containsPoint) {
                    return MethodResult.returns(new StringValue(variable.getName()));
                }
            }

            return MethodResult.returns(new StringValue("NULL"));
        })),

        Map.entry("MOVEBKG", MethodSpec.of((self, args, ctx) -> {
            // TODO: MOVEBKG(deltaX, deltaY)
            throw new UnsupportedOperationException("CANVAS_OBSERVER.MOVEBKG not implemented");
        })),

        Map.entry("PASTE", MethodSpec.of((self, args, ctx) -> {
            // TODO: PASTE(varName, posX, posY)
            throw new UnsupportedOperationException("CANVAS_OBSERVER.PASTE not implemented");
        })),

        Map.entry("REDRAW", MethodSpec.of((self, args, ctx) -> {
            // TODO: REDRAW
            throw new UnsupportedOperationException("CANVAS_OBSERVER.REDRAW not implemented");
        })),

        Map.entry("REFRESH", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.error("CanvasObserverVariable", "Currently refresh is not supported");
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVE", MethodSpec.of((self, args, ctx) -> {
            for (Value arg : args) {
                String varName = ArgumentHelper.getString(arg);
                Variable var = ctx.getVariable(varName);
                if (var instanceof ImageVariable img) {
                    img.state().visible = false;
                } else if (var instanceof AnimoVariable animo) {
                    animo.setVisible(false);
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SAVE", MethodSpec.of((self, args, ctx) -> {
            CanvasObserverVariable coSelf = (CanvasObserverVariable) self;
            String imgFileName = ArgumentHelper.getString(args.get(0));
            double xScaleFactor = ArgumentHelper.getDouble(args.get(1));
            double yScaleFactor = ArgumentHelper.getDouble(args.get(2));
            int xLeft = 0, yTop = 0, xRight = 800, yBottom = 600;

            if (args.size() == 7) {
                xLeft = ArgumentHelper.getInt(args.get(3));
                yTop = ArgumentHelper.getInt(args.get(4));
                xRight = ArgumentHelper.getInt(args.get(5));
                yBottom = ArgumentHelper.getInt(args.get(6));
            }

            Pixmap pixmap = ctx.getGame().getLastFrame();
            if (pixmap == null) {
                Gdx.app.error("CanvasObserverVariable", "Pixmap is null, screenshots may be not captured correctly");
                pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB565);
                Gdx.gl.glReadPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), GL20.GL_RGB, GL20.GL_UNSIGNED_SHORT_5_6_5, pixmap.getPixels());
            }

            flipPixmapVertically(pixmap);

            Pixmap croppedPixmap = new Pixmap(xRight - xLeft, yBottom - yTop, pixmap.getFormat());
            croppedPixmap.drawPixmap(pixmap, 0, 0, xLeft, yTop, xRight - xLeft, yBottom - yTop);
            pixmap.dispose();

            Pixmap scaledPixmap = new Pixmap((int) ((xRight - xLeft) * xScaleFactor), (int) ((yBottom - yTop) * yScaleFactor), croppedPixmap.getFormat());
            scaledPixmap.drawPixmap(croppedPixmap, 0, 0, croppedPixmap.getWidth(), croppedPixmap.getHeight(), 0, 0, scaledPixmap.getWidth(), scaledPixmap.getHeight());
            croppedPixmap.dispose();

            int width = scaledPixmap.getWidth();
            int height = scaledPixmap.getHeight();

            ImageSaver.saveScreenshot(ctx.getGame(), imgFileName, pixmapToByteArray(scaledPixmap), width, height);
            scaledPixmap.dispose();

            return MethodResult.noReturn();
        })),

        Map.entry("SETBACKGROUND", MethodSpec.of((self, args, ctx) -> {
            String imageName = ArgumentHelper.getString(args.get(0));
            Variable var = ctx.getVariable(imageName);
            if (var instanceof ImageVariable img) {
                ctx.getGame().setCurrentBackgroundImage(img);
                return MethodResult.noReturn();
            }
            // Create a new ImageVariable for the background and load it
            ImageVariable bgImage = new ImageVariable(imageName + "_BKG", imageName);
            bgImage.load(ctx.getGame());
            ctx.getGame().setCurrentBackgroundImage(bgImage);
            return MethodResult.noReturn();
        })),

        Map.entry("SETBKGPOS", MethodSpec.of((self, args, ctx) -> {
            // TODO: SETBKGPOS(posX, posY)
            throw new UnsupportedOperationException("CANVAS_OBSERVER.SETBKGPOS not implemented");
        }))
    );

    @Override
    public String toString() {
        return "CanvasObserverVariable[" + name + "]";
    }
}
