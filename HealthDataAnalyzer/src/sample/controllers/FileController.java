package sample.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FileController {
    @FXML private HBox fileBox;
    @FXML private ImageView uploadIcon;
    @FXML private Text uploadStatus;

    private Stage mWindow;

    public FileController(Stage window) {
        mWindow = window;
    }

    @FXML private void initialize() {
        final FileChooser fileChooser = new FileChooser();
        fileBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File file = fileChooser.showOpenDialog(mWindow);
                if (file != null) {
                    String fileName = file.getName();
                    String extension;
                    int i = fileName.lastIndexOf('.');
                    if (i > 0) {
                        extension = fileName.substring(i+1).toLowerCase();
                        if (extension.equals("xml")) {
                            Parent root;
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

                                Controller controller = new Controller(file);
                                fxmlLoader.setController(controller);
                                root = fxmlLoader.load();
                                mWindow.setScene(new Scene(root));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            System.out.println("Needs to be an .xml file.");
                            uploadStatus.setText("Needs to be an .xml file.");
                        }
                    } else {
                        System.out.println("Incorrect file type.");
                        uploadStatus.setText("Incorrect file type.");
                    }
                }
            }
        });
    }
}

