package proj9AbulhabFengMaoSavillo.bantam.lexer;

import proj9AbulhabFengMaoSavillo.bantam.util.ErrorHandler;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;


public class Scanner
{
	private SourceFile sourceFile;
    private ErrorHandler errorHandler;

    private char currentChar;
    private ArrayDeque<Character> buffer; // for when another token is found too early.

    //Code?
    public Scanner(ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = null;
        this.buffer = new ArrayDeque<Character>();
    }

    public Scanner(String filename, ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = new SourceFile(filename);
        this.buffer = new ArrayDeque<Character>();
    }

    //Code?
    public Scanner(Reader reader, ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = new SourceFile(reader);
        this.buffer = new ArrayDeque<Character>();
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
        if (this.currentChar == SourceFile.eof)
            return new Token(Token.Kind.EOF,
                             Character.toString(SourceFile.eof),
                             this.sourceFile.getCurrentLineNumber());

        Token.Kind kind = null;
        StringBuilder spelling = new StringBuilder();
        int lineNumber;

        boolean isTokenComplete = true; // start as true, and set to false if not a single char token

        Character c = ' ';
        if (!this.buffer.isEmpty())
        {
        		c = this.buffer.poll();
        }
        
        // Set first char to that caught in the buffer, if there was one. Else nextChar.
        if (!Character.isWhitespace(c))
        {
            this.currentChar = c;
        }
        else
        {
            do { this.currentChar = this.sourceFile.getNextChar(); }
            while (Character.isWhitespace(this.currentChar));
        }

        spelling.append(this.currentChar);
        lineNumber = this.sourceFile.getCurrentLineNumber();

        //check for single-char tokens that can be identified at once
        switch (this.currentChar)
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
                isTokenComplete = false;
                break;
        }

        if (isTokenComplete)
            return new Token(kind, spelling.toString(), lineNumber);

        isTokenComplete = true; // set to true, and set to false if fails next check

        //TODO: should check in these cases for whether illegal chars appear
        // complete longer tokens that can be identified at once by first char
        switch (this.currentChar)
        {
            case '&':
                kind = Token.Kind.BINARYLOGIC;
                //add a second &
                this.currentChar = this.sourceFile.getNextChar();
                spelling.append(this.currentChar);
                break;
            case '|':
                kind = Token.Kind.BINARYLOGIC;
                //add a second |
                this.currentChar = this.sourceFile.getNextChar();
                spelling.append(this.currentChar);
                break;
            case '\"':
                kind = Token.Kind.STRCONST;
                this.currentChar = this.sourceFile.getNextChar();
                spelling.append(this.completeStringToken());
                break;
            default:
                isTokenComplete = false; // (it has failed next check)
                break;
        }

        if (isTokenComplete)
            return new Token(kind, spelling.toString(), lineNumber);

        isTokenComplete = true;  // set to true, and set to false if fails next check

        //integer constant
        if (Character.isDigit(this.currentChar))
        {
            kind = Token.Kind.INTCONST;
            this.currentChar = this.sourceFile.getNextChar();
            spelling.append(this.completeIntconstToken());
        }
        //identifier/boolean/keyword
        else if (Character.isLetter(this.currentChar))
        {
            kind = Token.Kind.IDENTIFIER;
            this.currentChar = this.sourceFile.getNextChar();
            spelling.append(this.completeIdentifierToken());
        }
        else
        {
            isTokenComplete = false; // (it has failed next check)
        }

        if (isTokenComplete)
            return new Token(kind, spelling.toString(), lineNumber);

        isTokenComplete = true;  // set to true, and set to false if fails next check

