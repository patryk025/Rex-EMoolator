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
		PERC=17, PIPE=18, LBRACE=19, RBRACE=20, MISSING_CLOSE_QUOTE=21, OR=22, 
		AND=23, LESS=24, GREATER=25, EQUAL=26, NOT_EQUAL=27, GREATER_EQUAL=28, 
		LESS_EQUAL=29, IDENT=30, LINE_COMMENT=31, WS=32;
	public static final int
		RULE_script = 0, RULE_statement = 1, RULE_specialCall = 2, RULE_methodCall = 3, 
		RULE_objectName = 4, RULE_objectReference = 5, RULE_argListOpt = 6, RULE_arg = 7, 
		RULE_expr = 8, RULE_ifCondition = 9, RULE_ifSimpleCondition = 10, RULE_unaryExpr = 11, 
		RULE_primary = 12;
	private static String[] makeRuleNames() {
		return new String[] {
			"script", "statement", "specialCall", "methodCall", "objectName", "objectReference", 
			"argListOpt", "arg", "expr", "ifCondition", "ifSimpleCondition", "unaryExpr", 
			"primary"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'TRUE'", "'FALSE'", null, null, null, "'@'", "'^'", "'('", "')'", 
			"'['", "']'", "','", "';'", "'*'", "'+'", "'-'", "'%'", "'|'", "'{'", 
			"'}'", null, "'||'", "'&&'", "'<'", "'>'", "'''", "'!''", "'>''", "'<''"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TRUE", "FALSE", "NUMBER", "STRING", "CODE_BLOCK", "AT", "CARET", 
			"LPAREN", "RPAREN", "LBRACK", "RBRACK", "COMMA", "SEMI", "STAR", "PLUS", 
			"MINUS", "PERC", "PIPE", "LBRACE", "RBRACE", "MISSING_CLOSE_QUOTE", "OR", 
			"AND", "LESS", "GREATER", "EQUAL", "NOT_EQUAL", "GREATER_EQUAL", "LESS_EQUAL", 
			"IDENT", "LINE_COMMENT", "WS"
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
			setState(26);
			match(LBRACE);
			setState(32);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1073857662L) != 0)) {
				{
				{
				setState(27);
				statement();
				setState(28);
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
				setState(34);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(35);
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
			setState(40);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(37);
				specialCall();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(38);
				methodCall();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(39);
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
			setState(42);
			match(AT);
			setState(43);
			((SpecialCallContext)_localctx).name = match(IDENT);
			setState(44);
			match(LPAREN);
			setState(45);
			argListOpt();
			setState(46);
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
			setState(50);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENT:
				{
				setState(48);
				objectName();
				}
				break;
			case STAR:
				{
				setState(49);
				objectReference();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(52);
			match(CARET);
			setState(53);
			((MethodCallContext)_localctx).method = match(IDENT);
			setState(54);
			match(LPAREN);
			setState(55);
			argListOpt();
			setState(56);
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
			setState(58);
			((ObjectNameContext)_localctx).name = match(IDENT);
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PIPE) {
				{
				setState(59);
				match(PIPE);
				setState(60);
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
			setState(63);
			match(STAR);
			setState(64);
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
			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1075954750L) != 0)) {
				{
				setState(66);
				arg();
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(67);
					match(COMMA);
					setState(68);
					arg();
					}
					}
					setState(73);
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
			setState(78);
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
				setState(76);
				expr();
				}
				break;
			case MISSING_CLOSE_QUOTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(77);
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			((ExprContext)_localctx).left = unaryExpr();
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 245824L) != 0)) {
				{
				{
				setState(81);
				((ExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 245824L) != 0)) ) {
					((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(82);
				((ExprContext)_localctx).right = unaryExpr();
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
	public static class IfConditionContext extends ParserRuleContext {
		public IfSimpleConditionContext left;
		public Token op;
		public IfConditionContext right;
		public IfSimpleConditionContext ifSimpleCondition() {
			return getRuleContext(IfSimpleConditionContext.class,0);
		}
		public List<IfConditionContext> ifCondition() {
			return getRuleContexts(IfConditionContext.class);
		}
		public IfConditionContext ifCondition(int i) {
			return getRuleContext(IfConditionContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(AidemMediaParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(AidemMediaParser.OR, i);
		}
		public List<TerminalNode> AND() { return getTokens(AidemMediaParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(AidemMediaParser.AND, i);
		}
		public IfConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterIfCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitIfCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitIfCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfConditionContext ifCondition() throws RecognitionException {
		IfConditionContext _localctx = new IfConditionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_ifCondition);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			((IfConditionContext)_localctx).left = ifSimpleCondition();
			setState(93);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(89);
					((IfConditionContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==OR || _la==AND) ) {
						((IfConditionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(90);
					((IfConditionContext)_localctx).right = ifCondition();
					}
					} 
				}
				setState(95);
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

	@SuppressWarnings("CheckReturnValue")
	public static class IfSimpleConditionContext extends ParserRuleContext {
		public ExprContext left;
		public Token op;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode LESS() { return getToken(AidemMediaParser.LESS, 0); }
		public TerminalNode GREATER() { return getToken(AidemMediaParser.GREATER, 0); }
		public TerminalNode EQUAL() { return getToken(AidemMediaParser.EQUAL, 0); }
		public TerminalNode NOT_EQUAL() { return getToken(AidemMediaParser.NOT_EQUAL, 0); }
		public TerminalNode GREATER_EQUAL() { return getToken(AidemMediaParser.GREATER_EQUAL, 0); }
		public TerminalNode LESS_EQUAL() { return getToken(AidemMediaParser.LESS_EQUAL, 0); }
		public IfSimpleConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifSimpleCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).enterIfSimpleCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AidemMediaParserListener ) ((AidemMediaParserListener)listener).exitIfSimpleCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AidemMediaParserVisitor ) return ((AidemMediaParserVisitor<? extends T>)visitor).visitIfSimpleCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfSimpleConditionContext ifSimpleCondition() throws RecognitionException {
		IfSimpleConditionContext _localctx = new IfSimpleConditionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_ifSimpleCondition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			((IfSimpleConditionContext)_localctx).left = expr();
			setState(97);
			((IfSimpleConditionContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1056964608L) != 0)) ) {
				((IfSimpleConditionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(98);
			((IfSimpleConditionContext)_localctx).right = expr();
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
		enterRule(_localctx, 22, RULE_unaryExpr);
		int _la;
		try {
			setState(103);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(100);
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
				setState(101);
				unaryExpr();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(102);
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
		enterRule(_localctx, 24, RULE_primary);
		try {
			setState(116);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				match(TRUE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(107);
				match(FALSE);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(108);
				match(STRING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(109);
				match(CODE_BLOCK);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(110);
				methodCall();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(111);
				objectName();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(112);
				match(LBRACK);
				setState(113);
				expr();
				setState(114);
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
		"\u0004\u0001 w\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002"+
		"\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005"+
		"\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007"+
		"\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007"+
		"\f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u001f\b"+
		"\u0000\n\u0000\f\u0000\"\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0003\u0001)\b\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0003"+
		"\u00033\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004>\b"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0005\u0006F\b\u0006\n\u0006\f\u0006I\t\u0006\u0003\u0006K\b\u0006"+
		"\u0001\u0007\u0001\u0007\u0003\u0007O\b\u0007\u0001\b\u0001\b\u0001\b"+
		"\u0005\bT\b\b\n\b\f\bW\t\b\u0001\t\u0001\t\u0001\t\u0005\t\\\b\t\n\t\f"+
		"\t_\t\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000bh\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\fu\b\f\u0001\f\u0000"+
		"\u0000\r\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u0000\u0006\u0002\u0001\r\r\u0014\u0014\u0001\u0001\u0014\u0014\u0002"+
		"\u0000\u0006\u0006\u000e\u0011\u0001\u0000\u0016\u0017\u0001\u0000\u0018"+
		"\u001d\u0001\u0000\u000e\u0010{\u0000\u001a\u0001\u0000\u0000\u0000\u0002"+
		"(\u0001\u0000\u0000\u0000\u0004*\u0001\u0000\u0000\u0000\u00062\u0001"+
		"\u0000\u0000\u0000\b:\u0001\u0000\u0000\u0000\n?\u0001\u0000\u0000\u0000"+
		"\fJ\u0001\u0000\u0000\u0000\u000eN\u0001\u0000\u0000\u0000\u0010P\u0001"+
		"\u0000\u0000\u0000\u0012X\u0001\u0000\u0000\u0000\u0014`\u0001\u0000\u0000"+
		"\u0000\u0016g\u0001\u0000\u0000\u0000\u0018t\u0001\u0000\u0000\u0000\u001a"+
		" \u0005\u0013\u0000\u0000\u001b\u001c\u0003\u0002\u0001\u0000\u001c\u001d"+
		"\u0007\u0000\u0000\u0000\u001d\u001f\u0001\u0000\u0000\u0000\u001e\u001b"+
		"\u0001\u0000\u0000\u0000\u001f\"\u0001\u0000\u0000\u0000 \u001e\u0001"+
		"\u0000\u0000\u0000 !\u0001\u0000\u0000\u0000!#\u0001\u0000\u0000\u0000"+
		"\" \u0001\u0000\u0000\u0000#$\u0007\u0001\u0000\u0000$\u0001\u0001\u0000"+
		"\u0000\u0000%)\u0003\u0004\u0002\u0000&)\u0003\u0006\u0003\u0000\')\u0003"+
		"\u0010\b\u0000(%\u0001\u0000\u0000\u0000(&\u0001\u0000\u0000\u0000(\'"+
		"\u0001\u0000\u0000\u0000)\u0003\u0001\u0000\u0000\u0000*+\u0005\u0006"+
		"\u0000\u0000+,\u0005\u001e\u0000\u0000,-\u0005\b\u0000\u0000-.\u0003\f"+
		"\u0006\u0000./\u0005\t\u0000\u0000/\u0005\u0001\u0000\u0000\u000003\u0003"+
		"\b\u0004\u000013\u0003\n\u0005\u000020\u0001\u0000\u0000\u000021\u0001"+
		"\u0000\u0000\u000034\u0001\u0000\u0000\u000045\u0005\u0007\u0000\u0000"+
		"56\u0005\u001e\u0000\u000067\u0005\b\u0000\u000078\u0003\f\u0006\u0000"+
		"89\u0005\t\u0000\u00009\u0007\u0001\u0000\u0000\u0000:=\u0005\u001e\u0000"+
		"\u0000;<\u0005\u0012\u0000\u0000<>\u0005\u001e\u0000\u0000=;\u0001\u0000"+
		"\u0000\u0000=>\u0001\u0000\u0000\u0000>\t\u0001\u0000\u0000\u0000?@\u0005"+
		"\u000e\u0000\u0000@A\u0003\u0018\f\u0000A\u000b\u0001\u0000\u0000\u0000"+
		"BG\u0003\u000e\u0007\u0000CD\u0005\f\u0000\u0000DF\u0003\u000e\u0007\u0000"+
		"EC\u0001\u0000\u0000\u0000FI\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000"+
		"\u0000GH\u0001\u0000\u0000\u0000HK\u0001\u0000\u0000\u0000IG\u0001\u0000"+
		"\u0000\u0000JB\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000K\r\u0001"+
		"\u0000\u0000\u0000LO\u0003\u0010\b\u0000MO\u0005\u0015\u0000\u0000NL\u0001"+
		"\u0000\u0000\u0000NM\u0001\u0000\u0000\u0000O\u000f\u0001\u0000\u0000"+
		"\u0000PU\u0003\u0016\u000b\u0000QR\u0007\u0002\u0000\u0000RT\u0003\u0016"+
		"\u000b\u0000SQ\u0001\u0000\u0000\u0000TW\u0001\u0000\u0000\u0000US\u0001"+
		"\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000V\u0011\u0001\u0000\u0000"+
		"\u0000WU\u0001\u0000\u0000\u0000X]\u0003\u0014\n\u0000YZ\u0007\u0003\u0000"+
		"\u0000Z\\\u0003\u0012\t\u0000[Y\u0001\u0000\u0000\u0000\\_\u0001\u0000"+
		"\u0000\u0000][\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^\u0013"+
		"\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000`a\u0003\u0010\b\u0000"+
		"ab\u0007\u0004\u0000\u0000bc\u0003\u0010\b\u0000c\u0015\u0001\u0000\u0000"+
		"\u0000de\u0007\u0005\u0000\u0000eh\u0003\u0016\u000b\u0000fh\u0003\u0018"+
		"\f\u0000gd\u0001\u0000\u0000\u0000gf\u0001\u0000\u0000\u0000h\u0017\u0001"+
		"\u0000\u0000\u0000iu\u0005\u0003\u0000\u0000ju\u0005\u0001\u0000\u0000"+
		"ku\u0005\u0002\u0000\u0000lu\u0005\u0004\u0000\u0000mu\u0005\u0005\u0000"+
		"\u0000nu\u0003\u0006\u0003\u0000ou\u0003\b\u0004\u0000pq\u0005\n\u0000"+
		"\u0000qr\u0003\u0010\b\u0000rs\u0005\u000b\u0000\u0000su\u0001\u0000\u0000"+
		"\u0000ti\u0001\u0000\u0000\u0000tj\u0001\u0000\u0000\u0000tk\u0001\u0000"+
		"\u0000\u0000tl\u0001\u0000\u0000\u0000tm\u0001\u0000\u0000\u0000tn\u0001"+
		"\u0000\u0000\u0000to\u0001\u0000\u0000\u0000tp\u0001\u0000\u0000\u0000"+
		"u\u0019\u0001\u0000\u0000\u0000\u000b (2=GJNU]gt";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}