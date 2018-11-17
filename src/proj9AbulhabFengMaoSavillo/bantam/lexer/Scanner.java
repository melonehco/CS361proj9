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
    private State state;

    public Scanner(ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = null;
        this.buffer = new ArrayDeque<>();
    }

    public Scanner(String filename, ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = new SourceFile(filename);
        this.buffer = new ArrayDeque<>();
    }

    public Scanner(Reader reader, ErrorHandler handler)
    {
        this.errorHandler = handler;
        this.currentChar = ' ';
        this.sourceFile = new SourceFile(reader);
        this.buffer = new ArrayDeque<>();
    }

    public static void main(String[] args)
    {
        ArrayList<Token> tokenStream = new ArrayList<>();
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

        if (!this.buffer.isEmpty())
            this.currentChar = this.buffer.poll();
        else
            do { this.currentChar = this.sourceFile.getNextChar(); }
            while (this.currentChar != ' ' || this.currentChar != System.lineSeparator().charAt(0));

        //check for single-char tokens that can be identified at once
        boolean isSingleCharIDToken = true;
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

        //TODO: should check in these cases for whether illegal chars appear
        //check for longer tokens that can be identified at once by first char
        String fullToken = null;
        switch (this.currentChar)
        {
            case '&':
                kind = Token.Kind.BINARYLOGIC;
                fullToken = Character.toString(this.currentChar);
                //add on second &
                this.currentChar = this.sourceFile.getNextChar();
                fullToken += this.currentChar;
                break;
            case '|':
                kind = Token.Kind.BINARYLOGIC;
                fullToken = Character.toString(this.currentChar);
                //add on second |
                this.currentChar = this.sourceFile.getNextChar();
                fullToken += this.currentChar;
                break;
            case '\"':
                kind = Token.Kind.STRCONST;
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
        //integer constant
        if (Character.isDigit(this.currentChar))
        {
            kind = Token.Kind.INTCONST;
            //TODO: loop to either non-digit char (to handle badly formed
            //	tokens) or to whitespace/single-char token
        }
        //identifier/boolean/keyword
        else if (Character.isLetter(this.currentChar))
        {
            kind = Token.Kind.IDENTIFIER;
            //TODO: loop to whitespace/single-char token
        }

        //TODO: I don't actually know how to organize this part
        //but I'll just write it
        StringBuilder spelling = new StringBuilder();
        switch (this.currentChar)
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
                if (this.currentChar == '*') //check whether has =
                {
                    kind = Token.Kind.COMMENT;
                    //TODO: call method to complete multiline comment
                }
                else if (this.currentChar == '/') //check whether has =
                {
                    kind = Token.Kind.COMMENT;
                    spelling.append(this.currentChar);
                    //TODO: call method to complete single line comment
                }
                else //otherwise, is just divide operator
                    kind = Token.Kind.MULDIV;

                break;
        }

        //if first char doesn't match any of above cases, is illegal char

        //TODO: handle other tokens
        //remember to advance by one char after str
        //probably also handle illegal chars outside of comments/strings
        //	for any longer token
        //handle having already reached EOF

        //digit, /, <, >, =, letters
        // / -> line comment, multiline comment, or divide
        // < -> compare, but is either < or <=
        // > -> similar to prev
        // = -> = (assign) or == (compare)
        //digit -> int constant
        //letter -> identifier; token handles distinction between letter things

        //+ -> + or ++
        //- -> - or --

        return new Token(kind, spelling.toString(), this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Builds and returns a string token starting from the current character
     *
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
     *
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

    /**
     * A private enumeration of all the possible states in which
     * the enclosing class can be during the course of a file examination
     */
    private enum State
    {
        DEFAULT,
        LINE_COMMENT,
    }
}