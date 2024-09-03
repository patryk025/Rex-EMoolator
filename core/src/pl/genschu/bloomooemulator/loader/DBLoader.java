package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.types.DatabaseVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBLoader {
    public static void loadDatabase(DatabaseVariable variable, String filePath) {
        filePath = FileUtils.resolveRelativePath(variable, filePath);

        List<List<String>> data = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.startsWith("#")) continue;
                if(line.isEmpty()) continue;

                String[] values = line.split("\\|");

                // make sure that values size matches column size
                if(values.length < 1) {
                    Gdx.app.log("DBLoader", "Skipping line: " + line + " (missing values)");
                }
                if(variable.getColumns() == null) {
                    Gdx.app.error("DBLoader", "Missing model in database. Aborting...");
                    return;
                }
                /*if(variable.getColumns().getFields().size() != values.length) {
                    Gdx.app.log("DBLoader", "Skipping line: " + line + " (wrong number of columns)");
                }*/

                List<String> row = new ArrayList<>(Arrays.asList(values));
                data.add(row);
            }
            reader.close();

            variable.setData(data);
        } catch (Exception e) {
            Gdx.app.error("DBLoader", "Error while loading DATABASE: " + e.getMessage(), e);
        }
    }
}
