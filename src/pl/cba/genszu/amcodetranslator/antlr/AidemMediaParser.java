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
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
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
			setState(69);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(63);
				string();
				}
				break;
			case 2:
				{
				setState(64);
				match(LITERAL);
				}
				break;
			case 3:
				{
				setState(65);
				expression();
				}
				break;
			case 4:
				{
				setState(66);
				match(DIGIT);
				}
				break;
			case 5:
				{
				setState(67);
				match(NUMBER);
				}
				break;
			case 6:
				{
				setState(68);
				functionFire();
				}
				break;
			}
			setState(71);
			match(SEPARATOR);
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(72);
				string();
				}
				break;
			case 2:
				{
				setState(73);
				match(LITERAL);
				}
				break;
			case 3:
				{
				setState(74);
				expression();
				}
				break;
			case 4:
				{
				setState(75);
				match(DIGIT);
				}
				break;
			case 5:
				{
				setState(76);
				match(NUMBER);
				}
				break;
			case 6:
				{
				setState(77);
				functionFire();
				}
				break;
			}
			setState(80);
			match(SEPARATOR);
			setState(87);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(81);
				string();
				}
				break;
			case 2:
				{
				setState(82);
				match(LITERAL);
				}
				break;
			case 3:
				{
				setState(83);
				expression();
				}
				break;
			case 4:
				{
				setState(84);
				match(DIGIT);
				}
				break;
			case 5:
				{
				setState(85);
				match(NUMBER);
				}
				break;
			case 6:
				{
				setState(86);
				functionFire();
				}
				break;
			}
			setState(89);
			match(RPAREN);
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(90);
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
			setState(93);
			match(T__2);
			setState(94);
			match(LPAREN);
			setState(95);
			param();
			setState(96);
			match(SEPARATOR);
			setState(97);
			match(QUOTEMARK);
			setState(98);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(99);
			match(QUOTEMARK);
			setState(100);
			match(SEPARATOR);
			setState(101);
			param();
			setState(102);
			match(SEPARATOR);
			setState(105);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(103);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(104);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(107);
			match(RPAREN);
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(108);
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
			setState(115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(111);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(112);
				stringRef();
				}
				break;
			case 3:
				{
				setState(113);
				struct();
				}
				break;
			case 4:
				{
				setState(114);
				match(SELF);
				}
				break;
			}
			setState(117);
			match(FIREFUNC);
			setState(118);
			match(LITERAL);
			setState(119);
			match(LPAREN);
			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << BOOLEAN) | (1L << SELF) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(120);
					match(SEPARATOR);
					}
				}

				setState(123);
				param();
				}
				}
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(129);
			match(RPAREN);
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(130);
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
			setState(133);
			match(STARTCODE);
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << LITERAL) | (1L << SELF) | (1L << STRREF))) != 0)) {
				{
				setState(139);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LITERAL:
				case SELF:
				case STRREF:
					{
					setState(134);
					functionFire();
					}
					break;
				case T__0:
					{
					setState(135);
					ifInstr();
					}
					break;
				case T__1:
					{
					setState(136);
					loopInstr();
					}
					break;
				case T__2:
					{
					setState(137);
					whileInstr();
					}
					break;
				case T__3:
					{
					setState(138);
					instr();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(144);
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
		public List<StringRefContext> stringRef() {
			return getRuleContexts(StringRefContext.class);
		}
		public StringRefContext stringRef(int i) {
			return getRuleContext(StringRefContext.class,i);
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
			setState(146);
			match(STARTEXPR);
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SELF) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(147);
					match(ARITHMETIC);
					}
				}

				setState(159);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(150);
					match(LITERAL);
					}
					break;
				case 2:
					{
					setState(151);
					string();
					}
					break;
				case 3:
					{
					setState(152);
					match(DIGIT);
					}
					break;
				case 4:
					{
					setState(153);
					match(NUMBER);
					}
					break;
				case 5:
					{
					setState(154);
					match(ITERATOR);
					}
					break;
				case 6:
					{
					setState(155);
					functionFire();
					}
					break;
				case 7:
					{
					setState(156);
					expression();
					}
					break;
				case 8:
					{
					setState(157);
					struct();
					}
					break;
				case 9:
					{
					setState(158);
					stringRef();
					}
					break;
				}
				}
				}
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(166);
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
			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(174);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STARTCODE:
					{
					setState(168);
					codeBlock();
					}
					break;
				case STARTEXPR:
					{
					setState(169);
					expression();
					}
					break;
				case QUOTEMARK:
					{
					setState(170);
					string();
					}
					break;
				case T__0:
					{
					setState(171);
					ifInstr();
					}
					break;
				case T__1:
					{
					setState(172);
					loopInstr();
					}
					break;
				case T__2:
					{
					setState(173);
					whileInstr();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(178);
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
			setState(190);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(179);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(180);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(181);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(182);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(183);
				match(LITERAL);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(184);
				match(DIGIT);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(185);
				match(NUMBER);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(186);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(187);
				match(ITERATOR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(188);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(189);
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
		public StructContext struct() {
			return getRuleContext(StructContext.class,0);
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
			setState(195);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(192);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(193);
				functionFire();
				}
				break;
			case 3:
				{
				setState(194);
				struct();
				}
				break;
			}
			setState(197);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(198);
			param();
			setState(201);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LOGIC) {
				{
				setState(199);
				match(LOGIC);
				setState(200);
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
		public List<TerminalNode> SLASH() { return getTokens(AidemMediaParser.SLASH); }
		public TerminalNode SLASH(int i) {
			return getToken(AidemMediaParser.SLASH, i);
		}
		public List<StructContext> struct() {
			return getRuleContexts(StructContext.class);
		}
		public StructContext struct(int i) {
			return getRuleContext(StructContext.class,i);
		}
		public List<TerminalNode> LPAREN() { return getTokens(AidemMediaParser.LPAREN); }
		public TerminalNode LPAREN(int i) {
			return getToken(AidemMediaParser.LPAREN, i);
		}
		public List<TerminalNode> RPAREN() { return getTokens(AidemMediaParser.RPAREN); }
		public TerminalNode RPAREN(int i) {
			return getToken(AidemMediaParser.RPAREN, i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(AidemMediaParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(AidemMediaParser.SEPARATOR, i);
		}
		public List<TerminalNode> ARITHMETIC() { return getTokens(AidemMediaParser.ARITHMETIC); }
		public TerminalNode ARITHMETIC(int i) {
			return getToken(AidemMediaParser.ARITHMETIC, i);
		}
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
			setState(203);
			match(QUOTEMARK);
			setState(231);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(219); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(219);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
					case 1:
						{
						setState(204);
						match(LITERAL);
						}
						break;
					case 2:
						{
						setState(205);
						match(DIGIT);
						}
						break;
					case 3:
						{
						setState(206);
						match(NUMBER);
						}
						break;
					case 4:
						{
						setState(207);
						match(LSS);
						}
						break;
					case 5:
						{
						setState(208);
						match(LEQ);
						}
						break;
					case 6:
						{
						setState(209);
						match(GEQ);
						}
						break;
					case 7:
						{
						setState(210);
						match(GTR);
						}
						break;
					case 8:
						{
						setState(211);
						match(EQU);
						}
						break;
					case 9:
						{
						setState(212);
						match(NEQ);
						}
						break;
					case 10:
						{
						setState(213);
						match(SLASH);
						}
						break;
					case 11:
						{
						setState(214);
						struct();
						}
						break;
					case 12:
						{
						setState(215);
						match(LPAREN);
						}
						break;
					case 13:
						{
						setState(216);
						match(RPAREN);
						}
						break;
					case 14:
						{
						setState(217);
						match(SEPARATOR);
						}
						break;
					case 15:
						{
						setState(218);
						match(ARITHMETIC);
						}
						break;
					}
					}
					setState(221); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << DIGIT) | (1L << NUMBER) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(223);
				variable();
				setState(228);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(224);
					match(SLASH);
					setState(226);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(225);
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
				setState(230);
				string();
				}
				break;
			}
			setState(233);
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
			setState(235);
			match(T__3);
			setState(236);
			match(LITERAL);
			setState(237);
			match(LPAREN);
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << BOOLEAN) | (1L << SELF) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(238);
				param();
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(239);
					match(SEPARATOR);
					setState(240);
					param();
					}
				}

				}
			}

			setState(245);
			match(RPAREN);
			setState(246);
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
			setState(248);
			match(STRREF);
			setState(251);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(249);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(250);
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
			setState(253);
			match(LITERAL);
			setState(254);
			match(STRUCTFIELD);
			setState(255);
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
			setState(257);
			match(VARREF);
			setState(258);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u0107\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2/\n\2\3\2\3\2\5\2\63\n\2\3\2\3\2"+
		"\3\2\5\28\n\2\3\2\3\2\5\2<\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\5\3H\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3Q\n\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\5\3Z\n\3\3\3\3\3\5\3^\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\5\4l\n\4\3\4\3\4\5\4p\n\4\3\5\3\5\3\5\3\5\5\5v\n\5\3\5\3\5\3"+
		"\5\3\5\5\5|\n\5\3\5\7\5\177\n\5\f\5\16\5\u0082\13\5\3\5\3\5\5\5\u0086"+
		"\n\5\3\6\3\6\3\6\3\6\3\6\3\6\7\6\u008e\n\6\f\6\16\6\u0091\13\6\3\6\3\6"+
		"\3\7\3\7\5\7\u0097\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00a2\n"+
		"\7\7\7\u00a4\n\7\f\7\16\7\u00a7\13\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\7"+
		"\b\u00b1\n\b\f\b\16\b\u00b4\13\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\5\t\u00c1\n\t\3\n\3\n\3\n\5\n\u00c6\n\n\3\n\3\n\3\n\3\n\5\n\u00cc"+
		"\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\6\13\u00de\n\13\r\13\16\13\u00df\3\13\3\13\3\13\5\13\u00e5"+
		"\n\13\5\13\u00e7\n\13\3\13\5\13\u00ea\n\13\3\13\3\13\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\5\f\u00f4\n\f\5\f\u00f6\n\f\3\f\3\f\3\f\3\r\3\r\3\r\5\r\u00fe\n"+
		"\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\2\2\20\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\2\4\3\2\17\24\4\2\27\27\32\32\2\u014d\2\36\3\2\2\2\4="+
		"\3\2\2\2\6_\3\2\2\2\bu\3\2\2\2\n\u0087\3\2\2\2\f\u0094\3\2\2\2\16\u00b2"+
		"\3\2\2\2\20\u00c0\3\2\2\2\22\u00c5\3\2\2\2\24\u00cd\3\2\2\2\26\u00ed\3"+
		"\2\2\2\30\u00fa\3\2\2\2\32\u00ff\3\2\2\2\34\u0103\3\2\2\2\36\37\7\3\2"+
		"\2\37.\7\7\2\2 !\7\"\2\2!\"\5\22\n\2\"#\7\"\2\2#$\7 \2\2$/\3\2\2\2%&\5"+
		"\20\t\2&\'\7 \2\2\'(\7\"\2\2()\t\2\2\2)*\7\"\2\2*+\7 \2\2+,\5\20\t\2,"+
		"-\7 \2\2-/\3\2\2\2. \3\2\2\2.%\3\2\2\2/\62\3\2\2\2\60\63\5\n\6\2\61\63"+
		"\5\24\13\2\62\60\3\2\2\2\62\61\3\2\2\2\63\64\3\2\2\2\64\67\7 \2\2\658"+
		"\5\n\6\2\668\5\24\13\2\67\65\3\2\2\2\67\66\3\2\2\289\3\2\2\29;\7\b\2\2"+
		":<\7\25\2\2;:\3\2\2\2;<\3\2\2\2<\3\3\2\2\2=>\7\4\2\2>?\7\7\2\2?@\5\n\6"+
		"\2@G\7 \2\2AH\5\24\13\2BH\7\32\2\2CH\5\f\7\2DH\7\27\2\2EH\7\30\2\2FH\5"+
		"\b\5\2GA\3\2\2\2GB\3\2\2\2GC\3\2\2\2GD\3\2\2\2GE\3\2\2\2GF\3\2\2\2HI\3"+
		"\2\2\2IP\7 \2\2JQ\5\24\13\2KQ\7\32\2\2LQ\5\f\7\2MQ\7\27\2\2NQ\7\30\2\2"+
		"OQ\5\b\5\2PJ\3\2\2\2PK\3\2\2\2PL\3\2\2\2PM\3\2\2\2PN\3\2\2\2PO\3\2\2\2"+
		"QR\3\2\2\2RY\7 \2\2SZ\5\24\13\2TZ\7\32\2\2UZ\5\f\7\2VZ\7\27\2\2WZ\7\30"+
		"\2\2XZ\5\b\5\2YS\3\2\2\2YT\3\2\2\2YU\3\2\2\2YV\3\2\2\2YW\3\2\2\2YX\3\2"+
		"\2\2Z[\3\2\2\2[]\7\b\2\2\\^\7\25\2\2]\\\3\2\2\2]^\3\2\2\2^\5\3\2\2\2_"+
		"`\7\5\2\2`a\7\7\2\2ab\5\20\t\2bc\7 \2\2cd\7\"\2\2de\t\2\2\2ef\7\"\2\2"+
		"fg\7 \2\2gh\5\20\t\2hk\7 \2\2il\5\n\6\2jl\5\24\13\2ki\3\2\2\2kj\3\2\2"+
		"\2lm\3\2\2\2mo\7\b\2\2np\7\25\2\2on\3\2\2\2op\3\2\2\2p\7\3\2\2\2qv\7\32"+
		"\2\2rv\5\30\r\2sv\5\32\16\2tv\7\37\2\2uq\3\2\2\2ur\3\2\2\2us\3\2\2\2u"+
		"t\3\2\2\2vw\3\2\2\2wx\7\26\2\2xy\7\32\2\2y\u0080\7\7\2\2z|\7 \2\2{z\3"+
		"\2\2\2{|\3\2\2\2|}\3\2\2\2}\177\5\20\t\2~{\3\2\2\2\177\u0082\3\2\2\2\u0080"+
		"~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0083\3\2\2\2\u0082\u0080\3\2\2\2"+
		"\u0083\u0085\7\b\2\2\u0084\u0086\7\25\2\2\u0085\u0084\3\2\2\2\u0085\u0086"+
		"\3\2\2\2\u0086\t\3\2\2\2\u0087\u008f\7\n\2\2\u0088\u008e\5\b\5\2\u0089"+
		"\u008e\5\2\2\2\u008a\u008e\5\4\3\2\u008b\u008e\5\6\4\2\u008c\u008e\5\26"+
		"\f\2\u008d\u0088\3\2\2\2\u008d\u0089\3\2\2\2\u008d\u008a\3\2\2\2\u008d"+
		"\u008b\3\2\2\2\u008d\u008c\3\2\2\2\u008e\u0091\3\2\2\2\u008f\u008d\3\2"+
		"\2\2\u008f\u0090\3\2\2\2\u0090\u0092\3\2\2\2\u0091\u008f\3\2\2\2\u0092"+
		"\u0093\7\13\2\2\u0093\13\3\2\2\2\u0094\u00a5\7\f\2\2\u0095\u0097\7\34"+
		"\2\2\u0096\u0095\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u00a1\3\2\2\2\u0098"+
		"\u00a2\7\32\2\2\u0099\u00a2\5\24\13\2\u009a\u00a2\7\27\2\2\u009b\u00a2"+
		"\7\30\2\2\u009c\u00a2\7\31\2\2\u009d\u00a2\5\b\5\2\u009e\u00a2\5\f\7\2"+
		"\u009f\u00a2\5\32\16\2\u00a0\u00a2\5\30\r\2\u00a1\u0098\3\2\2\2\u00a1"+
		"\u0099\3\2\2\2\u00a1\u009a\3\2\2\2\u00a1\u009b\3\2\2\2\u00a1\u009c\3\2"+
		"\2\2\u00a1\u009d\3\2\2\2\u00a1\u009e\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1"+
		"\u00a0\3\2\2\2\u00a2\u00a4\3\2\2\2\u00a3\u0096\3\2\2\2\u00a4\u00a7\3\2"+
		"\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a8\3\2\2\2\u00a7"+
		"\u00a5\3\2\2\2\u00a8\u00a9\7\r\2\2\u00a9\r\3\2\2\2\u00aa\u00b1\5\n\6\2"+
		"\u00ab\u00b1\5\f\7\2\u00ac\u00b1\5\24\13\2\u00ad\u00b1\5\2\2\2\u00ae\u00b1"+
		"\5\4\3\2\u00af\u00b1\5\6\4\2\u00b0\u00aa\3\2\2\2\u00b0\u00ab\3\2\2\2\u00b0"+
		"\u00ac\3\2\2\2\u00b0\u00ad\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00af\3\2"+
		"\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3"+
		"\17\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5\u00c1\5\b\5\2\u00b6\u00c1\7\36\2"+
		"\2\u00b7\u00c1\5\34\17\2\u00b8\u00c1\5\24\13\2\u00b9\u00c1\7\32\2\2\u00ba"+
		"\u00c1\7\27\2\2\u00bb\u00c1\7\30\2\2\u00bc\u00c1\5\f\7\2\u00bd\u00c1\7"+
		"\31\2\2\u00be\u00c1\5\32\16\2\u00bf\u00c1\5\30\r\2\u00c0\u00b5\3\2\2\2"+
		"\u00c0\u00b6\3\2\2\2\u00c0\u00b7\3\2\2\2\u00c0\u00b8\3\2\2\2\u00c0\u00b9"+
		"\3\2\2\2\u00c0\u00ba\3\2\2\2\u00c0\u00bb\3\2\2\2\u00c0\u00bc\3\2\2\2\u00c0"+
		"\u00bd\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00bf\3\2\2\2\u00c1\21\3\2\2"+
		"\2\u00c2\u00c6\7\32\2\2\u00c3\u00c6\5\b\5\2\u00c4\u00c6\5\32\16\2\u00c5"+
		"\u00c2\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c5\u00c4\3\2\2\2\u00c6\u00c7\3\2"+
		"\2\2\u00c7\u00c8\t\2\2\2\u00c8\u00cb\5\20\t\2\u00c9\u00ca\7\35\2\2\u00ca"+
		"\u00cc\5\22\n\2\u00cb\u00c9\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\23\3\2\2"+
		"\2\u00cd\u00e9\7\"\2\2\u00ce\u00de\7\32\2\2\u00cf\u00de\7\27\2\2\u00d0"+
		"\u00de\7\30\2\2\u00d1\u00de\7\17\2\2\u00d2\u00de\7\20\2\2\u00d3\u00de"+
		"\7\21\2\2\u00d4\u00de\7\22\2\2\u00d5\u00de\7\23\2\2\u00d6\u00de\7\24\2"+
		"\2\u00d7\u00de\7\16\2\2\u00d8\u00de\5\32\16\2\u00d9\u00de\7\7\2\2\u00da"+
		"\u00de\7\b\2\2\u00db\u00de\7 \2\2\u00dc\u00de\7\34\2\2\u00dd\u00ce\3\2"+
		"\2\2\u00dd\u00cf\3\2\2\2\u00dd\u00d0\3\2\2\2\u00dd\u00d1\3\2\2\2\u00dd"+
		"\u00d2\3\2\2\2\u00dd\u00d3\3\2\2\2\u00dd\u00d4\3\2\2\2\u00dd\u00d5\3\2"+
		"\2\2\u00dd\u00d6\3\2\2\2\u00dd\u00d7\3\2\2\2\u00dd\u00d8\3\2\2\2\u00dd"+
		"\u00d9\3\2\2\2\u00dd\u00da\3\2\2\2\u00dd\u00db\3\2\2\2\u00dd\u00dc\3\2"+
		"\2\2\u00de\u00df\3\2\2\2\u00df\u00dd\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0"+
		"\u00ea\3\2\2\2\u00e1\u00e6\5\34\17\2\u00e2\u00e4\7\16\2\2\u00e3\u00e5"+
		"\7\32\2\2\u00e4\u00e3\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e7\3\2\2\2"+
		"\u00e6\u00e2\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00ea\3\2\2\2\u00e8\u00ea"+
		"\5\24\13\2\u00e9\u00dd\3\2\2\2\u00e9\u00e1\3\2\2\2\u00e9\u00e8\3\2\2\2"+
		"\u00e9\u00ea\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ec\7\"\2\2\u00ec\25"+
		"\3\2\2\2\u00ed\u00ee\7\6\2\2\u00ee\u00ef\7\32\2\2\u00ef\u00f5\7\7\2\2"+
		"\u00f0\u00f3\5\20\t\2\u00f1\u00f2\7 \2\2\u00f2\u00f4\5\20\t\2\u00f3\u00f1"+
		"\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f6\3\2\2\2\u00f5\u00f0\3\2\2\2\u00f5"+
		"\u00f6\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8\7\b\2\2\u00f8\u00f9\7\25"+
		"\2\2\u00f9\27\3\2\2\2\u00fa\u00fd\7#\2\2\u00fb\u00fe\5\f\7\2\u00fc\u00fe"+
		"\7\32\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fc\3\2\2\2\u00fe\31\3\2\2\2\u00ff"+
		"\u0100\7\32\2\2\u0100\u0101\7!\2\2\u0101\u0102\7\32\2\2\u0102\33\3\2\2"+
		"\2\u0103\u0104\7\t\2\2\u0104\u0105\t\3\2\2\u0105\35\3\2\2\2\".\62\67;"+
		"GPY]kou{\u0080\u0085\u008d\u008f\u0096\u00a1\u00a5\u00b0\u00b2\u00c0\u00c5"+
		"\u00cb\u00dd\u00df\u00e4\u00e6\u00e9\u00f3\u00f5\u00fd";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}