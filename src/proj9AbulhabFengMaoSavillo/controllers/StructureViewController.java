/*
File: StructureViewController.java
CS361 Project 9
Names: Melody Mao, Zena Abulhab, Yi Feng, and Evan Savillo
Date: 11/20/2018
*/

package proj9AbulhabFengMaoSavillo.controllers;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import proj9AbulhabFengMaoSavillo.JavaCodeArea;
import proj9AbulhabFengMaoSavillo.JavaTabPane;
import proj9AbulhabFengMaoSavillo.antlr.Java8BaseListener;
import proj9AbulhabFengMaoSavillo.antlr.Java8Lexer;
import proj9AbulhabFengMaoSavillo.antlr.Java8Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Controller that manages the generation and display of the structure of the
 * java code in the file currently being viewed.
 */
public class StructureViewController
{
    private final ParseTreeWalker walker;
    private Map<TreeItem, Integer> treeItemLineNumMap;
    private TreeView<String> treeView;
    private Thread thread;


    /**
     * Constructor for this class
     */
    public StructureViewController()
    {
        this.walker = new ParseTreeWalker();
        this.treeItemLineNumMap = new HashMap<>();
    }

    /**
     * Takes in the fxml item treeView from main Controller.
     *
     * @param treeView TreeView item representing structure display
     */
    public void setTreeView(TreeView<String> treeView)
    {
        this.treeView = treeView;
    }

    /**
     * Handles when the user clicks on the file structure tree view
     *
     * @param tabPane the tab pane
     */
    public void handleTreeItemClicked(JavaTabPane tabPane)
    {
        TreeItem selectedTreeItem = this.treeView.getSelectionModel().getSelectedItem();
        JavaCodeArea currentCodeArea = tabPane.getCurrentCodeArea();
        if (selectedTreeItem != null)
        {
            int lineNum = this.getTreeItemLineNum(selectedTreeItem);
            if (currentCodeArea != null)
            {
                currentCodeArea.showParagraphAtTop(lineNum - 1);
                currentCodeArea.moveTo(lineNum - 1, 0);
                currentCodeArea.selectLine();
            }
        }
    }

    /**
     * Returns the line number currently associated with the specified tree item
     *
     * @param treeItem Which TreeItem to get the line number of
     * @return the line number corresponding with that tree item
     */
    private Integer getTreeItemLineNum(TreeItem treeItem)
    {
        return this.treeItemLineNumMap.get(treeItem);
    }

