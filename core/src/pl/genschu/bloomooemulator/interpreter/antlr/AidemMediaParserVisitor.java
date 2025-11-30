// Generated from AidemMediaParser.g4 by ANTLR 4.13.2
package pl.genschu.bloomooemulator.interpreter.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AidemMediaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AidemMediaParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(AidemMediaParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(AidemMediaParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#specialCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecialCall(AidemMediaParser.SpecialCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#methodCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(AidemMediaParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#objectName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectName(AidemMediaParser.ObjectNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#objectReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectReference(AidemMediaParser.ObjectReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#argListOpt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgListOpt(AidemMediaParser.ArgListOptContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg(AidemMediaParser.ArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(AidemMediaParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#addExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(AidemMediaParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#mulExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExpr(AidemMediaParser.MulExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#unaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(AidemMediaParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(AidemMediaParser.PrimaryContext ctx);
}