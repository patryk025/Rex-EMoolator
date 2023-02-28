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
		RULE_variable = 16, RULE_number = 17, RULE_floatNumber = 18, RULE_literal = 19, 
		RULE_arithmetic = 20, RULE_logic = 21;
	private static String[] makeRuleNames() {
		return new String[] {
			"ifInstr", "loopInstr", "whileInstr", "functionFire", "codeBlock", "expression", 
			"script", "param", "condition", "conditionPart", "behFire", "modulo", 
			"string", "instr", "stringRef", "struct", "variable", "number", "floatNumber", 
			"literal", "arithmetic", "logic"
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
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(T__0);
			setState(45);
			match(LPAREN);
			setState(60);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(46);
				match(QUOTEMARK);
				setState(47);
				condition();
				setState(48);
				match(QUOTEMARK);
				setState(49);
				match(SEPARATOR);
				}
				break;
			case 2:
				{
				setState(51);
				param();
				setState(52);
				match(SEPARATOR);
				setState(53);
				match(QUOTEMARK);
				setState(54);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(55);
				match(QUOTEMARK);
				setState(56);
				match(SEPARATOR);
				setState(57);
				param();
				setState(58);
				match(SEPARATOR);
				}
				break;
			}
			setState(64);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(62);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(63);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(66);
			match(SEPARATOR);
			setState(69);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(67);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(68);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(71);
			match(RPAREN);
			setState(75);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(72);
					match(ENDINSTR);
					}
					} 
				}
				setState(77);
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
			setState(78);
			match(T__1);
			setState(79);
			match(LPAREN);
			setState(80);
			codeBlock();
			setState(81);
			match(SEPARATOR);
			setState(91);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(82);
				string();
				}
				break;
			case 2:
				{
				setState(83);
				literal();
				}
				break;
			case 3:
				{
				setState(84);
				expression();
				}
				break;
			case 4:
				{
				setState(86);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(85);
					arithmetic();
					}
				}

				setState(88);
				number();
				}
				break;
			case 5:
				{
				setState(89);
				functionFire();
				}
				break;
			case 6:
				{
				setState(90);
				struct();
				}
				break;
			}
			setState(93);
			match(SEPARATOR);
			setState(103);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(94);
				string();
				}
				break;
			case 2:
				{
				setState(95);
				literal();
				}
				break;
			case 3:
				{
				setState(96);
				expression();
				}
				break;
			case 4:
				{
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(97);
					arithmetic();
					}
				}

				setState(100);
				number();
				}
				break;
			case 5:
				{
				setState(101);
				functionFire();
				}
				break;
			case 6:
				{
				setState(102);
				struct();
				}
				break;
			}
			setState(105);
			match(SEPARATOR);
			setState(115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(106);
				string();
				}
				break;
			case 2:
				{
				setState(107);
				literal();
				}
				break;
			case 3:
				{
				setState(108);
				expression();
				}
				break;
			case 4:
				{
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(109);
					arithmetic();
					}
				}

				setState(112);
				number();
				}
				break;
			case 5:
				{
				setState(113);
				functionFire();
				}
				break;
			case 6:
				{
				setState(114);
				struct();
				}
				break;
			}
			setState(117);
			match(RPAREN);
			setState(121);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(118);
					match(ENDINSTR);
					}
					} 
				}
				setState(123);
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
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			match(T__2);
			setState(125);
			match(LPAREN);
			setState(126);
			param();
			setState(127);
			match(SEPARATOR);
			setState(128);
			match(QUOTEMARK);
			setState(129);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(130);
			match(QUOTEMARK);
			setState(131);
			match(SEPARATOR);
			setState(132);
			param();
			setState(133);
			match(SEPARATOR);
			setState(136);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(134);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(135);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(138);
			match(RPAREN);
			setState(142);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(139);
					match(ENDINSTR);
					}
					} 
				}
				setState(144);
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
		public TerminalNode ITERATOR() { return getToken(AidemMediaParser.ITERATOR, 0); }
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
			setState(150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(145);
				literal();
				}
				break;
			case 2:
				{
				setState(146);
				match(ITERATOR);
				}
				break;
			case 3:
				{
				setState(147);
				stringRef();
				}
				break;
			case 4:
				{
				setState(148);
				struct();
				}
				break;
			case 5:
				{
				setState(149);
				variable();
				}
				break;
			}
			setState(152);
			match(FIREFUNC);
			setState(153);
			literal();
			setState(154);
			match(LPAREN);
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << BOOLEAN) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(155);
					match(SEPARATOR);
					}
				}

				setState(158);
				param();
				}
				}
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(164);
			match(RPAREN);
			setState(168);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(165);
					match(ENDINSTR);
					}
					} 
				}
				setState(170);
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
			setState(171);
			match(STARTCODE);
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << VARREF) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << STRREF))) != 0)) {
				{
				setState(191);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(172);
					functionFire();
					}
					break;
				case 2:
					{
					setState(173);
					ifInstr();
					}
					break;
				case 3:
					{
					setState(174);
					loopInstr();
					}
					break;
				case 4:
					{
					setState(175);
					whileInstr();
					}
					break;
				case 5:
					{
					setState(176);
					instr();
					}
					break;
				case 6:
					{
					setState(177);
					behFire();
					}
					break;
				case 7:
					{
					setState(178);
					expression();
					}
					break;
				case 8:
					{
					setState(179);
					codeBlock();
					}
					break;
				case 9:
					{
					setState(183);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case LITERAL:
						{
						setState(180);
						literal();
						}
						break;
					case FLOAT:
						{
						setState(181);
						floatNumber();
						}
						break;
					case NUMBER:
						{
						setState(182);
						number();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(188);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(185);
							match(ENDINSTR);
							}
							} 
						}
						setState(190);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
					}
					}
					break;
				}
				}
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(196);
				match(ENDINSTR);
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(202);
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
			setState(204);
			match(STARTEXPR);
			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(208);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(205);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(206);
					match(DIV);
					}
					break;
				case 3:
					{
					setState(207);
					match(STRREF);
					}
					break;
				}
				setState(227);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(210);
					literal();
					}
					break;
				case 2:
					{
					setState(211);
					string();
					}
					break;
				case 3:
					{
					setState(213);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(212);
						arithmetic();
						}
					}

					setState(215);
					number();
					}
					break;
				case 4:
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
					floatNumber();
					}
					break;
				case 5:
					{
					setState(220);
					modulo();
					}
					break;
				case 6:
					{
					setState(221);
					match(ITERATOR);
					}
					break;
				case 7:
					{
					setState(222);
					functionFire();
					}
					break;
				case 8:
					{
					setState(223);
					expression();
					}
					break;
				case 9:
					{
					setState(224);
					struct();
					}
					break;
				case 10:
					{
					setState(225);
					stringRef();
					}
					break;
				case 11:
					{
					setState(226);
					variable();
					}
					break;
				}
				setState(231);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(229);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(230);
					match(STRREF);
					}
					break;
				}
				}
				}
				setState(237);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(238);
			match(STOPEXPR);
			setState(242);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(239);
					match(ENDINSTR);
					}
					} 
				}
				setState(244);
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
			setState(254);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(252);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					setState(245);
					codeBlock();
					}
					break;
				case 2:
					{
					setState(246);
					expression();
					}
					break;
				case 3:
					{
					setState(247);
					string();
					}
					break;
				case 4:
					{
					setState(248);
					ifInstr();
					}
					break;
				case 5:
					{
					setState(249);
					loopInstr();
					}
					break;
				case 6:
					{
					setState(250);
					whileInstr();
					}
					break;
				case 7:
					{
					setState(251);
					string();
					}
					break;
				}
				}
				setState(256);
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
			setState(274);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(257);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(258);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(259);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(260);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(261);
				literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(262);
					arithmetic();
					}
				}

				setState(265);
				number();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
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
				floatNumber();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(270);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(271);
				match(ITERATOR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(272);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(273);
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
			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << ITERATOR) | (1L << LITERAL) | (1L << LOGIC) | (1L << STRREF))) != 0)) {
				{
				{
				setState(277);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOGIC) {
					{
					setState(276);
					logic();
					}
				}

				setState(279);
				conditionPart();
				}
				}
				setState(284);
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
		public TerminalNode ITERATOR() { return getToken(AidemMediaParser.ITERATOR, 0); }
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(285);
				literal();
				}
				break;
			case 2:
				{
				setState(286);
				functionFire();
				}
				break;
			case 3:
				{
				setState(287);
				struct();
				}
				break;
			case 4:
				{
				setState(288);
				expression();
				}
				break;
			case 5:
				{
				setState(289);
				match(ITERATOR);
				}
				break;
			}
			setState(292);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(293);
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
			setState(295);
			literal();
			setState(296);
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
		public TerminalNode ITERATOR() { return getToken(AidemMediaParser.ITERATOR, 0); }
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
			setState(311);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(298);
				literal();
				}
				break;
			case 2:
				{
				setState(299);
				match(ITERATOR);
				}
				break;
			case 3:
				{
				setState(301);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(300);
					arithmetic();
					}
				}

				setState(303);
				number();
				}
				break;
			case 4:
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
				floatNumber();
				}
				break;
			case 5:
				{
				setState(308);
				functionFire();
				}
				break;
			case 6:
				{
				setState(309);
				struct();
				}
				break;
			case 7:
				{
				setState(310);
				expression();
				}
				break;
			}
			setState(313);
			match(MOD);
			setState(326);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(314);
				literal();
				}
				break;
			case 2:
				{
				setState(316);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(315);
					arithmetic();
					}
				}

				setState(318);
				number();
				}
				break;
			case 3:
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
				floatNumber();
				}
				break;
			case 4:
				{
				setState(323);
				functionFire();
				}
				break;
			case 5:
				{
				setState(324);
				struct();
				}
				break;
			case 6:
				{
				setState(325);
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
		public List<ArithmeticContext> arithmetic() {
			return getRuleContexts(ArithmeticContext.class);
		}
		public ArithmeticContext arithmetic(int i) {
			return getRuleContext(ArithmeticContext.class,i);
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
		enterRule(_localctx, 24, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			match(QUOTEMARK);
			setState(367);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(355); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(355);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
					case 1:
						{
						setState(329);
						literal();
						}
						break;
					case 2:
						{
						setState(330);
						match(FIREFUNC);
						}
						break;
					case 3:
						{
						setState(332);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(331);
							arithmetic();
							}
						}

						setState(334);
						number();
						}
						break;
					case 4:
						{
						setState(336);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(335);
							arithmetic();
							}
						}

						setState(338);
						floatNumber();
						}
						break;
					case 5:
						{
						setState(339);
						match(LSS);
						}
						break;
					case 6:
						{
						setState(340);
						match(LEQ);
						}
						break;
					case 7:
						{
						setState(341);
						match(GEQ);
						}
						break;
					case 8:
						{
						setState(342);
						match(GTR);
						}
						break;
					case 9:
						{
						setState(343);
						match(EQU);
						}
						break;
					case 10:
						{
						setState(344);
						match(NEQ);
						}
						break;
					case 11:
						{
						setState(345);
						match(SLASH);
						}
						break;
					case 12:
						{
						setState(346);
						struct();
						}
						break;
					case 13:
						{
						setState(347);
						match(LPAREN);
						}
						break;
					case 14:
						{
						setState(348);
						match(RPAREN);
						}
						break;
					case 15:
						{
						setState(349);
						match(SEPARATOR);
						}
						break;
					case 16:
						{
						setState(350);
						match(ARITHMETIC);
						}
						break;
					case 17:
						{
						setState(351);
						match(VARREF);
						}
						break;
					case 18:
						{
						setState(352);
						match(ITERATOR);
						}
						break;
					case 19:
						{
						setState(353);
						expression();
						}
						break;
					case 20:
						{
						setState(354);
						functionFire();
						}
						break;
					}
					}
					setState(357); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << VARREF) | (1L << STARTEXPR) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << FIREFUNC) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR) | (1L << STRREF))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(359);
				variable();
				setState(364);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(360);
					match(SLASH);
					setState(362);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(361);
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
				setState(366);
				string();
				}
				break;
			}
			setState(369);
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
		enterRule(_localctx, 26, RULE_instr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			match(DIV);
			setState(372);
			literal();
			setState(373);
			match(LPAREN);
			setState(379);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << BOOLEAN) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(374);
				param();
				setState(377);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(375);
					match(SEPARATOR);
					setState(376);
					param();
					}
				}

				}
			}

			setState(381);
			match(RPAREN);
			setState(382);
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
		enterRule(_localctx, 28, RULE_stringRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(384);
			match(STRREF);
			setState(387);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(385);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(386);
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
		enterRule(_localctx, 30, RULE_struct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(389);
			literal();
			setState(390);
			match(STRUCTFIELD);
			setState(391);
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
		enterRule(_localctx, 32, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(393);
			match(VARREF);
			setState(396);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
				{
				setState(394);
				literal();
				}
				break;
			case NUMBER:
				{
				setState(395);
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
		enterRule(_localctx, 34, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(398);
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
		enterRule(_localctx, 36, RULE_floatNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(400);
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
		enterRule(_localctx, 38, RULE_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(402);
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
		enterRule(_localctx, 40, RULE_arithmetic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404);
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
		enterRule(_localctx, 42, RULE_logic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(406);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u019b\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2?\n\2\3\2\3\2\5\2C\n\2"+
		"\3\2\3\2\3\2\5\2H\n\2\3\2\3\2\7\2L\n\2\f\2\16\2O\13\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\5\3Y\n\3\3\3\3\3\3\3\5\3^\n\3\3\3\3\3\3\3\3\3\3\3\5\3"+
		"e\n\3\3\3\3\3\3\3\5\3j\n\3\3\3\3\3\3\3\3\3\3\3\5\3q\n\3\3\3\3\3\3\3\5"+
		"\3v\n\3\3\3\3\3\7\3z\n\3\f\3\16\3}\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\5\4\u008b\n\4\3\4\3\4\7\4\u008f\n\4\f\4\16\4\u0092"+
		"\13\4\3\5\3\5\3\5\3\5\3\5\5\5\u0099\n\5\3\5\3\5\3\5\3\5\5\5\u009f\n\5"+
		"\3\5\7\5\u00a2\n\5\f\5\16\5\u00a5\13\5\3\5\3\5\7\5\u00a9\n\5\f\5\16\5"+
		"\u00ac\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00ba"+
		"\n\6\3\6\7\6\u00bd\n\6\f\6\16\6\u00c0\13\6\7\6\u00c2\n\6\f\6\16\6\u00c5"+
		"\13\6\3\6\7\6\u00c8\n\6\f\6\16\6\u00cb\13\6\3\6\3\6\3\7\3\7\3\7\3\7\5"+
		"\7\u00d3\n\7\3\7\3\7\3\7\5\7\u00d8\n\7\3\7\3\7\5\7\u00dc\n\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\5\7\u00e6\n\7\3\7\3\7\5\7\u00ea\n\7\7\7\u00ec\n"+
		"\7\f\7\16\7\u00ef\13\7\3\7\3\7\7\7\u00f3\n\7\f\7\16\7\u00f6\13\7\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\7\b\u00ff\n\b\f\b\16\b\u0102\13\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\5\t\u010a\n\t\3\t\3\t\5\t\u010e\n\t\3\t\3\t\3\t\3\t\3\t\5\t"+
		"\u0115\n\t\3\n\5\n\u0118\n\n\3\n\7\n\u011b\n\n\f\n\16\n\u011e\13\n\3\13"+
		"\3\13\3\13\3\13\3\13\5\13\u0125\n\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3"+
		"\r\3\r\5\r\u0130\n\r\3\r\3\r\5\r\u0134\n\r\3\r\3\r\3\r\3\r\5\r\u013a\n"+
		"\r\3\r\3\r\3\r\5\r\u013f\n\r\3\r\3\r\5\r\u0143\n\r\3\r\3\r\3\r\3\r\5\r"+
		"\u0149\n\r\3\16\3\16\3\16\3\16\5\16\u014f\n\16\3\16\3\16\5\16\u0153\n"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\6\16\u0166\n\16\r\16\16\16\u0167\3\16\3\16\3\16\5\16"+
		"\u016d\n\16\5\16\u016f\n\16\3\16\5\16\u0172\n\16\3\16\3\16\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\5\17\u017c\n\17\5\17\u017e\n\17\3\17\3\17\3\17\3\20"+
		"\3\20\3\20\5\20\u0186\n\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\5\22\u018f"+
		"\n\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\2\2\30\2"+
		"\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,\2\3\3\2\16\23\2\u020b\2"+
		".\3\2\2\2\4P\3\2\2\2\6~\3\2\2\2\b\u0098\3\2\2\2\n\u00ad\3\2\2\2\f\u00ce"+
		"\3\2\2\2\16\u0100\3\2\2\2\20\u0114\3\2\2\2\22\u011c\3\2\2\2\24\u0124\3"+
		"\2\2\2\26\u0129\3\2\2\2\30\u0139\3\2\2\2\32\u014a\3\2\2\2\34\u0175\3\2"+
		"\2\2\36\u0182\3\2\2\2 \u0187\3\2\2\2\"\u018b\3\2\2\2$\u0190\3\2\2\2&\u0192"+
		"\3\2\2\2(\u0194\3\2\2\2*\u0196\3\2\2\2,\u0198\3\2\2\2./\7\3\2\2/>\7\6"+
		"\2\2\60\61\7\"\2\2\61\62\5\22\n\2\62\63\7\"\2\2\63\64\7 \2\2\64?\3\2\2"+
		"\2\65\66\5\20\t\2\66\67\7 \2\2\678\7\"\2\289\t\2\2\29:\7\"\2\2:;\7 \2"+
		"\2;<\5\20\t\2<=\7 \2\2=?\3\2\2\2>\60\3\2\2\2>\65\3\2\2\2?B\3\2\2\2@C\5"+
		"\n\6\2AC\5\32\16\2B@\3\2\2\2BA\3\2\2\2CD\3\2\2\2DG\7 \2\2EH\5\n\6\2FH"+
		"\5\32\16\2GE\3\2\2\2GF\3\2\2\2HI\3\2\2\2IM\7\7\2\2JL\7\24\2\2KJ\3\2\2"+
		"\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2N\3\3\2\2\2OM\3\2\2\2PQ\7\4\2\2QR\7\6"+
		"\2\2RS\5\n\6\2S]\7 \2\2T^\5\32\16\2U^\5(\25\2V^\5\f\7\2WY\5*\26\2XW\3"+
		"\2\2\2XY\3\2\2\2YZ\3\2\2\2Z^\5$\23\2[^\5\b\5\2\\^\5 \21\2]T\3\2\2\2]U"+
		"\3\2\2\2]V\3\2\2\2]X\3\2\2\2][\3\2\2\2]\\\3\2\2\2^_\3\2\2\2_i\7 \2\2`"+
		"j\5\32\16\2aj\5(\25\2bj\5\f\7\2ce\5*\26\2dc\3\2\2\2de\3\2\2\2ef\3\2\2"+
		"\2fj\5$\23\2gj\5\b\5\2hj\5 \21\2i`\3\2\2\2ia\3\2\2\2ib\3\2\2\2id\3\2\2"+
		"\2ig\3\2\2\2ih\3\2\2\2jk\3\2\2\2ku\7 \2\2lv\5\32\16\2mv\5(\25\2nv\5\f"+
		"\7\2oq\5*\26\2po\3\2\2\2pq\3\2\2\2qr\3\2\2\2rv\5$\23\2sv\5\b\5\2tv\5 "+
		"\21\2ul\3\2\2\2um\3\2\2\2un\3\2\2\2up\3\2\2\2us\3\2\2\2ut\3\2\2\2vw\3"+
		"\2\2\2w{\7\7\2\2xz\7\24\2\2yx\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2\2|\5"+
		"\3\2\2\2}{\3\2\2\2~\177\7\5\2\2\177\u0080\7\6\2\2\u0080\u0081\5\20\t\2"+
		"\u0081\u0082\7 \2\2\u0082\u0083\7\"\2\2\u0083\u0084\t\2\2\2\u0084\u0085"+
		"\7\"\2\2\u0085\u0086\7 \2\2\u0086\u0087\5\20\t\2\u0087\u008a\7 \2\2\u0088"+
		"\u008b\5\n\6\2\u0089\u008b\5\32\16\2\u008a\u0088\3\2\2\2\u008a\u0089\3"+
		"\2\2\2\u008b\u008c\3\2\2\2\u008c\u0090\7\7\2\2\u008d\u008f\7\24\2\2\u008e"+
		"\u008d\3\2\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2"+
		"\2\2\u0091\7\3\2\2\2\u0092\u0090\3\2\2\2\u0093\u0099\5(\25\2\u0094\u0099"+
		"\7\33\2\2\u0095\u0099\5\36\20\2\u0096\u0099\5 \21\2\u0097\u0099\5\"\22"+
		"\2\u0098\u0093\3\2\2\2\u0098\u0094\3\2\2\2\u0098\u0095\3\2\2\2\u0098\u0096"+
		"\3\2\2\2\u0098\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009b\7\25\2\2"+
		"\u009b\u009c\5(\25\2\u009c\u00a3\7\6\2\2\u009d\u009f\7 \2\2\u009e\u009d"+
		"\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a2\5\20\t\2"+
		"\u00a1\u009e\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4"+
		"\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6\u00aa\7\7\2\2\u00a7"+
		"\u00a9\7\24\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00ac\3\2\2\2\u00aa\u00a8\3"+
		"\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\t\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ad\u00c3"+
		"\7\t\2\2\u00ae\u00c2\5\b\5\2\u00af\u00c2\5\2\2\2\u00b0\u00c2\5\4\3\2\u00b1"+
		"\u00c2\5\6\4\2\u00b2\u00c2\5\34\17\2\u00b3\u00c2\5\26\f\2\u00b4\u00c2"+
		"\5\f\7\2\u00b5\u00c2\5\n\6\2\u00b6\u00ba\5(\25\2\u00b7\u00ba\5&\24\2\u00b8"+
		"\u00ba\5$\23\2\u00b9\u00b6\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00b8\3\2"+
		"\2\2\u00ba\u00be\3\2\2\2\u00bb\u00bd\7\24\2\2\u00bc\u00bb\3\2\2\2\u00bd"+
		"\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c2\3\2"+
		"\2\2\u00c0\u00be\3\2\2\2\u00c1\u00ae\3\2\2\2\u00c1\u00af\3\2\2\2\u00c1"+
		"\u00b0\3\2\2\2\u00c1\u00b1\3\2\2\2\u00c1\u00b2\3\2\2\2\u00c1\u00b3\3\2"+
		"\2\2\u00c1\u00b4\3\2\2\2\u00c1\u00b5\3\2\2\2\u00c1\u00b9\3\2\2\2\u00c2"+
		"\u00c5\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c9\3\2"+
		"\2\2\u00c5\u00c3\3\2\2\2\u00c6\u00c8\7\24\2\2\u00c7\u00c6\3\2\2\2\u00c8"+
		"\u00cb\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cc\3\2"+
		"\2\2\u00cb\u00c9\3\2\2\2\u00cc\u00cd\7\n\2\2\u00cd\13\3\2\2\2\u00ce\u00ed"+
		"\7\13\2\2\u00cf\u00d3\5*\26\2\u00d0\u00d3\7\26\2\2\u00d1\u00d3\7#\2\2"+
		"\u00d2\u00cf\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d2\u00d1\3\2\2\2\u00d2\u00d3"+
		"\3\2\2\2\u00d3\u00e5\3\2\2\2\u00d4\u00e6\5(\25\2\u00d5\u00e6\5\32\16\2"+
		"\u00d6\u00d8\5*\26\2\u00d7\u00d6\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9"+
		"\3\2\2\2\u00d9\u00e6\5$\23\2\u00da\u00dc\5*\26\2\u00db\u00da\3\2\2\2\u00db"+
		"\u00dc\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00e6\5&\24\2\u00de\u00e6\5\30"+
		"\r\2\u00df\u00e6\7\33\2\2\u00e0\u00e6\5\b\5\2\u00e1\u00e6\5\f\7\2\u00e2"+
		"\u00e6\5 \21\2\u00e3\u00e6\5\36\20\2\u00e4\u00e6\5\"\22\2\u00e5\u00d4"+
		"\3\2\2\2\u00e5\u00d5\3\2\2\2\u00e5\u00d7\3\2\2\2\u00e5\u00db\3\2\2\2\u00e5"+
		"\u00de\3\2\2\2\u00e5\u00df\3\2\2\2\u00e5\u00e0\3\2\2\2\u00e5\u00e1\3\2"+
		"\2\2\u00e5\u00e2\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e4\3\2\2\2\u00e6"+
		"\u00e9\3\2\2\2\u00e7\u00ea\5*\26\2\u00e8\u00ea\7#\2\2\u00e9\u00e7\3\2"+
		"\2\2\u00e9\u00e8\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\u00ec\3\2\2\2\u00eb"+
		"\u00d2\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ee\3\2"+
		"\2\2\u00ee\u00f0\3\2\2\2\u00ef\u00ed\3\2\2\2\u00f0\u00f4\7\f\2\2\u00f1"+
		"\u00f3\7\24\2\2\u00f2\u00f1\3\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3"+
		"\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\r\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f7\u00ff"+
		"\5\n\6\2\u00f8\u00ff\5\f\7\2\u00f9\u00ff\5\32\16\2\u00fa\u00ff\5\2\2\2"+
		"\u00fb\u00ff\5\4\3\2\u00fc\u00ff\5\6\4\2\u00fd\u00ff\5\32\16\2\u00fe\u00f7"+
		"\3\2\2\2\u00fe\u00f8\3\2\2\2\u00fe\u00f9\3\2\2\2\u00fe\u00fa\3\2\2\2\u00fe"+
		"\u00fb\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00fd\3\2\2\2\u00ff\u0102\3\2"+
		"\2\2\u0100\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101\17\3\2\2\2\u0102\u0100"+
		"\3\2\2\2\u0103\u0115\5\b\5\2\u0104\u0115\7\37\2\2\u0105\u0115\5\"\22\2"+
		"\u0106\u0115\5\32\16\2\u0107\u0115\5(\25\2\u0108\u010a\5*\26\2\u0109\u0108"+
		"\3\2\2\2\u0109\u010a\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u0115\5$\23\2\u010c"+
		"\u010e\5*\26\2\u010d\u010c\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u010f\3\2"+
		"\2\2\u010f\u0115\5&\24\2\u0110\u0115\5\f\7\2\u0111\u0115\7\33\2\2\u0112"+
		"\u0115\5 \21\2\u0113\u0115\5\36\20\2\u0114\u0103\3\2\2\2\u0114\u0104\3"+
		"\2\2\2\u0114\u0105\3\2\2\2\u0114\u0106\3\2\2\2\u0114\u0107\3\2\2\2\u0114"+
		"\u0109\3\2\2\2\u0114\u010d\3\2\2\2\u0114\u0110\3\2\2\2\u0114\u0111\3\2"+
		"\2\2\u0114\u0112\3\2\2\2\u0114\u0113\3\2\2\2\u0115\21\3\2\2\2\u0116\u0118"+
		"\5,\27\2\u0117\u0116\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u0119\3\2\2\2\u0119"+
		"\u011b\5\24\13\2\u011a\u0117\3\2\2\2\u011b\u011e\3\2\2\2\u011c\u011a\3"+
		"\2\2\2\u011c\u011d\3\2\2\2\u011d\23\3\2\2\2\u011e\u011c\3\2\2\2\u011f"+
		"\u0125\5(\25\2\u0120\u0125\5\b\5\2\u0121\u0125\5 \21\2\u0122\u0125\5\f"+
		"\7\2\u0123\u0125\7\33\2\2\u0124\u011f\3\2\2\2\u0124\u0120\3\2\2\2\u0124"+
		"\u0121\3\2\2\2\u0124\u0122\3\2\2\2\u0124\u0123\3\2\2\2\u0125\u0126\3\2"+
		"\2\2\u0126\u0127\t\2\2\2\u0127\u0128\5\20\t\2\u0128\25\3\2\2\2\u0129\u012a"+
		"\5(\25\2\u012a\u012b\7\24\2\2\u012b\27\3\2\2\2\u012c\u013a\5(\25\2\u012d"+
		"\u013a\7\33\2\2\u012e\u0130\5*\26\2\u012f\u012e\3\2\2\2\u012f\u0130\3"+
		"\2\2\2\u0130\u0131\3\2\2\2\u0131\u013a\5$\23\2\u0132\u0134\5*\26\2\u0133"+
		"\u0132\3\2\2\2\u0133\u0134\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u013a\5&"+
		"\24\2\u0136\u013a\5\b\5\2\u0137\u013a\5 \21\2\u0138\u013a\5\f\7\2\u0139"+
		"\u012c\3\2\2\2\u0139\u012d\3\2\2\2\u0139\u012f\3\2\2\2\u0139\u0133\3\2"+
		"\2\2\u0139\u0136\3\2\2\2\u0139\u0137\3\2\2\2\u0139\u0138\3\2\2\2\u013a"+
		"\u013b\3\2\2\2\u013b\u0148\7\27\2\2\u013c\u0149\5(\25\2\u013d\u013f\5"+
		"*\26\2\u013e\u013d\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u0140\3\2\2\2\u0140"+
		"\u0149\5$\23\2\u0141\u0143\5*\26\2\u0142\u0141\3\2\2\2\u0142\u0143\3\2"+
		"\2\2\u0143\u0144\3\2\2\2\u0144\u0149\5&\24\2\u0145\u0149\5\b\5\2\u0146"+
		"\u0149\5 \21\2\u0147\u0149\5\f\7\2\u0148\u013c\3\2\2\2\u0148\u013e\3\2"+
		"\2\2\u0148\u0142\3\2\2\2\u0148\u0145\3\2\2\2\u0148\u0146\3\2\2\2\u0148"+
		"\u0147\3\2\2\2\u0149\31\3\2\2\2\u014a\u0171\7\"\2\2\u014b\u0166\5(\25"+
		"\2\u014c\u0166\7\25\2\2\u014d\u014f\5*\26\2\u014e\u014d\3\2\2\2\u014e"+
		"\u014f\3\2\2\2\u014f\u0150\3\2\2\2\u0150\u0166\5$\23\2\u0151\u0153\5*"+
		"\26\2\u0152\u0151\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\3\2\2\2\u0154"+
		"\u0166\5&\24\2\u0155\u0166\7\16\2\2\u0156\u0166\7\17\2\2\u0157\u0166\7"+
		"\20\2\2\u0158\u0166\7\21\2\2\u0159\u0166\7\22\2\2\u015a\u0166\7\23\2\2"+
		"\u015b\u0166\7\r\2\2\u015c\u0166\5 \21\2\u015d\u0166\7\6\2\2\u015e\u0166"+
		"\7\7\2\2\u015f\u0166\7 \2\2\u0160\u0166\7\35\2\2\u0161\u0166\7\b\2\2\u0162"+
		"\u0166\7\33\2\2\u0163\u0166\5\f\7\2\u0164\u0166\5\b\5\2\u0165\u014b\3"+
		"\2\2\2\u0165\u014c\3\2\2\2\u0165\u014e\3\2\2\2\u0165\u0152\3\2\2\2\u0165"+
		"\u0155\3\2\2\2\u0165\u0156\3\2\2\2\u0165\u0157\3\2\2\2\u0165\u0158\3\2"+
		"\2\2\u0165\u0159\3\2\2\2\u0165\u015a\3\2\2\2\u0165\u015b\3\2\2\2\u0165"+
		"\u015c\3\2\2\2\u0165\u015d\3\2\2\2\u0165\u015e\3\2\2\2\u0165\u015f\3\2"+
		"\2\2\u0165\u0160\3\2\2\2\u0165\u0161\3\2\2\2\u0165\u0162\3\2\2\2\u0165"+
		"\u0163\3\2\2\2\u0165\u0164\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0165\3\2"+
		"\2\2\u0167\u0168\3\2\2\2\u0168\u0172\3\2\2\2\u0169\u016e\5\"\22\2\u016a"+
		"\u016c\7\r\2\2\u016b\u016d\5(\25\2\u016c\u016b\3\2\2\2\u016c\u016d\3\2"+
		"\2\2\u016d\u016f\3\2\2\2\u016e\u016a\3\2\2\2\u016e\u016f\3\2\2\2\u016f"+
		"\u0172\3\2\2\2\u0170\u0172\5\32\16\2\u0171\u0165\3\2\2\2\u0171\u0169\3"+
		"\2\2\2\u0171\u0170\3\2\2\2\u0171\u0172\3\2\2\2\u0172\u0173\3\2\2\2\u0173"+
		"\u0174\7\"\2\2\u0174\33\3\2\2\2\u0175\u0176\7\26\2\2\u0176\u0177\5(\25"+
		"\2\u0177\u017d\7\6\2\2\u0178\u017b\5\20\t\2\u0179\u017a\7 \2\2\u017a\u017c"+
		"\5\20\t\2\u017b\u0179\3\2\2\2\u017b\u017c\3\2\2\2\u017c\u017e\3\2\2\2"+
		"\u017d\u0178\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0180"+
		"\7\7\2\2\u0180\u0181\7\24\2\2\u0181\35\3\2\2\2\u0182\u0185\7#\2\2\u0183"+
		"\u0186\5\f\7\2\u0184\u0186\5(\25\2\u0185\u0183\3\2\2\2\u0185\u0184\3\2"+
		"\2\2\u0186\37\3\2\2\2\u0187\u0188\5(\25\2\u0188\u0189\7!\2\2\u0189\u018a"+
		"\5(\25\2\u018a!\3\2\2\2\u018b\u018e\7\b\2\2\u018c\u018f\5(\25\2\u018d"+
		"\u018f\5$\23\2\u018e\u018c\3\2\2\2\u018e\u018d\3\2\2\2\u018f#\3\2\2\2"+
		"\u0190\u0191\7\30\2\2\u0191%\3\2\2\2\u0192\u0193\7\31\2\2\u0193\'\3\2"+
		"\2\2\u0194\u0195\7\34\2\2\u0195)\3\2\2\2\u0196\u0197\7\35\2\2\u0197+\3"+
		"\2\2\2\u0198\u0199\7\36\2\2\u0199-\3\2\2\28>BGMX]dipu{\u008a\u0090\u0098"+
		"\u009e\u00a3\u00aa\u00b9\u00be\u00c1\u00c3\u00c9\u00d2\u00d7\u00db\u00e5"+
		"\u00e9\u00ed\u00f4\u00fe\u0100\u0109\u010d\u0114\u0117\u011c\u0124\u012f"+
		"\u0133\u0139\u013e\u0142\u0148\u014e\u0152\u0165\u0167\u016c\u016e\u0171"+
		"\u017b\u017d\u0185\u018e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}