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
    newMapmenu.setOnAction(event -> {});//Skapar ett event med vad som ska hända när man klickar på knappen (Kan behöva ändas beroende på vad action är)

    MenuItem openInMenu = new MenuItem("Open");
    newMapmenu.setOnAction(event -> {});

    MenuItem saveInMenu = new MenuItem("Save");
    saveInMenu.setOnAction(event -> {});

    MenuItem saveImageInMenu = new MenuItem("Save Image");
    saveImageInMenu.setOnAction(event -> {});

    MenuItem exitInMenu = new MenuItem("Exit");
    exitInMenu.setOnAction(event -> {});

    menuBar.getItems().addAll(newMapmenu, openInMenu, saveInMenu, saveImageInMenu, exitInMenu); //Lägger till alla knappar i menyn

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
      newMapScene(stage); //skapar ett event för första knappen (ändrar färg)
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
