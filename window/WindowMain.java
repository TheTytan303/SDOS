package window;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class WindowMain extends Application {

    private Stage window;
    MainScene s;

    public void run(String[] args){
        System.out.println("Window opened!");

        launch(args);
    }
    @Override
    public void start(Stage primaryStage){

        window = primaryStage;
        window.setTitle("Lookin' for firends!");

        s = new MainScene();

        primaryStage.setScene(new Scene(s.getScene(), 500, 400));
        primaryStage.show();

        window.setOnCloseRequest((WindowEvent event)->mainWindowClosed());

        window.setMinWidth(800);
        //window.setMaxWidth(800);
        window.setMinHeight(420);
        window.setMaxHeight(450);
    }

    void mainWindowClosed(){

    }
    private static void popExceptionMsg(Exception e){
        if(e.getMessage().contains("lastSave")){
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}