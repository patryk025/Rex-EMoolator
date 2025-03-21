package pl.genschu.bloomooemulator.interpreter.antlr;

import com.badlogic.gdx.Gdx;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;

public class AidemMediaErrorStrategy extends DefaultErrorStrategy {
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        TokenStream tokens = recognizer.getInputStream();

        while (tokens.LA(1) != AidemMediaParser.ENDINSTR
                && tokens.LA(1) != AidemMediaParser.STOPCODE
                && tokens.LA(1) != Token.EOF) {
            tokens.consume();
        }
        if (tokens.LA(1) == AidemMediaParser.ENDINSTR) {
            tokens.consume();
        }
    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        return super.recoverInline(recognizer);
    }
}
