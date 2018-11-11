/*
 * File: PreferencesMenuController.java
 * F18 CS361 Project 7
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Implemented using code from AhnDeGrawHangSlagerproj6
 * Date: 10/31/2018
 * This file contains the Preferences Menu controller class, handling actions evoked by the Preferences Menu,
 * namely, changing the color theme of the IDE
 */
package proj9AbulhabFengMaoSavillo.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;

import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import proj9AbulhabFengMaoSavillo.Main;

/**
 * Preferences Menu controller handles actions evoked by the Main window.
 * Used for changing the color theme of the IDE.
 *
 * @author Zena Abulhab
 * @author Yi Feng
 * @author Melody Mao
 * @author Evan Savillo
 */
public class PreferencesMenuController {

    /**
     * Reference to the VBox that Controller passes in
     */
    private VBox vBox;

    /**
     * The menu item that was last pressed, representing which theme the IDE was switched to
     */
    private MenuItem lastPressedMenuItem;

    /**
     * Maps string names for colors to the Colors assigned to them in our CSS
     */
    private HashMap<String, Color> colorNamesToColors;

    /**
     * ObservableLists used to create the options in the color-chooser windows
     */
    private ObservableList<String> keywordColorChoices;
    private ObservableList<String> parenthesisColorChoices;
    private ObservableList<String> strColorChoices;
    private ObservableList<String> intColorChoices;
    
    /**
     * Maps the string for a color name to the filename for the associated CSS
     * for the particular styling class
     */
    private HashMap<String, String> keywordColorCSSNames;
    private HashMap<String, String> parenthesisColorCSSNames;
    private HashMap<String, String> strColorCSSNames;
    private HashMap<String, String> intColorCSSNames;
    
    
    /**
     * Constructor in which we initialize the last pressed menu item and disable it; in our case,
     * we want it to be the light theme menu item because we start off with this as the default
     */
    public PreferencesMenuController(MenuItem initialSelectedMenuItem){
        initialSelectedMenuItem.setDisable(true);
        this.lastPressedMenuItem = initialSelectedMenuItem;
        this.setupColorChoices();
    }

    /**
     * Sets the vBox field of this controller
     * @param vBox the VBox to store
     */
    public void setVBox(VBox vBox){
        this.vBox = vBox;
    }

    /**
     * Sets up the fields storing the color preference choices and their CSS
     */
    private void setupColorChoices()
    {
    	//set up color map for setting color-chooser window colors
    	this.colorNamesToColors = new HashMap<String, Color>();
    	this.colorNamesToColors.put("Purple", Color.PURPLE);
    	this.colorNamesToColors.put("Black", Color.BLACK);
    	this.colorNamesToColors.put("Blue", Color.ROYALBLUE);
    	this.colorNamesToColors.put("Teal", Color.TEAL);
    	this.colorNamesToColors.put("Pink", Color.ORCHID);
    	this.colorNamesToColors.put("Orange", Color.ORANGE);
    	this.colorNamesToColors.put("Red", Color.FIREBRICK);
    	this.colorNamesToColors.put("Grey", Color.GREY);
    	this.colorNamesToColors.put("SkyBlue", Color.SKYBLUE);

    	//--------------------- colors for keywords --------------------

    	this.keywordColorChoices = FXCollections.observableArrayList(
                "Black","Blue","Teal","Pink","Orange", "Red");

    	//put CSS strings into map with associated colors
    	this.keywordColorCSSNames = new HashMap<String, String>();
    	for ( String colorName: this.keywordColorChoices )
    	{
            String colorCSSPath = "/proj9AbulhabFengMaoSavillo/resources/KeywordColorCSS/Keyword" + colorName + ".css";
    		String colorCSS = getClass().getResource(colorCSSPath).toExternalForm();
    		this.keywordColorCSSNames.put(colorName, colorCSS);
    	}

    	//add in default color by itself (because it doesn't have a CSS file)
    	this.keywordColorChoices.add(0, "Purple");

        //------------------ colors for parentheses etc ----------------

        this.parenthesisColorChoices = FXCollections.observableArrayList(
                "Black","Blue","Grey","Pink","Orange", "Red");

    	//put CSS strings into map with associated colors
    	this.parenthesisColorCSSNames = new HashMap<String, String>();
    	for ( String colorName: this.parenthesisColorChoices )
    	{
            String colorCSSPath = "/proj9AbulhabFengMaoSavillo/resources/ParenColorCSS/Paren" + colorName + ".css";
    		String colorCSS = getClass().getResource(colorCSSPath).toExternalForm();
    		this.parenthesisColorCSSNames.put(colorName, colorCSS);
    	}

        //add in default color by itself (because it doesn't have a CSS file)
    	this.parenthesisColorChoices.add(0, "Teal");

        //---------------------- colors for strings ---------------------

        this.strColorChoices = FXCollections.observableArrayList(
                "Black","Teal","SkyBlue","Pink","Orange", "Red");

        //put CSS strings into map with associated colors
    	this.strColorCSSNames = new HashMap<String, String>();
    	for ( String colorName: this.strColorChoices )
    	{
            String colorCSSPath = "/proj9AbulhabFengMaoSavillo/resources/StrColorCSS/StrColor" + colorName + ".css";
    		String colorCSS = getClass().getResource(colorCSSPath).toExternalForm();
    		this.strColorCSSNames.put(colorName, colorCSS);
    	}

        //add in default color by itself (because it doesn't have a CSS file)
    	this.strColorChoices.add(0, "Blue");

        //----------------------- colors for ints -----------------------

        this.intColorChoices = FXCollections.observableArrayList(
                "Black","Blue","SkyBlue","Pink","Orange", "Teal");

        //put CSS strings into map with associated colors
    	this.intColorCSSNames = new HashMap<String, String>();
    	for ( String colorName: this.intColorChoices )
    	{
            String colorCSSPath = "/proj9AbulhabFengMaoSavillo/resources/IntColorCSS/Int" + colorName + ".css";
    		String colorCSS = getClass().getResource(colorCSSPath).toExternalForm();
    		this.intColorCSSNames.put(colorName, colorCSS);
    	}

        //add in default color by itself (because it doesn't have a CSS file)
    	this.intColorChoices.add(0, "Red");


    }

