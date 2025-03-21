package pl.genschu.bloomooemulator.interpreter.antlr;

import com.badlogic.gdx.Gdx;
import org.antlr.v4.runtime.*;
import java.util.ArrayList;
import java.util.List;

public class AidemMediaErrorListener extends BaseErrorListener {
    private final List<ParseError> errors = new ArrayList<>();

    public static class ParseError {
        private final int line;
        private final int charPosition;
        private final String message;
        private final Token offendingToken;

        public ParseError(int line, int charPosition, String message, Token offendingToken) {
            this.line = line;
            this.charPosition = charPosition;
            this.message = message;
            this.offendingToken = offendingToken;
        }

        public int getLine() { return line; }
        public int getCharPosition() { return charPosition; }
        public String getMessage() { return message; }
        public Token getOffendingToken() { return offendingToken; }

        @Override
        public String toString() {
            return "Line " + line + ":" + charPosition + " - " + message + " at token '" + offendingToken.getText() + "'";
        }
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        Token token = (Token) offendingSymbol;
        errors.add(new ParseError(line, charPositionInLine, msg, token));
        Gdx.app.log("ParserError", "Syntax error: " + msg + " at " + line + ":" + charPositionInLine);
    }

    public List<ParseError> getErrors() {
        return new ArrayList<>(errors);
    }

    public void clearErrors() {
        errors.clear();
    }
}