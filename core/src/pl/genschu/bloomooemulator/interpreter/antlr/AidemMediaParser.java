// Generated from AidemMediaParser.g4 by ANTLR 4.13.2
package pl.genschu.bloomooemulator.interpreter.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class AidemMediaParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TRUE=1, FALSE=2, NUMBER=3, STRING=4, CODE_BLOCK=5, AT=6, CARET=7, LPAREN=8, 
		RPAREN=9, LBRACK=10, RBRACK=11, COMMA=12, SEMI=13, STAR=14, PLUS=15, MINUS=16, 
		PERC=17, PIPE=18, LBRACE=19, RBRACE=20, MISSING_CLOSE_QUOTE=21, IDENT=22, 
		LINE_COMMENT=23, WS=24;
	public static final int
		RULE_script = 0, RULE_statement = 1, RULE_specialCall = 2, RULE_methodCall = 3, 
		RULE_objectName = 4, RULE_objectReference = 5, RULE_argListOpt = 6, RULE_arg = 7, 
		RULE_expr = 8, RULE_arithmeticExpr = 9, RULE_unaryExpr = 10, RULE_primary = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"script", "statement", "specialCall", "methodCall", "objectName", "objectReference", 
			"argListOpt", "arg", "expr", "arithmeticExpr", "unaryExpr", "primary"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'TRUE'", "'FALSE'", null, null, null, "'@'", "'^'", "'('", "')'", 
			"'['", "']'", "','", "';'", "'*'", "'+'", "'-'", "'%'", "'|'", "'{'", 
			"'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TRUE", "FALSE", "NUMBER", "STRING", "CODE_BLOCK", "AT", "CARET", 
			"LPAREN", "RPAREN", "LBRACK", "RBRACK", "COMMA", "SEMI", "STAR", "PLUS", 
			"MINUS", "PERC", "PIPE", "LBRACE", "RBRACE", "MISSING_CLOSE_QUOTE", "IDENT", 
			"LINE_COMMENT", "WS"
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
	public String getGrammarFileName() { return "AidemMediaParser.g4"; }

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

	@SuppressWarnings("CheckReturnValue")
	public static class ScriptContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(AidemMediaParser.LBRACE, 0); }
		public List<TerminalNode> RBRACE() { return getTokens(AidemMediaParser.RBRACE); }
		public TerminalNode RBRACE(int i) {
			return getToken(AidemMediaParser.RBRACE, i);
		}
		public List<TerminalNode> EOF() { return getTokens(AidemMediaParser.EOF); }
		public TerminalNode EOF(int i) {
			return getToken(AidemMediaParser.EOF, i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(AidemMediaParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(AidemMediaParser.SEMI, i);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_script);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(LBRACE);
			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4310142L) != 0)) {
				{
				{
				setState(25);
				statement();
				setState(26);
				_la = _input.LA(1);
				if ( !(((((_la - -1)) & ~0x3f) == 0 && ((1L << (_la - -1)) & 2113537L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(33);
			_la = _input.LA(1);
			if ( !(_la==EOF || _la==RBRACE) ) {
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

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public SpecialCallContext specialCall() {
			return getRuleContext(SpecialCallContext.class,0);
		}
		public MethodCallContext methodCall() {
			return getRuleContext(MethodCallContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			setState(38);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(35);
				specialCall();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(36);
				methodCall();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(37);
				expr();
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

	@SuppressWarnings("CheckReturnValue")
	public static class SpecialCallContext extends ParserRuleContext {
		public Token name;
		public TerminalNode AT() { return getToken(AidemMediaParser.AT, 0); }
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public ArgListOptContext argListOpt() {
			return getRuleContext(ArgListOptContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public TerminalNode IDENT() { return getToken(AidemMediaParser.IDENT, 0); }
		public SpecialCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specialCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterSpecialCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitSpecialCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitSpecialCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpecialCallContext specialCall() throws RecognitionException {
		SpecialCallContext _localctx = new SpecialCallContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_specialCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(AT);
			setState(41);
			((SpecialCallContext)_localctx).name = match(IDENT);
			setState(42);
			match(LPAREN);
			setState(43);
			argListOpt();
			setState(44);
			match(RPAREN);
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

	@SuppressWarnings("CheckReturnValue")
	public static class MethodCallContext extends ParserRuleContext {
		public Token method;
		public TerminalNode CARET() { return getToken(AidemMediaParser.CARET, 0); }
		public TerminalNode LPAREN() { return getToken(AidemMediaParser.LPAREN, 0); }
		public ArgListOptContext argListOpt() {
			return getRuleContext(ArgListOptContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(AidemMediaParser.RPAREN, 0); }
		public TerminalNode IDENT() { return getToken(AidemMediaParser.IDENT, 0); }
		public ObjectNameContext objectName() {
			return getRuleContext(ObjectNameContext.class,0);
		}
		public ObjectReferenceContext objectReference() {
			return getRuleContext(ObjectReferenceContext.class,0);
		}
		public MethodCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterMethodCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitMethodCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitMethodCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodCallContext methodCall() throws RecognitionException {
		MethodCallContext _localctx = new MethodCallContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_methodCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENT:
				{
				setState(46);
				objectName();
				}
				break;
			case STAR:
				{
				setState(47);
				objectReference();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(50);
			match(CARET);
			setState(51);
			((MethodCallContext)_localctx).method = match(IDENT);
			setState(52);
			match(LPAREN);
			setState(53);
			argListOpt();
			setState(54);
			match(RPAREN);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectNameContext extends ParserRuleContext {
		public Token name;
		public Token field;
		public List<TerminalNode> IDENT() { return getTokens(AidemMediaParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(AidemMediaParser.IDENT, i);
		}
		public TerminalNode PIPE() { return getToken(AidemMediaParser.PIPE, 0); }
		public ObjectNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterObjectName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitObjectName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitObjectName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectNameContext objectName() throws RecognitionException {
		ObjectNameContext _localctx = new ObjectNameContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_objectName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			((ObjectNameContext)_localctx).name = match(IDENT);
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PIPE) {
				{
				setState(57);
				match(PIPE);
				setState(58);
				((ObjectNameContext)_localctx).field = match(IDENT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectReferenceContext extends ParserRuleContext {
		public TerminalNode STAR() { return getToken(AidemMediaParser.STAR, 0); }
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public ObjectReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterObjectReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitObjectReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitObjectReference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectReferenceContext objectReference() throws RecognitionException {
		ObjectReferenceContext _localctx = new ObjectReferenceContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_objectReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			match(STAR);
			setState(62);
			primary();
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

	@SuppressWarnings("CheckReturnValue")
	public static class ArgListOptContext extends ParserRuleContext {
		public List<ArgContext> arg() {
			return getRuleContexts(ArgContext.class);
		}
		public ArgContext arg(int i) {
			return getRuleContext(ArgContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(AidemMediaParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(AidemMediaParser.COMMA, i);
		}
		public ArgListOptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argListOpt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterArgListOpt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitArgListOpt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitArgListOpt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgListOptContext argListOpt() throws RecognitionException {
		ArgListOptContext _localctx = new ArgListOptContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_argListOpt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6407230L) != 0)) {
				{
				setState(64);
				arg();
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(65);
					match(COMMA);
					setState(66);
					arg();
					}
					}
					setState(71);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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

	@SuppressWarnings("CheckReturnValue")
	public static class ArgContext extends ParserRuleContext {
		public Token missing_quote;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode MISSING_CLOSE_QUOTE() { return getToken(AidemMediaParser.MISSING_CLOSE_QUOTE, 0); }
		public ArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgContext arg() throws RecognitionException {
		ArgContext _localctx = new ArgContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_arg);
		try {
			setState(76);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
			case NUMBER:
			case STRING:
			case CODE_BLOCK:
			case LBRACK:
			case STAR:
			case PLUS:
			case MINUS:
			case IDENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				expr();
				}
				break;
			case MISSING_CLOSE_QUOTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(75);
				((ArgContext)_localctx).missing_quote = match(MISSING_CLOSE_QUOTE);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public ArithmeticExprContext arithmeticExpr() {
			return getRuleContext(ArithmeticExprContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			arithmeticExpr();
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

	@SuppressWarnings("CheckReturnValue")
	public static class ArithmeticExprContext extends ParserRuleContext {
		public UnaryExprContext left;
		public Token op;
		public UnaryExprContext right;
		public List<UnaryExprContext> unaryExpr() {
			return getRuleContexts(UnaryExprContext.class);
		}
		public UnaryExprContext unaryExpr(int i) {
			return getRuleContext(UnaryExprContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(AidemMediaParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(AidemMediaParser.PLUS, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(AidemMediaParser.MINUS); }
		public TerminalNode MINUS(int i) {
			return getToken(AidemMediaParser.MINUS, i);
		}
		public List<TerminalNode> STAR() { return getTokens(AidemMediaParser.STAR); }
		public TerminalNode STAR(int i) {
			return getToken(AidemMediaParser.STAR, i);
		}
		public List<TerminalNode> AT() { return getTokens(AidemMediaParser.AT); }
		public TerminalNode AT(int i) {
			return getToken(AidemMediaParser.AT, i);
		}
		public List<TerminalNode> PERC() { return getTokens(AidemMediaParser.PERC); }
		public TerminalNode PERC(int i) {
			return getToken(AidemMediaParser.PERC, i);
		}
		public ArithmeticExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arithmeticExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterArithmeticExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitArithmeticExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitArithmeticExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArithmeticExprContext arithmeticExpr() throws RecognitionException {
		ArithmeticExprContext _localctx = new ArithmeticExprContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_arithmeticExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			((ArithmeticExprContext)_localctx).left = unaryExpr();
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 245824L) != 0)) {
				{
				{
				setState(81);
				((ArithmeticExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 245824L) != 0)) ) {
					((ArithmeticExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(82);
				((ArithmeticExprContext)_localctx).right = unaryExpr();
				}
				}
				setState(87);
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

	@SuppressWarnings("CheckReturnValue")
	public static class UnaryExprContext extends ParserRuleContext {
		public Token op;
		public UnaryExprContext unaryExpr() {
			return getRuleContext(UnaryExprContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(AidemMediaParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(AidemMediaParser.MINUS, 0); }
		public TerminalNode STAR() { return getToken(AidemMediaParser.STAR, 0); }
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public UnaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterUnaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitUnaryExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitUnaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExprContext unaryExpr() throws RecognitionException {
		UnaryExprContext _localctx = new UnaryExprContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_unaryExpr);
		int _la;
		try {
			setState(91);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(88);
				((UnaryExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 114688L) != 0)) ) {
					((UnaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(89);
				unaryExpr();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(90);
				primary();
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

	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(AidemMediaParser.NUMBER, 0); }
		public TerminalNode TRUE() { return getToken(AidemMediaParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(AidemMediaParser.FALSE, 0); }
		public TerminalNode STRING() { return getToken(AidemMediaParser.STRING, 0); }
		public TerminalNode CODE_BLOCK() { return getToken(AidemMediaParser.CODE_BLOCK, 0); }
		public MethodCallContext methodCall() {
			return getRuleContext(MethodCallContext.class,0);
		}
		public ObjectNameContext objectName() {
			return getRuleContext(ObjectNameContext.class,0);
		}
		public TerminalNode LBRACK() { return getToken(AidemMediaParser.LBRACK, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RBRACK() { return getToken(AidemMediaParser.RBRACK, 0); }
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_primary);
		try {
			setState(104);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(93);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(94);
				match(TRUE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(95);
				match(FALSE);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(96);
				match(STRING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(97);
				match(CODE_BLOCK);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(98);
				methodCall();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(99);
				objectName();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(100);
				match(LBRACK);
				setState(101);
				expr();
				setState(102);
				match(RBRACK);
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

	public static final String _serializedATN =
		"\u0004\u0001\u0018k\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u001d\b\u0000\n"+
		"\u0000\f\u0000 \t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001\'\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0003\u0003"+
		"1\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004<\b\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0005\u0006D\b\u0006\n\u0006\f\u0006G\t\u0006\u0003\u0006I\b\u0006\u0001"+
		"\u0007\u0001\u0007\u0003\u0007M\b\u0007\u0001\b\u0001\b\u0001\t\u0001"+
		"\t\u0001\t\u0005\tT\b\t\n\t\f\tW\t\t\u0001\n\u0001\n\u0001\n\u0003\n\\"+
		"\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003"+
		"\u000bi\b\u000b\u0001\u000b\u0000\u0000\f\u0000\u0002\u0004\u0006\b\n"+
		"\f\u000e\u0010\u0012\u0014\u0016\u0000\u0004\u0002\u0001\r\r\u0014\u0014"+
		"\u0001\u0001\u0014\u0014\u0002\u0000\u0006\u0006\u000e\u0011\u0001\u0000"+
		"\u000e\u0010o\u0000\u0018\u0001\u0000\u0000\u0000\u0002&\u0001\u0000\u0000"+
		"\u0000\u0004(\u0001\u0000\u0000\u0000\u00060\u0001\u0000\u0000\u0000\b"+
		"8\u0001\u0000\u0000\u0000\n=\u0001\u0000\u0000\u0000\fH\u0001\u0000\u0000"+
		"\u0000\u000eL\u0001\u0000\u0000\u0000\u0010N\u0001\u0000\u0000\u0000\u0012"+
		"P\u0001\u0000\u0000\u0000\u0014[\u0001\u0000\u0000\u0000\u0016h\u0001"+
		"\u0000\u0000\u0000\u0018\u001e\u0005\u0013\u0000\u0000\u0019\u001a\u0003"+
		"\u0002\u0001\u0000\u001a\u001b\u0007\u0000\u0000\u0000\u001b\u001d\u0001"+
		"\u0000\u0000\u0000\u001c\u0019\u0001\u0000\u0000\u0000\u001d \u0001\u0000"+
		"\u0000\u0000\u001e\u001c\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000"+
		"\u0000\u0000\u001f!\u0001\u0000\u0000\u0000 \u001e\u0001\u0000\u0000\u0000"+
		"!\"\u0007\u0001\u0000\u0000\"\u0001\u0001\u0000\u0000\u0000#\'\u0003\u0004"+
		"\u0002\u0000$\'\u0003\u0006\u0003\u0000%\'\u0003\u0010\b\u0000&#\u0001"+
		"\u0000\u0000\u0000&$\u0001\u0000\u0000\u0000&%\u0001\u0000\u0000\u0000"+
		"\'\u0003\u0001\u0000\u0000\u0000()\u0005\u0006\u0000\u0000)*\u0005\u0016"+
		"\u0000\u0000*+\u0005\b\u0000\u0000+,\u0003\f\u0006\u0000,-\u0005\t\u0000"+
		"\u0000-\u0005\u0001\u0000\u0000\u0000.1\u0003\b\u0004\u0000/1\u0003\n"+
		"\u0005\u00000.\u0001\u0000\u0000\u00000/\u0001\u0000\u0000\u000012\u0001"+
		"\u0000\u0000\u000023\u0005\u0007\u0000\u000034\u0005\u0016\u0000\u0000"+
		"45\u0005\b\u0000\u000056\u0003\f\u0006\u000067\u0005\t\u0000\u00007\u0007"+
		"\u0001\u0000\u0000\u00008;\u0005\u0016\u0000\u00009:\u0005\u0012\u0000"+
		"\u0000:<\u0005\u0016\u0000\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000"+
		"\u0000\u0000<\t\u0001\u0000\u0000\u0000=>\u0005\u000e\u0000\u0000>?\u0003"+
		"\u0016\u000b\u0000?\u000b\u0001\u0000\u0000\u0000@E\u0003\u000e\u0007"+
		"\u0000AB\u0005\f\u0000\u0000BD\u0003\u000e\u0007\u0000CA\u0001\u0000\u0000"+
		"\u0000DG\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000EF\u0001\u0000"+
		"\u0000\u0000FI\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000H@\u0001"+
		"\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000I\r\u0001\u0000\u0000\u0000"+
		"JM\u0003\u0010\b\u0000KM\u0005\u0015\u0000\u0000LJ\u0001\u0000\u0000\u0000"+
		"LK\u0001\u0000\u0000\u0000M\u000f\u0001\u0000\u0000\u0000NO\u0003\u0012"+
		"\t\u0000O\u0011\u0001\u0000\u0000\u0000PU\u0003\u0014\n\u0000QR\u0007"+
		"\u0002\u0000\u0000RT\u0003\u0014\n\u0000SQ\u0001\u0000\u0000\u0000TW\u0001"+
		"\u0000\u0000\u0000US\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000"+
		"V\u0013\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000\u0000XY\u0007\u0003"+
		"\u0000\u0000Y\\\u0003\u0014\n\u0000Z\\\u0003\u0016\u000b\u0000[X\u0001"+
		"\u0000\u0000\u0000[Z\u0001\u0000\u0000\u0000\\\u0015\u0001\u0000\u0000"+
		"\u0000]i\u0005\u0003\u0000\u0000^i\u0005\u0001\u0000\u0000_i\u0005\u0002"+
		"\u0000\u0000`i\u0005\u0004\u0000\u0000ai\u0005\u0005\u0000\u0000bi\u0003"+
		"\u0006\u0003\u0000ci\u0003\b\u0004\u0000de\u0005\n\u0000\u0000ef\u0003"+
		"\u0010\b\u0000fg\u0005\u000b\u0000\u0000gi\u0001\u0000\u0000\u0000h]\u0001"+
		"\u0000\u0000\u0000h^\u0001\u0000\u0000\u0000h_\u0001\u0000\u0000\u0000"+
		"h`\u0001\u0000\u0000\u0000ha\u0001\u0000\u0000\u0000hb\u0001\u0000\u0000"+
		"\u0000hc\u0001\u0000\u0000\u0000hd\u0001\u0000\u0000\u0000i\u0017\u0001"+
		"\u0000\u0000\u0000\n\u001e&0;EHLU[h";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}