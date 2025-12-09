// Generated from AidemMediaLexer.g4 by ANTLR 4.13.2
package pl.genschu.bloomooemulator.interpreter.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class AidemMediaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TRUE=1, FALSE=2, NUMBER=3, STRING=4, CODE_BLOCK=5, AT=6, CARET=7, LPAREN=8, 
		RPAREN=9, LBRACK=10, RBRACK=11, COMMA=12, SEMI=13, STAR=14, PLUS=15, MINUS=16, 
		PERC=17, PIPE=18, LBRACE=19, RBRACE=20, MISSING_CLOSE_QUOTE=21, IDENT=22, 
		LINE_COMMENT=23, WS=24;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"TRUE", "FALSE", "NUMBER", "STRING", "BALANCED_CONTENT", "CODE_BLOCK", 
			"AT", "CARET", "LPAREN", "RPAREN", "LBRACK", "RBRACK", "COMMA", "SEMI", 
			"STAR", "PLUS", "MINUS", "PERC", "PIPE", "LBRACE", "RBRACE", "MISSING_CLOSE_QUOTE", 
			"IDENT", "LINE_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'TRUE'", "'FALSE'", null, null, null, "'@'", "'^'", "'('", "')'", 
			"'['", "']'", "','", "';'", "'*'", "'+'", "'-'", "'%'", "'|'", "'{'", 
			"'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TRUE", "FALSE", "NUMBER", "STRING", "CODE_BLOCK", "AT", "CARET", 
			"LPAREN", "RPAREN", "LBRACK", "RBRACK", "COMMA", "SEMI", "STAR", "PLUS", 
			"MINUS", "PERC", "PIPE", "LBRACE", "RBRACE", "MISSING_CLOSE_QUOTE", "IDENT", 
			"LINE_COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public AidemMediaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "AidemMediaLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0018\u00ae\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017"+
		"\u0002\u0018\u0007\u0018\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0002\u0004\u0002@\b\u0002\u000b\u0002\f\u0002A\u0001"+
		"\u0002\u0001\u0002\u0004\u0002F\b\u0002\u000b\u0002\f\u0002G\u0003\u0002"+
		"J\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003O\b\u0003\n\u0003"+
		"\f\u0003R\t\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0005\u0004Y\b\u0004\n\u0004\f\u0004\\\t\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0005\u0005f\b\u0005\n\u0005\f\u0005i\t\u0005\u0001\u0005\u0005"+
		"\u0005l\b\u0005\n\u0005\f\u0005o\t\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b"+
		"\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0004"+
		"\u0015\u0094\b\u0015\u000b\u0015\f\u0015\u0095\u0001\u0016\u0001\u0016"+
		"\u0005\u0016\u009a\b\u0016\n\u0016\f\u0016\u009d\t\u0016\u0001\u0017\u0001"+
		"\u0017\u0005\u0017\u00a1\b\u0017\n\u0017\f\u0017\u00a4\t\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0018\u0004\u0018\u00a9\b\u0018\u000b\u0018\f\u0018"+
		"\u00aa\u0001\u0018\u0001\u0018\u0000\u0000\u0019\u0001\u0001\u0003\u0002"+
		"\u0005\u0003\u0007\u0004\t\u0000\u000b\u0005\r\u0006\u000f\u0007\u0011"+
		"\b\u0013\t\u0015\n\u0017\u000b\u0019\f\u001b\r\u001d\u000e\u001f\u000f"+
		"!\u0010#\u0011%\u0012\'\u0013)\u0014+\u0015-\u0016/\u00171\u0018\u0001"+
		"\u0000\t\u0001\u000009\u0002\u0000))+,\u0001\u0000))\u0001\u0000}}\u0003"+
		"\u0000\"\")),,\u0004\u0000$$AZ__az\u0006\u0000$$-9??AZ__az\u0001\u0000"+
		";;\u0003\u0000\t\n\r\r  \u00ba\u0000\u0001\u0001\u0000\u0000\u0000\u0000"+
		"\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000"+
		"\u0007\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000"+
		"\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001"+
		"\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000"+
		"\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000"+
		"\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/"+
		"\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u00013\u0001\u0000"+
		"\u0000\u0000\u00038\u0001\u0000\u0000\u0000\u0005?\u0001\u0000\u0000\u0000"+
		"\u0007K\u0001\u0000\u0000\u0000\tU\u0001\u0000\u0000\u0000\u000b_\u0001"+
		"\u0000\u0000\u0000\rs\u0001\u0000\u0000\u0000\u000fu\u0001\u0000\u0000"+
		"\u0000\u0011w\u0001\u0000\u0000\u0000\u0013y\u0001\u0000\u0000\u0000\u0015"+
		"{\u0001\u0000\u0000\u0000\u0017}\u0001\u0000\u0000\u0000\u0019\u007f\u0001"+
		"\u0000\u0000\u0000\u001b\u0081\u0001\u0000\u0000\u0000\u001d\u0083\u0001"+
		"\u0000\u0000\u0000\u001f\u0085\u0001\u0000\u0000\u0000!\u0087\u0001\u0000"+
		"\u0000\u0000#\u0089\u0001\u0000\u0000\u0000%\u008b\u0001\u0000\u0000\u0000"+
		"\'\u008d\u0001\u0000\u0000\u0000)\u008f\u0001\u0000\u0000\u0000+\u0091"+
		"\u0001\u0000\u0000\u0000-\u0097\u0001\u0000\u0000\u0000/\u009e\u0001\u0000"+
		"\u0000\u00001\u00a8\u0001\u0000\u0000\u000034\u0005T\u0000\u000045\u0005"+
		"R\u0000\u000056\u0005U\u0000\u000067\u0005E\u0000\u00007\u0002\u0001\u0000"+
		"\u0000\u000089\u0005F\u0000\u00009:\u0005A\u0000\u0000:;\u0005L\u0000"+
		"\u0000;<\u0005S\u0000\u0000<=\u0005E\u0000\u0000=\u0004\u0001\u0000\u0000"+
		"\u0000>@\u0007\u0000\u0000\u0000?>\u0001\u0000\u0000\u0000@A\u0001\u0000"+
		"\u0000\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BI\u0001"+
		"\u0000\u0000\u0000CE\u0005.\u0000\u0000DF\u0007\u0000\u0000\u0000ED\u0001"+
		"\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000"+
		"GH\u0001\u0000\u0000\u0000HJ\u0001\u0000\u0000\u0000IC\u0001\u0000\u0000"+
		"\u0000IJ\u0001\u0000\u0000\u0000J\u0006\u0001\u0000\u0000\u0000KP\u0005"+
		"\"\u0000\u0000LO\u0003\t\u0004\u0000MO\b\u0001\u0000\u0000NL\u0001\u0000"+
		"\u0000\u0000NM\u0001\u0000\u0000\u0000OR\u0001\u0000\u0000\u0000PN\u0001"+
		"\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QS\u0001\u0000\u0000\u0000"+
		"RP\u0001\u0000\u0000\u0000ST\u0005\"\u0000\u0000T\b\u0001\u0000\u0000"+
		"\u0000UZ\u0005(\u0000\u0000VY\u0003\t\u0004\u0000WY\b\u0002\u0000\u0000"+
		"XV\u0001\u0000\u0000\u0000XW\u0001\u0000\u0000\u0000Y\\\u0001\u0000\u0000"+
		"\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[]\u0001\u0000"+
		"\u0000\u0000\\Z\u0001\u0000\u0000\u0000]^\u0005)\u0000\u0000^\n\u0001"+
		"\u0000\u0000\u0000_`\u0005\"\u0000\u0000`a\u0005{\u0000\u0000am\u0001"+
		"\u0000\u0000\u0000bl\b\u0003\u0000\u0000cg\u0005{\u0000\u0000df\b\u0003"+
		"\u0000\u0000ed\u0001\u0000\u0000\u0000fi\u0001\u0000\u0000\u0000ge\u0001"+
		"\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hj\u0001\u0000\u0000\u0000"+
		"ig\u0001\u0000\u0000\u0000jl\u0005}\u0000\u0000kb\u0001\u0000\u0000\u0000"+
		"kc\u0001\u0000\u0000\u0000lo\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000"+
		"\u0000mn\u0001\u0000\u0000\u0000np\u0001\u0000\u0000\u0000om\u0001\u0000"+
		"\u0000\u0000pq\u0005}\u0000\u0000qr\u0005\"\u0000\u0000r\f\u0001\u0000"+
		"\u0000\u0000st\u0005@\u0000\u0000t\u000e\u0001\u0000\u0000\u0000uv\u0005"+
		"^\u0000\u0000v\u0010\u0001\u0000\u0000\u0000wx\u0005(\u0000\u0000x\u0012"+
		"\u0001\u0000\u0000\u0000yz\u0005)\u0000\u0000z\u0014\u0001\u0000\u0000"+
		"\u0000{|\u0005[\u0000\u0000|\u0016\u0001\u0000\u0000\u0000}~\u0005]\u0000"+
		"\u0000~\u0018\u0001\u0000\u0000\u0000\u007f\u0080\u0005,\u0000\u0000\u0080"+
		"\u001a\u0001\u0000\u0000\u0000\u0081\u0082\u0005;\u0000\u0000\u0082\u001c"+
		"\u0001\u0000\u0000\u0000\u0083\u0084\u0005*\u0000\u0000\u0084\u001e\u0001"+
		"\u0000\u0000\u0000\u0085\u0086\u0005+\u0000\u0000\u0086 \u0001\u0000\u0000"+
		"\u0000\u0087\u0088\u0005-\u0000\u0000\u0088\"\u0001\u0000\u0000\u0000"+
		"\u0089\u008a\u0005%\u0000\u0000\u008a$\u0001\u0000\u0000\u0000\u008b\u008c"+
		"\u0005|\u0000\u0000\u008c&\u0001\u0000\u0000\u0000\u008d\u008e\u0005{"+
		"\u0000\u0000\u008e(\u0001\u0000\u0000\u0000\u008f\u0090\u0005}\u0000\u0000"+
		"\u0090*\u0001\u0000\u0000\u0000\u0091\u0093\u0005\"\u0000\u0000\u0092"+
		"\u0094\b\u0004\u0000\u0000\u0093\u0092\u0001\u0000\u0000\u0000\u0094\u0095"+
		"\u0001\u0000\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000\u0095\u0096"+
		"\u0001\u0000\u0000\u0000\u0096,\u0001\u0000\u0000\u0000\u0097\u009b\u0007"+
		"\u0005\u0000\u0000\u0098\u009a\u0007\u0006\u0000\u0000\u0099\u0098\u0001"+
		"\u0000\u0000\u0000\u009a\u009d\u0001\u0000\u0000\u0000\u009b\u0099\u0001"+
		"\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000\u009c.\u0001\u0000"+
		"\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009e\u00a2\u0005!\u0000"+
		"\u0000\u009f\u00a1\b\u0007\u0000\u0000\u00a0\u009f\u0001\u0000\u0000\u0000"+
		"\u00a1\u00a4\u0001\u0000\u0000\u0000\u00a2\u00a0\u0001\u0000\u0000\u0000"+
		"\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3\u00a5\u0001\u0000\u0000\u0000"+
		"\u00a4\u00a2\u0001\u0000\u0000\u0000\u00a5\u00a6\u0006\u0017\u0000\u0000"+
		"\u00a60\u0001\u0000\u0000\u0000\u00a7\u00a9\u0007\b\u0000\u0000\u00a8"+
		"\u00a7\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa"+
		"\u00a8\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab"+
		"\u00ac\u0001\u0000\u0000\u0000\u00ac\u00ad\u0006\u0018\u0000\u0000\u00ad"+
		"2\u0001\u0000\u0000\u0000\u000f\u0000AGINPXZgkm\u0095\u009b\u00a2\u00aa"+
		"\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}