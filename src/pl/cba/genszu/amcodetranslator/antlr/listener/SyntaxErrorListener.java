package pl.cba.genszu.amcodetranslator.antlr.listener;

import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import pl.cba.genszu.amcodetranslator.antlr.objects.SyntaxError;

public class SyntaxErrorListener extends BaseErrorListener
{
    private final List<SyntaxError> syntaxErrors = new ArrayList<>();

    public SyntaxErrorListener()
    {
    }

    public List<SyntaxError> getSyntaxErrors()
    {
        return syntaxErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {
        syntaxErrors.add(new SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
		//throw new ParseCancellationException();
	}

    @Override
    public String toString()
    {
        return Utils.join(syntaxErrors.iterator(), "\n");
    }
}
