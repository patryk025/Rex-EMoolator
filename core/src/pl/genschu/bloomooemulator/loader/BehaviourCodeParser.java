package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.ast.BlockNode;
import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.parser.CodeParser;

import java.util.List;

/**
 * Helper class for parsing BEHAVIOUR code to v2 ASTNode.
 *
 * Handles errors gracefully by returning empty block on failure.
 */
public class BehaviourCodeParser {

    /**
     * Parses BEHAVIOUR code string to AST.
     *
     * @param code The code to parse
     * @param name Name of the behaviour (for error messages)
     * @return Parsed ASTNode (BlockNode)
     */
    public static ASTNode parseCode(String code, String name) {
        if (code == null || code.trim().isEmpty()) {
            Gdx.app.log("BehaviourCodeParser", "Empty code for " + name);
            return createEmptyBlock();
        }

        try {
            Gdx.app.log("BehaviourCodeParser", "Parsing code for " + name + " (length=" + code.length() + ")");
            ASTNode ast = CodeParser.parseCode(code);

            if (ast == null) {
                Gdx.app.error("BehaviourCodeParser", "Parser returned null for " + name);
                return createEmptyBlock();
            }

            Gdx.app.log("BehaviourCodeParser", "Successfully parsed " + name);
            return ast;
        } catch (Exception e) {
            Gdx.app.error("BehaviourCodeParser", "Failed to parse " + name + ": " + e.getMessage(), e);
            return createEmptyBlock();
        }
    }

    /**
     * Creates an empty BlockNode placeholder.
     */
    private static ASTNode createEmptyBlock() {
        return new BlockNode(List.of(), SourceLocation.UNKNOWN);
    }
}
