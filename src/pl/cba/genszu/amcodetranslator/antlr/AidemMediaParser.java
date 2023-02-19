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
			int _alt;
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
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(62);
					match(ENDINSTR);
					}
					} 
				}
				setState(67);
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
			int _alt;
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
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(108);
					match(ENDINSTR);
					}
					} 
				}
				setState(113);
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
	}

	public final WhileInstrContext whileInstr() throws RecognitionException {
		WhileInstrContext _localctx = new WhileInstrContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_whileInstr);
		int _la;
		try {
			int _alt;
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
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(129);
					match(ENDINSTR);
					}
					} 
				}
				setState(134);
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
	}

	public final FunctionFireContext functionFire() throws RecognitionException {
		FunctionFireContext _localctx = new FunctionFireContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_functionFire);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
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
			case 5:
				{
				setState(139);
				variable();
				}
				break;
			}
			setState(142);
			match(FIREFUNC);
			setState(143);
			match(LITERAL);
			setState(144);
			match(LPAREN);
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << BOOLEAN) | (1L << SEPARATOR) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(145);
					match(SEPARATOR);
					}
				}

				setState(148);
				param();
				}
				}
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(154);
			match(RPAREN);
			setState(158);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(155);
					match(ENDINSTR);
					}
					} 
				}
				setState(160);
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
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			match(STARTCODE);
			setState(179);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << VARREF) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << STRREF))) != 0)) {
				{
				setState(177);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(162);
					functionFire();
					}
					break;
				case 2:
					{
					setState(163);
					ifInstr();
					}
					break;
				case 3:
					{
					setState(164);
					loopInstr();
					}
					break;
				case 4:
					{
					setState(165);
					whileInstr();
					}
					break;
				case 5:
					{
					setState(166);
					instr();
					}
					break;
				case 6:
					{
					setState(167);
					behFire();
					}
					break;
				case 7:
					{
					setState(168);
					expression();
					}
					break;
				case 8:
					{
					setState(169);
					codeBlock();
					}
					break;
				case 9:
					{
					setState(170);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NUMBER) | (1L << FLOAT) | (1L << LITERAL))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(174);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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
						_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
					}
					}
					break;
				}
				}
				setState(181);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDINSTR) {
				{
				{
				setState(182);
				match(ENDINSTR);
				}
				}
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(188);
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
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(STARTEXPR);
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << DIV) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				{
				setState(192);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(191);
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
				setState(211);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(194);
					match(LITERAL);
					}
					break;
				case 2:
					{
					setState(195);
					string();
					}
					break;
				case 3:
					{
					setState(197);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(196);
						match(ARITHMETIC);
						}
					}

					setState(199);
					match(NUMBER);
					}
					break;
				case 4:
					{
					setState(201);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ARITHMETIC) {
						{
						setState(200);
						match(ARITHMETIC);
						}
					}

					setState(203);
					match(FLOAT);
					}
					break;
				case 5:
					{
					setState(204);
					modulo();
					}
					break;
				case 6:
					{
					setState(205);
					match(ITERATOR);
					}
					break;
				case 7:
					{
					setState(206);
					functionFire();
					}
					break;
				case 8:
					{
					setState(207);
					expression();
					}
					break;
				case 9:
					{
					setState(208);
					struct();
					}
					break;
				case 10:
					{
					setState(209);
					stringRef();
					}
					break;
				case 11:
					{
					setState(210);
					variable();
					}
					break;
				}
				setState(214);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(213);
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
				setState(220);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(221);
			match(STOPEXPR);
			setState(225);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(222);
					match(ENDINSTR);
					}
					} 
				}
				setState(227);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
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
			setState(237);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << STARTCODE) | (1L << STARTEXPR) | (1L << QUOTEMARK))) != 0)) {
				{
				setState(235);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
				case 1:
					{
					setState(228);
					codeBlock();
					}
					break;
				case 2:
					{
					setState(229);
					expression();
					}
					break;
				case 3:
					{
					setState(230);
					string();
					}
					break;
				case 4:
					{
					setState(231);
					ifInstr();
					}
					break;
				case 5:
					{
					setState(232);
					loopInstr();
					}
					break;
				case 6:
					{
					setState(233);
					whileInstr();
					}
					break;
				case 7:
					{
					setState(234);
					string();
					}
					break;
				}
				}
				setState(239);
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
			setState(257);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(240);
				functionFire();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(241);
				match(BOOLEAN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(242);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(243);
				string();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(244);
				match(LITERAL);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(246);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(245);
					match(ARITHMETIC);
					}
				}

				setState(248);
				match(NUMBER);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(250);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(249);
					match(ARITHMETIC);
					}
				}

				setState(252);
				match(FLOAT);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(253);
				expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(254);
				match(ITERATOR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(255);
				struct();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(256);
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
			setState(265);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << ITERATOR) | (1L << LITERAL) | (1L << LOGIC) | (1L << STRREF))) != 0)) {
				{
				{
				setState(260);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOGIC) {
					{
					setState(259);
					match(LOGIC);
					}
				}

				setState(262);
				conditionPart();
				}
				}
				setState(267);
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
			setState(273);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				{
				setState(268);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(269);
				functionFire();
				}
				break;
			case 3:
				{
				setState(270);
				struct();
				}
				break;
			case 4:
				{
				setState(271);
				expression();
				}
				break;
			case 5:
				{
				setState(272);
				match(ITERATOR);
				}
				break;
			}
			setState(275);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(276);
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
			setState(278);
			match(LITERAL);
			setState(279);
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
			setState(294);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(281);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(282);
				match(ITERATOR);
				}
				break;
			case 3:
				{
				setState(284);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(283);
					match(ARITHMETIC);
					}
				}

				setState(286);
				match(NUMBER);
				}
				break;
			case 4:
				{
				setState(288);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(287);
					match(ARITHMETIC);
					}
				}

				setState(290);
				match(FLOAT);
				}
				break;
			case 5:
				{
				setState(291);
				functionFire();
				}
				break;
			case 6:
				{
				setState(292);
				struct();
				}
				break;
			case 7:
				{
				setState(293);
				expression();
				}
				break;
			}
			setState(296);
			match(MOD);
			setState(309);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(297);
				match(LITERAL);
				}
				break;
			case 2:
				{
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(298);
					match(ARITHMETIC);
					}
				}

				setState(301);
				match(NUMBER);
				}
				break;
			case 3:
				{
				setState(303);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARITHMETIC) {
					{
					setState(302);
					match(ARITHMETIC);
					}
				}

				setState(305);
				match(FLOAT);
				}
				break;
			case 4:
				{
				setState(306);
				functionFire();
				}
				break;
			case 5:
				{
				setState(307);
				struct();
				}
				break;
			case 6:
				{
				setState(308);
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
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(311);
			match(QUOTEMARK);
			setState(350);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				{
				setState(338); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					setState(338);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
					case 1:
						{
						setState(312);
						match(LITERAL);
						}
						break;
					case 2:
						{
						setState(313);
						match(FIREFUNC);
						}
						break;
					case 3:
						{
						setState(315);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(314);
							match(ARITHMETIC);
							}
						}

						setState(317);
						match(NUMBER);
						}
						break;
					case 4:
						{
						setState(319);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ARITHMETIC) {
							{
							setState(318);
							match(ARITHMETIC);
							}
						}

						setState(321);
						match(FLOAT);
						}
						break;
					case 5:
						{
						setState(322);
						match(LSS);
						}
						break;
					case 6:
						{
						setState(323);
						match(LEQ);
						}
						break;
					case 7:
						{
						setState(324);
						match(GEQ);
						}
						break;
					case 8:
						{
						setState(325);
						match(GTR);
						}
						break;
					case 9:
						{
						setState(326);
						match(EQU);
						}
						break;
					case 10:
						{
						setState(327);
						match(NEQ);
						}
						break;
					case 11:
						{
						setState(328);
						match(SLASH);
						}
						break;
					case 12:
						{
						setState(329);
						struct();
						}
						break;
					case 13:
						{
						setState(330);
						match(LPAREN);
						}
						break;
					case 14:
						{
						setState(331);
						match(RPAREN);
						}
						break;
					case 15:
						{
						setState(332);
						match(SEPARATOR);
						}
						break;
					case 16:
						{
						setState(333);
						match(ARITHMETIC);
						}
						break;
					case 17:
						{
						setState(334);
						match(VARREF);
						}
						break;
					case 18:
						{
						setState(335);
						match(ITERATOR);
						}
						break;
					case 19:
						{
						setState(336);
						expression();
						}
						break;
					case 20:
						{
						setState(337);
						functionFire();
						}
						break;
					}
					}
					setState(340); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << VARREF) | (1L << STARTEXPR) | (1L << SLASH) | (1L << LSS) | (1L << LEQ) | (1L << GEQ) | (1L << GTR) | (1L << EQU) | (1L << NEQ) | (1L << FIREFUNC) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << SEPARATOR) | (1L << STRREF))) != 0) );
				}
				break;
			case 2:
				{
				{
				setState(342);
				variable();
				setState(347);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SLASH) {
					{
					setState(343);
					match(SLASH);
					setState(345);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LITERAL) {
						{
						setState(344);
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
				setState(349);
				string();
				}
				break;
			}
			setState(352);
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
			setState(354);
			match(DIV);
			setState(355);
			match(LITERAL);
			setState(356);
			match(LPAREN);
			setState(362);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARREF) | (1L << STARTEXPR) | (1L << NUMBER) | (1L << FLOAT) | (1L << ITERATOR) | (1L << LITERAL) | (1L << ARITHMETIC) | (1L << BOOLEAN) | (1L << QUOTEMARK) | (1L << STRREF))) != 0)) {
				{
				setState(357);
				param();
				setState(360);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(358);
					match(SEPARATOR);
					setState(359);
					param();
					}
				}

				}
			}

			setState(364);
			match(RPAREN);
			setState(365);
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
			setState(367);
			match(STRREF);
			setState(370);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STARTEXPR:
				{
				setState(368);
				expression();
				}
				break;
			case LITERAL:
				{
				setState(369);
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
			setState(372);
			match(LITERAL);
			setState(373);
			match(STRUCTFIELD);
			setState(374);
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
			setState(376);
			match(VARREF);
			setState(377);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u017e\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\65"+
		"\n\2\3\2\3\2\5\29\n\2\3\2\3\2\3\2\5\2>\n\2\3\2\3\2\7\2B\n\2\f\2\16\2E"+
		"\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3O\n\3\3\3\3\3\3\3\5\3T\n\3\3"+
		"\3\3\3\3\3\3\3\3\3\5\3[\n\3\3\3\3\3\3\3\5\3`\n\3\3\3\3\3\3\3\3\3\3\3\5"+
		"\3g\n\3\3\3\3\3\3\3\5\3l\n\3\3\3\3\3\7\3p\n\3\f\3\16\3s\13\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u0081\n\4\3\4\3\4\7\4\u0085"+
		"\n\4\f\4\16\4\u0088\13\4\3\5\3\5\3\5\3\5\3\5\5\5\u008f\n\5\3\5\3\5\3\5"+
		"\3\5\5\5\u0095\n\5\3\5\7\5\u0098\n\5\f\5\16\5\u009b\13\5\3\5\3\5\7\5\u009f"+
		"\n\5\f\5\16\5\u00a2\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7"+
		"\6\u00af\n\6\f\6\16\6\u00b2\13\6\7\6\u00b4\n\6\f\6\16\6\u00b7\13\6\3\6"+
		"\7\6\u00ba\n\6\f\6\16\6\u00bd\13\6\3\6\3\6\3\7\3\7\5\7\u00c3\n\7\3\7\3"+
		"\7\3\7\5\7\u00c8\n\7\3\7\3\7\5\7\u00cc\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\5\7\u00d6\n\7\3\7\5\7\u00d9\n\7\7\7\u00db\n\7\f\7\16\7\u00de\13\7"+
		"\3\7\3\7\7\7\u00e2\n\7\f\7\16\7\u00e5\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\7\b\u00ee\n\b\f\b\16\b\u00f1\13\b\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u00f9\n"+
		"\t\3\t\3\t\5\t\u00fd\n\t\3\t\3\t\3\t\3\t\3\t\5\t\u0104\n\t\3\n\5\n\u0107"+
		"\n\n\3\n\7\n\u010a\n\n\f\n\16\n\u010d\13\n\3\13\3\13\3\13\3\13\3\13\5"+
		"\13\u0114\n\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\5\r\u011f\n\r\3"+
		"\r\3\r\5\r\u0123\n\r\3\r\3\r\3\r\3\r\5\r\u0129\n\r\3\r\3\r\3\r\5\r\u012e"+
		"\n\r\3\r\3\r\5\r\u0132\n\r\3\r\3\r\3\r\3\r\5\r\u0138\n\r\3\16\3\16\3\16"+
		"\3\16\5\16\u013e\n\16\3\16\3\16\5\16\u0142\n\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\6\16\u0155"+
		"\n\16\r\16\16\16\u0156\3\16\3\16\3\16\5\16\u015c\n\16\5\16\u015e\n\16"+
		"\3\16\5\16\u0161\n\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u016b"+
		"\n\17\5\17\u016d\n\17\3\17\3\17\3\17\3\20\3\20\3\20\5\20\u0175\n\20\3"+
		"\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\2\2\23\2\4\6\b\n\f\16\20\22\24"+
		"\26\30\32\34\36 \"\2\7\3\2\16\23\4\2\30\31\34\34\5\2\26\26\35\35##\4\2"+
		"\35\35##\4\2\30\30\34\34\2\u01ed\2$\3\2\2\2\4F\3\2\2\2\6t\3\2\2\2\b\u008e"+
		"\3\2\2\2\n\u00a3\3\2\2\2\f\u00c0\3\2\2\2\16\u00ef\3\2\2\2\20\u0103\3\2"+
		"\2\2\22\u010b\3\2\2\2\24\u0113\3\2\2\2\26\u0118\3\2\2\2\30\u0128\3\2\2"+
		"\2\32\u0139\3\2\2\2\34\u0164\3\2\2\2\36\u0171\3\2\2\2 \u0176\3\2\2\2\""+
		"\u017a\3\2\2\2$%\7\3\2\2%\64\7\6\2\2&\'\7\"\2\2\'(\5\22\n\2()\7\"\2\2"+
		")*\7 \2\2*\65\3\2\2\2+,\5\20\t\2,-\7 \2\2-.\7\"\2\2./\t\2\2\2/\60\7\""+
		"\2\2\60\61\7 \2\2\61\62\5\20\t\2\62\63\7 \2\2\63\65\3\2\2\2\64&\3\2\2"+
		"\2\64+\3\2\2\2\658\3\2\2\2\669\5\n\6\2\679\5\32\16\28\66\3\2\2\28\67\3"+
		"\2\2\29:\3\2\2\2:=\7 \2\2;>\5\n\6\2<>\5\32\16\2=;\3\2\2\2=<\3\2\2\2>?"+
		"\3\2\2\2?C\7\7\2\2@B\7\24\2\2A@\3\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2"+
		"D\3\3\2\2\2EC\3\2\2\2FG\7\4\2\2GH\7\6\2\2HI\5\n\6\2IS\7 \2\2JT\5\32\16"+
		"\2KT\7\34\2\2LT\5\f\7\2MO\7\35\2\2NM\3\2\2\2NO\3\2\2\2OP\3\2\2\2PT\7\30"+
		"\2\2QT\5\b\5\2RT\5 \21\2SJ\3\2\2\2SK\3\2\2\2SL\3\2\2\2SN\3\2\2\2SQ\3\2"+
		"\2\2SR\3\2\2\2TU\3\2\2\2U_\7 \2\2V`\5\32\16\2W`\7\34\2\2X`\5\f\7\2Y[\7"+
		"\35\2\2ZY\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2\\`\7\30\2\2]`\5\b\5\2^`\5 \21\2"+
		"_V\3\2\2\2_W\3\2\2\2_X\3\2\2\2_Z\3\2\2\2_]\3\2\2\2_^\3\2\2\2`a\3\2\2\2"+
		"ak\7 \2\2bl\5\32\16\2cl\7\34\2\2dl\5\f\7\2eg\7\35\2\2fe\3\2\2\2fg\3\2"+
		"\2\2gh\3\2\2\2hl\7\30\2\2il\5\b\5\2jl\5 \21\2kb\3\2\2\2kc\3\2\2\2kd\3"+
		"\2\2\2kf\3\2\2\2ki\3\2\2\2kj\3\2\2\2lm\3\2\2\2mq\7\7\2\2np\7\24\2\2on"+
		"\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2\2\2r\5\3\2\2\2sq\3\2\2\2tu\7\5\2\2"+
		"uv\7\6\2\2vw\5\20\t\2wx\7 \2\2xy\7\"\2\2yz\t\2\2\2z{\7\"\2\2{|\7 \2\2"+
		"|}\5\20\t\2}\u0080\7 \2\2~\u0081\5\n\6\2\177\u0081\5\32\16\2\u0080~\3"+
		"\2\2\2\u0080\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0086\7\7\2\2\u0083"+
		"\u0085\7\24\2\2\u0084\u0083\3\2\2\2\u0085\u0088\3\2\2\2\u0086\u0084\3"+
		"\2\2\2\u0086\u0087\3\2\2\2\u0087\7\3\2\2\2\u0088\u0086\3\2\2\2\u0089\u008f"+
		"\7\34\2\2\u008a\u008f\7\33\2\2\u008b\u008f\5\36\20\2\u008c\u008f\5 \21"+
		"\2\u008d\u008f\5\"\22\2\u008e\u0089\3\2\2\2\u008e\u008a\3\2\2\2\u008e"+
		"\u008b\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008d\3\2\2\2\u008f\u0090\3\2"+
		"\2\2\u0090\u0091\7\25\2\2\u0091\u0092\7\34\2\2\u0092\u0099\7\6\2\2\u0093"+
		"\u0095\7 \2\2\u0094\u0093\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0096\3\2"+
		"\2\2\u0096\u0098\5\20\t\2\u0097\u0094\3\2\2\2\u0098\u009b\3\2\2\2\u0099"+
		"\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009c\3\2\2\2\u009b\u0099\3\2"+
		"\2\2\u009c\u00a0\7\7\2\2\u009d\u009f\7\24\2\2\u009e\u009d\3\2\2\2\u009f"+
		"\u00a2\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\t\3\2\2\2"+
		"\u00a2\u00a0\3\2\2\2\u00a3\u00b5\7\t\2\2\u00a4\u00b4\5\b\5\2\u00a5\u00b4"+
		"\5\2\2\2\u00a6\u00b4\5\4\3\2\u00a7\u00b4\5\6\4\2\u00a8\u00b4\5\34\17\2"+
		"\u00a9\u00b4\5\26\f\2\u00aa\u00b4\5\f\7\2\u00ab\u00b4\5\n\6\2\u00ac\u00b0"+
		"\t\3\2\2\u00ad\u00af\7\24\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b2\3\2\2\2"+
		"\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0"+
		"\3\2\2\2\u00b3\u00a4\3\2\2\2\u00b3\u00a5\3\2\2\2\u00b3\u00a6\3\2\2\2\u00b3"+
		"\u00a7\3\2\2\2\u00b3\u00a8\3\2\2\2\u00b3\u00a9\3\2\2\2\u00b3\u00aa\3\2"+
		"\2\2\u00b3\u00ab\3\2\2\2\u00b3\u00ac\3\2\2\2\u00b4\u00b7\3\2\2\2\u00b5"+
		"\u00b3\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00bb\3\2\2\2\u00b7\u00b5\3\2"+
		"\2\2\u00b8\u00ba\7\24\2\2\u00b9\u00b8\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb"+
		"\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00be\3\2\2\2\u00bd\u00bb\3\2"+
		"\2\2\u00be\u00bf\7\n\2\2\u00bf\13\3\2\2\2\u00c0\u00dc\7\13\2\2\u00c1\u00c3"+
		"\t\4\2\2\u00c2\u00c1\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00d5\3\2\2\2\u00c4"+
		"\u00d6\7\34\2\2\u00c5\u00d6\5\32\16\2\u00c6\u00c8\7\35\2\2\u00c7\u00c6"+
		"\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00d6\7\30\2\2"+
		"\u00ca\u00cc\7\35\2\2\u00cb\u00ca\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00cd"+
		"\3\2\2\2\u00cd\u00d6\7\31\2\2\u00ce\u00d6\5\30\r\2\u00cf\u00d6\7\33\2"+
		"\2\u00d0\u00d6\5\b\5\2\u00d1\u00d6\5\f\7\2\u00d2\u00d6\5 \21\2\u00d3\u00d6"+
		"\5\36\20\2\u00d4\u00d6\5\"\22\2\u00d5\u00c4\3\2\2\2\u00d5\u00c5\3\2\2"+
		"\2\u00d5\u00c7\3\2\2\2\u00d5\u00cb\3\2\2\2\u00d5\u00ce\3\2\2\2\u00d5\u00cf"+
		"\3\2\2\2\u00d5\u00d0\3\2\2\2\u00d5\u00d1\3\2\2\2\u00d5\u00d2\3\2\2\2\u00d5"+
		"\u00d3\3\2\2\2\u00d5\u00d4\3\2\2\2\u00d6\u00d8\3\2\2\2\u00d7\u00d9\t\5"+
		"\2\2\u00d8\u00d7\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00db\3\2\2\2\u00da"+
		"\u00c2\3\2\2\2\u00db\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00dd\3\2"+
		"\2\2\u00dd\u00df\3\2\2\2\u00de\u00dc\3\2\2\2\u00df\u00e3\7\f\2\2\u00e0"+
		"\u00e2\7\24\2\2\u00e1\u00e0\3\2\2\2\u00e2\u00e5\3\2\2\2\u00e3\u00e1\3"+
		"\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\r\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e6\u00ee"+
		"\5\n\6\2\u00e7\u00ee\5\f\7\2\u00e8\u00ee\5\32\16\2\u00e9\u00ee\5\2\2\2"+
		"\u00ea\u00ee\5\4\3\2\u00eb\u00ee\5\6\4\2\u00ec\u00ee\5\32\16\2\u00ed\u00e6"+
		"\3\2\2\2\u00ed\u00e7\3\2\2\2\u00ed\u00e8\3\2\2\2\u00ed\u00e9\3\2\2\2\u00ed"+
		"\u00ea\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ec\3\2\2\2\u00ee\u00f1\3\2"+
		"\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\17\3\2\2\2\u00f1\u00ef"+
		"\3\2\2\2\u00f2\u0104\5\b\5\2\u00f3\u0104\7\37\2\2\u00f4\u0104\5\"\22\2"+
		"\u00f5\u0104\5\32\16\2\u00f6\u0104\7\34\2\2\u00f7\u00f9\7\35\2\2\u00f8"+
		"\u00f7\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u0104\7\30"+
		"\2\2\u00fb\u00fd\7\35\2\2\u00fc\u00fb\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd"+
		"\u00fe\3\2\2\2\u00fe\u0104\7\31\2\2\u00ff\u0104\5\f\7\2\u0100\u0104\7"+
		"\33\2\2\u0101\u0104\5 \21\2\u0102\u0104\5\36\20\2\u0103\u00f2\3\2\2\2"+
		"\u0103\u00f3\3\2\2\2\u0103\u00f4\3\2\2\2\u0103\u00f5\3\2\2\2\u0103\u00f6"+
		"\3\2\2\2\u0103\u00f8\3\2\2\2\u0103\u00fc\3\2\2\2\u0103\u00ff\3\2\2\2\u0103"+
		"\u0100\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0102\3\2\2\2\u0104\21\3\2\2"+
		"\2\u0105\u0107\7\36\2\2\u0106\u0105\3\2\2\2\u0106\u0107\3\2\2\2\u0107"+
		"\u0108\3\2\2\2\u0108\u010a\5\24\13\2\u0109\u0106\3\2\2\2\u010a\u010d\3"+
		"\2\2\2\u010b\u0109\3\2\2\2\u010b\u010c\3\2\2\2\u010c\23\3\2\2\2\u010d"+
		"\u010b\3\2\2\2\u010e\u0114\7\34\2\2\u010f\u0114\5\b\5\2\u0110\u0114\5"+
		" \21\2\u0111\u0114\5\f\7\2\u0112\u0114\7\33\2\2\u0113\u010e\3\2\2\2\u0113"+
		"\u010f\3\2\2\2\u0113\u0110\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0112\3\2"+
		"\2\2\u0114\u0115\3\2\2\2\u0115\u0116\t\2\2\2\u0116\u0117\5\20\t\2\u0117"+
		"\25\3\2\2\2\u0118\u0119\7\34\2\2\u0119\u011a\7\24\2\2\u011a\27\3\2\2\2"+
		"\u011b\u0129\7\34\2\2\u011c\u0129\7\33\2\2\u011d\u011f\7\35\2\2\u011e"+
		"\u011d\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0120\3\2\2\2\u0120\u0129\7\30"+
		"\2\2\u0121\u0123\7\35\2\2\u0122\u0121\3\2\2\2\u0122\u0123\3\2\2\2\u0123"+
		"\u0124\3\2\2\2\u0124\u0129\7\31\2\2\u0125\u0129\5\b\5\2\u0126\u0129\5"+
		" \21\2\u0127\u0129\5\f\7\2\u0128\u011b\3\2\2\2\u0128\u011c\3\2\2\2\u0128"+
		"\u011e\3\2\2\2\u0128\u0122\3\2\2\2\u0128\u0125\3\2\2\2\u0128\u0126\3\2"+
		"\2\2\u0128\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u0137\7\27\2\2\u012b"+
		"\u0138\7\34\2\2\u012c\u012e\7\35\2\2\u012d\u012c\3\2\2\2\u012d\u012e\3"+
		"\2\2\2\u012e\u012f\3\2\2\2\u012f\u0138\7\30\2\2\u0130\u0132\7\35\2\2\u0131"+
		"\u0130\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0138\7\31"+
		"\2\2\u0134\u0138\5\b\5\2\u0135\u0138\5 \21\2\u0136\u0138\5\f\7\2\u0137"+
		"\u012b\3\2\2\2\u0137\u012d\3\2\2\2\u0137\u0131\3\2\2\2\u0137\u0134\3\2"+
		"\2\2\u0137\u0135\3\2\2\2\u0137\u0136\3\2\2\2\u0138\31\3\2\2\2\u0139\u0160"+
		"\7\"\2\2\u013a\u0155\7\34\2\2\u013b\u0155\7\25\2\2\u013c\u013e\7\35\2"+
		"\2\u013d\u013c\3\2\2\2\u013d\u013e\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u0155"+
		"\7\30\2\2\u0140\u0142\7\35\2\2\u0141\u0140\3\2\2\2\u0141\u0142\3\2\2\2"+
		"\u0142\u0143\3\2\2\2\u0143\u0155\7\31\2\2\u0144\u0155\7\16\2\2\u0145\u0155"+
		"\7\17\2\2\u0146\u0155\7\20\2\2\u0147\u0155\7\21\2\2\u0148\u0155\7\22\2"+
		"\2\u0149\u0155\7\23\2\2\u014a\u0155\7\r\2\2\u014b\u0155\5 \21\2\u014c"+
		"\u0155\7\6\2\2\u014d\u0155\7\7\2\2\u014e\u0155\7 \2\2\u014f\u0155\7\35"+
		"\2\2\u0150\u0155\7\b\2\2\u0151\u0155\7\33\2\2\u0152\u0155\5\f\7\2\u0153"+
		"\u0155\5\b\5\2\u0154\u013a\3\2\2\2\u0154\u013b\3\2\2\2\u0154\u013d\3\2"+
		"\2\2\u0154\u0141\3\2\2\2\u0154\u0144\3\2\2\2\u0154\u0145\3\2\2\2\u0154"+
		"\u0146\3\2\2\2\u0154\u0147\3\2\2\2\u0154\u0148\3\2\2\2\u0154\u0149\3\2"+
		"\2\2\u0154\u014a\3\2\2\2\u0154\u014b\3\2\2\2\u0154\u014c\3\2\2\2\u0154"+
		"\u014d\3\2\2\2\u0154\u014e\3\2\2\2\u0154\u014f\3\2\2\2\u0154\u0150\3\2"+
		"\2\2\u0154\u0151\3\2\2\2\u0154\u0152\3\2\2\2\u0154\u0153\3\2\2\2\u0155"+
		"\u0156\3\2\2\2\u0156\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0161\3\2"+
		"\2\2\u0158\u015d\5\"\22\2\u0159\u015b\7\r\2\2\u015a\u015c\7\34\2\2\u015b"+
		"\u015a\3\2\2\2\u015b\u015c\3\2\2\2\u015c\u015e\3\2\2\2\u015d\u0159\3\2"+
		"\2\2\u015d\u015e\3\2\2\2\u015e\u0161\3\2\2\2\u015f\u0161\5\32\16\2\u0160"+
		"\u0154\3\2\2\2\u0160\u0158\3\2\2\2\u0160\u015f\3\2\2\2\u0160\u0161\3\2"+
		"\2\2\u0161\u0162\3\2\2\2\u0162\u0163\7\"\2\2\u0163\33\3\2\2\2\u0164\u0165"+
		"\7\26\2\2\u0165\u0166\7\34\2\2\u0166\u016c\7\6\2\2\u0167\u016a\5\20\t"+
		"\2\u0168\u0169\7 \2\2\u0169\u016b\5\20\t\2\u016a\u0168\3\2\2\2\u016a\u016b"+
		"\3\2\2\2\u016b\u016d\3\2\2\2\u016c\u0167\3\2\2\2\u016c\u016d\3\2\2\2\u016d"+
		"\u016e\3\2\2\2\u016e\u016f\7\7\2\2\u016f\u0170\7\24\2\2\u0170\35\3\2\2"+
		"\2\u0171\u0174\7#\2\2\u0172\u0175\5\f\7\2\u0173\u0175\7\34\2\2\u0174\u0172"+
		"\3\2\2\2\u0174\u0173\3\2\2\2\u0175\37\3\2\2\2\u0176\u0177\7\34\2\2\u0177"+
		"\u0178\7!\2\2\u0178\u0179\7\34\2\2\u0179!\3\2\2\2\u017a\u017b\7\b\2\2"+
		"\u017b\u017c\t\6\2\2\u017c#\3\2\2\2\66\648=CNSZ_fkq\u0080\u0086\u008e"+
		"\u0094\u0099\u00a0\u00b0\u00b3\u00b5\u00bb\u00c2\u00c7\u00cb\u00d5\u00d8"+
		"\u00dc\u00e3\u00ed\u00ef\u00f8\u00fc\u0103\u0106\u010b\u0113\u011e\u0122"+
		"\u0128\u012d\u0131\u0137\u013d\u0141\u0154\u0156\u015b\u015d\u0160\u016a"+
		"\u016c\u0174";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}