        //TODO: I don't actually know how to organize this part
        //but I'll just write it
        //TODO check StringBuilder spelling = new StringBuilder();
        switch (this.currentChar)
        {
            case '+': //token can be + or ++
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '+') //check whether has second +
                {
                    spelling.append(this.currentChar);
                    kind = Token.Kind.UNARYINCR;
                }
                else
                {
                    kind = Token.Kind.PLUSMINUS;
                    //has read in start of next token, so store in buffer
                    this.buffer.add(this.currentChar);
                }
                break;
            case '-': //token can be - or --
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '-') //check whether has second -
                {
                    spelling.append(this.currentChar);
                    kind = Token.Kind.UNARYDECR;
                }
                else
                {
                    kind = Token.Kind.PLUSMINUS;
                    //has read in start of next token, so store in buffer
                    this.buffer.add(this.currentChar);
                }
                break;
            case '<': //token can be < or <=
                kind = Token.Kind.COMPARE;
                
                //check whether has =
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '=')
                {
                    spelling.append(this.currentChar);
                }
                else
                {
                    //has read in start of next token, so store in buffer
                    this.buffer.add(this.currentChar);
                }

                break;
            case '>': //token can be > or >=
                kind = Token.Kind.COMPARE;
                
                //check whether has =
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '=')
                {
                    spelling.append(this.currentChar);
                }
                else
                {
                    //has read in start of next token, so store in buffer
                    this.buffer.add(this.currentChar);
                }
                break;
            case '=': //token can be = or ==
            	//check whether has =
            	this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '=')
                {
                    spelling.append(this.currentChar);
                    kind = Token.Kind.COMPARE;
                }
                else //otherwise, is just assignment operator
                {
                    kind = Token.Kind.ASSIGN;
                    //has read in start of next token, so store in buffer
                    this.buffer.add(this.currentChar);
                }
                break;
            case '/': //token can be / or a comment
            	//check whether next char starts a comment
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '*') //block comment
                {
                    kind = Token.Kind.COMMENT;
                    String tokenString = this.completeBlockCommentToken();
                    spelling.append(tokenString);
                }
                else if (this.currentChar == '/') //single-line comment
                {
                    kind = Token.Kind.COMMENT;
                    String tokenString = this.completeLineCommentToken();
                    spelling.append(tokenString);
                }
                else
                {
                    kind = Token.Kind.MULDIV;
                    //has read in start of next token, so store in buffer
                    this.buffer.add(this.currentChar);
                }

                break;
            default:
                isTokenComplete = false;
                break;
        }

        //if first char doesn't match any of above cases, is illegal char
        //TODO: error

    	/* TODO:
    	-handle EOF in longer tokens
    	-handle error thrown by SourceFile?
    	*/

        //digit -> int constant
        //letter -> identifier; token handles distinction between letter things

        //+ -> + or ++
        //- -> - or --

        if (isTokenComplete)
            return new Token(kind, spelling.toString(), lineNumber);
        else
        {
            System.out.println("is whitespace?" + Character.isWhitespace(this.currentChar));
        		System.out.println("something went wrong: char " + this.currentChar);
            return null;
        }
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
	        	
	        	//check for escaped chars
	        	if (this.currentChar == '\\')
	        	{
	        		spellingBuilder.append(Character.toString(this.currentChar));
	    	        this.currentChar = this.sourceFile.getNextChar();	        		
	    	        
	    	        //handle having escaped quote \"
	    	        if (this.currentChar == '"')
	    	        {
		        		spellingBuilder.append(Character.toString(this.currentChar));
		    	        this.currentChar = this.sourceFile.getNextChar();	  
	    	        }
	        		
	    	        //check for invalid escape chars
	    	        else if (this.currentChar != 'n' && this.currentChar != 't' &&
	    	        		this.currentChar != '"' && this.currentChar != '\\' &&
	    	        		this.currentChar != 'f' && this.currentChar != 'r')
	    	        {
	    	        		//TODO
	    	        		System.out.println("illegal escape: " + this.currentChar);
	    	        }
	        	}
	        	
	        	//check if not terminated correctly
	        	if (this.currentChar == SourceFile.eof || this.currentChar == SourceFile.eol)
	        	{
	        		System.out.println("string not terminated");
	        		break;
	        	}
	        	
	        	//TODO:
	            //EOF
	        	//check for newline
	        	//check if too long
	    	}
	    	
	    	//append closing quote
	    	spellingBuilder.append(Character.toString(this.currentChar));
	    	
	    	return spellingBuilder.toString();
    }
    
    /**
     * Builds and returns a block comment token string starting from the current char
     *
     * @return the comment token string
     */
    private String completeBlockCommentToken()
    {
        StringBuilder spellingBuilder = new StringBuilder();

        boolean atTentativeEnd = false; // a '*' has been seen
        boolean terminated = false; // a '*' and '/' have been seen in sequence

        while (!terminated && this.currentChar != SourceFile.eof)
        {
            spellingBuilder.append(this.currentChar);
            this.currentChar = this.sourceFile.getNextChar();

            if (atTentativeEnd) // if '*' has been seen
            {
                if (this.currentChar == '/')    // block comment indeed terminated
                {
                		spellingBuilder.append(this.currentChar);
                    terminated = true;
                }
                else                            // otherwise just a '*' in the middle somewhere
                    atTentativeEnd = false;
            }
            else if (this.currentChar == '*')
            {
                atTentativeEnd = true;
            }
        }

        //if left loop before seeing "*/", block comment was not terminated correctly
        if (!terminated)
        {
            //TODO: error
        		System.out.println("unterminated");
        }

        return spellingBuilder.toString();
    }

    /**
     * Builds and returns a single-line comment token string starting from the current char
     *
     * @return the comment token string
     */
    private String completeLineCommentToken()
    {
        StringBuilder spellingBuilder = new StringBuilder();

        //collect chars until end of line or file
        while (this.currentChar != '\n' && this.currentChar != SourceFile.eof)
        {
        		spellingBuilder.append(this.currentChar);
            this.currentChar = this.sourceFile.getNextChar();
        }

        this.buffer.add(this.currentChar);

        return spellingBuilder.toString();
    }

    /**
     * Builds and returns an intconst token string starting from the current char
     * Returns upon reading in any non-digit char
     *
     * @return the intconst token string
     */
    private String completeIntconstToken()
    {
        StringBuilder spellingBuilder = new StringBuilder();

        //collect chars until non-digit char
        while (Character.isDigit(this.currentChar))
        {
            spellingBuilder.append(this.currentChar);
            this.currentChar = this.sourceFile.getNextChar();

            //TODO: check whether int is too long
        }

        this.buffer.add(this.currentChar);

        return spellingBuilder.toString();
    }

    /**
     * Builds and returns an identifier token string (or boolean or keyword)
     * starting from the current character
     * Returns upon reading in any non-identifier char
     *
     * @return the identifier token string
     */
    private String completeIdentifierToken()
    {
        StringBuilder spellingBuilder = new StringBuilder();

        //collect chars until non-identifier char
        while (Character.isLetter(this.currentChar) ||
                Character.isDigit(this.currentChar) ||
                this.currentChar == '_')
        {
            spellingBuilder.append(this.currentChar);
            this.currentChar = this.sourceFile.getNextChar();
        }

        this.buffer.add(this.currentChar);
        
        return spellingBuilder.toString();
    }
    
    public static void main(String[] args)
    {
	    	ArrayList<Token> tokenStream = new ArrayList<Token>();
	    	ErrorHandler errorHandler = new ErrorHandler();
	    	String filename = "/Users/hopehu/Desktop/Winwin.java";
	    	Scanner scanner = new Scanner(filename, errorHandler);
	    	
	    	Token currentToken = scanner.scan();
	    	while(currentToken.kind != Token.Kind.EOF)
	    	{
	    		tokenStream.add(currentToken);
	    		currentToken = scanner.scan();
	    	}
	    	
	    	for (Token t: tokenStream)
	    	{
	    		System.out.println(t);
	    		System.out.println("--------------------");
	    	}
    }
}