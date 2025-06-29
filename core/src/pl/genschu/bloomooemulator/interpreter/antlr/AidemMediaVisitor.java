// Generated from AidemMedia.g4 by ANTLR 4.13.2
package pl.genschu.bloomooemulator.interpreter.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AidemMediaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AidemMediaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#ifInstr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfInstr(AidemMediaParser.IfInstrContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#loopInstr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopInstr(AidemMediaParser.LoopInstrContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#whileInstr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileInstr(AidemMediaParser.WhileInstrContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#functionFire}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionFire(AidemMediaParser.FunctionFireContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#codeBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCodeBlock(AidemMediaParser.CodeBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#varWithNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarWithNumber(AidemMediaParser.VarWithNumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#loopCodeParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopCodeParam(AidemMediaParser.LoopCodeParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#conditionSimple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionSimple(AidemMediaParser.ConditionSimpleContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#ifTrue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfTrue(AidemMediaParser.IfTrueContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#ifFalse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfFalse(AidemMediaParser.IfFalseContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(AidemMediaParser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(AidemMediaParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(AidemMediaParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(AidemMediaParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(AidemMediaParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#conditionPart}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionPart(AidemMediaParser.ConditionPartContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#behFire}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBehFire(AidemMediaParser.BehFireContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#modulo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModulo(AidemMediaParser.ModuloContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#iterator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterator(AidemMediaParser.IteratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(AidemMediaParser.StringContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstr(AidemMediaParser.InstrContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#stringRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringRef(AidemMediaParser.StringRefContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#struct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct(AidemMediaParser.StructContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(AidemMediaParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(AidemMediaParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#floatNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatNumber(AidemMediaParser.FloatNumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(AidemMediaParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#arithmetic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmetic(AidemMediaParser.ArithmeticContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#logic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic(AidemMediaParser.LogicContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#compare}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompare(AidemMediaParser.CompareContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#bool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(AidemMediaParser.BoolContext ctx);
}