// Generated from C:/Users/massi/Documents/IntelliJProject/Compilatori-e-Interpreti/grammar\SVM.g4 by ANTLR 4.7
package parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SVMParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
            PUSH = 1, POP = 2, ADD = 3, SUB = 4, MULT = 5, DIV = 6, NOT = 7, AND = 8, OR = 9, STOREW = 10,
            LOADW = 11, BRANCH = 12, BRANCHEQ = 13, BRANCHLESS = 14, BRANCHLESSEQ = 15, BRANCHGREATHER = 16,
            BRANCHGREATHEREQ = 17, JS = 18, LOADRA = 19, STORERA = 20, LOADRV = 21, STORERV = 22,
            LOADFP = 23, STOREFP = 24, COPYFP = 25, LOADHP = 26, STOREHP = 27, PRINT = 28, HALT = 29,
            NEW = 30, LOADC = 31, COPY = 32, HEAPOFFSET = 33, COL = 34, LABEL = 35, NUMBER = 36,
            WHITESP = 37, ERR = 38;
	public static final int
		RULE_assembly = 0;
	public static final String[] ruleNames = {
		"assembly"
	};

	private static final String[] _LITERAL_NAMES = {
            null, "'push'", "'pop'", "'add'", "'sub'", "'mult'", "'div'", "'not'",
            "'and'", "'or'", "'sw'", "'lw'", "'b'", "'beq'", "'blt'", "'ble'", "'bgt'",
            "'bge'", "'js'", "'lra'", "'sra'", "'lrv'", "'srv'", "'lfp'", "'sfp'",
            "'cfp'", "'lhp'", "'shp'", "'print'", "'halt'", "'new'", "'loadc'", "'copy'",
            "'heapoffset'", "':'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
            null, "PUSH", "POP", "ADD", "SUB", "MULT", "DIV", "NOT", "AND", "OR",
            "STOREW", "LOADW", "BRANCH", "BRANCHEQ", "BRANCHLESS", "BRANCHLESSEQ",
            "BRANCHGREATHER", "BRANCHGREATHEREQ", "JS", "LOADRA", "STORERA", "LOADRV",
            "STORERV", "LOADFP", "STOREFP", "COPYFP", "LOADHP", "STOREHP", "PRINT",
            "HALT", "NEW", "LOADC", "COPY", "HEAPOFFSET", "COL", "LABEL", "NUMBER",
            "WHITESP", "ERR"
	};
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
	public String getGrammarFileName() { return "SVM.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }



	    private ArrayList<Integer> code = new ArrayList<Integer>();
	    private HashMap<String,Integer> labelAdd = new HashMap<String,Integer>();
	    private HashMap<Integer,String> labelRef = new HashMap<Integer,String>();

	    public int[] getBytecode() {
	        int[] bytecode = new int[this.code.size()];
	        for (int ii = 0; ii < this.code.size(); ii++) {
	            bytecode[ii] = this.code.get(ii);
	        }
	        return bytecode;
	    }

	public SVMParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class AssemblyContext extends ParserRuleContext {
		public Token n;
		public Token l;
		public List<TerminalNode> PUSH() { return getTokens(SVMParser.PUSH); }
		public TerminalNode PUSH(int i) {
			return getToken(SVMParser.PUSH, i);
		}
		public List<TerminalNode> POP() { return getTokens(SVMParser.POP); }
		public TerminalNode POP(int i) {
			return getToken(SVMParser.POP, i);
		}
		public List<TerminalNode> ADD() { return getTokens(SVMParser.ADD); }
		public TerminalNode ADD(int i) {
			return getToken(SVMParser.ADD, i);
		}
		public List<TerminalNode> SUB() { return getTokens(SVMParser.SUB); }
		public TerminalNode SUB(int i) {
			return getToken(SVMParser.SUB, i);
		}

        public List<TerminalNode> MULT() {
            return getTokens(SVMParser.MULT);
        }

        public TerminalNode MULT(int i) {
            return getToken(SVMParser.MULT, i);
		}
		public List<TerminalNode> DIV() { return getTokens(SVMParser.DIV); }
		public TerminalNode DIV(int i) {
			return getToken(SVMParser.DIV, i);
		}

        public List<TerminalNode> AND() {
            return getTokens(SVMParser.AND);
        }

        public TerminalNode AND(int i) {
            return getToken(SVMParser.AND, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(SVMParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(SVMParser.OR, i);
        }
		public List<TerminalNode> STOREW() { return getTokens(SVMParser.STOREW); }
		public TerminalNode STOREW(int i) {
			return getToken(SVMParser.STOREW, i);
		}
		public List<TerminalNode> LOADW() { return getTokens(SVMParser.LOADW); }
		public TerminalNode LOADW(int i) {
			return getToken(SVMParser.LOADW, i);
		}
		public List<TerminalNode> COL() { return getTokens(SVMParser.COL); }
		public TerminalNode COL(int i) {
			return getToken(SVMParser.COL, i);
		}
		public List<TerminalNode> BRANCH() { return getTokens(SVMParser.BRANCH); }
		public TerminalNode BRANCH(int i) {
			return getToken(SVMParser.BRANCH, i);
		}
		public List<TerminalNode> BRANCHEQ() { return getTokens(SVMParser.BRANCHEQ); }
		public TerminalNode BRANCHEQ(int i) {
			return getToken(SVMParser.BRANCHEQ, i);
		}
		public List<TerminalNode> BRANCHLESS() { return getTokens(SVMParser.BRANCHLESS); }
		public TerminalNode BRANCHLESS(int i) {
			return getToken(SVMParser.BRANCHLESS, i);
		}
		public List<TerminalNode> BRANCHLESSEQ() { return getTokens(SVMParser.BRANCHLESSEQ); }
		public TerminalNode BRANCHLESSEQ(int i) {
			return getToken(SVMParser.BRANCHLESSEQ, i);
		}
		public List<TerminalNode> BRANCHGREATHER() { return getTokens(SVMParser.BRANCHGREATHER); }
		public TerminalNode BRANCHGREATHER(int i) {
			return getToken(SVMParser.BRANCHGREATHER, i);
		}
		public List<TerminalNode> BRANCHGREATHEREQ() { return getTokens(SVMParser.BRANCHGREATHEREQ); }
		public TerminalNode BRANCHGREATHEREQ(int i) {
			return getToken(SVMParser.BRANCHGREATHEREQ, i);
		}
		public List<TerminalNode> JS() { return getTokens(SVMParser.JS); }
		public TerminalNode JS(int i) {
			return getToken(SVMParser.JS, i);
		}
		public List<TerminalNode> LOADRA() { return getTokens(SVMParser.LOADRA); }
		public TerminalNode LOADRA(int i) {
			return getToken(SVMParser.LOADRA, i);
		}
		public List<TerminalNode> STORERA() { return getTokens(SVMParser.STORERA); }
		public TerminalNode STORERA(int i) {
			return getToken(SVMParser.STORERA, i);
		}
		public List<TerminalNode> LOADRV() { return getTokens(SVMParser.LOADRV); }
		public TerminalNode LOADRV(int i) {
			return getToken(SVMParser.LOADRV, i);
		}
		public List<TerminalNode> STORERV() { return getTokens(SVMParser.STORERV); }
		public TerminalNode STORERV(int i) {
			return getToken(SVMParser.STORERV, i);
		}
		public List<TerminalNode> LOADFP() { return getTokens(SVMParser.LOADFP); }
		public TerminalNode LOADFP(int i) {
			return getToken(SVMParser.LOADFP, i);
		}
		public List<TerminalNode> STOREFP() { return getTokens(SVMParser.STOREFP); }
		public TerminalNode STOREFP(int i) {
			return getToken(SVMParser.STOREFP, i);
		}
		public List<TerminalNode> COPYFP() { return getTokens(SVMParser.COPYFP); }
		public TerminalNode COPYFP(int i) {
			return getToken(SVMParser.COPYFP, i);
		}
		public List<TerminalNode> LOADHP() { return getTokens(SVMParser.LOADHP); }
		public TerminalNode LOADHP(int i) {
			return getToken(SVMParser.LOADHP, i);
		}
		public List<TerminalNode> STOREHP() { return getTokens(SVMParser.STOREHP); }
		public TerminalNode STOREHP(int i) {
			return getToken(SVMParser.STOREHP, i);
		}
		public List<TerminalNode> PRINT() { return getTokens(SVMParser.PRINT); }
		public TerminalNode PRINT(int i) {
			return getToken(SVMParser.PRINT, i);
		}
		public List<TerminalNode> NEW() { return getTokens(SVMParser.NEW); }
		public TerminalNode NEW(int i) {
			return getToken(SVMParser.NEW, i);
		}

        public List<TerminalNode> LOADC() {
            return getTokens(SVMParser.LOADC);
        }

        public TerminalNode LOADC(int i) {
            return getToken(SVMParser.LOADC, i);
        }
		public List<TerminalNode> HALT() { return getTokens(SVMParser.HALT); }
		public TerminalNode HALT(int i) {
			return getToken(SVMParser.HALT, i);
		}
		public List<TerminalNode> COPY() { return getTokens(SVMParser.COPY); }
		public TerminalNode COPY(int i) {
			return getToken(SVMParser.COPY, i);
		}
		public List<TerminalNode> HEAPOFFSET() { return getTokens(SVMParser.HEAPOFFSET); }
		public TerminalNode HEAPOFFSET(int i) {
			return getToken(SVMParser.HEAPOFFSET, i);
		}
		public List<TerminalNode> NUMBER() { return getTokens(SVMParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(SVMParser.NUMBER, i);
		}
		public List<TerminalNode> LABEL() { return getTokens(SVMParser.LABEL); }
		public TerminalNode LABEL(int i) {
			return getToken(SVMParser.LABEL, i);
		}
		public AssemblyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assembly; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SVMListener ) ((SVMListener)listener).enterAssembly(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SVMListener ) ((SVMListener)listener).exitAssembly(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SVMVisitor ) return ((SVMVisitor<? extends T>)visitor).visitAssembly(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssemblyContext assembly() throws RecognitionException {
		AssemblyContext _localctx = new AssemblyContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_assembly);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PUSH) | (1L << POP) | (1L << ADD) | (1L << SUB) | (1L << MULT) | (1L << DIV) | (1L << AND) | (1L << OR) | (1L << STOREW) | (1L << LOADW) | (1L << BRANCH) | (1L << BRANCHEQ) | (1L << BRANCHLESS) | (1L << BRANCHLESSEQ) | (1L << BRANCHGREATHER) | (1L << BRANCHGREATHEREQ) | (1L << JS) | (1L << LOADRA) | (1L << STORERA) | (1L << LOADRV) | (1L << STORERV) | (1L << LOADFP) | (1L << STOREFP) | (1L << COPYFP) | (1L << LOADHP) | (1L << STOREHP) | (1L << PRINT) | (1L << HALT) | (1L << NEW) | (1L << LOADC) | (1L << COPY) | (1L << HEAPOFFSET) | (1L << LABEL))) != 0)) {
				{
                    setState(81);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(2);
					match(PUSH);
					setState(3);
					((AssemblyContext)_localctx).n = match(NUMBER);
					   code.add(PUSH);
					                                        code.add(Integer.parseInt((((AssemblyContext)_localctx).n!=null?((AssemblyContext)_localctx).n.getText():null))); 
					}
					break;
				case 2:
					{
					setState(5);
					match(PUSH);
					setState(6);
					((AssemblyContext)_localctx).l = match(LABEL);
					   code.add(PUSH);
						                                    labelRef.put(code.size(), (((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null));
						                                    code.add(0);          
					}
					break;
				case 3:
					{
					setState(8);
					match(POP);
					   code.add(POP);        
					}
					break;
				case 4:
					{
					setState(10);
					match(ADD);
					   code.add(ADD);        
					}
					break;
				case 5:
					{
					setState(12);
					match(SUB);
					   code.add(SUB);        
					}
					break;
				case 6:
					{
					setState(14);
                        match(MULT);
                        code.add(MULT);
					}
					break;
				case 7:
					{
					setState(16);
					match(DIV);
					   code.add(DIV);        
					}
					break;
				case 8:
					{
					setState(18);
                        match(AND);
                        code.add(AND);
					}
					break;
				case 9:
					{
					setState(20);
                        match(OR);
                        code.add(OR);
					}
					break;
				case 10: {
                    setState(22);
                    match(STOREW);
                    code.add(STOREW);
                }
                break;
                    case 11: {
                        setState(24);
                        match(LOADW);
                        code.add(LOADW);
                    }
                    break;
                    case 12: {
                        setState(26);
                        ((AssemblyContext)_localctx).l = match(LABEL);
                        setState(27);
                        match(COL);
                        labelAdd.put((((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null), code.size());
                    }
					break;
                    case 13:
					{
                        setState(29);
					match(BRANCH);
                        setState(30);
					((AssemblyContext)_localctx).l = match(LABEL);
					   code.add(BRANCH);
					                                        labelRef.put(code.size(), (((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null));
					                                        code.add(0);          
					}
					break;
                    case 14:
					{
                        setState(32);
					match(BRANCHEQ);
                        setState(33);
					((AssemblyContext)_localctx).l = match(LABEL);
					   code.add(BRANCHEQ);
					                                        labelRef.put(code.size(), (((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null));
					                                        code.add(0);          
					}
					break;
                    case 15:
					{
                        setState(35);
					match(BRANCHLESS);
                        setState(36);
					((AssemblyContext)_localctx).l = match(LABEL);
					   code.add(BRANCHLESS);
					                                        labelRef.put(code.size(), (((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null));
					                                        code.add(0);          
					}
					break;
                    case 16:
					{
                        setState(38);
					match(BRANCHLESSEQ);
                        setState(39);
					((AssemblyContext)_localctx).l = match(LABEL);
					   code.add(BRANCHLESSEQ);
					                                        labelRef.put(code.size(), (((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null));
					                                        code.add(0);          
					}
					break;
                    case 17:
					{
                        setState(41);
					match(BRANCHGREATHER);
                        setState(42);
					((AssemblyContext)_localctx).l = match(LABEL);
					   code.add(BRANCHGREATHER);
					                                        labelRef.put(code.size(), (((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null));
					                                        code.add(0);          
					}
					break;
                    case 18:
					{
                        setState(44);
					match(BRANCHGREATHEREQ);
                        setState(45);
					((AssemblyContext)_localctx).l = match(LABEL);
					   code.add(BRANCHGREATHEREQ);
					                                        labelRef.put(code.size(), (((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null));
					                                        code.add(0);          
					}
					break;
                    case 19:
					{
                        setState(47);
					match(JS);
					   code.add(JS);         
					}
					break;
                    case 20:
					{
                        setState(49);
					match(LOADRA);
					   code.add(LOADRA);     
					}
					break;
                    case 21:
					{
                        setState(51);
					match(STORERA);
					   code.add(STORERA);    
					}
					break;
                    case 22:
					{
                        setState(53);
					match(LOADRV);
					   code.add(LOADRV);     
					}
					break;
                    case 23:
					{
                        setState(55);
					match(STORERV);
					   code.add(STORERV);    
					}
					break;
                    case 24:
					{
                        setState(57);
					match(LOADFP);
					   code.add(LOADFP);     
					}
					break;
                    case 25:
					{
                        setState(59);
					match(STOREFP);
					   code.add(STOREFP);    
					}
					break;
                    case 26:
					{
                        setState(61);
					match(COPYFP);
					   code.add(COPYFP);     
					}
					break;
                    case 27:
					{
                        setState(63);
					match(LOADHP);
					   code.add(LOADHP);     
					}
					break;
                    case 28:
					{
                        setState(65);
					match(STOREHP);
					   code.add(STOREHP);    
					}
					break;
                    case 29:
					{
                        setState(67);
					match(PRINT);
					   code.add(PRINT);      
					}
					break;
                    case 30:
					{
                        setState(69);
					match(NEW);
					   code.add(NEW);        
					}
					break;
                    case 31:
					{
                        setState(71);
                        match(LOADC);
                        code.add(LOADC);
                    }
                    break;
                    case 32: {
                        setState(73);
					match(HALT);
					   code.add(HALT);       
					}
					break;
                    case 33:
					{
                        setState(75);
					((AssemblyContext)_localctx).l = match(LABEL);
					   labelRef.put(code.size(), (((AssemblyContext)_localctx).l!=null?((AssemblyContext)_localctx).l.getText():null));
						                                    code.add(0);          
					}
					break;
                    case 34:
					{
                        setState(77);
					match(COPY);
					   code.add(COPY);       
					}
					break;
                    case 35:
					{
                        setState(79);
					match(HEAPOFFSET);
					   code.add(HEAPOFFSET); 
					}
					break;
				}
				}
                    setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}

				   for (Integer refAdd: labelRef.keySet()) {
				        code.set(refAdd, labelAdd.get(labelRef.get(refAdd)));
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
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3([\4\2\t\2\3\2\3\2" +
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
                    "\3\2\3\2\3\2\3\2\3\2\3\2\3\2\7\2T\n\2\f\2\16\2W\13\2\3\2\3\2\3\2\2\2\3" +
                    "\2\2\2\2|\2U\3\2\2\2\4\5\7\3\2\2\5\6\7&\2\2\6T\b\2\1\2\7\b\7\3\2\2\b\t" +
                    "\7%\2\2\tT\b\2\1\2\n\13\7\4\2\2\13T\b\2\1\2\f\r\7\5\2\2\rT\b\2\1\2\16" +
                    "\17\7\6\2\2\17T\b\2\1\2\20\21\7\7\2\2\21T\b\2\1\2\22\23\7\b\2\2\23T\b" +
                    "\2\1\2\24\25\7\n\2\2\25T\b\2\1\2\26\27\7\13\2\2\27T\b\2\1\2\30\31\7\f" +
                    "\2\2\31T\b\2\1\2\32\33\7\r\2\2\33T\b\2\1\2\34\35\7%\2\2\35\36\7$\2\2\36" +
                    "T\b\2\1\2\37 \7\16\2\2 !\7%\2\2!T\b\2\1\2\"#\7\17\2\2#$\7%\2\2$T\b\2\1" +
                    "\2%&\7\20\2\2&\'\7%\2\2\'T\b\2\1\2()\7\21\2\2)*\7%\2\2*T\b\2\1\2+,\7\22" +
                    "\2\2,-\7%\2\2-T\b\2\1\2./\7\23\2\2/\60\7%\2\2\60T\b\2\1\2\61\62\7\24\2" +
                    "\2\62T\b\2\1\2\63\64\7\25\2\2\64T\b\2\1\2\65\66\7\26\2\2\66T\b\2\1\2\67" +
                    "8\7\27\2\28T\b\2\1\29:\7\30\2\2:T\b\2\1\2;<\7\31\2\2<T\b\2\1\2=>\7\32" +
                    "\2\2>T\b\2\1\2?@\7\33\2\2@T\b\2\1\2AB\7\34\2\2BT\b\2\1\2CD\7\35\2\2DT" +
                    "\b\2\1\2EF\7\36\2\2FT\b\2\1\2GH\7 \2\2HT\b\2\1\2IJ\7!\2\2JT\b\2\1\2KL" +
                    "\7\37\2\2LT\b\2\1\2MN\7%\2\2NT\b\2\1\2OP\7\"\2\2PT\b\2\1\2QR\7#\2\2RT" +
                    "\b\2\1\2S\4\3\2\2\2S\7\3\2\2\2S\n\3\2\2\2S\f\3\2\2\2S\16\3\2\2\2S\20\3" +
                    "\2\2\2S\22\3\2\2\2S\24\3\2\2\2S\26\3\2\2\2S\30\3\2\2\2S\32\3\2\2\2S\34" +
                    "\3\2\2\2S\37\3\2\2\2S\"\3\2\2\2S%\3\2\2\2S(\3\2\2\2S+\3\2\2\2S.\3\2\2" +
                    "\2S\61\3\2\2\2S\63\3\2\2\2S\65\3\2\2\2S\67\3\2\2\2S9\3\2\2\2S;\3\2\2\2" +
                    "S=\3\2\2\2S?\3\2\2\2SA\3\2\2\2SC\3\2\2\2SE\3\2\2\2SG\3\2\2\2SI\3\2\2\2" +
                    "SK\3\2\2\2SM\3\2\2\2SO\3\2\2\2SQ\3\2\2\2TW\3\2\2\2US\3\2\2\2UV\3\2\2\2" +
                    "VX\3\2\2\2WU\3\2\2\2XY\b\2\1\2Y\3\3\2\2\2\4SU";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}