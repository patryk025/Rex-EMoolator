package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public  class SEQParser {
    public static void parseFile(SequenceVariable sequenceVariable) throws IOException {
        String filePath = FileUtils.resolveRelativePath(sequenceVariable);
        File file = new File(filePath);
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            StringBuilder content = new StringBuilder();
            boolean decypher = false;
            int offset = 0;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("{<")) {
                    String[] tmpParam = line.replace("{<", "").replace(">}", "").split(":");
                    offset = Integer.parseInt(tmpParam[1]);
                    if (tmpParam[0].equalsIgnoreCase("D")) offset *= -1;
                    decypher = true;
                } else {
                    content.append(line).append("\n");
                }
            }
            if (decypher) {
                Gdx.app.log("SEQParser", "Decyphering " + file.getName() + "...");
                parseString(ScriptDecypher.decode(content.toString(), offset), sequenceVariable);
            } else {
                parseString(content.toString(), sequenceVariable);
            }
        } catch (FileNotFoundException e) {
            Gdx.app.error("SEQParser", "File not found: " + file.getName());
        }
    }

    private static void parseString(String string, SequenceVariable sequenceVariable) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(string));
        String line;
        String currentObjectName = null;
        Map<String, String> properties = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("NAME =")) {
                if (currentObjectName != null) {
                    createSequenceVariable(currentObjectName, properties, sequenceVariable);
                }
                currentObjectName = line.split("=")[1].trim();
                properties = new HashMap<>();
            } else {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    properties.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        if (currentObjectName != null) {
            createSequenceVariable(currentObjectName, properties, sequenceVariable);
        }
    }

    private static void createSequenceVariable(String objectName, Map<String, String> properties, SequenceVariable sequenceVariable) {
       // TODO: implement
    }
}
