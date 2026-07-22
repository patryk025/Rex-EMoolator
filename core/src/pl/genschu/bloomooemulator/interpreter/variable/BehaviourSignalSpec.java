package pl.genschu.bloomooemulator.interpreter.variable;

import java.util.List;

/** Parsed BEHAVIOUR reference used by ONxxx attributes in CNV files. */
public record BehaviourSignalSpec(String behaviourName, List<String> declaredArguments) {
    public BehaviourSignalSpec {
        behaviourName = behaviourName != null ? behaviourName.trim() : "";
        declaredArguments = declaredArguments == null ? List.of() : List.copyOf(declaredArguments);
    }

    public static BehaviourSignalSpec parse(String specification) {
        return parse(specification, SignalArgumentTokenizer.Dialect.PIKLIB8);
    }

    public static BehaviourSignalSpec parse(String specification,
                                            SignalArgumentTokenizer.Dialect dialect) {
        String text = specification != null ? specification.trim() : "";
        int openingParenthesis = text.indexOf('(');
        if (openingParenthesis < 0 || !text.endsWith(")")) {
            return new BehaviourSignalSpec(text, List.of());
        }

        String name = text.substring(0, openingParenthesis);
        String argumentText = text.substring(openingParenthesis + 1, text.length() - 1);
        return new BehaviourSignalSpec(name, SignalArgumentTokenizer.tokenize(argumentText, dialect));
    }
}
