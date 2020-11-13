package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class mainGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/mainView.fxml"));
            Scene scene = new Scene(root, 1200, 459);
            stage.setTitle("TeamFormation");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Fail to load");
            e.printStackTrace();
        }
    }
}
