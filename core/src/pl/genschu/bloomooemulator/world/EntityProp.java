package pl.genschu.bloomooemulator.world;

public class EntityProp {
    private String name;
    private String value;

    public EntityProp(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
