package pl.cba.genszu.amcodetranslator.interpreter.util;

import pl.cba.genszu.amcodetranslator.antlr.AidemMediaParser;
import pl.cba.genszu.amcodetranslator.visitors.AidemMediaCodeVisitor;

import java.util.List;

public class ParamHelper {
    public static String getValueFromParam(AidemMediaCodeVisitor visitor, AidemMediaParser.ParamContext param) {
        if (param.string() != null)
        {
            List<AidemMediaParser.FunctionFireContext> functionFire = param.string().functionFire();
            if (functionFire != null)
            {
                if (!functionFire.isEmpty())
                {
                    for (AidemMediaParser.FunctionFireContext ctx_functionFire : functionFire)
                    {
                        visitor.visitFunctionFire(ctx_functionFire);
                        //TODO: jakoś pobrać wynik
                    }
                }
                else
                    return param.string().getText();
            }
            else
            {
                return param.string().getText();
            }
        }
        else if (param.number() != null)
        {
            return param.number().getText();
        }
        else if (param.functionFire() != null)
        {
            visitor.visitFunctionFire(param.functionFire());
            //TODO: jakoś pobrać wynik
        }
        else if (param.literal() != null)
        {
            return param.literal().getText();
        }
        return "";
    }
}
