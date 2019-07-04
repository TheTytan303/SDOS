package window;

import Core.Actor;
import com.sun.tools.javac.Main;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Validating implements Runnable {

    TextField inputActor;
    TextArea actualActor;
    MainScene service;
    int i;


    Validating(TextField inputActor, TextArea actualActor, MainScene service, int i){
        this.actualActor=actualActor;
        this.inputActor=inputActor;
        this.service=service;
        this.i=i;
    }
    public void run() {
        try{
            Actor a;
            try{
                int id = Integer.decode(inputActor.getText());
                a = Actor.searchForActor(id);
            }
            catch (NumberFormatException e){
                return;
            }
            if(i == 1){
                service.a1=a;
            }
            else {
                service.a2 = a;
            }
            actualActor.setText(a.toString());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
    }
}
