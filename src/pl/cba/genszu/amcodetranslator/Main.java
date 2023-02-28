package pl.cba.genszu.amcodetranslator;

import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import pl.cba.genszu.amcodetranslator.antlr.*;

public class Main {

    public static void main(String[] args) {
		String debugInstr = "{@IF(\"ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAL\")'TRUE||ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAP\")'TRUE||ANNREKSIO^ISPLAYING(\"ZPLANSZY22\")'TRUE\",\"{@BREAK();}\",\"\");IREXPOSX^SET(-1);IREXPOSY^SET(-1);@IF(\"ANNREKSIO^GETEVENTNAME()'\"L\"\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAL\");}\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAP\");}\");BEHPLAYHENSFX^RUN();};";
    
		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(debugInstr));
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AidemMediaParser parser = new AidemMediaParser(tokens);
		ParseTree tree = parser.script();
		
		List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
		String[] tokensList = lexer.getTokenNames();
		String prettyTree = TreeUtils.toPrettyTree(tree, ruleNamesList);
		System.out.println(prettyTree);
		//ParseTreeAnalyzer.analyzeTree(tree, ruleNamesList, tokens, tokensList);
	}
}
