// Generated from /storage/emulated/0/AppProjects/AidemMediaInterpreterAntlr/src/pl/cba/genszu/amcodetranslator/AidemMedia.g4 by ANTLR 4.9.2
package pl.cba.genszu.amcodetranslator.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AidemMediaParser}.
 */
public interface AidemMediaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#ifInstr}.
	 * @param ctx the parse tree
	 */
	void enterIfInstr(AidemMediaParser.IfInstrContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#ifInstr}.
	 * @param ctx the parse tree
	 */
	void exitIfInstr(AidemMediaParser.IfInstrContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#loopInstr}.
	 * @param ctx the parse tree
	 */
	void enterLoopInstr(AidemMediaParser.LoopInstrContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#loopInstr}.
	 * @param ctx the parse tree
	 */
	void exitLoopInstr(AidemMediaParser.LoopInstrContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#whileInstr}.
	 * @param ctx the parse tree
	 */
	void enterWhileInstr(AidemMediaParser.WhileInstrContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#whileInstr}.
	 * @param ctx the parse tree
	 */
	void exitWhileInstr(AidemMediaParser.WhileInstrContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#functionFire}.
	 * @param ctx the parse tree
	 */
	void enterFunctionFire(AidemMediaParser.FunctionFireContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#functionFire}.
	 * @param ctx the parse tree
	 */
	void exitFunctionFire(AidemMediaParser.FunctionFireContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#codeBlock}.
	 * @param ctx the parse tree
	 */
	void enterCodeBlock(AidemMediaParser.CodeBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#codeBlock}.
	 * @param ctx the parse tree
	 */
	void exitCodeBlock(AidemMediaParser.CodeBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(AidemMediaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(AidemMediaParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(AidemMediaParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(AidemMediaParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(AidemMediaParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(AidemMediaParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(AidemMediaParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(AidemMediaParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#conditionPart}.
	 * @param ctx the parse tree
	 */
	void enterConditionPart(AidemMediaParser.ConditionPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#conditionPart}.
	 * @param ctx the parse tree
	 */
	void exitConditionPart(AidemMediaParser.ConditionPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#behFire}.
	 * @param ctx the parse tree
	 */
	void enterBehFire(AidemMediaParser.BehFireContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#behFire}.
	 * @param ctx the parse tree
	 */
	void exitBehFire(AidemMediaParser.BehFireContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#modulo}.
	 * @param ctx the parse tree
	 */
	void enterModulo(AidemMediaParser.ModuloContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#modulo}.
	 * @param ctx the parse tree
	 */
	void exitModulo(AidemMediaParser.ModuloContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(AidemMediaParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(AidemMediaParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterInstr(AidemMediaParser.InstrContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitInstr(AidemMediaParser.InstrContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#stringRef}.
	 * @param ctx the parse tree
	 */
	void enterStringRef(AidemMediaParser.StringRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#stringRef}.
	 * @param ctx the parse tree
	 */
	void exitStringRef(AidemMediaParser.StringRefContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#struct}.
	 * @param ctx the parse tree
	 */
	void enterStruct(AidemMediaParser.StructContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#struct}.
	 * @param ctx the parse tree
	 */
	void exitStruct(AidemMediaParser.StructContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(AidemMediaParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(AidemMediaParser.VariableContext ctx);
}