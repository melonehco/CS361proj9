/*
 * File: Controller.java
 * F18 CS361 Project 7
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 11/03/18
 * This file contains the DirectoryViewController from another group's project
 */

package proj9AbulhabFengMaoSavillo.controllers;

import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import proj9AbulhabFengMaoSavillo.TabPaneContentGetters;

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
     * a HashMap mapping the tabs and the associated files
     */
    private Map<Tab, File> tabFileMap;
    /**
     * A HashMap mapping the TreeItems and associated files
     */
    private Map<TreeItem<String>, File> treeItemFileMap;
    /**
     * TabPane defined in Main.fxml
     */
    private TabPane tabPane;
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
     * Sets the tabFileMap.
     *
     * @param tabFileMap HashMap mapping the tabs and the associated files
     */
    public void setTabFileMap(Map<Tab, File> tabFileMap)
    {
        this.tabFileMap = tabFileMap;
    }

    /**
     * Sets the tabPane.
     *
     * @param tabPane TabPane
     */
    public void setTabPane(TabPane tabPane)
    {
        this.tabPane = tabPane;

        // add listener to tab selection to switch directories based on open file
        this.tabPane.getSelectionModel().selectedItemProperty().addListener(
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
    }

    /**
     * Adds the directory tree for the current file to the GUI
     */
    public void createDirectoryTree()
    {
        // capture current file
        File file = TabPaneContentGetters.getCurrentFile(this.tabPane, this.tabFileMap);
        // create the directory tree
        if (file != null)
        {
            this.treeView.setRoot(this.getNode(file.getParentFile()));
            this.treeView.getRoot().setExpanded(true);
        }
    }
}
