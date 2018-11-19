package proj9AbulhabFengMaoSavillo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class JavaTabPane extends TabPane {


    private ArrayList<Tab> tabList = new ArrayList<>();

    /**
     * Helper method to create a new tab.
     *
     * @param contentString the contentString being added into the styled code area; empty string if
     *                      creating an empty window
     * @param filename      the name of the file opened; "untitled" if creating an empty window
     * @param file          File opened; null if creating an empty window
     */
    public void createTab(String contentString, String filename, File file)
    {
        //this.javaTabPane.handleNewTab();
        JavaCodeArea newJavaCodeArea = new JavaCodeArea();
        newJavaCodeArea.appendText(contentString); //set to given contents


        Tab newTab = new JavaTab(newJavaCodeArea,file);

        //order is important
        this.getTabs().add(newTab);
        this.getSelectionModel().select(newTab);

        this.tabList.add(newTab);
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

    public ArrayList<Tab> getTabList() {
        return tabList;
    }
}
