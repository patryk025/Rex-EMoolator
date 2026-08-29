package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.engine.context.CanvasBoundsProvider;
import pl.genschu.bloomooemulator.engine.context.CurrentImageProvider;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.render.PastedGraphic;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasCoordinateSystem;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.saver.ImageSaver;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CanvasObserverVariable observes the canvas (screen) and provides methods
 * for querying graphics at positions, managing backgrounds, and saving screenshots.
 */
public record CanvasObserverVariable(
    String name,
    Map<String, SignalHandler> signals,
    AtomicBoolean notificationsEnabled
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
        if (notificationsEnabled == null) {
            notificationsEnabled = new AtomicBoolean(true);
        }
    }

    public CanvasObserverVariable(String name) {
        this(name, Map.of(), new AtomicBoolean(true));
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
        return new CanvasObserverVariable(name, newSignals, notificationsEnabled);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

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

    private static float getOpacity(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.getOpacity();
        if (variable instanceof AnimoVariable animo) return animo.getOpacity() / 255.0f;
        return 1f;
    }

    private static byte[] pixmapToByteArray(Pixmap pixmap) {
        ByteBuffer buffer = pixmap.getPixels();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return byteArray;
    }

    private static Texture snapshotTexture(Texture src) {
        if (src == null) return null;
        TextureData data = src.getTextureData();
        if (!data.isPrepared()) data.prepare();
        Pixmap pixmap = data.consumePixmap();
        Pixmap copy = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
        copy.setBlending(Pixmap.Blending.None);
        copy.drawPixmap(pixmap, 0, 0);
        if (data.disposePixmap()) pixmap.dispose();
        Texture result = new Texture(copy);
        copy.dispose();
        return result;
    }

    // ========================================
    // METHODS
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args, ctx) -> {
            if (args.isEmpty()) return MethodResult.noReturn();
            String varName = ArgumentHelper.getString(args.get(0));
            int priority = args.size() > 1 ? ArgumentHelper.getInt(args.get(1)) : 1000;
            Variable var = ctx.getVariable(varName);
            if (var instanceof ImageVariable img) {
                img.state().priority = priority;
                img.state().visible = true;
            } else if (var instanceof AnimoVariable animo) {
                animo.setPriority(priority);
                animo.setVisible(true);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("ENABLENOTIFY", MethodSpec.of((self, args, ctx) -> {
            CanvasObserverVariable co = (CanvasObserverVariable) self;
            boolean enable = !args.isEmpty() && ArgumentHelper.getBoolean(args.get(0));
            co.notificationsEnabled.set(enable);
            return MethodResult.noReturn();
        })),

        Map.entry("GETBPP", MethodSpec.of((self, args, ctx) -> {
            // Original BlooMoo runs in 16bpp (RGB565). The emulator uses RGBA8888 internally
            // but games querying this expect the canonical BlooMoo value.
            return MethodResult.returns(new IntValue(16));
        })),

        Map.entry("GETGRAPHICSAT", MethodSpec.of((self, args, ctx) -> {
            // Searches only the current scene container (no walk-up to episode/root).
            return getGraphicsAt(args, ctx, false);
        })),

        Map.entry("GETGRAPHICSAT2", MethodSpec.of((self, args, ctx) -> {
            // Walks up the container hierarchy (scene -> episode -> root) if not found in current.
            return getGraphicsAt(args, ctx, true);
        })),

        Map.entry("MOVEBKG", MethodSpec.of((self, args, ctx) -> {
            int dx = ArgumentHelper.getInt(args.get(0));
            int dy = ArgumentHelper.getInt(args.get(1));
            if (ctx.getGame().getCurrentBackgroundImage() != null) {
                ctx.getGame().moveBackground(dx, dy);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("PASTE", MethodSpec.of((self, args, ctx) -> {
            String varName = ArgumentHelper.getString(args.get(0));
            int posX = ArgumentHelper.getInt(args.get(1));
            int posY = ArgumentHelper.getInt(args.get(2));

            Variable var = ctx.getVariable(varName);
            if (!(var instanceof EngineVariable ev)) return MethodResult.noReturn();
            Image image = ev instanceof CurrentImageProvider imageProvider
                    ? imageProvider.getCurrentImage()
                    : null;
            if (image == null || image.getImageTexture() == null) return MethodResult.noReturn();

            Texture snapshot = snapshotTexture(image.getImageTexture());
            CanvasRect bounds = CanvasRect.fromPositionAndSize(
                    posX, posY, image.width, image.height);
            PastedGraphic p = new PastedGraphic(snapshot, bounds, getOpacity(ev));
            ctx.getGame().addPastedGraphic(p);
            return MethodResult.noReturn();
        })),

        Map.entry("REDRAW", MethodSpec.of((self, args, ctx) -> {
            // In the original this marks the canvas dirty and notifies observers. The emulator
            // redraws the canvas every frame anyway, so there is nothing to do here.
            return MethodResult.noReturn();
        })),

        Map.entry("REFRESH", MethodSpec.of((self, args, ctx) -> {
            // technically in original it sets all canvas as dirty for redraw, but as I do that in every frame
            // just simply invalidate all IMAGE variables
            ctx.getGame().getCurrentSceneContext().getGraphicsVariables().values()
                    .forEach(variable -> {
                        if (variable instanceof ImageVariable img) {
                            img.invalidate();
                        }
                    });
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
            String imgFileName = ArgumentHelper.getString(args.get(0));
            double xScaleFactor = ArgumentHelper.getDouble(args.get(1));
            double yScaleFactor = ArgumentHelper.getDouble(args.get(2));
            int xLeft = 0;
            int yTop = 0;
            int xRight = CanvasCoordinateSystem.WIDTH;
            int yBottom = CanvasCoordinateSystem.HEIGHT;

            if (args.size() == 7) {
                xLeft = ArgumentHelper.getInt(args.get(3));
                yTop = ArgumentHelper.getInt(args.get(4));
                xRight = ArgumentHelper.getInt(args.get(5));
                yBottom = ArgumentHelper.getInt(args.get(6));
            }

            Pixmap pixmap = ctx.getGame().captureCanvas();
            if (pixmap == null) {
                Gdx.app.error("CanvasObserverVariable", "Logical canvas is not available; screenshot was not saved");
                return MethodResult.noReturn();
            }

            Pixmap croppedPixmap = null;
            Pixmap scaledPixmap = null;
            try {
                CanvasRect cropRect;
                try {
                    cropRect = new CanvasRect(xLeft, yTop, xRight, yBottom);
                } catch (IllegalArgumentException e) {
                    Gdx.app.error("CanvasObserverVariable", "Invalid screenshot rectangle or scale");
                    return MethodResult.noReturn();
                }
                int cropWidth = cropRect.width();
                int cropHeight = cropRect.height();
                int scaledWidth = (int) (cropWidth * xScaleFactor);
                int scaledHeight = (int) (cropHeight * yScaleFactor);
                CanvasRect availableCanvas = new CanvasRect(
                        0, 0, pixmap.getWidth(), pixmap.getHeight());
                boolean cropOutsideCanvas = !availableCanvas.contains(cropRect);
                if (cropRect.isEmpty()
                        || scaledWidth <= 0
                        || scaledHeight <= 0
                        || cropOutsideCanvas) {
                    Gdx.app.error("CanvasObserverVariable", "Invalid screenshot rectangle or scale");
                    return MethodResult.noReturn();
                }

                croppedPixmap = new Pixmap(cropWidth, cropHeight, pixmap.getFormat());
                croppedPixmap.setBlending(Pixmap.Blending.None);
                croppedPixmap.drawPixmap(pixmap, 0, 0, xLeft, yTop, cropWidth, cropHeight);

                scaledPixmap = new Pixmap(scaledWidth, scaledHeight, croppedPixmap.getFormat());
                scaledPixmap.setBlending(Pixmap.Blending.None);
                scaledPixmap.drawPixmap(
                        croppedPixmap,
                        0,
                        0,
                        croppedPixmap.getWidth(),
                        croppedPixmap.getHeight(),
                        0,
                        0,
                        scaledPixmap.getWidth(),
                        scaledPixmap.getHeight());

                ImageSaver.saveScreenshot(
                        ctx.getGame(),
                        imgFileName,
                        pixmapToByteArray(scaledPixmap),
                        scaledPixmap.getWidth(),
                        scaledPixmap.getHeight());
            } finally {
                if (scaledPixmap != null && !scaledPixmap.isDisposed()) {
                    scaledPixmap.dispose();
                }
                if (croppedPixmap != null && !croppedPixmap.isDisposed()) {
                    croppedPixmap.dispose();
                }
                if (!pixmap.isDisposed()) {
                    pixmap.dispose();
                }
            }

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
            int x = ArgumentHelper.getInt(args.get(0));
            int y = ArgumentHelper.getInt(args.get(1));
            if (ctx.getGame().getCurrentBackgroundImage() != null) {
                ctx.getGame().setBackgroundPosition(x, y);
            }
            return MethodResult.noReturn();
        }))
    );

    private static MethodResult getGraphicsAt(List<Value> args, MethodContext ctx, boolean walkParentContainers) {
        int posX = ArgumentHelper.getInt(args.get(0));
        int posY = ArgumentHelper.getInt(args.get(1));
        boolean onlyVisible = args.size() > 2 && ArgumentHelper.getBoolean(args.get(2));
        int minZ = args.size() > 4 ? ArgumentHelper.getInt(args.get(3)) : Integer.MIN_VALUE;
        int maxZ = args.size() > 4 ? ArgumentHelper.getInt(args.get(4)) : Integer.MAX_VALUE;
        boolean ignoreAlpha = args.size() > 5 && ArgumentHelper.getBoolean(args.get(5));

        var sceneCtx = ctx.getGame().getCurrentSceneContext();
        List<EngineVariable> drawList = new ArrayList<>();
        if (walkParentContainers) {
            drawList.addAll(sceneCtx.getGraphicsVariables().values());
        } else {
            for (EngineVariable v : sceneCtx.getVariables().values()) {
                if (v instanceof ImageVariable || v instanceof AnimoVariable) {
                    drawList.add(v);
                }
            }
        }

        drawList.sort((v1, v2) -> Integer.compare(getPriority(v2), getPriority(v1)));

        for (EngineVariable variable : drawList) {
            if (onlyVisible && !isVisible(variable)) continue;

            int z = getPriority(variable);
            if (z < minZ || z > maxZ) continue;

            if (!(variable instanceof CanvasBoundsProvider boundsProvider)) continue;
            CanvasRect rect = boundsProvider.getCanvasBounds();
            if (rect == null) continue;

            boolean containsPoint;
            if (ignoreAlpha) {
                containsPoint = rect.contains(posX, posY);
            } else {
                if (rect.contains(posX, posY)) {
                    Image image = variable instanceof CurrentImageProvider imageProvider
                            ? imageProvider.getCurrentImage()
                            : null;
                    int relativeX = posX - rect.left();
                    int relativeY = posY - rect.top();
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

        return MethodResult.returns(NullValue.INSTANCE);
    }

    @Override
    public String toString() {
        return "CanvasObserverVariable[" + name + "]";
    }
}
