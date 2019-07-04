package window;

import Core.Actor;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Searching implements Runnable {
    TextArea resultLabel;
    Actor a1, a2;
    //private boolean ifSearch;

    public Searching(TextArea resultLabel, Actor a1, Actor a2){
        this.resultLabel = resultLabel;
        this.a1=a1;
        this.a2=a2;
    }
    public void run() {
        try{
            resultLabel.setText(Actor.find(a1,a2));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }

    }
}