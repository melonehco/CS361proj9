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
	    	//some operators
	    	case '*':
	    		kind = Token.Kind.MULDIV;
	    		break;
	    	case '!':
	    		kind = Token.Kind.UNARYNOT;
	    		break;
	    	case '%':
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
    	
    	//check for longer tokens that can be identified at once by first char
    	String fullToken = null;
    	switch(this.currentChar)
    	{
	    	case '&':
	    		//TODO: can only be &&, so just handle inline here?
	    		break;
	    	case '|':
	    		//TODO: can only be ||, so just handle inline here?
	    		break;
	    	case '\"':
	    		fullToken = this.completeStringToken();
	    		break;
    	}
    	//if token type was identified by first char
    	if (fullToken != null)
    	{
    		Token token = new Token(kind, fullToken, this.sourceFile.getCurrentLineNumber());
    		
    		//move currentChar forward to next char
    		//to match behavior of other cases, where current token
    		//can be ended by reading in first char of next token
        	this.currentChar = this.sourceFile.getNextChar();
        	
    		return token;
    	}
    	
    	//otherwise, handle other token types
    	if (Character.isDigit(this.currentChar))
    	{
    		
    	}
    	else if (Character.isLetter(this.currentChar))
    	{
    		
    	}

    	//TODO: cases for int, identifier
    		//Character class isLetter, isDigit
    	//TODO: handle other tokens
    	//remember to advance by one char after str
    	//probably also handle illegal chars outside of comments/strings
    	//	for any longer token
    	//handle having already reached EOF
    	
    	//to handle:
    	//< and >
    	//handler for each case?
    	
    	//digit, /, <, >, =, letters
    	// / -> line comment, multiline comment, or divide
    	// < -> compare, but is either < or <=
    	// > -> similar to prev
    	// = -> = (assign) or == (compare)
    	//digit -> int constant
    	//letter -> identifier; token handles distinction between letter things
    	
    	//+ -> + or ++
    	//- -> - or --
    	
    	StringBuilder spelling = new StringBuilder();

        return new Token(kind, spelling.toString(), this.sourceFile.getCurrentLineNumber());
    }
    
    /**
     * Builds and returns a string token starting from the current character
     * @return the string token
     */
    private String completeStringToken()
    {
    	StringBuilder spellingBuilder = new StringBuilder();
    	
    	//collect chars until closing double quote
    	while (this.currentChar != '\"')
    	{
    		spellingBuilder.append(Character.toString(this.currentChar));
        	this.currentChar = this.sourceFile.getNextChar();
        	
        	//TODO:
        	//check for newline
        	//check for invalid escape chars
        	//check if too long
    	}
    	
    	//append closing quote
    	spellingBuilder.append(Character.toString(this.currentChar));
    	
    	return spellingBuilder.toString();
    }
    
    /**
     * Builds and returns an intconst token string
     * starting from the current character
     * @return the intconst token string
     */
    private String completeIntconstToken()
    {
    	StringBuilder spellingBuilder = new StringBuilder();
    	
    	//TODO: collect chars until non-digit char
    	while (this.currentChar != '\"')
    	{
    		spellingBuilder.append(Character.toString(this.currentChar));
        	this.currentChar = this.sourceFile.getNextChar();
        	
        	//TODO: check whether int is too long
    	}
    	
    	return spellingBuilder.toString();
    }

    public static void main(String[] args)
    {
        ArrayList<Token> tokenStream = new ArrayList<>();

    }
}