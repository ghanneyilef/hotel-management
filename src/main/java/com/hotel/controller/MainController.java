package com.hotel.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Contrôleur principal — gère la navigation sidebar.
 * Charge chaque vue à la demande dans le contentArea (StackPane).
 */
public class MainController {

    @FXML private StackPane contentArea;

    // Boutons sidebar
    @FXML private Button btnClients;
    @FXML private Button btnChambres;
    @FXML private Button btnReservations;
    @FXML private Button btnEmployes;
    @FXML private Button btnPaiements;

    // Bouton actif courant
    private Button activeBtn;

    @FXML
    public void initialize() {
        // Clients est actif par défaut (déjà inclus dans le FXML)
        activeBtn = btnClients;
    }

    // ── Navigation ──────────────────────────────────────────────────────────

    @FXML
    private void showClients() {
        loadView("ClientView.fxml", btnClients);
    }

    @FXML
    private void showChambres() {
        loadView("ChambreView.fxml", btnChambres);
    }

    @FXML
    private void showReservations() {
        loadView("ReservationView.fxml", btnReservations);
    }

    @FXML
    private void showEmployes() {
        loadView("EmployeView.fxml", btnEmployes);
    }

    @FXML
    private void showPaiements() {
        loadView("PaiementView.fxml", btnPaiements);
    }

    // ── Chargement dynamique de la vue ──────────────────────────────────────

    private void loadView(String fxmlFile, Button clickedBtn) {
        // Évite de recharger si déjà actif
        if (clickedBtn == activeBtn) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/hotel/view/" + fxmlFile));
            Node view = loader.load();

            contentArea.getChildren().setAll(view);

            // Met à jour le style des boutons sidebar
            if (activeBtn != null) {
                activeBtn.getStyleClass().remove("nav-btn-active");
                activeBtn.getStyleClass().add("nav-btn");
            }
            clickedBtn.getStyleClass().remove("nav-btn");
            clickedBtn.getStyleClass().add("nav-btn-active");
            activeBtn = clickedBtn;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
