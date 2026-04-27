package pl.genschu.bloomooemulator.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface InternalMutable {
    /**
     * Marks a field or type as intentionally mutable.
     * Must not be exposed outside its owning abstraction.
     */
}
