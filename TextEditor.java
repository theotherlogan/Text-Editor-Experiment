/*
Logan White
CSC151, honors project
A text editor built using the JavaFx GUI and CSS for styling purposes.

Credit for the underlying menu animation structure goes to fabian, and can be found at the following link:
https://stackoverflow.com/questions/37234729/javafx-slide-out-menu

A big thank you to Ms. Gaines for helping me to learn and apply Java and OOP this semester and create one of my dream projects!
 */

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TextEditor extends Application {

    // instance variables
    // window controls
    private Stage window;
    private Pane root;
    private VBox menu;

    // text and file handling
    private TextArea text;
    private File file;
    private FileChooser select;
    private ComboBox fonts;
    private ComboBox sizes;

    // sizing variables
    private static final int HEIGHT = 700;
    private static final int WIDTH = HEIGHT;
    private static final int MENU_WIDTH = 200;

    // main method - launches the application
    public static void main(String [] args) { launch(args); }

    @Override
    // start method - runs the main body of the application
    public void start(Stage primaryStage) throws Exception {

        // establishing settings for the main stage (window) and menu
        window = primaryStage;
        window.setTitle("new file");
        // this pane is the stage that sits within the window
        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);
        // creating a vertical box layout to hold the menu
        menu = new VBox();
        menu.setId("menu"); // sets the ID for the stylesheet
        menu.prefHeightProperty().bind(root.heightProperty()); // binds the height of the menu to the window size
        menu.setPrefWidth(200); // sets width

        // initializes a file selector
        select = new FileChooser();

        // creates a new text area, establishes settings
        text = new TextArea();
        text.setWrapText(true);
        text.setId("text");
        // setting style and sizing for the text area
        text.getStylesheets().add(getClass().getResource("editorstyle.css").toExternalForm());
        text.setFont(new Font("Avenir", 14));
        text.prefHeightProperty().bind(root.heightProperty());
        text.prefWidthProperty().bind(root.widthProperty());

        // creating the menu items - see methods below
        createFileMenus();
        createFontMenu();
        createSizingMenu();
        menuStyling();

        // adding components to the root pane
        root.getChildren().addAll(text, menu);


        // creating a scene and adding it to the main window
        Scene scene = new Scene(root);
        window.setScene(scene); // adds the scene to the primary stage
        window.show(); // shows the window

    }

    // opening files, reading contents into text area
    private void openFile() throws FileNotFoundException {

        // filtering out all files that aren't .txt
        select.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        // saving the selected file into a file object
        file = select.showOpenDialog(null);

        //clears any previous text in the window
        text.clear();

        // reading the file into the text area
        Scanner fin = new Scanner(file);
        while (fin.hasNextLine())
            text.appendText(fin.nextLine() + "\n");

    }
    // saving files
    private void saveFile() throws IOException {
        // opens save window
        file = select.showSaveDialog(null);

        // if file is selected/named
        if(file != null){
            FileWriter fileWriter = new FileWriter(file); // new file writer
            fileWriter.write(text.getText()); // writes into file from text area
            fileWriter.close(); // closes the file
        }
    }

    // method to set title of window
    private void setWindowTitle() { window.setTitle(file.getName()); }

    // creating the file-based menu items
    private void createFileMenus() {
        // for opening files
        Button open = new Button("Open...");
        open.setOnAction( e -> {
            try {
                openFile(); // calls the open file method
                setWindowTitle(); // resets the window title
            } catch (FileNotFoundException ex) {
                System.out.println("error: file not found");
            }
        });
        // for saving files
        Button save = new Button("Save...");
        save.setOnAction( e -> {
            try {
                saveFile(); // calls the save file method
                setWindowTitle(); // resets the window title
            } catch (IOException ex) {
                System.out.println("error: cannot save file");
            }
        });
        // exits the program
        Button exit = new Button("Exit...");
        exit.setOnAction(e -> Platform.exit());

        // adds menu items to the menu pane
        menu.getChildren().addAll(open, save, exit);

    }

    // creates the font drop-down menu
    private void createFontMenu(){
        fonts = new ComboBox<>();

        // adding options to the menu
        fonts.getItems().addAll(
                "Arial",
                "Avenir",
                "Comic Sans MS",
                "Courier",
                "Times New Roman"
        );

        // sets the number of visible rows
        fonts.setVisibleRowCount(5);
        // sets default font
        fonts.setValue("Avenir");

        // action listener, changes the font on selection
        fonts.setOnAction(e -> {
            if (fonts.getValue() != null)
                text.setFont(new Font((String) fonts.getValue(), (int) sizes.getValue()));

        });

        // adding the submenu to the menu
        menu.getChildren().addAll(fonts);
    }

    // creates the drop-down menu for font sizing
    private void createSizingMenu(){
        sizes = new ComboBox<>();

        sizes.getItems().addAll(
                12,
                14,
                16,
                18,
                20
        );

        // adding options to the menu
        sizes.setVisibleRowCount(5);
        // sets default font size
        sizes.setValue(14);

        // action listener, changes the font on selection
        sizes.setOnAction(e -> {
            if (fonts.getValue() != null)
                text.setFont(new Font((String) fonts.getValue(), (int) sizes.getValue()));
        });

        // adding the submenu to the menu
        menu.getChildren().addAll(sizes);
    }

    // handles menu styling and sliding animation
    private void menuStyling() {
        menu.getStylesheets().add(getClass().getResource("menustyle.css").toExternalForm());
        menu.setTranslateX(10-MENU_WIDTH);

        TranslateTransition menuSlide = new TranslateTransition(Duration.millis(600), menu);

        menuSlide.setFromX(10-MENU_WIDTH);
        menuSlide.setToX(0);

        menu.setOnMouseEntered(evt -> {
            menuSlide.setRate(1);
            menuSlide.play();
        });
        menu.setOnMouseExited(evt -> {
            menuSlide.setRate(-1);
            menuSlide.play();
        });
    }
}