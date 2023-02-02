package pl.cba.genszu.amcodetranslator.antlr;

// Generated from e:\gramatykaAM\AidemMedia.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AidemMediaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, LPAREN=5, RPAREN=6, VARREF=7, STARTCODE=8, 
		STOPCODE=9, STARTEXPR=10, STOPEXPR=11, SLASH=12, LSS=13, LEQ=14, GEQ=15, 
		GTR=16, EQU=17, NEQ=18, ENDINSTR=19, FIREFUNC=20, DIGIT=21, NUMBER=22, 
		ITERATOR=23, LITERAL=24, FLOAT=25, ARITHMETIC=26, LOGIC=27, BOOLEAN=28, 
		SELF=29, SEPARATOR=30, STRUCTFIELD=31, QUOTEMARK=32, STRREF=33, WS=34, 
		CHAR=35;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "LPAREN", "RPAREN", "VARREF", "STARTCODE", 
			"STOPCODE", "STARTEXPR", "STOPEXPR", "SLASH", "LSS", "LEQ", "GEQ", "GTR", 
			"EQU", "NEQ", "ENDINSTR", "FIREFUNC", "DIGIT", "NUMBER", "ITERATOR", 
			"LITERAL", "FLOAT", "ARITHMETIC", "LOGIC", "BOOLEAN", "SELF", "SEPARATOR", 
			"STRUCTFIELD", "QUOTEMARK", "STRREF", "WS", "CHAR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@IF'", "'@LOOP'", "'@WHILE'", "'@'", "'('", "')'", "'$'", null, 
			null, "'['", "']'", "'\\'", "'<'", null, null, "'>'", null, null, "';'", 
			"'^'", null, null, "'_I_'", null, null, null, null, null, "'THIS'", "','", 
			"'|'", "'\"'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "LPAREN", "RPAREN", "VARREF", "STARTCODE", 
			"STOPCODE", "STARTEXPR", "STOPEXPR", "SLASH", "LSS", "LEQ", "GEQ", "GTR", 
			"EQU", "NEQ", "ENDINSTR", "FIREFUNC", "DIGIT", "NUMBER", "ITERATOR", 
			"LITERAL", "FLOAT", "ARITHMETIC", "LOGIC", "BOOLEAN", "SELF", "SEPARATOR", 
			"STRUCTFIELD", "QUOTEMARK", "STRREF", "WS", "CHAR"
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
	public String getGrammarFileName() { return "AidemMedia.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2%\u00de\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\5\td\n\t\3"+
		"\t\3\t\3\n\3\n\5\nj\n\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17"+
		"\3\17\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\25"+
		"\3\25\3\26\3\26\3\27\5\27\u0088\n\27\3\27\6\27\u008b\n\27\r\27\16\27\u008c"+
		"\3\30\3\30\3\30\3\30\3\31\5\31\u0094\n\31\3\31\6\31\u0097\n\31\r\31\16"+
		"\31\u0098\6\31\u009b\n\31\r\31\16\31\u009c\3\32\6\32\u00a0\n\32\r\32\16"+
		"\32\u00a1\3\32\3\32\7\32\u00a6\n\32\f\32\16\32\u00a9\13\32\3\32\3\32\6"+
		"\32\u00ad\n\32\r\32\16\32\u00ae\5\32\u00b1\n\32\3\33\3\33\3\33\3\33\5"+
		"\33\u00b7\n\33\3\34\3\34\3\34\3\34\5\34\u00bd\n\34\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\3\35\3\35\3\35\5\35\u00c8\n\35\3\36\3\36\3\36\3\36\3\36\3\37"+
		"\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3#\3$\3$\3$\3$\2\2%\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24"+
		"\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%\3\2"+
		"\t\4\2))aa\5\2\62;C\\aa\4\2--//\3\2C]\4\2\61\61BB\5\2\13\f\17\17\"\"\4"+
		"\2))^^\2\u00ec\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3"+
		"\2\2\2\2G\3\2\2\2\3I\3\2\2\2\5M\3\2\2\2\7S\3\2\2\2\tZ\3\2\2\2\13\\\3\2"+
		"\2\2\r^\3\2\2\2\17`\3\2\2\2\21c\3\2\2\2\23g\3\2\2\2\25k\3\2\2\2\27m\3"+
		"\2\2\2\31o\3\2\2\2\33q\3\2\2\2\35s\3\2\2\2\37v\3\2\2\2!y\3\2\2\2#{\3\2"+
		"\2\2%}\3\2\2\2\'\u0080\3\2\2\2)\u0082\3\2\2\2+\u0084\3\2\2\2-\u0087\3"+
		"\2\2\2/\u008e\3\2\2\2\61\u009a\3\2\2\2\63\u00b0\3\2\2\2\65\u00b6\3\2\2"+
		"\2\67\u00bc\3\2\2\29\u00c7\3\2\2\2;\u00c9\3\2\2\2=\u00ce\3\2\2\2?\u00d0"+
		"\3\2\2\2A\u00d2\3\2\2\2C\u00d4\3\2\2\2E\u00d6\3\2\2\2G\u00da\3\2\2\2I"+
		"J\7B\2\2JK\7K\2\2KL\7H\2\2L\4\3\2\2\2MN\7B\2\2NO\7N\2\2OP\7Q\2\2PQ\7Q"+
		"\2\2QR\7R\2\2R\6\3\2\2\2ST\7B\2\2TU\7Y\2\2UV\7J\2\2VW\7K\2\2WX\7N\2\2"+
		"XY\7G\2\2Y\b\3\2\2\2Z[\7B\2\2[\n\3\2\2\2\\]\7*\2\2]\f\3\2\2\2^_\7+\2\2"+
		"_\16\3\2\2\2`a\7&\2\2a\20\3\2\2\2bd\5A!\2cb\3\2\2\2cd\3\2\2\2de\3\2\2"+
		"\2ef\7}\2\2f\22\3\2\2\2gi\7\177\2\2hj\5A!\2ih\3\2\2\2ij\3\2\2\2j\24\3"+
		"\2\2\2kl\7]\2\2l\26\3\2\2\2mn\7_\2\2n\30\3\2\2\2op\7^\2\2p\32\3\2\2\2"+
		"qr\7>\2\2r\34\3\2\2\2st\7>\2\2tu\t\2\2\2u\36\3\2\2\2vw\7@\2\2wx\t\2\2"+
		"\2x \3\2\2\2yz\7@\2\2z\"\3\2\2\2{|\t\2\2\2|$\3\2\2\2}~\7#\2\2~\177\t\2"+
		"\2\2\177&\3\2\2\2\u0080\u0081\7=\2\2\u0081(\3\2\2\2\u0082\u0083\7`\2\2"+
		"\u0083*\3\2\2\2\u0084\u0085\4\62;\2\u0085,\3\2\2\2\u0086\u0088\5\65\33"+
		"\2\u0087\u0086\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u008a\3\2\2\2\u0089\u008b"+
		"\5+\26\2\u008a\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008a\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d.\3\2\2\2\u008e\u008f\7a\2\2\u008f\u0090\7K\2\2\u0090"+
		"\u0091\7a\2\2\u0091\60\3\2\2\2\u0092\u0094\7\60\2\2\u0093\u0092\3\2\2"+
		"\2\u0093\u0094\3\2\2\2\u0094\u0096\3\2\2\2\u0095\u0097\t\3\2\2\u0096\u0095"+
		"\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099"+
		"\u009b\3\2\2\2\u009a\u0093\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009a\3\2"+
		"\2\2\u009c\u009d\3\2\2\2\u009d\62\3\2\2\2\u009e\u00a0\5-\27\2\u009f\u009e"+
		"\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2"+
		"\u00a3\3\2\2\2\u00a3\u00a7\7\60\2\2\u00a4\u00a6\5-\27\2\u00a5\u00a4\3"+
		"\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8"+
		"\u00b1\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa\u00ac\7\60\2\2\u00ab\u00ad\5"+
		"-\27\2\u00ac\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae"+
		"\u00af\3\2\2\2\u00af\u00b1\3\2\2\2\u00b0\u009f\3\2\2\2\u00b0\u00aa\3\2"+
		"\2\2\u00b1\64\3\2\2\2\u00b2\u00b7\t\4\2\2\u00b3\u00b4\7,\2\2\u00b4\u00b7"+
		"\n\5\2\2\u00b5\u00b7\t\6\2\2\u00b6\u00b2\3\2\2\2\u00b6\u00b3\3\2\2\2\u00b6"+
		"\u00b5\3\2\2\2\u00b7\66\3\2\2\2\u00b8\u00b9\7(\2\2\u00b9\u00bd\7(\2\2"+
		"\u00ba\u00bb\7~\2\2\u00bb\u00bd\7~\2\2\u00bc\u00b8\3\2\2\2\u00bc\u00ba"+
		"\3\2\2\2\u00bd8\3\2\2\2\u00be\u00bf\7V\2\2\u00bf\u00c0\7T\2\2\u00c0\u00c1"+
		"\7W\2\2\u00c1\u00c8\7G\2\2\u00c2\u00c3\7H\2\2\u00c3\u00c4\7C\2\2\u00c4"+
		"\u00c5\7N\2\2\u00c5\u00c6\7U\2\2\u00c6\u00c8\7G\2\2\u00c7\u00be\3\2\2"+
		"\2\u00c7\u00c2\3\2\2\2\u00c8:\3\2\2\2\u00c9\u00ca\7V\2\2\u00ca\u00cb\7"+
		"J\2\2\u00cb\u00cc\7K\2\2\u00cc\u00cd\7U\2\2\u00cd<\3\2\2\2\u00ce\u00cf"+
		"\7.\2\2\u00cf>\3\2\2\2\u00d0\u00d1\7~\2\2\u00d1@\3\2\2\2\u00d2\u00d3\7"+
		"$\2\2\u00d3B\3\2\2\2\u00d4\u00d5\7,\2\2\u00d5D\3\2\2\2\u00d6\u00d7\t\7"+
		"\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\b#\2\2\u00d9F\3\2\2\2\u00da\u00db"+
		"\7)\2\2\u00db\u00dc\n\b\2\2\u00dc\u00dd\7)\2\2\u00ddH\3\2\2\2\21\2ci\u0087"+
		"\u008c\u0093\u0098\u009c\u00a1\u00a7\u00ae\u00b0\u00b6\u00bc\u00c7\3\2"+
		"\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}