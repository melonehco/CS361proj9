/*
 * File: ToolBarController.java
 * F18 CS361 Project 9
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 11/20/2018
 * This file contains the ToolBarController class, handling Toolbar related actions.
 */

package proj9AbulhabFengMaoSavillo.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import org.fxmisc.richtext.StyleClassedTextArea;
import javafx.event.Event;

import java.util.List;
import java.util.concurrent.*;
import java.io.*;

import javafx.concurrent.Task;
import javafx.concurrent.Service;

import proj9AbulhabFengMaoSavillo.ControllerErrorCreator;
import proj9AbulhabFengMaoSavillo.JavaCodeArea;
import proj9AbulhabFengMaoSavillo.bantam.lexer.Scanner;
import proj9AbulhabFengMaoSavillo.bantam.lexer.Token;
import proj9AbulhabFengMaoSavillo.bantam.util.Error;
import proj9AbulhabFengMaoSavillo.bantam.util.ErrorHandler;


/**
 * ToolbarController handles Toolbar related actions.
 *
 * @author Evan Savillo
 * @author Yi Feng
 * @author Zena Abulhab
 * @author Melody Mao
 */
public class ToolBarController
{
    /**
     * Console defined in Main.fxml
     */
    private StyleClassedTextArea console;
    /**
     * Process currently compiling or running a Java file
     */
    private Process currentProcess;
    /**
     * Thread representing the Java program input stream
     */
    private Thread inThread;
    /**
     * Thread representing the Java program output stream
     */
    private Thread outThread;
    /**
     * Mutex lock to control input and output threads' access to console
     */
    private Semaphore mutex;
    /**
     * The consoleLength of the output on the console
     */
    private int consoleLength;
    /**
     * The FileMenuController
     */
    private FileMenuController fileMenuController;

    private Thread thread;

    /**
     * Initializes the ToolBarController controller.
     * Sets the Semaphore, the CompileWorker and the CompileRunWorker.
     */
    public void initialize()
    {
        this.mutex = new Semaphore(1);
    }

    /**
     * Sets the console pane.
     *
     * @param console StyleClassedTextArea defined in Main.fxml
     */
    public void setConsole(StyleClassedTextArea console)
    {
        this.console = console;
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
     * handles clicking of the scan button
     */
    public void handleScanButtonAction(Event event, File file)
    {
        // user select cancel button
        if (this.fileMenuController.checkSaveBeforeCompile() == 2)
        {
            event.consume();
        }
        else
        {
            // This may or may not solve a hard-to-replicate bug.
            if (this.thread != null)
            {
                if (this.thread.isAlive())
                {
                    try
                    {
                        this.thread.join(5000);
                    }
                    catch (Exception e)
                    {
                        System.out.println("threading headaches1");
                    }
                    finally
                    {
                        if (!this.thread.isInterrupted())
                            this.thread.interrupt();
                        this.thread = null;
                    }
                }
            }

            Platform.runLater(() ->
                              {
                                  this.console.clear();
                                  consoleLength = 0;
                              });

            JavaCodeArea outputArea = requestAreaForOutput();
            Scanner scanner = new Scanner(file.getAbsolutePath(), new ErrorHandler());

            Task task = new Task()
            {
                @Override
                protected Object call() throws Exception
                {
                    Token currentToken = scanner.scan();
                    while (currentToken.kind != Token.Kind.EOF)
                    {
                        String s = currentToken.toString();
                        Platform.runLater(() -> outputArea.appendText(s + "\n"));
                        currentToken = scanner.scan();
                    }

                    outputArea.setEditable(true);

                    List<Error> errorList = scanner.getErrorList();
                    int errorCount = errorList.size();
                    if (errorCount == 0)
                    {
                        Platform.runLater(() -> console.appendText("No errors detected\n"));
                    }
                    else
                    {
                        errorList.forEach((error) ->
                                          {
                                              Platform.runLater(() -> console.appendText(error.toString() + "\n"));
                                          });
                        String msg = String.format("Found %d error(s)", errorCount);
                        Platform.runLater(() -> console.appendText(msg));
                    }

                    return null;
                }
            };

            this.thread = new Thread(task);
            this.thread.setDaemon(true);
            this.thread.start();
        }

    }

    private JavaCodeArea requestAreaForOutput()
    {
        return this.fileMenuController.giveNewCodeArea();
    }
}
