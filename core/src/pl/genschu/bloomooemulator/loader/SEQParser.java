package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SEQParser {
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
                String[] parts;
                if (line.contains("="))
                    parts = line.split("=");
                else {
                    parts = line.split(" "); // :ADD
                }
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
        String type = properties.get(objectName + ":TYPE");

        SequenceVariable.SequenceEvent event;
        String animoFile;
        String animoEvent;

        switch (type) {
            case "SEQUENCE":
                event = sequenceVariable.new SequenceEvent(objectName, sequenceVariable);
                break;
            case "SPEAKING":
                animoFile = properties.get(objectName + ":ANIMOFN");
                animoEvent = properties.get(objectName + ":PREFIX");
                String sequenceWav = properties.get(objectName + ":WAVFN");
                boolean starting = properties.get(objectName + ":STARTING").equals("TRUE");
                boolean ending = properties.get(objectName + ":ENDING").equals("TRUE");
                event = sequenceVariable.new SpeakingEvent(objectName, animoFile, animoEvent, sequenceWav, starting, ending, sequenceVariable);
                break;
            case "SIMPLE":
                animoFile = properties.get(objectName + ":FILENAME");
                animoEvent = properties.get(objectName + ":EVENT");
                event = sequenceVariable.new SimpleEvent(objectName, animoFile, animoEvent, sequenceVariable);
                break;
            default:
                Gdx.app.error("SEQParser", "Unknown event type: " + type);
                return;
        }

        String addTo = properties.get(objectName + ":ADD");

        if (addTo != null) {
            SequenceVariable.SequenceEvent sequenceEvent = sequenceVariable.getEventMap().get(addTo);
            if (sequenceEvent != null) {
                sequenceEvent.getEventMap().put(objectName, event);
            } else {
                Gdx.app.error("SEQParser", "Event not found: " + objectName);
            }
        }

        sequenceVariable.getEventMap().put(objectName, event);
    }
}
