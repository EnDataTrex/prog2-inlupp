package se.su.inlupp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Gui extends Application {
  Image image;
  String fileName;
  Stage stage;
  Graph<String> graph;
  Graph<Location> locationGraph;
  GridPane grid;
  HBox hbox;
  StackPane stack;
  Pane pane;
  BorderPane root;
  boolean saveStatus = false;
  VBox vboxLeft;
  VBox vboxRight;
  ArrayList<String> elements;
  Circle[] markedPlaces;
  //Gör det tydligt om programmet är sparat eller inte

  public void start(Stage stage) {

    this.stage = stage;
    emptyGraphs();

    root = new BorderPane(); //roten
    grid = new GridPane();
    hbox = new HBox();
    stack = new StackPane();// till för "övriga" knappar
    pane = new Pane();
    vboxLeft = new VBox();
    vboxRight = new VBox();

    root.setTop(grid);
    root.setCenter(stack);

    stack.getChildren().add(pane);

    MenuButton menuBar = new MenuButton("File"); //till för menyknappar

    Label newMapLabel = new Label("New Map");
    newMapLabel.setPadding(new Insets(1, 30, 1, 1));
    newMapLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    newMapLabel.setOnMouseClicked(event -> {
      newMap();
      saveStatus = true;
    });
    CustomMenuItem menuItemNewMap = new CustomMenuItem(newMapLabel);
    menuBar.getItems().add(menuItemNewMap);
    //-------------------------------------------------------------------------------//
    /* Sätt bilen till fixed, och se till att fönstrets minimi storlek är anpassad till den så
    * den alltid är synlig, men se också till att weight/alignment är anpassat till resten av fönstret
    * TODO KOLLA UPP VILKA KOMMANDON SOM GÖR PADDING TRANSFORMATIVT*/
    Label openLabel = new Label("Open");
    openLabel.setPadding(new Insets(1, 30, 1, 1));
    openLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    openLabel.setOnMouseClicked(event -> {
      open();
      saveStatus = true;
    });
    CustomMenuItem menuItemOpen = new CustomMenuItem(openLabel);
    menuBar.getItems().add(menuItemOpen);
    //-------------------------------------------------------------------------------//
    Label saveLabel = new Label("Save");
    saveLabel.setPadding(new Insets(1, 30, 1, 1));
    saveLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    saveLabel.setOnMouseClicked(event -> {
        save();
        saveStatus = false;
      });
    CustomMenuItem menuItemSave = new CustomMenuItem(saveLabel);
    menuBar.getItems().add(menuItemSave);

    Label saveImageLabel = new Label("Save Image");
    saveImageLabel.setPadding(new Insets(1, 30, 1, 1));
    saveImageLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    saveImageLabel.setOnMouseClicked(event -> {
      saveImage();
    });
    CustomMenuItem menuItemSaveImage = new CustomMenuItem(saveImageLabel);
    menuBar.getItems().add(menuItemSaveImage);

    Label exitLabel = new Label("Exit");
    exitLabel.setPadding(new Insets(1, 30, 1, 1));
    exitLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    exitLabel.setOnMouseClicked(event -> {
      exitProgram(); //Kallar på metoden som checkar vilkoren för att stänga ner programmet
      event.consume(); //Om vilkoren inte uppnåtts (aka) om man klickar på cancel så stängs fönstret ner

    });
    CustomMenuItem menuItemExit = new CustomMenuItem(exitLabel);
    menuBar.getItems().add(menuItemExit);

    //två problem:
    //1. klickytan är begränsad till texten, vilket skapar en inte helt användarvanlig funktion,
    //(skulle typ vilja att hela "boxen" där texten står ingår i klickytan)
    //2. skulle kunna vara så att detta inte längre är användbart när vi vill lägga till funktioner,
    //för varje label, vet inte kanske går att göra, kan också vara så att du redan har tänkt,
    //på detta sättet och kommit fram till samma problem:)
    /*
    ArrayList<String> labels = new ArrayList<>(Arrays.asList("New Map", "Open", "Save", "Save Image", "Exit"));
    for (String label : labels) {
      Label l = new Label(label);
      l.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
      l.setOnMouseClicked(event -> {l.setText("I clicked " + label);});
      CustomMenuItem customMenuItem1 = new CustomMenuItem(l);
      menuBar.getItems().add(customMenuItem1);
    }
     */

    grid.setBackground(Background.fill(Color.LIGHTGREY)); //sätter bakgrund
    grid.add(menuBar, 0, 0); //lägger till menubar i roten
    grid.add(hbox, 0,1); //lägger till hbox i roten
    grid.setPadding(new Insets(10)); //sätter padding aka utrymme mellan rotens innehåll och kanter

    hbox.setBackground(Background.fill(Color.LIGHTBLUE)); //sätter "övriga knappars" bakgrund till blå
    hbox.setAlignment(Pos.CENTER); //centrerar de "övriga knapparna"
    hbox.setSpacing(3); //Sätter ett utrymme runt knapparna

    GridPane.setMargin(menuBar, new Insets(0,0,10,0)); //sätter utrymme mellan boxar i griden

    ColumnConstraints column = new ColumnConstraints(); //skapar kolumnernas constraints
    column.setHgrow(Priority.ALWAYS); //Låter kolumnens längd växa dynamiskt
    grid.getColumnConstraints().add(column); //Lägger till det i roten

    elements = new ArrayList<>(Arrays.asList("Find Path", "Show Connection", "New Place", "New Connection", "Change Connection")); //Skapar lista med övriga knappar
    for (String element : elements) {
      Button button = new Button(element);//skapar ny knapp
      button.setBackground(Background.fill(Color.FLORALWHITE)); //sätter färg
      hbox.getChildren().add(button); //lägger till den som ett barn i hbox
    }

    /*----------------------------------------------------------------------------------------------*/
    //newMap
    hbox.getChildren().get(0).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.LIGHTBLUE));
      root.setBackground(Background.fill(Color.LIGHTBLUE));
    });

    //Open
    hbox.getChildren().get(1).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.LIGHTBLUE));
      root.setBackground(Background.fill(Color.LIGHTBLUE));
    });

    //Save
    hbox.getChildren().get(2).onMouseClickedProperty().setValue(event -> {
      root.setBackground(Background.fill(Color.YELLOW));
      //TODO kollar om filename inte är null så att det finns en bakgrundsbild?
      if (fileName != null) {
        newPlace();
      }
    });

    //SaveImage
    hbox.getChildren().get(3).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.PURPLE));
      root.setBackground(Background.fill(Color.PURPLE));
      newConnection();
    });

    //Exit
    hbox.getChildren().get(4).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.BLACK));
      root.setBackground(Background.fill(Color.BLACK));

    });

    //TODO nu ligger klick på stack, pane funkar inte
    //kan bero på att vi sätter den till null new place

    markPlace();

    stage.setOnCloseRequest(event -> {
      //TODO försökte med att använda exitprogrammetoden men det funkar inte riktigt
      if (saveStatus) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have unsaved changes", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Unsaved changes");
        alert.setHeaderText("You have unsaved changes");
        alert.setContentText("Are you sure you want to exit?");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
          Platform.exit();
        } else {
          event.consume();
        }
      }
    });

    /*-----------------------------------------------------------------------------------*/

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    //ladda testfall
    //testFall();
  }

  private void open() {
    try{
      if (!saveStatus) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        emptyGraphs();

        fileName = bufferedReader.readLine();
        image = new Image(fileName, false);
        setBackground(image);
        setStageSize();

        String line = bufferedReader.readLine();
        String[] objects = line.split(";");
        for(int i = 0; i < objects.length; i+=3) {
          String name = objects[i];
          double x = Double.parseDouble(objects[i+1]);
          double y = Double.parseDouble(objects[i+2]);
          Location location = new Location(name, x, y);
          locationGraph.add(location);

          //skulle kunna göra en metod för att måla ut punkter då vi ockdå gör den i new place
          Circle circle = new Circle(location.getX(), location.getY(), 7, Color.BLUE);
          pane.getChildren().add(circle);
        }

        while((line = bufferedReader.readLine()) != null) {
          objects = line.split(";");
          String from = objects[0];
          String to = objects[1];
          String edge = objects[2];
          int weight = Integer.parseInt(objects[3]);
          if(graph.getEdgeBetween(from, to) == null) {
            graph.connect(from, to, edge, weight);

            //TODO när vi gjort connection, lägg till linjer mellan cirklar
          }
        }
      } else{
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have unsaved changes", ButtonType.OK, ButtonType.CANCEL);
          alert.showAndWait();
          if(alert.getResult() == ButtonType.OK) {
            emptyGraphs();
            saveStatus = false;
            open();
          } else if (alert.getResult() == ButtonType.CANCEL) {
            alert.close();
          }
      }
    }catch (IOException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error " + e.getMessage());
      alert.showAndWait();
    }
  }

  private void save() {
    try{
      if(image != null){
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showSaveDialog(stage);

      FileWriter fileWriter = new FileWriter(file);
      PrintWriter printWriter = new PrintWriter(fileWriter);

      printWriter.println(fileName);

      for(Location l : locationGraph.getNodes()) {
        if(!locationGraph.getNodes().isEmpty()){
          String locationWithFormat = "";
          locationWithFormat = locationWithFormat + l.getName() + ";" + l.getX() + ";" + l.getY() + ";";
          printWriter.write(locationWithFormat);
          //TODO om det är den sista i listan ska inget ; skrivas ut
        }
      }
      printWriter.println();
      for(String node : graph.getNodes()) {
        String nodeWithFormat = "";
        nodeWithFormat = nodeWithFormat + node + ";";
        if(!graph.getNodes().isEmpty()){
          for(Edge<String> edge : graph.getEdgesFrom(node)) {
            nodeWithFormat = nodeWithFormat + edge.getDestination() + ";" + edge.getName() + ";" + edge.getWeight();
            printWriter.println(nodeWithFormat);
          }
        }
      }
      printWriter.close();
      fileWriter.close();
      } else{
        Alert alert = new Alert(Alert.AlertType.ERROR, "No image Error");
        alert.showAndWait();
      }
    } catch (FileNotFoundException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "File not found!");
      alert.showAndWait();
    } catch (IOException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error " + e.getMessage());
      alert.showAndWait();
    }
  }

  private void newMap() {
    FileChooser filechooser = new FileChooser();
    File file = filechooser.showOpenDialog(stage);

    fileName = file.toURI().toString();
    image = new Image(fileName, false);

    setBackground(image);

    setStageSize();
  }

  private void setStageSize(){
    pane.setMinSize(image.getWidth(), image.getHeight());
    pane.setMaxSize(image.getWidth(), image.getHeight());

    //hittade inget annat sätt än att hårdkoda med 50
    stage.setMinHeight(image.getHeight() + grid.getHeight() + 50);
    stage.setMinWidth(image.getWidth() + 50);

    //stage.sizeToScene();
  }

  //kändes onödig när jag ändå flyttade ut den koden över  setstagestage så la till de två raderna,
  //kod där istället
  //private void changeWindowSize(double width, double height) {
    //stack.setMinHeight(height);
    //stack.setMinWidth(width);
  //}

  private void setBackground(Image image) {
    pane.getChildren().clear();
    BackgroundImage backgroundImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            BackgroundSize.DEFAULT
    );
    //changeWindowSize(image.getWidth(), image.getHeight());
    setStageSize();
    stack.setBackground(new Background(backgroundImage));
  }

  private void saveImage() {
    try {
      WritableImage image = stack.snapshot(null, null);
      BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
      ImageIO.write(bufferedImage, "png", new File("capture.png"));
    } catch (IOException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error " + e.getMessage());
      alert.showAndWait();
    }
  }

  //Kollar först ifall savestatus är false eller true. Om det är false, skapa en popop med 2 alternativ.
  //Om man klickar på ok så stängs programmet ner, om man klickar på cancel så stängs programmet inte ner
  private void exitProgram() {
    if (saveStatus) {
      Alert notSavedAlert = new Alert(Alert.AlertType.CONFIRMATION);
      notSavedAlert.setTitle("Unsaved Changes");
      notSavedAlert.setHeaderText("You have unsaved changes");
      notSavedAlert.setContentText("Are you sure you want to exit?");

      ButtonType okButton = new ButtonType("OK");
      ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

      notSavedAlert.getButtonTypes().setAll(cancelButton, okButton);

      Optional<ButtonType> result = notSavedAlert.showAndWait();
      if (result.isPresent() && result.get() == okButton) {
        Platform.exit();
      }
    } else {
      Platform.exit();
    }
  }

  private void newPlace(){
    root.setCursor(Cursor.CROSSHAIR);

    for (String e : elements) {
      hbox.getChildren().get(elements.indexOf(e)).setDisable(true);
    }

    pane.setOnMouseClicked(mouseEvent -> {

      TextInputDialog newPlaceDialog = new TextInputDialog();
      newPlaceDialog.setTitle("Name");
      newPlaceDialog.setHeaderText("Name of place");

      Optional<String> result = newPlaceDialog.showAndWait();
      String name = newPlaceDialog.getEditor().getText();

      if(result.isPresent() && !name.isEmpty()){
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        Circle place = new Circle(x, y,7, Color.BLUE);
        pane.getChildren().add(place);

        //Lägger till punkten i listan
        Location location = new Location(name, x, y);
        locationGraph.add(location);

        pane.setOnMouseClicked(null);
        root.setCursor(Cursor.DEFAULT);
      }
      for (String e : elements) {
        hbox.getChildren().get(elements.indexOf(e)).setDisable(false);
      }
      //TODO den slutar inte sedan placera ut new place även om man trycker på andra knappar
    });
  }

  private void emptyGraphs() {
    graph = new ListGraph<>();
    locationGraph = new ListGraph<>();
  }

  public void markPlace() {
    markedPlaces = new Circle[2];
    pane.setOnMouseClicked(mouseEvent -> {
      double x = mouseEvent.getX();
      double y = mouseEvent.getY();
      //kollar igenom locationGraph med alla punkter på kartan
      for (Location l : locationGraph.getNodes()) {
        double differenceX = x - l.getX();
        double differenceY = y - l.getY();
        //Kollar differancen mellan musklickets x och graphens x
        //Om diffen är mindre än 5 så är de samma
        if (Math.abs(x - l.getX()) < 5 && Math.abs(y - l.getY()) < 5) {
          //Går igenom punkterna i markedPlaces
          for (int i = 0; i < markedPlaces.length; i++) {
            //Om det finns cirklar i markedPlaces, kolla om någon av dessa är Location l
            //tar i och lägger den i en cirkel
            Circle circle = markedPlaces[i];
            //kollar om cirkeln inte är null och om cirkeln är lika med en cirkel som redan finns
              if (circle != null && circle.getCenterX() == l.getX() && circle.getCenterY() == l.getY()) {
                pane.getChildren().remove(markedPlaces[i]);
                markedPlaces[i] = new Circle(l.getX(), l.getY(), 7, Color.BLUE);
                pane.getChildren().add(markedPlaces[i]);
                markedPlaces[i] = null;
                return;
              }
            }
          //kollar om listan är full
          if (markedPlaces[0] != null && markedPlaces[1] != null) {
            return;
          }
          //det finns en ledig plats där circle kan läggas till och ritas ut på kartan
          Circle circle = new Circle(l.getX(), l.getY(), 10, Color.DARKRED);
          pane.getChildren().add(circle);
          for (int i = 0; i < markedPlaces.length; i++) {
            if (markedPlaces[i] == null) {
              markedPlaces[i] = circle;
              break;
            }
          }
        }
      }
    });
  }

  public void newConnection(){
      if (markedPlaces[0] == null || markedPlaces[1] == null) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Connection Error");
        alert.setContentText("Two places are not marked");
        alert.showAndWait();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  public void testFall() {
    Location location1 = new Location("Stockholm", 33,45);
    locationGraph.add(location1);

    Location location2 = new Location("Malmö", 55, 60);
    locationGraph.add(location2);

    Location location3 = new Location("Göteborg", 120, 30);
    locationGraph.add(location3);

    String node1 = "Stockholm";
    String node2 = "Malmö";
    String node3 = "Göteborg";
    graph.add(node1);
    graph.add(node2);
    graph.add(node3);
    graph.connect(node1, node2, "train", 4);
    graph.connect(node2, node3, "airplane", 2);
  }
}