    /**
     * Get the action that the user chose and execute the correct methos
     * @param event
     */
    public void handlePreferencesMenuAction(ActionEvent event){
        lastPressedMenuItem.setDisable(false); // enable the former last pressed menu item
        lastPressedMenuItem = (MenuItem) event.getTarget();
        switch (lastPressedMenuItem.getId()) {
            case "lightThemeMenuItem":
                handleNormalMode();
                break;
            case "darkThemeMenuItem":
                loadAlternateTheme("/proj9AbulhabFengMaoSavillo/resources/DarkTheme.css");
                break;
            case "halloweenThemeMenuItem":
                loadAlternateTheme("/proj9AbulhabFengMaoSavillo/resources/HallowTheme.css");
                break;
            default:
        }
        lastPressedMenuItem.setDisable(true); // disable the menu item we just pressed
    }

    /**
     * Helper method to use a non-default theme
     * @param themeCSS which theme to change to
     */
    private void loadAlternateTheme(String themeCSS){
        if(this.vBox.getStylesheets().size() > 1){
            this.vBox.getStylesheets().remove(this.vBox.getStylesheets().size()-1);
        }
        this.vBox.getStylesheets().add(themeCSS);
    }

    /**
     * Helper method to reset the IDE color theme, removing any alternate styling
     */
    @FXML
    public void handleNormalMode(){
        this.vBox.getStylesheets().remove(vBox.getStylesheets().size()-1);
    }

