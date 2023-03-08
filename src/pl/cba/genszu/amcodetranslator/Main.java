package pl.cba.genszu.amcodetranslator;

import org.antlr.v4.runtime.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.utils.*;
import pl.cba.genszu.amcodetranslator.visitors.*;

public class Main {

    public static void main(String[] args) {
		String debugInstr = "{@IF(\"ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAL\")'TRUE||ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAP\")'TRUE||ANNREKSIO^ISPLAYING(\"ZPLANSZY22\")'TRUE\",\"{@BREAK();}\",\"\");IREXPOSX^SET(-1);IREXPOSY^SET(-1);@IF(\"ANNREKSIO^GETEVENTNAME()'\"L\"\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAL\");}\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAP\");}\");BEHPLAYHENSFX^RUN();};";
    	
		CodeBeautifier.beautify(debugInstr);
		//System.out.println(debugInstr);
		
		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(debugInstr));
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AidemMediaParser parser = new AidemMediaParser(tokens);
		//ParseTree tree = parser.script();
		AidemMediaParser.ScriptContext scriptContext = parser.script();
		AidemMediaCodeVisitor visitor = new AidemMediaCodeVisitor();
		visitor.visit(scriptContext);
		
		/*List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
		String[] tokensList = lexer.getTokenNames();
		String prettyTree = TreeUtils.toPrettyTree(tree, ruleNamesList);
		System.out.println(prettyTree);*/
		//ParseTreeAnalyzer.analyzeTree(tree, ruleNamesList, tokens, tokensList);
		
		/*FireFuncListener listener = new FireFuncListener();
        ParseTreeWalker.DEFAULT.walk(listener, tree);*/
	}
}
