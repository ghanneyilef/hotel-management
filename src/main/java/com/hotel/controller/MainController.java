package com.hotel.controller;

import com.hotel.service.AlerteService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Contrôleur principal — gère la navigation sidebar + badge notifications.
 */
public class MainController {

    @FXML private StackPane contentArea;

    // Boutons sidebar
    @FXML private Button btnClients;
    @FXML private Button btnChambres;
    @FXML private Button btnReservations;
    @FXML private Button btnEmployes;
    @FXML private Button btnPaiements;

    // ── NOUVEAU : bouton notifications + badge ────────────────────────────
    @FXML private Button btnAlertes;   // bouton 🔔 dans le FXML
    @FXML private Label  badgeAlertes; // petit cercle rouge avec le chiffre

    private Button activeBtn;
    private final AlerteService alerteService = new AlerteService();

    @FXML
    public void initialize() {
        activeBtn = btnClients;

        // Charger le badge au démarrage
        rafraichirBadge();

        // Rafraîchir le badge toutes les 5 minutes
        Timeline timer = new Timeline(
                new KeyFrame(Duration.minutes(5), e -> rafraichirBadge())
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    // ── Navigation ────────────────────────────────────────────────────────

    @FXML private void showClients()      { loadView("ClientView.fxml",      btnClients); }
    @FXML private void showChambres()     { loadView("ChambreView.fxml",     btnChambres); }
    @FXML private void showReservations() { loadView("ReservationView.fxml", btnReservations); }
    @FXML private void showEmployes()     { loadView("EmployeView.fxml",     btnEmployes); }
    @FXML private void showPaiements()    { loadView("PaiementView.fxml",    btnPaiements); }

    // ── NOUVEAU : ouvrir le panneau d'alertes ─────────────────────────────
    @FXML
    private void showAlertes() {
        loadView("AlerteView.fxml", btnAlertes);
        // Remettre badge à 0 visuellement quand on ouvre le panneau
        badgeAlertes.setVisible(false);
    }

    // ── Chargement de vue ─────────────────────────────────────────────────

    private void loadView(String fxmlFile, Button clickedBtn) {
        if (clickedBtn == activeBtn) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/hotel/view/" + fxmlFile));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);

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

    // ── Badge notifications ───────────────────────────────────────────────

    private void rafraichirBadge() {
        long count = alerteService.countAlertes();
        if (count > 0) {
            badgeAlertes.setText(count > 99 ? "99+" : String.valueOf(count));
            badgeAlertes.setVisible(true);
        } else {
            badgeAlertes.setVisible(false);
        }
    }
}