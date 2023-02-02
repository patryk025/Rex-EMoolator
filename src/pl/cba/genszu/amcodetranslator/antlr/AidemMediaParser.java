package pl.cba.genszu.amcodetranslator.antlr;

// Generated from e:\gramatykaAM\AidemMedia.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AidemMediaParser extends Parser {
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
	public static final int
		RULE_ifInstr = 0, RULE_loopInstr = 1, RULE_whileInstr = 2, RULE_functionFire = 3, 
		RULE_codeBlock = 4, RULE_expression = 5, RULE_script = 6, RULE_param = 7, 
		RULE_condition = 8, RULE_string = 9, RULE_instr = 10, RULE_stringRef = 11, 
		RULE_struct = 12, RULE_variable = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"ifInstr", "loopInstr", "whileInstr", "functionFire", "codeBlock", "expression", 
			"script", "param", "condition", "string", "instr", "stringRef", "struct", 
			"variable"
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

	@Override
	public String getGrammarFileName() { return "AidemMedia.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public AidemMediaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class IfInstrContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public List<TerminalNode> SEPARATOR() { return getTokens(AidemMediaParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(AidemMediaParser.SEPARATOR, i);
		}
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public List<TerminalNode> QUOTEMARK() { return getTokens(AidemMediaParser.QUOTEMARK); }
		public TerminalNode QUOTEMARK(int i) {
			return getToken(AidemMediaParser.QUOTEMARK, i);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public List<CodeBlockContext> codeBlock() {
			return getRuleContexts(CodeBlockContext.class);
		}
		public CodeBlockContext codeBlock(int i) {
			return getRuleContext(CodeBlockContext.class,i);
		}
		public List<StringContext> string() {
			return getRuleContexts(StringContext.class);
		}
		public StringContext string(int i) {
			return getRuleContext(StringContext.class,i);
		}
		public TerminalNode LSS() { return getToken(AidemMediaParser.LSS, 0); }
		public TerminalNode LEQ() { return getToken(AidemMediaParser.LEQ, 0); }
		public TerminalNode GEQ() { return getToken(AidemMediaParser.GEQ, 0); }
		public TerminalNode GTR() { return getToken(AidemMediaParser.GTR, 0); }
		public TerminalNode EQU() { return getToken(AidemMediaParser.EQU, 0); }
		public TerminalNode NEQ() { return getToken(AidemMediaParser.NEQ, 0); }
		public TerminalNode ENDINSTR() { return getToken(AidemMediaParser.ENDINSTR, 0); }
		public IfInstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifInstr; }
	}

	public final IfInstrContext ifInstr() throws RecognitionException {
		IfInstrContext _localctx = new IfInstrContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_ifInstr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(T__0);
			setState(29);
			match(LPAREN);
			setState(44);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(30);
				match(QUOTEMARK);
				setState(31);
				condition();
				setState(32);
				match(QUOTEMARK);
				setState(33);
				match(SEPARATOR);
				}
				break;
			case 2:
				{
				setState(35);
				param();
				setState(36);
				match(SEPARATOR);
				setState(37);
				match(QUOTEMARK);
				setState(38);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(39);
				match(QUOTEMARK);
				setState(40);
				match(SEPARATOR);
				setState(41);
				param();
				setState(42);
				match(SEPARATOR);
				}
				break;
			}
			setState(48);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(46);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(47);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(50);
			match(SEPARATOR);
			setState(53);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(51);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(52);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(55);
			match(RPAREN);
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(56);
				match(ENDINSTR);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LoopInstrContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public CodeBlockContext codeBlock() {
			return getRuleContext(CodeBlockContext.class,0);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(AidemMediaParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(AidemMediaParser.SEPARATOR, i);
		}
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public List<StringContext> string() {
			return getRuleContexts(StringContext.class);
		}
		public StringContext string(int i) {
			return getRuleContext(StringContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> DIGIT() { return getTokens(AidemMediaParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(AidemMediaParser.DIGIT, i);
		}
		public List<TerminalNode> NUMBER() { return getTokens(AidemMediaParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(AidemMediaParser.NUMBER, i);
		}
		public List<FunctionFireContext> functionFire() {
			return getRuleContexts(FunctionFireContext.class);
		}
		public FunctionFireContext functionFire(int i) {
			return getRuleContext(FunctionFireContext.class,i);
		}
		public TerminalNode ENDINSTR() { return getToken(AidemMediaParser.ENDINSTR, 0); }
		public LoopInstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loopInstr; }
	}

	public final LoopInstrContext loopInstr() throws RecognitionException {
		LoopInstrContext _localctx = new LoopInstrContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_loopInstr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(T__1);
			setState(60);
			match(LPAREN);
			setState(61);
			codeBlock();
			setState(62);
			match(SEPARATOR);
			setState(68);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUOTEMARK:
				{
				setState(63);
				string();
				}
				break;
			case STARTEXPR:
				{
				setState(64);
				expression();
				}
				break;
			case DIGIT:
				{
				setState(65);
				match(DIGIT);
				}
				break;
			case NUMBER:
				{
				setState(66);
				match(NUMBER);
				}
				break;
			case LITERAL:
			case SELF:
			case STRREF:
				{
				setState(67);
				functionFire();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(70);
			match(SEPARATOR);
			setState(76);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUOTEMARK:
				{
				setState(71);
				string();
				}
				break;
			case STARTEXPR:
				{
				setState(72);
				expression();
				}
				break;
			case DIGIT:
				{
				setState(73);
				match(DIGIT);
				}
				break;
			case NUMBER:
				{
				setState(74);
				match(NUMBER);
				}
				break;
			case LITERAL:
			case SELF:
			case STRREF:
				{
				setState(75);
				functionFire();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(78);
			match(SEPARATOR);
			setState(84);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QUOTEMARK:
				{
				setState(79);
				string();
				}
				break;
			case STARTEXPR:
				{
				setState(80);
				expression();
				}
				break;
			case DIGIT:
				{
				setState(81);
				match(DIGIT);
				}
				break;
			case NUMBER:
				{
				setState(82);
				match(NUMBER);
				}
				break;
			case LITERAL:
			case SELF:
			case STRREF:
				{
				setState(83);
				functionFire();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(86);
			match(RPAREN);
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(87);
				match(ENDINSTR);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhileInstrContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(AidemMediaParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(AidemMediaParser.SEPARATOR, i);
		}
		public List<TerminalNode> QUOTEMARK() { return getTokens(AidemMediaParser.QUOTEMARK); }
		public TerminalNode QUOTEMARK(int i) {
			return getToken(AidemMediaParser.QUOTEMARK, i);
		}
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public TerminalNode LSS() { return getToken(AidemMediaParser.LSS, 0); }
		public TerminalNode LEQ() { return getToken(AidemMediaParser.LEQ, 0); }
		public TerminalNode GEQ() { return getToken(AidemMediaParser.GEQ, 0); }
		public TerminalNode GTR() { return getToken(AidemMediaParser.GTR, 0); }
		public TerminalNode EQU() { return getToken(AidemMediaParser.EQU, 0); }
		public TerminalNode NEQ() { return getToken(AidemMediaParser.NEQ, 0); }
		public CodeBlockContext codeBlock() {
			return getRuleContext(CodeBlockContext.class,0);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public TerminalNode ENDINSTR() { return getToken(AidemMediaParser.ENDINSTR, 0); }
		public WhileInstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileInstr; }
	}

	public final WhileInstrContext whileInstr() throws RecognitionException {
		WhileInstrContext _localctx = new WhileInstrContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_whileInstr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(T__2);
			setState(91);
			match(LPAREN);
			setState(92);
			param();
			setState(93);
			match(SEPARATOR);
			setState(94);
			match(QUOTEMARK);
			setState(95);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(96);
			match(QUOTEMARK);
			setState(97);
			match(SEPARATOR);
			setState(98);
			param();
			setState(99);
			match(SEPARATOR);
			setState(102);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(100);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(101);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(104);
			match(RPAREN);
			setState(106);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(105);
				match(ENDINSTR);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionFireContext extends ParserRuleContext {
		public TerminalNode FIREFUNC() { return getToken(AidemMediaParser.FIREFUNC, 0); }
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
		}
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public StringRefContext stringRef() {
			return getRuleContext(StringRefContext.class,0);
		}
		public StructContext struct() {
			return getRuleContext(StructContext.class,0);
		}
		public TerminalNode SELF() { return getToken(AidemMediaParser.SELF, 0); }
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public TerminalNode ENDINSTR() { return getToken(AidemMediaParser.ENDINSTR, 0); }
		public List<TerminalNode> SEPARATOR() { return getTokens(AidemMediaParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(AidemMediaParser.SEPARATOR, i);
		}
		public FunctionFireContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionFire; }
	}

	public final FunctionFireContext functionFire() throws RecognitionException {
		FunctionFireContext _localctx = new FunctionFireContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_functionFire);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(108);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(109);
				stringRef();
				}
				break;
			case 3:
				{
				setState(110);
				struct();
				}
				break;
			case 4:
				{
				setState(111);
				match(SELF);
				}
				break;
			}
			setState(114);
			match(FIREFUNC);
			setState(115);
			match(LITERAL);
			setState(116);
			match(LPAREN);
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << BOOLEAN) | (1L << SELF) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(117);
					match(SEPARATOR);
					}
				}

				setState(120);
				param();
				}
				}
				setState(125);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(126);
			match(RPAREN);
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(127);
				match(ENDINSTR);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CodeBlockContext extends ParserRuleContext {
		public TerminalNode STARTCODE() { return getToken(AidemMediaParser.STARTCODE, 0); }
		public TerminalNode STOPCODE() { return getToken(AidemMediaParser.STOPCODE, 0); }
		public List<FunctionFireContext> functionFire() {
			return getRuleContexts(FunctionFireContext.class);
		}
		public FunctionFireContext functionFire(int i) {
			return getRuleContext(FunctionFireContext.class,i);
		}
		public List<IfInstrContext> ifInstr() {
			return getRuleContexts(IfInstrContext.class);
		}
		public IfInstrContext ifInstr(int i) {
			return getRuleContext(IfInstrContext.class,i);
		}
		public List<LoopInstrContext> loopInstr() {
			return getRuleContexts(LoopInstrContext.class);
		}
		public LoopInstrContext loopInstr(int i) {
			return getRuleContext(LoopInstrContext.class,i);
		}
		public List<WhileInstrContext> whileInstr() {
			return getRuleContexts(WhileInstrContext.class);
		}
		public WhileInstrContext whileInstr(int i) {
			return getRuleContext(WhileInstrContext.class,i);
		}
		public List<InstrContext> instr() {
			return getRuleContexts(InstrContext.class);
		}
		public InstrContext instr(int i) {
			return getRuleContext(InstrContext.class,i);
		}
		public CodeBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_codeBlock; }
	}

	public final CodeBlockContext codeBlock() throws RecognitionException {
		CodeBlockContext _localctx = new CodeBlockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_codeBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			match(STARTCODE);
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << LITERAL) | (1L << SELF) | (1L << STRREF))) != 0)) {
				{
				setState(136);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LITERAL:
				case SELF:
				case STRREF:
					{
					setState(131);
					functionFire();
					}
					break;
				case T__0:
					{
					setState(132);
					ifInstr();
					}
					break;
				case T__1:
					{
					setState(133);
					loopInstr();
					}
					break;
				case T__2:
					{
					setState(134);
					whileInstr();
					}
					break;
				case T__3:
					{
					setState(135);
					instr();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(140);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(141);
			match(STOPCODE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public TerminalNode STARTEXPR() { return getToken(AidemMediaParser.STARTEXPR, 0); }
		public TerminalNode STOPEXPR() { return getToken(AidemMediaParser.STOPEXPR, 0); }
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
		}
		public List<StringContext> string() {
			return getRuleContexts(StringContext.class);
		}
		public StringContext string(int i) {
			return getRuleContext(StringContext.class,i);
		}
		public List<TerminalNode> DIGIT() { return getTokens(AidemMediaParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(AidemMediaParser.DIGIT, i);
		}
		public List<TerminalNode> NUMBER() { return getTokens(AidemMediaParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(AidemMediaParser.NUMBER, i);
		}
		public List<TerminalNode> ITERATOR() { return getTokens(AidemMediaParser.ITERATOR); }
		public TerminalNode ITERATOR(int i) {
			return getToken(AidemMediaParser.ITERATOR, i);
		}
		public List<FunctionFireContext> functionFire() {
			return getRuleContexts(FunctionFireContext.class);
		}
		public FunctionFireContext functionFire(int i) {
			return getRuleContext(FunctionFireContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<StructContext> struct() {
			return getRuleContexts(StructContext.class);
		}
		public StructContext struct(int i) {
			return getRuleContext(StructContext.class,i);
		}
		public List<TerminalNode> ARITHMETIC() { return getTokens(AidemMediaParser.ARITHMETIC); }
		public TerminalNode ARITHMETIC(int i) {
			return getToken(AidemMediaParser.ARITHMETIC, i);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(STARTEXPR);
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SELF) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(144);
					match(ARITHMETIC);
					}
				}

				setState(155);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(147);
					match(LITERAL);
					}
					break;
				case 2:
					{
					setState(148);
					string();
					}
					break;
				case 3:
					{
					setState(149);
					match(DIGIT);
					}
					break;
				case 4:
					{
					setState(150);
					match(NUMBER);
					}
					break;
				case 5:
					{
					setState(151);
					match(ITERATOR);
					}
					break;
				case 6:
					{
					setState(152);
					functionFire();
					}
					break;
				case 7:
					{
					setState(153);
					expression();
					}
					break;
				case 8:
					{
					setState(154);
					struct();
					}
					break;
				}
				}
				}
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(162);
			match(STOPEXPR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ScriptContext extends ParserRuleContext {
		public List<CodeBlockContext> codeBlock() {
			return getRuleContexts(CodeBlockContext.class);
		}
		public CodeBlockContext codeBlock(int i) {
			return getRuleContext(CodeBlockContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<StringContext> string() {
			return getRuleContexts(StringContext.class);
		}
		public StringContext string(int i) {
			return getRuleContext(StringContext.class,i);
		}
		public List<IfInstrContext> ifInstr() {
			return getRuleContexts(IfInstrContext.class);
		}
		public IfInstrContext ifInstr(int i) {
			return getRuleContext(IfInstrContext.class,i);
		}
		public List<LoopInstrContext> loopInstr() {
			return getRuleContexts(LoopInstrContext.class);
		}
		public LoopInstrContext loopInstr(int i) {
			return getRuleContext(LoopInstrContext.class,i);
		}
		public List<WhileInstrContext> whileInstr() {
			return getRuleContexts(WhileInstrContext.class);
		}
		public WhileInstrContext whileInstr(int i) {
			return getRuleContext(WhileInstrContext.class,i);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_script);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(170);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STARTCODE:
					{
					setState(164);
					codeBlock();
					}
					break;
				case STARTEXPR:
					{
					setState(165);
					expression();
					}
					break;
				case QUOTEMARK:
					{
					setState(166);
					string();
					}
					break;
				case T__0:
					{
					setState(167);
					ifInstr();
					}
					break;
				case T__1:
					{
					setState(168);
					loopInstr();
					}
					break;
				case T__2:
					{
					setState(169);
					whileInstr();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(174);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamContext extends ParserRuleContext {
		public FunctionFireContext functionFire() {
			return getRuleContext(FunctionFireContext.class,0);
		}
		public TerminalNode BOOLEAN() { return getToken(AidemMediaParser.BOOLEAN, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public TerminalNode LITERAL() { return getToken(AidemMediaParser.LITERAL, 0); }
		public TerminalNode DIGIT() { return getToken(AidemMediaParser.DIGIT, 0); }
		public TerminalNode NUMBER() { return getToken(AidemMediaParser.NUMBER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ITERATOR() { return getToken(AidemMediaParser.ITERATOR, 0); }
		public StructContext struct() {
			return getRuleContext(StructContext.class,0);
		}
		public StringRefContext stringRef() {
			return getRuleContext(StringRefContext.class,0);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_param);
		try {
			setState(186);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(175);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(177);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(178);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(179);
				match(LITERAL);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(180);
				match(DIGIT);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(181);
				match(NUMBER);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(182);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(183);
				match(ITERATOR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(184);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(185);
				stringRef();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionContext extends ParserRuleContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode LSS() { return getToken(AidemMediaParser.LSS, 0); }
		public TerminalNode LEQ() { return getToken(AidemMediaParser.LEQ, 0); }
		public TerminalNode GEQ() { return getToken(AidemMediaParser.GEQ, 0); }
		public TerminalNode GTR() { return getToken(AidemMediaParser.GTR, 0); }
		public TerminalNode EQU() { return getToken(AidemMediaParser.EQU, 0); }
		public TerminalNode NEQ() { return getToken(AidemMediaParser.NEQ, 0); }
		public TerminalNode LITERAL() { return getToken(AidemMediaParser.LITERAL, 0); }
		public FunctionFireContext functionFire() {
			return getRuleContext(FunctionFireContext.class,0);
		}
		public TerminalNode LOGIC() { return getToken(AidemMediaParser.LOGIC, 0); }
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(188);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(189);
				functionFire();
				}
				break;
			}
			setState(192);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(193);
			param();
			setState(196);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LOGIC) {
				{
				setState(194);
				match(LOGIC);
				setState(195);
				condition();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringContext extends ParserRuleContext {
		public List<TerminalNode> QUOTEMARK() { return getTokens(AidemMediaParser.QUOTEMARK); }
		public TerminalNode QUOTEMARK(int i) {
			return getToken(AidemMediaParser.QUOTEMARK, i);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
		}
		public List<TerminalNode> DIGIT() { return getTokens(AidemMediaParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(AidemMediaParser.DIGIT, i);
		}
		public List<TerminalNode> NUMBER() { return getTokens(AidemMediaParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(AidemMediaParser.NUMBER, i);
		}
		public List<TerminalNode> LSS() { return getTokens(AidemMediaParser.LSS); }
		public TerminalNode LSS(int i) {
			return getToken(AidemMediaParser.LSS, i);
		}
		public List<TerminalNode> LEQ() { return getTokens(AidemMediaParser.LEQ); }
		public TerminalNode LEQ(int i) {
			return getToken(AidemMediaParser.LEQ, i);
		}
		public List<TerminalNode> GEQ() { return getTokens(AidemMediaParser.GEQ); }
		public TerminalNode GEQ(int i) {
			return getToken(AidemMediaParser.GEQ, i);
		}
		public List<TerminalNode> GTR() { return getTokens(AidemMediaParser.GTR); }
		public TerminalNode GTR(int i) {
			return getToken(AidemMediaParser.GTR, i);
		}
		public List<TerminalNode> EQU() { return getTokens(AidemMediaParser.EQU); }
		public TerminalNode EQU(int i) {
			return getToken(AidemMediaParser.EQU, i);
		}
		public List<TerminalNode> NEQ() { return getTokens(AidemMediaParser.NEQ); }
		public TerminalNode NEQ(int i) {
			return getToken(AidemMediaParser.NEQ, i);
		}
		public TerminalNode SLASH() { return getToken(AidemMediaParser.SLASH, 0); }
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			match(QUOTEMARK);
			setState(212);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(200); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(199);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << DIGIT) | (1L << NUMBER) | (1L << LITERAL))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					setState(202); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << DIGIT) | (1L << NUMBER) | (1L << LITERAL))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(204);
				variable();
				setState(209);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(205);
					match(SLASH);
					setState(207);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(206);
						match(LITERAL);
						}
					}

					}
				}

				}
				}
				break;
			case 3:
				{
				setState(211);
				string();
				}
				break;
			}
			setState(214);
			match(QUOTEMARK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InstrContext extends ParserRuleContext {
		public TerminalNode LITERAL() { return getToken(AidemMediaParser.LITERAL, 0); }
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public TerminalNode ENDINSTR() { return getToken(AidemMediaParser.ENDINSTR, 0); }
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public TerminalNode SEPARATOR() { return getToken(AidemMediaParser.SEPARATOR, 0); }
		public InstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instr; }
	}

	public final InstrContext instr() throws RecognitionException {
		InstrContext _localctx = new InstrContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_instr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			match(T__3);
			setState(217);
			match(LITERAL);
			setState(218);
			match(LPAREN);
			setState(223);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << BOOLEAN) | (1L << SELF) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(219);
				param();
				setState(220);
				match(SEPARATOR);
				setState(221);
				param();
				}
			}

			setState(225);
			match(RPAREN);
			setState(226);
			match(ENDINSTR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringRefContext extends ParserRuleContext {
		public TerminalNode STRREF() { return getToken(AidemMediaParser.STRREF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LITERAL() { return getToken(AidemMediaParser.LITERAL, 0); }
		public StringRefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringRef; }
	}

	public final StringRefContext stringRef() throws RecognitionException {
		StringRefContext _localctx = new StringRefContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_stringRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(STRREF);
			setState(231);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(229);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(230);
				match(LITERAL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructContext extends ParserRuleContext {
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
		}
		public TerminalNode STRUCTFIELD() { return getToken(AidemMediaParser.STRUCTFIELD, 0); }
		public StructContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_struct; }
	}

	public final StructContext struct() throws RecognitionException {
		StructContext _localctx = new StructContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_struct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			match(LITERAL);
			setState(234);
			match(STRUCTFIELD);
			setState(235);
			match(LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode VARREF() { return getToken(AidemMediaParser.VARREF, 0); }
		public TerminalNode LITERAL() { return getToken(AidemMediaParser.LITERAL, 0); }
		public TerminalNode DIGIT() { return getToken(AidemMediaParser.DIGIT, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			match(VARREF);
			setState(238);
			_la = _input.LA(1);
			if ( !(_la==DIGIT || _la==LITERAL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u00f3\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2/\n\2\3\2\3\2\5\2\63\n\2\3\2\3\2"+
		"\3\2\5\28\n\2\3\2\3\2\5\2<\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3"+
		"G\n\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3O\n\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3W\n"+
		"\3\3\3\3\3\5\3[\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5"+
		"\4i\n\4\3\4\3\4\5\4m\n\4\3\5\3\5\3\5\3\5\5\5s\n\5\3\5\3\5\3\5\3\5\5\5"+
		"y\n\5\3\5\7\5|\n\5\f\5\16\5\177\13\5\3\5\3\5\5\5\u0083\n\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\7\6\u008b\n\6\f\6\16\6\u008e\13\6\3\6\3\6\3\7\3\7\5\7\u0094"+
		"\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u009e\n\7\7\7\u00a0\n\7\f\7\16"+
		"\7\u00a3\13\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\7\b\u00ad\n\b\f\b\16\b\u00b0"+
		"\13\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u00bd\n\t\3\n\3"+
		"\n\5\n\u00c1\n\n\3\n\3\n\3\n\3\n\5\n\u00c7\n\n\3\13\3\13\6\13\u00cb\n"+
		"\13\r\13\16\13\u00cc\3\13\3\13\3\13\5\13\u00d2\n\13\5\13\u00d4\n\13\3"+
		"\13\5\13\u00d7\n\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00e2\n"+
		"\f\3\f\3\f\3\f\3\r\3\r\3\r\5\r\u00ea\n\r\3\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\2\2\20\2\4\6\b\n\f\16\20\22\24\26\30\32\34\2\5\3\2\17\24\5"+
		"\2\17\24\27\30\32\32\4\2\27\27\32\32\2\u0125\2\36\3\2\2\2\4=\3\2\2\2\6"+
		"\\\3\2\2\2\br\3\2\2\2\n\u0084\3\2\2\2\f\u0091\3\2\2\2\16\u00ae\3\2\2\2"+
		"\20\u00bc\3\2\2\2\22\u00c0\3\2\2\2\24\u00c8\3\2\2\2\26\u00da\3\2\2\2\30"+
		"\u00e6\3\2\2\2\32\u00eb\3\2\2\2\34\u00ef\3\2\2\2\36\37\7\3\2\2\37.\7\7"+
		"\2\2 !\7\"\2\2!\"\5\22\n\2\"#\7\"\2\2#$\7 \2\2$/\3\2\2\2%&\5\20\t\2&\'"+
		"\7 \2\2\'(\7\"\2\2()\t\2\2\2)*\7\"\2\2*+\7 \2\2+,\5\20\t\2,-\7 \2\2-/"+
		"\3\2\2\2. \3\2\2\2.%\3\2\2\2/\62\3\2\2\2\60\63\5\n\6\2\61\63\5\24\13\2"+
		"\62\60\3\2\2\2\62\61\3\2\2\2\63\64\3\2\2\2\64\67\7 \2\2\658\5\n\6\2\66"+
		"8\5\24\13\2\67\65\3\2\2\2\67\66\3\2\2\289\3\2\2\29;\7\b\2\2:<\7\25\2\2"+
		";:\3\2\2\2;<\3\2\2\2<\3\3\2\2\2=>\7\4\2\2>?\7\7\2\2?@\5\n\6\2@F\7 \2\2"+
		"AG\5\24\13\2BG\5\f\7\2CG\7\27\2\2DG\7\30\2\2EG\5\b\5\2FA\3\2\2\2FB\3\2"+
		"\2\2FC\3\2\2\2FD\3\2\2\2FE\3\2\2\2GH\3\2\2\2HN\7 \2\2IO\5\24\13\2JO\5"+
		"\f\7\2KO\7\27\2\2LO\7\30\2\2MO\5\b\5\2NI\3\2\2\2NJ\3\2\2\2NK\3\2\2\2N"+
		"L\3\2\2\2NM\3\2\2\2OP\3\2\2\2PV\7 \2\2QW\5\24\13\2RW\5\f\7\2SW\7\27\2"+
		"\2TW\7\30\2\2UW\5\b\5\2VQ\3\2\2\2VR\3\2\2\2VS\3\2\2\2VT\3\2\2\2VU\3\2"+
		"\2\2WX\3\2\2\2XZ\7\b\2\2Y[\7\25\2\2ZY\3\2\2\2Z[\3\2\2\2[\5\3\2\2\2\\]"+
		"\7\5\2\2]^\7\7\2\2^_\5\20\t\2_`\7 \2\2`a\7\"\2\2ab\t\2\2\2bc\7\"\2\2c"+
		"d\7 \2\2de\5\20\t\2eh\7 \2\2fi\5\n\6\2gi\5\24\13\2hf\3\2\2\2hg\3\2\2\2"+
		"ij\3\2\2\2jl\7\b\2\2km\7\25\2\2lk\3\2\2\2lm\3\2\2\2m\7\3\2\2\2ns\7\32"+
		"\2\2os\5\30\r\2ps\5\32\16\2qs\7\37\2\2rn\3\2\2\2ro\3\2\2\2rp\3\2\2\2r"+
		"q\3\2\2\2st\3\2\2\2tu\7\26\2\2uv\7\32\2\2v}\7\7\2\2wy\7 \2\2xw\3\2\2\2"+
		"xy\3\2\2\2yz\3\2\2\2z|\5\20\t\2{x\3\2\2\2|\177\3\2\2\2}{\3\2\2\2}~\3\2"+
		"\2\2~\u0080\3\2\2\2\177}\3\2\2\2\u0080\u0082\7\b\2\2\u0081\u0083\7\25"+
		"\2\2\u0082\u0081\3\2\2\2\u0082\u0083\3\2\2\2\u0083\t\3\2\2\2\u0084\u008c"+
		"\7\n\2\2\u0085\u008b\5\b\5\2\u0086\u008b\5\2\2\2\u0087\u008b\5\4\3\2\u0088"+
		"\u008b\5\6\4\2\u0089\u008b\5\26\f\2\u008a\u0085\3\2\2\2\u008a\u0086\3"+
		"\2\2\2\u008a\u0087\3\2\2\2\u008a\u0088\3\2\2\2\u008a\u0089\3\2\2\2\u008b"+
		"\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f\3\2"+
		"\2\2\u008e\u008c\3\2\2\2\u008f\u0090\7\13\2\2\u0090\13\3\2\2\2\u0091\u00a1"+
		"\7\f\2\2\u0092\u0094\7\34\2\2\u0093\u0092\3\2\2\2\u0093\u0094\3\2\2\2"+
		"\u0094\u009d\3\2\2\2\u0095\u009e\7\32\2\2\u0096\u009e\5\24\13\2\u0097"+
		"\u009e\7\27\2\2\u0098\u009e\7\30\2\2\u0099\u009e\7\31\2\2\u009a\u009e"+
		"\5\b\5\2\u009b\u009e\5\f\7\2\u009c\u009e\5\32\16\2\u009d\u0095\3\2\2\2"+
		"\u009d\u0096\3\2\2\2\u009d\u0097\3\2\2\2\u009d\u0098\3\2\2\2\u009d\u0099"+
		"\3\2\2\2\u009d\u009a\3\2\2\2\u009d\u009b\3\2\2\2\u009d\u009c\3\2\2\2\u009e"+
		"\u00a0\3\2\2\2\u009f\u0093\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2"+
		"\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a4\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4"+
		"\u00a5\7\r\2\2\u00a5\r\3\2\2\2\u00a6\u00ad\5\n\6\2\u00a7\u00ad\5\f\7\2"+
		"\u00a8\u00ad\5\24\13\2\u00a9\u00ad\5\2\2\2\u00aa\u00ad\5\4\3\2\u00ab\u00ad"+
		"\5\6\4\2\u00ac\u00a6\3\2\2\2\u00ac\u00a7\3\2\2\2\u00ac\u00a8\3\2\2\2\u00ac"+
		"\u00a9\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ab\3\2\2\2\u00ad\u00b0\3\2"+
		"\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\17\3\2\2\2\u00b0\u00ae"+
		"\3\2\2\2\u00b1\u00bd\5\b\5\2\u00b2\u00bd\7\36\2\2\u00b3\u00bd\5\34\17"+
		"\2\u00b4\u00bd\5\24\13\2\u00b5\u00bd\7\32\2\2\u00b6\u00bd\7\27\2\2\u00b7"+
		"\u00bd\7\30\2\2\u00b8\u00bd\5\f\7\2\u00b9\u00bd\7\31\2\2\u00ba\u00bd\5"+
		"\32\16\2\u00bb\u00bd\5\30\r\2\u00bc\u00b1\3\2\2\2\u00bc\u00b2\3\2\2\2"+
		"\u00bc\u00b3\3\2\2\2\u00bc\u00b4\3\2\2\2\u00bc\u00b5\3\2\2\2\u00bc\u00b6"+
		"\3\2\2\2\u00bc\u00b7\3\2\2\2\u00bc\u00b8\3\2\2\2\u00bc\u00b9\3\2\2\2\u00bc"+
		"\u00ba\3\2\2\2\u00bc\u00bb\3\2\2\2\u00bd\21\3\2\2\2\u00be\u00c1\7\32\2"+
		"\2\u00bf\u00c1\5\b\5\2\u00c0\u00be\3\2\2\2\u00c0\u00bf\3\2\2\2\u00c1\u00c2"+
		"\3\2\2\2\u00c2\u00c3\t\2\2\2\u00c3\u00c6\5\20\t\2\u00c4\u00c5\7\35\2\2"+
		"\u00c5\u00c7\5\22\n\2\u00c6\u00c4\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\23"+
		"\3\2\2\2\u00c8\u00d6\7\"\2\2\u00c9\u00cb\t\3\2\2\u00ca\u00c9\3\2\2\2\u00cb"+
		"\u00cc\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00d7\3\2"+
		"\2\2\u00ce\u00d3\5\34\17\2\u00cf\u00d1\7\16\2\2\u00d0\u00d2\7\32\2\2\u00d1"+
		"\u00d0\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d4\3\2\2\2\u00d3\u00cf\3\2"+
		"\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d7\5\24\13\2\u00d6"+
		"\u00ca\3\2\2\2\u00d6\u00ce\3\2\2\2\u00d6\u00d5\3\2\2\2\u00d6\u00d7\3\2"+
		"\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\7\"\2\2\u00d9\25\3\2\2\2\u00da\u00db"+
		"\7\6\2\2\u00db\u00dc\7\32\2\2\u00dc\u00e1\7\7\2\2\u00dd\u00de\5\20\t\2"+
		"\u00de\u00df\7 \2\2\u00df\u00e0\5\20\t\2\u00e0\u00e2\3\2\2\2\u00e1\u00dd"+
		"\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e4\7\b\2\2\u00e4"+
		"\u00e5\7\25\2\2\u00e5\27\3\2\2\2\u00e6\u00e9\7#\2\2\u00e7\u00ea\5\f\7"+
		"\2\u00e8\u00ea\7\32\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00e8\3\2\2\2\u00ea"+
		"\31\3\2\2\2\u00eb\u00ec\7\32\2\2\u00ec\u00ed\7!\2\2\u00ed\u00ee\7\32\2"+
		"\2\u00ee\33\3\2\2\2\u00ef\u00f0\7\t\2\2\u00f0\u00f1\t\4\2\2\u00f1\35\3"+
		"\2\2\2 .\62\67;FNVZhlrx}\u0082\u008a\u008c\u0093\u009d\u00a1\u00ac\u00ae"+
		"\u00bc\u00c0\u00c6\u00cc\u00d1\u00d3\u00d6\u00e1\u00e9";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}