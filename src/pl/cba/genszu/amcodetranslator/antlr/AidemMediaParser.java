// Generated from c:\Users\oem\Desktop\gramatykaAM\AidemMedia.g4 by ANTLR 4.9.2
package pl.cba.genszu.amcodetranslator.antlr;
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
		T__0=1, T__1=2, T__2=3, COMMENT=4, LPAREN=5, RPAREN=6, VARREF=7, STARTCODE=8, 
		STOPCODE=9, STARTEXPR=10, STOPEXPR=11, SLASH=12, LSS=13, LEQ=14, GEQ=15, 
		GTR=16, EQU=17, NEQ=18, ENDINSTR=19, FIREFUNC=20, DIV=21, MOD=22, NUMBER=23, 
		FLOAT=24, DOT=25, ITERATOR=26, BOOLEAN=27, LITERAL=28, ARITHMETIC=29, 
		LOGIC=30, SEPARATOR=31, STRUCTFIELD=32, QUOTEMARK=33, STRREF=34, WS=35;
	public static final int
		RULE_ifInstr = 0, RULE_loopInstr = 1, RULE_whileInstr = 2, RULE_functionFire = 3, 
		RULE_codeBlock = 4, RULE_comment = 5, RULE_expression = 6, RULE_script = 7, 
		RULE_param = 8, RULE_condition = 9, RULE_conditionPart = 10, RULE_behFire = 11, 
		RULE_modulo = 12, RULE_iterator = 13, RULE_string = 14, RULE_instr = 15, 
		RULE_stringRef = 16, RULE_struct = 17, RULE_variable = 18, RULE_number = 19, 
		RULE_floatNumber = 20, RULE_literal = 21, RULE_arithmetic = 22, RULE_logic = 23, 
		RULE_compare = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"ifInstr", "loopInstr", "whileInstr", "functionFire", "codeBlock", "comment", 
			"expression", "script", "param", "condition", "conditionPart", "behFire", 
			"modulo", "iterator", "string", "instr", "stringRef", "struct", "variable", 
			"number", "floatNumber", "literal", "arithmetic", "logic", "compare"
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
			"DOT", "ITERATOR", "BOOLEAN", "LITERAL", "ARITHMETIC", "LOGIC", "SEPARATOR", 
			"STRUCTFIELD", "QUOTEMARK", "STRREF", "WS"
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
			setState(50);
			match(T__0);
			setState(51);
			match(LPAREN);
			setState(66);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(52);
				match(QUOTEMARK);
				setState(53);
				condition();
				setState(54);
				match(QUOTEMARK);
				setState(55);
				match(SEPARATOR);
				}
				break;
			case 2:
				{
				setState(57);
				param();
				setState(58);
				match(SEPARATOR);
				setState(59);
				match(QUOTEMARK);
				setState(60);
				compare();
				setState(61);
				match(QUOTEMARK);
				setState(62);
				match(SEPARATOR);
				setState(63);
				param();
				setState(64);
				match(SEPARATOR);
				}
				break;
			}
			setState(70);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(68);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(69);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(72);
			match(SEPARATOR);
			setState(75);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(73);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(74);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(77);
			match(RPAREN);
			setState(81);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(78);
					match(ENDINSTR);
					}
					} 
				}
				setState(83);
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
			setState(84);
			match(T__1);
			setState(85);
			match(LPAREN);
			setState(86);
			codeBlock();
			setState(87);
			match(SEPARATOR);
			setState(97);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(88);
				string();
				}
				break;
			case 2:
				{
				setState(89);
				literal();
				}
				break;
			case 3:
				{
				setState(90);
				expression();
				}
				break;
			case 4:
				{
				setState(92);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(91);
					arithmetic();
					}
				}

				setState(94);
				number();
				}
				break;
			case 5:
				{
				setState(95);
				functionFire();
				}
				break;
			case 6:
				{
				setState(96);
				struct();
				}
				break;
			}
			setState(99);
			match(SEPARATOR);
			setState(109);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(100);
				string();
				}
				break;
			case 2:
				{
				setState(101);
				literal();
				}
				break;
			case 3:
				{
				setState(102);
				expression();
				}
				break;
			case 4:
				{
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(103);
					arithmetic();
					}
				}

				setState(106);
				number();
				}
				break;
			case 5:
				{
				setState(107);
				functionFire();
				}
				break;
			case 6:
				{
				setState(108);
				struct();
				}
				break;
			}
			setState(111);
			match(SEPARATOR);
			setState(121);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(112);
				string();
				}
				break;
			case 2:
				{
				setState(113);
				literal();
				}
				break;
			case 3:
				{
				setState(114);
				expression();
				}
				break;
			case 4:
				{
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(115);
					arithmetic();
					}
				}

				setState(118);
				number();
				}
				break;
			case 5:
				{
				setState(119);
				functionFire();
				}
				break;
			case 6:
				{
				setState(120);
				struct();
				}
				break;
			}
			setState(123);
			match(RPAREN);
			setState(127);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(124);
					match(ENDINSTR);
					}
					} 
				}
				setState(129);
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
			setState(130);
			match(T__2);
			setState(131);
			match(LPAREN);
			setState(132);
			param();
			setState(133);
			match(SEPARATOR);
			setState(134);
			match(QUOTEMARK);
			setState(135);
			compare();
			setState(136);
			match(QUOTEMARK);
			setState(137);
			match(SEPARATOR);
			setState(138);
			param();
			setState(139);
			match(SEPARATOR);
			setState(142);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTCODE:
				{
				setState(140);
				codeBlock();
				}
				break;
			case QUOTEMARK:
				{
				setState(141);
				string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(144);
			match(RPAREN);
			setState(148);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(145);
					match(ENDINSTR);
					}
					} 
				}
				setState(150);
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
			setState(156);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(151);
				literal();
				}
				break;
			case 2:
				{
				setState(152);
				iterator();
				}
				break;
			case 3:
				{
				setState(153);
				stringRef();
				}
				break;
			case 4:
				{
				setState(154);
				struct();
				}
				break;
			case 5:
				{
				setState(155);
				variable();
				}
				break;
			}
			setState(158);
			match(FIREFUNC);
			setState(159);
			literal();
			setState(160);
			match(LPAREN);
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << BOOLEAN) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(162);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(161);
					match(SEPARATOR);
					}
				}

				setState(164);
				param();
				}
				}
				setState(169);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(170);
			match(RPAREN);
			setState(174);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(171);
					match(ENDINSTR);
					}
					} 
				}
				setState(176);
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
			setState(177);
			match(STARTCODE);
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << COMMENT) | (1L << VARREF) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << STRREF))) != 0)) {
				{
				setState(198);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(178);
					functionFire();
					}
					break;
				case 2:
					{
					setState(179);
					ifInstr();
					}
					break;
				case 3:
					{
					setState(180);
					loopInstr();
					}
					break;
				case 4:
					{
					setState(181);
					whileInstr();
					}
					break;
				case 5:
					{
					setState(182);
					instr();
					}
					break;
				case 6:
					{
					setState(183);
					behFire();
					}
					break;
				case 7:
					{
					setState(184);
					expression();
					}
					break;
				case 8:
					{
					setState(185);
					codeBlock();
					}
					break;
				case 9:
					{
					setState(189);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case LITERAL:
						{
						setState(186);
						literal();
						}
						break;
					case FLOAT:
						{
						setState(187);
						floatNumber();
						}
						break;
					case NUMBER:
						{
						setState(188);
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
					setState(191);
					comment();
					setState(195);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(192);
							match(ENDINSTR);
							}
							} 
						}
						setState(197);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
					}
					}
					break;
				}
				}
				setState(202);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(206);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(203);
				match(ENDINSTR);
				}
				}
				setState(208);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(209);
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
		enterRule(_localctx, 10, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
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
		enterRule(_localctx, 12, RULE_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(STARTEXPR);
			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(217);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(214);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(215);
					match(DIV);
					}
					break;
				case 3:
					{
					setState(216);
					match(STRREF);
					}
					break;
				}
				setState(236);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(219);
					literal();
					}
					break;
				case 2:
					{
					setState(220);
					string();
					}
					break;
				case 3:
					{
					setState(222);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(221);
						arithmetic();
						}
					}

					setState(224);
					number();
					}
					break;
				case 4:
					{
					setState(226);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(225);
						arithmetic();
						}
					}

					setState(228);
					floatNumber();
					}
					break;
				case 5:
					{
					setState(229);
					modulo();
					}
					break;
				case 6:
					{
					setState(230);
					iterator();
					}
					break;
				case 7:
					{
					setState(231);
					functionFire();
					}
					break;
				case 8:
					{
					setState(232);
					expression();
					}
					break;
				case 9:
					{
					setState(233);
					struct();
					}
					break;
				case 10:
					{
					setState(234);
					stringRef();
					}
					break;
				case 11:
					{
					setState(235);
					variable();
					}
					break;
				}
				setState(240);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(238);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(239);
					match(STRREF);
					}
					break;
				}
				}
				}
				setState(246);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(247);
			match(STOPEXPR);
			setState(251);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(248);
					match(ENDINSTR);
					}
					} 
				}
				setState(253);
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
		enterRule(_localctx, 14, RULE_script);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(263);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(261);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					setState(254);
					codeBlock();
					}
					break;
				case 2:
					{
					setState(255);
					expression();
					}
					break;
				case 3:
					{
					setState(256);
					string();
					}
					break;
				case 4:
					{
					setState(257);
					ifInstr();
					}
					break;
				case 5:
					{
					setState(258);
					loopInstr();
					}
					break;
				case 6:
					{
					setState(259);
					whileInstr();
					}
					break;
				case 7:
					{
					setState(260);
					string();
					}
					break;
				}
				}
				setState(265);
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
		enterRule(_localctx, 16, RULE_param);
		int _la;
		try {
			setState(283);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(266);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(267);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(268);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(269);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(270);
				literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(272);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(271);
					arithmetic();
					}
				}

				setState(274);
				number();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(276);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(275);
					arithmetic();
					}
				}

				setState(278);
				floatNumber();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(279);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(280);
				iterator();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(281);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(282);
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
		enterRule(_localctx, 18, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << ITERATOR) | (1L << LITERAL) | (1L << LOGIC) | (1L << STRREF))) != 0)) {
				{
				{
				setState(286);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOGIC) {
					{
					setState(285);
					logic();
					}
				}

				setState(288);
				conditionPart();
				}
				}
				setState(293);
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
		enterRule(_localctx, 20, RULE_conditionPart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(294);
				literal();
				}
				break;
			case 2:
				{
				setState(295);
				functionFire();
				}
				break;
			case 3:
				{
				setState(296);
				struct();
				}
				break;
			case 4:
				{
				setState(297);
				expression();
				}
				break;
			case 5:
				{
				setState(298);
				iterator();
				}
				break;
			}
			setState(301);
			compare();
			setState(302);
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
		enterRule(_localctx, 22, RULE_behFire);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			literal();
			setState(305);
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
		enterRule(_localctx, 24, RULE_modulo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(320);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(307);
				literal();
				}
				break;
			case 2:
				{
				setState(308);
				iterator();
				}
				break;
			case 3:
				{
				setState(310);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(309);
					arithmetic();
					}
				}

				setState(312);
				number();
				}
				break;
			case 4:
				{
				setState(314);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(313);
					arithmetic();
					}
				}

				setState(316);
				floatNumber();
				}
				break;
			case 5:
				{
				setState(317);
				functionFire();
				}
				break;
			case 6:
				{
				setState(318);
				struct();
				}
				break;
			case 7:
				{
				setState(319);
				expression();
				}
				break;
			}
			setState(322);
			match(MOD);
			setState(335);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(323);
				literal();
				}
				break;
			case 2:
				{
				setState(325);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(324);
					arithmetic();
					}
				}

				setState(327);
				number();
				}
				break;
			case 3:
				{
				setState(329);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(328);
					arithmetic();
					}
				}

				setState(331);
				floatNumber();
				}
				break;
			case 4:
				{
				setState(332);
				functionFire();
				}
				break;
			case 5:
				{
				setState(333);
				struct();
				}
				break;
			case 6:
				{
				setState(334);
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
		enterRule(_localctx, 26, RULE_iterator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
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
		enterRule(_localctx, 28, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(339);
			match(QUOTEMARK);
			setState(373);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(360); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(360);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
					case 1:
						{
						setState(340);
						literal();
						}
						break;
					case 2:
						{
						setState(341);
						match(FIREFUNC);
						}
						break;
					case 3:
						{
						setState(343);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(342);
							arithmetic();
							}
						}

						setState(347);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case NUMBER:
							{
							setState(345);
							number();
							}
							break;
						case FLOAT:
							{
							setState(346);
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
						setState(349);
						compare();
						}
						break;
					case 5:
						{
						setState(350);
						match(SLASH);
						}
						break;
					case 6:
						{
						setState(351);
						struct();
						}
						break;
					case 7:
						{
						setState(352);
						match(LPAREN);
						}
						break;
					case 8:
						{
						setState(353);
						match(RPAREN);
						}
						break;
					case 9:
						{
						setState(354);
						match(SEPARATOR);
						}
						break;
					case 10:
						{
						setState(355);
						arithmetic();
						}
						break;
					case 11:
						{
						setState(356);
						match(VARREF);
						}
						break;
					case 12:
						{
						setState(357);
						iterator();
						}
						break;
					case 13:
						{
						setState(358);
						expression();
						}
						break;
					case 14:
						{
						setState(359);
						functionFire();
						}
						break;
					}
					}
					setState(362); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << VARREF) | (1L << STARTEXPR) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << FIREFUNC) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR) | (1L << STRREF))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(364);
				variable();
				setState(369);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(365);
					match(SLASH);
					setState(367);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(366);
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
				setState(371);
				string();
				}
				break;
			case 4:
				{
				setState(372);
				match(BOOLEAN);
				}
				break;
			}
			setState(375);
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
		enterRule(_localctx, 30, RULE_instr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(377);
			match(DIV);
			setState(378);
			literal();
			setState(379);
			match(LPAREN);
			setState(385);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << BOOLEAN) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(380);
				param();
				setState(383);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(381);
					match(SEPARATOR);
					setState(382);
					param();
					}
				}

				}
			}

			setState(387);
			match(RPAREN);
			setState(388);
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
		enterRule(_localctx, 32, RULE_stringRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(390);
			match(STRREF);
			setState(393);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(391);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(392);
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
		enterRule(_localctx, 34, RULE_struct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(395);
			literal();
			setState(396);
			match(STRUCTFIELD);
			setState(397);
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
		enterRule(_localctx, 36, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(399);
			match(VARREF);
			setState(402);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
				{
				setState(400);
				literal();
				}
				break;
			case NUMBER:
				{
				setState(401);
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
		enterRule(_localctx, 38, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404);
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
		enterRule(_localctx, 40, RULE_floatNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(406);
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
		enterRule(_localctx, 42, RULE_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(408);
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
		enterRule(_localctx, 44, RULE_arithmetic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(410);
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
		enterRule(_localctx, 46, RULE_logic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
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
		enterRule(_localctx, 48, RULE_compare);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(414);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u01a3\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\5\2E\n\2\3\2\3\2\5\2I\n\2\3\2\3\2\3\2\5\2N\n\2\3\2\3\2\7\2R\n\2\f"+
		"\2\16\2U\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3_\n\3\3\3\3\3\3\3\5\3"+
		"d\n\3\3\3\3\3\3\3\3\3\3\3\5\3k\n\3\3\3\3\3\3\3\5\3p\n\3\3\3\3\3\3\3\3"+
		"\3\3\3\5\3w\n\3\3\3\3\3\3\3\5\3|\n\3\3\3\3\3\7\3\u0080\n\3\f\3\16\3\u0083"+
		"\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u0091\n\4\3"+
		"\4\3\4\7\4\u0095\n\4\f\4\16\4\u0098\13\4\3\5\3\5\3\5\3\5\3\5\5\5\u009f"+
		"\n\5\3\5\3\5\3\5\3\5\5\5\u00a5\n\5\3\5\7\5\u00a8\n\5\f\5\16\5\u00ab\13"+
		"\5\3\5\3\5\7\5\u00af\n\5\f\5\16\5\u00b2\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\5\6\u00c0\n\6\3\6\3\6\7\6\u00c4\n\6\f\6\16\6\u00c7"+
		"\13\6\7\6\u00c9\n\6\f\6\16\6\u00cc\13\6\3\6\7\6\u00cf\n\6\f\6\16\6\u00d2"+
		"\13\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\5\b\u00dc\n\b\3\b\3\b\3\b\5\b\u00e1"+
		"\n\b\3\b\3\b\5\b\u00e5\n\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u00ef\n"+
		"\b\3\b\3\b\5\b\u00f3\n\b\7\b\u00f5\n\b\f\b\16\b\u00f8\13\b\3\b\3\b\7\b"+
		"\u00fc\n\b\f\b\16\b\u00ff\13\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\7\t\u0108\n"+
		"\t\f\t\16\t\u010b\13\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0113\n\n\3\n\3\n\5"+
		"\n\u0117\n\n\3\n\3\n\3\n\3\n\3\n\5\n\u011e\n\n\3\13\5\13\u0121\n\13\3"+
		"\13\7\13\u0124\n\13\f\13\16\13\u0127\13\13\3\f\3\f\3\f\3\f\3\f\5\f\u012e"+
		"\n\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\5\16\u0139\n\16\3\16\3\16"+
		"\5\16\u013d\n\16\3\16\3\16\3\16\3\16\5\16\u0143\n\16\3\16\3\16\3\16\5"+
		"\16\u0148\n\16\3\16\3\16\5\16\u014c\n\16\3\16\3\16\3\16\3\16\5\16\u0152"+
		"\n\16\3\17\3\17\3\20\3\20\3\20\3\20\5\20\u015a\n\20\3\20\3\20\5\20\u015e"+
		"\n\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\6\20\u016b"+
		"\n\20\r\20\16\20\u016c\3\20\3\20\3\20\5\20\u0172\n\20\5\20\u0174\n\20"+
		"\3\20\3\20\5\20\u0178\n\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\5\21"+
		"\u0182\n\21\5\21\u0184\n\21\3\21\3\21\3\21\3\22\3\22\3\22\5\22\u018c\n"+
		"\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\5\24\u0195\n\24\3\25\3\25\3\26"+
		"\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\2\2\33\2\4\6\b\n\f"+
		"\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\2\3\3\2\17\24\2\u020c\2\64"+
		"\3\2\2\2\4V\3\2\2\2\6\u0084\3\2\2\2\b\u009e\3\2\2\2\n\u00b3\3\2\2\2\f"+
		"\u00d5\3\2\2\2\16\u00d7\3\2\2\2\20\u0109\3\2\2\2\22\u011d\3\2\2\2\24\u0125"+
		"\3\2\2\2\26\u012d\3\2\2\2\30\u0132\3\2\2\2\32\u0142\3\2\2\2\34\u0153\3"+
		"\2\2\2\36\u0155\3\2\2\2 \u017b\3\2\2\2\"\u0188\3\2\2\2$\u018d\3\2\2\2"+
		"&\u0191\3\2\2\2(\u0196\3\2\2\2*\u0198\3\2\2\2,\u019a\3\2\2\2.\u019c\3"+
		"\2\2\2\60\u019e\3\2\2\2\62\u01a0\3\2\2\2\64\65\7\3\2\2\65D\7\7\2\2\66"+
		"\67\7#\2\2\678\5\24\13\289\7#\2\29:\7!\2\2:E\3\2\2\2;<\5\22\n\2<=\7!\2"+
		"\2=>\7#\2\2>?\5\62\32\2?@\7#\2\2@A\7!\2\2AB\5\22\n\2BC\7!\2\2CE\3\2\2"+
		"\2D\66\3\2\2\2D;\3\2\2\2EH\3\2\2\2FI\5\n\6\2GI\5\36\20\2HF\3\2\2\2HG\3"+
		"\2\2\2IJ\3\2\2\2JM\7!\2\2KN\5\n\6\2LN\5\36\20\2MK\3\2\2\2ML\3\2\2\2NO"+
		"\3\2\2\2OS\7\b\2\2PR\7\25\2\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2"+
		"T\3\3\2\2\2US\3\2\2\2VW\7\4\2\2WX\7\7\2\2XY\5\n\6\2Yc\7!\2\2Zd\5\36\20"+
		"\2[d\5,\27\2\\d\5\16\b\2]_\5.\30\2^]\3\2\2\2^_\3\2\2\2_`\3\2\2\2`d\5("+
		"\25\2ad\5\b\5\2bd\5$\23\2cZ\3\2\2\2c[\3\2\2\2c\\\3\2\2\2c^\3\2\2\2ca\3"+
		"\2\2\2cb\3\2\2\2de\3\2\2\2eo\7!\2\2fp\5\36\20\2gp\5,\27\2hp\5\16\b\2i"+
		"k\5.\30\2ji\3\2\2\2jk\3\2\2\2kl\3\2\2\2lp\5(\25\2mp\5\b\5\2np\5$\23\2"+
		"of\3\2\2\2og\3\2\2\2oh\3\2\2\2oj\3\2\2\2om\3\2\2\2on\3\2\2\2pq\3\2\2\2"+
		"q{\7!\2\2r|\5\36\20\2s|\5,\27\2t|\5\16\b\2uw\5.\30\2vu\3\2\2\2vw\3\2\2"+
		"\2wx\3\2\2\2x|\5(\25\2y|\5\b\5\2z|\5$\23\2{r\3\2\2\2{s\3\2\2\2{t\3\2\2"+
		"\2{v\3\2\2\2{y\3\2\2\2{z\3\2\2\2|}\3\2\2\2}\u0081\7\b\2\2~\u0080\7\25"+
		"\2\2\177~\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3"+
		"\2\2\2\u0082\5\3\2\2\2\u0083\u0081\3\2\2\2\u0084\u0085\7\5\2\2\u0085\u0086"+
		"\7\7\2\2\u0086\u0087\5\22\n\2\u0087\u0088\7!\2\2\u0088\u0089\7#\2\2\u0089"+
		"\u008a\5\62\32\2\u008a\u008b\7#\2\2\u008b\u008c\7!\2\2\u008c\u008d\5\22"+
		"\n\2\u008d\u0090\7!\2\2\u008e\u0091\5\n\6\2\u008f\u0091\5\36\20\2\u0090"+
		"\u008e\3\2\2\2\u0090\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0096\7\b"+
		"\2\2\u0093\u0095\7\25\2\2\u0094\u0093\3\2\2\2\u0095\u0098\3\2\2\2\u0096"+
		"\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\7\3\2\2\2\u0098\u0096\3\2\2\2"+
		"\u0099\u009f\5,\27\2\u009a\u009f\5\34\17\2\u009b\u009f\5\"\22\2\u009c"+
		"\u009f\5$\23\2\u009d\u009f\5&\24\2\u009e\u0099\3\2\2\2\u009e\u009a\3\2"+
		"\2\2\u009e\u009b\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009d\3\2\2\2\u009f"+
		"\u00a0\3\2\2\2\u00a0\u00a1\7\26\2\2\u00a1\u00a2\5,\27\2\u00a2\u00a9\7"+
		"\7\2\2\u00a3\u00a5\7!\2\2\u00a4\u00a3\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5"+
		"\u00a6\3\2\2\2\u00a6\u00a8\5\22\n\2\u00a7\u00a4\3\2\2\2\u00a8\u00ab\3"+
		"\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ac\3\2\2\2\u00ab"+
		"\u00a9\3\2\2\2\u00ac\u00b0\7\b\2\2\u00ad\u00af\7\25\2\2\u00ae\u00ad\3"+
		"\2\2\2\u00af\u00b2\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1"+
		"\t\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b3\u00ca\7\n\2\2\u00b4\u00c9\5\b\5\2"+
		"\u00b5\u00c9\5\2\2\2\u00b6\u00c9\5\4\3\2\u00b7\u00c9\5\6\4\2\u00b8\u00c9"+
		"\5 \21\2\u00b9\u00c9\5\30\r\2\u00ba\u00c9\5\16\b\2\u00bb\u00c9\5\n\6\2"+
		"\u00bc\u00c0\5,\27\2\u00bd\u00c0\5*\26\2\u00be\u00c0\5(\25\2\u00bf\u00bc"+
		"\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00be\3\2\2\2\u00c0\u00c9\3\2\2\2\u00c1"+
		"\u00c5\5\f\7\2\u00c2\u00c4\7\25\2\2\u00c3\u00c2\3\2\2\2\u00c4\u00c7\3"+
		"\2\2\2\u00c5\u00c3\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c9\3\2\2\2\u00c7"+
		"\u00c5\3\2\2\2\u00c8\u00b4\3\2\2\2\u00c8\u00b5\3\2\2\2\u00c8\u00b6\3\2"+
		"\2\2\u00c8\u00b7\3\2\2\2\u00c8\u00b8\3\2\2\2\u00c8\u00b9\3\2\2\2\u00c8"+
		"\u00ba\3\2\2\2\u00c8\u00bb\3\2\2\2\u00c8\u00bf\3\2\2\2\u00c8\u00c1\3\2"+
		"\2\2\u00c9\u00cc\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb"+
		"\u00d0\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00cf\7\25\2\2\u00ce\u00cd\3"+
		"\2\2\2\u00cf\u00d2\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1"+
		"\u00d3\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3\u00d4\7\13\2\2\u00d4\13\3\2\2"+
		"\2\u00d5\u00d6\7\6\2\2\u00d6\r\3\2\2\2\u00d7\u00f6\7\f\2\2\u00d8\u00dc"+
		"\5.\30\2\u00d9\u00dc\7\27\2\2\u00da\u00dc\7$\2\2\u00db\u00d8\3\2\2\2\u00db"+
		"\u00d9\3\2\2\2\u00db\u00da\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\u00ee\3\2"+
		"\2\2\u00dd\u00ef\5,\27\2\u00de\u00ef\5\36\20\2\u00df\u00e1\5.\30\2\u00e0"+
		"\u00df\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00ef\5("+
		"\25\2\u00e3\u00e5\5.\30\2\u00e4\u00e3\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5"+
		"\u00e6\3\2\2\2\u00e6\u00ef\5*\26\2\u00e7\u00ef\5\32\16\2\u00e8\u00ef\5"+
		"\34\17\2\u00e9\u00ef\5\b\5\2\u00ea\u00ef\5\16\b\2\u00eb\u00ef\5$\23\2"+
		"\u00ec\u00ef\5\"\22\2\u00ed\u00ef\5&\24\2\u00ee\u00dd\3\2\2\2\u00ee\u00de"+
		"\3\2\2\2\u00ee\u00e0\3\2\2\2\u00ee\u00e4\3\2\2\2\u00ee\u00e7\3\2\2\2\u00ee"+
		"\u00e8\3\2\2\2\u00ee\u00e9\3\2\2\2\u00ee\u00ea\3\2\2\2\u00ee\u00eb\3\2"+
		"\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ed\3\2\2\2\u00ef\u00f2\3\2\2\2\u00f0"+
		"\u00f3\5.\30\2\u00f1\u00f3\7$\2\2\u00f2\u00f0\3\2\2\2\u00f2\u00f1\3\2"+
		"\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f5\3\2\2\2\u00f4\u00db\3\2\2\2\u00f5"+
		"\u00f8\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f9\3\2"+
		"\2\2\u00f8\u00f6\3\2\2\2\u00f9\u00fd\7\r\2\2\u00fa\u00fc\7\25\2\2\u00fb"+
		"\u00fa\3\2\2\2\u00fc\u00ff\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2"+
		"\2\2\u00fe\17\3\2\2\2\u00ff\u00fd\3\2\2\2\u0100\u0108\5\n\6\2\u0101\u0108"+
		"\5\16\b\2\u0102\u0108\5\36\20\2\u0103\u0108\5\2\2\2\u0104\u0108\5\4\3"+
		"\2\u0105\u0108\5\6\4\2\u0106\u0108\5\36\20\2\u0107\u0100\3\2\2\2\u0107"+
		"\u0101\3\2\2\2\u0107\u0102\3\2\2\2\u0107\u0103\3\2\2\2\u0107\u0104\3\2"+
		"\2\2\u0107\u0105\3\2\2\2\u0107\u0106\3\2\2\2\u0108\u010b\3\2\2\2\u0109"+
		"\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a\21\3\2\2\2\u010b\u0109\3\2\2"+
		"\2\u010c\u011e\5\b\5\2\u010d\u011e\7\35\2\2\u010e\u011e\5&\24\2\u010f"+
		"\u011e\5\36\20\2\u0110\u011e\5,\27\2\u0111\u0113\5.\30\2\u0112\u0111\3"+
		"\2\2\2\u0112\u0113\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u011e\5(\25\2\u0115"+
		"\u0117\5.\30\2\u0116\u0115\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118\3\2"+
		"\2\2\u0118\u011e\5*\26\2\u0119\u011e\5\16\b\2\u011a\u011e\5\34\17\2\u011b"+
		"\u011e\5$\23\2\u011c\u011e\5\"\22\2\u011d\u010c\3\2\2\2\u011d\u010d\3"+
		"\2\2\2\u011d\u010e\3\2\2\2\u011d\u010f\3\2\2\2\u011d\u0110\3\2\2\2\u011d"+
		"\u0112\3\2\2\2\u011d\u0116\3\2\2\2\u011d\u0119\3\2\2\2\u011d\u011a\3\2"+
		"\2\2\u011d\u011b\3\2\2\2\u011d\u011c\3\2\2\2\u011e\23\3\2\2\2\u011f\u0121"+
		"\5\60\31\2\u0120\u011f\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0122\3\2\2\2"+
		"\u0122\u0124\5\26\f\2\u0123\u0120\3\2\2\2\u0124\u0127\3\2\2\2\u0125\u0123"+
		"\3\2\2\2\u0125\u0126\3\2\2\2\u0126\25\3\2\2\2\u0127\u0125\3\2\2\2\u0128"+
		"\u012e\5,\27\2\u0129\u012e\5\b\5\2\u012a\u012e\5$\23\2\u012b\u012e\5\16"+
		"\b\2\u012c\u012e\5\34\17\2\u012d\u0128\3\2\2\2\u012d\u0129\3\2\2\2\u012d"+
		"\u012a\3\2\2\2\u012d\u012b\3\2\2\2\u012d\u012c\3\2\2\2\u012e\u012f\3\2"+
		"\2\2\u012f\u0130\5\62\32\2\u0130\u0131\5\22\n\2\u0131\27\3\2\2\2\u0132"+
		"\u0133\5,\27\2\u0133\u0134\7\25\2\2\u0134\31\3\2\2\2\u0135\u0143\5,\27"+
		"\2\u0136\u0143\5\34\17\2\u0137\u0139\5.\30\2\u0138\u0137\3\2\2\2\u0138"+
		"\u0139\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u0143\5(\25\2\u013b\u013d\5."+
		"\30\2\u013c\u013b\3\2\2\2\u013c\u013d\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u0143\5*\26\2\u013f\u0143\5\b\5\2\u0140\u0143\5$\23\2\u0141\u0143\5\16"+
		"\b\2\u0142\u0135\3\2\2\2\u0142\u0136\3\2\2\2\u0142\u0138\3\2\2\2\u0142"+
		"\u013c\3\2\2\2\u0142\u013f\3\2\2\2\u0142\u0140\3\2\2\2\u0142\u0141\3\2"+
		"\2\2\u0143\u0144\3\2\2\2\u0144\u0151\7\30\2\2\u0145\u0152\5,\27\2\u0146"+
		"\u0148\5.\30\2\u0147\u0146\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u0149\3\2"+
		"\2\2\u0149\u0152\5(\25\2\u014a\u014c\5.\30\2\u014b\u014a\3\2\2\2\u014b"+
		"\u014c\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u0152\5*\26\2\u014e\u0152\5\b"+
		"\5\2\u014f\u0152\5$\23\2\u0150\u0152\5\16\b\2\u0151\u0145\3\2\2\2\u0151"+
		"\u0147\3\2\2\2\u0151\u014b\3\2\2\2\u0151\u014e\3\2\2\2\u0151\u014f\3\2"+
		"\2\2\u0151\u0150\3\2\2\2\u0152\33\3\2\2\2\u0153\u0154\7\34\2\2\u0154\35"+
		"\3\2\2\2\u0155\u0177\7#\2\2\u0156\u016b\5,\27\2\u0157\u016b\7\26\2\2\u0158"+
		"\u015a\5.\30\2\u0159\u0158\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u015d\3\2"+
		"\2\2\u015b\u015e\5(\25\2\u015c\u015e\5*\26\2\u015d\u015b\3\2\2\2\u015d"+
		"\u015c\3\2\2\2\u015e\u016b\3\2\2\2\u015f\u016b\5\62\32\2\u0160\u016b\7"+
		"\16\2\2\u0161\u016b\5$\23\2\u0162\u016b\7\7\2\2\u0163\u016b\7\b\2\2\u0164"+
		"\u016b\7!\2\2\u0165\u016b\5.\30\2\u0166\u016b\7\t\2\2\u0167\u016b\5\34"+
		"\17\2\u0168\u016b\5\16\b\2\u0169\u016b\5\b\5\2\u016a\u0156\3\2\2\2\u016a"+
		"\u0157\3\2\2\2\u016a\u0159\3\2\2\2\u016a\u015f\3\2\2\2\u016a\u0160\3\2"+
		"\2\2\u016a\u0161\3\2\2\2\u016a\u0162\3\2\2\2\u016a\u0163\3\2\2\2\u016a"+
		"\u0164\3\2\2\2\u016a\u0165\3\2\2\2\u016a\u0166\3\2\2\2\u016a\u0167\3\2"+
		"\2\2\u016a\u0168\3\2\2\2\u016a\u0169\3\2\2\2\u016b\u016c\3\2\2\2\u016c"+
		"\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u0178\3\2\2\2\u016e\u0173\5&"+
		"\24\2\u016f\u0171\7\16\2\2\u0170\u0172\5,\27\2\u0171\u0170\3\2\2\2\u0171"+
		"\u0172\3\2\2\2\u0172\u0174\3\2\2\2\u0173\u016f\3\2\2\2\u0173\u0174\3\2"+
		"\2\2\u0174\u0178\3\2\2\2\u0175\u0178\5\36\20\2\u0176\u0178\7\35\2\2\u0177"+
		"\u016a\3\2\2\2\u0177\u016e\3\2\2\2\u0177\u0175\3\2\2\2\u0177\u0176\3\2"+
		"\2\2\u0177\u0178\3\2\2\2\u0178\u0179\3\2\2\2\u0179\u017a\7#\2\2\u017a"+
		"\37\3\2\2\2\u017b\u017c\7\27\2\2\u017c\u017d\5,\27\2\u017d\u0183\7\7\2"+
		"\2\u017e\u0181\5\22\n\2\u017f\u0180\7!\2\2\u0180\u0182\5\22\n\2\u0181"+
		"\u017f\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0184\3\2\2\2\u0183\u017e\3\2"+
		"\2\2\u0183\u0184\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u0186\7\b\2\2\u0186"+
		"\u0187\7\25\2\2\u0187!\3\2\2\2\u0188\u018b\7$\2\2\u0189\u018c\5\16\b\2"+
		"\u018a\u018c\5,\27\2\u018b\u0189\3\2\2\2\u018b\u018a\3\2\2\2\u018c#\3"+
		"\2\2\2\u018d\u018e\5,\27\2\u018e\u018f\7\"\2\2\u018f\u0190\5,\27\2\u0190"+
		"%\3\2\2\2\u0191\u0194\7\t\2\2\u0192\u0195\5,\27\2\u0193\u0195\5(\25\2"+
		"\u0194\u0192\3\2\2\2\u0194\u0193\3\2\2\2\u0195\'\3\2\2\2\u0196\u0197\7"+
		"\31\2\2\u0197)\3\2\2\2\u0198\u0199\7\32\2\2\u0199+\3\2\2\2\u019a\u019b"+
		"\7\36\2\2\u019b-\3\2\2\2\u019c\u019d\7\37\2\2\u019d/\3\2\2\2\u019e\u019f"+
		"\7 \2\2\u019f\61\3\2\2\2\u01a0\u01a1\t\2\2\2\u01a1\63\3\2\2\28DHMS^cj"+
		"ov{\u0081\u0090\u0096\u009e\u00a4\u00a9\u00b0\u00bf\u00c5\u00c8\u00ca"+
		"\u00d0\u00db\u00e0\u00e4\u00ee\u00f2\u00f6\u00fd\u0107\u0109\u0112\u0116"+
		"\u011d\u0120\u0125\u012d\u0138\u013c\u0142\u0147\u014b\u0151\u0159\u015d"+
		"\u016a\u016c\u0171\u0173\u0177\u0181\u0183\u018b\u0194";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}