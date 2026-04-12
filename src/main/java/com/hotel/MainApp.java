package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Point d'entrée JavaFX de l'application Hôtel Manager.
 *
 * IMPORTANT — Emplacement des fichiers FXML :
 *   src/main/resources/com/hotel/view/LoginView.fxml
 *   src/main/resources/com/hotel/view/ClientView.fxml
 *   src/main/resources/com/hotel/view/EmployeView.fxml
 *   ...
 *
 * Si les fichiers FXML sont au mauvais endroit, getResource() retourne null
 * et JavaFX affiche une fenêtre vide ou lance NullPointerException.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // ── Vérification FXML avant chargement ───────────────────────────
        // CORRECTION : on vérifie que la ressource existe pour afficher
        // un message d'erreur clair si le fichier est introuvable
        URL fxmlUrl = getClass().getResource("/com/hotel/view/LoginView.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException(
                    "❌ LoginView.fxml introuvable !\n" +
                            "Vérifiez que le fichier est dans :\n" +
                            "  src/main/resources/com/hotel/view/LoginView.fxml\n" +
                            "Et que le dossier 'resources' est marqué comme " +
                            "'Resources Root' dans IntelliJ."
            );
        }

        // ── Chargement de la vue Login ────────────────────────────────────
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 500, 400);

        // ── Configuration de la fenêtre ───────────────────────────────────
        primaryStage.setTitle("🏨 Hôtel Manager");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}