    /**
     * Handles Keyword Color menu item action.
     * Pops up a window displaying the color preference for the keywords.
     * By selecting a color from the drop-down menu, the color of the keywords will change accordingly.
     */
    public void handleKeywordColorAction(){
        Stage keywordColorWin = new Stage();
        keywordColorWin.setTitle("Keyword Color");

        Parent root = Main.getParentRoot();

        VBox keywordColorRoot = new VBox();
        keywordColorRoot.setAlignment(Pos.CENTER);
        keywordColorRoot.setSpacing(10);

        final Rectangle rect = new Rectangle(75,75, Color.PURPLE);

        ChoiceBox<String> keywordColorCB = new ChoiceBox<String>(this.keywordColorChoices);
        keywordColorCB.setValue("Purple");

        Text message = new Text("Keyword Color");
        message.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        message.setFill(Color.PURPLE);

        keywordColorCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	String selectedColor = keywordColorCB.getValue();
            	
            	//update color-chooser coloring
            	Color newColor = colorNamesToColors.get(selectedColor);
            	rect.setFill(newColor);
            	message.setFill(newColor);
            	
            	//remove previous styling
            	root.getStylesheets().removeAll(keywordColorCSSNames.values());
            	
            	//add new styling for non-default colors
            	if (!selectedColor.equals("Purple")){
            		root.getStylesheets().add(keywordColorCSSNames.get(selectedColor));
                }
            }
        });

        keywordColorRoot.getChildren().addAll(message,rect, keywordColorCB);
        Scene keywordColorScene = new Scene(keywordColorRoot, 200,200);
        keywordColorWin.setScene(keywordColorScene);
        keywordColorWin.show();

    }

    /**
     * Handles Parentheses/Brackets Color menu item action.
     */
    public void handleParenColorAction(){
        Stage parenColorWin = new Stage();
        parenColorWin.setTitle("Parentheses/Brackets Color");

        Parent root = Main.getParentRoot();

        VBox parenColorRoot = new VBox();
        parenColorRoot.setAlignment(Pos.CENTER);
        parenColorRoot.setSpacing(10);

        final Rectangle rect = new Rectangle(75,75, Color.TEAL);

        ChoiceBox<String> parenColorCB = new ChoiceBox<String>(this.parenthesisColorChoices);
        parenColorCB.setValue("Teal");

        Text message = new Text("Parentheses/Brackets Color");
        message.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        message.setFill(Color.TEAL);

        parenColorCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	String selectedColor = parenColorCB.getValue();
            	
            	//update color-chooser coloring
            	Color newColor = colorNamesToColors.get(selectedColor);
            	rect.setFill(newColor);
            	message.setFill(newColor);
            	
            	//remove previous styling
            	root.getStylesheets().removeAll(parenthesisColorCSSNames.values());
            	
            	//add new styling for non-default colors
            	if (!selectedColor.equals("Teal")){
            		root.getStylesheets().add(parenthesisColorCSSNames.get(selectedColor));
                }
            }
        });

        parenColorRoot.getChildren().addAll(message,rect, parenColorCB);
        Scene keywordColorScene = new Scene(parenColorRoot, 230,200);
        parenColorWin.setScene(keywordColorScene);
        parenColorWin.show();
    }


    /**
     * Handles String Color menu item action.
     */
    public void handleStrColorAction(){
        Stage strColorWin = new Stage();
        strColorWin.setTitle("String Color");

        Parent root = Main.getParentRoot();

        VBox strColorRoot = new VBox();
        strColorRoot.setAlignment(Pos.CENTER);
        strColorRoot.setSpacing(10);

        final Rectangle rect = new Rectangle(75,75, Color.BLUE);

        ChoiceBox<String> strColorCB = new ChoiceBox<String>(this.strColorChoices);
        strColorCB.setValue("Blue");

        Text message = new Text("String Color");
        message.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        message.setFill(Color.BLUE);

        strColorCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	String selectedColor = strColorCB.getValue();
            	
            	//update color-chooser coloring
            	Color newColor = colorNamesToColors.get(selectedColor);
            	rect.setFill(newColor);
            	message.setFill(newColor);
            	
            	//remove previous styling
            	root.getStylesheets().removeAll(strColorCSSNames.values());
            	
            	//add new styling for non-default colors
            	if (!selectedColor.equals("Blue")){
            		root.getStylesheets().add(strColorCSSNames.get(selectedColor));
                }
            }
        });

        strColorRoot.getChildren().addAll(message,rect, strColorCB);
        Scene keywordColorScene = new Scene(strColorRoot, 230,200);
        strColorWin.setScene(keywordColorScene);
        strColorWin.show();
    }


    /**
     * Handles int Color menu item action.
     */
    public void handleIntColorAction(){
        Stage intColorWin = new Stage();
        intColorWin.setTitle("int Color");

        Parent root = Main.getParentRoot();

        VBox intColorRoot = new VBox();
        intColorRoot.setAlignment(Pos.CENTER);
        intColorRoot.setSpacing(10);

        final Rectangle rect = new Rectangle(75,75, Color.FIREBRICK);

        ChoiceBox<String> intColorCB = new ChoiceBox<String>(this.intColorChoices);
        intColorCB.setValue("Red");

        Text message = new Text("Integer(int) Color");
        message.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        message.setFill(Color.FIREBRICK);

        intColorCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String selectedColor = intColorCB.getValue();
            	
            	//update color-chooser coloring
            	Color newColor = colorNamesToColors.get(selectedColor);
            	rect.setFill(newColor);
            	message.setFill(newColor);
            	
            	//remove previous styling
            	root.getStylesheets().removeAll(intColorCSSNames.values());
            	
            	//add new styling for non-default colors
            	if (!selectedColor.equals("Red")){
            		root.getStylesheets().add(intColorCSSNames.get(selectedColor));
                }
            }
        });

        intColorRoot.getChildren().addAll(message,rect, intColorCB);
        Scene keywordColorScene = new Scene(intColorRoot, 230,200);
        intColorWin.setScene(keywordColorScene);
        intColorWin.show();
    }
}
