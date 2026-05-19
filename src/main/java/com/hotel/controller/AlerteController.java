package com.hotel.controller;

import com.hotel.model.AlerteNotification;
import com.hotel.model.AlerteNotification.Niveau;
import com.hotel.service.AlerteService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Contrôleur du panneau d'alertes/notifications.
 * Lié à AlerteView.fxml.
 */
public class AlerteController {

    @FXML private VBox          listeAlertes;
    @FXML private Label         lblCompteur;
    @FXML private ComboBox<String> comboFiltre;
    @FXML private ScrollPane    scrollPane;

    private final AlerteService alerteService = new AlerteService();
    private List<AlerteNotification> toutesAlertes;

    // ── Couleurs ──────────────────────────────────────────────────────────
    private static final String ROUGE   = "#dc3545";
    private static final String ORANGE  = "#fd7e14";
    private static final String BLEU    = "#0d6efd";
    private static final String FOND    = "#f8f9fc";
    private static final String BLANC   = "#ffffff";
    private static final String TEXTE   = "#1e1e32";
    private static final String GRIS    = "#868696";

    @FXML
    public void initialize() {
        comboFiltre.getItems().addAll("Tout", "🔴 Critique", "🟠 Attention", "🔵 Info");
        comboFiltre.setValue("Tout");
        comboFiltre.setOnAction(e -> filtrer());
        chargerAlertes();
    }

    // ── Chargement ────────────────────────────────────────────────────────

    @FXML
    public void chargerAlertes() {
        toutesAlertes = alerteService.genererToutesAlertes();
        afficher(toutesAlertes);
        int n = toutesAlertes.size();
        lblCompteur.setText(n == 0 ? "Aucune alerte active" : n + " alerte(s) active(s)");
    }

    private void filtrer() {
        if (toutesAlertes == null) return;
        String sel = comboFiltre.getValue();
        List<AlerteNotification> filtrees = switch (sel) {
            case "🔴 Critique"  -> toutesAlertes.stream()
                    .filter(a -> a.getNiveau() == Niveau.CRITIQUE).toList();
            case "🟠 Attention" -> toutesAlertes.stream()
                    .filter(a -> a.getNiveau() == Niveau.ATTENTION).toList();
            case "🔵 Info"      -> toutesAlertes.stream()
                    .filter(a -> a.getNiveau() == Niveau.INFO).toList();
            default -> toutesAlertes;
        };
        afficher(filtrees);
    }

    // ── Affichage ─────────────────────────────────────────────────────────

    private void afficher(List<AlerteNotification> alertes) {
        listeAlertes.getChildren().clear();

        if (alertes.isEmpty()) {
            Label vide = new Label("✅  Tout est en ordre — aucune alerte !");
            vide.setStyle("-fx-font-size: 14px; -fx-text-fill: #4caf50; -fx-font-style: italic;");
            vide.setPadding(new Insets(40, 0, 0, 20));
            listeAlertes.getChildren().add(vide);
            return;
        }

        for (AlerteNotification a : alertes) {
            listeAlertes.getChildren().add(creerCarte(a));
        }
    }

    // ── Création d'une carte d'alerte ─────────────────────────────────────

    private HBox creerCarte(AlerteNotification alerte) {
        String couleur = couleurPourNiveau(alerte.getNiveau());

        // Conteneur principal
        HBox carte = new HBox(12);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setPadding(new Insets(14, 16, 14, 0));
        carte.setMaxWidth(Double.MAX_VALUE);
        carte.setStyle(
                "-fx-background-color: " + BLANC + ";" +
                        "-fx-border-color: " + couleur + ";" +
                        "-fx-border-width: 0 0 0 5;" +
                        "-fx-border-radius: 0 8 8 0;" +
                        "-fx-background-radius: 0 8 8 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        );
        VBox.setMargin(carte, new Insets(0, 0, 8, 0));

        // Icône
        Label icone = new Label(alerte.getIcone());
        icone.setStyle("-fx-font-size: 22px;");
        icone.setPadding(new Insets(0, 4, 0, 16));

        // Textes (message + meta)
        VBox textes = new VBox(4);
        HBox.setHgrow(textes, Priority.ALWAYS);

        Label lblMsg = new Label(alerte.getMessage());
        lblMsg.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXTE + ";"
        );
        lblMsg.setWrapText(true);

        Label lblMeta = new Label(
                alerte.getDateFormatee() + "   ·   Chambre : " + alerte.getChambreNumero()
        );
        lblMeta.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GRIS + ";");

        textes.getChildren().addAll(lblMsg, lblMeta);

        // Badge niveau
        Label badge = new Label(alerte.getBadgeTexte());
        badge.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 4 10 4 10;" +
                        "-fx-background-radius: 12;"
        );
        HBox.setMargin(badge, new Insets(0, 12, 0, 0));

        // Hover effect
        carte.setOnMouseEntered(e -> carte.setStyle(
                "-fx-background-color: #f0f4ff;" +
                        "-fx-border-color: " + couleur + ";" +
                        "-fx-border-width: 0 0 0 5;" +
                        "-fx-border-radius: 0 8 8 0;" +
                        "-fx-background-radius: 0 8 8 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 8, 0, 0, 3);" +
                        "-fx-cursor: hand;"
        ));
        carte.setOnMouseExited(e -> carte.setStyle(
                "-fx-background-color: " + BLANC + ";" +
                        "-fx-border-color: " + couleur + ";" +
                        "-fx-border-width: 0 0 0 5;" +
                        "-fx-border-radius: 0 8 8 0;" +
                        "-fx-background-radius: 0 8 8 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        ));

        carte.getChildren().addAll(icone, textes, badge);
        return carte;
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    private String couleurPourNiveau(Niveau niveau) {
        return switch (niveau) {
            case CRITIQUE  -> ROUGE;
            case ATTENTION -> ORANGE;
            case INFO      -> BLEU;
        };
    }
}