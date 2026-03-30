package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SequenceVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SoundVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * v2 SEQ loader used by SequenceVariable.init().
 */
public final class SequenceLoader {
    private SequenceLoader() {}

    public static void load(SequenceVariable sequenceVariable, Context context) throws IOException {
        String filename = sequenceVariable.getFilename();
        if (filename == null || filename.isBlank()) {
            return;
        }

        String resolvedPath = FileUtils.resolveRelativePath(context.getGame(), filename);
        File file = new File(resolvedPath);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder content = new StringBuilder();
            boolean decipher = false;
            int offset = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("{<")) {
                    String[] tmpParam = line.replace("{<", "").replace(">}", "").split(":");
                    offset = Integer.parseInt(tmpParam[1]);
                    if (tmpParam[0].equalsIgnoreCase("D")) {
                        offset *= -1;
                    }
                    decipher = true;
                } else {
                    content.append(line).append("\n");
                }
            }

            if (decipher) {
                Gdx.app.log("SequenceLoader", "Deciphering " + file.getName() + "...");
                parseString(ScriptDecypher.decode(content.toString(), offset), sequenceVariable, context);
            } else {
                parseString(content.toString(), sequenceVariable, context);
            }
        } catch (FileNotFoundException e) {
            Gdx.app.error("SequenceLoader", "File not found: " + resolvedPath);
            throw e;
        }
    }

    private static void parseString(String string, SequenceVariable sequenceVariable, Context context) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(string))) {
            String line;
            String currentObjectName = null;
            Map<String, String> properties = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }

                String separator = line.contains(" = ") ? " = " : "=";

                if (line.startsWith("NAME" + separator)) {
                    if (currentObjectName != null) {
                        createSequenceEvent(currentObjectName, properties, sequenceVariable, context);
                    }
                    currentObjectName = line.split(separator, 2)[1].trim();
                    properties = new HashMap<>();
                } else {
                    String[] parts = line.contains("=") ? line.split("=", 2) : line.split(" ", 2);
                    if (parts.length == 2) {
                        properties.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }

            if (currentObjectName != null) {
                createSequenceEvent(currentObjectName, properties, sequenceVariable, context);
            }
        }
    }

    private static void createSequenceEvent(String objectName, Map<String, String> properties,
                                            SequenceVariable sequenceVariable, Context context) {
        String type = properties.get(objectName + ":TYPE");
        if (type == null) {
            Gdx.app.error("SequenceLoader", "Missing TYPE property for " + objectName);
            return;
        }

        SequenceVariable.SequenceEvent event = switch (type) {
            case "SEQUENCE" -> createSequenceGroup(objectName, properties);
            case "SPEAKING" -> createSpeakingEvent(objectName, properties, sequenceVariable, context);
            case "SIMPLE" -> createSimpleEvent(objectName, properties, sequenceVariable, context);
            default -> {
                Gdx.app.error("SequenceLoader", "Unknown event type: " + type + " for " + objectName);
                yield null;
            }
        };

        if (event == null) {
            return;
        }

        String addTo = properties.get(objectName + ":ADD");
        if (addTo != null) {
            SequenceVariable.SequenceEvent parentEvent = sequenceVariable.getEventByName(addTo);
            if (parentEvent != null) {
                parentEvent.addSubEvent(event);
            } else {
                Gdx.app.error("SequenceLoader", "Parent event not found: " + addTo + " for " + objectName);
            }
        } else {
            sequenceVariable.addEvent(event);
        }

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith("SEQEVENT:")) {
                continue;
            }
            String name = key.substring("SEQEVENT:".length());
            String indexer = entry.getValue();
            int index = SequenceVariable.PARAMS_CHARACTER_SET.indexOf(indexer.charAt(0));
            if (index >= 0) {
                sequenceVariable.getParametersMapping().put(name, index);
            } else {
                Gdx.app.error("SequenceLoader", "Invalid SEQEVENT indexer: " + indexer + " for " + name);
            }
        }
    }

    private static SequenceVariable.SequenceEvent createSequenceGroup(String objectName, Map<String, String> properties) {
        String modeStr = properties.get(objectName + ":MODE");
        if (modeStr == null) {
            Gdx.app.error("SequenceLoader", "Missing MODE property for SEQUENCE " + objectName);
            return null;
        }

        try {
            SequenceVariable.SequenceMode mode = SequenceVariable.SequenceMode.valueOf(modeStr);
            return new SequenceVariable.SequenceEvent(
                objectName,
                SequenceVariable.EventType.SEQUENCE,
                mode,
                null,
                false,
                false
            );
        } catch (IllegalArgumentException e) {
            Gdx.app.error("SequenceLoader", "Invalid mode " + modeStr + " for SEQUENCE " + objectName);
            return null;
        }
    }

    private static SequenceVariable.SequenceEvent createSpeakingEvent(String objectName, Map<String, String> properties,
                                                                      SequenceVariable sequenceVariable, Context context) {
        String animoFile = properties.get(objectName + ":ANIMOFN");
        String prefix = properties.get(objectName + ":PREFIX");
        String wavFile = properties.get(objectName + ":WAVFN");
        boolean starting = "TRUE".equalsIgnoreCase(properties.get(objectName + ":STARTING"));
        boolean ending = "TRUE".equalsIgnoreCase(properties.get(objectName + ":ENDING"));

        if (animoFile == null || prefix == null || wavFile == null) {
            Gdx.app.error("SequenceLoader", "Missing required properties for SPEAKING " + objectName);
            return null;
        }

        SequenceVariable.SequenceEvent event = new SequenceVariable.SequenceEvent(
            objectName,
            SequenceVariable.EventType.SPEAKING,
            SequenceVariable.SequenceMode.SEQUENCE,
            prefix,
            starting,
            ending
        );

        AnimoVariable animo = resolveOrCreateAnimo(animoFile, context);
        if (animo != null) {
            event.setAnimationName(animo.name());
            sequenceVariable.getAnimosInSequence().add(animo.name());
        }

        SoundVariable sound = resolveOrCreateSound(wavFile, context);
        if (sound != null) {
            event.setSoundName(sound.name());
        }

        return event;
    }

    private static SequenceVariable.SequenceEvent createSimpleEvent(String objectName, Map<String, String> properties,
                                                                    SequenceVariable sequenceVariable, Context context) {
        String animoFile = properties.get(objectName + ":FILENAME");
        String animoEvent = properties.get(objectName + ":EVENT");

        if (animoFile == null || animoEvent == null) {
            Gdx.app.error("SequenceLoader", "Missing required properties for SIMPLE " + objectName);
            return null;
        }

        SequenceVariable.SequenceEvent event = new SequenceVariable.SequenceEvent(
            objectName,
            SequenceVariable.EventType.SIMPLE,
            SequenceVariable.SequenceMode.SEQUENCE,
            animoEvent,
            false,
            false
        );

        AnimoVariable animo = resolveOrCreateAnimo(animoFile, context);
        if (animo != null) {
            event.setAnimationName(animo.name());
            sequenceVariable.getAnimosInSequence().add(animo.name());
        }

        return event;
    }

    private static AnimoVariable resolveOrCreateAnimo(String reference, Context context) {
        Variable existing = findVariable(reference, context);
        if (existing instanceof AnimoVariable animo) {
            return animo;
        }

        String variableName = nextAvailableName(context, deriveVariableName(reference, "SEQ_ANIMO"));
        AnimoVariable created = new AnimoVariable(variableName, reference);
        context.setVariable(variableName, created);
        created.init(context);
        return (AnimoVariable) context.getVariable(variableName);
    }

    private static SoundVariable resolveOrCreateSound(String reference, Context context) {
        Variable existing = findVariable(reference, context);
        if (existing instanceof SoundVariable sound) {
            return sound;
        }

        String variableName = nextAvailableName(context, deriveVariableName(reference, "SEQ_SOUND"));
        SoundVariable created = new SoundVariable(variableName, reference);
        context.setVariable(variableName, created);
        created.init(context);
        return (SoundVariable) context.getVariable(variableName);
    }

    private static Variable findVariable(String name, Context context) {
        if (name.toLowerCase().endsWith(".ann")) {
            for (Variable variable : context.getGraphicsVariables().values()) {
                if (variable instanceof AnimoVariable animo) {
                    if (name.equalsIgnoreCase(animo.getFilename()) || name.equalsIgnoreCase(animo.name())) {
                        return animo;
                    }
                }
            }
            return null;
        }

        if (name.toLowerCase().endsWith(".wav")) {
            for (Variable variable : context.getSoundVariables().values()) {
                if (variable instanceof SoundVariable sound) {
                    if (name.equalsIgnoreCase(sound.state().filename) || name.equalsIgnoreCase(sound.name())) {
                        return sound;
                    }
                }
            }
            return null;
        }

        return context.getVariable(name);
    }

    private static String deriveVariableName(String reference, String fallbackPrefix) {
        String base = new File(reference).getName().replace('.', '_').toUpperCase();
        if (!base.isBlank()) {
            return base;
        }
        return fallbackPrefix + "_" + Math.abs(reference.hashCode());
    }

    private static String nextAvailableName(Context context, String baseName) {
        if (!context.hasVariable(baseName)) {
            return baseName;
        }

        int suffix = 1;
        while (context.hasVariable(baseName + "_" + suffix)) {
            suffix++;
        }
        return baseName + "_" + suffix;
    }
}
