/*
 * File: DirectoryViewController.java
 * F18 CS361 Project 9
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 11/20/18
 * This file contains the DirectoryViewController from another group's project
 */

package proj9AbulhabFengMaoSavillo.controllers;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import proj9AbulhabFengMaoSavillo.JavaTabPane;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * This controller handles directory related actions.
 *
 * @author Douglas Abrams
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Matt Jones
 */
public class DirectoryViewController
{
    /**
     * the tree view representing the directory
     */
    private TreeView<String> treeView;
    /**
     * A HashMap mapping the TreeItems and associated files
     */
    private Map<TreeItem<String>, File> treeItemFileMap;
    /**
     * TabPane defined in Main.fxml
     */
    private JavaTabPane javaTabPane;
    /**
     * FileMenuController defined in main controller
     */
    private FileMenuController fileMenuController;

    /**
     * Sets the directory tree from Main.fxml
     *
     * @param treeView the directory tree
     */
    public void setTreeView(TreeView treeView)
    {
        this.treeView = treeView;
        this.treeItemFileMap = new HashMap<>();

        // add listener to listen for clicks in the directory tree
        EventHandler<MouseEvent> mouseEventHandle = this::handleDirectoryItemClicked;
        this.treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
    }

    /**
     * Event handler to open a file selected from the directory
     *
     * @param event a MouseEvent object
     */
    private void handleDirectoryItemClicked(MouseEvent event)
    {
        // only open file if double clicked
        if (event.getClickCount() == 2 && !event.isConsumed())
        {
            event.consume();
            TreeItem selectedItem = treeView.getSelectionModel().getSelectedItem();
            String fileName = (String) selectedItem.getValue();

            this.fileMenuController.openFile(this.treeItemFileMap.get(selectedItem));
        }
    }

    /**
     * Sets the tabPane.
     *
     * @param tabPane TabPane
     */
    public void setTabPane(JavaTabPane tabPane)
    {
        this.javaTabPane = tabPane;

        // add listener to tab selection to switch directories based on open file
        this.javaTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldTab, newTab) -> this.createDirectoryTree());
    }

    /**
     * Sets the FileMenuController.
     *
     * @param fileMenuController FileMenuController created in main Controller.
     */
    public void setFileMenuController(FileMenuController fileMenuController)
    {
        this.fileMenuController = fileMenuController;
    }

    /**
     * Returns the directory tree for the given file
     *
     * @param file the file
     * @return the root TreeItem of the tree
     */
    private TreeItem<String> getNode(File file)
    {
        // create root, which is returned at the end
        TreeItem<String> root = new TreeItem<>(file.getName());
        treeItemFileMap.put(root, file);

        for (File f : Objects.requireNonNull(file.listFiles()))
        {
            if (f.isDirectory())
            {
                // recursively traverse file directory
                root.getChildren().add(getNode(f));
            }
            else
            {
                TreeItem<String> leaf = new TreeItem<>(f.getName());
                root.getChildren().add(leaf);
                treeItemFileMap.put(leaf, f);
            }
        }
        return root;

//        Task task = new Task()
//        {
//            @Override
//            protected Object call() throws Exception
//            {
//                Java8Lexer lexer = new Java8Lexer(CharStreams.fromString(fileContents));
//                lexer.removeErrorListeners();
//
//                CommonTokenStream tokens = new CommonTokenStream(lexer);
//
//                Java8Parser parser = new Java8Parser(tokens);
//                parser.removeErrorListeners();
//
//                ParseTree tree = parser.compilationUnit();
//
//                //walk through parse tree with listening for code structure elements
//                CodeStructureListener codeStructureListener = new CodeStructureListener(newRoot, treeItemLineNumMap);
//
//                walker.walk(codeStructureListener, tree);
//
//                return null;
//            }
//        };
//
//        Thread newThread = new Thread(task);
//        newThread.setDaemon(true);
//        newThread.start();
    }

    /**
     * Adds the directory tree for the current file to the GUI
     */
    public void createDirectoryTree()
    {
        // capture current file
        File file = this.javaTabPane.getCurrentFile();
        // create the directory tree
        if (file != null)
        {
            this.treeView.setRoot(this.getNode(file.getParentFile()));
            this.treeView.getRoot().setExpanded(true);
        }
    }
}
