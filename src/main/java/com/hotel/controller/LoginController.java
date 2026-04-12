package com.hotel.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.hotel.dao.DatabaseConnection;
import java.sql.*;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id FROM users WHERE username=? AND password=?")) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ouvrirDashboard();
            } else {
                errorLabel.setText("Identifiants incorrects");
            }
        } catch (Exception e) {
            errorLabel.setText("Erreur de connexion à la base");
        }
    }

    private void ouvrirDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/hotel/view/ClientView.fxml"));
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(loader.load(), 900, 600));
        stage.setTitle("Hôtel Manager — Dashboard");
    }
}