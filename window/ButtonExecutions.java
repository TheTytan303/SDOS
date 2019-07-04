package window;

import Core.Actor;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.print.attribute.standard.RequestingUserName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ButtonExecutions implements EventHandler<ActionEvent> {
    private MainScene service;
    public ArrayList<Actor> actorsList1;
    public ArrayList<Actor> actorsList2;

    ButtonExecutions(MainScene i){
        service = i;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() == service.eva) {
            eva();
        }
        if (actionEvent.getSource() == service.chk1) {
            try{
                Integer.decode(service.inputActor1.getText());
            }
            catch (NumberFormatException e){
                ArrayList<Actor> tmp = Actor.searchForActors(service.inputActor1.getText());
                if(tmp.size()==1){
                    service.a1=tmp.get(0);
                }
                else{
                    service.a1=choseDialog(tmp);
                    service.actualActor1.setText(service.a1.toString());
                }
                return;
            }
            //service.validatingClass1 = new Validating(service.inputActor1, service.actualActor1, service.a1);


            service.validatingClass1 = new Validating(service.inputActor1, service.actualActor1, service, 1);
            service.validateSolution1 = new Thread( service.validatingClass1);
            service.validateSolution1.start();
        }
        if (actionEvent.getSource() == service.chk2) {
            try{
                Integer.decode(service.inputActor2.getText());
            }
            catch (NumberFormatException e){
                ArrayList<Actor> tmp = Actor.searchForActors(service.inputActor2.getText());
                if(tmp.size()==1){
                    service.a2=tmp.get(0);
                }
                else{
                    service.a2=choseDialog(tmp);
                    service.actualActor2.setText(service.a2.toString());
                }
                return;
            }
            service.validatingClass2 = new Validating(service.inputActor2, service.actualActor2, service, 2);
            service.validateSolution2 = new Thread(service.validatingClass2);
            service.validateSolution2.start();
        }
    }
    public void eva(){
        if(service.a1==null || service.a2==null){
            //this.chk(service.inputActor1,service.actualActor1, service.a1);
            //this.chk(service.inputActor2, service.actualActor2, service.a2);
            service.result.setText("wrong actor input - validate them first");
            return;
        }
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        //Callable<String> callable = () -> Actor.find(service.a1,service.a2);
        //Future<String> future = executor.submit(callable);
        //try{
        //    service.result.setText(future.get());
        //}catch(Exception e){
        //    System.err.println(e.getMessage()+"EVA :|");
        //}
        //executor.shutdown();
        service.searchingClass = new Searching(service.result, service.a1,service.a2);
        service.searchSolution = new Thread(service.searchingClass);
        service.searchSolution.start();
    };
    private void chk(TextField inputActor, TextField actualActor, Actor a){

    };

    private Actor choseDialog(ArrayList<Actor> actorsList){
        //System.out.println("Chose Actor");


        ButtonType createButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        Dialog<List<String>> nazwa= new Dialog<>();
        nazwa.setTitle("Chose Actor");
        nazwa.getDialogPane().getButtonTypes().addAll(createButton, ButtonType.CANCEL);
        GridPane windowPane = new GridPane();
        ChoiceBox maskInput = new ChoiceBox<>(FXCollections.observableArrayList(actorsList));
        windowPane.setHgap(10);
        windowPane.setVgap(10);
        windowPane.add(maskInput,0 ,0);
        nazwa.getDialogPane().setContent(windowPane);
        nazwa.setResultConverter(dialogButton -> {
            if(dialogButton == createButton){
                List<String> returnVale = new ArrayList<>();
                returnVale.add(((Actor)maskInput.getValue()).getId()+"");
                return  returnVale;
            }
            else{
                return null;
            }
        });
        Optional<List<String>> returnedList = nazwa.showAndWait();
        if(returnedList.isPresent()){
            List<String> gotList = returnedList.get();
            try{
                return new Actor(Integer.decode(gotList.get(0)));
            }
            catch (Exception ignore){
                return null;
            }
        }
        return null;
    }

    class ChkIfActor implements Runnable{
         String name;
         Actor a;
        ChkIfActor(String s, Actor input){
            name = s;
            a=input;
        }
        public void run(){

        }
    }
}
