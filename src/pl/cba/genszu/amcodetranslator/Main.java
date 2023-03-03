package pl.cba.genszu.amcodetranslator;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import pl.cba.genszu.amcodetranslator.antlr.AidemMediaLexer;
import pl.cba.genszu.amcodetranslator.antlr.AidemMediaParser;
import pl.cba.genszu.amcodetranslator.visitors.IfInstrVisitor;

public class Main {
	public static void main(String[] args) {
		String line = "{@IF(\"KEYBOARD^ISKEYDOWN(\"LEFT\")\",\"!_\",\"0\",\"BFITMP33\",\"\");};";
		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(line));
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		AidemMediaParser parser = new AidemMediaParser(tokens);
		AidemMediaParser.ScriptContext scriptContext = parser.script();
		IfInstrVisitor visitor = new IfInstrVisitor();
		visitor.visit(scriptContext);
	}

}
