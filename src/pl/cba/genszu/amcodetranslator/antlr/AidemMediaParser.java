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
		T__0=1, T__1=2, T__2=3, LPAREN=4, RPAREN=5, VARREF=6, STARTCODE=7, STOPCODE=8, 
		STARTEXPR=9, STOPEXPR=10, SLASH=11, LSS=12, LEQ=13, GEQ=14, GTR=15, EQU=16, 
		NEQ=17, ENDINSTR=18, FIREFUNC=19, DIV=20, MOD=21, NUMBER=22, FLOAT=23, 
		DOT=24, ITERATOR=25, LITERAL=26, ARITHMETIC=27, LOGIC=28, BOOLEAN=29, 
		SEPARATOR=30, STRUCTFIELD=31, QUOTEMARK=32, STRREF=33, WS=34, CHAR=35;
	public static final int
		RULE_ifInstr = 0, RULE_loopInstr = 1, RULE_whileInstr = 2, RULE_functionFire = 3, 
		RULE_codeBlock = 4, RULE_expression = 5, RULE_script = 6, RULE_param = 7, 
		RULE_condition = 8, RULE_conditionPart = 9, RULE_behFire = 10, RULE_modulo = 11, 
		RULE_string = 12, RULE_instr = 13, RULE_stringRef = 14, RULE_struct = 15, 
		RULE_variable = 16;
	private static String[] makeRuleNames() {
		return new String[] {
			"ifInstr", "loopInstr", "whileInstr", "functionFire", "codeBlock", "expression", 
			"script", "param", "condition", "conditionPart", "behFire", "modulo", 
			"string", "instr", "stringRef", "struct", "variable"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@IF'", "'@LOOP'", "'@WHILE'", "'('", "')'", "'$'", null, null, 
			"'['", "']'", "'\\'", "'<'", null, null, "'>'", null, null, "';'", "'^'", 
			"'@'", "'%'", null, null, "'.'", "'_I_'", null, null, null, null, "','", 
			"'|'", "'\"'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "LPAREN", "RPAREN", "VARREF", "STARTCODE", "STOPCODE", 
			"STARTEXPR", "STOPEXPR", "SLASH", "LSS", "LEQ", "GEQ", "GTR", "EQU", 
			"NEQ", "ENDINSTR", "FIREFUNC", "DIV", "MOD", "NUMBER", "FLOAT", "DOT", 
			"ITERATOR", "LITERAL", "ARITHMETIC", "LOGIC", "BOOLEAN", "SEPARATOR", 
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
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
		}
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
			setState(34);
			match(T__0);
			setState(35);
			match(LPAREN);
			setState(50);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(36);
				match(QUOTEMARK);
				setState(37);
				condition();
				setState(38);
				match(QUOTEMARK);
				setState(39);
				match(SEPARATOR);
				}
				break;
			case 2:
				{
				setState(41);
				param();
				setState(42);
				match(SEPARATOR);
				setState(43);
				match(QUOTEMARK);
				setState(44);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(45);
				match(QUOTEMARK);
				setState(46);
				match(SEPARATOR);
				setState(47);
				param();
				setState(48);
				match(SEPARATOR);
				}
				break;
			}
			setState(54);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(52);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(53);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(56);
			match(SEPARATOR);
			setState(59);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(57);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(58);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(61);
			match(RPAREN);
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(62);
				match(ENDINSTR);
				}
				}
				setState(67);
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
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
		}
		public List<TerminalNode> ARITHMETIC() { return getTokens(AidemMediaParser.ARITHMETIC); }
		public TerminalNode ARITHMETIC(int i) {
			return getToken(AidemMediaParser.ARITHMETIC, i);
		}
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
			setState(68);
			match(T__1);
			setState(69);
			match(LPAREN);
			setState(70);
			codeBlock();
			setState(71);
			match(SEPARATOR);
			setState(81);
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
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(75);
					match(ARITHMETIC);
					}
				}

				setState(78);
				match(NUMBER);
				}
				break;
			case 5:
				{
				setState(79);
				functionFire();
				}
				break;
			case 6:
				{
				setState(80);
				struct();
				}
				break;
			}
			setState(83);
			match(SEPARATOR);
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(84);
				string();
				}
				break;
			case 2:
				{
				setState(85);
				match(LITERAL);
				}
				break;
			case 3:
				{
				setState(86);
				expression();
				}
				break;
			case 4:
				{
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(87);
					match(ARITHMETIC);
					}
				}

				setState(90);
				match(NUMBER);
				}
				break;
			case 5:
				{
				setState(91);
				functionFire();
				}
				break;
			case 6:
				{
				setState(92);
				struct();
				}
				break;
			}
			setState(95);
			match(SEPARATOR);
			setState(105);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(96);
				string();
				}
				break;
			case 2:
				{
				setState(97);
				match(LITERAL);
				}
				break;
			case 3:
				{
				setState(98);
				expression();
				}
				break;
			case 4:
				{
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(99);
					match(ARITHMETIC);
					}
				}

				setState(102);
				match(NUMBER);
				}
				break;
			case 5:
				{
				setState(103);
				functionFire();
				}
				break;
			case 6:
				{
				setState(104);
				struct();
				}
				break;
			}
			setState(107);
			match(RPAREN);
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(108);
				match(ENDINSTR);
				}
				}
				setState(113);
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
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
		}
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
			setState(114);
			match(T__2);
			setState(115);
			match(LPAREN);
			setState(116);
			param();
			setState(117);
			match(SEPARATOR);
			setState(118);
			match(QUOTEMARK);
			setState(119);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(120);
			match(QUOTEMARK);
			setState(121);
			match(SEPARATOR);
			setState(122);
			param();
			setState(123);
			match(SEPARATOR);
			setState(126);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(124);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(125);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(128);
			match(RPAREN);
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(129);
				match(ENDINSTR);
				}
				}
				setState(134);
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

	public static class FunctionFireContext extends ParserRuleContext {
		public TerminalNode FIREFUNC() { return getToken(AidemMediaParser.FIREFUNC, 0); }
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
		}
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public TerminalNode ITERATOR() { return getToken(AidemMediaParser.ITERATOR, 0); }
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
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
		}
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
			setState(139);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(135);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(136);
				match(ITERATOR);
				}
				break;
			case 3:
				{
				setState(137);
				stringRef();
				}
				break;
			case 4:
				{
				setState(138);
				struct();
				}
				break;
			}
			setState(141);
			match(FIREFUNC);
			setState(142);
			match(LITERAL);
			setState(143);
			match(LPAREN);
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << BOOLEAN) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(144);
					match(SEPARATOR);
					}
				}

				setState(147);
				param();
				}
				}
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(153);
			match(RPAREN);
			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(154);
				match(ENDINSTR);
				}
				}
				setState(159);
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
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
		}
		public List<TerminalNode> FLOAT() { return getTokens(AidemMediaParser.FLOAT); }
		public TerminalNode FLOAT(int i) {
			return getToken(AidemMediaParser.FLOAT, i);
		}
		public List<TerminalNode> NUMBER() { return getTokens(AidemMediaParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(AidemMediaParser.NUMBER, i);
		}
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
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
			setState(160);
			match(STARTCODE);
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << STRREF))) != 0)) {
				{
				setState(175);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(161);
					functionFire();
					}
					break;
				case 2:
					{
					setState(162);
					ifInstr();
					}
					break;
				case 3:
					{
					setState(163);
					loopInstr();
					}
					break;
				case 4:
					{
					setState(164);
					whileInstr();
					}
					break;
				case 5:
					{
					setState(165);
					instr();
					}
					break;
				case 6:
					{
					setState(166);
					behFire();
					}
					break;
				case 7:
					{
					setState(167);
					expression();
					}
					break;
				case 8:
					{
					setState(168);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NUMBER) | (1L << FLOAT) | (1L << LITERAL))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(172);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==ENDINSTR) {
						{
						{
						setState(169);
						match(ENDINSTR);
						}
						}
						setState(174);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					break;
				}
				}
				setState(179);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(180);
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
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
		}
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
		public List<TerminalNode> NUMBER() { return getTokens(AidemMediaParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(AidemMediaParser.NUMBER, i);
		}
		public List<TerminalNode> FLOAT() { return getTokens(AidemMediaParser.FLOAT); }
		public TerminalNode FLOAT(int i) {
			return getToken(AidemMediaParser.FLOAT, i);
		}
		public List<ModuloContext> modulo() {
			return getRuleContexts(ModuloContext.class);
		}
		public ModuloContext modulo(int i) {
			return getRuleContext(ModuloContext.class,i);
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
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
		}
		public List<TerminalNode> ARITHMETIC() { return getTokens(AidemMediaParser.ARITHMETIC); }
		public TerminalNode ARITHMETIC(int i) {
			return getToken(AidemMediaParser.ARITHMETIC, i);
		}
		public List<TerminalNode> DIV() { return getTokens(AidemMediaParser.DIV); }
		public TerminalNode DIV(int i) {
			return getToken(AidemMediaParser.DIV, i);
		}
		public List<TerminalNode> STRREF() { return getTokens(AidemMediaParser.STRREF); }
		public TerminalNode STRREF(int i) {
			return getToken(AidemMediaParser.STRREF, i);
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
			setState(182);
			match(STARTEXPR);
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(184);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
				case 1:
					{
					setState(183);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DIV) | (1L << ARITHMETIC) | (1L << STRREF))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(203);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
				case 1:
					{
					setState(186);
					match(LITERAL);
					}
					break;
				case 2:
					{
					setState(187);
					string();
					}
					break;
				case 3:
					{
					setState(189);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(188);
						match(ARITHMETIC);
						}
					}

					setState(191);
					match(NUMBER);
					}
					break;
				case 4:
					{
					setState(193);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(192);
						match(ARITHMETIC);
						}
					}

					setState(195);
					match(FLOAT);
					}
					break;
				case 5:
					{
					setState(196);
					modulo();
					}
					break;
				case 6:
					{
					setState(197);
					match(ITERATOR);
					}
					break;
				case 7:
					{
					setState(198);
					functionFire();
					}
					break;
				case 8:
					{
					setState(199);
					expression();
					}
					break;
				case 9:
					{
					setState(200);
					struct();
					}
					break;
				case 10:
					{
					setState(201);
					stringRef();
					}
					break;
				case 11:
					{
					setState(202);
					variable();
					}
					break;
				}
				setState(206);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(205);
					_la = _input.LA(1);
					if ( !(_la==ARITHMETIC || _la==STRREF) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				}
				}
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(213);
			match(STOPEXPR);
			setState(217);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(214);
				match(ENDINSTR);
				}
				}
				setState(219);
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
			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(227);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
				case 1:
					{
					setState(220);
					codeBlock();
					}
					break;
				case 2:
					{
					setState(221);
					expression();
					}
					break;
				case 3:
					{
					setState(222);
					string();
					}
					break;
				case 4:
					{
					setState(223);
					ifInstr();
					}
					break;
				case 5:
					{
					setState(224);
					loopInstr();
					}
					break;
				case 6:
					{
					setState(225);
					whileInstr();
					}
					break;
				case 7:
					{
					setState(226);
					string();
					}
					break;
				}
				}
				setState(231);
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
		public TerminalNode NUMBER() { return getToken(AidemMediaParser.NUMBER, 0); }
		public TerminalNode ARITHMETIC() { return getToken(AidemMediaParser.ARITHMETIC, 0); }
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
		int _la;
		try {
			setState(249);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(232);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(233);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(234);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(235);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(236);
				match(LITERAL);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(238);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(237);
					match(ARITHMETIC);
					}
				}

				setState(240);
				match(NUMBER);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(242);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(241);
					match(ARITHMETIC);
					}
				}

				setState(244);
				match(FLOAT);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(245);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(246);
				match(ITERATOR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(247);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(248);
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
		public List<ConditionPartContext> conditionPart() {
			return getRuleContexts(ConditionPartContext.class);
		}
		public ConditionPartContext conditionPart(int i) {
			return getRuleContext(ConditionPartContext.class,i);
		}
		public List<TerminalNode> LOGIC() { return getTokens(AidemMediaParser.LOGIC); }
		public TerminalNode LOGIC(int i) {
			return getToken(AidemMediaParser.LOGIC, i);
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
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTEXPR) | (1L << ITERATOR) | (1L << LITERAL) | (1L << LOGIC) | (1L << STRREF))) != 0)) {
				{
				{
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOGIC) {
					{
					setState(251);
					match(LOGIC);
					}
				}

				setState(254);
				conditionPart();
				}
				}
				setState(259);
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

	public static class ConditionPartContext extends ParserRuleContext {
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
		public TerminalNode ITERATOR() { return getToken(AidemMediaParser.ITERATOR, 0); }
		public ConditionPartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionPart; }
	}

	public final ConditionPartContext conditionPart() throws RecognitionException {
		ConditionPartContext _localctx = new ConditionPartContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_conditionPart);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				{
				setState(260);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(261);
				functionFire();
				}
				break;
			case 3:
				{
				setState(262);
				struct();
				}
				break;
			case 4:
				{
				setState(263);
				expression();
				}
				break;
			case 5:
				{
				setState(264);
				match(ITERATOR);
				}
				break;
			}
			setState(267);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(268);
			param();
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
		enterRule(_localctx, 20, RULE_behFire);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(270);
			match(LITERAL);
			setState(271);
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
		public TerminalNode MOD() { return getToken(AidemMediaParser.MOD, 0); }
		public List<TerminalNode> LITERAL() { return getTokens(AidemMediaParser.LITERAL); }
		public TerminalNode LITERAL(int i) {
			return getToken(AidemMediaParser.LITERAL, i);
		}
		public TerminalNode ITERATOR() { return getToken(AidemMediaParser.ITERATOR, 0); }
		public List<TerminalNode> NUMBER() { return getTokens(AidemMediaParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(AidemMediaParser.NUMBER, i);
		}
		public List<TerminalNode> FLOAT() { return getTokens(AidemMediaParser.FLOAT); }
		public TerminalNode FLOAT(int i) {
			return getToken(AidemMediaParser.FLOAT, i);
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
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> ARITHMETIC() { return getTokens(AidemMediaParser.ARITHMETIC); }
		public TerminalNode ARITHMETIC(int i) {
			return getToken(AidemMediaParser.ARITHMETIC, i);
		}
		public ModuloContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modulo; }
	}

	public final ModuloContext modulo() throws RecognitionException {
		ModuloContext _localctx = new ModuloContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_modulo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(286);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(273);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(274);
				match(ITERATOR);
				}
				break;
			case 3:
				{
				setState(276);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(275);
					match(ARITHMETIC);
					}
				}

				setState(278);
				match(NUMBER);
				}
				break;
			case 4:
				{
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(279);
					match(ARITHMETIC);
					}
				}

				setState(282);
				match(FLOAT);
				}
				break;
			case 5:
				{
				setState(283);
				functionFire();
				}
				break;
			case 6:
				{
				setState(284);
				struct();
				}
				break;
			case 7:
				{
				setState(285);
				expression();
				}
				break;
			}
			setState(288);
			match(MOD);
			setState(301);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(289);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(291);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(290);
					match(ARITHMETIC);
					}
				}

				setState(293);
				match(NUMBER);
				}
				break;
			case 3:
				{
				setState(295);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(294);
					match(ARITHMETIC);
					}
				}

				setState(297);
				match(FLOAT);
				}
				break;
			case 4:
				{
				setState(298);
				functionFire();
				}
				break;
			case 5:
				{
				setState(299);
				struct();
				}
				break;
			case 6:
				{
				setState(300);
				expression();
				}
				break;
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
		public List<TerminalNode> FIREFUNC() { return getTokens(AidemMediaParser.FIREFUNC); }
		public TerminalNode FIREFUNC(int i) {
			return getToken(AidemMediaParser.FIREFUNC, i);
		}
		public List<TerminalNode> NUMBER() { return getTokens(AidemMediaParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(AidemMediaParser.NUMBER, i);
		}
		public List<TerminalNode> FLOAT() { return getTokens(AidemMediaParser.FLOAT); }
		public TerminalNode FLOAT(int i) {
			return getToken(AidemMediaParser.FLOAT, i);
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
		public List<TerminalNode> VARREF() { return getTokens(AidemMediaParser.VARREF); }
		public TerminalNode VARREF(int i) {
			return getToken(AidemMediaParser.VARREF, i);
		}
		public List<TerminalNode> ITERATOR() { return getTokens(AidemMediaParser.ITERATOR); }
		public TerminalNode ITERATOR(int i) {
			return getToken(AidemMediaParser.ITERATOR, i);
		}
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			match(QUOTEMARK);
			setState(340);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				{
				setState(328); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(328);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
					case 1:
						{
						setState(304);
						match(LITERAL);
						}
						break;
					case 2:
						{
						setState(305);
						match(FIREFUNC);
						}
						break;
					case 3:
						{
						setState(307);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(306);
							match(ARITHMETIC);
							}
						}

						setState(309);
						match(NUMBER);
						}
						break;
					case 4:
						{
						setState(311);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(310);
							match(ARITHMETIC);
							}
						}

						setState(313);
						match(FLOAT);
						}
						break;
					case 5:
						{
						setState(314);
						match(LSS);
						}
						break;
					case 6:
						{
						setState(315);
						match(LEQ);
						}
						break;
					case 7:
						{
						setState(316);
						match(GEQ);
						}
						break;
					case 8:
						{
						setState(317);
						match(GTR);
						}
						break;
					case 9:
						{
						setState(318);
						match(EQU);
						}
						break;
					case 10:
						{
						setState(319);
						match(NEQ);
						}
						break;
					case 11:
						{
						setState(320);
						match(SLASH);
						}
						break;
					case 12:
						{
						setState(321);
						struct();
						}
						break;
					case 13:
						{
						setState(322);
						match(LPAREN);
						}
						break;
					case 14:
						{
						setState(323);
						match(RPAREN);
						}
						break;
					case 15:
						{
						setState(324);
						match(SEPARATOR);
						}
						break;
					case 16:
						{
						setState(325);
						match(ARITHMETIC);
						}
						break;
					case 17:
						{
						setState(326);
						match(VARREF);
						}
						break;
					case 18:
						{
						setState(327);
						match(ITERATOR);
						}
						break;
					}
					}
					setState(330); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << VARREF) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << FIREFUNC) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(332);
				variable();
				setState(337);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(333);
					match(SLASH);
					setState(335);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(334);
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
				setState(339);
				string();
				}
				break;
			}
			setState(342);
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
		public TerminalNode DIV() { return getToken(AidemMediaParser.DIV, 0); }
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
		enterRule(_localctx, 26, RULE_instr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			match(DIV);
			setState(345);
			match(LITERAL);
			setState(346);
			match(LPAREN);
			setState(352);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << BOOLEAN) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(347);
				param();
				setState(350);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(348);
					match(SEPARATOR);
					setState(349);
					param();
					}
				}

				}
			}

			setState(354);
			match(RPAREN);
			setState(355);
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
		enterRule(_localctx, 28, RULE_stringRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(357);
			match(STRREF);
			setState(360);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(358);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(359);
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
		enterRule(_localctx, 30, RULE_struct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			match(LITERAL);
			setState(363);
			match(STRUCTFIELD);
			setState(364);
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
		public TerminalNode NUMBER() { return getToken(AidemMediaParser.NUMBER, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(366);
			match(VARREF);
			setState(367);
			_la = _input.LA(1);
			if ( !(_la==NUMBER || _la==LITERAL) ) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u0174\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\65"+
		"\n\2\3\2\3\2\5\29\n\2\3\2\3\2\3\2\5\2>\n\2\3\2\3\2\7\2B\n\2\f\2\16\2E"+
		"\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3O\n\3\3\3\3\3\3\3\5\3T\n\3\3"+
		"\3\3\3\3\3\3\3\3\3\5\3[\n\3\3\3\3\3\3\3\5\3`\n\3\3\3\3\3\3\3\3\3\3\3\5"+
		"\3g\n\3\3\3\3\3\3\3\5\3l\n\3\3\3\3\3\7\3p\n\3\f\3\16\3s\13\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u0081\n\4\3\4\3\4\7\4\u0085"+
		"\n\4\f\4\16\4\u0088\13\4\3\5\3\5\3\5\3\5\5\5\u008e\n\5\3\5\3\5\3\5\3\5"+
		"\5\5\u0094\n\5\3\5\7\5\u0097\n\5\f\5\16\5\u009a\13\5\3\5\3\5\7\5\u009e"+
		"\n\5\f\5\16\5\u00a1\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6\u00ad"+
		"\n\6\f\6\16\6\u00b0\13\6\7\6\u00b2\n\6\f\6\16\6\u00b5\13\6\3\6\3\6\3\7"+
		"\3\7\5\7\u00bb\n\7\3\7\3\7\3\7\5\7\u00c0\n\7\3\7\3\7\5\7\u00c4\n\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00ce\n\7\3\7\5\7\u00d1\n\7\7\7\u00d3"+
		"\n\7\f\7\16\7\u00d6\13\7\3\7\3\7\7\7\u00da\n\7\f\7\16\7\u00dd\13\7\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\7\b\u00e6\n\b\f\b\16\b\u00e9\13\b\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\5\t\u00f1\n\t\3\t\3\t\5\t\u00f5\n\t\3\t\3\t\3\t\3\t\3\t\5"+
		"\t\u00fc\n\t\3\n\5\n\u00ff\n\n\3\n\7\n\u0102\n\n\f\n\16\n\u0105\13\n\3"+
		"\13\3\13\3\13\3\13\3\13\5\13\u010c\n\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r"+
		"\3\r\3\r\5\r\u0117\n\r\3\r\3\r\5\r\u011b\n\r\3\r\3\r\3\r\3\r\5\r\u0121"+
		"\n\r\3\r\3\r\3\r\5\r\u0126\n\r\3\r\3\r\5\r\u012a\n\r\3\r\3\r\3\r\3\r\5"+
		"\r\u0130\n\r\3\16\3\16\3\16\3\16\5\16\u0136\n\16\3\16\3\16\5\16\u013a"+
		"\n\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\6\16\u014b\n\16\r\16\16\16\u014c\3\16\3\16\3\16\5\16\u0152"+
		"\n\16\5\16\u0154\n\16\3\16\5\16\u0157\n\16\3\16\3\16\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\5\17\u0161\n\17\5\17\u0163\n\17\3\17\3\17\3\17\3\20\3\20"+
		"\3\20\5\20\u016b\n\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\2\2\23\2"+
		"\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"\2\7\3\2\16\23\4\2\30\31\34\34"+
		"\5\2\26\26\35\35##\4\2\35\35##\4\2\30\30\34\34\2\u01de\2$\3\2\2\2\4F\3"+
		"\2\2\2\6t\3\2\2\2\b\u008d\3\2\2\2\n\u00a2\3\2\2\2\f\u00b8\3\2\2\2\16\u00e7"+
		"\3\2\2\2\20\u00fb\3\2\2\2\22\u0103\3\2\2\2\24\u010b\3\2\2\2\26\u0110\3"+
		"\2\2\2\30\u0120\3\2\2\2\32\u0131\3\2\2\2\34\u015a\3\2\2\2\36\u0167\3\2"+
		"\2\2 \u016c\3\2\2\2\"\u0170\3\2\2\2$%\7\3\2\2%\64\7\6\2\2&\'\7\"\2\2\'"+
		"(\5\22\n\2()\7\"\2\2)*\7 \2\2*\65\3\2\2\2+,\5\20\t\2,-\7 \2\2-.\7\"\2"+
		"\2./\t\2\2\2/\60\7\"\2\2\60\61\7 \2\2\61\62\5\20\t\2\62\63\7 \2\2\63\65"+
		"\3\2\2\2\64&\3\2\2\2\64+\3\2\2\2\658\3\2\2\2\669\5\n\6\2\679\5\32\16\2"+
		"8\66\3\2\2\28\67\3\2\2\29:\3\2\2\2:=\7 \2\2;>\5\n\6\2<>\5\32\16\2=;\3"+
		"\2\2\2=<\3\2\2\2>?\3\2\2\2?C\7\7\2\2@B\7\24\2\2A@\3\2\2\2BE\3\2\2\2CA"+
		"\3\2\2\2CD\3\2\2\2D\3\3\2\2\2EC\3\2\2\2FG\7\4\2\2GH\7\6\2\2HI\5\n\6\2"+
		"IS\7 \2\2JT\5\32\16\2KT\7\34\2\2LT\5\f\7\2MO\7\35\2\2NM\3\2\2\2NO\3\2"+
		"\2\2OP\3\2\2\2PT\7\30\2\2QT\5\b\5\2RT\5 \21\2SJ\3\2\2\2SK\3\2\2\2SL\3"+
		"\2\2\2SN\3\2\2\2SQ\3\2\2\2SR\3\2\2\2TU\3\2\2\2U_\7 \2\2V`\5\32\16\2W`"+
		"\7\34\2\2X`\5\f\7\2Y[\7\35\2\2ZY\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2\\`\7\30"+
		"\2\2]`\5\b\5\2^`\5 \21\2_V\3\2\2\2_W\3\2\2\2_X\3\2\2\2_Z\3\2\2\2_]\3\2"+
		"\2\2_^\3\2\2\2`a\3\2\2\2ak\7 \2\2bl\5\32\16\2cl\7\34\2\2dl\5\f\7\2eg\7"+
		"\35\2\2fe\3\2\2\2fg\3\2\2\2gh\3\2\2\2hl\7\30\2\2il\5\b\5\2jl\5 \21\2k"+
		"b\3\2\2\2kc\3\2\2\2kd\3\2\2\2kf\3\2\2\2ki\3\2\2\2kj\3\2\2\2lm\3\2\2\2"+
		"mq\7\7\2\2np\7\24\2\2on\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2\2\2r\5\3\2\2"+
		"\2sq\3\2\2\2tu\7\5\2\2uv\7\6\2\2vw\5\20\t\2wx\7 \2\2xy\7\"\2\2yz\t\2\2"+
		"\2z{\7\"\2\2{|\7 \2\2|}\5\20\t\2}\u0080\7 \2\2~\u0081\5\n\6\2\177\u0081"+
		"\5\32\16\2\u0080~\3\2\2\2\u0080\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082"+
		"\u0086\7\7\2\2\u0083\u0085\7\24\2\2\u0084\u0083\3\2\2\2\u0085\u0088\3"+
		"\2\2\2\u0086\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\7\3\2\2\2\u0088\u0086"+
		"\3\2\2\2\u0089\u008e\7\34\2\2\u008a\u008e\7\33\2\2\u008b\u008e\5\36\20"+
		"\2\u008c\u008e\5 \21\2\u008d\u0089\3\2\2\2\u008d\u008a\3\2\2\2\u008d\u008b"+
		"\3\2\2\2\u008d\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0090\7\25\2\2"+
		"\u0090\u0091\7\34\2\2\u0091\u0098\7\6\2\2\u0092\u0094\7 \2\2\u0093\u0092"+
		"\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0097\5\20\t\2"+
		"\u0096\u0093\3\2\2\2\u0097\u009a\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099"+
		"\3\2\2\2\u0099\u009b\3\2\2\2\u009a\u0098\3\2\2\2\u009b\u009f\7\7\2\2\u009c"+
		"\u009e\7\24\2\2\u009d\u009c\3\2\2\2\u009e\u00a1\3\2\2\2\u009f\u009d\3"+
		"\2\2\2\u009f\u00a0\3\2\2\2\u00a0\t\3\2\2\2\u00a1\u009f\3\2\2\2\u00a2\u00b3"+
		"\7\t\2\2\u00a3\u00b2\5\b\5\2\u00a4\u00b2\5\2\2\2\u00a5\u00b2\5\4\3\2\u00a6"+
		"\u00b2\5\6\4\2\u00a7\u00b2\5\34\17\2\u00a8\u00b2\5\26\f\2\u00a9\u00b2"+
		"\5\f\7\2\u00aa\u00ae\t\3\2\2\u00ab\u00ad\7\24\2\2\u00ac\u00ab\3\2\2\2"+
		"\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b2"+
		"\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00a3\3\2\2\2\u00b1\u00a4\3\2\2\2\u00b1"+
		"\u00a5\3\2\2\2\u00b1\u00a6\3\2\2\2\u00b1\u00a7\3\2\2\2\u00b1\u00a8\3\2"+
		"\2\2\u00b1\u00a9\3\2\2\2\u00b1\u00aa\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3"+
		"\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b6\3\2\2\2\u00b5\u00b3\3\2"+
		"\2\2\u00b6\u00b7\7\n\2\2\u00b7\13\3\2\2\2\u00b8\u00d4\7\13\2\2\u00b9\u00bb"+
		"\t\4\2\2\u00ba\u00b9\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00cd\3\2\2\2\u00bc"+
		"\u00ce\7\34\2\2\u00bd\u00ce\5\32\16\2\u00be\u00c0\7\35\2\2\u00bf\u00be"+
		"\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00ce\7\30\2\2"+
		"\u00c2\u00c4\7\35\2\2\u00c3\u00c2\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c5"+
		"\3\2\2\2\u00c5\u00ce\7\31\2\2\u00c6\u00ce\5\30\r\2\u00c7\u00ce\7\33\2"+
		"\2\u00c8\u00ce\5\b\5\2\u00c9\u00ce\5\f\7\2\u00ca\u00ce\5 \21\2\u00cb\u00ce"+
		"\5\36\20\2\u00cc\u00ce\5\"\22\2\u00cd\u00bc\3\2\2\2\u00cd\u00bd\3\2\2"+
		"\2\u00cd\u00bf\3\2\2\2\u00cd\u00c3\3\2\2\2\u00cd\u00c6\3\2\2\2\u00cd\u00c7"+
		"\3\2\2\2\u00cd\u00c8\3\2\2\2\u00cd\u00c9\3\2\2\2\u00cd\u00ca\3\2\2\2\u00cd"+
		"\u00cb\3\2\2\2\u00cd\u00cc\3\2\2\2\u00ce\u00d0\3\2\2\2\u00cf\u00d1\t\5"+
		"\2\2\u00d0\u00cf\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d3\3\2\2\2\u00d2"+
		"\u00ba\3\2\2\2\u00d3\u00d6\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5\3\2"+
		"\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d7\u00db\7\f\2\2\u00d8"+
		"\u00da\7\24\2\2\u00d9\u00d8\3\2\2\2\u00da\u00dd\3\2\2\2\u00db\u00d9\3"+
		"\2\2\2\u00db\u00dc\3\2\2\2\u00dc\r\3\2\2\2\u00dd\u00db\3\2\2\2\u00de\u00e6"+
		"\5\n\6\2\u00df\u00e6\5\f\7\2\u00e0\u00e6\5\32\16\2\u00e1\u00e6\5\2\2\2"+
		"\u00e2\u00e6\5\4\3\2\u00e3\u00e6\5\6\4\2\u00e4\u00e6\5\32\16\2\u00e5\u00de"+
		"\3\2\2\2\u00e5\u00df\3\2\2\2\u00e5\u00e0\3\2\2\2\u00e5\u00e1\3\2\2\2\u00e5"+
		"\u00e2\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e9\3\2"+
		"\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\17\3\2\2\2\u00e9\u00e7"+
		"\3\2\2\2\u00ea\u00fc\5\b\5\2\u00eb\u00fc\7\37\2\2\u00ec\u00fc\5\"\22\2"+
		"\u00ed\u00fc\5\32\16\2\u00ee\u00fc\7\34\2\2\u00ef\u00f1\7\35\2\2\u00f0"+
		"\u00ef\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00fc\7\30"+
		"\2\2\u00f3\u00f5\7\35\2\2\u00f4\u00f3\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5"+
		"\u00f6\3\2\2\2\u00f6\u00fc\7\31\2\2\u00f7\u00fc\5\f\7\2\u00f8\u00fc\7"+
		"\33\2\2\u00f9\u00fc\5 \21\2\u00fa\u00fc\5\36\20\2\u00fb\u00ea\3\2\2\2"+
		"\u00fb\u00eb\3\2\2\2\u00fb\u00ec\3\2\2\2\u00fb\u00ed\3\2\2\2\u00fb\u00ee"+
		"\3\2\2\2\u00fb\u00f0\3\2\2\2\u00fb\u00f4\3\2\2\2\u00fb\u00f7\3\2\2\2\u00fb"+
		"\u00f8\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fa\3\2\2\2\u00fc\21\3\2\2"+
		"\2\u00fd\u00ff\7\36\2\2\u00fe\u00fd\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff"+
		"\u0100\3\2\2\2\u0100\u0102\5\24\13\2\u0101\u00fe\3\2\2\2\u0102\u0105\3"+
		"\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104\23\3\2\2\2\u0105"+
		"\u0103\3\2\2\2\u0106\u010c\7\34\2\2\u0107\u010c\5\b\5\2\u0108\u010c\5"+
		" \21\2\u0109\u010c\5\f\7\2\u010a\u010c\7\33\2\2\u010b\u0106\3\2\2\2\u010b"+
		"\u0107\3\2\2\2\u010b\u0108\3\2\2\2\u010b\u0109\3\2\2\2\u010b\u010a\3\2"+
		"\2\2\u010c\u010d\3\2\2\2\u010d\u010e\t\2\2\2\u010e\u010f\5\20\t\2\u010f"+
		"\25\3\2\2\2\u0110\u0111\7\34\2\2\u0111\u0112\7\24\2\2\u0112\27\3\2\2\2"+
		"\u0113\u0121\7\34\2\2\u0114\u0121\7\33\2\2\u0115\u0117\7\35\2\2\u0116"+
		"\u0115\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u0121\7\30"+
		"\2\2\u0119\u011b\7\35\2\2\u011a\u0119\3\2\2\2\u011a\u011b\3\2\2\2\u011b"+
		"\u011c\3\2\2\2\u011c\u0121\7\31\2\2\u011d\u0121\5\b\5\2\u011e\u0121\5"+
		" \21\2\u011f\u0121\5\f\7\2\u0120\u0113\3\2\2\2\u0120\u0114\3\2\2\2\u0120"+
		"\u0116\3\2\2\2\u0120\u011a\3\2\2\2\u0120\u011d\3\2\2\2\u0120\u011e\3\2"+
		"\2\2\u0120\u011f\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u012f\7\27\2\2\u0123"+
		"\u0130\7\34\2\2\u0124\u0126\7\35\2\2\u0125\u0124\3\2\2\2\u0125\u0126\3"+
		"\2\2\2\u0126\u0127\3\2\2\2\u0127\u0130\7\30\2\2\u0128\u012a\7\35\2\2\u0129"+
		"\u0128\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012b\3\2\2\2\u012b\u0130\7\31"+
		"\2\2\u012c\u0130\5\b\5\2\u012d\u0130\5 \21\2\u012e\u0130\5\f\7\2\u012f"+
		"\u0123\3\2\2\2\u012f\u0125\3\2\2\2\u012f\u0129\3\2\2\2\u012f\u012c\3\2"+
		"\2\2\u012f\u012d\3\2\2\2\u012f\u012e\3\2\2\2\u0130\31\3\2\2\2\u0131\u0156"+
		"\7\"\2\2\u0132\u014b\7\34\2\2\u0133\u014b\7\25\2\2\u0134\u0136\7\35\2"+
		"\2\u0135\u0134\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u014b"+
		"\7\30\2\2\u0138\u013a\7\35\2\2\u0139\u0138\3\2\2\2\u0139\u013a\3\2\2\2"+
		"\u013a\u013b\3\2\2\2\u013b\u014b\7\31\2\2\u013c\u014b\7\16\2\2\u013d\u014b"+
		"\7\17\2\2\u013e\u014b\7\20\2\2\u013f\u014b\7\21\2\2\u0140\u014b\7\22\2"+
		"\2\u0141\u014b\7\23\2\2\u0142\u014b\7\r\2\2\u0143\u014b\5 \21\2\u0144"+
		"\u014b\7\6\2\2\u0145\u014b\7\7\2\2\u0146\u014b\7 \2\2\u0147\u014b\7\35"+
		"\2\2\u0148\u014b\7\b\2\2\u0149\u014b\7\33\2\2\u014a\u0132\3\2\2\2\u014a"+
		"\u0133\3\2\2\2\u014a\u0135\3\2\2\2\u014a\u0139\3\2\2\2\u014a\u013c\3\2"+
		"\2\2\u014a\u013d\3\2\2\2\u014a\u013e\3\2\2\2\u014a\u013f\3\2\2\2\u014a"+
		"\u0140\3\2\2\2\u014a\u0141\3\2\2\2\u014a\u0142\3\2\2\2\u014a\u0143\3\2"+
		"\2\2\u014a\u0144\3\2\2\2\u014a\u0145\3\2\2\2\u014a\u0146\3\2\2\2\u014a"+
		"\u0147\3\2\2\2\u014a\u0148\3\2\2\2\u014a\u0149\3\2\2\2\u014b\u014c\3\2"+
		"\2\2\u014c\u014a\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u0157\3\2\2\2\u014e"+
		"\u0153\5\"\22\2\u014f\u0151\7\r\2\2\u0150\u0152\7\34\2\2\u0151\u0150\3"+
		"\2\2\2\u0151\u0152\3\2\2\2\u0152\u0154\3\2\2\2\u0153\u014f\3\2\2\2\u0153"+
		"\u0154\3\2\2\2\u0154\u0157\3\2\2\2\u0155\u0157\5\32\16\2\u0156\u014a\3"+
		"\2\2\2\u0156\u014e\3\2\2\2\u0156\u0155\3\2\2\2\u0156\u0157\3\2\2\2\u0157"+
		"\u0158\3\2\2\2\u0158\u0159\7\"\2\2\u0159\33\3\2\2\2\u015a\u015b\7\26\2"+
		"\2\u015b\u015c\7\34\2\2\u015c\u0162\7\6\2\2\u015d\u0160\5\20\t\2\u015e"+
		"\u015f\7 \2\2\u015f\u0161\5\20\t\2\u0160\u015e\3\2\2\2\u0160\u0161\3\2"+
		"\2\2\u0161\u0163\3\2\2\2\u0162\u015d\3\2\2\2\u0162\u0163\3\2\2\2\u0163"+
		"\u0164\3\2\2\2\u0164\u0165\7\7\2\2\u0165\u0166\7\24\2\2\u0166\35\3\2\2"+
		"\2\u0167\u016a\7#\2\2\u0168\u016b\5\f\7\2\u0169\u016b\7\34\2\2\u016a\u0168"+
		"\3\2\2\2\u016a\u0169\3\2\2\2\u016b\37\3\2\2\2\u016c\u016d\7\34\2\2\u016d"+
		"\u016e\7!\2\2\u016e\u016f\7\34\2\2\u016f!\3\2\2\2\u0170\u0171\7\b\2\2"+
		"\u0171\u0172\t\6\2\2\u0172#\3\2\2\2\65\648=CNSZ_fkq\u0080\u0086\u008d"+
		"\u0093\u0098\u009f\u00ae\u00b1\u00b3\u00ba\u00bf\u00c3\u00cd\u00d0\u00d4"+
		"\u00db\u00e5\u00e7\u00f0\u00f4\u00fb\u00fe\u0103\u010b\u0116\u011a\u0120"+
		"\u0125\u0129\u012f\u0135\u0139\u014a\u014c\u0151\u0153\u0156\u0160\u0162"+
		"\u016a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}