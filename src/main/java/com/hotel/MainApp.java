package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL fxmlUrl = getClass().getResource("/com/hotel/view/LoginView.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException(
                    "LoginView.fxml introuvable !\n" +
                            "Vérifiez que le fichier est dans :\n" +
                            "  src/main/resources/com/hotel/view/LoginView.fxml"
            );
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        // Taille agrandie pour la page de login avec la nouvelle carte
        Scene scene = new Scene(loader.load(), 520, 520);

        primaryStage.setTitle("Hôtel Manager");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
