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
		SEPARATOR=29, STRUCTFIELD=30, QUOTEMARK=31, STRREF=32, WS=33, CHAR=34;
	public static final int
		RULE_ifInstr = 0, RULE_loopInstr = 1, RULE_whileInstr = 2, RULE_functionFire = 3, 
		RULE_codeBlock = 4, RULE_expression = 5, RULE_script = 6, RULE_param = 7, 
		RULE_condition = 8, RULE_behFire = 9, RULE_modulo = 10, RULE_string = 11, 
		RULE_instr = 12, RULE_stringRef = 13, RULE_struct = 14, RULE_variable = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"ifInstr", "loopInstr", "whileInstr", "functionFire", "codeBlock", "expression", 
			"script", "param", "condition", "behFire", "modulo", "string", "instr", 
			"stringRef", "struct", "variable"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@IF'", "'@LOOP'", "'@WHILE'", "'@'", "'('", "')'", "'$'", null, 
			null, "'['", "']'", "'\\'", "'<'", null, null, "'>'", null, null, "';'", 
			"'^'", null, null, "'_I_'", null, null, null, null, null, "','", "'|'", 
			"'\"'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "LPAREN", "RPAREN", "VARREF", "STARTCODE", 
			"STOPCODE", "STARTEXPR", "STOPEXPR", "SLASH", "LSS", "LEQ", "GEQ", "GTR", 
			"EQU", "NEQ", "ENDINSTR", "FIREFUNC", "DIGIT", "NUMBER", "ITERATOR", 
			"LITERAL", "FLOAT", "ARITHMETIC", "LOGIC", "BOOLEAN", "SEPARATOR", "STRUCTFIELD", 
			"QUOTEMARK", "STRREF", "WS", "CHAR"
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
			setState(32);
			match(T__0);
			setState(33);
			match(LPAREN);
			setState(48);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(34);
				match(QUOTEMARK);
				setState(35);
				condition();
				setState(36);
				match(QUOTEMARK);
				setState(37);
				match(SEPARATOR);
				}
				break;
			case 2:
				{
				setState(39);
				param();
				setState(40);
				match(SEPARATOR);
				setState(41);
				match(QUOTEMARK);
				setState(42);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(43);
				match(QUOTEMARK);
				setState(44);
				match(SEPARATOR);
				setState(45);
				param();
				setState(46);
				match(SEPARATOR);
				}
				break;
			}
			setState(52);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(50);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(51);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(54);
			match(SEPARATOR);
			setState(57);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(55);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(56);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(59);
			match(RPAREN);
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(60);
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
		public List<StructContext> struct() {
			return getRuleContexts(StructContext.class);
		}
		public StructContext struct(int i) {
			return getRuleContext(StructContext.class,i);
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
			setState(63);
			match(T__1);
			setState(64);
			match(LPAREN);
			setState(65);
			codeBlock();
			setState(66);
			match(SEPARATOR);
			setState(74);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(67);
				string();
				}
				break;
			case 2:
				{
				setState(68);
				match(LITERAL);
				}
				break;
			case 3:
				{
				setState(69);
				expression();
				}
				break;
			case 4:
				{
				setState(70);
				match(DIGIT);
				}
				break;
			case 5:
				{
				setState(71);
				match(NUMBER);
				}
				break;
			case 6:
				{
				setState(72);
				functionFire();
				}
				break;
			case 7:
				{
				setState(73);
				struct();
				}
				break;
			}
			setState(76);
			match(SEPARATOR);
			setState(84);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(77);
				string();
				}
				break;
			case 2:
				{
				setState(78);
				match(LITERAL);
				}
				break;
			case 3:
				{
				setState(79);
				expression();
				}
				break;
			case 4:
				{
				setState(80);
				match(DIGIT);
				}
				break;
			case 5:
				{
				setState(81);
				match(NUMBER);
				}
				break;
			case 6:
				{
				setState(82);
				functionFire();
				}
				break;
			case 7:
				{
				setState(83);
				struct();
				}
				break;
			}
			setState(86);
			match(SEPARATOR);
			setState(94);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(87);
				string();
				}
				break;
			case 2:
				{
				setState(88);
				match(LITERAL);
				}
				break;
			case 3:
				{
				setState(89);
				expression();
				}
				break;
			case 4:
				{
				setState(90);
				match(DIGIT);
				}
				break;
			case 5:
				{
				setState(91);
				match(NUMBER);
				}
				break;
			case 6:
				{
				setState(92);
				functionFire();
				}
				break;
			case 7:
				{
				setState(93);
				struct();
				}
				break;
			}
			setState(96);
			match(RPAREN);
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(97);
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
			setState(100);
			match(T__2);
			setState(101);
			match(LPAREN);
			setState(102);
			param();
			setState(103);
			match(SEPARATOR);
			setState(104);
			match(QUOTEMARK);
			setState(105);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(106);
			match(QUOTEMARK);
			setState(107);
			match(SEPARATOR);
			setState(108);
			param();
			setState(109);
			match(SEPARATOR);
			setState(112);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(110);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(111);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(114);
			match(RPAREN);
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(115);
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
			setState(121);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(118);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(119);
				stringRef();
				}
				break;
			case 3:
				{
				setState(120);
				struct();
				}
				break;
			}
			setState(123);
			match(FIREFUNC);
			setState(124);
			match(LITERAL);
			setState(125);
			match(LPAREN);
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << FLOAT) | (1L << BOOLEAN) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(126);
					match(SEPARATOR);
					}
				}

				setState(129);
				param();
				}
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(135);
			match(RPAREN);
			setState(137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ENDINSTR) {
				{
				setState(136);
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
		public List<BehFireContext> behFire() {
			return getRuleContexts(BehFireContext.class);
		}
		public BehFireContext behFire(int i) {
			return getRuleContext(BehFireContext.class,i);
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
			setState(139);
			match(STARTCODE);
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << LITERAL) | (1L << STRREF))) != 0)) {
				{
				setState(146);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(140);
					functionFire();
					}
					break;
				case 2:
					{
					setState(141);
					ifInstr();
					}
					break;
				case 3:
					{
					setState(142);
					loopInstr();
					}
					break;
				case 4:
					{
					setState(143);
					whileInstr();
					}
					break;
				case 5:
					{
					setState(144);
					instr();
					}
					break;
				case 6:
					{
					setState(145);
					behFire();
					}
					break;
				}
				}
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(151);
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
		public List<ModuloContext> modulo() {
			return getRuleContexts(ModuloContext.class);
		}
		public ModuloContext modulo(int i) {
			return getRuleContext(ModuloContext.class,i);
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
			setState(153);
			match(STARTEXPR);
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(155);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(154);
					match(ARITHMETIC);
					}
				}

				setState(167);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(157);
					match(LITERAL);
					}
					break;
				case 2:
					{
					setState(158);
					string();
					}
					break;
				case 3:
					{
					setState(159);
					match(DIGIT);
					}
					break;
				case 4:
					{
					setState(160);
					match(NUMBER);
					}
					break;
				case 5:
					{
					setState(161);
					match(ITERATOR);
					}
					break;
				case 6:
					{
					setState(162);
					functionFire();
					}
					break;
				case 7:
					{
					setState(163);
					expression();
					}
					break;
				case 8:
					{
					setState(164);
					struct();
					}
					break;
				case 9:
					{
					setState(165);
					stringRef();
					}
					break;
				case 10:
					{
					setState(166);
					modulo();
					}
					break;
				}
				setState(170);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(169);
					match(ARITHMETIC);
					}
					break;
				}
				}
				}
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(177);
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
			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(185);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STARTCODE:
					{
					setState(179);
					codeBlock();
					}
					break;
				case STARTEXPR:
					{
					setState(180);
					expression();
					}
					break;
				case QUOTEMARK:
					{
					setState(181);
					string();
					}
					break;
				case T__0:
					{
					setState(182);
					ifInstr();
					}
					break;
				case T__1:
					{
					setState(183);
					loopInstr();
					}
					break;
				case T__2:
					{
					setState(184);
					whileInstr();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(189);
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
		public TerminalNode FLOAT() { return getToken(AidemMediaParser.FLOAT, 0); }
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
			setState(202);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(190);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(191);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(192);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(193);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(194);
				match(LITERAL);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(195);
				match(DIGIT);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(196);
				match(NUMBER);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(197);
				match(FLOAT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(198);
				expression();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(199);
				match(ITERATOR);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(200);
				struct();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(201);
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
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
			setState(208);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(204);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(205);
				functionFire();
				}
				break;
			case 3:
				{
				setState(206);
				struct();
				}
				break;
			case 4:
				{
				setState(207);
				expression();
				}
				break;
			}
			setState(210);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(211);
			param();
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LOGIC) {
				{
				setState(212);
				match(LOGIC);
				setState(213);
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

	public static class BehFireContext extends ParserRuleContext {
		public TerminalNode LITERAL() { return getToken(AidemMediaParser.LITERAL, 0); }
		public TerminalNode ENDINSTR() { return getToken(AidemMediaParser.ENDINSTR, 0); }
		public BehFireContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_behFire; }
	}

	public final BehFireContext behFire() throws RecognitionException {
		BehFireContext _localctx = new BehFireContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_behFire);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			match(LITERAL);
			setState(217);
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

	public static class ModuloContext extends ParserRuleContext {
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
		}
		public TerminalNode DIGIT() { return getToken(AidemMediaParser.DIGIT, 0); }
		public TerminalNode NUMBER() { return getToken(AidemMediaParser.NUMBER, 0); }
		public ModuloContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modulo; }
	}

	public final ModuloContext modulo() throws RecognitionException {
		ModuloContext _localctx = new ModuloContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_modulo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(LITERAL);
			setState(220);
			match(T__3);
			setState(221);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DIGIT) | (1L << NUMBER) | (1L << LITERAL))) != 0)) ) {
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
		enterRule(_localctx, 22, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			match(QUOTEMARK);
			setState(251);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				{
				setState(239); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(239);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
					case 1:
						{
						setState(224);
						match(LITERAL);
						}
						break;
					case 2:
						{
						setState(225);
						match(DIGIT);
						}
						break;
					case 3:
						{
						setState(226);
						match(NUMBER);
						}
						break;
					case 4:
						{
						setState(227);
						match(LSS);
						}
						break;
					case 5:
						{
						setState(228);
						match(LEQ);
						}
						break;
					case 6:
						{
						setState(229);
						match(GEQ);
						}
						break;
					case 7:
						{
						setState(230);
						match(GTR);
						}
						break;
					case 8:
						{
						setState(231);
						match(EQU);
						}
						break;
					case 9:
						{
						setState(232);
						match(NEQ);
						}
						break;
					case 10:
						{
						setState(233);
						match(SLASH);
						}
						break;
					case 11:
						{
						setState(234);
						struct();
						}
						break;
					case 12:
						{
						setState(235);
						match(LPAREN);
						}
						break;
					case 13:
						{
						setState(236);
						match(RPAREN);
						}
						break;
					case 14:
						{
						setState(237);
						match(SEPARATOR);
						}
						break;
					case 15:
						{
						setState(238);
						match(ARITHMETIC);
						}
						break;
					}
					}
					setState(241); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << DIGIT) | (1L << NUMBER) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(243);
				variable();
				setState(248);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(244);
					match(SLASH);
					setState(246);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(245);
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
				setState(250);
				string();
				}
				break;
			}
			setState(253);
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
		enterRule(_localctx, 24, RULE_instr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			match(T__3);
			setState(256);
			match(LITERAL);
			setState(257);
			match(LPAREN);
			setState(263);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIGIT) | (1L << NUMBER) | (1L << ITERATOR) | (1L << LITERAL) | (1L << FLOAT) | (1L << BOOLEAN) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(258);
				param();
				setState(261);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(259);
					match(SEPARATOR);
					setState(260);
					param();
					}
				}

				}
			}

			setState(265);
			match(RPAREN);
			setState(266);
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
		enterRule(_localctx, 26, RULE_stringRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(268);
			match(STRREF);
			setState(271);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(269);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(270);
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
		enterRule(_localctx, 28, RULE_struct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			match(LITERAL);
			setState(274);
			match(STRUCTFIELD);
			setState(275);
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
		enterRule(_localctx, 30, RULE_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(VARREF);
			setState(278);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3$\u011b\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\63\n\2\3\2"+
		"\3\2\5\2\67\n\2\3\2\3\2\3\2\5\2<\n\2\3\2\3\2\5\2@\n\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3M\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\5\3W\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3a\n\3\3\3\3\3\5\3e\n\3\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4s\n\4\3\4\3\4\5\4w\n"+
		"\4\3\5\3\5\3\5\5\5|\n\5\3\5\3\5\3\5\3\5\5\5\u0082\n\5\3\5\7\5\u0085\n"+
		"\5\f\5\16\5\u0088\13\5\3\5\3\5\5\5\u008c\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\7\6\u0095\n\6\f\6\16\6\u0098\13\6\3\6\3\6\3\7\3\7\5\7\u009e\n\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00aa\n\7\3\7\5\7\u00ad\n\7\7"+
		"\7\u00af\n\7\f\7\16\7\u00b2\13\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\7\b\u00bc"+
		"\n\b\f\b\16\b\u00bf\13\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\5\t\u00cd\n\t\3\n\3\n\3\n\3\n\5\n\u00d3\n\n\3\n\3\n\3\n\3\n\5\n\u00d9"+
		"\n\n\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\6\r\u00f2\n\r\r\r\16\r\u00f3\3\r\3\r\3"+
		"\r\5\r\u00f9\n\r\5\r\u00fb\n\r\3\r\5\r\u00fe\n\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\5\16\u0108\n\16\5\16\u010a\n\16\3\16\3\16\3\16\3\17\3"+
		"\17\3\17\5\17\u0112\n\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\2\2\22"+
		"\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \2\5\3\2\17\24\4\2\27\30\32\32"+
		"\4\2\27\27\32\32\2\u0166\2\"\3\2\2\2\4A\3\2\2\2\6f\3\2\2\2\b{\3\2\2\2"+
		"\n\u008d\3\2\2\2\f\u009b\3\2\2\2\16\u00bd\3\2\2\2\20\u00cc\3\2\2\2\22"+
		"\u00d2\3\2\2\2\24\u00da\3\2\2\2\26\u00dd\3\2\2\2\30\u00e1\3\2\2\2\32\u0101"+
		"\3\2\2\2\34\u010e\3\2\2\2\36\u0113\3\2\2\2 \u0117\3\2\2\2\"#\7\3\2\2#"+
		"\62\7\7\2\2$%\7!\2\2%&\5\22\n\2&\'\7!\2\2\'(\7\37\2\2(\63\3\2\2\2)*\5"+
		"\20\t\2*+\7\37\2\2+,\7!\2\2,-\t\2\2\2-.\7!\2\2./\7\37\2\2/\60\5\20\t\2"+
		"\60\61\7\37\2\2\61\63\3\2\2\2\62$\3\2\2\2\62)\3\2\2\2\63\66\3\2\2\2\64"+
		"\67\5\n\6\2\65\67\5\30\r\2\66\64\3\2\2\2\66\65\3\2\2\2\678\3\2\2\28;\7"+
		"\37\2\29<\5\n\6\2:<\5\30\r\2;9\3\2\2\2;:\3\2\2\2<=\3\2\2\2=?\7\b\2\2>"+
		"@\7\25\2\2?>\3\2\2\2?@\3\2\2\2@\3\3\2\2\2AB\7\4\2\2BC\7\7\2\2CD\5\n\6"+
		"\2DL\7\37\2\2EM\5\30\r\2FM\7\32\2\2GM\5\f\7\2HM\7\27\2\2IM\7\30\2\2JM"+
		"\5\b\5\2KM\5\36\20\2LE\3\2\2\2LF\3\2\2\2LG\3\2\2\2LH\3\2\2\2LI\3\2\2\2"+
		"LJ\3\2\2\2LK\3\2\2\2MN\3\2\2\2NV\7\37\2\2OW\5\30\r\2PW\7\32\2\2QW\5\f"+
		"\7\2RW\7\27\2\2SW\7\30\2\2TW\5\b\5\2UW\5\36\20\2VO\3\2\2\2VP\3\2\2\2V"+
		"Q\3\2\2\2VR\3\2\2\2VS\3\2\2\2VT\3\2\2\2VU\3\2\2\2WX\3\2\2\2X`\7\37\2\2"+
		"Ya\5\30\r\2Za\7\32\2\2[a\5\f\7\2\\a\7\27\2\2]a\7\30\2\2^a\5\b\5\2_a\5"+
		"\36\20\2`Y\3\2\2\2`Z\3\2\2\2`[\3\2\2\2`\\\3\2\2\2`]\3\2\2\2`^\3\2\2\2"+
		"`_\3\2\2\2ab\3\2\2\2bd\7\b\2\2ce\7\25\2\2dc\3\2\2\2de\3\2\2\2e\5\3\2\2"+
		"\2fg\7\5\2\2gh\7\7\2\2hi\5\20\t\2ij\7\37\2\2jk\7!\2\2kl\t\2\2\2lm\7!\2"+
		"\2mn\7\37\2\2no\5\20\t\2or\7\37\2\2ps\5\n\6\2qs\5\30\r\2rp\3\2\2\2rq\3"+
		"\2\2\2st\3\2\2\2tv\7\b\2\2uw\7\25\2\2vu\3\2\2\2vw\3\2\2\2w\7\3\2\2\2x"+
		"|\7\32\2\2y|\5\34\17\2z|\5\36\20\2{x\3\2\2\2{y\3\2\2\2{z\3\2\2\2|}\3\2"+
		"\2\2}~\7\26\2\2~\177\7\32\2\2\177\u0086\7\7\2\2\u0080\u0082\7\37\2\2\u0081"+
		"\u0080\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\5\20"+
		"\t\2\u0084\u0081\3\2\2\2\u0085\u0088\3\2\2\2\u0086\u0084\3\2\2\2\u0086"+
		"\u0087\3\2\2\2\u0087\u0089\3\2\2\2\u0088\u0086\3\2\2\2\u0089\u008b\7\b"+
		"\2\2\u008a\u008c\7\25\2\2\u008b\u008a\3\2\2\2\u008b\u008c\3\2\2\2\u008c"+
		"\t\3\2\2\2\u008d\u0096\7\n\2\2\u008e\u0095\5\b\5\2\u008f\u0095\5\2\2\2"+
		"\u0090\u0095\5\4\3\2\u0091\u0095\5\6\4\2\u0092\u0095\5\32\16\2\u0093\u0095"+
		"\5\24\13\2\u0094\u008e\3\2\2\2\u0094\u008f\3\2\2\2\u0094\u0090\3\2\2\2"+
		"\u0094\u0091\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0093\3\2\2\2\u0095\u0098"+
		"\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0099\3\2\2\2\u0098"+
		"\u0096\3\2\2\2\u0099\u009a\7\13\2\2\u009a\13\3\2\2\2\u009b\u00b0\7\f\2"+
		"\2\u009c\u009e\7\34\2\2\u009d\u009c\3\2\2\2\u009d\u009e\3\2\2\2\u009e"+
		"\u00a9\3\2\2\2\u009f\u00aa\7\32\2\2\u00a0\u00aa\5\30\r\2\u00a1\u00aa\7"+
		"\27\2\2\u00a2\u00aa\7\30\2\2\u00a3\u00aa\7\31\2\2\u00a4\u00aa\5\b\5\2"+
		"\u00a5\u00aa\5\f\7\2\u00a6\u00aa\5\36\20\2\u00a7\u00aa\5\34\17\2\u00a8"+
		"\u00aa\5\26\f\2\u00a9\u009f\3\2\2\2\u00a9\u00a0\3\2\2\2\u00a9\u00a1\3"+
		"\2\2\2\u00a9\u00a2\3\2\2\2\u00a9\u00a3\3\2\2\2\u00a9\u00a4\3\2\2\2\u00a9"+
		"\u00a5\3\2\2\2\u00a9\u00a6\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00a8\3\2"+
		"\2\2\u00aa\u00ac\3\2\2\2\u00ab\u00ad\7\34\2\2\u00ac\u00ab\3\2\2\2\u00ac"+
		"\u00ad\3\2\2\2\u00ad\u00af\3\2\2\2\u00ae\u009d\3\2\2\2\u00af\u00b2\3\2"+
		"\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b3\3\2\2\2\u00b2"+
		"\u00b0\3\2\2\2\u00b3\u00b4\7\r\2\2\u00b4\r\3\2\2\2\u00b5\u00bc\5\n\6\2"+
		"\u00b6\u00bc\5\f\7\2\u00b7\u00bc\5\30\r\2\u00b8\u00bc\5\2\2\2\u00b9\u00bc"+
		"\5\4\3\2\u00ba\u00bc\5\6\4\2\u00bb\u00b5\3\2\2\2\u00bb\u00b6\3\2\2\2\u00bb"+
		"\u00b7\3\2\2\2\u00bb\u00b8\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00ba\3\2"+
		"\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be"+
		"\17\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00cd\5\b\5\2\u00c1\u00cd\7\36\2"+
		"\2\u00c2\u00cd\5 \21\2\u00c3\u00cd\5\30\r\2\u00c4\u00cd\7\32\2\2\u00c5"+
		"\u00cd\7\27\2\2\u00c6\u00cd\7\30\2\2\u00c7\u00cd\7\33\2\2\u00c8\u00cd"+
		"\5\f\7\2\u00c9\u00cd\7\31\2\2\u00ca\u00cd\5\36\20\2\u00cb\u00cd\5\34\17"+
		"\2\u00cc\u00c0\3\2\2\2\u00cc\u00c1\3\2\2\2\u00cc\u00c2\3\2\2\2\u00cc\u00c3"+
		"\3\2\2\2\u00cc\u00c4\3\2\2\2\u00cc\u00c5\3\2\2\2\u00cc\u00c6\3\2\2\2\u00cc"+
		"\u00c7\3\2\2\2\u00cc\u00c8\3\2\2\2\u00cc\u00c9\3\2\2\2\u00cc\u00ca\3\2"+
		"\2\2\u00cc\u00cb\3\2\2\2\u00cd\21\3\2\2\2\u00ce\u00d3\7\32\2\2\u00cf\u00d3"+
		"\5\b\5\2\u00d0\u00d3\5\36\20\2\u00d1\u00d3\5\f\7\2\u00d2\u00ce\3\2\2\2"+
		"\u00d2\u00cf\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d2\u00d1\3\2\2\2\u00d3\u00d4"+
		"\3\2\2\2\u00d4\u00d5\t\2\2\2\u00d5\u00d8\5\20\t\2\u00d6\u00d7\7\35\2\2"+
		"\u00d7\u00d9\5\22\n\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\23"+
		"\3\2\2\2\u00da\u00db\7\32\2\2\u00db\u00dc\7\25\2\2\u00dc\25\3\2\2\2\u00dd"+
		"\u00de\7\32\2\2\u00de\u00df\7\6\2\2\u00df\u00e0\t\3\2\2\u00e0\27\3\2\2"+
		"\2\u00e1\u00fd\7!\2\2\u00e2\u00f2\7\32\2\2\u00e3\u00f2\7\27\2\2\u00e4"+
		"\u00f2\7\30\2\2\u00e5\u00f2\7\17\2\2\u00e6\u00f2\7\20\2\2\u00e7\u00f2"+
		"\7\21\2\2\u00e8\u00f2\7\22\2\2\u00e9\u00f2\7\23\2\2\u00ea\u00f2\7\24\2"+
		"\2\u00eb\u00f2\7\16\2\2\u00ec\u00f2\5\36\20\2\u00ed\u00f2\7\7\2\2\u00ee"+
		"\u00f2\7\b\2\2\u00ef\u00f2\7\37\2\2\u00f0\u00f2\7\34\2\2\u00f1\u00e2\3"+
		"\2\2\2\u00f1\u00e3\3\2\2\2\u00f1\u00e4\3\2\2\2\u00f1\u00e5\3\2\2\2\u00f1"+
		"\u00e6\3\2\2\2\u00f1\u00e7\3\2\2\2\u00f1\u00e8\3\2\2\2\u00f1\u00e9\3\2"+
		"\2\2\u00f1\u00ea\3\2\2\2\u00f1\u00eb\3\2\2\2\u00f1\u00ec\3\2\2\2\u00f1"+
		"\u00ed\3\2\2\2\u00f1\u00ee\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f1\u00f0\3\2"+
		"\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4"+
		"\u00fe\3\2\2\2\u00f5\u00fa\5 \21\2\u00f6\u00f8\7\16\2\2\u00f7\u00f9\7"+
		"\32\2\2\u00f8\u00f7\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fb\3\2\2\2\u00fa"+
		"\u00f6\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb\u00fe\3\2\2\2\u00fc\u00fe\5\30"+
		"\r\2\u00fd\u00f1\3\2\2\2\u00fd\u00f5\3\2\2\2\u00fd\u00fc\3\2\2\2\u00fd"+
		"\u00fe\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0100\7!\2\2\u0100\31\3\2\2\2"+
		"\u0101\u0102\7\6\2\2\u0102\u0103\7\32\2\2\u0103\u0109\7\7\2\2\u0104\u0107"+
		"\5\20\t\2\u0105\u0106\7\37\2\2\u0106\u0108\5\20\t\2\u0107\u0105\3\2\2"+
		"\2\u0107\u0108\3\2\2\2\u0108\u010a\3\2\2\2\u0109\u0104\3\2\2\2\u0109\u010a"+
		"\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010c\7\b\2\2\u010c\u010d\7\25\2\2"+
		"\u010d\33\3\2\2\2\u010e\u0111\7\"\2\2\u010f\u0112\5\f\7\2\u0110\u0112"+
		"\7\32\2\2\u0111\u010f\3\2\2\2\u0111\u0110\3\2\2\2\u0112\35\3\2\2\2\u0113"+
		"\u0114\7\32\2\2\u0114\u0115\7 \2\2\u0115\u0116\7\32\2\2\u0116\37\3\2\2"+
		"\2\u0117\u0118\7\t\2\2\u0118\u0119\t\4\2\2\u0119!\3\2\2\2#\62\66;?LV`"+
		"drv{\u0081\u0086\u008b\u0094\u0096\u009d\u00a9\u00ac\u00b0\u00bb\u00bd"+
		"\u00cc\u00d2\u00d8\u00f1\u00f3\u00f8\u00fa\u00fd\u0107\u0109\u0111";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}