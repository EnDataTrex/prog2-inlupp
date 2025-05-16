package se.su.inlupp;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Gui extends Application {
  Image image;
  String fileName;
  Stage stage;
  Graph<String> graph;
  GridPane root;

  public void start(Stage stage) {
    this.stage = stage;
    graph = new ListGraph<String>();

    root = new GridPane(); //roten
    HBox hbox = new HBox(); // till för "övriga" knappar

    MenuButton menuBar = new MenuButton("File"); //till för menyknappar
    MenuItem newMapmenu = new MenuItem("New Map");

    FileChooser filechooser = new FileChooser();


    Label newMapLabel = new Label("New Map");
    newMapLabel.setPadding(new Insets(1, 30, 1, 1));
    newMapLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    newMapLabel.setOnMouseClicked(event -> {
      File file = filechooser.showOpenDialog(stage);
      fileName = file.toURI().toString();
      image = new Image(fileName);
      System.out.println(fileName);
      BackgroundImage backgroundImage = new BackgroundImage(
              image,
              BackgroundRepeat.NO_REPEAT,
              BackgroundRepeat.NO_REPEAT,
              BackgroundPosition.CENTER,
              new BackgroundSize(
                        100, 100, true, true, true, false
              )
              //BackgroundSize.DEFAULT
              );
      root.setBackground(new Background(backgroundImage));
    });
    CustomMenuItem customMenuItem = new CustomMenuItem(newMapLabel);
    menuBar.getItems().add(customMenuItem);
    //-------------------------------------------------------------------------------//
    Label openLabel = new Label("Open");
    openLabel.setPadding(new Insets(1, 30, 1, 1));
    openLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    openLabel.setOnMouseClicked(event -> {openLabel.setText("I clicked Open");});
    CustomMenuItem customMenuItem2 = new CustomMenuItem(openLabel);
    menuBar.getItems().add(customMenuItem2);
    //-------------------------------------------------------------------------------//
    Label saveLabel = new Label("Save");
    saveLabel.setPadding(new Insets(1, 30, 1, 1));
    saveLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    saveLabel.setOnMouseClicked(event -> {
        save();
      });
    CustomMenuItem customMenuItem3 = new CustomMenuItem(saveLabel);
    menuBar.getItems().add(customMenuItem3);

    Label saveImageLabel = new Label("Save Image");
    saveImageLabel.setPadding(new Insets(1, 30, 1, 1));
    saveImageLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    saveImageLabel.setOnMouseClicked(event -> {
      //saveImageLabel.setText("I clicked Saved Image");
      saveImage();
    });
    CustomMenuItem customMenuItem4 = new CustomMenuItem(saveImageLabel);
    menuBar.getItems().add(customMenuItem4);

    Label exitLabel = new Label("Exit");
    exitLabel.setPadding(new Insets(1, 30, 1, 1));
    exitLabel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    exitLabel.setOnMouseClicked(event -> {exitLabel.setText("I clicked Exit");});
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

    hbox.getChildren().get(0).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.DARKRED));
    });

    hbox.getChildren().get(1).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.LIGHTBLUE));
    });

    hbox.getChildren().get(2).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.LIGHTYELLOW));
    });

    hbox.getChildren().get(3).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.PURPLE));
    });

    hbox.getChildren().get(4).onMouseClickedProperty().setValue(event -> {
      hbox.setBackground(Background.fill(Color.BLACK));
    });

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  private void save() {
    try{
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showSaveDialog(this.stage);
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(file);
      objectOutputStream.close();
      //for(String node : graph.getNodes()) {

      //}
    }
    catch (FileNotFoundException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "File not found!");
      alert.showAndWait();
      //e.printStackTrace(); //TODO VI SKA HA EN POPUP ALERT
    }
    catch (IOException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error " + e.getMessage());
      alert.showAndWait();
      //e.printStackTrace(); //TODO VI SKA HA EN POPUP ALERT
      }
    }

   /*
  private void save(String fileName) {
    try{
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showSaveDialog(stage);

      FileOutputStream fileOutputStream = new FileOutputStream(file);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

      //vet inte om det finns något annat sätt att göra det på eller om det är såhär de har tänkt,
      //men efter att ha googlat runt lite verkar det som att detta är ett vanligt sätt att spara bilder
      //verkar docks om att vi då inte behöver fileName då
      BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
      ImageIO.write(bufferedImage, "png", file);

      //objectOutputStream.writeObject(file);
      //objectOutputStream.close();
      //for(String node : graph.getNodes()) {

      //}
    }
    catch (FileNotFoundException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "File not found!");
      alert.showAndWait();
      //e.printStackTrace(); //TODO VI SKA HA EN POPUP ALERT
    }
    catch (IOException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error " + e.getMessage());
      alert.showAndWait();
      //e.printStackTrace(); //TODO VI SKA HA EN POPUP ALERT
    }
  }
    */

  public void saveImage(){
    try {
      WritableImage image = root.snapshot(null, null);
      BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
      ImageIO.write(bufferedImage, "png", new File("capture.png"));
    }
    catch (IOException e){
      Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error " + e.getMessage());
      alert.showAndWait();
    }
  }

  //WORK IN PROGRESS
  public void newMapScene(Stage stage) {
    GridPane root = new GridPane();
    HBox hbox2 = new HBox();
    Button mapButton = new Button("New Map");
    hbox2.getChildren().add(mapButton);
    root.add(hbox2, 0, 2);

    Scene newMapScene = new Scene(root);
    stage.setScene(newMapScene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
