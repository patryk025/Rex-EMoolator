package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.filters.Filter;
import pl.genschu.bloomooemulator.engine.render.RenderOrder;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.util.*;

/**
 * ImageVariable represents a static image in the game.
 * Uses mutable ImageState for position, visibility, and loaded texture.
 */
public record ImageVariable(
    String name,
    @InternalMutable ImageState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    /**
     * Mutable state for image rendering and collision.
     */
    public static final class ImageState {
        public Image image;
        public int posX = 0;
        public int posY = 0;
        public float opacity = 255f;
        public Box2D rect = new Box2D(0, 0, 0, 0);
        public Box2D clippingRect = null;
        public int priority = 0;
        public long renderOrder = RenderOrder.next();
        public boolean visible = true;
        public boolean monitorCollision = false;
        public boolean monitorCollisionAlpha = false;
        public String filename = "";
        public AlphaMaskBinding alphaMask = null;
        public final List<Filter> filters = new ArrayList<>();

        public ImageState() {}

        public ImageState copy() {
            ImageState copy = new ImageState();
            copy.image = this.image;
            copy.posX = this.posX;
            copy.posY = this.posY;
            copy.opacity = this.opacity;
            copy.rect = new Box2D(rect.getXLeft(), rect.getYBottom(), rect.getXRight(), rect.getYTop());
            copy.clippingRect = this.clippingRect != null
                    ? new Box2D(clippingRect.getXLeft(), clippingRect.getYBottom(), clippingRect.getXRight(), clippingRect.getYTop())
                    : null;
            copy.priority = this.priority;
            copy.renderOrder = this.renderOrder;
            copy.visible = this.visible;
            copy.monitorCollision = this.monitorCollision;
            copy.monitorCollisionAlpha = this.monitorCollisionAlpha;
            copy.filename = this.filename;
            copy.alphaMask = this.alphaMask;
            copy.filters.addAll(this.filters);
            return copy;
        }

        public void updateRect() {
            if (image == null) return;
            rect.setXLeft(posX);
            rect.setYTop(posY);
            rect.setXRight(posX + image.width);
            rect.setYBottom(posY - image.height);
        }

        public void dispose() {
            if (image != null && image.isLoaded() && image.getImageTexture() != null) {
                image.getImageTexture().dispose();
            }
        }
    }

    public ImageVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new ImageState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public ImageVariable(String name) {
        this(name, new ImageState(), Map.of());
    }

    public ImageVariable(String name, String filename) {
        this(name);
        state.filename = filename != null ? filename : "";
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.IMAGE;
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
        return new ImageVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new ImageVariable(newName, state.copy(), new HashMap<>(signals));
    }

    // ========================================
    // INITIALIZABLE
    // ========================================

    @Override
    public void init(Context context) {
        // Read attributes from context
        String filename = context.attributes().get(name, "FILENAME");
        if (filename != null) {
            state.filename = filename;
        }

        String visibleAttr = context.attributes().get(name, "VISIBLE");
        if (visibleAttr != null) {
            state.visible = visibleAttr.equalsIgnoreCase("TRUE");
        }

        String priorityAttr = context.attributes().get(name, "PRIORITY");
        if (priorityAttr != null) {
            try { state.priority = Integer.parseInt(priorityAttr); } catch (NumberFormatException ignored) {}
        }

        String monitorAttr = context.attributes().get(name, "MONITORCOLLISION");
        if (monitorAttr != null) {
            state.monitorCollision = monitorAttr.equalsIgnoreCase("TRUE");
        }

        String monitorAlphaAttr = context.attributes().get(name, "MONITORCOLLISIONALPHA");
        if (monitorAlphaAttr != null) {
            state.monitorCollisionAlpha = monitorAlphaAttr.equalsIgnoreCase("TRUE");
        }

        // Load image
        if (!state.filename.isEmpty()) {
            loadImage(context);
        }
    }

    public void load(pl.genschu.bloomooemulator.engine.Game game) {
        String filename = state.filename;
        if (!filename.toUpperCase().endsWith(".IMG")) {
            filename = filename + ".IMG";
            state.filename = filename;
        }
        try {
            String path = FileUtils.resolveRelativePath(game, filename);
            ImageLoader.loadImage(this, path);
            state.updateRect();
        } catch (Exception e) {
            Gdx.app.error("ImageVariable", "Error loading IMAGE: " + filename, e);
        }
    }

    private void loadImage(Context context) {
        String filename = state.filename;
        if (!filename.toUpperCase().endsWith(".IMG")) {
            filename = filename + ".IMG";
            state.filename = filename;
        }
        try {
            String path = FileUtils.resolveRelativePath(context.getGame(), filename);
            ImageLoader.loadImage(this, path);
            state.updateRect();
        } catch (Exception e) {
            Gdx.app.error("ImageVariable", "Error loading IMAGE: " + filename, e);
        }
    }

    // ========================================
    // CONVENIENT ACCESSORS (for managers)
    // ========================================

    public Image getImage() { return state.image; }
    public int getPosX() { return state.posX; }
    public int getPosY() { return state.posY; }
    public float getOpacity() { return state.opacity; }
    public Box2D getRect() { return state.rect; }
    public Box2D getClippingRect() { return state.clippingRect; }
    public boolean isVisible() { return state.visible; }
    public long getRenderOrder() { return state.renderOrder; }
    public List<Filter> getFilters() { return state.filters; }
    public boolean hasFilters() { return !state.filters.isEmpty(); }
    public void addFilter(Filter filter) { state.filters.add(filter); }
    public void removeFilter(Filter filter) { state.filters.remove(filter); }
    public AlphaMaskBinding getAlphaMask() { return state.alphaMask; }

    public record AlphaMaskBinding(ImageVariable mask, int posX, int posY) {}

    public boolean isAt(int x, int y) {
        return x >= state.rect.getXLeft() && x <= state.rect.getXRight()
            && y >= state.rect.getYTop() && y <= state.rect.getYBottom();
    }

    public int getAlpha(int x, int y) {
        if (state.image == null || state.image.getImageTexture() == null) return 0;
        Pixmap pixmap = state.image.getImageTexture().getTextureData().consumePixmap();
        Color color = new Color(pixmap.getPixel(x, y));
        return (int) (color.a * 255f);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETALPHA", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int posX = ArgumentHelper.getInt(args.get(0));
            int posY = ArgumentHelper.getInt(args.get(1));
            return MethodResult.returns(new IntValue(img.getAlpha(posX, posY)));
        })),

        Map.entry("GETCENTERX", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int cx = (img.state.rect.getXLeft() + img.state.rect.getXRight()) / 2;
            return MethodResult.returns(new IntValue(cx));
        })),

        Map.entry("GETCENTERY", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int cy = (img.state.rect.getYTop() + img.state.rect.getYBottom()) / 2;
            return MethodResult.returns(new IntValue(cy));
        })),

        Map.entry("GETHEIGHT", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int h = img.state.image != null ? img.state.image.height : 0;
            return MethodResult.returns(new IntValue(h));
        })),

        Map.entry("GETPIXEL", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int posX = ArgumentHelper.getInt(args.get(0));
            int posY = ArgumentHelper.getInt(args.get(1));
            if (img.state.image == null || img.state.image.getImageTexture() == null) {
                return MethodResult.returns(new IntValue(0));
            }
            TextureData textureData = img.state.image.getImageTexture().getTextureData();
            if (!textureData.isPrepared()) textureData.prepare();
            Pixmap pixmap = textureData.consumePixmap();
            Color color = new Color(pixmap.getPixel(posX, posY));
            int red = (int) (color.r * 255f);
            int green = (int) (color.g * 255f);
            int blue = (int) (color.b * 255f);
            int rgb;
            if (img.state.image.colorDepth == 16) {
                rgb = (red & 0xF8) << 8 | (green & 0xFC) << 3 | (blue & 0xF8) >> 3;
            } else if (img.state.image.colorDepth == 15) {
                rgb = (red & 0xF8) << 7 | (green & 0xF8) << 2 | (blue & 0xF8) >> 3;
            } else {
                rgb = 255;
            }
            return MethodResult.returns(new IntValue(rgb));
        })),

        Map.entry("GETPOSITIONX", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(new IntValue(((ImageVariable) self).state.posX))
        )),

        Map.entry("GETPOSITIONY", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(new IntValue(((ImageVariable) self).state.posY))
        )),

        Map.entry("GETWIDTH", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int w = img.state.image != null ? img.state.image.width : 0;
            return MethodResult.returns(new IntValue(w));
        })),

        Map.entry("HIDE", MethodSpec.of((self, args, ctx) -> {
            ((ImageVariable) self).state.visible = false;
            return MethodResult.noReturn();
        })),

        Map.entry("ISAT", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int posX = ArgumentHelper.getInt(args.get(0));
            int posY = ArgumentHelper.getInt(args.get(1));
            return MethodResult.returns(BoolValue.of(img.isAt(posX, posY)));
        })),

        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            String path = ArgumentHelper.getString(args.get(0));
            img.state.filename = path;
            String resolved = FileUtils.resolveRelativePath(ctx.getGame(), path);
            ImageLoader.loadImage(img, resolved);
            img.state.updateRect();
            img.state.visible = true;
            return MethodResult.noReturn();
        })),

        Map.entry("MERGEALPHA", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int posX = ArgumentHelper.getInt(args.get(0));
            int posY = ArgumentHelper.getInt(args.get(1));
            String maskName = ArgumentHelper.getString(args.get(2));
            Variable maskVar = ctx.getVariable(maskName);
            if (!(maskVar instanceof ImageVariable mask) || mask.state.image == null) {
                return MethodResult.noReturn();
            }
            img.state.alphaMask = new AlphaMaskBinding(mask, posX, posY);
            return MethodResult.noReturn();
        })),

        Map.entry("MONITORCOLLISION", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            img.state.monitorCollision = true;
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().addCollisionMonitor(img);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("MOVE", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int dx = ArgumentHelper.getInt(args.get(0));
            int dy = ArgumentHelper.getInt(args.get(1));
            img.state.posX += dx;
            img.state.posY += dy;
            img.state.updateRect();
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().markCollisionDirty(img);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEMONITORCOLLISION", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            img.state.monitorCollision = false;
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().removeCollisionMonitor(img);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("MONITORCOLLISIONALPHA", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            img.state.monitorCollisionAlpha = true;
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEMONITORCOLLISIONALPHA", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            img.state.monitorCollisionAlpha = false;
            return MethodResult.noReturn();
        })),

        Map.entry("SETCLIPPING", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            int xL = ArgumentHelper.getInt(args.get(0));
            int yB = ArgumentHelper.getInt(args.get(1));
            int xR = ArgumentHelper.getInt(args.get(2));
            int yT = ArgumentHelper.getInt(args.get(3));
            img.state.clippingRect = new Box2D(xL, yB, xR, yT);
            return MethodResult.noReturn();
        })),

        Map.entry("SETOPACITY", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            img.state.opacity = ArgumentHelper.getInt(args.get(0)) / 255.0f;
            return MethodResult.noReturn();
        })),

        Map.entry("SETPOSITION", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            img.state.posX = ArgumentHelper.getInt(args.get(0));
            img.state.posY = ArgumentHelper.getInt(args.get(1));
            img.state.updateRect();
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().markCollisionDirty(img);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETPRIORITY", MethodSpec.of((self, args, ctx) -> {
            ImageVariable img = (ImageVariable) self;
            img.state.priority = ArgumentHelper.getInt(args.get(0));
            img.state.renderOrder = RenderOrder.next();
            return MethodResult.noReturn();
        })),

        Map.entry("SHOW", MethodSpec.of((self, args, ctx) -> {
            ((ImageVariable) self).state.visible = true;
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "ImageVariable[" + name + ", file=" + state.filename + ", visible=" + state.visible + "]";
    }
}
