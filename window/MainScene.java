package window;

import Core.Actor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class MainScene {
    private BorderPane panel;
    private GridPane rightMenu;
    private GridPane topMenu;
    GridPane centrer;
    ButtonExecutions orders;
    Searching searchingClass;
    Validating validatingClass1;
    Validating validatingClass2;
    Button eva, chk1, chk2;
    Actor a1, a2;
    TextField inputActor1, inputActor2;
    TextArea actualActor1, actualActor2;
    Thread searchSolution, validateSolution1, validateSolution2;
    TextArea result;

    public MainScene(){
        a1 = new Actor();
        a2 = new Actor();


        centrer = new GridPane();


        orders = new ButtonExecutions(this);

        panel = new BorderPane();

        rightMenu = new GridPane();
        eva = new Button("Eva!");
        eva.setMinWidth(90);
        eva.setOnAction(orders);
        chk1 = new Button("Check first");
        chk1.setMinWidth(90);
        chk1.setOnAction(orders);
        chk2 = new Button("Check second");
        chk2.setMinWidth(90);
        chk2.setOnAction(orders);
        Label actuarActor1Label = new Label("First chosen Actor");
        actualActor1 = new TextArea("none");
        actualActor1.setEditable(false);
        actualActor1.setMaxWidth(150);
        actualActor1.setMaxHeight(13);
        Label actualActor2Label = new Label("Second chosen Actor");
        actualActor2 = new TextArea("none");
        actualActor2.setEditable(false);
        actualActor2.setMaxWidth(150);
        actualActor2.setMaxHeight(13);
        rightMenu.add(chk1 , 0,0);
        rightMenu.add(chk2 , 0,1);
        rightMenu.add(actuarActor1Label , 0,2);
        rightMenu.add(actualActor1 , 0,3);
        rightMenu.add(actualActor2Label , 0,4);
        rightMenu.add(actualActor2 , 0,5);
        rightMenu.add(eva , 0,6);

        topMenu = new GridPane();
        Label actor1Label = new Label("First Actor");
        Label actor2Label = new Label("Second Actor");
        inputActor1 = new TextField(); inputActor1.setText("24577");
        inputActor2 = new TextField(); inputActor2.setText("921024");
        topMenu.add(actor1Label,0,0);
        topMenu.add(inputActor1,1,0);
        topMenu.add(actor2Label,2,0);
        topMenu.add(inputActor2,3,0);

        result = new TextArea("new one");
        result.setEditable(false);
        result.setWrapText(true);

    }

    public BorderPane getScene() {

        panel.setRight(rightMenu);
        panel.setTop(topMenu);
        panel.setCenter(result);
        BorderPane.setMargin(panel.getTop(), new Insets(0,0,10,0));
        return panel;
    }

    public String getIntypedActor1(){
        return inputActor1.getText();
    }



}
