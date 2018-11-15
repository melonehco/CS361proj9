package proj9AbulhabFengMaoSavillo;

import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Subclass of the JavaFX Tab class.
 */
public class JavaTab extends Tab{

    private JavaCodeArea javaCodeArea;
    private File file;


    public JavaTab(){
        this.javaCodeArea = new JavaCodeArea();
        this.setContent(new VirtualizedScrollPane<>(this.javaCodeArea));
        this.setTabName();
    }

    /**
     * Handles closing this tab through the [x] button
     */
    public void handleClose(){

    }

    /**
     * Checks if the text content within this tab should be saved.
     *
     * @param ifSaveEmptyFile boolean false if not to save the empty file; true if to save the empty file
     * @return true if the tab needs saving; false if the tab does not need saving.
     */
    public boolean needsSaving(boolean ifSaveEmptyFile)
    {
        // check whether the embedded text has been saved or not
        if (this.file == null)
        {
            // if the newly created file is empty, don't save
            if (!ifSaveEmptyFile)
            {
                if (javaCodeArea.getText().equals(""))
                {
                    return false;
                }
            }
            return true;
        }
        // check whether the saved file match the tab content or not
        else
        {
            return !this.fileContainsMatch(javaCodeArea, file);
        }
    }

    /**
     * Helper method to check if the content of the specified JavaCodeArea
     * matches the content of the specified File.
     *
     * @param javaCodeArea JavaCodeArea to compare with the the specified File
     * @param file         File to compare with the the specified JavaCodeArea
     * @return true if the content of the JavaCodeArea matches the content of the File; false if not
     */
    public boolean fileContainsMatch(JavaCodeArea javaCodeArea, File file)
    {
        String javaCodeAreaContent = javaCodeArea.getText();
        String fileContent = this.getFileContent(file);
        return javaCodeAreaContent.equals(fileContent);
    }

    /**
     * Helper method to get the text content of a specified file.
     *
     * @param file File to get the text content from
     * @return the text content of the specified file; null if an error occurs when reading the specified file.
     */
    private String getFileContent(File file)
    {
        try
        {
            return new String(Files.readAllBytes(Paths.get(file.toURI())));
        }
        catch (Exception ex)
        {
            this.createErrorDialog("Reading File", "Cannot read " + file.getName() + ".");
            return null;
        }
    }

    /**
     * Creates a error dialog displaying message of any error encountered.
     *
     * @param errorTitle  String of the error title
     * @param errorString String of error message
     */
    public void createErrorDialog(String errorTitle, String errorString)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorTitle + " Error");
        alert.setHeaderText("Error for " + errorTitle);
        alert.setContentText(errorString);
        alert.showAndWait();
    }

    /**
     * Sets the name of the tab to untitled if the file is new, or to the name of an existing file
     */
    private void setTabName()
    {
        if (file.getName() == null)
        {
            this.setText("Untitled");
        }
        else
        {
            this.setText(this.file.getName());
        }
    }
}
