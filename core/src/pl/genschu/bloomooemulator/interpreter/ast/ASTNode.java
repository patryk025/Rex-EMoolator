package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

/**
 * Base sealed interface for all AST nodes.
 *
 * Key differences from the old system:
 * - AST nodes are pure data structures (no execute/evaluate methods)
 * - All nodes are immutable (records)
 * - All nodes carry source location information
 * - Interpretation is done by a separate ASTInterpreter class
 *
 * This separation allows:
 * - Easier testing (AST can be constructed and inspected without execution)
 * - Multiple interpreters (optimizing, debugging, etc.)
 * - AST transformations and optimizations
 * - Serialization of AST
 */
public sealed interface ASTNode permits
    // Literals
    LiteralNode,
    VariableNode,

    // Expressions
    ArithmeticNode,
    ComparisonNode,
    LogicalNode,
    MethodCallNode,
    PointerDerefNode,
    StructAccessNode,

    // Statements
    BlockNode,
    IfNode,
    LoopNode,
    WhileNode,
    BreakNode,
    OneBreakNode,
    ReturnNode,
    VarDefNode,
    ConvNode {

    /**
     * Returns the source location of this node.
     */
    SourceLocation location();
}
