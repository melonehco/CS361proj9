package proj9AbulhabFengMaoSavillo;

import javafx.scene.control.Tab;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;

/**
 * Subclass of the JavaFX Tab class.
 */
public class JavaTab extends Tab{

    private JavaCodeArea javaCodeArea;
    private File file;

    public JavaTab(){
        this.javaCodeArea = new JavaCodeArea();
        this.setContent(new VirtualizedScrollPane<>(this.javaCodeArea));
        this.setText();
    }

    /**
     * Handles closing this tab through the [x] button
     */
    public void handleClose(){

    }
}
