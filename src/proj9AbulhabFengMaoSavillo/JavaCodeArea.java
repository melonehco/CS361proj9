/*
 * File: StyledCodeArea.java
 * F18 CS361 Project 9
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 11/28/2018
 * This file contains the StyledCodeArea class, which extends the CodeArea class
 * to handle syntax highlighting for Java.
 */

package proj9AbulhabFengMaoSavillo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import org.reactfx.util.FxTimer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class extends the CodeArea class from RichTextFx to handle
 * syntax highlighting for Java.
 *
 * @author Evan Savillo
 * @author Yi Feng
 * @author Zena Abulhab
 * @author Melody Mao
 */
public class JavaCodeArea extends CodeArea
{

    private ContextMenu contextMenu;

    //constructor
    public JavaCodeArea(ObservableList<MenuItem> menu){
        this.contextMenu=new ContextMenu();
        this.contextMenu.getItems().addAll(menu);
        this.addRightClickMenu();
    }
    /**
     * a list of key words to be highlighted
     */
    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "var"
    };

    /**
     * regular expressions of characters to be highlighted
     */
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String INTEGER_PATTERN = "(?<![\\w])(?<![\\d.])[0-9]+(?![\\d.])(?![\\w])";

    /**
     * patterns to be highlighted
     */
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<INTEGER>" + INTEGER_PATTERN + ")"
    );

    /**
     * Creates a new empty JavaCodeArea
     */
    public JavaCodeArea()
    {
        //color syntax immediately
        this.handleTextChange();
        //update syntax coloring whenever contents update
        this.setOnKeyPressed(event -> this.handleTextChange());
        //highlight for syntax
        this.highlightText();
        //Enables line numbering
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));

        //add right click menu
        this.addRightClickMenu();

        //enables auto-closing parentheses, brackets, and curly braces
        this.addEventHandler(KeyEvent.KEY_PRESSED,
                             event ->
                             {
                                 //check the key combination for a (
                                 if (event.getCode() == KeyCode.DIGIT9 && event.isShiftDown())
                                 {
                                     FxTimer.runLater( //wait for original key to type & appear before auto-closing
                                                       Duration.ofMillis(100),
                                                       () ->
                                                       {
                                                           int caretPosition = this.getCaretPosition();
                                                           this.insertText(caretPosition, ")");
                                                           this.moveTo(caretPosition);
                                                       });
                                 }
                                 //check for [] or {}
                                 else if (event.getCode() == KeyCode.OPEN_BRACKET)
                                 {
                                     if (event.isShiftDown()) //handle closing {}
                                     {
                                         FxTimer.runLater( //wait for original key to type & appear before auto-closing
                                                           Duration.ofMillis(100),
                                                           () ->
                                                           {
                                                               int caretPosition = this.getCaretPosition();
                                                               this.insertText(caretPosition, "\n\n}");
                                                               this.moveTo(caretPosition + 1);
                                                           });
                                     }
                                     else //handle closing []
                                     {
                                         FxTimer.runLater( //wait for original key to type & appear before auto-closing
                                                           Duration.ofMillis(100),
                                                           () ->
                                                           {
                                                               int caretPosition = this.getCaretPosition();
                                                               this.insertText(caretPosition, "]");
                                                               this.moveTo(caretPosition);
                                                           });
                                     }
                                 }
                             }
        );
    }

    /**
     * Computes the highlighting of substrings of text to return the style of each substring.
     *
     * @param text string to compute highlighting of
     * @return StyleSpans Collection Object
     */
    private static StyleSpans<Collection<String>> computeHighlighting(String text)
    {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find())
        {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            matcher.group("INTEGER") != null ? "integer" :
                                                                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Helper function to highlight the text within the StyledCodeArea.
     */
    private void highlightText()
    {
        this.setStyleSpans(0, computeHighlighting(this.getText()));
    }

    /**
     * Handles the text change action.
     * Listens to the text changes and highlights the keywords real-time.
     */
    private void handleTextChange()
    {
        Subscription cleanupWhenNoLongerNeedIt = this

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                // when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> this.highlightText());
    }

    private void addRightClickMenu(){

        this.setOnMousePressed(event ->
        {
            if (event.isSecondaryButtonDown())
            {
                this.contextMenu.show(this,
                        event.getScreenX(),
                        event.getScreenY());
            }
            else if (event.isPrimaryButtonDown() && this.contextMenu.isShowing())
            {
                this.contextMenu.hide();
            }
        });
    }


    /**
     * Static helper method to duplicate the contents of a menu
     *
     * @param menuItems List of Menu items to be duplicated
     * @return a clone of menu.getItems()
     */
    private static ObservableList<MenuItem> duplicateMenuItems(ObservableList<MenuItem> menuItems)
    {
        ArrayList<MenuItem> clone = new ArrayList<>();

        menuItems.forEach(menuItem ->
        {
            MenuItem newItem = new MenuItem();
            newItem.setText(menuItem.getText());
            newItem.setOnAction(menuItem.getOnAction());
            newItem.setId(menuItem.getId());
            clone.add(newItem);
        });

        return FXCollections.observableList(clone);
    }
}
