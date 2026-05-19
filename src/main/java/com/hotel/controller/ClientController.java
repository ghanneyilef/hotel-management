package com.hotel.controller;

import com.hotel.model.Client;
import com.hotel.service.ClientService;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ClientController {

    // ── Colonnes TableView ──────────────────────────────────────────────────
    @FXML private TableView<Client>              clientTable;
    @FXML private TableColumn<Client, Integer>   colId;
    @FXML private TableColumn<Client, String>    colNom;
    @FXML private TableColumn<Client, String>    colPrenom;
    @FXML private TableColumn<Client, String>    colEmail;
    @FXML private TableColumn<Client, String>    colTel;
    // CORRECTION : type générique <Client, Void> obligatoire pour que
    // updateItem(Void, boolean) compile sans erreur
    @FXML private TableColumn<Client, Void>      colActions;

    @FXML private TextField searchField;

    // ── Service ─────────────────────────────────────────────────────────────
    private final ClientService              service = new ClientService();
    private       ObservableList<Client>     data;

    // ── Initialisation ──────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Liaison colonnes ↔ propriétés du modèle
        colId    .setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom   .setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail .setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel   .setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Colonne Actions : boutons ✏️ et 🗑️
        // CORRECTION : TableCell<Client, Void> correspond à colActions<Client,Void>
        colActions.setCellFactory(col -> new TableCell<Client, Void>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDel  = new Button("🗑️");
            {
                btnEdit.setStyle("-fx-cursor:hand;");
                btnDel .setStyle("-fx-cursor:hand;-fx-text-fill:#f87171;");
                btnEdit.setOnAction(e ->
                        modifierClient(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e ->
                        supprimerClient(getTableView().getItems().get(getIndex())));
            }

            @Override
            // CORRECTION : signature exacte — Void (pas Object)
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnEdit, btnDel));
            }
        });

        chargerClients();
    }

    // ── Chargement des données ───────────────────────────────────────────────
    private void chargerClients() {
        data = FXCollections.observableArrayList(service.rechercherClients(""));
        clientTable.setItems(data);
    }

    // ── Recherche ────────────────────────────────────────────────────────────
    @FXML
    private void handleSearch() {
        data.setAll(service.rechercherClients(searchField.getText()));
    }

    // ── Ajout ────────────────────────────────────────────────────────────────
    @FXML
    private void showAddDialog() {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Client");
        dialog.setHeaderText("Remplir les informations du client");

        TextField nom    = new TextField(); nom   .setPromptText("Nom");
        TextField prenom = new TextField(); prenom.setPromptText("Prénom");
        TextField email  = new TextField(); email .setPromptText("Email");
        TextField tel    = new TextField(); tel   .setPromptText("Téléphone");
        TextField cin    = new TextField(); cin   .setPromptText("CIN");

        VBox content = new VBox(10, nom, prenom, email, tel, cin);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK)
                return new Client(nom.getText(), prenom.getText(),
                        email.getText(), tel.getText(), cin.getText());
            return null;
        });

        dialog.showAndWait().ifPresent(c -> {
            try {
                service.ajouterClient(c);
                chargerClients();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        });
    }

    // ── Modification ─────────────────────────────────────────────────────────
    private void modifierClient(Client c) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Modifier Client");
        dialog.setHeaderText("Modifier les informations de " + c.getNom());

        TextField nom    = new TextField(c.getNom());
        TextField prenom = new TextField(c.getPrenom());
        TextField email  = new TextField(c.getEmail());
        TextField tel    = new TextField(c.getTelephone());

        VBox content = new VBox(10, nom, prenom, email, tel);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                c.setNom(nom.getText());
                c.setPrenom(prenom.getText());
                c.setEmail(email.getText());
                c.setTelephone(tel.getText());
                return c;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            try {
                service.modifierClient(updated);
                chargerClients();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        });
    }

    // ── Suppression ──────────────────────────────────────────────────────────
    private void supprimerClient(Client c) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + c.getNom() + " " + c.getPrenom() + " ?");
        confirm.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .ifPresent(r -> {
                    service.supprimerClient(c.getId());
                    chargerClients();
                });
    }
}