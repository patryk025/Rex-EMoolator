// Generated from AidemMedia.g4 by ANTLR 4.9.2
package pl.genschu.bloomooemulator.interpreter.antlr;
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
	 * Enter a parse tree produced by {@link AidemMediaParser#varWithNumber}.
	 * @param ctx the parse tree
	 */
	void enterVarWithNumber(AidemMediaParser.VarWithNumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#varWithNumber}.
	 * @param ctx the parse tree
	 */
	void exitVarWithNumber(AidemMediaParser.VarWithNumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#loopCodeParam}.
	 * @param ctx the parse tree
	 */
	void enterLoopCodeParam(AidemMediaParser.LoopCodeParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#loopCodeParam}.
	 * @param ctx the parse tree
	 */
	void exitLoopCodeParam(AidemMediaParser.LoopCodeParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#conditionSimple}.
	 * @param ctx the parse tree
	 */
	void enterConditionSimple(AidemMediaParser.ConditionSimpleContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#conditionSimple}.
	 * @param ctx the parse tree
	 */
	void exitConditionSimple(AidemMediaParser.ConditionSimpleContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#ifTrue}.
	 * @param ctx the parse tree
	 */
	void enterIfTrue(AidemMediaParser.IfTrueContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#ifTrue}.
	 * @param ctx the parse tree
	 */
	void exitIfTrue(AidemMediaParser.IfTrueContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#ifFalse}.
	 * @param ctx the parse tree
	 */
	void enterIfFalse(AidemMediaParser.IfFalseContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#ifFalse}.
	 * @param ctx the parse tree
	 */
	void exitIfFalse(AidemMediaParser.IfFalseContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(AidemMediaParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(AidemMediaParser.CommentContext ctx);
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
	 * Enter a parse tree produced by {@link AidemMediaParser#iterator}.
	 * @param ctx the parse tree
	 */
	void enterIterator(AidemMediaParser.IteratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#iterator}.
	 * @param ctx the parse tree
	 */
	void exitIterator(AidemMediaParser.IteratorContext ctx);
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
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(AidemMediaParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(AidemMediaParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#floatNumber}.
	 * @param ctx the parse tree
	 */
	void enterFloatNumber(AidemMediaParser.FloatNumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#floatNumber}.
	 * @param ctx the parse tree
	 */
	void exitFloatNumber(AidemMediaParser.FloatNumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(AidemMediaParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(AidemMediaParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#arithmetic}.
	 * @param ctx the parse tree
	 */
	void enterArithmetic(AidemMediaParser.ArithmeticContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#arithmetic}.
	 * @param ctx the parse tree
	 */
	void exitArithmetic(AidemMediaParser.ArithmeticContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#logic}.
	 * @param ctx the parse tree
	 */
	void enterLogic(AidemMediaParser.LogicContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#logic}.
	 * @param ctx the parse tree
	 */
	void exitLogic(AidemMediaParser.LogicContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#compare}.
	 * @param ctx the parse tree
	 */
	void enterCompare(AidemMediaParser.CompareContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#compare}.
	 * @param ctx the parse tree
	 */
	void exitCompare(AidemMediaParser.CompareContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#bool}.
	 * @param ctx the parse tree
	 */
	void enterBool(AidemMediaParser.BoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#bool}.
	 * @param ctx the parse tree
	 */
	void exitBool(AidemMediaParser.BoolContext ctx);
}