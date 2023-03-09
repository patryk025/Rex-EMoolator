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
		RULE_iterator = 12, RULE_string = 13, RULE_instr = 14, RULE_stringRef = 15, 
		RULE_struct = 16, RULE_variable = 17, RULE_number = 18, RULE_floatNumber = 19, 
		RULE_literal = 20, RULE_arithmetic = 21, RULE_logic = 22, RULE_compare = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"ifInstr", "loopInstr", "whileInstr", "functionFire", "codeBlock", "expression", 
			"script", "param", "condition", "conditionPart", "behFire", "modulo", 
			"iterator", "string", "instr", "stringRef", "struct", "variable", "number", 
			"floatNumber", "literal", "arithmetic", "logic", "compare"
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
		public CompareContext compare() {
			return getRuleContext(CompareContext.class,0);
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
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
		}
		public IfInstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifInstr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterIfInstr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitIfInstr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitIfInstr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfInstrContext ifInstr() throws RecognitionException {
		IfInstrContext _localctx = new IfInstrContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_ifInstr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(T__0);
			setState(49);
			match(LPAREN);
			setState(64);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(50);
				match(QUOTEMARK);
				setState(51);
				condition();
				setState(52);
				match(QUOTEMARK);
				setState(53);
				match(SEPARATOR);
				}
				break;
			case 2:
				{
				setState(55);
				param();
				setState(56);
				match(SEPARATOR);
				setState(57);
				match(QUOTEMARK);
				setState(58);
				compare();
				setState(59);
				match(QUOTEMARK);
				setState(60);
				match(SEPARATOR);
				setState(61);
				param();
				setState(62);
				match(SEPARATOR);
				}
				break;
			}
			setState(68);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(66);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(67);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(70);
			match(SEPARATOR);
			setState(73);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(71);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(72);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(75);
			match(RPAREN);
			setState(79);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(76);
					match(ENDINSTR);
					}
					} 
				}
				setState(81);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
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
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
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
		public List<ArithmeticContext> arithmetic() {
			return getRuleContexts(ArithmeticContext.class);
		}
		public ArithmeticContext arithmetic(int i) {
			return getRuleContext(ArithmeticContext.class,i);
		}
		public LoopInstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loopInstr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterLoopInstr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitLoopInstr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitLoopInstr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoopInstrContext loopInstr() throws RecognitionException {
		LoopInstrContext _localctx = new LoopInstrContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_loopInstr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(T__1);
			setState(83);
			match(LPAREN);
			setState(84);
			codeBlock();
			setState(85);
			match(SEPARATOR);
			setState(95);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(86);
				string();
				}
				break;
			case 2:
				{
				setState(87);
				literal();
				}
				break;
			case 3:
				{
				setState(88);
				expression();
				}
				break;
			case 4:
				{
				setState(90);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(89);
					arithmetic();
					}
				}

				setState(92);
				number();
				}
				break;
			case 5:
				{
				setState(93);
				functionFire();
				}
				break;
			case 6:
				{
				setState(94);
				struct();
				}
				break;
			}
			setState(97);
			match(SEPARATOR);
			setState(107);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(98);
				string();
				}
				break;
			case 2:
				{
				setState(99);
				literal();
				}
				break;
			case 3:
				{
				setState(100);
				expression();
				}
				break;
			case 4:
				{
				setState(102);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(101);
					arithmetic();
					}
				}

				setState(104);
				number();
				}
				break;
			case 5:
				{
				setState(105);
				functionFire();
				}
				break;
			case 6:
				{
				setState(106);
				struct();
				}
				break;
			}
			setState(109);
			match(SEPARATOR);
			setState(119);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(110);
				string();
				}
				break;
			case 2:
				{
				setState(111);
				literal();
				}
				break;
			case 3:
				{
				setState(112);
				expression();
				}
				break;
			case 4:
				{
				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(113);
					arithmetic();
					}
				}

				setState(116);
				number();
				}
				break;
			case 5:
				{
				setState(117);
				functionFire();
				}
				break;
			case 6:
				{
				setState(118);
				struct();
				}
				break;
			}
			setState(121);
			match(RPAREN);
			setState(125);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(122);
					match(ENDINSTR);
					}
					} 
				}
				setState(127);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
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
		public CompareContext compare() {
			return getRuleContext(CompareContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterWhileInstr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitWhileInstr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitWhileInstr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileInstrContext whileInstr() throws RecognitionException {
		WhileInstrContext _localctx = new WhileInstrContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_whileInstr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(T__2);
			setState(129);
			match(LPAREN);
			setState(130);
			param();
			setState(131);
			match(SEPARATOR);
			setState(132);
			match(QUOTEMARK);
			setState(133);
			compare();
			setState(134);
			match(QUOTEMARK);
			setState(135);
			match(SEPARATOR);
			setState(136);
			param();
			setState(137);
			match(SEPARATOR);
			setState(140);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(138);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(139);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(142);
			match(RPAREN);
			setState(146);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(143);
					match(ENDINSTR);
					}
					} 
				}
				setState(148);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
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
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public IteratorContext iterator() {
			return getRuleContext(IteratorContext.class,0);
		}
		public StringRefContext stringRef() {
			return getRuleContext(StringRefContext.class,0);
		}
		public StructContext struct() {
			return getRuleContext(StructContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterFunctionFire(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitFunctionFire(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitFunctionFire(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionFireContext functionFire() throws RecognitionException {
		FunctionFireContext _localctx = new FunctionFireContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_functionFire);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(149);
				literal();
				}
				break;
			case 2:
				{
				setState(150);
				iterator();
				}
				break;
			case 3:
				{
				setState(151);
				stringRef();
				}
				break;
			case 4:
				{
				setState(152);
				struct();
				}
				break;
			case 5:
				{
				setState(153);
				variable();
				}
				break;
			}
			setState(156);
			match(FIREFUNC);
			setState(157);
			literal();
			setState(158);
			match(LPAREN);
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << BOOLEAN) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(159);
					match(SEPARATOR);
					}
				}

				setState(162);
				param();
				}
				}
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(168);
			match(RPAREN);
			setState(172);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(169);
					match(ENDINSTR);
					}
					} 
				}
				setState(174);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
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
		public List<CodeBlockContext> codeBlock() {
			return getRuleContexts(CodeBlockContext.class);
		}
		public CodeBlockContext codeBlock(int i) {
			return getRuleContext(CodeBlockContext.class,i);
		}
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
		}
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public List<FloatNumberContext> floatNumber() {
			return getRuleContexts(FloatNumberContext.class);
		}
		public FloatNumberContext floatNumber(int i) {
			return getRuleContext(FloatNumberContext.class,i);
		}
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public CodeBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_codeBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterCodeBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitCodeBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitCodeBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeBlockContext codeBlock() throws RecognitionException {
		CodeBlockContext _localctx = new CodeBlockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_codeBlock);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			match(STARTCODE);
			setState(197);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << VARREF) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << STRREF))) != 0)) {
				{
				setState(195);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(176);
					functionFire();
					}
					break;
				case 2:
					{
					setState(177);
					ifInstr();
					}
					break;
				case 3:
					{
					setState(178);
					loopInstr();
					}
					break;
				case 4:
					{
					setState(179);
					whileInstr();
					}
					break;
				case 5:
					{
					setState(180);
					instr();
					}
					break;
				case 6:
					{
					setState(181);
					behFire();
					}
					break;
				case 7:
					{
					setState(182);
					expression();
					}
					break;
				case 8:
					{
					setState(183);
					codeBlock();
					}
					break;
				case 9:
					{
					setState(187);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case LITERAL:
						{
						setState(184);
						literal();
						}
						break;
					case FLOAT:
						{
						setState(185);
						floatNumber();
						}
						break;
					case NUMBER:
						{
						setState(186);
						number();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(192);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(189);
							match(ENDINSTR);
							}
							} 
						}
						setState(194);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
					}
					}
					break;
				}
				}
				setState(199);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(200);
				match(ENDINSTR);
				}
				}
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(206);
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
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public List<StringContext> string() {
			return getRuleContexts(StringContext.class);
		}
		public StringContext string(int i) {
			return getRuleContext(StringContext.class,i);
		}
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public List<FloatNumberContext> floatNumber() {
			return getRuleContexts(FloatNumberContext.class);
		}
		public FloatNumberContext floatNumber(int i) {
			return getRuleContext(FloatNumberContext.class,i);
		}
		public List<ModuloContext> modulo() {
			return getRuleContexts(ModuloContext.class);
		}
		public ModuloContext modulo(int i) {
			return getRuleContext(ModuloContext.class,i);
		}
		public List<IteratorContext> iterator() {
			return getRuleContexts(IteratorContext.class);
		}
		public IteratorContext iterator(int i) {
			return getRuleContext(IteratorContext.class,i);
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
		public List<ArithmeticContext> arithmetic() {
			return getRuleContexts(ArithmeticContext.class);
		}
		public ArithmeticContext arithmetic(int i) {
			return getRuleContext(ArithmeticContext.class,i);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			match(STARTEXPR);
			setState(239);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(212);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(209);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(210);
					match(DIV);
					}
					break;
				case 3:
					{
					setState(211);
					match(STRREF);
					}
					break;
				}
				setState(231);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(214);
					literal();
					}
					break;
				case 2:
					{
					setState(215);
					string();
					}
					break;
				case 3:
					{
					setState(217);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(216);
						arithmetic();
						}
					}

					setState(219);
					number();
					}
					break;
				case 4:
					{
					setState(221);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(220);
						arithmetic();
						}
					}

					setState(223);
					floatNumber();
					}
					break;
				case 5:
					{
					setState(224);
					modulo();
					}
					break;
				case 6:
					{
					setState(225);
					iterator();
					}
					break;
				case 7:
					{
					setState(226);
					functionFire();
					}
					break;
				case 8:
					{
					setState(227);
					expression();
					}
					break;
				case 9:
					{
					setState(228);
					struct();
					}
					break;
				case 10:
					{
					setState(229);
					stringRef();
					}
					break;
				case 11:
					{
					setState(230);
					variable();
					}
					break;
				}
				setState(235);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(233);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(234);
					match(STRREF);
					}
					break;
				}
				}
				}
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(242);
			match(STOPEXPR);
			setState(246);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(243);
					match(ENDINSTR);
					}
					} 
				}
				setState(248);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_script);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(256);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					setState(249);
					codeBlock();
					}
					break;
				case 2:
					{
					setState(250);
					expression();
					}
					break;
				case 3:
					{
					setState(251);
					string();
					}
					break;
				case 4:
					{
					setState(252);
					ifInstr();
					}
					break;
				case 5:
					{
					setState(253);
					loopInstr();
					}
					break;
				case 6:
					{
					setState(254);
					whileInstr();
					}
					break;
				case 7:
					{
					setState(255);
					string();
					}
					break;
				}
				}
				setState(260);
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
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public ArithmeticContext arithmetic() {
			return getRuleContext(ArithmeticContext.class,0);
		}
		public FloatNumberContext floatNumber() {
			return getRuleContext(FloatNumberContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IteratorContext iterator() {
			return getRuleContext(IteratorContext.class,0);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_param);
		int _la;
		try {
			setState(278);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(261);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(262);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(263);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(264);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(265);
				literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(267);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(266);
					arithmetic();
					}
				}

				setState(269);
				number();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(271);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(270);
					arithmetic();
					}
				}

				setState(273);
				floatNumber();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(274);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(275);
				iterator();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(276);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(277);
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
		public List<LogicContext> logic() {
			return getRuleContexts(LogicContext.class);
		}
		public LogicContext logic(int i) {
			return getRuleContext(LogicContext.class,i);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << ITERATOR) | (1L << LITERAL) | (1L << LOGIC) | (1L << STRREF))) != 0)) {
				{
				{
				setState(281);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOGIC) {
					{
					setState(280);
					logic();
					}
				}

				setState(283);
				conditionPart();
				}
				}
				setState(288);
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
		public CompareContext compare() {
			return getRuleContext(CompareContext.class,0);
		}
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public FunctionFireContext functionFire() {
			return getRuleContext(FunctionFireContext.class,0);
		}
		public StructContext struct() {
			return getRuleContext(StructContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IteratorContext iterator() {
			return getRuleContext(IteratorContext.class,0);
		}
		public ConditionPartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionPart; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterConditionPart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitConditionPart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitConditionPart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionPartContext conditionPart() throws RecognitionException {
		ConditionPartContext _localctx = new ConditionPartContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_conditionPart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(294);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(289);
				literal();
				}
				break;
			case 2:
				{
				setState(290);
				functionFire();
				}
				break;
			case 3:
				{
				setState(291);
				struct();
				}
				break;
			case 4:
				{
				setState(292);
				expression();
				}
				break;
			case 5:
				{
				setState(293);
				iterator();
				}
				break;
			}
			setState(296);
			compare();
			setState(297);
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
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TerminalNode ENDINSTR() { return getToken(AidemMediaParser.ENDINSTR, 0); }
		public BehFireContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_behFire; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterBehFire(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitBehFire(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitBehFire(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BehFireContext behFire() throws RecognitionException {
		BehFireContext _localctx = new BehFireContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_behFire);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			literal();
			setState(300);
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
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public IteratorContext iterator() {
			return getRuleContext(IteratorContext.class,0);
		}
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public List<FloatNumberContext> floatNumber() {
			return getRuleContexts(FloatNumberContext.class);
		}
		public FloatNumberContext floatNumber(int i) {
			return getRuleContext(FloatNumberContext.class,i);
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
		public List<ArithmeticContext> arithmetic() {
			return getRuleContexts(ArithmeticContext.class);
		}
		public ArithmeticContext arithmetic(int i) {
			return getRuleContext(ArithmeticContext.class,i);
		}
		public ModuloContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modulo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterModulo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitModulo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitModulo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuloContext modulo() throws RecognitionException {
		ModuloContext _localctx = new ModuloContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_modulo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(302);
				literal();
				}
				break;
			case 2:
				{
				setState(303);
				iterator();
				}
				break;
			case 3:
				{
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(304);
					arithmetic();
					}
				}

				setState(307);
				number();
				}
				break;
			case 4:
				{
				setState(309);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(308);
					arithmetic();
					}
				}

				setState(311);
				floatNumber();
				}
				break;
			case 5:
				{
				setState(312);
				functionFire();
				}
				break;
			case 6:
				{
				setState(313);
				struct();
				}
				break;
			case 7:
				{
				setState(314);
				expression();
				}
				break;
			}
			setState(317);
			match(MOD);
			setState(330);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(318);
				literal();
				}
				break;
			case 2:
				{
				setState(320);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(319);
					arithmetic();
					}
				}

				setState(322);
				number();
				}
				break;
			case 3:
				{
				setState(324);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(323);
					arithmetic();
					}
				}

				setState(326);
				floatNumber();
				}
				break;
			case 4:
				{
				setState(327);
				functionFire();
				}
				break;
			case 5:
				{
				setState(328);
				struct();
				}
				break;
			case 6:
				{
				setState(329);
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

	public static class IteratorContext extends ParserRuleContext {
		public TerminalNode ITERATOR() { return getToken(AidemMediaParser.ITERATOR, 0); }
		public IteratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iterator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterIterator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitIterator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitIterator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IteratorContext iterator() throws RecognitionException {
		IteratorContext _localctx = new IteratorContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_iterator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
			match(ITERATOR);
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
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public List<TerminalNode> FIREFUNC() { return getTokens(AidemMediaParser.FIREFUNC); }
		public TerminalNode FIREFUNC(int i) {
			return getToken(AidemMediaParser.FIREFUNC, i);
		}
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public List<FloatNumberContext> floatNumber() {
			return getRuleContexts(FloatNumberContext.class);
		}
		public FloatNumberContext floatNumber(int i) {
			return getRuleContext(FloatNumberContext.class,i);
		}
		public List<CompareContext> compare() {
			return getRuleContexts(CompareContext.class);
		}
		public CompareContext compare(int i) {
			return getRuleContext(CompareContext.class,i);
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
		public List<ArithmeticContext> arithmetic() {
			return getRuleContexts(ArithmeticContext.class);
		}
		public ArithmeticContext arithmetic(int i) {
			return getRuleContext(ArithmeticContext.class,i);
		}
		public List<TerminalNode> VARREF() { return getTokens(AidemMediaParser.VARREF); }
		public TerminalNode VARREF(int i) {
			return getToken(AidemMediaParser.VARREF, i);
		}
		public List<IteratorContext> iterator() {
			return getRuleContexts(IteratorContext.class);
		}
		public IteratorContext iterator(int i) {
			return getRuleContext(IteratorContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<FunctionFireContext> functionFire() {
			return getRuleContexts(FunctionFireContext.class);
		}
		public FunctionFireContext functionFire(int i) {
			return getRuleContext(FunctionFireContext.class,i);
		}
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
			match(QUOTEMARK);
			setState(368);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(356); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(356);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
					case 1:
						{
						setState(335);
						literal();
						}
						break;
					case 2:
						{
						setState(336);
						match(FIREFUNC);
						}
						break;
					case 3:
						{
						setState(338);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(337);
							arithmetic();
							}
						}

						setState(340);
						number();
						}
						break;
					case 4:
						{
						setState(342);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(341);
							arithmetic();
							}
						}

						setState(344);
						floatNumber();
						}
						break;
					case 5:
						{
						setState(345);
						compare();
						}
						break;
					case 6:
						{
						setState(346);
						match(SLASH);
						}
						break;
					case 7:
						{
						setState(347);
						struct();
						}
						break;
					case 8:
						{
						setState(348);
						match(LPAREN);
						}
						break;
					case 9:
						{
						setState(349);
						match(RPAREN);
						}
						break;
					case 10:
						{
						setState(350);
						match(SEPARATOR);
						}
						break;
					case 11:
						{
						setState(351);
						arithmetic();
						}
						break;
					case 12:
						{
						setState(352);
						match(VARREF);
						}
						break;
					case 13:
						{
						setState(353);
						iterator();
						}
						break;
					case 14:
						{
						setState(354);
						expression();
						}
						break;
					case 15:
						{
						setState(355);
						functionFire();
						}
						break;
					}
					}
					setState(358); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << VARREF) | (1L << STARTEXPR) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << FIREFUNC) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR) | (1L << STRREF))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(360);
				variable();
				setState(365);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(361);
					match(SLASH);
					setState(363);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(362);
						literal();
						}
					}

					}
				}

				}
				}
				break;
			case 3:
				{
				setState(367);
				string();
				}
				break;
			}
			setState(370);
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
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterInstr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitInstr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitInstr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstrContext instr() throws RecognitionException {
		InstrContext _localctx = new InstrContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_instr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			match(DIV);
			setState(373);
			literal();
			setState(374);
			match(LPAREN);
			setState(380);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << BOOLEAN) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(375);
				param();
				setState(378);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(376);
					match(SEPARATOR);
					setState(377);
					param();
					}
				}

				}
			}

			setState(382);
			match(RPAREN);
			setState(383);
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
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public StringRefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringRef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterStringRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitStringRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitStringRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringRefContext stringRef() throws RecognitionException {
		StringRefContext _localctx = new StringRefContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_stringRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			match(STRREF);
			setState(388);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(386);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(387);
				literal();
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
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public TerminalNode STRUCTFIELD() { return getToken(AidemMediaParser.STRUCTFIELD, 0); }
		public StructContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_struct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterStruct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitStruct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitStruct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructContext struct() throws RecognitionException {
		StructContext _localctx = new StructContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_struct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(390);
			literal();
			setState(391);
			match(STRUCTFIELD);
			setState(392);
			literal();
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
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(394);
			match(VARREF);
			setState(397);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
				{
				setState(395);
				literal();
				}
				break;
			case NUMBER:
				{
				setState(396);
				number();
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

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(AidemMediaParser.NUMBER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(399);
			match(NUMBER);
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

	public static class FloatNumberContext extends ParserRuleContext {
		public TerminalNode FLOAT() { return getToken(AidemMediaParser.FLOAT, 0); }
		public FloatNumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_floatNumber; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterFloatNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitFloatNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitFloatNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FloatNumberContext floatNumber() throws RecognitionException {
		FloatNumberContext _localctx = new FloatNumberContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_floatNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(401);
			match(FLOAT);
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

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode LITERAL() { return getToken(AidemMediaParser.LITERAL, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(403);
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

	public static class ArithmeticContext extends ParserRuleContext {
		public TerminalNode ARITHMETIC() { return getToken(AidemMediaParser.ARITHMETIC, 0); }
		public ArithmeticContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arithmetic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterArithmetic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitArithmetic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitArithmetic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArithmeticContext arithmetic() throws RecognitionException {
		ArithmeticContext _localctx = new ArithmeticContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_arithmetic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405);
			match(ARITHMETIC);
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

	public static class LogicContext extends ParserRuleContext {
		public TerminalNode LOGIC() { return getToken(AidemMediaParser.LOGIC, 0); }
		public LogicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterLogic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitLogic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitLogic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicContext logic() throws RecognitionException {
		LogicContext _localctx = new LogicContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_logic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(407);
			match(LOGIC);
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

	public static class CompareContext extends ParserRuleContext {
		public TerminalNode LSS() { return getToken(AidemMediaParser.LSS, 0); }
		public TerminalNode LEQ() { return getToken(AidemMediaParser.LEQ, 0); }
		public TerminalNode GEQ() { return getToken(AidemMediaParser.GEQ, 0); }
		public TerminalNode GTR() { return getToken(AidemMediaParser.GTR, 0); }
		public TerminalNode EQU() { return getToken(AidemMediaParser.EQU, 0); }
		public TerminalNode NEQ() { return getToken(AidemMediaParser.NEQ, 0); }
		public CompareContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compare; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterCompare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitCompare(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitCompare(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompareContext compare() throws RecognitionException {
		CompareContext _localctx = new CompareContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_compare);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u019e\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2C"+
		"\n\2\3\2\3\2\5\2G\n\2\3\2\3\2\3\2\5\2L\n\2\3\2\3\2\7\2P\n\2\f\2\16\2S"+
		"\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3]\n\3\3\3\3\3\3\3\5\3b\n\3\3"+
		"\3\3\3\3\3\3\3\3\3\5\3i\n\3\3\3\3\3\3\3\5\3n\n\3\3\3\3\3\3\3\3\3\3\3\5"+
		"\3u\n\3\3\3\3\3\3\3\5\3z\n\3\3\3\3\3\7\3~\n\3\f\3\16\3\u0081\13\3\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u008f\n\4\3\4\3\4\7\4"+
		"\u0093\n\4\f\4\16\4\u0096\13\4\3\5\3\5\3\5\3\5\3\5\5\5\u009d\n\5\3\5\3"+
		"\5\3\5\3\5\5\5\u00a3\n\5\3\5\7\5\u00a6\n\5\f\5\16\5\u00a9\13\5\3\5\3\5"+
		"\7\5\u00ad\n\5\f\5\16\5\u00b0\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\5\6\u00be\n\6\3\6\7\6\u00c1\n\6\f\6\16\6\u00c4\13\6\7\6\u00c6"+
		"\n\6\f\6\16\6\u00c9\13\6\3\6\7\6\u00cc\n\6\f\6\16\6\u00cf\13\6\3\6\3\6"+
		"\3\7\3\7\3\7\3\7\5\7\u00d7\n\7\3\7\3\7\3\7\5\7\u00dc\n\7\3\7\3\7\5\7\u00e0"+
		"\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00ea\n\7\3\7\3\7\5\7\u00ee\n"+
		"\7\7\7\u00f0\n\7\f\7\16\7\u00f3\13\7\3\7\3\7\7\7\u00f7\n\7\f\7\16\7\u00fa"+
		"\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7\b\u0103\n\b\f\b\16\b\u0106\13\b\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\5\t\u010e\n\t\3\t\3\t\5\t\u0112\n\t\3\t\3\t\3\t"+
		"\3\t\3\t\5\t\u0119\n\t\3\n\5\n\u011c\n\n\3\n\7\n\u011f\n\n\f\n\16\n\u0122"+
		"\13\n\3\13\3\13\3\13\3\13\3\13\5\13\u0129\n\13\3\13\3\13\3\13\3\f\3\f"+
		"\3\f\3\r\3\r\3\r\5\r\u0134\n\r\3\r\3\r\5\r\u0138\n\r\3\r\3\r\3\r\3\r\5"+
		"\r\u013e\n\r\3\r\3\r\3\r\5\r\u0143\n\r\3\r\3\r\5\r\u0147\n\r\3\r\3\r\3"+
		"\r\3\r\5\r\u014d\n\r\3\16\3\16\3\17\3\17\3\17\3\17\5\17\u0155\n\17\3\17"+
		"\3\17\5\17\u0159\n\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\6\17\u0167\n\17\r\17\16\17\u0168\3\17\3\17\3\17\5\17\u016e"+
		"\n\17\5\17\u0170\n\17\3\17\5\17\u0173\n\17\3\17\3\17\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\5\20\u017d\n\20\5\20\u017f\n\20\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\5\21\u0187\n\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\5\23\u0190\n"+
		"\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\31\2"+
		"\2\32\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\2\3\3\2\16\23"+
		"\2\u0207\2\62\3\2\2\2\4T\3\2\2\2\6\u0082\3\2\2\2\b\u009c\3\2\2\2\n\u00b1"+
		"\3\2\2\2\f\u00d2\3\2\2\2\16\u0104\3\2\2\2\20\u0118\3\2\2\2\22\u0120\3"+
		"\2\2\2\24\u0128\3\2\2\2\26\u012d\3\2\2\2\30\u013d\3\2\2\2\32\u014e\3\2"+
		"\2\2\34\u0150\3\2\2\2\36\u0176\3\2\2\2 \u0183\3\2\2\2\"\u0188\3\2\2\2"+
		"$\u018c\3\2\2\2&\u0191\3\2\2\2(\u0193\3\2\2\2*\u0195\3\2\2\2,\u0197\3"+
		"\2\2\2.\u0199\3\2\2\2\60\u019b\3\2\2\2\62\63\7\3\2\2\63B\7\6\2\2\64\65"+
		"\7\"\2\2\65\66\5\22\n\2\66\67\7\"\2\2\678\7 \2\28C\3\2\2\29:\5\20\t\2"+
		":;\7 \2\2;<\7\"\2\2<=\5\60\31\2=>\7\"\2\2>?\7 \2\2?@\5\20\t\2@A\7 \2\2"+
		"AC\3\2\2\2B\64\3\2\2\2B9\3\2\2\2CF\3\2\2\2DG\5\n\6\2EG\5\34\17\2FD\3\2"+
		"\2\2FE\3\2\2\2GH\3\2\2\2HK\7 \2\2IL\5\n\6\2JL\5\34\17\2KI\3\2\2\2KJ\3"+
		"\2\2\2LM\3\2\2\2MQ\7\7\2\2NP\7\24\2\2ON\3\2\2\2PS\3\2\2\2QO\3\2\2\2QR"+
		"\3\2\2\2R\3\3\2\2\2SQ\3\2\2\2TU\7\4\2\2UV\7\6\2\2VW\5\n\6\2Wa\7 \2\2X"+
		"b\5\34\17\2Yb\5*\26\2Zb\5\f\7\2[]\5,\27\2\\[\3\2\2\2\\]\3\2\2\2]^\3\2"+
		"\2\2^b\5&\24\2_b\5\b\5\2`b\5\"\22\2aX\3\2\2\2aY\3\2\2\2aZ\3\2\2\2a\\\3"+
		"\2\2\2a_\3\2\2\2a`\3\2\2\2bc\3\2\2\2cm\7 \2\2dn\5\34\17\2en\5*\26\2fn"+
		"\5\f\7\2gi\5,\27\2hg\3\2\2\2hi\3\2\2\2ij\3\2\2\2jn\5&\24\2kn\5\b\5\2l"+
		"n\5\"\22\2md\3\2\2\2me\3\2\2\2mf\3\2\2\2mh\3\2\2\2mk\3\2\2\2ml\3\2\2\2"+
		"no\3\2\2\2oy\7 \2\2pz\5\34\17\2qz\5*\26\2rz\5\f\7\2su\5,\27\2ts\3\2\2"+
		"\2tu\3\2\2\2uv\3\2\2\2vz\5&\24\2wz\5\b\5\2xz\5\"\22\2yp\3\2\2\2yq\3\2"+
		"\2\2yr\3\2\2\2yt\3\2\2\2yw\3\2\2\2yx\3\2\2\2z{\3\2\2\2{\177\7\7\2\2|~"+
		"\7\24\2\2}|\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080\3\2\2\2\u0080"+
		"\5\3\2\2\2\u0081\177\3\2\2\2\u0082\u0083\7\5\2\2\u0083\u0084\7\6\2\2\u0084"+
		"\u0085\5\20\t\2\u0085\u0086\7 \2\2\u0086\u0087\7\"\2\2\u0087\u0088\5\60"+
		"\31\2\u0088\u0089\7\"\2\2\u0089\u008a\7 \2\2\u008a\u008b\5\20\t\2\u008b"+
		"\u008e\7 \2\2\u008c\u008f\5\n\6\2\u008d\u008f\5\34\17\2\u008e\u008c\3"+
		"\2\2\2\u008e\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0094\7\7\2\2\u0091"+
		"\u0093\7\24\2\2\u0092\u0091\3\2\2\2\u0093\u0096\3\2\2\2\u0094\u0092\3"+
		"\2\2\2\u0094\u0095\3\2\2\2\u0095\7\3\2\2\2\u0096\u0094\3\2\2\2\u0097\u009d"+
		"\5*\26\2\u0098\u009d\5\32\16\2\u0099\u009d\5 \21\2\u009a\u009d\5\"\22"+
		"\2\u009b\u009d\5$\23\2\u009c\u0097\3\2\2\2\u009c\u0098\3\2\2\2\u009c\u0099"+
		"\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009b\3\2\2\2\u009d\u009e\3\2\2\2\u009e"+
		"\u009f\7\25\2\2\u009f\u00a0\5*\26\2\u00a0\u00a7\7\6\2\2\u00a1\u00a3\7"+
		" \2\2\u00a2\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4"+
		"\u00a6\5\20\t\2\u00a5\u00a2\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3"+
		"\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa"+
		"\u00ae\7\7\2\2\u00ab\u00ad\7\24\2\2\u00ac\u00ab\3\2\2\2\u00ad\u00b0\3"+
		"\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\t\3\2\2\2\u00b0\u00ae"+
		"\3\2\2\2\u00b1\u00c7\7\t\2\2\u00b2\u00c6\5\b\5\2\u00b3\u00c6\5\2\2\2\u00b4"+
		"\u00c6\5\4\3\2\u00b5\u00c6\5\6\4\2\u00b6\u00c6\5\36\20\2\u00b7\u00c6\5"+
		"\26\f\2\u00b8\u00c6\5\f\7\2\u00b9\u00c6\5\n\6\2\u00ba\u00be\5*\26\2\u00bb"+
		"\u00be\5(\25\2\u00bc\u00be\5&\24\2\u00bd\u00ba\3\2\2\2\u00bd\u00bb\3\2"+
		"\2\2\u00bd\u00bc\3\2\2\2\u00be\u00c2\3\2\2\2\u00bf\u00c1\7\24\2\2\u00c0"+
		"\u00bf\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2"+
		"\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5\u00b2\3\2\2\2\u00c5"+
		"\u00b3\3\2\2\2\u00c5\u00b4\3\2\2\2\u00c5\u00b5\3\2\2\2\u00c5\u00b6\3\2"+
		"\2\2\u00c5\u00b7\3\2\2\2\u00c5\u00b8\3\2\2\2\u00c5\u00b9\3\2\2\2\u00c5"+
		"\u00bd\3\2\2\2\u00c6\u00c9\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c7\u00c8\3\2"+
		"\2\2\u00c8\u00cd\3\2\2\2\u00c9\u00c7\3\2\2\2\u00ca\u00cc\7\24\2\2\u00cb"+
		"\u00ca\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2"+
		"\2\2\u00ce\u00d0\3\2\2\2\u00cf\u00cd\3\2\2\2\u00d0\u00d1\7\n\2\2\u00d1"+
		"\13\3\2\2\2\u00d2\u00f1\7\13\2\2\u00d3\u00d7\5,\27\2\u00d4\u00d7\7\26"+
		"\2\2\u00d5\u00d7\7#\2\2\u00d6\u00d3\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d6"+
		"\u00d5\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00e9\3\2\2\2\u00d8\u00ea\5*"+
		"\26\2\u00d9\u00ea\5\34\17\2\u00da\u00dc\5,\27\2\u00db\u00da\3\2\2\2\u00db"+
		"\u00dc\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00ea\5&\24\2\u00de\u00e0\5,"+
		"\27\2\u00df\u00de\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00ea\5(\25\2\u00e2\u00ea\5\30\r\2\u00e3\u00ea\5\32\16\2\u00e4\u00ea"+
		"\5\b\5\2\u00e5\u00ea\5\f\7\2\u00e6\u00ea\5\"\22\2\u00e7\u00ea\5 \21\2"+
		"\u00e8\u00ea\5$\23\2\u00e9\u00d8\3\2\2\2\u00e9\u00d9\3\2\2\2\u00e9\u00db"+
		"\3\2\2\2\u00e9\u00df\3\2\2\2\u00e9\u00e2\3\2\2\2\u00e9\u00e3\3\2\2\2\u00e9"+
		"\u00e4\3\2\2\2\u00e9\u00e5\3\2\2\2\u00e9\u00e6\3\2\2\2\u00e9\u00e7\3\2"+
		"\2\2\u00e9\u00e8\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00ee\5,\27\2\u00ec"+
		"\u00ee\7#\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ec\3\2\2\2\u00ed\u00ee\3\2"+
		"\2\2\u00ee\u00f0\3\2\2\2\u00ef\u00d6\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1"+
		"\u00ef\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00f4\3\2\2\2\u00f3\u00f1\3\2"+
		"\2\2\u00f4\u00f8\7\f\2\2\u00f5\u00f7\7\24\2\2\u00f6\u00f5\3\2\2\2\u00f7"+
		"\u00fa\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\r\3\2\2\2"+
		"\u00fa\u00f8\3\2\2\2\u00fb\u0103\5\n\6\2\u00fc\u0103\5\f\7\2\u00fd\u0103"+
		"\5\34\17\2\u00fe\u0103\5\2\2\2\u00ff\u0103\5\4\3\2\u0100\u0103\5\6\4\2"+
		"\u0101\u0103\5\34\17\2\u0102\u00fb\3\2\2\2\u0102\u00fc\3\2\2\2\u0102\u00fd"+
		"\3\2\2\2\u0102\u00fe\3\2\2\2\u0102\u00ff\3\2\2\2\u0102\u0100\3\2\2\2\u0102"+
		"\u0101\3\2\2\2\u0103\u0106\3\2\2\2\u0104\u0102\3\2\2\2\u0104\u0105\3\2"+
		"\2\2\u0105\17\3\2\2\2\u0106\u0104\3\2\2\2\u0107\u0119\5\b\5\2\u0108\u0119"+
		"\7\37\2\2\u0109\u0119\5$\23\2\u010a\u0119\5\34\17\2\u010b\u0119\5*\26"+
		"\2\u010c\u010e\5,\27\2\u010d\u010c\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u010f"+
		"\3\2\2\2\u010f\u0119\5&\24\2\u0110\u0112\5,\27\2\u0111\u0110\3\2\2\2\u0111"+
		"\u0112\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0119\5(\25\2\u0114\u0119\5\f"+
		"\7\2\u0115\u0119\5\32\16\2\u0116\u0119\5\"\22\2\u0117\u0119\5 \21\2\u0118"+
		"\u0107\3\2\2\2\u0118\u0108\3\2\2\2\u0118\u0109\3\2\2\2\u0118\u010a\3\2"+
		"\2\2\u0118\u010b\3\2\2\2\u0118\u010d\3\2\2\2\u0118\u0111\3\2\2\2\u0118"+
		"\u0114\3\2\2\2\u0118\u0115\3\2\2\2\u0118\u0116\3\2\2\2\u0118\u0117\3\2"+
		"\2\2\u0119\21\3\2\2\2\u011a\u011c\5.\30\2\u011b\u011a\3\2\2\2\u011b\u011c"+
		"\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011f\5\24\13\2\u011e\u011b\3\2\2\2"+
		"\u011f\u0122\3\2\2\2\u0120\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121\23"+
		"\3\2\2\2\u0122\u0120\3\2\2\2\u0123\u0129\5*\26\2\u0124\u0129\5\b\5\2\u0125"+
		"\u0129\5\"\22\2\u0126\u0129\5\f\7\2\u0127\u0129\5\32\16\2\u0128\u0123"+
		"\3\2\2\2\u0128\u0124\3\2\2\2\u0128\u0125\3\2\2\2\u0128\u0126\3\2\2\2\u0128"+
		"\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012b\5\60\31\2\u012b\u012c\5"+
		"\20\t\2\u012c\25\3\2\2\2\u012d\u012e\5*\26\2\u012e\u012f\7\24\2\2\u012f"+
		"\27\3\2\2\2\u0130\u013e\5*\26\2\u0131\u013e\5\32\16\2\u0132\u0134\5,\27"+
		"\2\u0133\u0132\3\2\2\2\u0133\u0134\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u013e"+
		"\5&\24\2\u0136\u0138\5,\27\2\u0137\u0136\3\2\2\2\u0137\u0138\3\2\2\2\u0138"+
		"\u0139\3\2\2\2\u0139\u013e\5(\25\2\u013a\u013e\5\b\5\2\u013b\u013e\5\""+
		"\22\2\u013c\u013e\5\f\7\2\u013d\u0130\3\2\2\2\u013d\u0131\3\2\2\2\u013d"+
		"\u0133\3\2\2\2\u013d\u0137\3\2\2\2\u013d\u013a\3\2\2\2\u013d\u013b\3\2"+
		"\2\2\u013d\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u014c\7\27\2\2\u0140"+
		"\u014d\5*\26\2\u0141\u0143\5,\27\2\u0142\u0141\3\2\2\2\u0142\u0143\3\2"+
		"\2\2\u0143\u0144\3\2\2\2\u0144\u014d\5&\24\2\u0145\u0147\5,\27\2\u0146"+
		"\u0145\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u014d\5("+
		"\25\2\u0149\u014d\5\b\5\2\u014a\u014d\5\"\22\2\u014b\u014d\5\f\7\2\u014c"+
		"\u0140\3\2\2\2\u014c\u0142\3\2\2\2\u014c\u0146\3\2\2\2\u014c\u0149\3\2"+
		"\2\2\u014c\u014a\3\2\2\2\u014c\u014b\3\2\2\2\u014d\31\3\2\2\2\u014e\u014f"+
		"\7\33\2\2\u014f\33\3\2\2\2\u0150\u0172\7\"\2\2\u0151\u0167\5*\26\2\u0152"+
		"\u0167\7\25\2\2\u0153\u0155\5,\27\2\u0154\u0153\3\2\2\2\u0154\u0155\3"+
		"\2\2\2\u0155\u0156\3\2\2\2\u0156\u0167\5&\24\2\u0157\u0159\5,\27\2\u0158"+
		"\u0157\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u0167\5("+
		"\25\2\u015b\u0167\5\60\31\2\u015c\u0167\7\r\2\2\u015d\u0167\5\"\22\2\u015e"+
		"\u0167\7\6\2\2\u015f\u0167\7\7\2\2\u0160\u0167\7 \2\2\u0161\u0167\5,\27"+
		"\2\u0162\u0167\7\b\2\2\u0163\u0167\5\32\16\2\u0164\u0167\5\f\7\2\u0165"+
		"\u0167\5\b\5\2\u0166\u0151\3\2\2\2\u0166\u0152\3\2\2\2\u0166\u0154\3\2"+
		"\2\2\u0166\u0158\3\2\2\2\u0166\u015b\3\2\2\2\u0166\u015c\3\2\2\2\u0166"+
		"\u015d\3\2\2\2\u0166\u015e\3\2\2\2\u0166\u015f\3\2\2\2\u0166\u0160\3\2"+
		"\2\2\u0166\u0161\3\2\2\2\u0166\u0162\3\2\2\2\u0166\u0163\3\2\2\2\u0166"+
		"\u0164\3\2\2\2\u0166\u0165\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u0166\3\2"+
		"\2\2\u0168\u0169\3\2\2\2\u0169\u0173\3\2\2\2\u016a\u016f\5$\23\2\u016b"+
		"\u016d\7\r\2\2\u016c\u016e\5*\26\2\u016d\u016c\3\2\2\2\u016d\u016e\3\2"+
		"\2\2\u016e\u0170\3\2\2\2\u016f\u016b\3\2\2\2\u016f\u0170\3\2\2\2\u0170"+
		"\u0173\3\2\2\2\u0171\u0173\5\34\17\2\u0172\u0166\3\2\2\2\u0172\u016a\3"+
		"\2\2\2\u0172\u0171\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0174\3\2\2\2\u0174"+
		"\u0175\7\"\2\2\u0175\35\3\2\2\2\u0176\u0177\7\26\2\2\u0177\u0178\5*\26"+
		"\2\u0178\u017e\7\6\2\2\u0179\u017c\5\20\t\2\u017a\u017b\7 \2\2\u017b\u017d"+
		"\5\20\t\2\u017c\u017a\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u017f\3\2\2\2"+
		"\u017e\u0179\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0180\3\2\2\2\u0180\u0181"+
		"\7\7\2\2\u0181\u0182\7\24\2\2\u0182\37\3\2\2\2\u0183\u0186\7#\2\2\u0184"+
		"\u0187\5\f\7\2\u0185\u0187\5*\26\2\u0186\u0184\3\2\2\2\u0186\u0185\3\2"+
		"\2\2\u0187!\3\2\2\2\u0188\u0189\5*\26\2\u0189\u018a\7!\2\2\u018a\u018b"+
		"\5*\26\2\u018b#\3\2\2\2\u018c\u018f\7\b\2\2\u018d\u0190\5*\26\2\u018e"+
		"\u0190\5&\24\2\u018f\u018d\3\2\2\2\u018f\u018e\3\2\2\2\u0190%\3\2\2\2"+
		"\u0191\u0192\7\30\2\2\u0192\'\3\2\2\2\u0193\u0194\7\31\2\2\u0194)\3\2"+
		"\2\2\u0195\u0196\7\34\2\2\u0196+\3\2\2\2\u0197\u0198\7\35\2\2\u0198-\3"+
		"\2\2\2\u0199\u019a\7\36\2\2\u019a/\3\2\2\2\u019b\u019c\t\2\2\2\u019c\61"+
		"\3\2\2\28BFKQ\\ahmty\177\u008e\u0094\u009c\u00a2\u00a7\u00ae\u00bd\u00c2"+
		"\u00c5\u00c7\u00cd\u00d6\u00db\u00df\u00e9\u00ed\u00f1\u00f8\u0102\u0104"+
		"\u010d\u0111\u0118\u011b\u0120\u0128\u0133\u0137\u013d\u0142\u0146\u014c"+
		"\u0154\u0158\u0166\u0168\u016d\u016f\u0172\u017c\u017e\u0186\u018f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}