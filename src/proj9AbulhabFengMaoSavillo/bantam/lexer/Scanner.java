package proj9AbulhabFengMaoSavillo.bantam.lexer;

import proj9AbulhabFengMaoSavillo.bantam.util.ErrorHandler;

import java.io.Reader;
import java.util.ArrayList;


public class Scanner
{
    /**
     * A private enumeration of all the possible states in which
     * the enclosing class can be during the course of a file examination
     */
    private enum State
    {
        DEFAULT,
        TENTATIVE_COMMENT_START,
        LINE_COMMENT,
        MULTILINE_COMMENT,
        TENTATIVE_MULTILINE_COMMENT_END,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        IGNORE_NEXT
    }

    private SourceFile sourceFile;
    private ErrorHandler errorHandler;

    private char currentChar;

    private State state;
    private State previousState;

    //Code?
    public Scanner(ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = null;
        this.state = State.DEFAULT;
        this.previousState = State.DEFAULT;
    }

    public Scanner(String filename, ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = new SourceFile(filename);
        this.state = State.DEFAULT;
        this.previousState = State.DEFAULT;
    }

    //Code?
    public Scanner(Reader reader, ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = new SourceFile(reader);
        this.state = State.DEFAULT;
        this.previousState = State.DEFAULT;
    }

    /**
     * Each call of this method builds the next Token from the contents
     * of the file being scanned and returns it. When it reaches the end of the file,
     * any calls to scan() result in a Token of kind EOF.
     *
     * @return
     */
    public Token scan()
    {
    	//initialize token kind
    	Token.Kind kind = null;
    	
    	//eat whitespace**
    	this.currentChar = this.sourceFile.getNextChar();
    	
        //check for single-char tokens that can be identified at once
    	boolean isSingleCharIDToken = true;
    	switch(this.currentChar)
    	{
	    	//punctuation
    		case '.':
    			kind = Token.Kind.DOT;
	    		break;
	    	case ':':
    			kind = Token.Kind.COLON;
	    		break;
	    	case ';':
    			kind = Token.Kind.SEMICOLON;
	    		break;
	    	case ',':
    			kind = Token.Kind.COMMA;
	    		break;
	    	//brackets
	    	case '(':
    			kind = Token.Kind.LPAREN;
	    		break;
	    	case ')':
    			kind = Token.Kind.RPAREN;
	    		break;
	    	case '[':
    			kind = Token.Kind.LBRACKET;
	    		break;
	    	case ']':
    			kind = Token.Kind.RBRACKET;
	    		break;
	    	case '{':
    			kind = Token.Kind.LCURLY;
	    		break;
	    	case '}':
    			kind = Token.Kind.RCURLY;
	    		break;
	    	//end of file
	    	case SourceFile.eof:
    			kind = Token.Kind.EOF;
	    		break;
	    	//multiply
	    	case '*':
	    		kind = Token.Kind.MULDIV;
	    		break;
    		//otherwise, is not single-char token that can be identified at once
	    	default:
    			isSingleCharIDToken = false;
    	}
    	
    	if (isSingleCharIDToken)
    	{
    		//make new token before updating current char
    		Token token = new Token(kind, Character.toString(this.currentChar), this.sourceFile.getCurrentLineNumber());
    		
    		//move currentChar forward to next char
    		//to match behavior of other cases, where current token
    		//can be ended by reading in first char of next token
        	this.currentChar = this.sourceFile.getNextChar();
        	
    		return token;
    	}
    	
    	//check for longer tokens that can be identified by first char
    	switch(this.currentChar)
    	{
	    	case '&':
	    		
	    		break;
	    	case '|':
	    		
	    		break;
	    	case '\"':
	    		
	    		break;
    	}
    	
    	//digit, /, <, >, =, letters
    	// / -> line comment, multiline comment, or divide
    	// < -> compare, but is either < or <=
    	// > -> similar to prev
    	// = -> = (assign) or == (compare)
    	//digit -> int constant
    	//letter -> identifier; token handles distinction between letter things
    	
    	/*
         
         questions
         SHOULD FINDING END OF TOKEN AND DETERMINING KIND OF TOKEN BE SEPARATE?
         can Bantam have arbitrary amounts of whitespace? yes
         a bunch of tokens have ends that can be found by reading in first char of next token,
         i.e. single-char token w/o spacing in between
         so then should all tokens leave the current char on the next char? then don't call getNextChar first
         only call if whitespace
         should make use of code in token that handles keywords and booleans?
         what are legal identifier starts? can we use isJavaIdentifierStart?
         
         //things identified by start
          * (all single-char)
          * str -> method that loops to end of str, 
          * 	raises error if mult lines/too long,
          * 	checks for invalid escape chars
          * line comment -> loop to eol
          * multiline comment -> loop to *\/
          * int -> method that loops to whitespace/single-char
          * binary logic -> same method as int
          * 
          * compare, assign, other operators -> same method as int
          * otherwise booleans, identifiers, keywords -> same method as int
         
         //what tokens end on
         *  no need to check next char
         *EOF, 
         *DOT, COLON, SEMICOLON, COMMA, 
         *LPAREN, RPAREN, LBRACKET, RBRACKET, LCURLY, RCURLY,
         *MULDIV, 
         *BINARYLOGIC (&& and ||), <- could be badly formed though
         *
         * double quote
         *STRCONST, 
         *
         * end line or closing *\/
         *COMMENT, 
         *
         * whitespace, single-char token
         *INTCONST, BOOLEAN, IDENTIFIER,
         BREAK, CAST, CLASS, VAR, ELSE, EXTENDS, FOR, IF, INSTANCEOF, NEW,
         RETURN, WHILE
         *
         * whitespace, identifier start, single-char token
         *PLUSMINUS, UNARYINCR, UNARYDECR, UNARYNOT,
         *
         *
         *

         // operators...
         COMPARE, ASSIGN,

         // special tokens...
         ERROR, 
    	 */
    	
    	StringBuilder spelling = new StringBuilder();

        return new Token(kind, spelling.toString(), this.sourceFile.getCurrentLineNumber());
    }
    
    private String completeStringToken()
    {
    	StringBuilder spellingBuilder = new StringBuilder();
    	
    	//collect chars until closing double quote
    	while (this.currentChar != '\"')
    	{
    		spellingBuilder.append(Character.toString(this.currentChar));
        	this.currentChar = this.sourceFile.getNextChar();
        	
        	//check for newline
        	//check for invalid escape chars
        	//check if too long
    	}
    	
    	//append closing quote
    	spellingBuilder.append(Character.toString(this.currentChar));
    	
    	return spellingBuilder.toString();
    }

    public static void main(String[] args)
    {
        ArrayList<Token> tokenStream = new ArrayList<>();

    }
}
