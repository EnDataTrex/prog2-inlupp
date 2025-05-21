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
  GridPane root;
  HBox hbox;
  boolean saveStatus = false;
  //StackPane stackPane;
  Pane pane;
  //Gör det tydligt om programmet är sparat eller inte

  public void start(Stage stage) {

    this.stage = stage;
    emptyGraphs();

    root = new GridPane(); //roten
    hbox = new HBox();
    //stackPane = new StackPane();// till för "övriga" knappar
    pane = new Pane();

    //stackPane.getChildren().addAll(root);

    MenuButton menuBar = new MenuButton("File"); //till för menyknappar
    //Behövs den? Den används aldrig
    //MenuItem newMapmenu = new MenuItem("New Map");

    Label newMapLabel = new Label("New Map");
    newMapLabel.setPadding(new Insets(1, 30, 1, 1));
    newMapLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    newMapLabel.setOnMouseClicked(event -> {
      newMap();
      saveStatus = true;
    });
    CustomMenuItem customMenuItem = new CustomMenuItem(newMapLabel);
    menuBar.getItems().add(customMenuItem);
    //-------------------------------------------------------------------------------//
    /* Sätt bilen till fixed, och se till att fönstrets minimi storlek är anpassad till den så
    * den alltid är synlig, men se också till att weight/alignment är anpassat till resten av fönstret
    * TODO KOLLA UPP VILKA KOMMANDON SOM GÖR PADDING TRANSFORMATIVT*/
    Label openLabel = new Label("Open");
    openLabel.setPadding(new Insets(1, 30, 1, 1));
    openLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    openLabel.setOnMouseClicked(event -> {
      open();
      //TODO ändra savestatus?
    });
    CustomMenuItem customMenuItem2 = new CustomMenuItem(openLabel);
    menuBar.getItems().add(customMenuItem2);
    //-------------------------------------------------------------------------------//
    Label saveLabel = new Label("Save");
    saveLabel.setPadding(new Insets(1, 30, 1, 1));
    saveLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    saveLabel.setOnMouseClicked(event -> {
        save();
        saveStatus = false;
      });
    CustomMenuItem customMenuItem3 = new CustomMenuItem(saveLabel);
    menuBar.getItems().add(customMenuItem3);

    Label saveImageLabel = new Label("Save Image");
    saveImageLabel.setPadding(new Insets(1, 30, 1, 1));
    saveImageLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    saveImageLabel.setOnMouseClicked(event -> {
      saveImage();
    });
    CustomMenuItem customMenuItem4 = new CustomMenuItem(saveImageLabel);
    menuBar.getItems().add(customMenuItem4);

    Label exitLabel = new Label("Exit");
    exitLabel.setPadding(new Insets(1, 30, 1, 1));
    exitLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    exitLabel.setOnMouseClicked(event -> {
      exitProgram(); //Kallar på metoden som checkar vilkoren för att stänga ner programmet
      event.consume(); //Om vilkoren inte uppnåtts (aka) om man klickar på cancel så stängs fönstret ner

    });
    CustomMenuItem customMenuItem5 = new CustomMenuItem(exitLabel);
    menuBar.getItems().add(customMenuItem5);

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

    root.setBackground(Background.fill(Color.LIGHTGREY)); //sätter bakgrund
    root.add(menuBar, 0, 0); //lägger till menubar i roten
    root.add(hbox, 0,1); //lägger till hbox i roten
    root.setPadding(new Insets(10)); //sätter padding aka utrymme mellan rotens innehåll och kanter

    hbox.setBackground(Background.fill(Color.LIGHTBLUE)); //sätter "övriga knappars" bakgrund till blå
    hbox.setAlignment(Pos.CENTER); //centrerar de "övriga knapparna"
    hbox.setSpacing(3); //Sätter ett utrymme runt knapparna

    GridPane.setMargin(menuBar, new Insets(0,0,10,0)); //sätter utrymme mellan boxar i griden

    ColumnConstraints column1 = new ColumnConstraints(); //skapar kolumnernas constraints
    column1.setHgrow(Priority.ALWAYS); //Låter kolumnens längd växa dynamiskt
    root.getColumnConstraints().add(column1); //Lägger till det i roten

    ArrayList<String> elements = new ArrayList<>(Arrays.asList("Find Path", "Show Connection", "New Place", "New Connection", "Change Connection")); //Skapar lista med övriga knappar
    for (String element : elements) {
      Button button = new Button(element);//skapar ny knapp
      button.setBackground(Background.fill(Color.FLORALWHITE)); //sätter färg
      hbox.getChildren().add(button); //lägger till den som ett barn i hbox
    }

    /*----------------------------------------------------------------------------------------------*/
    hbox.getChildren().get(0).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.DARKRED));
    });

    hbox.getChildren().get(1).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.LIGHTBLUE));
    });

    hbox.getChildren().get(2).onMouseClickedProperty().setValue(event -> {
      //TODO kollar om filename inte är null så att det finns en bakgrundsbild?
      if (fileName != null) {
        newPlace();
      }
    });

    hbox.getChildren().get(3).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.PURPLE));
    });

    hbox.getChildren().get(4).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.BLACK));
    });

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

    //Scene scene = new Scene(stackPane, 800, 600);
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    //ladda testfall
    testFall();
  }

  private void changeWindowSize(double width, double height) {
    stage.setMinHeight(height);
    stage.setMinWidth(width);
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
        image = new Image(fileName);
        setBackground(image);

        String line = bufferedReader.readLine();
        String[] objects = line.split(";");
        for(int i = 0; i < objects.length; i+=3) {
          String name = objects[i];
          double x = Double.parseDouble(objects[i+1]);
          double y = Double.parseDouble(objects[i+2]);
          Location location = new Location(name, x, y);
          locationGraph.add(location);
        }

        while((line = bufferedReader.readLine()) != null) {
          objects = line.split(";");
          String from = objects[0];
          String to = objects[1];
          String edge = objects[2];
          int weight = Integer.parseInt(objects[3]);
          if(graph.getEdgeBetween(from, to) == null) {
            graph.connect(from, to, edge, weight);
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
    image = new Image(fileName);
    setBackground(image);

    pane.setPrefSize(image.getWidth(), image.getHeight());

    //root.add(pane, 0, 0);

  }

  private void saveImage() {
    try {
      WritableImage image = root.snapshot(null, null);
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

  private void setBackground(Image image) {
    BackgroundImage backgroundImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            BackgroundSize.DEFAULT
    );
    root.setBackground(new Background(backgroundImage));
    changeWindowSize(image.getWidth(),image.getHeight());
  }

  private void newPlace(){
    root.setCursor(Cursor.CROSSHAIR);
    hbox.getChildren().get(2).onMouseClickedProperty().setValue(null);
    root.setOnMouseClicked(mouseEvent -> {

      TextInputDialog newPlaceDialog = new TextInputDialog();
      newPlaceDialog.setTitle("Name");
      newPlaceDialog.setHeaderText("Name of place");

      Optional<String> result = newPlaceDialog.showAndWait();
      String name = newPlaceDialog.getEditor().getText();

      if(result.isPresent() && !name.isEmpty()){
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        Circle place = new Circle(x, y,5, Color.BLUE);
        pane.getChildren().add(place);
        //root.add(place, x, y);
        //stackPane.getChildren().add(place);
        //TODO den låter mig inte lägga till x, y (jag är osäker på hur man löser detta
        //root.getChildren().add(place);
      }
      //TODO kan vara så att vi behöver göra en stackpane som vi sedan lägger gridpane (root) i,
      //TODO för att sedan kunna lägga på cirklar, för just nu blir det fel i position, samt att den,
      //TODO följer med rooten och inte stannar på bakgrundsbilden
      //root.setCursor(Cursor.DEFAULT);
      //hbox.getChildren().get(2).setDisable(false);
      //TODO den slutar inte sedan placera ut new place även om man trycker på andra knappar
    });
  }

  private void emptyGraphs() {
    graph = new ListGraph<>();
    locationGraph = new ListGraph<>();
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
