// Generated from /storage/emulated/0/AppProjects/AidemMediaInterpreterAntlr/src/pl/cba/genszu/amcodetranslator/AidemMedia.g4 by ANTLR 4.9.2
package pl.cba.genszu.amcodetranslator.antlr;
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
		T__0=1, T__1=2, T__2=3, COMMENT=4, LPAREN=5, RPAREN=6, VARREF=7, STARTCODE=8, 
		STOPCODE=9, STARTEXPR=10, STOPEXPR=11, SLASH=12, LSS=13, LEQ=14, GEQ=15, 
		GTR=16, EQU=17, NEQ=18, ENDINSTR=19, FIREFUNC=20, DIV=21, MOD=22, NUMBER=23, 
		FLOAT=24, DOT=25, ITERATOR=26, LITERAL=27, ARITHMETIC=28, LOGIC=29, BOOLEAN=30, 
		SEPARATOR=31, STRUCTFIELD=32, QUOTEMARK=33, STRREF=34, WS=35, CHAR=36;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "COMMENT", "LPAREN", "RPAREN", "VARREF", "STARTCODE", 
			"STOPCODE", "STARTEXPR", "STOPEXPR", "SLASH", "LSS", "LEQ", "GEQ", "GTR", 
			"EQU", "NEQ", "ENDINSTR", "FIREFUNC", "DIV", "MOD", "DIGIT", "LETTER", 
			"NUMBER", "FLOAT", "DOT", "ITERATOR", "LITERAL", "ARITHMETIC", "LOGIC", 
			"BOOLEAN", "SEPARATOR", "STRUCTFIELD", "QUOTEMARK", "STRREF", "WS", "CHAR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@IF'", "'@LOOP'", "'@WHILE'", null, "'('", "')'", "'$'", null, 
			null, "'['", "']'", "'\\'", "'<'", null, null, "'>'", null, null, "';'", 
			"'^'", "'@'", "'%'", null, null, "'.'", "'_I_'", null, null, null, null, 
			"','", "'|'", "'\"'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "COMMENT", "LPAREN", "RPAREN", "VARREF", "STARTCODE", 
			"STOPCODE", "STARTEXPR", "STOPEXPR", "SLASH", "LSS", "LEQ", "GEQ", "GTR", 
			"EQU", "NEQ", "ENDINSTR", "FIREFUNC", "DIV", "MOD", "NUMBER", "FLOAT", 
			"DOT", "ITERATOR", "LITERAL", "ARITHMETIC", "LOGIC", "BOOLEAN", "SEPARATOR", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2&\u011e\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\3\2\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\7\5c\n\5\f\5\16\5f"+
		"\13\5\3\5\7\5i\n\5\f\5\16\5l\13\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\5\tu\n\t"+
		"\3\t\3\t\3\n\3\n\5\n{\n\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17"+
		"\3\17\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\25"+
		"\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\6\32\u009f\n\32\r\32"+
		"\16\32\u00a0\3\33\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3"+
		"\36\3\36\7\36\u00b0\n\36\f\36\16\36\u00b3\13\36\3\36\3\36\5\36\u00b7\n"+
		"\36\3\36\3\36\3\36\3\36\3\36\3\36\7\36\u00bf\n\36\f\36\16\36\u00c2\13"+
		"\36\3\36\7\36\u00c5\n\36\f\36\16\36\u00c8\13\36\3\36\3\36\6\36\u00cc\n"+
		"\36\r\36\16\36\u00cd\3\36\3\36\6\36\u00d2\n\36\r\36\16\36\u00d3\5\36\u00d6"+
		"\n\36\3\36\3\36\3\36\3\36\3\36\7\36\u00dd\n\36\f\36\16\36\u00e0\13\36"+
		"\3\36\7\36\u00e3\n\36\f\36\16\36\u00e6\13\36\5\36\u00e8\n\36\5\36\u00ea"+
		"\n\36\3\36\3\36\3\36\5\36\u00ef\n\36\3\36\3\36\3\36\5\36\u00f4\n\36\5"+
		"\36\u00f6\n\36\3\37\3\37\3\37\3\37\5\37\u00fc\n\37\3 \3 \3 \3 \5 \u0102"+
		"\n \3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u010d\n!\3\"\3\"\3#\3#\3$\3$\3%\3%\3"+
		"&\3&\3&\3&\3\'\3\'\3\'\3\'\3d\2(\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\2"+
		"\61\2\63\31\65\32\67\339\34;\35=\36?\37A C!E\"G#I$K%M&\3\2\t\4\2))aa\4"+
		"\2C\\c|\4\2##AA\4\2--//\5\2&&\62;C]\5\2\13\f\17\17\"\"\4\2))^^\2\u013d"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2\63\3\2"+
		"\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2"+
		"\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2"+
		"\2M\3\2\2\2\3O\3\2\2\2\5S\3\2\2\2\7Y\3\2\2\2\t`\3\2\2\2\13m\3\2\2\2\r"+
		"o\3\2\2\2\17q\3\2\2\2\21t\3\2\2\2\23x\3\2\2\2\25|\3\2\2\2\27~\3\2\2\2"+
		"\31\u0080\3\2\2\2\33\u0082\3\2\2\2\35\u0084\3\2\2\2\37\u0087\3\2\2\2!"+
		"\u008a\3\2\2\2#\u008c\3\2\2\2%\u008e\3\2\2\2\'\u0091\3\2\2\2)\u0093\3"+
		"\2\2\2+\u0095\3\2\2\2-\u0097\3\2\2\2/\u0099\3\2\2\2\61\u009b\3\2\2\2\63"+
		"\u009e\3\2\2\2\65\u00a2\3\2\2\2\67\u00a6\3\2\2\29\u00a8\3\2\2\2;\u00e9"+
		"\3\2\2\2=\u00fb\3\2\2\2?\u0101\3\2\2\2A\u010c\3\2\2\2C\u010e\3\2\2\2E"+
		"\u0110\3\2\2\2G\u0112\3\2\2\2I\u0114\3\2\2\2K\u0116\3\2\2\2M\u011a\3\2"+
		"\2\2OP\7B\2\2PQ\7K\2\2QR\7H\2\2R\4\3\2\2\2ST\7B\2\2TU\7N\2\2UV\7Q\2\2"+
		"VW\7Q\2\2WX\7R\2\2X\6\3\2\2\2YZ\7B\2\2Z[\7Y\2\2[\\\7J\2\2\\]\7K\2\2]^"+
		"\7N\2\2^_\7G\2\2_\b\3\2\2\2`d\7#\2\2ac\13\2\2\2ba\3\2\2\2cf\3\2\2\2de"+
		"\3\2\2\2db\3\2\2\2ej\3\2\2\2fd\3\2\2\2gi\5\'\24\2hg\3\2\2\2il\3\2\2\2"+
		"jh\3\2\2\2jk\3\2\2\2k\n\3\2\2\2lj\3\2\2\2mn\7*\2\2n\f\3\2\2\2op\7+\2\2"+
		"p\16\3\2\2\2qr\7&\2\2r\20\3\2\2\2su\5G$\2ts\3\2\2\2tu\3\2\2\2uv\3\2\2"+
		"\2vw\7}\2\2w\22\3\2\2\2xz\7\177\2\2y{\5G$\2zy\3\2\2\2z{\3\2\2\2{\24\3"+
		"\2\2\2|}\7]\2\2}\26\3\2\2\2~\177\7_\2\2\177\30\3\2\2\2\u0080\u0081\7^"+
		"\2\2\u0081\32\3\2\2\2\u0082\u0083\7>\2\2\u0083\34\3\2\2\2\u0084\u0085"+
		"\7>\2\2\u0085\u0086\t\2\2\2\u0086\36\3\2\2\2\u0087\u0088\7@\2\2\u0088"+
		"\u0089\t\2\2\2\u0089 \3\2\2\2\u008a\u008b\7@\2\2\u008b\"\3\2\2\2\u008c"+
		"\u008d\t\2\2\2\u008d$\3\2\2\2\u008e\u008f\7#\2\2\u008f\u0090\t\2\2\2\u0090"+
		"&\3\2\2\2\u0091\u0092\7=\2\2\u0092(\3\2\2\2\u0093\u0094\7`\2\2\u0094*"+
		"\3\2\2\2\u0095\u0096\7B\2\2\u0096,\3\2\2\2\u0097\u0098\7\'\2\2\u0098."+
		"\3\2\2\2\u0099\u009a\4\62;\2\u009a\60\3\2\2\2\u009b\u009c\t\3\2\2\u009c"+
		"\62\3\2\2\2\u009d\u009f\5/\30\2\u009e\u009d\3\2\2\2\u009f\u00a0\3\2\2"+
		"\2\u00a0\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\64\3\2\2\2\u00a2\u00a3"+
		"\5\63\32\2\u00a3\u00a4\7\60\2\2\u00a4\u00a5\5\63\32\2\u00a5\66\3\2\2\2"+
		"\u00a6\u00a7\7\60\2\2\u00a78\3\2\2\2\u00a8\u00a9\7a\2\2\u00a9\u00aa\7"+
		"K\2\2\u00aa\u00ab\7a\2\2\u00ab:\3\2\2\2\u00ac\u00b0\7a\2\2\u00ad\u00b0"+
		"\5\67\34\2\u00ae\u00b0\5\31\r\2\u00af\u00ac\3\2\2\2\u00af\u00ad\3\2\2"+
		"\2\u00af\u00ae\3\2\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b2"+
		"\3\2\2\2\u00b2\u00b6\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b4\u00b7\5\61\31\2"+
		"\u00b5\u00b7\5\63\32\2\u00b6\u00b4\3\2\2\2\u00b6\u00b5\3\2\2\2\u00b7\u00c0"+
		"\3\2\2\2\u00b8\u00bf\7a\2\2\u00b9\u00bf\5\61\31\2\u00ba\u00bf\5/\30\2"+
		"\u00bb\u00bf\5\67\34\2\u00bc\u00bf\5\31\r\2\u00bd\u00bf\t\4\2\2\u00be"+
		"\u00b8\3\2\2\2\u00be\u00b9\3\2\2\2\u00be\u00ba\3\2\2\2\u00be\u00bb\3\2"+
		"\2\2\u00be\u00bc\3\2\2\2\u00be\u00bd\3\2\2\2\u00bf\u00c2\3\2\2\2\u00c0"+
		"\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00ea\3\2\2\2\u00c2\u00c0\3\2"+
		"\2\2\u00c3\u00c5\7a\2\2\u00c4\u00c3\3\2\2\2\u00c5\u00c8\3\2\2\2\u00c6"+
		"\u00c4\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c9\3\2\2\2\u00c8\u00c6\3\2"+
		"\2\2\u00c9\u00cb\5\67\34\2\u00ca\u00cc\5/\30\2\u00cb\u00ca\3\2\2\2\u00cc"+
		"\u00cd\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00d5\3\2"+
		"\2\2\u00cf\u00d1\7\60\2\2\u00d0\u00d2\5/\30\2\u00d1\u00d0\3\2\2\2\u00d2"+
		"\u00d3\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d6\3\2"+
		"\2\2\u00d5\u00cf\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00e7\3\2\2\2\u00d7"+
		"\u00de\5\61\31\2\u00d8\u00dd\7a\2\2\u00d9\u00dd\5/\30\2\u00da\u00dd\5"+
		"\31\r\2\u00db\u00dd\t\4\2\2\u00dc\u00d8\3\2\2\2\u00dc\u00d9\3\2\2\2\u00dc"+
		"\u00da\3\2\2\2\u00dc\u00db\3\2\2\2\u00dd\u00e0\3\2\2\2\u00de\u00dc\3\2"+
		"\2\2\u00de\u00df\3\2\2\2\u00df\u00e8\3\2\2\2\u00e0\u00de\3\2\2\2\u00e1"+
		"\u00e3\7a\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4\u00e2\3\2"+
		"\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7"+
		"\u00d7\3\2\2\2\u00e7\u00e4\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9\u00b1\3\2"+
		"\2\2\u00e9\u00c6\3\2\2\2\u00ea\u00f5\3\2\2\2\u00eb\u00ee\5=\37\2\u00ec"+
		"\u00ef\5;\36\2\u00ed\u00ef\5\63\32\2\u00ee\u00ec\3\2\2\2\u00ee\u00ed\3"+
		"\2\2\2\u00ef\u00f6\3\2\2\2\u00f0\u00f3\5\17\b\2\u00f1\u00f4\5;\36\2\u00f2"+
		"\u00f4\5\63\32\2\u00f3\u00f1\3\2\2\2\u00f3\u00f2\3\2\2\2\u00f4\u00f6\3"+
		"\2\2\2\u00f5\u00eb\3\2\2\2\u00f5\u00f0\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6"+
		"<\3\2\2\2\u00f7\u00fc\t\5\2\2\u00f8\u00f9\7,\2\2\u00f9\u00fc\n\6\2\2\u00fa"+
		"\u00fc\5+\26\2\u00fb\u00f7\3\2\2\2\u00fb\u00f8\3\2\2\2\u00fb\u00fa\3\2"+
		"\2\2\u00fc>\3\2\2\2\u00fd\u00fe\7(\2\2\u00fe\u0102\7(\2\2\u00ff\u0100"+
		"\7~\2\2\u0100\u0102\7~\2\2\u0101\u00fd\3\2\2\2\u0101\u00ff\3\2\2\2\u0102"+
		"@\3\2\2\2\u0103\u0104\7V\2\2\u0104\u0105\7T\2\2\u0105\u0106\7W\2\2\u0106"+
		"\u010d\7G\2\2\u0107\u0108\7H\2\2\u0108\u0109\7C\2\2\u0109\u010a\7N\2\2"+
		"\u010a\u010b\7U\2\2\u010b\u010d\7G\2\2\u010c\u0103\3\2\2\2\u010c\u0107"+
		"\3\2\2\2\u010dB\3\2\2\2\u010e\u010f\7.\2\2\u010fD\3\2\2\2\u0110\u0111"+
		"\7~\2\2\u0111F\3\2\2\2\u0112\u0113\7$\2\2\u0113H\3\2\2\2\u0114\u0115\7"+
		",\2\2\u0115J\3\2\2\2\u0116\u0117\t\7\2\2\u0117\u0118\3\2\2\2\u0118\u0119"+
		"\b&\2\2\u0119L\3\2\2\2\u011a\u011b\7)\2\2\u011b\u011c\n\b\2\2\u011c\u011d"+
		"\7)\2\2\u011dN\3\2\2\2\34\2djtz\u00a0\u00af\u00b1\u00b6\u00be\u00c0\u00c6"+
		"\u00cd\u00d3\u00d5\u00dc\u00de\u00e4\u00e7\u00e9\u00ee\u00f3\u00f5\u00fb"+
		"\u0101\u010c\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}