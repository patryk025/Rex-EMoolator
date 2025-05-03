package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SoundVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        String separator;

        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#")) continue;

            if(line.contains(" = ")) separator = " = ";
            else separator = "=";

            if (line.startsWith("NAME" + separator)) {
                if (currentObjectName != null) {
                    createSequenceVariable(currentObjectName, properties, sequenceVariable);
                }
                currentObjectName = line.split(separator)[1].trim();
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
        if (type == null) {
            Gdx.app.error("SequenceVariable", "Missing TYPE property for " + objectName);
            return;
        }

        SequenceVariable.SequenceEvent event = null;
        switch (type) {
            case "SEQUENCE":
                String modeStr = properties.get(objectName + ":MODE");
                if (modeStr == null) {
                    Gdx.app.error("SequenceVariable", "Missing MODE property for SEQUENCE " + objectName);
                    return;
                }

                event = new SequenceVariable.SequenceEvent(objectName, SequenceVariable.EventType.SEQUENCE);
                try {
                    event.setMode(SequenceVariable.SequenceMode.valueOf(modeStr));
                } catch (IllegalArgumentException e) {
                    Gdx.app.error("SequenceVariable", "Invalid mode " + modeStr + " for SEQUENCE " + objectName);
                    return;
                }
                break;

            case "SPEAKING":
                String animoFile = properties.get(objectName + ":ANIMOFN");
                String prefix = properties.get(objectName + ":PREFIX");
                String wavFile = properties.get(objectName + ":WAVFN");
                boolean starting = "TRUE".equals(properties.get(objectName + ":STARTING"));
                boolean ending = "TRUE".equals(properties.get(objectName + ":ENDING"));

                if (animoFile == null || prefix == null || wavFile == null) {
                    Gdx.app.error("SequenceVariable", "Missing required properties for SPEAKING " + objectName);
                    return;
                }

                event = new SequenceVariable.SequenceEvent(objectName, SequenceVariable.EventType.SPEAKING);
                event.setPrefix(prefix);
                event.setHasStartAnimation(starting);
                event.setHasEndAnimation(ending);

                // find existing animo
                Variable existingAnimo = findVariable(animoFile, sequenceVariable);
                if (existingAnimo instanceof AnimoVariable) {
                    event.setAnimation((AnimoVariable) existingAnimo);
                } else {
                    // create new animation
                    event.setAnimation(new AnimoVariable(animoFile, sequenceVariable.getContext()));
                    event.getAnimation().setAttribute("FILENAME", new Attribute("STRING", animoFile));
                    sequenceVariable.getContext().setVariable(animoFile, event.getAnimation());
                    event.getAnimation().init();
                }

                sequenceVariable.getAnimosInSequence().add(event.getAnimation());

                // create audio
                Variable existingSound = findVariable(wavFile, sequenceVariable);
                if (existingSound instanceof SoundVariable) {
                    event.setSound((SoundVariable) existingSound);
                }
                else {
                    event.setSound(new SoundVariable(wavFile, sequenceVariable.getContext()));
                    event.getSound().setAttribute("FILENAME", new Attribute("STRING", "$WAVS\\" + wavFile));
                    event.getSound().init();
                }
                sequenceVariable.getContext().setVariable(wavFile, event.getSound());
                break;

            case "SIMPLE":
                String simpleAnimoFile = properties.get(objectName + ":FILENAME");
                String simpleAnimoEvent = properties.get(objectName + ":EVENT");

                if (simpleAnimoFile == null || simpleAnimoEvent == null) {
                    Gdx.app.error("SequenceVariable", "Missing required properties for SIMPLE " + objectName);
                    return;
                }

                event = new SequenceVariable.SequenceEvent(objectName, SequenceVariable.EventType.SIMPLE);
                event.setPrefix(simpleAnimoEvent);

                // find existing animo
                Variable existingSimpleAnimo = findVariable(simpleAnimoFile, sequenceVariable);
                if (existingSimpleAnimo instanceof AnimoVariable) {
                    event.setAnimation((AnimoVariable) existingSimpleAnimo);
                } else {
                    // create new animation
                    event.setAnimation(new AnimoVariable(simpleAnimoFile, sequenceVariable.getContext()));
                    event.getAnimation().setAttribute("FILENAME", new Attribute("STRING", simpleAnimoFile));
                    sequenceVariable.getContext().setVariable(simpleAnimoFile, event.getAnimation());
                    event.getAnimation().init();
                }
                sequenceVariable.getAnimosInSequence().add(event.getAnimation());
                break;

            default:
                Gdx.app.error("SequenceVariable", "Unknown event type: " + type + " for " + objectName);
        }

        String addTo = properties.get(objectName + ":ADD");
        if (addTo != null) {
            SequenceVariable.SequenceEvent parentEvent = sequenceVariable.getEventsByName().get(addTo);
            if (parentEvent != null) {
                if (event != null) {
                    event.setParent(parentEvent);
                }
                parentEvent.getSubEvents().add(event);
            } else {
                Gdx.app.error("SequenceVariable", "Parent event not found: " + addTo + " for " + objectName);
            }
        } else {
            // add it to main events
            sequenceVariable.getEvents().add(event);
        }

        // Dodanie do mapy eventów
        sequenceVariable.getEventsByName().put(objectName, event);

        // parse SEQEVENT properties
        for(String key : properties.keySet()) {
            if(key.startsWith("SEQEVENT:")) {
                String name = key.substring("SEQEVENT:".length());
                String indexer = properties.get(key);
                int index = sequenceVariable.getPARAMS_CHARACTER_SET().indexOf(indexer.charAt(0)); // Index mapping (0-based), use only first letter
                if (index >= 0) {
                    sequenceVariable.getParametersMapping().put(name, index);
                } else {
                    Gdx.app.error("SequenceVariable", "Invalid SEQEVENT indexer: " + indexer + " for " + name);
                }
            }
        }
    }

    private static Variable findVariable(String name, SequenceVariable sequenceVariable) {
        if(name.toLowerCase().endsWith(".ann")) {
            Gdx.app.log("SequenceVariable", "Looking for ANIMO variable with filename: " + name);
            List<Variable> graphicsVariables = new ArrayList<>(sequenceVariable.getContext().getGraphicsVariables().values());

            for (Variable var : graphicsVariables) {
                if (var instanceof AnimoVariable) {
                    AnimoVariable animoVar = (AnimoVariable) var;
                    if (animoVar.getAttribute("FILENAME").getValue().equals(name)) {
                        Gdx.app.log("SequenceVariable", "Found ANIMO variable with filename: " + name);
                        animoVar.setAttribute("TOCANVAS", new Attribute("BOOL", "TRUE"));
                        animoVar.setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
                        return animoVar;
                    }
                }
            }

            Gdx.app.log("SequenceVariable", "Could not find ANIMO variable with filename: " + name);
        }
        else if (name.toLowerCase().endsWith(".wav")) {
            Gdx.app.log("SequenceVariable", "Looking for SOUND variable with filename: " + name);
            List<Variable> soundVariables = new ArrayList<>(sequenceVariable.getContext().getSoundVariables().values());

            for (Variable var : soundVariables) {
                if (var instanceof SoundVariable) {
                    SoundVariable soundVar = (SoundVariable) var;
                    if (soundVar.getAttribute("FILENAME").getValue().equals(name)) {
                        Gdx.app.log("SequenceVariable", "Found SOUND variable with filename: " + name);
                        return soundVar;
                    }
                }
            }

            Gdx.app.log("SequenceVariable", "Could not find SOUND variable with filename: " + name);
        }
        else {
            Gdx.app.log("SequenceVariable", "Looking for variable with name: " + name);
            Variable variable = sequenceVariable.getContext().getVariable(name);
            if (variable != null) {
                Gdx.app.log("SequenceVariable", "Found variable " + variable.getType() + " with name: " + name);
                return variable;
            }
            Gdx.app.log("SequenceVariable", "Could not find variable with name: " + name);
        }
        return null;
    }
}
