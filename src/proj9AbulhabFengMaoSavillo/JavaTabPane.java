/*
 * File: JavaTabPane
 * F18 CS361 Project 9
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 11/18/18
 */

package proj9AbulhabFengMaoSavillo;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.io.File;
import java.util.ArrayList;


public class JavaTabPane extends TabPane {

    private ArrayList<Tab> tabList;

    /**
     * constructor, initialize the tab list
     */
    public JavaTabPane(){
        this.tabList = new ArrayList<>();
    }
    /**
     * Create a new tab
     * @param contentString content of the new tab
     * @param file
     * @param handler
     * @param menu
     */
    public void createTab(String contentString, File file, EventHandler<Event> handler, ObservableList<MenuItem> menu)
    {

        JavaTab newTab = new JavaTab(contentString,file,handler,menu);

        this.getTabs().add(newTab);
        this.getSelectionModel().select(newTab);

        this.tabList.add(newTab);
    }

    /**
     * remove a tab from the tabpane and the tablist
     * @param tab
     */
    public void removeTab(JavaTab tab){
        this.tabList.remove(tab);
        this.getTabs().remove(tab);
    }


    /**
     * Returns the current tab
     * @return the current tab if there is one, return null otherwise.
     */
    public JavaTab getCurrentTab()
    {
        System.out.println(this.getSelectionModel().
                getSelectedItem());
        return (JavaTab)this.getSelectionModel().getSelectedItem();

    }


    /**
     * Returns the code area currently being viewed in the current tab
     * @return the JavaCodeArea object of the open tab if there is one,
     *         return null otherwise.
     */
    public JavaCodeArea getCurrentCodeArea()
    {
        JavaTab selectedTab = getCurrentTab();
        if (selectedTab != null)
        {
            return selectedTab.getJavaCodeArea();
        }
        else
            return null;
    }

    /**
     * Returns the file object in the current tab
     * @return the File object of the item selected in the tab pane if there is one,
     *          return null otherwise.
     */
    public File getCurrentFile()
    {
        JavaTab selectedTab = getCurrentTab();
        if (selectedTab != null)
        {
            return selectedTab.getFile();
        }
        else return null;
    }

    /**
     * getter of the tabList
     * @return tabList
     */
    public ArrayList<Tab> getTabList() {
        return tabList;
    }



}
