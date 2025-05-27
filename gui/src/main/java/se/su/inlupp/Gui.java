package se.su.inlupp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
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
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
  Circle[] markedPlaces = new Circle[2];
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

    //hbox.setBackground(Background.fill(Color.LIGHTBLUE)); //sätter "övriga knappars" bakgrund till blå
    hbox.setAlignment(Pos.CENTER); //centrerar de "övriga knapparna"
    hbox.setSpacing(3); //Sätter ett utrymme runt knapparna

    GridPane.setMargin(menuBar, new Insets(0,0,10,0)); //sätter utrymme mellan boxar i griden

    ColumnConstraints column = new ColumnConstraints(); //skapar kolumnernas constraints
    column.setHgrow(Priority.ALWAYS); //Låter kolumnens längd växa dynamiskt
    grid.getColumnConstraints().add(column); //Lägger till det i roten

    elements = new ArrayList<>(Arrays.asList("Find Path", "Show Connection", "New Place", "New Connection", "Change Connection")); //Skapar lista med övriga knappar
    for (String element : elements) {
      Button button = new Button(element);//skapar ny knapp
      button.setStyle("-fx-background-color: floralwhite; -fx-border-color: darkgray;");
      //button.setBackground(Background.fill(Color.FLORALWHITE)); //sätter färg
      hbox.getChildren().add(button); //lägger till den som ett barn i hbox
    }

    /*----------------------------------------------------------------------------------------------*/
    //Find Path
    hbox.getChildren().get(0).onMouseClickedProperty().setValue(event -> {
      //hbox.setBackground(Background.fill(Color.web("ffeded")));
      hbox.getChildren().get(0).setStyle("-fx-background-color: #ffeded;");
      root.setBackground(Background.fill(Color.web("ffeded")));
      findPath();
      hbox.getChildren().get(0).setStyle("-fx-background-color: floralwhite; -fx-border-color: darkgray;");
    });

    //Show Connection
    hbox.getChildren().get(1).onMouseClickedProperty().setValue(event -> {
      hbox.getChildren().get(1).setStyle("-fx-background-color: #e2efff;");
      root.setBackground(Background.fill(Color.web("e2efff")));
      showConnection();
      hbox.getChildren().get(1).setStyle("-fx-background-color: floralwhite; -fx-border-color: darkgray;");
    });

    //New place
    hbox.getChildren().get(2).onMouseClickedProperty().setValue(event -> {
      hbox.getChildren().get(2).setStyle("-fx-background-color: #fff7e2;");
      root.setBackground(Background.fill(Color.web("fff7e2")));
      //TODO kollar om filename inte är null så att det finns en bakgrundsbild?
      if (fileName != null) {
        newPlace();
      }
      hbox.getChildren().get(2).setStyle("-fx-background-color: floralwhite; -fx-border-color: darkgray;");
    });

    //New Connection
    hbox.getChildren().get(3).onMouseClickedProperty().setValue(event -> {
      hbox.getChildren().get(3).setStyle("-fx-background-color: #f4f0ff;");
      root.setBackground(Background.fill(Color.web("f4f0ff")));
      newConnection();

      hbox.getChildren().get(3).setStyle("-fx-background-color: floralwhite; -fx-border-color: darkgray;");
    });

    //Change Connection
    hbox.getChildren().get(4).onMouseClickedProperty().setValue(event -> {
      hbox.getChildren().get(4).setStyle("-fx-background-color: #f0ffea;");
      root.setBackground(Background.fill(Color.web("f0ffea")));
      changeConnection();
      hbox.getChildren().get(4).setStyle("-fx-background-color: floralwhite; -fx-border-color: darkgray;");
    });

    markPlace();

    stage.setOnCloseRequest(event -> {
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
          if(!graph.getNodes().contains(name)){
            graph.add(name);
          }
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

            Location fromLocation = null;
            Location toLocation = null;
            for(Location l : locationGraph.getNodes()) {
              if(l.getName().equals(from)) {
                fromLocation = l;
              }
              if(l.getName().equals(to)) {
                toLocation = l;
              }
            }
            if (fromLocation != null && toLocation != null) {
              Line connectionLine = new Line(fromLocation.getX(), fromLocation.getY(), toLocation.getX(), toLocation.getY());
              connectionLine.setStrokeWidth(2);
              connectionLine.setStroke(Color.BLACK);
              pane.getChildren().add(connectionLine);
            }
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
      //går igenom alla noder i graph
      for(String node : graph.getNodes()) {
        //String nodeWithFormat = "";
        //nodeWithFormat = nodeWithFormat + node + ";";
        //kollar så att listan inte är tom
        if(!graph.getNodes().isEmpty()){
          //går igenom alla edges för noden
          for(Edge<String> edge : graph.getEdgesFrom(node)) {
            String nodeWithFormat = node + ";" + edge.getDestination() + ";" + edge.getName() + ";" + edge.getWeight() + "\n";
            printWriter.print(nodeWithFormat);
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
    //stage.setMinHeight(image.getHeight() + grid.getHeight() + 50);
    //stage.setMinWidth(image.getWidth() + 50);

    stage.sizeToScene();
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
    //Detta löste klickproblemet med new place, den klagar lite grann men vet inget annat sätt
    //Istället för att sätta den till null där nere (pane.setOnMouseClicked(null);)
    //Så sätt den till paneMouseHandler så man "återställer" den till så den var förut
    EventHandler<MouseEvent> paneMouseHandler = (EventHandler<MouseEvent>) pane.getOnMouseClicked();

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

      if(result.isPresent() && !name.isEmpty()) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        Circle place = new Circle(x, y, 7, Color.BLUE);
        pane.getChildren().add(place);

        //Lägger till punkten i listan
        Location location = new Location(name, x, y);
        locationGraph.add(location);

        if(!graph.getNodes().contains(name)){
          graph.add(name);
        }
      }
      pane.setOnMouseClicked(paneMouseHandler);
      root.setCursor(Cursor.DEFAULT);

      for (String e : elements) {
        hbox.getChildren().get(elements.indexOf(e)).setDisable(false);
      }
    });
  }

  private void emptyGraphs() {
    graph = new ListGraph<>();
    locationGraph = new ListGraph<>();
  }

  private void markPlace() {
    pane.setOnMouseClicked(mouseEvent -> {
      double x = mouseEvent.getX();
      double y = mouseEvent.getY();
      //kollar igenom locationGraph med alla punkter på kartan
      for (Location l : locationGraph.getNodes()) {
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

  private void showConnection(){
    checkMarkedPlaces();
    Location[] location = getLocationFromMarkedPlaces();
    if (location != null && location.length >= 2) {

      Location firstLocation = location[0];
      Location secondLocation = location[1];

      //TODO om det inte finns någon connection funkar inte
      if(checkExistedConnection(firstLocation, secondLocation)){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No connection available");
        alert.showAndWait();
      }
      else{
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection found");
        alert.setHeaderText("Connection from " + firstLocation.getName() + " to " + secondLocation.getName());

        Edge<String> edge = graph.getEdgeBetween(firstLocation.getName(), secondLocation.getName());
        String timeAsString = String.valueOf(edge.getWeight());
        TextField name = new TextField(edge.getName());
        TextField time = new TextField(timeAsString);
        name.setEditable(false);
        time.setEditable(false);
        name.setFocusTraversable(false);
        time.setFocusTraversable(false);

        VBox fields = new VBox(10, new Label("Name:"), name, new Label("Time"), time);
        alert.getDialogPane().setContent(fields);

        alert.showAndWait();
      }
    }
  }

  private void checkMarkedPlaces(){
    if (markedPlaces[0] == null || markedPlaces[1] == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Connection Error");
      alert.setContentText("Two places are not marked");
      alert.showAndWait();
    }
  }

  private Location[] getLocationFromMarkedPlaces() {
    Location[] locations = new Location[2];
    if (markedPlaces[0] != null && markedPlaces[1] != null) {
      Location firstLocation = null;
      Location secondLocation = null;
      //gå igenom listan över locations
      for (Location l : locationGraph.getNodes()) {
        //kolla om noden är lika
        if (l.getX() == markedPlaces[0].getCenterX() && l.getY() == markedPlaces[0].getCenterY()) {
          //hämtar ut location
          firstLocation = l;
        }
        //kan orsaka problem med == och doubles
        if (l.getX() == markedPlaces[1].getCenterX() && l.getY() == markedPlaces[1].getCenterY()) {
          secondLocation = l;
        }
      }
      locations[0] = firstLocation;
      locations[1] = secondLocation;
      return locations;
    }
    return null;
  }

  private void newConnection(){
    checkMarkedPlaces();

    if (getLocationFromMarkedPlaces() != null) {
      Location[] location = getLocationFromMarkedPlaces();

      Location firstLocation = location[0];
      Location secondLocation = location[1];

      //TODO blir fel, man kan lägga ut flera connections på två punkter, existedConnections ger fel resultat
      boolean existedConnection = checkExistedConnection(firstLocation, secondLocation);
      System.out.println(existedConnection);

      if (firstLocation != null && secondLocation != null && !existedConnection) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Connection");
        dialog.setHeaderText("New Connection from " + firstLocation.getName() + " to " + secondLocation.getName());

        TextField nameField = new TextField();
        TextField timeField = new TextField();

        VBox fields = new VBox(10, new Label("Name:"), nameField, new Label("Time"), timeField);
        dialog.getDialogPane().setContent(fields);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == okButton && !nameField.getText().isEmpty() && !timeField.getText().isEmpty() && isInteger(timeField.getText())) {
          String name = nameField.getText();
          int time = Integer.parseInt(timeField.getText());
          graph.connect(firstLocation.getName(), secondLocation.getName(), name, time);

          Line line = new Line(firstLocation.getX(), firstLocation.getY(), secondLocation.getX(), secondLocation.getY());
          line.setStrokeWidth(2);
          line.setStroke(Color.BLACK);
          pane.getChildren().add(line);

        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error");
          alert.setHeaderText("Connection Error");
          alert.setContentText("Name and time must be filled in correctly");
          alert.showAndWait();
        }
      }
    }
  }

  private boolean checkExistedConnection(Location firstLocation, Location secondLocation) {
    //går igenom edges från firstLocation
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Connection Error");
    alert.setContentText("Connection already exists");
    for (Edge<Location> edge : locationGraph.getEdgesFrom(firstLocation)) {
      //om destinationen är lika med secondLocation så finns redan en connection
      //och ett felmeddelande ges
      if (edge.getDestination() == secondLocation) {
        alert.showAndWait();
        return true;
      }
    }
    for (Edge<Location> edge : locationGraph.getEdgesFrom(secondLocation)) {
      if (edge.getDestination() == firstLocation)
        alert.showAndWait();
      return true;
    }
    return false;
  }

  private void changeConnection(){
    checkMarkedPlaces();
    Location[] location = getLocationFromMarkedPlaces();
    if (location != null && location.length >= 2) {
      Location firstLocation = location[0];
      Location secondLocation = location[1];

      boolean existedConnection = checkExistedConnection(firstLocation, secondLocation);

      if (firstLocation != null && secondLocation != null && !existedConnection) {
        Dialog alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Change connection");
        alert.setHeaderText("Change connection from " + firstLocation.getName() + " to " + secondLocation.getName());

        Edge<String> edge = graph.getEdgeBetween(firstLocation.getName(), secondLocation.getName());
        TextField nameField = new TextField(edge.getName());
        nameField.setEditable(false);
        nameField.setFocusTraversable(false);
        TextField timeField = new TextField();

        VBox fields = new VBox(10, new Label("Name:"), nameField, new Label("Time"), timeField);
        alert.getDialogPane().setContent(fields);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> result = alert.showAndWait();

        int time = Integer.parseInt(timeField.getText());
        String name = edge.getName();

        //Jag vet inte om det här är så smidigt haha. Måste också göra fler checkar.
        graph.disconnect(firstLocation.getName(),secondLocation.getName());

        graph.connect(firstLocation.getName(), secondLocation.getName(), name, time);
      }
    }
  }
  //Till för att kolla om time är en int eller inte
  private boolean isInteger(String integerToCheck) {
    try{
      Integer.parseInt(integerToCheck);
      return true;
    }catch(Exception e){
      return false;
    }
  }

  private void findPath(){
    checkMarkedPlaces();
    Location[] location = getLocationFromMarkedPlaces();
      if(graph.pathExists(location[0].getName(), location[1].getName())){
        List<Edge<String>> listOfPath = graph.getPath(location[0].getName(), location[1].getName());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Path");
        dialog.setHeaderText("The path from " + location[0].getName() + " to " + location[1].getName());
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(okButton);
        TextArea textArea = new TextArea();
        int counter = 0;
        for(Edge<String> edge : listOfPath){
          textArea.appendText(edge.toString() + "\n");
          counter += edge.getWeight();
        }

        textArea.appendText("Totalt avstånd" + counter + "\n");
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        dialog.getDialogPane().setContent(textArea);
        dialog.showAndWait();
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
