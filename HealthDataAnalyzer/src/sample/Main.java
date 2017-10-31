package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.controllers.Controller;
import sample.controllers.FileController;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Health Data Analyzer");
        primaryStage.getIcons().add(new Image("/images/icon.png"));
        if (false) { // if live
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/file.fxml"));

            FileController fileController = new FileController(primaryStage);
            fxmlLoader.setController(fileController);
            Parent root = fxmlLoader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } else { // if testing
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Controller controller = new Controller(null);
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}