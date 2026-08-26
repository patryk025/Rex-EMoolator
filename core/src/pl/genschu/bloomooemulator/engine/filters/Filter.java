package pl.genschu.bloomooemulator.engine.filters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import pl.genschu.bloomooemulator.geometry.coordinates.OpenGlRect;

import java.util.HashMap;
import java.util.Map;

public abstract class Filter {
    protected Map<String, Object> properties = new HashMap<>();

    public abstract void apply(Batch batch, Texture texture, OpenGlRect destination);

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }
}
