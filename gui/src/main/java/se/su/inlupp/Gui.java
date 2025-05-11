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
    MenuItem newMapmenu = new MenuItem("New Map");
    MenuItem openInMenu = new MenuItem("Open");
    MenuItem saveInMenu = new MenuItem("Save");
    MenuItem saveImageInMenu = new MenuItem("Save Image");
    MenuItem exitInMenu = new MenuItem("Exit");
    menuBar.getItems().addAll(newMapmenu, openInMenu, saveInMenu, saveImageInMenu, exitInMenu);

    root.setBackground(Background.fill(Color.LIGHTGREY)); //sätter bakgrund
    root.add(menuBar, 0, 0); //lägger till menubar i roten
    root.add(hbox, 0,1); //lägger till hbox i roten
    root.setPadding(new Insets(10));

    hbox.setBackground(Background.fill(Color.LIGHTBLUE));
    hbox.setAlignment(Pos.CENTER);
    hbox.setSpacing(3);

    GridPane.setMargin(menuBar, new Insets(0,0,10,0));

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setHgrow(Priority.ALWAYS);
    root.getColumnConstraints().add(column1);

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
