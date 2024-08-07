package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SoundVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

public class SoundLoader {
    public static void loadSound(SoundVariable variable) {
        try {
            Attribute filename = variable.getAttribute("FILENAME");
            String filenameString = filename.getValue().toString();
            Variable value = variable.getContext().getVariable(filenameString);
            if(value != null) {
                filenameString = value.getValue().toString();
            }
            if (!filenameString.startsWith("$")) {
                filename.setValue("$WAVS\\" + filenameString);
            }
            String filePath = FileUtils.resolveRelativePath(variable);
            FileHandle soundFileHandle = Gdx.files.absolute(filePath);

            variable.setSound(Gdx.audio.newMusic(soundFileHandle));
        } catch (Exception e) {
            Gdx.app.error("SoundLoader", "Error while loading sound: " + e.getMessage());
        }
    }
}
