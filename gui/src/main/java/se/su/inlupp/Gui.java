package se.su.inlupp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class Gui extends Application {

  public void start(Stage stage) {
    Graph<String> graph = new ListGraph<String>();
    GridPane root = new GridPane(); //roten
    HBox hbox = new HBox(); // till för "övriga" knappar
    MenuButton menuBar = new MenuButton("File"); //till för menyknappar
    MenuItem fileMenu = new MenuItem("Hej");
    MenuItem newMapMenu = new MenuItem("New Map");
    menuBar.getItems().addAll(fileMenu, newMapMenu);
    root.setBackground(Background.fill(Color.LIGHTBLUE)); //sätter bakgrund
    root.add(menuBar, 0, 0); //lägger till menubar i roten
    root.add(hbox, 0,1); //lägger till hbox i roten
    hbox.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(10));

    hbox.setSpacing(3);


    ArrayList<String> elements = new ArrayList<>(Arrays.asList("Find Path", "Show Connection", "New Place", "New Connection", "Change Connection"));
    for (String element : elements) {
      Button button = new Button(element);
      hbox.getChildren().add(button);
    }
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