    /**
     * Parses a file thereby storing contents as TreeItems in our special tree.
     *
     * @param fileContents the file to be parsed
     */
    public void generateStructureTree(String fileContents)
    {
        TreeItem<String> newRoot = new TreeItem<>(fileContents);
        this.setRootNode(newRoot);

        if (this.thread != null)
        {
            if (this.thread.isAlive())
            {
                try
                {
                    this.thread.join(3000);
                }
                catch (Exception e)
                {
                    System.out.println("threading headaches3");
                }
                finally
                {
                    if (!this.thread.isInterrupted())
                        this.thread.interrupt();
                    this.thread = null;
                }
            }
        }

        Task task = new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                Java8Lexer lexer = new Java8Lexer(CharStreams.fromString(fileContents));
                lexer.removeErrorListeners();

                CommonTokenStream tokens = new CommonTokenStream(lexer);

                Java8Parser parser = new Java8Parser(tokens);
                parser.removeErrorListeners();

                ParseTree tree = parser.compilationUnit();

                //walk through parse tree with listening for code structure elements
                CodeStructureListener codeStructureListener = new CodeStructureListener(newRoot, treeItemLineNumMap);

                walker.walk(codeStructureListener, tree);

                return null;
            }
        };

        this.thread = new Thread(task);
        this.thread.setDaemon(true);
        this.thread.start();
    }


    /**
     * Sets the currently displaying File TreeItem<String> View.
     *
     * @param root root node corresponding to currently displaying file
     */
    private void setRootNode(TreeItem<String> root)
    {
        this.treeView.setRoot(root);
        this.treeView.setShowRoot(false);
    }

    /**
     * Sets the currently displaying file to nothing.
     */
    public void resetRootNode()
    {
        this.setRootNode(null);
    }

    /**
     * Private helper class that listens for code structure declarations
     * (classes, fields, methods) during a parse tree walk and builds a
     * TreeView subtree representing the code structure.
     */
    private class CodeStructureListener extends Java8BaseListener
    {
        Image classPic;
        Image methodPic;
        Image fieldPic;
        private TreeItem<String> currentNode;
        private Map<TreeItem, Integer> treeItemIntegerMap;

        /**
         * creates a new CodeStructureListener that builds a subtree
         * from the given root TreeItem
         *
         * @param root root TreeItem to build subtree from
         */
        public CodeStructureListener(TreeItem<String> root, Map<TreeItem, Integer> treeItemIntegerMap)
        {
            this.currentNode = root;
            this.treeItemIntegerMap = treeItemIntegerMap;

            String path = "/proj9AbulhabFengMaoSavillo/resources/icons/";
            try
            {
                this.classPic = new Image(new FileInputStream(
                        getClass().getResource(path + "c.png").getFile()));
                this.methodPic = new Image(new FileInputStream(
                        getClass().getResource(path + "m.png").getFile()));
                this.fieldPic = new Image(new FileInputStream(
                        getClass().getResource(path + "f.png").getFile()));
            }
            catch (IOException e)
            {
                System.out.println("Error Loading Images");
            }
        }

        /**
         * Starts a new subtree for the class declaration entered
         */
        @Override
        public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx)
        {
            //get class name
            TerminalNode node = ctx.Identifier();
            if (node != null)
            {
                String className = node.getText();

                //add class to TreeView under the current class tree
                //set up the icon
                //store the line number of its declaration
                TreeItem<String> newNode = new TreeItem<>(className);
                newNode.setGraphic(new ImageView(this.classPic));
                newNode.setExpanded(true);
                this.currentNode.getChildren().add(newNode);
                this.currentNode = newNode; //move current node into new subtree
                this.treeItemIntegerMap.put(newNode, ctx.getStart().getLine());
            }
        }

        /**
         * ends the new subtree for the class declaration exited,
         * returns traversal to parent node
         */
        @Override
        public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx)
        {
            this.currentNode = this.currentNode.getParent(); //move current node back to parent
        }

        /**
         * adds a child node for the field entered under the TreeItem for the current class
         */
        @Override
        public void enterFieldDeclaration(Java8Parser.FieldDeclarationContext ctx)
        {
            //get field name
            TerminalNode node = ctx.variableDeclaratorList().variableDeclarator(0).variableDeclaratorId().Identifier();
            if (node != null)
            {
                String fieldName = node.getText();

                //add field to TreeView under the current class tree
                //set up the icon
                //store the line number of its declaration
                TreeItem<String> newNode = new TreeItem<>(fieldName);
                newNode.setGraphic(new ImageView(this.fieldPic));
                this.currentNode.getChildren().add(newNode);
                this.treeItemIntegerMap.put(newNode, ctx.getStart().getLine());
            }
        }

        /**
         * adds a child node for the method entered under the TreeItem for the current class
         */
        @Override
        public void enterMethodHeader(Java8Parser.MethodHeaderContext ctx)
        {
            //get method name
            TerminalNode nameNode = ctx.methodDeclarator().Identifier();
            if (nameNode != null)
            {
                String methodName = nameNode.getText();

                //add method to TreeView under the current class tree
                //set up the icon
                //store the line number of its declaration
                TreeItem<String> newNode = new TreeItem<>(methodName);
                newNode.setGraphic(new ImageView(this.methodPic));
                this.currentNode.getChildren().add(newNode);
                this.treeItemIntegerMap.put(newNode, ctx.getStart().getLine());
            }
        }
    }
}