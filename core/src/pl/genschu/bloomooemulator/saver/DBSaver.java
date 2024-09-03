package pl.genschu.bloomooemulator.saver;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.types.DatabaseVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DBSaver {
    public static void saveDatabase(DatabaseVariable variable, String filePath) {
        filePath = FileUtils.resolveRelativePath(variable, filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            List<List<String>> data = variable.getData();
            if (data == null) {
                Gdx.app.error("DBSaver", "No data to save in the database.");
                return;
            }

            for (List<String> row : data) {
                String line = String.join("|", row);
                writer.write(line);
                writer.newLine();
            }
            Gdx.app.log("DBSaver", "Database saved successfully to " + filePath);
        } catch (IOException e) {
            Gdx.app.error("DBSaver", "Error while saving DATABASE: " + e.getMessage(), e);
        }
    }
}
