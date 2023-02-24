package pl.cba.genszu.amcodetranslator;

import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class ParseTreeAnalyzer
{
	private static int intend = 0;
	private static List<Token> ruleTokens;
	
	private static void tab(int intend) {
		for(int i = 0; i < intend; i++) System.out.print("\t");
	}
	
	public static void analyzeTree(Tree tree, List<String> ruleNames, CommonTokenStream tokenStream, String[] tokensList) {
		if (tree.getChildCount() == 0) {
			for(Token token : ruleTokens) {
				tab(intend);
				System.out.println(tokensList[token.getType()]);
			}
			return;
		}
        tab(intend);
        intend++;
        String s = Trees.getNodeText(tree, ruleNames);
        System.out.println("Blok " + s + ":");
		ParserRuleContext parserRule = (ParserRuleContext) tree;
		//CommonTokenStream tokenStream = tree.
		Token start = parserRule.getStart();
        Token stop = parserRule.getStop();
		ruleTokens = tokenStream.getTokens(start.getTokenIndex(), stop.getTokenIndex());
        /*ListTokenSource tokenSource = new ListTokenSource(ruleTokens);
        CommonTokenStream commonTokenStream = new CommonTokenStream(tokenSource);
        commonTokenStream.fill();*/
        //rewriter = new TokenStreamRewriter(commonTokenStream);
    
        for (int i = 0; i < tree.getChildCount(); i++) {
            analyzeTree(tree.getChild(i), ruleNames, tokenStream, tokensList);
        }
        intend--;
        tab(intend);
	}
}
