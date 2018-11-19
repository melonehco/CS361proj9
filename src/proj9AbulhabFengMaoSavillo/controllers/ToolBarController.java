/*
 * File: ToolBarController.java
 * F18 CS361 Project 7
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 10/27/2018
 * This file contains the ToolBarController class, handling Toolbar related actions.
 */

package proj9AbulhabFengMaoSavillo.controllers;

import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;
import javafx.event.Event;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.io.*;

import javafx.concurrent.Task;
import javafx.concurrent.Service;

import proj9AbulhabFengMaoSavillo.ControllerErrorCreator;
import proj9AbulhabFengMaoSavillo.bantam.lexer.Scanner;
import proj9AbulhabFengMaoSavillo.bantam.lexer.Token;
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
    /**
     * A CompileWorker object compiles a Java file in a separate thread.
     */
    private CompileWorker compileWorker;
    /**
     * A CompileRunWorker object compiles and runs a Java file in a separate thread.
     */
    private CompileRunWorker compileRunWorker;


    /**
     * scanner
     */
    private Scanner scanner;

    /**
     * Initializes the ToolBarController controller.
     * Sets the Semaphore, the CompileWorker and the CompileRunWorker.
     */
    public void initialize()
    {
        this.mutex = new Semaphore(1);
        this.compileWorker = new CompileWorker();
        this.compileRunWorker = new CompileRunWorker();
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
     * Gets the CompileWorker.
     *
     * @return CompileWorker
     */
    public CompileWorker getCompileWorker()
    {
        return this.compileWorker;
    }

    /**
     * Gets the CompileRunWorker.
     *
     * @return CompileRunWorker
     */
    public CompileRunWorker getCompileRunWorker()
    {
        return this.compileRunWorker;
    }


    /**
     * handler of the scan button
     */
    public void handleScanButtonAction(File file)
    {
        System.out.println("handleScanButtonAction");
        scanFile(file);
    }

    /**
     * helper function to scan the file
     *
     * @param file
     */
    private void scanFile(File file)
    {
        //TODO: print to our own console
        Thread scanThread = new Thread();

        scanThread = new Thread()
        {
            public void run()
            {
                try
                {
                    ArrayList<Token> tokenStream = new ArrayList<Token>();
                    ErrorHandler errorHandler = new ErrorHandler();
                    String filename = file.getAbsolutePath();
                    Scanner scanner = new Scanner(filename, errorHandler);

                    Token currentToken = scanner.scan();
                    while (currentToken.kind != Token.Kind.EOF)
                    {
                        tokenStream.add(currentToken);
                        currentToken = scanner.scan();
                    }

                    for (Token t : tokenStream)
                    {
                        System.out.println(t);
                        System.out.println("--------------------");
                    }
                }
                catch (Throwable e)
                {
                    Platform.runLater(() ->
                                      {
                                          // print stop message if other thread hasn't
                                          if (consoleLength == console.getLength())
                                          {
                                              console.appendText("\nScanner stopped unexpectedly\n");
                                              console.requestFollowCaret();
                                          }
                                      });
                }
            }
        };
        scanThread.start();

    }

    /**
     * Helper method for running Java Compiler.
     */
    private boolean compileJavaFile(File file)
    {
        try
        {
            Platform.runLater(() ->
                              {
                                  this.console.clear();
                                  this.consoleLength = 0;
                              });

            ProcessBuilder pb = new ProcessBuilder("javac", file.getAbsolutePath());
            this.currentProcess = pb.start();

            this.outputToConsole();

            // true if compiled without compile-time error, else false
            return this.currentProcess.waitFor() == 0;
        }
        catch (Throwable e)
        {
            Platform.runLater(() ->
                              {
                                  ControllerErrorCreator.createErrorDialog("File Compilation",
                                                                           "Error compiling.\nPlease try again with another valid Java File.");
                              });
            return false;
        }
    }

    /**
     * Helper method for getting program output
     */
    private void outputToConsole() throws java.io.IOException, java.lang.InterruptedException
    {
        InputStream stdout = this.currentProcess.getInputStream();
        InputStream stderr = this.currentProcess.getErrorStream();

        BufferedReader outputReader = new BufferedReader(new InputStreamReader(stdout));
        printOutput(outputReader);

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(stderr));
        printOutput(errorReader);
    }

    /**
     * Helper method for printing to console
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    private void printOutput(BufferedReader reader) throws java.io.IOException, java.lang.InterruptedException
    {
        // if the output stream is paused, signal the input thread
        if (!reader.ready())
        {
            this.mutex.release();
        }

        //TODO rename
        int intch;
        // read in program output one character at a time
        while ((intch = reader.read()) != -1)
        {
            this.mutex.tryAcquire();
            char ch = (char) intch;
            String out = Character.toString(ch);
            Platform.runLater(() ->
                              {
                                  // add output to console
                                  this.console.appendText(out);
                                  this.console.requestFollowCaret();
                              });
            // update console length tracker to include output character
            this.consoleLength++;

            // if the output stream is paused, signal the input thread
            if (!reader.ready())
            {
                this.mutex.release();
            }
            // wait for input thread to acquire mutex if necessary
            Thread.sleep(1);
        }
        this.mutex.release();
        reader.close();
    }

    /**
     * Helper method for running Java Program.
     */
    private boolean runJavaFile(File file)
    {
        try
        {
            Platform.runLater(() ->
                              {
                                  this.console.clear();
                                  consoleLength = 0;
                              });
            ProcessBuilder pb = new ProcessBuilder("java", file.getName().substring(0, file.getName().length() - 5));
            pb.directory(file.getParentFile());
            this.currentProcess = pb.start();

            // Start output and input in different threads to avoid deadlock
            this.outThread = new Thread()
            {
                public void run()
                {
                    try
                    {
                        // start output thread first
                        mutex.acquire();
                        outputToConsole();
                    }
                    catch (Throwable e)
                    {
                        Platform.runLater(() ->
                                          {
                                              // print stop message if other thread hasn't
                                              if (consoleLength == console.getLength())
                                              {
                                                  console.appendText("\nProgram exited unexpectedly\n");
                                                  console.requestFollowCaret();
                                              }
                                          });
                    }
                }
            };
            outThread.start();

            inThread = new Thread()
            {
                public void run()
                {
                    try
                    {
                        inputFromConsole();
                    }
                    catch (Throwable e)
                    {
                        Platform.runLater(() ->
                                          {
                                              // print stop message if other thread hasn't
                                              if (consoleLength == console.getLength())
                                              {
                                                  console.appendText("\nProgram exited unexpectedly\n");
                                                  console.requestFollowCaret();
                                              }
                                          });
                    }
                }
            };
            inThread.start();

            // true if ran without error, else false
            return currentProcess.waitFor() == 0;
        }
        catch (Throwable e)
        {
            Platform.runLater(() ->
                              {
                                  ControllerErrorCreator.createErrorDialog("File Running",
                                                                           "Error running " + file.getName() + ".");
                              });
            return false;
        }
    }

    /**
     * Helper method for getting program input
     */
    public void inputFromConsole() throws java.io.IOException, java.lang.InterruptedException
    {
        OutputStream stdin = currentProcess.getOutputStream();
        BufferedWriter inputWriter = new BufferedWriter(new OutputStreamWriter(stdin));

        while (currentProcess.isAlive())
        {
            // wait until signaled by output thread
            this.mutex.acquire();
            // write input to program
            writeInput(inputWriter);
            // signal output thread
            this.mutex.release();
            // wait for output to acquire mutex
            Thread.sleep(1);
        }
        inputWriter.close();
    }

    /**
     * Helper function to write user input
     */
    public void writeInput(BufferedWriter writer) throws java.io.IOException
    {
        // wait for user to input line of text
        while (true)
        {
            if (this.console.getLength() > this.consoleLength)
            {
                // check if user has hit enter
                if (this.console.getText().substring(this.consoleLength).contains("\n"))
                {
                    break;
                }
            }
        }
        // write user-entered text to program input
        writer.write(this.console.getText().substring(this.consoleLength));
        writer.flush();
        // update console length to include user input
        this.consoleLength = this.console.getLength();
    }

    /**
     * Handles the Compile button action.
     *
     * @param event Event object
     * @param file  the Selected file
     */
    public void handleCompileButtonAction(Event event, File file)
    {
        // user select cancel button
        if (this.fileMenuController.checkSaveBeforeCompile() == 2)
        {
            event.consume();
        }
        else
        {
            compileWorker.setFile(file);
            compileWorker.restart();
        }
    }

    /**
     * Handles the CompileRun button action.
     *
     * @param event Event object
     * @param file  the Selected file
     */
    public void handleCompileRunButtonAction(Event event, File file)
    {
        // user select cancel button
        if (this.fileMenuController.checkSaveBeforeCompile() == 2)
        {
            event.consume();
        }
        else
        {
            compileRunWorker.setFile(file);
            compileRunWorker.restart();
        }
    }

    /**
     * Handles the Stop button action.
     */
    public void handleStopButtonAction()
    {
        try
        {
            if (this.currentProcess.isAlive())
            {
                this.inThread.interrupt();
                this.outThread.interrupt();
                this.currentProcess.destroy();
            }
        }
        catch (Throwable e)
        {
            ControllerErrorCreator.createErrorDialog("Program Stop", "Error stopping the Java program.");
        }
    }

    /**
     * A CompileWorker subclass handling Java program compiling in a separated thread in the background.
     * CompileWorker extends the javafx Service class.
     */
    protected class CompileWorker extends Service<Boolean>
    {
        /**
         * the file to be compiled.
         */
        private File file;

        /**
         * Sets the selected file.
         *
         * @param file the file to be compiled.
         */
        private void setFile(File file)
        {
            this.file = file;
        }

        /**
         * Overrides the createTask method in Service class.
         * Compiles the file embedded in the selected tab, if appropriate.
         *
         * @return true if the program compiles successfully;
         * false otherwise.
         */
        @Override
        protected Task<Boolean> createTask()
        {
            return new Task<Boolean>()
            {
                /**
                 * Called when we execute the start() method of a CompileRunWorker object
                 * Compiles the file.
                 *
                 * @return true if the program compiles successfully;
                 *         false otherwise.
                 */
                @Override
                protected Boolean call()
                {
                    Boolean compileResult = compileJavaFile(file);
                    if (compileResult)
                    {
                        Platform.runLater(() -> console.appendText("Compilation was successful!\n"));
                    }
                    return compileResult;
                }
            };
        }
    }


    /**
     * A CompileRunWorker subclass handling Java program compiling and running in a separated thread in the background.
     * CompileWorker extends the javafx Service class.
     */
    protected class CompileRunWorker extends Service<Boolean>
    {
        /**
         * the file to be compiled.
         */
        private File file;

        /**
         * Sets the selected file.
         *
         * @param file the file to be compiled.
         */
        private void setFile(File file)
        {
            this.file = file;
        }

        /**
         * Overrides the createTask method in Service class.
         * Compiles and runs the file embedded in the selected tab, if appropriate.
         *
         * @return true if the program runs successfully;
         * false otherwise.
         */
        @Override
        protected Task<Boolean> createTask()
        {
            return new Task<Boolean>()
            {
                /**
                 * Called when we execute the start() method of a CompileRunWorker object.
                 * Compiles the file and runs it if compiles successfully.
                 *
                 * @return true if the program runs successfully;
                 *         false otherwise.
                 */
                @Override
                protected Boolean call()
                {
                    if (compileJavaFile(file))
                    {
                        return runJavaFile(file);
                    }
                    return false;
                }
            };
        }
    }
}
