package pl.genschu.bloomooemulator.loader;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.Interpreter;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaLexer;
import org.antlr.v4.runtime.CharStreams;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.genschu.bloomooemulator.interpreter.ast.ASTBuilderVisitor;
import pl.genschu.bloomooemulator.interpreter.ast.Node;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import com.badlogic.gdx.Gdx;

import java.io.*;
import java.util.*;

public class CNVParser {
    public void parseFile(File plik, Context context) throws IOException {
        try {
            FileReader reader = new FileReader(plik);
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
                Gdx.app.log("CNVParser", "Decyphering " + plik.getName() + "...");
                parseString(ScriptDecypher.decode(content.toString(), offset), context);
            } else {
                parseString(content.toString(), context);
            }
        } catch (FileNotFoundException e) {
            Gdx.app.error("CNVParser", "File not found: " + plik.getName());
        }
    }

    public void parseString(String string, Context context) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(string));
        String line;
        Map<String, Map<String, String>> objects = new HashMap<>();
        String currentObjectName = null;
        Map<String, String> currentObject = null;
        
        String separator;
        
        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#")) continue;

            if(line.contains(" = ")) separator = " = ";
            else separator = "=";
            if (line.startsWith("OBJECT"+separator)) {
                if (currentObjectName != null) {
                    objects.put(currentObjectName, currentObject);
                }
                currentObjectName = line.split(separator)[1];
                currentObject = new HashMap<>();
            } else if (currentObjectName != null) {
                String[] parts = line.split(separator, 2);
                if (parts.length == 2) {
                    currentObject.put(parts[0], parts[1]);
                }
            }
        }
        if (currentObjectName != null) {
            objects.put(currentObjectName, currentObject);
        }

        for (Map.Entry<String, Map<String, String>> entry : objects.entrySet()) {
            String objectName = entry.getKey();
            Map<String, String> properties = entry.getValue();
            processObject(objectName, properties, context);
        }
    }

    private void processObject(String objectName, Map<String, String> properties, Context context) {
        String type = properties.get(objectName + ":TYPE");
        if (type == null) {
            Gdx.app.error("CNVParser", "Missing TYPE for object " + objectName);
            return;
        }

        Gdx.app.log("CNVParser", "Processing object " + objectName + " of type " + type);

        Object value;

        if(type.equals("BEHAVIOUR"))
            value = properties.get(objectName + ":CODE");
        else
            value = properties.get(objectName + ":VALUE");

        try {
            Variable variable = VariableFactory.createVariable(type, objectName, value, context);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                if (
                       !property.getKey().equals(objectName + ":TYPE")
                ) {
                    if(!property.getKey().startsWith(objectName + ":ON")) {
                        variable.setAttribute(property.getKey().replace(objectName + ":", ""), property.getValue());
                    }
                    else {
                        variable.setSignal(property.getKey().replace(objectName + ":", ""), new Signal() {
                            @Override
                            public void execute(Object argument) {
                                Interpreter intepreter = processEventCode(property.getValue(), context);
                                intepreter.interpret();
                            }
                        });
                    }
                }
            }

            context.setVariable(objectName, variable);
        } catch (IllegalArgumentException e) {
            Gdx.app.error("CNVParser", "Failed to create variable " + objectName + ": " + e.getMessage());
        }
    }
    
    private Interpreter processEventCode(String code, Context context) {
        AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(code));
		AidemMediaParser parser = new AidemMediaParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.script();

		ASTBuilderVisitor astBuilder = new ASTBuilderVisitor(context);
		Node astRoot = astBuilder.visit(tree);

		return new Interpreter(astRoot, context);
    }
}