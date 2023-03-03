package pl.cba.genszu.amcodetranslator;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import pl.cba.genszu.amcodetranslator.antlr.AidemMediaLexer;
import pl.cba.genszu.amcodetranslator.antlr.AidemMediaParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GrammarTester {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new File("script.txt"));

            PrintWriter pw = new PrintWriter("errors.txt");

            int codes = 0;
            int good_codes = 0;
            int error_codes = 0;

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();

                codes++;

                AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(line));

                CommonTokenStream tokens = new CommonTokenStream(lexer);
                AidemMediaParser parser = new AidemMediaParser(tokens);
                SyntaxErrorListener listener = new SyntaxErrorListener();
                parser.addErrorListener(listener);
                lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
                parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
                parser.script();

                System.out.print(line + " -> ");
                if(listener.getSyntaxErrors().size()>0) {
                    System.out.println("błąd");
                    pw.write(line+"\n");
                    pw.flush();
                    error_codes++;
                }
                else {
                    System.out.println("OK");
                    good_codes++;
                }
            }

            System.out.println();
            System.out.println("Kodów: "+codes);
            System.out.println("Poprawnie przeparsowanych: "+good_codes);
            System.out.println("Niepoprawnie przeparsowanych: "+error_codes);

            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

		/*
		List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
		String prettyTree = TreeUtils.toPrettyTree(tree, ruleNamesList);
		System.out.println(prettyTree);
		 */
    }
}
