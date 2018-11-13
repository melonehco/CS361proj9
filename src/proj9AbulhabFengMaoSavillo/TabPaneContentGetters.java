package proj9AbulhabFengMaoSavillo;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import proj9AbulhabFengMaoSavillo.JavaCodeArea;

import java.io.File;
import java.util.Map;
/**
 * Class containing static getters of the current tab,
 * the code area in the current tab,
 * and the file in the current tab.
 *
 * @author Zena Abulhab
 * @author Yi Feng
 * @author Melody Mao
 * @author Evan Savillo
 */
public class TabPaneContentGetters {


    /**
     * Returns the current tab
     * @param tabPane
     * @return the current tab if there is one, return null otherwise.
     */
    public static Tab getCurrentTab(TabPane tabPane)
    {
        return tabPane.getSelectionModel().getSelectedItem();

    }


    /**
     * Returns the code area currently being viewed in the current tab
     *
     * @param tabPane
     * @return the JavaCodeArea object of the open tab if there is one,
     *         return null otherwise.
     */
    public static JavaCodeArea getCurrentCodeArea(TabPane tabPane)
    {
        Tab selectedTab = getCurrentTab(tabPane);
        if (selectedTab != null)
        {
            return (JavaCodeArea) ((VirtualizedScrollPane) selectedTab.getContent()).getContent();
        }
        else
            return null;
    }

    /**
     * Returns the file object in the current tab
     *
     * @param tabPane
     * @param tabFileMap the hashmap of tabs and files
     * @return the File object of the item selected in the tab pane if there is one,
     *          return null otherwise.
     */
    public static File getCurrentFile(TabPane tabPane, Map<Tab, File> tabFileMap)
    {
        Tab selectedTab = getCurrentTab(tabPane);
        if (selectedTab != null)
        {
            return tabFileMap.get(selectedTab);
        }
        else return null;
    }


}
