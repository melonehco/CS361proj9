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
        StringBuilder spelling = new StringBuilder();

        State stateSnapshot = this.state; // save state now to be given to previousState at the end

        this.currentChar = this.sourceFile.getNextChar();
        switch (this.state)
        {
            // Either we encounter a brace and increment our internal counters,
            // or we encounter a significant token and move to a another state
            // where any braces encountered will be trivial (comment or string)
            case DEFAULT:
                switch (this.currentChar)
                {
                    case '/':
                        this.state = State.TENTATIVE_COMMENT_START;
                        break;
                    case '\'':
                        this.state = State.SINGLE_QUOTE;
                        break;
                    case '\"':
                        this.state = State.DOUBLE_QUOTE;
                        break;
                }
                break;

            // If a forward slash was encountered in the state DEFAULT,
            // check to see if the next character is such that we have entered a comment
            // (else we're back in a DEFAULT context)
            case TENTATIVE_COMMENT_START:
                switch (this.currentChar)
                {
                    case '/':
                        this.state = State.LINE_COMMENT;
                        break;
                    case '*':
                        this.state = State.MULTILINE_COMMENT;
                        break;
                    default:
                        this.state = State.DEFAULT;
                        break;
                }
                break;

            case LINE_COMMENT:
                // System-dependent line return signals the end of a line-comment
                if (this.currentChar == System.lineSeparator().charAt(0))
                    this.state = State.DEFAULT;
                break;

            case MULTILINE_COMMENT:
                switch (this.currentChar)
                {
                    case '*':
                        this.state = State.TENTATIVE_MULTILINE_COMMENT_END;
                        break;
                }
                break;

            // If an asterisk was encountered in a multiline comment,
            // check to see if the next character is such that the comment has ended,
            // (else we're back in a multiline comment context)
            case TENTATIVE_MULTILINE_COMMENT_END:
                switch (this.currentChar)
                {
                    case '/':
                        this.state = State.DEFAULT;
                        break;
                    default:
                        this.state = State.MULTILINE_COMMENT;
                        break;
                }
                break;

            case SINGLE_QUOTE:
                switch (this.currentChar)
                {
                    case '\'':
                        this.state = State.DEFAULT;
                        break;
                    case '\\':
                        this.state = State.IGNORE_NEXT;
                        break;
                }
                break;

            case DOUBLE_QUOTE:
                switch (this.currentChar)
                {
                    case '\"':
                        this.state = State.DEFAULT;
                        break;
                    case '\\':
                        this.state = State.IGNORE_NEXT;
                        break;
                }
                break;

            // If a backslash occurred last character, ignore this one and return to previous state
            case IGNORE_NEXT:
                this.state = this.previousState;
                break;
        }

        this.previousState = stateSnapshot;

        return new Token(kind, spelling.toString(), position);
    }

    public static void main(String[] args)
    {
        ArrayList<Token> tokenStream = new ArrayList<>();

    }
}
