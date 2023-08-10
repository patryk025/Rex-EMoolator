// Generated from c:\Users\Patryk\Desktop\GramatykaAM\AidemMedia.g4 by ANTLR 4.9.2
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
		RULE_codeBlock = 4, RULE_varWithNumber = 5, RULE_conditionSimple = 6, 
		RULE_comment = 7, RULE_expression = 8, RULE_script = 9, RULE_param = 10, 
		RULE_condition = 11, RULE_conditionPart = 12, RULE_behFire = 13, RULE_modulo = 14, 
		RULE_iterator = 15, RULE_string = 16, RULE_instr = 17, RULE_stringRef = 18, 
		RULE_struct = 19, RULE_variable = 20, RULE_number = 21, RULE_floatNumber = 22, 
		RULE_literal = 23, RULE_arithmetic = 24, RULE_logic = 25, RULE_compare = 26;
	private static String[] makeRuleNames() {
		return new String[] {
			"ifInstr", "loopInstr", "whileInstr", "functionFire", "codeBlock", "varWithNumber", 
			"conditionSimple", "comment", "expression", "script", "param", "condition", 
			"conditionPart", "behFire", "modulo", "iterator", "string", "instr", 
			"stringRef", "struct", "variable", "number", "floatNumber", "literal", 
			"arithmetic", "logic", "compare"
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
		public ConditionSimpleContext conditionSimple() {
			return getRuleContext(ConditionSimpleContext.class,0);
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
			setState(54);
			match(T__0);
			setState(55);
			match(LPAREN);
			setState(64);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(56);
				match(QUOTEMARK);
				setState(57);
				condition();
				setState(58);
				match(QUOTEMARK);
				setState(59);
				match(SEPARATOR);
				}
				break;
			case 2:
				{
				setState(61);
				conditionSimple();
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
			setState(155);
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
			case 6:
				{
				setState(154);
				varWithNumber();
				}
				break;
			}
			setState(157);
			match(FIREFUNC);
			setState(158);
			literal();
			setState(159);
			match(LPAREN);
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << BOOLEAN) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(160);
					match(SEPARATOR);
					}
				}

				setState(163);
				param();
				}
				}
				setState(168);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(169);
			match(RPAREN);
			setState(173);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(170);
					match(ENDINSTR);
					}
					} 
				}
				setState(175);
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
			setState(176);
			match(STARTCODE);
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << COMMENT) | (1L << VARREF) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << STRREF))) != 0)) {
				{
				setState(197);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(177);
					functionFire();
					}
					break;
				case 2:
					{
					setState(178);
					ifInstr();
					}
					break;
				case 3:
					{
					setState(179);
					loopInstr();
					}
					break;
				case 4:
					{
					setState(180);
					whileInstr();
					}
					break;
				case 5:
					{
					setState(181);
					instr();
					}
					break;
				case 6:
					{
					setState(182);
					behFire();
					}
					break;
				case 7:
					{
					setState(183);
					expression();
					}
					break;
				case 8:
					{
					setState(184);
					codeBlock();
					}
					break;
				case 9:
					{
					setState(188);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case LITERAL:
						{
						setState(185);
						literal();
						}
						break;
					case FLOAT:
						{
						setState(186);
						floatNumber();
						}
						break;
					case NUMBER:
						{
						setState(187);
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
					setState(190);
					comment();
					setState(194);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(191);
							match(ENDINSTR);
							}
							} 
						}
						setState(196);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
					}
					}
					break;
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(202);
				match(ENDINSTR);
				}
				}
				setState(207);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(208);
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
			setState(210);
			literal();
			setState(211);
			arithmetic();
			setState(212);
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
		enterRule(_localctx, 12, RULE_conditionSimple);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			param();
			setState(215);
			match(SEPARATOR);
			setState(216);
			match(QUOTEMARK);
			setState(217);
			compare();
			setState(218);
			match(QUOTEMARK);
			setState(219);
			match(SEPARATOR);
			setState(220);
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
		enterRule(_localctx, 14, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
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
		enterRule(_localctx, 16, RULE_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			match(STARTEXPR);
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(228);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(225);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(226);
					match(DIV);
					}
					break;
				case 3:
					{
					setState(227);
					match(STRREF);
					}
					break;
				}
				setState(247);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(230);
					literal();
					}
					break;
				case 2:
					{
					setState(231);
					string();
					}
					break;
				case 3:
					{
					setState(233);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(232);
						arithmetic();
						}
					}

					setState(235);
					number();
					}
					break;
				case 4:
					{
					setState(237);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(236);
						arithmetic();
						}
					}

					setState(239);
					floatNumber();
					}
					break;
				case 5:
					{
					setState(240);
					modulo();
					}
					break;
				case 6:
					{
					setState(241);
					iterator();
					}
					break;
				case 7:
					{
					setState(242);
					functionFire();
					}
					break;
				case 8:
					{
					setState(243);
					expression();
					}
					break;
				case 9:
					{
					setState(244);
					struct();
					}
					break;
				case 10:
					{
					setState(245);
					stringRef();
					}
					break;
				case 11:
					{
					setState(246);
					variable();
					}
					break;
				}
				setState(251);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(249);
					arithmetic();
					}
					break;
				case 2:
					{
					setState(250);
					match(STRREF);
					}
					break;
				}
				}
				}
				setState(257);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(258);
			match(STOPEXPR);
			setState(262);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(259);
					match(ENDINSTR);
					}
					} 
				}
				setState(264);
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
		enterRule(_localctx, 18, RULE_script);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(272);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					setState(265);
					codeBlock();
					}
					break;
				case 2:
					{
					setState(266);
					expression();
					}
					break;
				case 3:
					{
					setState(267);
					string();
					}
					break;
				case 4:
					{
					setState(268);
					ifInstr();
					}
					break;
				case 5:
					{
					setState(269);
					loopInstr();
					}
					break;
				case 6:
					{
					setState(270);
					whileInstr();
					}
					break;
				case 7:
					{
					setState(271);
					string();
					}
					break;
				}
				}
				setState(276);
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
		enterRule(_localctx, 20, RULE_param);
		int _la;
		try {
			setState(294);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(277);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(278);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(279);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(280);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(281);
				literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(282);
					arithmetic();
					}
				}

				setState(285);
				number();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(286);
					arithmetic();
					}
				}

				setState(289);
				floatNumber();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(290);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(291);
				iterator();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(292);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(293);
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
		enterRule(_localctx, 22, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << ITERATOR) | (1L << LITERAL) | (1L << LOGIC) | (1L << STRREF))) != 0)) {
				{
				{
				setState(297);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOGIC) {
					{
					setState(296);
					logic();
					}
				}

				setState(299);
				conditionPart();
				}
				}
				setState(304);
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
		enterRule(_localctx, 24, RULE_conditionPart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(305);
				literal();
				}
				break;
			case 2:
				{
				setState(306);
				functionFire();
				}
				break;
			case 3:
				{
				setState(307);
				struct();
				}
				break;
			case 4:
				{
				setState(308);
				expression();
				}
				break;
			case 5:
				{
				setState(309);
				iterator();
				}
				break;
			}
			setState(312);
			compare();
			setState(313);
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
		enterRule(_localctx, 26, RULE_behFire);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			literal();
			setState(316);
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
		enterRule(_localctx, 28, RULE_modulo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(318);
				literal();
				}
				break;
			case 2:
				{
				setState(319);
				iterator();
				}
				break;
			case 3:
				{
				setState(321);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(320);
					arithmetic();
					}
				}

				setState(323);
				number();
				}
				break;
			case 4:
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
				floatNumber();
				}
				break;
			case 5:
				{
				setState(328);
				functionFire();
				}
				break;
			case 6:
				{
				setState(329);
				struct();
				}
				break;
			case 7:
				{
				setState(330);
				expression();
				}
				break;
			}
			setState(333);
			match(MOD);
			setState(346);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(334);
				literal();
				}
				break;
			case 2:
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
				number();
				}
				break;
			case 3:
				{
				setState(340);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(339);
					arithmetic();
					}
				}

				setState(342);
				floatNumber();
				}
				break;
			case 4:
				{
				setState(343);
				functionFire();
				}
				break;
			case 5:
				{
				setState(344);
				struct();
				}
				break;
			case 6:
				{
				setState(345);
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
		enterRule(_localctx, 30, RULE_iterator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
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
		enterRule(_localctx, 32, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(350);
			match(QUOTEMARK);
			setState(384);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(371); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(371);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
					case 1:
						{
						setState(351);
						literal();
						}
						break;
					case 2:
						{
						setState(352);
						match(FIREFUNC);
						}
						break;
					case 3:
						{
						setState(354);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(353);
							arithmetic();
							}
						}

						setState(358);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case NUMBER:
							{
							setState(356);
							number();
							}
							break;
						case FLOAT:
							{
							setState(357);
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
						setState(360);
						compare();
						}
						break;
					case 5:
						{
						setState(361);
						match(SLASH);
						}
						break;
					case 6:
						{
						setState(362);
						struct();
						}
						break;
					case 7:
						{
						setState(363);
						match(LPAREN);
						}
						break;
					case 8:
						{
						setState(364);
						match(RPAREN);
						}
						break;
					case 9:
						{
						setState(365);
						match(SEPARATOR);
						}
						break;
					case 10:
						{
						setState(366);
						arithmetic();
						}
						break;
					case 11:
						{
						setState(367);
						match(VARREF);
						}
						break;
					case 12:
						{
						setState(368);
						iterator();
						}
						break;
					case 13:
						{
						setState(369);
						expression();
						}
						break;
					case 14:
						{
						setState(370);
						functionFire();
						}
						break;
					}
					}
					setState(373); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << VARREF) | (1L << STARTEXPR) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << FIREFUNC) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR) | (1L << STRREF))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(375);
				variable();
				setState(380);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(376);
					match(SLASH);
					setState(378);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(377);
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
				setState(382);
				string();
				}
				break;
			case 4:
				{
				setState(383);
				match(BOOLEAN);
				}
				break;
			}
			setState(386);
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
		enterRule(_localctx, 34, RULE_instr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			match(DIV);
			setState(389);
			literal();
			setState(390);
			match(LPAREN);
			setState(396);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << BOOLEAN) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(391);
				param();
				setState(394);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(392);
					match(SEPARATOR);
					setState(393);
					param();
					}
				}

				}
			}

			setState(398);
			match(RPAREN);
			setState(399);
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
		enterRule(_localctx, 36, RULE_stringRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(401);
			match(STRREF);
			setState(404);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(402);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(403);
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
		enterRule(_localctx, 38, RULE_struct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(406);
			literal();
			setState(407);
			match(STRUCTFIELD);
			setState(408);
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
		enterRule(_localctx, 40, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(410);
			match(VARREF);
			setState(413);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
				{
				setState(411);
				literal();
				}
				break;
			case NUMBER:
				{
				setState(412);
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
		enterRule(_localctx, 42, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
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
		enterRule(_localctx, 44, RULE_floatNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(417);
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
		enterRule(_localctx, 46, RULE_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(419);
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
		enterRule(_localctx, 48, RULE_arithmetic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
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
		enterRule(_localctx, 50, RULE_logic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
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
		enterRule(_localctx, 52, RULE_compare);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(425);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u01ae\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\5\2C\n\2\3\2\3\2\5\2G\n\2\3\2\3\2\3\2\5\2L\n\2\3\2\3\2\7\2P\n\2\f\2\16"+
		"\2S\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3]\n\3\3\3\3\3\3\3\5\3b\n\3"+
		"\3\3\3\3\3\3\3\3\3\3\5\3i\n\3\3\3\3\3\3\3\5\3n\n\3\3\3\3\3\3\3\3\3\3\3"+
		"\5\3u\n\3\3\3\3\3\3\3\5\3z\n\3\3\3\3\3\7\3~\n\3\f\3\16\3\u0081\13\3\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u008f\n\4\3\4\3\4\7"+
		"\4\u0093\n\4\f\4\16\4\u0096\13\4\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u009e\n\5"+
		"\3\5\3\5\3\5\3\5\5\5\u00a4\n\5\3\5\7\5\u00a7\n\5\f\5\16\5\u00aa\13\5\3"+
		"\5\3\5\7\5\u00ae\n\5\f\5\16\5\u00b1\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\5\6\u00bf\n\6\3\6\3\6\7\6\u00c3\n\6\f\6\16\6\u00c6"+
		"\13\6\7\6\u00c8\n\6\f\6\16\6\u00cb\13\6\3\6\7\6\u00ce\n\6\f\6\16\6\u00d1"+
		"\13\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t"+
		"\3\n\3\n\3\n\3\n\5\n\u00e7\n\n\3\n\3\n\3\n\5\n\u00ec\n\n\3\n\3\n\5\n\u00f0"+
		"\n\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00fa\n\n\3\n\3\n\5\n\u00fe\n"+
		"\n\7\n\u0100\n\n\f\n\16\n\u0103\13\n\3\n\3\n\7\n\u0107\n\n\f\n\16\n\u010a"+
		"\13\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u0113\n\13\f\13\16\13\u0116"+
		"\13\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u011e\n\f\3\f\3\f\5\f\u0122\n\f\3\f"+
		"\3\f\3\f\3\f\3\f\5\f\u0129\n\f\3\r\5\r\u012c\n\r\3\r\7\r\u012f\n\r\f\r"+
		"\16\r\u0132\13\r\3\16\3\16\3\16\3\16\3\16\5\16\u0139\n\16\3\16\3\16\3"+
		"\16\3\17\3\17\3\17\3\20\3\20\3\20\5\20\u0144\n\20\3\20\3\20\5\20\u0148"+
		"\n\20\3\20\3\20\3\20\3\20\5\20\u014e\n\20\3\20\3\20\3\20\5\20\u0153\n"+
		"\20\3\20\3\20\5\20\u0157\n\20\3\20\3\20\3\20\3\20\5\20\u015d\n\20\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\5\22\u0165\n\22\3\22\3\22\5\22\u0169\n\22\3"+
		"\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\6\22\u0176\n\22"+
		"\r\22\16\22\u0177\3\22\3\22\3\22\5\22\u017d\n\22\5\22\u017f\n\22\3\22"+
		"\3\22\5\22\u0183\n\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u018d"+
		"\n\23\5\23\u018f\n\23\3\23\3\23\3\23\3\24\3\24\3\24\5\24\u0197\n\24\3"+
		"\25\3\25\3\25\3\25\3\26\3\26\3\26\5\26\u01a0\n\26\3\27\3\27\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\2\2\35\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\66\2\3\3\2\17\24\2\u0216\28\3"+
		"\2\2\2\4T\3\2\2\2\6\u0082\3\2\2\2\b\u009d\3\2\2\2\n\u00b2\3\2\2\2\f\u00d4"+
		"\3\2\2\2\16\u00d8\3\2\2\2\20\u00e0\3\2\2\2\22\u00e2\3\2\2\2\24\u0114\3"+
		"\2\2\2\26\u0128\3\2\2\2\30\u0130\3\2\2\2\32\u0138\3\2\2\2\34\u013d\3\2"+
		"\2\2\36\u014d\3\2\2\2 \u015e\3\2\2\2\"\u0160\3\2\2\2$\u0186\3\2\2\2&\u0193"+
		"\3\2\2\2(\u0198\3\2\2\2*\u019c\3\2\2\2,\u01a1\3\2\2\2.\u01a3\3\2\2\2\60"+
		"\u01a5\3\2\2\2\62\u01a7\3\2\2\2\64\u01a9\3\2\2\2\66\u01ab\3\2\2\289\7"+
		"\3\2\29B\7\7\2\2:;\7#\2\2;<\5\30\r\2<=\7#\2\2=>\7!\2\2>C\3\2\2\2?@\5\16"+
		"\b\2@A\7!\2\2AC\3\2\2\2B:\3\2\2\2B?\3\2\2\2CF\3\2\2\2DG\5\n\6\2EG\5\""+
		"\22\2FD\3\2\2\2FE\3\2\2\2GH\3\2\2\2HK\7!\2\2IL\5\n\6\2JL\5\"\22\2KI\3"+
		"\2\2\2KJ\3\2\2\2LM\3\2\2\2MQ\7\b\2\2NP\7\25\2\2ON\3\2\2\2PS\3\2\2\2QO"+
		"\3\2\2\2QR\3\2\2\2R\3\3\2\2\2SQ\3\2\2\2TU\7\4\2\2UV\7\7\2\2VW\5\n\6\2"+
		"Wa\7!\2\2Xb\5\"\22\2Yb\5\60\31\2Zb\5\22\n\2[]\5\62\32\2\\[\3\2\2\2\\]"+
		"\3\2\2\2]^\3\2\2\2^b\5,\27\2_b\5\b\5\2`b\5(\25\2aX\3\2\2\2aY\3\2\2\2a"+
		"Z\3\2\2\2a\\\3\2\2\2a_\3\2\2\2a`\3\2\2\2bc\3\2\2\2cm\7!\2\2dn\5\"\22\2"+
		"en\5\60\31\2fn\5\22\n\2gi\5\62\32\2hg\3\2\2\2hi\3\2\2\2ij\3\2\2\2jn\5"+
		",\27\2kn\5\b\5\2ln\5(\25\2md\3\2\2\2me\3\2\2\2mf\3\2\2\2mh\3\2\2\2mk\3"+
		"\2\2\2ml\3\2\2\2no\3\2\2\2oy\7!\2\2pz\5\"\22\2qz\5\60\31\2rz\5\22\n\2"+
		"su\5\62\32\2ts\3\2\2\2tu\3\2\2\2uv\3\2\2\2vz\5,\27\2wz\5\b\5\2xz\5(\25"+
		"\2yp\3\2\2\2yq\3\2\2\2yr\3\2\2\2yt\3\2\2\2yw\3\2\2\2yx\3\2\2\2z{\3\2\2"+
		"\2{\177\7\b\2\2|~\7\25\2\2}|\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080"+
		"\3\2\2\2\u0080\5\3\2\2\2\u0081\177\3\2\2\2\u0082\u0083\7\5\2\2\u0083\u0084"+
		"\7\7\2\2\u0084\u0085\5\26\f\2\u0085\u0086\7!\2\2\u0086\u0087\7#\2\2\u0087"+
		"\u0088\5\66\34\2\u0088\u0089\7#\2\2\u0089\u008a\7!\2\2\u008a\u008b\5\26"+
		"\f\2\u008b\u008e\7!\2\2\u008c\u008f\5\n\6\2\u008d\u008f\5\"\22\2\u008e"+
		"\u008c\3\2\2\2\u008e\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0094\7\b"+
		"\2\2\u0091\u0093\7\25\2\2\u0092\u0091\3\2\2\2\u0093\u0096\3\2\2\2\u0094"+
		"\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\7\3\2\2\2\u0096\u0094\3\2\2\2"+
		"\u0097\u009e\5\60\31\2\u0098\u009e\5 \21\2\u0099\u009e\5&\24\2\u009a\u009e"+
		"\5(\25\2\u009b\u009e\5*\26\2\u009c\u009e\5\f\7\2\u009d\u0097\3\2\2\2\u009d"+
		"\u0098\3\2\2\2\u009d\u0099\3\2\2\2\u009d\u009a\3\2\2\2\u009d\u009b\3\2"+
		"\2\2\u009d\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\7\26\2\2\u00a0"+
		"\u00a1\5\60\31\2\u00a1\u00a8\7\7\2\2\u00a2\u00a4\7!\2\2\u00a3\u00a2\3"+
		"\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a7\5\26\f\2\u00a6"+
		"\u00a3\3\2\2\2\u00a7\u00aa\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9\3\2"+
		"\2\2\u00a9\u00ab\3\2\2\2\u00aa\u00a8\3\2\2\2\u00ab\u00af\7\b\2\2\u00ac"+
		"\u00ae\7\25\2\2\u00ad\u00ac\3\2\2\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3"+
		"\2\2\2\u00af\u00b0\3\2\2\2\u00b0\t\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00c9"+
		"\7\n\2\2\u00b3\u00c8\5\b\5\2\u00b4\u00c8\5\2\2\2\u00b5\u00c8\5\4\3\2\u00b6"+
		"\u00c8\5\6\4\2\u00b7\u00c8\5$\23\2\u00b8\u00c8\5\34\17\2\u00b9\u00c8\5"+
		"\22\n\2\u00ba\u00c8\5\n\6\2\u00bb\u00bf\5\60\31\2\u00bc\u00bf\5.\30\2"+
		"\u00bd\u00bf\5,\27\2\u00be\u00bb\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bd"+
		"\3\2\2\2\u00bf\u00c8\3\2\2\2\u00c0\u00c4\5\20\t\2\u00c1\u00c3\7\25\2\2"+
		"\u00c2\u00c1\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5"+
		"\3\2\2\2\u00c5\u00c8\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7\u00b3\3\2\2\2\u00c7"+
		"\u00b4\3\2\2\2\u00c7\u00b5\3\2\2\2\u00c7\u00b6\3\2\2\2\u00c7\u00b7\3\2"+
		"\2\2\u00c7\u00b8\3\2\2\2\u00c7\u00b9\3\2\2\2\u00c7\u00ba\3\2\2\2\u00c7"+
		"\u00be\3\2\2\2\u00c7\u00c0\3\2\2\2\u00c8\u00cb\3\2\2\2\u00c9\u00c7\3\2"+
		"\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cf\3\2\2\2\u00cb\u00c9\3\2\2\2\u00cc"+
		"\u00ce\7\25\2\2\u00cd\u00cc\3\2\2\2\u00ce\u00d1\3\2\2\2\u00cf\u00cd\3"+
		"\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d2\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d2"+
		"\u00d3\7\13\2\2\u00d3\13\3\2\2\2\u00d4\u00d5\5\60\31\2\u00d5\u00d6\5\62"+
		"\32\2\u00d6\u00d7\5,\27\2\u00d7\r\3\2\2\2\u00d8\u00d9\5\26\f\2\u00d9\u00da"+
		"\7!\2\2\u00da\u00db\7#\2\2\u00db\u00dc\5\66\34\2\u00dc\u00dd\7#\2\2\u00dd"+
		"\u00de\7!\2\2\u00de\u00df\5\26\f\2\u00df\17\3\2\2\2\u00e0\u00e1\7\6\2"+
		"\2\u00e1\21\3\2\2\2\u00e2\u0101\7\f\2\2\u00e3\u00e7\5\62\32\2\u00e4\u00e7"+
		"\7\27\2\2\u00e5\u00e7\7$\2\2\u00e6\u00e3\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6"+
		"\u00e5\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00f9\3\2\2\2\u00e8\u00fa\5\60"+
		"\31\2\u00e9\u00fa\5\"\22\2\u00ea\u00ec\5\62\32\2\u00eb\u00ea\3\2\2\2\u00eb"+
		"\u00ec\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00fa\5,\27\2\u00ee\u00f0\5\62"+
		"\32\2\u00ef\u00ee\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1"+
		"\u00fa\5.\30\2\u00f2\u00fa\5\36\20\2\u00f3\u00fa\5 \21\2\u00f4\u00fa\5"+
		"\b\5\2\u00f5\u00fa\5\22\n\2\u00f6\u00fa\5(\25\2\u00f7\u00fa\5&\24\2\u00f8"+
		"\u00fa\5*\26\2\u00f9\u00e8\3\2\2\2\u00f9\u00e9\3\2\2\2\u00f9\u00eb\3\2"+
		"\2\2\u00f9\u00ef\3\2\2\2\u00f9\u00f2\3\2\2\2\u00f9\u00f3\3\2\2\2\u00f9"+
		"\u00f4\3\2\2\2\u00f9\u00f5\3\2\2\2\u00f9\u00f6\3\2\2\2\u00f9\u00f7\3\2"+
		"\2\2\u00f9\u00f8\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00fe\5\62\32\2\u00fc"+
		"\u00fe\7$\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fc\3\2\2\2\u00fd\u00fe\3\2"+
		"\2\2\u00fe\u0100\3\2\2\2\u00ff\u00e6\3\2\2\2\u0100\u0103\3\2\2\2\u0101"+
		"\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0104\3\2\2\2\u0103\u0101\3\2"+
		"\2\2\u0104\u0108\7\r\2\2\u0105\u0107\7\25\2\2\u0106\u0105\3\2\2\2\u0107"+
		"\u010a\3\2\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109\23\3\2\2"+
		"\2\u010a\u0108\3\2\2\2\u010b\u0113\5\n\6\2\u010c\u0113\5\22\n\2\u010d"+
		"\u0113\5\"\22\2\u010e\u0113\5\2\2\2\u010f\u0113\5\4\3\2\u0110\u0113\5"+
		"\6\4\2\u0111\u0113\5\"\22\2\u0112\u010b\3\2\2\2\u0112\u010c\3\2\2\2\u0112"+
		"\u010d\3\2\2\2\u0112\u010e\3\2\2\2\u0112\u010f\3\2\2\2\u0112\u0110\3\2"+
		"\2\2\u0112\u0111\3\2\2\2\u0113\u0116\3\2\2\2\u0114\u0112\3\2\2\2\u0114"+
		"\u0115\3\2\2\2\u0115\25\3\2\2\2\u0116\u0114\3\2\2\2\u0117\u0129\5\b\5"+
		"\2\u0118\u0129\7\35\2\2\u0119\u0129\5*\26\2\u011a\u0129\5\"\22\2\u011b"+
		"\u0129\5\60\31\2\u011c\u011e\5\62\32\2\u011d\u011c\3\2\2\2\u011d\u011e"+
		"\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0129\5,\27\2\u0120\u0122\5\62\32\2"+
		"\u0121\u0120\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0129"+
		"\5.\30\2\u0124\u0129\5\22\n\2\u0125\u0129\5 \21\2\u0126\u0129\5(\25\2"+
		"\u0127\u0129\5&\24\2\u0128\u0117\3\2\2\2\u0128\u0118\3\2\2\2\u0128\u0119"+
		"\3\2\2\2\u0128\u011a\3\2\2\2\u0128\u011b\3\2\2\2\u0128\u011d\3\2\2\2\u0128"+
		"\u0121\3\2\2\2\u0128\u0124\3\2\2\2\u0128\u0125\3\2\2\2\u0128\u0126\3\2"+
		"\2\2\u0128\u0127\3\2\2\2\u0129\27\3\2\2\2\u012a\u012c\5\64\33\2\u012b"+
		"\u012a\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012f\5\32"+
		"\16\2\u012e\u012b\3\2\2\2\u012f\u0132\3\2\2\2\u0130\u012e\3\2\2\2\u0130"+
		"\u0131\3\2\2\2\u0131\31\3\2\2\2\u0132\u0130\3\2\2\2\u0133\u0139\5\60\31"+
		"\2\u0134\u0139\5\b\5\2\u0135\u0139\5(\25\2\u0136\u0139\5\22\n\2\u0137"+
		"\u0139\5 \21\2\u0138\u0133\3\2\2\2\u0138\u0134\3\2\2\2\u0138\u0135\3\2"+
		"\2\2\u0138\u0136\3\2\2\2\u0138\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a"+
		"\u013b\5\66\34\2\u013b\u013c\5\26\f\2\u013c\33\3\2\2\2\u013d\u013e\5\60"+
		"\31\2\u013e\u013f\7\25\2\2\u013f\35\3\2\2\2\u0140\u014e\5\60\31\2\u0141"+
		"\u014e\5 \21\2\u0142\u0144\5\62\32\2\u0143\u0142\3\2\2\2\u0143\u0144\3"+
		"\2\2\2\u0144\u0145\3\2\2\2\u0145\u014e\5,\27\2\u0146\u0148\5\62\32\2\u0147"+
		"\u0146\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u0149\3\2\2\2\u0149\u014e\5."+
		"\30\2\u014a\u014e\5\b\5\2\u014b\u014e\5(\25\2\u014c\u014e\5\22\n\2\u014d"+
		"\u0140\3\2\2\2\u014d\u0141\3\2\2\2\u014d\u0143\3\2\2\2\u014d\u0147\3\2"+
		"\2\2\u014d\u014a\3\2\2\2\u014d\u014b\3\2\2\2\u014d\u014c\3\2\2\2\u014e"+
		"\u014f\3\2\2\2\u014f\u015c\7\30\2\2\u0150\u015d\5\60\31\2\u0151\u0153"+
		"\5\62\32\2\u0152\u0151\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\3\2\2\2"+
		"\u0154\u015d\5,\27\2\u0155\u0157\5\62\32\2\u0156\u0155\3\2\2\2\u0156\u0157"+
		"\3\2\2\2\u0157\u0158\3\2\2\2\u0158\u015d\5.\30\2\u0159\u015d\5\b\5\2\u015a"+
		"\u015d\5(\25\2\u015b\u015d\5\22\n\2\u015c\u0150\3\2\2\2\u015c\u0152\3"+
		"\2\2\2\u015c\u0156\3\2\2\2\u015c\u0159\3\2\2\2\u015c\u015a\3\2\2\2\u015c"+
		"\u015b\3\2\2\2\u015d\37\3\2\2\2\u015e\u015f\7\34\2\2\u015f!\3\2\2\2\u0160"+
		"\u0182\7#\2\2\u0161\u0176\5\60\31\2\u0162\u0176\7\26\2\2\u0163\u0165\5"+
		"\62\32\2\u0164\u0163\3\2\2\2\u0164\u0165\3\2\2\2\u0165\u0168\3\2\2\2\u0166"+
		"\u0169\5,\27\2\u0167\u0169\5.\30\2\u0168\u0166\3\2\2\2\u0168\u0167\3\2"+
		"\2\2\u0169\u0176\3\2\2\2\u016a\u0176\5\66\34\2\u016b\u0176\7\16\2\2\u016c"+
		"\u0176\5(\25\2\u016d\u0176\7\7\2\2\u016e\u0176\7\b\2\2\u016f\u0176\7!"+
		"\2\2\u0170\u0176\5\62\32\2\u0171\u0176\7\t\2\2\u0172\u0176\5 \21\2\u0173"+
		"\u0176\5\22\n\2\u0174\u0176\5\b\5\2\u0175\u0161\3\2\2\2\u0175\u0162\3"+
		"\2\2\2\u0175\u0164\3\2\2\2\u0175\u016a\3\2\2\2\u0175\u016b\3\2\2\2\u0175"+
		"\u016c\3\2\2\2\u0175\u016d\3\2\2\2\u0175\u016e\3\2\2\2\u0175\u016f\3\2"+
		"\2\2\u0175\u0170\3\2\2\2\u0175\u0171\3\2\2\2\u0175\u0172\3\2\2\2\u0175"+
		"\u0173\3\2\2\2\u0175\u0174\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0175\3\2"+
		"\2\2\u0177\u0178\3\2\2\2\u0178\u0183\3\2\2\2\u0179\u017e\5*\26\2\u017a"+
		"\u017c\7\16\2\2\u017b\u017d\5\60\31\2\u017c\u017b\3\2\2\2\u017c\u017d"+
		"\3\2\2\2\u017d\u017f\3\2\2\2\u017e\u017a\3\2\2\2\u017e\u017f\3\2\2\2\u017f"+
		"\u0183\3\2\2\2\u0180\u0183\5\"\22\2\u0181\u0183\7\35\2\2\u0182\u0175\3"+
		"\2\2\2\u0182\u0179\3\2\2\2\u0182\u0180\3\2\2\2\u0182\u0181\3\2\2\2\u0182"+
		"\u0183\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0185\7#\2\2\u0185#\3\2\2\2\u0186"+
		"\u0187\7\27\2\2\u0187\u0188\5\60\31\2\u0188\u018e\7\7\2\2\u0189\u018c"+
		"\5\26\f\2\u018a\u018b\7!\2\2\u018b\u018d\5\26\f\2\u018c\u018a\3\2\2\2"+
		"\u018c\u018d\3\2\2\2\u018d\u018f\3\2\2\2\u018e\u0189\3\2\2\2\u018e\u018f"+
		"\3\2\2\2\u018f\u0190\3\2\2\2\u0190\u0191\7\b\2\2\u0191\u0192\7\25\2\2"+
		"\u0192%\3\2\2\2\u0193\u0196\7$\2\2\u0194\u0197\5\22\n\2\u0195\u0197\5"+
		"\60\31\2\u0196\u0194\3\2\2\2\u0196\u0195\3\2\2\2\u0197\'\3\2\2\2\u0198"+
		"\u0199\5\60\31\2\u0199\u019a\7\"\2\2\u019a\u019b\5\60\31\2\u019b)\3\2"+
		"\2\2\u019c\u019f\7\t\2\2\u019d\u01a0\5\60\31\2\u019e\u01a0\5,\27\2\u019f"+
		"\u019d\3\2\2\2\u019f\u019e\3\2\2\2\u01a0+\3\2\2\2\u01a1\u01a2\7\31\2\2"+
		"\u01a2-\3\2\2\2\u01a3\u01a4\7\32\2\2\u01a4/\3\2\2\2\u01a5\u01a6\7\36\2"+
		"\2\u01a6\61\3\2\2\2\u01a7\u01a8\7\37\2\2\u01a8\63\3\2\2\2\u01a9\u01aa"+
		"\7 \2\2\u01aa\65\3\2\2\2\u01ab\u01ac\t\2\2\2\u01ac\67\3\2\2\28BFKQ\\a"+
		"hmty\177\u008e\u0094\u009d\u00a3\u00a8\u00af\u00be\u00c4\u00c7\u00c9\u00cf"+
		"\u00e6\u00eb\u00ef\u00f9\u00fd\u0101\u0108\u0112\u0114\u011d\u0121\u0128"+
		"\u012b\u0130\u0138\u0143\u0147\u014d\u0152\u0156\u015c\u0164\u0168\u0175"+
		"\u0177\u017c\u017e\u0182\u018c\u018e\u0196\u019f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}