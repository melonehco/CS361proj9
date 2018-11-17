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
    }

    public Scanner(String filename, ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = new SourceFile(filename);
    }

    //Code?
    public Scanner(Reader reader, ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = new SourceFile(reader);
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
        StringBuilder spelling = new StringBuilder();
        Token.Kind kind = null;
        boolean isTokenComplete = true; // start as true, and set to false if not a single char token

        // Set first char to that caught in the buffer, if there was one. Else nextChar.
        if (!this.buffer.isEmpty())
            this.currentChar = this.buffer.poll();
        else
            do { this.currentChar = this.sourceFile.getNextChar(); }
            while (this.currentChar != ' ' ||
                    this.currentChar != System.lineSeparator().charAt(0));
        // || this.currentChar != '\t' TODO ?????

        spelling.append(this.currentChar);

        //check for single-char tokens that can be identified at once
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
                isTokenComplete = false;
                break;
    	}

        if (isTokenComplete)
            return new Token(kind, spelling.toString(), this.sourceFile.getCurrentLineNumber());

        isTokenComplete = true; // set to true, and set to false if fail next check

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
                spelling.append(this.completeStringToken());
                break;
            default:
                isTokenComplete = false;
                break;
        }

        if (isTokenComplete)
            return new Token(kind, spelling.toString(), this.sourceFile.getCurrentLineNumber());


        //otherwise, handle other token types
        //integer constant
        if (Character.isDigit(this.currentChar))
        {
            kind = Token.Kind.INTCONST;
            String tokenString = this.completeIntconstToken();
            spelling.append(tokenString);
        }
        //identifier/boolean/keyword
        else if (Character.isLetter(this.currentChar))
        {
            kind = Token.Kind.IDENTIFIER;
            String tokenString = this.completeIdentifierToken();
            spelling.append(tokenString);
        }
    	
    	//TODO: I don't actually know how to organize this part
    	//but I'll just write it
        //TODO check StringBuilder spelling = new StringBuilder();
        switch(this.currentChar)
        {
            case '+': //token can be + or ++
                spelling.append(this.currentChar);
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '+') //check whether has second +
                {
                    spelling.append(this.currentChar);
                    this.currentChar = this.sourceFile.getNextChar();
                    kind = Token.Kind.UNARYINCR;
                }
                else
                    kind = Token.Kind.PLUSMINUS;
                break;
            case '-': //token can be - or --
                spelling.append(this.currentChar);
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '-') //check whether has second -
                {
                    spelling.append(this.currentChar);
                    this.currentChar = this.sourceFile.getNextChar();
                    kind = Token.Kind.UNARYDECR;
                }
                else
                    kind = Token.Kind.PLUSMINUS;
                break;
            case '<': //token can be < or <=
                kind = Token.Kind.COMPARE;
                spelling.append(this.currentChar);
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '=') //check whether has =
                {
                    spelling.append(this.currentChar);
                    this.currentChar = this.sourceFile.getNextChar();
                }

                break;
            case '>': //token can be > or >=
                kind = Token.Kind.COMPARE;
                spelling.append(this.currentChar);
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '=') //check whether has =
                {
                    spelling.append(this.currentChar);
                    this.currentChar = this.sourceFile.getNextChar();
                }
                break;
            case '=': //token can be = or ==
                spelling.append(this.currentChar);
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '=') //check whether has =
                {
                    spelling.append(this.currentChar);
                    this.currentChar = this.sourceFile.getNextChar();
                    kind = Token.Kind.COMPARE;
                }
                else //otherwise, is just assignment operator
                    kind = Token.Kind.ASSIGN;
                break;
            case '/': //token can be / or a comment
                spelling.append(this.currentChar);
                this.currentChar = this.sourceFile.getNextChar();
                if (this.currentChar == '*') //multiline comment
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
                else //otherwise, is just divide operator
                    kind = Token.Kind.MULDIV;

                break;
        }
    	
        //if first char doesn't match any of above cases, is illegal char
    	//TODO: error

    	/* TODO:
    	handle having already reached EOF
    	handle EOF in longer tokens
    	reorganize string builder stuff
    	possibly have complete methods return token instead of string
    	if newline is read in, need to get line number beforehand?
    	handle error thrown by SourceFile?
    	*/

    	//digit -> int constant
    	//letter -> identifier; token handles distinction between letter things
    	
    	//+ -> + or ++
    	//- -> - or --
    	
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
     * Builds and returns a block comment token string starting from the current char
     * @return the comment token string
     */
    private String completeBlockCommentToken()
    {
    	StringBuilder spellingBuilder = new StringBuilder();

    	boolean atTentativeEnd = false; //whether a * has been seen
    	boolean terminated = false; //whether consecutive */ have been seen
    	while (!terminated)
    	{
    		spellingBuilder.append(this.currentChar);
        	this.currentChar = this.sourceFile.getNextChar();

        	if (atTentativeEnd) //if * has been seen
        	{
        		if (this.currentChar == '/') //block comment terminated
        		{
        			terminated = true;
        		}
        		else //otherwise just a * in the middle somewhere
        		{
        			atTentativeEnd = false;
        		}
        	}
        	else if (this.currentChar == '*')
        	{
        		atTentativeEnd = true;
        	}
        	else if (this.currentChar == SourceFile.eof)
        	{
        		break;
        	}

    	}

    	//if left loop before seeing */, block comment was not terminated correctly
    	if (!terminated)
    	{
    		//TODO: error
    	}

    	return spellingBuilder.toString();
    }

    /**
     * Builds and returns a single-line comment token string starting from the current char
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

    	return spellingBuilder.toString();
    }

    /**
     * Builds and returns an intconst token string starting from the current char
     * Returns upon reading in any non-digit char
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

    	return spellingBuilder.toString();
    }

    /**
     * Builds and returns an identifier token string (or boolean or keyword)
     * starting from the current character
     * Returns upon reading in any non-identifier char
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
    	
    	return spellingBuilder.toString();
    }
}