// Generated from c:\Users\oem\Desktop\gramatykaAM\AidemMedia.g4 by ANTLR 4.9.2
package pl.cba.genszu.amcodetranslator.interpreter.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AidemMediaParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, COMMENT=4, LPAREN=5, RPAREN=6, VARREF=7, STARTCODE=8, 
		STOPCODE=9, STARTEXPR=10, STOPEXPR=11, SLASH=12, LSS=13, LEQ=14, GEQ=15, 
		GTR=16, EQU=17, NEQ=18, ENDINSTR=19, FIREFUNC=20, ADD=21, SUBTRACT=22, 
		MULT=23, DIV=24, MOD=25, NUMBER=26, FLOAT=27, DOT=28, ITERATOR=29, BOOLEAN=30, 
		LITERAL=31, LOGIC=32, SEPARATOR=33, STRUCTFIELD=34, QUOTEMARK=35, STRREF=36, 
		WS=37;
	public static final int
		RULE_ifInstr = 0, RULE_loopInstr = 1, RULE_whileInstr = 2, RULE_functionFire = 3, 
		RULE_codeBlock = 4, RULE_varWithNumber = 5, RULE_loopCodeParam = 6, RULE_conditionSimple = 7, 
		RULE_ifTrue = 8, RULE_ifFalse = 9, RULE_comment = 10, RULE_expression = 11, 
		RULE_script = 12, RULE_param = 13, RULE_condition = 14, RULE_conditionPart = 15, 
		RULE_behFire = 16, RULE_modulo = 17, RULE_iterator = 18, RULE_string = 19, 
		RULE_instr = 20, RULE_stringRef = 21, RULE_struct = 22, RULE_variable = 23, 
		RULE_number = 24, RULE_floatNumber = 25, RULE_literal = 26, RULE_arithmetic = 27, 
		RULE_logic = 28, RULE_compare = 29;
	private static String[] makeRuleNames() {
		return new String[] {
			"ifInstr", "loopInstr", "whileInstr", "functionFire", "codeBlock", "varWithNumber", 
			"loopCodeParam", "conditionSimple", "ifTrue", "ifFalse", "comment", "expression", 
			"script", "param", "condition", "conditionPart", "behFire", "modulo", 
			"iterator", "string", "instr", "stringRef", "struct", "variable", "number", 
			"floatNumber", "literal", "arithmetic", "logic", "compare"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@IF'", "'@LOOP'", "'@WHILE'", null, "'('", "')'", "'$'", null, 
			null, "'['", "']'", "'\\'", "'<'", null, null, "'>'", null, null, "';'", 
			"'^'", "'+'", "'-'", null, "'@'", "'%'", null, null, "'.'", "'_I_'", 
			null, null, null, "','", "'|'", "'\"'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "COMMENT", "LPAREN", "RPAREN", "VARREF", "STARTCODE", 
			"STOPCODE", "STARTEXPR", "STOPEXPR", "SLASH", "LSS", "LEQ", "GEQ", "GTR", 
			"EQU", "NEQ", "ENDINSTR", "FIREFUNC", "ADD", "SUBTRACT", "MULT", "DIV", 
			"MOD", "NUMBER", "FLOAT", "DOT", "ITERATOR", "BOOLEAN", "LITERAL", "LOGIC", 
			"SEPARATOR", "STRUCTFIELD", "QUOTEMARK", "STRREF", "WS"
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
		public IfTrueContext ifTrue() {
			return getRuleContext(IfTrueContext.class,0);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(AidemMediaParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(AidemMediaParser.SEPARATOR, i);
		}
		public IfFalseContext ifFalse() {
			return getRuleContext(IfFalseContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public List<TerminalNode> QUOTEMARK() { return getTokens(AidemMediaParser.QUOTEMARK); }
		public TerminalNode QUOTEMARK(int i) {
			return getToken(AidemMediaParser.QUOTEMARK, i);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public ConditionSimpleContext conditionSimple() {
			return getRuleContext(ConditionSimpleContext.class,0);
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
			setState(60);
			match(T__0);
			setState(61);
			match(LPAREN);
			setState(70);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(62);
				match(QUOTEMARK);
				setState(63);
				condition();
				setState(64);
				match(QUOTEMARK);
				setState(65);
				match(SEPARATOR);
				}
				break;
			case 2:
				{
				setState(67);
				conditionSimple();
				setState(68);
				match(SEPARATOR);
				}
				break;
			}
			setState(72);
			ifTrue();
			setState(73);
			match(SEPARATOR);
			setState(74);
			ifFalse();
			setState(75);
			match(RPAREN);
			setState(79);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
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
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
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
		public LoopCodeParamContext loopCodeParam() {
			return getRuleContext(LoopCodeParamContext.class,0);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(AidemMediaParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(AidemMediaParser.SEPARATOR, i);
		}
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public List<TerminalNode> ENDINSTR() { return getTokens(AidemMediaParser.ENDINSTR); }
		public TerminalNode ENDINSTR(int i) {
			return getToken(AidemMediaParser.ENDINSTR, i);
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
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(T__1);
			setState(83);
			match(LPAREN);
			setState(84);
			loopCodeParam();
			setState(85);
			match(SEPARATOR);
			setState(86);
			param();
			setState(87);
			match(SEPARATOR);
			setState(88);
			param();
			setState(89);
			match(SEPARATOR);
			setState(90);
			param();
			setState(91);
			match(RPAREN);
			setState(95);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(92);
					match(ENDINSTR);
					}
					} 
				}
				setState(97);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
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
			setState(98);
			match(T__2);
			setState(99);
			match(LPAREN);
			setState(100);
			param();
			setState(101);
			match(SEPARATOR);
			setState(102);
			match(QUOTEMARK);
			setState(103);
			compare();
			setState(104);
			match(QUOTEMARK);
			setState(105);
			match(SEPARATOR);
			setState(106);
			param();
			setState(107);
			match(SEPARATOR);
			setState(110);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(108);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(109);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(112);
			match(RPAREN);
			setState(116);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(113);
					match(ENDINSTR);
					}
					} 
				}
				setState(118);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
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
		public VarWithNumberContext varWithNumber() {
			return getRuleContext(VarWithNumberContext.class,0);
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
			setState(125);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(119);
				literal();
				}
				break;
			case 2:
				{
				setState(120);
				iterator();
				}
				break;
			case 3:
				{
				setState(121);
				stringRef();
				}
				break;
			case 4:
				{
				setState(122);
				struct();
				}
				break;
			case 5:
				{
				setState(123);
				variable();
				}
				break;
			case 6:
				{
				setState(124);
				varWithNumber();
				}
				break;
			}
			setState(127);
			match(FIREFUNC);
			setState(128);
			literal();
			setState(129);
			match(LPAREN);
			setState(136);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(131);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==SEPARATOR) {
						{
						setState(130);
						match(SEPARATOR);
						}
					}

					setState(133);
					param();
					}
					} 
				}
				setState(138);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(139);
			match(RPAREN);
			setState(143);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(140);
					match(ENDINSTR);
					}
					} 
				}
				setState(145);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
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
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
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
			setState(146);
			match(STARTCODE);
			setState(169);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << COMMENT) | (1L << VARREF) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << STRREF))) != 0)) {
				{
				setState(167);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(147);
					functionFire();
					}
					break;
				case 2:
					{
					setState(148);
					ifInstr();
					}
					break;
				case 3:
					{
					setState(149);
					loopInstr();
					}
					break;
				case 4:
					{
					setState(150);
					whileInstr();
					}
					break;
				case 5:
					{
					setState(151);
					instr();
					}
					break;
				case 6:
					{
					setState(152);
					behFire();
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
					codeBlock();
					}
					break;
				case 9:
					{
					setState(158);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case LITERAL:
						{
						setState(155);
						literal();
						}
						break;
					case FLOAT:
						{
						setState(156);
						floatNumber();
						}
						break;
					case NUMBER:
						{
						setState(157);
						number();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				case 10:
					{
					setState(160);
					comment();
					setState(164);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(161);
							match(ENDINSTR);
							}
							} 
						}
						setState(166);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
					}
					}
					break;
				}
				}
				setState(171);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(172);
				match(ENDINSTR);
				}
				}
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(178);
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

	public static class VarWithNumberContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public ArithmeticContext arithmetic() {
			return getRuleContext(ArithmeticContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public VarWithNumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varWithNumber; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterVarWithNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitVarWithNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitVarWithNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarWithNumberContext varWithNumber() throws RecognitionException {
		VarWithNumberContext _localctx = new VarWithNumberContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_varWithNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			literal();
			setState(181);
			arithmetic();
			setState(182);
			number();
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

	public static class LoopCodeParamContext extends ParserRuleContext {
		public CodeBlockContext codeBlock() {
			return getRuleContext(CodeBlockContext.class,0);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LoopCodeParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loopCodeParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterLoopCodeParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitLoopCodeParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitLoopCodeParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoopCodeParamContext loopCodeParam() throws RecognitionException {
		LoopCodeParamContext _localctx = new LoopCodeParamContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_loopCodeParam);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(184);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(185);
				string();
				}
				break;
			case LITERAL:
				{
				setState(186);
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

	public static class ConditionSimpleContext extends ParserRuleContext {
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
		public ConditionSimpleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionSimple; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterConditionSimple(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitConditionSimple(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitConditionSimple(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionSimpleContext conditionSimple() throws RecognitionException {
		ConditionSimpleContext _localctx = new ConditionSimpleContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_conditionSimple);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			param();
			setState(190);
			match(SEPARATOR);
			setState(191);
			match(QUOTEMARK);
			setState(192);
			compare();
			setState(193);
			match(QUOTEMARK);
			setState(194);
			match(SEPARATOR);
			setState(195);
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

	public static class IfTrueContext extends ParserRuleContext {
		public CodeBlockContext codeBlock() {
			return getRuleContext(CodeBlockContext.class,0);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public IfTrueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifTrue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterIfTrue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitIfTrue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitIfTrue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfTrueContext ifTrue() throws RecognitionException {
		IfTrueContext _localctx = new IfTrueContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_ifTrue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(197);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(198);
				string();
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

	public static class IfFalseContext extends ParserRuleContext {
		public CodeBlockContext codeBlock() {
			return getRuleContext(CodeBlockContext.class,0);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public IfFalseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifFalse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterIfFalse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitIfFalse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitIfFalse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfFalseContext ifFalse() throws RecognitionException {
		IfFalseContext _localctx = new IfFalseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_ifFalse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(201);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(202);
				string();
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

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(AidemMediaParser.COMMENT, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaListener ) ((AidemMediaListener)listener).exitComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaVisitor ) return ((AidemMediaVisitor<? extends T>)visitor).visitComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			match(COMMENT);
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
		enterRule(_localctx, 22, RULE_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(STARTEXPR);
			setState(236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << VARREF) | (1L << STARTEXPR) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(209);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(208);
					arithmetic();
					}
					break;
				}
				setState(228);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
				case 1:
					{
					setState(211);
					literal();
					}
					break;
				case 2:
					{
					setState(212);
					string();
					}
					break;
				case 3:
					{
					setState(214);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
						{
						setState(213);
						arithmetic();
						}
					}

					setState(216);
					number();
					}
					break;
				case 4:
					{
					setState(218);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
						{
						setState(217);
						arithmetic();
						}
					}

					setState(220);
					floatNumber();
					}
					break;
				case 5:
					{
					setState(221);
					modulo();
					}
					break;
				case 6:
					{
					setState(222);
					iterator();
					}
					break;
				case 7:
					{
					setState(223);
					functionFire();
					}
					break;
				case 8:
					{
					setState(224);
					expression();
					}
					break;
				case 9:
					{
					setState(225);
					struct();
					}
					break;
				case 10:
					{
					setState(226);
					stringRef();
					}
					break;
				case 11:
					{
					setState(227);
					variable();
					}
					break;
				}
				setState(232);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(230);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(231);
					match(STRREF);
					}
					break;
				}
				}
				}
				setState(238);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(239);
			match(STOPEXPR);
			setState(243);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(240);
					match(ENDINSTR);
					}
					} 
				}
				setState(245);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
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
		enterRule(_localctx, 24, RULE_script);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(253);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(246);
					codeBlock();
					}
					break;
				case 2:
					{
					setState(247);
					expression();
					}
					break;
				case 3:
					{
					setState(248);
					string();
					}
					break;
				case 4:
					{
					setState(249);
					ifInstr();
					}
					break;
				case 5:
					{
					setState(250);
					loopInstr();
					}
					break;
				case 6:
					{
					setState(251);
					whileInstr();
					}
					break;
				case 7:
					{
					setState(252);
					string();
					}
					break;
				}
				}
				setState(257);
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
		enterRule(_localctx, 26, RULE_param);
		int _la;
		try {
			setState(275);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(258);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(259);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(260);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(261);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(262);
				literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(264);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
					{
					setState(263);
					arithmetic();
					}
				}

				setState(266);
				number();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(268);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
					{
					setState(267);
					arithmetic();
					}
				}

				setState(270);
				floatNumber();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(271);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(272);
				iterator();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(273);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(274);
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
		enterRule(_localctx, 28, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << ITERATOR) | (1L << LITERAL) | (1L << LOGIC) | (1L << STRREF))) != 0)) {
				{
				{
				setState(278);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOGIC) {
					{
					setState(277);
					logic();
					}
				}

				setState(280);
				conditionPart();
				}
				}
				setState(285);
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
		enterRule(_localctx, 30, RULE_conditionPart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				{
				setState(286);
				literal();
				}
				break;
			case 2:
				{
				setState(287);
				functionFire();
				}
				break;
			case 3:
				{
				setState(288);
				struct();
				}
				break;
			case 4:
				{
				setState(289);
				expression();
				}
				break;
			case 5:
				{
				setState(290);
				iterator();
				}
				break;
			}
			setState(293);
			compare();
			setState(294);
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
		enterRule(_localctx, 32, RULE_behFire);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			literal();
			setState(297);
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
		enterRule(_localctx, 34, RULE_modulo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				{
				setState(299);
				literal();
				}
				break;
			case 2:
				{
				setState(300);
				iterator();
				}
				break;
			case 3:
				{
				setState(302);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
					{
					setState(301);
					arithmetic();
					}
				}

				setState(304);
				number();
				}
				break;
			case 4:
				{
				setState(306);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
					{
					setState(305);
					arithmetic();
					}
				}

				setState(308);
				floatNumber();
				}
				break;
			case 5:
				{
				setState(309);
				functionFire();
				}
				break;
			case 6:
				{
				setState(310);
				struct();
				}
				break;
			case 7:
				{
				setState(311);
				expression();
				}
				break;
			}
			setState(314);
			match(MOD);
			setState(327);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(315);
				literal();
				}
				break;
			case 2:
				{
				setState(317);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
					{
					setState(316);
					arithmetic();
					}
				}

				setState(319);
				number();
				}
				break;
			case 3:
				{
				setState(321);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
					{
					setState(320);
					arithmetic();
					}
				}

				setState(323);
				floatNumber();
				}
				break;
			case 4:
				{
				setState(324);
				functionFire();
				}
				break;
			case 5:
				{
				setState(325);
				struct();
				}
				break;
			case 6:
				{
				setState(326);
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
		enterRule(_localctx, 36, RULE_iterator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(329);
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
		public TerminalNode BOOLEAN() { return getToken(AidemMediaParser.BOOLEAN, 0); }
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
		enterRule(_localctx, 38, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			match(QUOTEMARK);
			setState(365);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(352); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(352);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
					case 1:
						{
						setState(332);
						literal();
						}
						break;
					case 2:
						{
						setState(333);
						match(FIREFUNC);
						}
						break;
					case 3:
						{
						setState(335);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
							{
							setState(334);
							arithmetic();
							}
						}

						setState(339);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case NUMBER:
							{
							setState(337);
							number();
							}
							break;
						case FLOAT:
							{
							setState(338);
							floatNumber();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						}
						break;
					case 4:
						{
						setState(341);
						compare();
						}
						break;
					case 5:
						{
						setState(342);
						match(SLASH);
						}
						break;
					case 6:
						{
						setState(343);
						struct();
						}
						break;
					case 7:
						{
						setState(344);
						match(LPAREN);
						}
						break;
					case 8:
						{
						setState(345);
						match(RPAREN);
						}
						break;
					case 9:
						{
						setState(346);
						match(SEPARATOR);
						}
						break;
					case 10:
						{
						setState(347);
						arithmetic();
						}
						break;
					case 11:
						{
						setState(348);
						match(VARREF);
						}
						break;
					case 12:
						{
						setState(349);
						iterator();
						}
						break;
					case 13:
						{
						setState(350);
						expression();
						}
						break;
					case 14:
						{
						setState(351);
						functionFire();
						}
						break;
					}
					}
					setState(354); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << VARREF) | (1L << STARTEXPR) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << FIREFUNC) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << SEPARATOR) | (1L << STRREF))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(356);
				variable();
				setState(361);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(357);
					match(SLASH);
					setState(359);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(358);
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
				setState(363);
				string();
				}
				break;
			case 4:
				{
				setState(364);
				match(BOOLEAN);
				}
				break;
			}
			setState(367);
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
		enterRule(_localctx, 40, RULE_instr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			match(DIV);
			setState(370);
			literal();
			setState(371);
			match(LPAREN);
			setState(377);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(372);
				param();
				setState(375);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(373);
					match(SEPARATOR);
					setState(374);
					param();
					}
				}

				}
				break;
			}
			setState(379);
			match(RPAREN);
			setState(380);
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
		enterRule(_localctx, 42, RULE_stringRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(382);
			match(STRREF);
			setState(385);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(383);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(384);
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
		enterRule(_localctx, 44, RULE_struct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(387);
			literal();
			setState(388);
			match(STRUCTFIELD);
			setState(389);
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
		enterRule(_localctx, 46, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(391);
			match(VARREF);
			setState(394);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
				{
				setState(392);
				literal();
				}
				break;
			case NUMBER:
				{
				setState(393);
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
		enterRule(_localctx, 48, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(396);
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
		enterRule(_localctx, 50, RULE_floatNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(398);
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
		enterRule(_localctx, 52, RULE_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(400);
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
		public TerminalNode ADD() { return getToken(AidemMediaParser.ADD, 0); }
		public TerminalNode SUBTRACT() { return getToken(AidemMediaParser.SUBTRACT, 0); }
		public TerminalNode MULT() { return getToken(AidemMediaParser.MULT, 0); }
		public TerminalNode DIV() { return getToken(AidemMediaParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(AidemMediaParser.MOD, 0); }
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
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
		enterRule(_localctx, 54, RULE_arithmetic);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(402);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << ADD) | (1L << SUBTRACT) | (1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
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
		enterRule(_localctx, 56, RULE_logic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404);
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
		enterRule(_localctx, 58, RULE_compare);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(406);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\'\u019b\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2I\n\2\3\2\3\2\3\2\3\2\3\2\7\2P\n\2\f"+
		"\2\16\2S\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3`\n\3\f\3"+
		"\16\3c\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4q\n\4\3"+
		"\4\3\4\7\4u\n\4\f\4\16\4x\13\4\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0080\n\5\3"+
		"\5\3\5\3\5\3\5\5\5\u0086\n\5\3\5\7\5\u0089\n\5\f\5\16\5\u008c\13\5\3\5"+
		"\3\5\7\5\u0090\n\5\f\5\16\5\u0093\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\5\6\u00a1\n\6\3\6\3\6\7\6\u00a5\n\6\f\6\16\6\u00a8\13"+
		"\6\7\6\u00aa\n\6\f\6\16\6\u00ad\13\6\3\6\7\6\u00b0\n\6\f\6\16\6\u00b3"+
		"\13\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\5\b\u00be\n\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\n\3\n\5\n\u00ca\n\n\3\13\3\13\5\13\u00ce\n\13\3\f"+
		"\3\f\3\r\3\r\5\r\u00d4\n\r\3\r\3\r\3\r\5\r\u00d9\n\r\3\r\3\r\5\r\u00dd"+
		"\n\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00e7\n\r\3\r\3\r\5\r\u00eb\n"+
		"\r\7\r\u00ed\n\r\f\r\16\r\u00f0\13\r\3\r\3\r\7\r\u00f4\n\r\f\r\16\r\u00f7"+
		"\13\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\7\16\u0100\n\16\f\16\16\16\u0103"+
		"\13\16\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u010b\n\17\3\17\3\17\5\17\u010f"+
		"\n\17\3\17\3\17\3\17\3\17\3\17\5\17\u0116\n\17\3\20\5\20\u0119\n\20\3"+
		"\20\7\20\u011c\n\20\f\20\16\20\u011f\13\20\3\21\3\21\3\21\3\21\3\21\5"+
		"\21\u0126\n\21\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\5\23\u0131"+
		"\n\23\3\23\3\23\5\23\u0135\n\23\3\23\3\23\3\23\3\23\5\23\u013b\n\23\3"+
		"\23\3\23\3\23\5\23\u0140\n\23\3\23\3\23\5\23\u0144\n\23\3\23\3\23\3\23"+
		"\3\23\5\23\u014a\n\23\3\24\3\24\3\25\3\25\3\25\3\25\5\25\u0152\n\25\3"+
		"\25\3\25\5\25\u0156\n\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\6\25\u0163\n\25\r\25\16\25\u0164\3\25\3\25\3\25\5\25\u016a"+
		"\n\25\5\25\u016c\n\25\3\25\3\25\5\25\u0170\n\25\3\25\3\25\3\26\3\26\3"+
		"\26\3\26\3\26\3\26\5\26\u017a\n\26\5\26\u017c\n\26\3\26\3\26\3\26\3\27"+
		"\3\27\3\27\5\27\u0184\n\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31\5\31\u018d"+
		"\n\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3\37"+
		"\2\2 \2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<\2"+
		"\4\4\2\7\b\27\33\3\2\17\24\2\u01ee\2>\3\2\2\2\4T\3\2\2\2\6d\3\2\2\2\b"+
		"\177\3\2\2\2\n\u0094\3\2\2\2\f\u00b6\3\2\2\2\16\u00bd\3\2\2\2\20\u00bf"+
		"\3\2\2\2\22\u00c9\3\2\2\2\24\u00cd\3\2\2\2\26\u00cf\3\2\2\2\30\u00d1\3"+
		"\2\2\2\32\u0101\3\2\2\2\34\u0115\3\2\2\2\36\u011d\3\2\2\2 \u0125\3\2\2"+
		"\2\"\u012a\3\2\2\2$\u013a\3\2\2\2&\u014b\3\2\2\2(\u014d\3\2\2\2*\u0173"+
		"\3\2\2\2,\u0180\3\2\2\2.\u0185\3\2\2\2\60\u0189\3\2\2\2\62\u018e\3\2\2"+
		"\2\64\u0190\3\2\2\2\66\u0192\3\2\2\28\u0194\3\2\2\2:\u0196\3\2\2\2<\u0198"+
		"\3\2\2\2>?\7\3\2\2?H\7\7\2\2@A\7%\2\2AB\5\36\20\2BC\7%\2\2CD\7#\2\2DI"+
		"\3\2\2\2EF\5\20\t\2FG\7#\2\2GI\3\2\2\2H@\3\2\2\2HE\3\2\2\2IJ\3\2\2\2J"+
		"K\5\22\n\2KL\7#\2\2LM\5\24\13\2MQ\7\b\2\2NP\7\25\2\2ON\3\2\2\2PS\3\2\2"+
		"\2QO\3\2\2\2QR\3\2\2\2R\3\3\2\2\2SQ\3\2\2\2TU\7\4\2\2UV\7\7\2\2VW\5\16"+
		"\b\2WX\7#\2\2XY\5\34\17\2YZ\7#\2\2Z[\5\34\17\2[\\\7#\2\2\\]\5\34\17\2"+
		"]a\7\b\2\2^`\7\25\2\2_^\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2\2b\5\3\2\2"+
		"\2ca\3\2\2\2de\7\5\2\2ef\7\7\2\2fg\5\34\17\2gh\7#\2\2hi\7%\2\2ij\5<\37"+
		"\2jk\7%\2\2kl\7#\2\2lm\5\34\17\2mp\7#\2\2nq\5\n\6\2oq\5(\25\2pn\3\2\2"+
		"\2po\3\2\2\2qr\3\2\2\2rv\7\b\2\2su\7\25\2\2ts\3\2\2\2ux\3\2\2\2vt\3\2"+
		"\2\2vw\3\2\2\2w\7\3\2\2\2xv\3\2\2\2y\u0080\5\66\34\2z\u0080\5&\24\2{\u0080"+
		"\5,\27\2|\u0080\5.\30\2}\u0080\5\60\31\2~\u0080\5\f\7\2\177y\3\2\2\2\177"+
		"z\3\2\2\2\177{\3\2\2\2\177|\3\2\2\2\177}\3\2\2\2\177~\3\2\2\2\u0080\u0081"+
		"\3\2\2\2\u0081\u0082\7\26\2\2\u0082\u0083\5\66\34\2\u0083\u008a\7\7\2"+
		"\2\u0084\u0086\7#\2\2\u0085\u0084\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087"+
		"\3\2\2\2\u0087\u0089\5\34\17\2\u0088\u0085\3\2\2\2\u0089\u008c\3\2\2\2"+
		"\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008d\3\2\2\2\u008c\u008a"+
		"\3\2\2\2\u008d\u0091\7\b\2\2\u008e\u0090\7\25\2\2\u008f\u008e\3\2\2\2"+
		"\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\t\3"+
		"\2\2\2\u0093\u0091\3\2\2\2\u0094\u00ab\7\n\2\2\u0095\u00aa\5\b\5\2\u0096"+
		"\u00aa\5\2\2\2\u0097\u00aa\5\4\3\2\u0098\u00aa\5\6\4\2\u0099\u00aa\5*"+
		"\26\2\u009a\u00aa\5\"\22\2\u009b\u00aa\5\30\r\2\u009c\u00aa\5\n\6\2\u009d"+
		"\u00a1\5\66\34\2\u009e\u00a1\5\64\33\2\u009f\u00a1\5\62\32\2\u00a0\u009d"+
		"\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0\u009f\3\2\2\2\u00a1\u00aa\3\2\2\2\u00a2"+
		"\u00a6\5\26\f\2\u00a3\u00a5\7\25\2\2\u00a4\u00a3\3\2\2\2\u00a5\u00a8\3"+
		"\2\2\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00aa\3\2\2\2\u00a8"+
		"\u00a6\3\2\2\2\u00a9\u0095\3\2\2\2\u00a9\u0096\3\2\2\2\u00a9\u0097\3\2"+
		"\2\2\u00a9\u0098\3\2\2\2\u00a9\u0099\3\2\2\2\u00a9\u009a\3\2\2\2\u00a9"+
		"\u009b\3\2\2\2\u00a9\u009c\3\2\2\2\u00a9\u00a0\3\2\2\2\u00a9\u00a2\3\2"+
		"\2\2\u00aa\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac"+
		"\u00b1\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00b0\7\25\2\2\u00af\u00ae\3"+
		"\2\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2"+
		"\u00b4\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b4\u00b5\7\13\2\2\u00b5\13\3\2\2"+
		"\2\u00b6\u00b7\5\66\34\2\u00b7\u00b8\58\35\2\u00b8\u00b9\5\62\32\2\u00b9"+
		"\r\3\2\2\2\u00ba\u00be\5\n\6\2\u00bb\u00be\5(\25\2\u00bc\u00be\5\66\34"+
		"\2\u00bd\u00ba\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00bc\3\2\2\2\u00be\17"+
		"\3\2\2\2\u00bf\u00c0\5\34\17\2\u00c0\u00c1\7#\2\2\u00c1\u00c2\7%\2\2\u00c2"+
		"\u00c3\5<\37\2\u00c3\u00c4\7%\2\2\u00c4\u00c5\7#\2\2\u00c5\u00c6\5\34"+
		"\17\2\u00c6\21\3\2\2\2\u00c7\u00ca\5\n\6\2\u00c8\u00ca\5(\25\2\u00c9\u00c7"+
		"\3\2\2\2\u00c9\u00c8\3\2\2\2\u00ca\23\3\2\2\2\u00cb\u00ce\5\n\6\2\u00cc"+
		"\u00ce\5(\25\2\u00cd\u00cb\3\2\2\2\u00cd\u00cc\3\2\2\2\u00ce\25\3\2\2"+
		"\2\u00cf\u00d0\7\6\2\2\u00d0\27\3\2\2\2\u00d1\u00ee\7\f\2\2\u00d2\u00d4"+
		"\58\35\2\u00d3\u00d2\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00e6\3\2\2\2\u00d5"+
		"\u00e7\5\66\34\2\u00d6\u00e7\5(\25\2\u00d7\u00d9\58\35\2\u00d8\u00d7\3"+
		"\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00e7\5\62\32\2\u00db"+
		"\u00dd\58\35\2\u00dc\u00db\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00de\3\2"+
		"\2\2\u00de\u00e7\5\64\33\2\u00df\u00e7\5$\23\2\u00e0\u00e7\5&\24\2\u00e1"+
		"\u00e7\5\b\5\2\u00e2\u00e7\5\30\r\2\u00e3\u00e7\5.\30\2\u00e4\u00e7\5"+
		",\27\2\u00e5\u00e7\5\60\31\2\u00e6\u00d5\3\2\2\2\u00e6\u00d6\3\2\2\2\u00e6"+
		"\u00d8\3\2\2\2\u00e6\u00dc\3\2\2\2\u00e6\u00df\3\2\2\2\u00e6\u00e0\3\2"+
		"\2\2\u00e6\u00e1\3\2\2\2\u00e6\u00e2\3\2\2\2\u00e6\u00e3\3\2\2\2\u00e6"+
		"\u00e4\3\2\2\2\u00e6\u00e5\3\2\2\2\u00e7\u00ea\3\2\2\2\u00e8\u00eb\58"+
		"\35\2\u00e9\u00eb\7&\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00e9\3\2\2\2\u00ea"+
		"\u00eb\3\2\2\2\u00eb\u00ed\3\2\2\2\u00ec\u00d3\3\2\2\2\u00ed\u00f0\3\2"+
		"\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f1\3\2\2\2\u00f0"+
		"\u00ee\3\2\2\2\u00f1\u00f5\7\r\2\2\u00f2\u00f4\7\25\2\2\u00f3\u00f2\3"+
		"\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6"+
		"\31\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f8\u0100\5\n\6\2\u00f9\u0100\5\30\r"+
		"\2\u00fa\u0100\5(\25\2\u00fb\u0100\5\2\2\2\u00fc\u0100\5\4\3\2\u00fd\u0100"+
		"\5\6\4\2\u00fe\u0100\5(\25\2\u00ff\u00f8\3\2\2\2\u00ff\u00f9\3\2\2\2\u00ff"+
		"\u00fa\3\2\2\2\u00ff\u00fb\3\2\2\2\u00ff\u00fc\3\2\2\2\u00ff\u00fd\3\2"+
		"\2\2\u00ff\u00fe\3\2\2\2\u0100\u0103\3\2\2\2\u0101\u00ff\3\2\2\2\u0101"+
		"\u0102\3\2\2\2\u0102\33\3\2\2\2\u0103\u0101\3\2\2\2\u0104\u0116\5\b\5"+
		"\2\u0105\u0116\7 \2\2\u0106\u0116\5\60\31\2\u0107\u0116\5(\25\2\u0108"+
		"\u0116\5\66\34\2\u0109\u010b\58\35\2\u010a\u0109\3\2\2\2\u010a\u010b\3"+
		"\2\2\2\u010b\u010c\3\2\2\2\u010c\u0116\5\62\32\2\u010d\u010f\58\35\2\u010e"+
		"\u010d\3\2\2\2\u010e\u010f\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0116\5\64"+
		"\33\2\u0111\u0116\5\30\r\2\u0112\u0116\5&\24\2\u0113\u0116\5.\30\2\u0114"+
		"\u0116\5,\27\2\u0115\u0104\3\2\2\2\u0115\u0105\3\2\2\2\u0115\u0106\3\2"+
		"\2\2\u0115\u0107\3\2\2\2\u0115\u0108\3\2\2\2\u0115\u010a\3\2\2\2\u0115"+
		"\u010e\3\2\2\2\u0115\u0111\3\2\2\2\u0115\u0112\3\2\2\2\u0115\u0113\3\2"+
		"\2\2\u0115\u0114\3\2\2\2\u0116\35\3\2\2\2\u0117\u0119\5:\36\2\u0118\u0117"+
		"\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011a\3\2\2\2\u011a\u011c\5 \21\2\u011b"+
		"\u0118\3\2\2\2\u011c\u011f\3\2\2\2\u011d\u011b\3\2\2\2\u011d\u011e\3\2"+
		"\2\2\u011e\37\3\2\2\2\u011f\u011d\3\2\2\2\u0120\u0126\5\66\34\2\u0121"+
		"\u0126\5\b\5\2\u0122\u0126\5.\30\2\u0123\u0126\5\30\r\2\u0124\u0126\5"+
		"&\24\2\u0125\u0120\3\2\2\2\u0125\u0121\3\2\2\2\u0125\u0122\3\2\2\2\u0125"+
		"\u0123\3\2\2\2\u0125\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0128\5<"+
		"\37\2\u0128\u0129\5\34\17\2\u0129!\3\2\2\2\u012a\u012b\5\66\34\2\u012b"+
		"\u012c\7\25\2\2\u012c#\3\2\2\2\u012d\u013b\5\66\34\2\u012e\u013b\5&\24"+
		"\2\u012f\u0131\58\35\2\u0130\u012f\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0132"+
		"\3\2\2\2\u0132\u013b\5\62\32\2\u0133\u0135\58\35\2\u0134\u0133\3\2\2\2"+
		"\u0134\u0135\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u013b\5\64\33\2\u0137\u013b"+
		"\5\b\5\2\u0138\u013b\5.\30\2\u0139\u013b\5\30\r\2\u013a\u012d\3\2\2\2"+
		"\u013a\u012e\3\2\2\2\u013a\u0130\3\2\2\2\u013a\u0134\3\2\2\2\u013a\u0137"+
		"\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c"+
		"\u0149\7\33\2\2\u013d\u014a\5\66\34\2\u013e\u0140\58\35\2\u013f\u013e"+
		"\3\2\2\2\u013f\u0140\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u014a\5\62\32\2"+
		"\u0142\u0144\58\35\2\u0143\u0142\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145"+
		"\3\2\2\2\u0145\u014a\5\64\33\2\u0146\u014a\5\b\5\2\u0147\u014a\5.\30\2"+
		"\u0148\u014a\5\30\r\2\u0149\u013d\3\2\2\2\u0149\u013f\3\2\2\2\u0149\u0143"+
		"\3\2\2\2\u0149\u0146\3\2\2\2\u0149\u0147\3\2\2\2\u0149\u0148\3\2\2\2\u014a"+
		"%\3\2\2\2\u014b\u014c\7\37\2\2\u014c\'\3\2\2\2\u014d\u016f\7%\2\2\u014e"+
		"\u0163\5\66\34\2\u014f\u0163\7\26\2\2\u0150\u0152\58\35\2\u0151\u0150"+
		"\3\2\2\2\u0151\u0152\3\2\2\2\u0152\u0155\3\2\2\2\u0153\u0156\5\62\32\2"+
		"\u0154\u0156\5\64\33\2\u0155\u0153\3\2\2\2\u0155\u0154\3\2\2\2\u0156\u0163"+
		"\3\2\2\2\u0157\u0163\5<\37\2\u0158\u0163\7\16\2\2\u0159\u0163\5.\30\2"+
		"\u015a\u0163\7\7\2\2\u015b\u0163\7\b\2\2\u015c\u0163\7#\2\2\u015d\u0163"+
		"\58\35\2\u015e\u0163\7\t\2\2\u015f\u0163\5&\24\2\u0160\u0163\5\30\r\2"+
		"\u0161\u0163\5\b\5\2\u0162\u014e\3\2\2\2\u0162\u014f\3\2\2\2\u0162\u0151"+
		"\3\2\2\2\u0162\u0157\3\2\2\2\u0162\u0158\3\2\2\2\u0162\u0159\3\2\2\2\u0162"+
		"\u015a\3\2\2\2\u0162\u015b\3\2\2\2\u0162\u015c\3\2\2\2\u0162\u015d\3\2"+
		"\2\2\u0162\u015e\3\2\2\2\u0162\u015f\3\2\2\2\u0162\u0160\3\2\2\2\u0162"+
		"\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164\u0162\3\2\2\2\u0164\u0165\3\2"+
		"\2\2\u0165\u0170\3\2\2\2\u0166\u016b\5\60\31\2\u0167\u0169\7\16\2\2\u0168"+
		"\u016a\5\66\34\2\u0169\u0168\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016c\3"+
		"\2\2\2\u016b\u0167\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u0170\3\2\2\2\u016d"+
		"\u0170\5(\25\2\u016e\u0170\7 \2\2\u016f\u0162\3\2\2\2\u016f\u0166\3\2"+
		"\2\2\u016f\u016d\3\2\2\2\u016f\u016e\3\2\2\2\u016f\u0170\3\2\2\2\u0170"+
		"\u0171\3\2\2\2\u0171\u0172\7%\2\2\u0172)\3\2\2\2\u0173\u0174\7\32\2\2"+
		"\u0174\u0175\5\66\34\2\u0175\u017b\7\7\2\2\u0176\u0179\5\34\17\2\u0177"+
		"\u0178\7#\2\2\u0178\u017a\5\34\17\2\u0179\u0177\3\2\2\2\u0179\u017a\3"+
		"\2\2\2\u017a\u017c\3\2\2\2\u017b\u0176\3\2\2\2\u017b\u017c\3\2\2\2\u017c"+
		"\u017d\3\2\2\2\u017d\u017e\7\b\2\2\u017e\u017f\7\25\2\2\u017f+\3\2\2\2"+
		"\u0180\u0183\7&\2\2\u0181\u0184\5\30\r\2\u0182\u0184\5\66\34\2\u0183\u0181"+
		"\3\2\2\2\u0183\u0182\3\2\2\2\u0184-\3\2\2\2\u0185\u0186\5\66\34\2\u0186"+
		"\u0187\7$\2\2\u0187\u0188\5\66\34\2\u0188/\3\2\2\2\u0189\u018c\7\t\2\2"+
		"\u018a\u018d\5\66\34\2\u018b\u018d\5\62\32\2\u018c\u018a\3\2\2\2\u018c"+
		"\u018b\3\2\2\2\u018d\61\3\2\2\2\u018e\u018f\7\34\2\2\u018f\63\3\2\2\2"+
		"\u0190\u0191\7\35\2\2\u0191\65\3\2\2\2\u0192\u0193\7!\2\2\u0193\67\3\2"+
		"\2\2\u0194\u0195\t\2\2\2\u01959\3\2\2\2\u0196\u0197\7\"\2\2\u0197;\3\2"+
		"\2\2\u0198\u0199\t\3\2\2\u0199=\3\2\2\2\63HQapv\177\u0085\u008a\u0091"+
		"\u00a0\u00a6\u00a9\u00ab\u00b1\u00bd\u00c9\u00cd\u00d3\u00d8\u00dc\u00e6"+
		"\u00ea\u00ee\u00f5\u00ff\u0101\u010a\u010e\u0115\u0118\u011d\u0125\u0130"+
		"\u0134\u013a\u013f\u0143\u0149\u0151\u0155\u0162\u0164\u0169\u016b\u016f"+
		"\u0179\u017b\u0183\u018c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}