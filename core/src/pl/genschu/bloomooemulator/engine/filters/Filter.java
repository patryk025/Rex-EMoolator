package pl.genschu.bloomooemulator.engine.filters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.HashMap;
import java.util.Map;

public abstract class Filter {
    protected Map<String, Object> properties = new HashMap<>();

    public abstract void apply(Batch batch, Texture texture, float x, float y, float width, float height);

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }
}
