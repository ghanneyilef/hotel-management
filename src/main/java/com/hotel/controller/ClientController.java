package com.hotel.controller;

import com.hotel.model.Client;
import com.hotel.service.ClientService;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class ClientController {

    // ── TableView ────────────────────────────────────────────────────────────
    @FXML private TableView<Client>            clientTable;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String>  colNom;
    @FXML private TableColumn<Client, String>  colPrenom;
    @FXML private TableColumn<Client, String>  colEmail;
    @FXML private TableColumn<Client, String>  colTel;
    @FXML private TableColumn<Client, Void>    colActions;

    // ── Topbar ───────────────────────────────────────────────────────────────
    @FXML private TextField searchField;

    // ── Stats ────────────────────────────────────────────────────────────────
    @FXML private Label statTotal;
    @FXML private Label statDernier;
    @FXML private Label statRecherche;
    @FXML private Label tableCountLabel;

    // ── Service ──────────────────────────────────────────────────────────────
    private final ClientService          service = new ClientService();
    private       ObservableList<Client> data;

    // ── Init ─────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        colId    .setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom   .setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail .setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel   .setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Colonne NOM avec avatar initiales
        colNom.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nom, boolean empty) {
                super.updateItem(nom, empty);
                if (empty || nom == null) { setGraphic(null); return; }

                String initiale = nom.substring(0, 1).toUpperCase();
                Label lblInit = new Label(initiale);
                lblInit.setStyle("-fx-text-fill: #185FA5; -fx-font-size: 11px; -fx-font-weight: bold;");

                StackPane avatar = new StackPane(lblInit);
                avatar.setMinSize(28, 28);
                avatar.setMaxSize(28, 28);
                avatar.setStyle("-fx-background-color: #E6F1FB; -fx-background-radius: 14;");

                Label lblNom = new Label(nom);
                lblNom.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A1A2E;");

                HBox cell = new HBox(9, avatar, lblNom);
                cell.setAlignment(Pos.CENTER_LEFT);
                setGraphic(cell);
                setText(null);
            }
        });

        // Colonne ACTIONS avec boutons stylisés
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDel  = new Button("Supprimer");
            private final HBox   box     = new HBox(6, btnEdit, btnDel);
            {
                btnEdit.getStyleClass().add("btn-edit");
                btnDel .getStyleClass().add("btn-delete");
                box.setAlignment(Pos.CENTER_LEFT);
                btnEdit.setOnAction(e -> modifierClient(getTableView().getItems().get(getIndex())));
                btnDel .setOnAction(e -> supprimerClient(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        chargerClients();
    }

    // ── Données ──────────────────────────────────────────────────────────────
    private void chargerClients() {
        data = FXCollections.observableArrayList(service.rechercherClients(""));
        clientTable.setItems(data);
        updateStats(data.size(), data.size());
    }

    private void updateStats(int total, int recherche) {
        if (statTotal    != null) statTotal.setText(String.valueOf(total));
        if (statRecherche != null) statRecherche.setText(String.valueOf(recherche));
        if (tableCountLabel != null)
            tableCountLabel.setText(recherche + " client" + (recherche > 1 ? "s" : ""));
        if (statDernier != null && !data.isEmpty()) {
            Client last = data.get(data.size() - 1);
            statDernier.setText(last.getPrenom() + " " + last.getNom());
        }
    }

    // ── Recherche ────────────────────────────────────────────────────────────
    @FXML
    private void handleSearch() {
        java.util.List<Client> result = service.rechercherClients(searchField.getText());
        data.setAll(result);
        updateStats(data.size(), result.size());
    }

    // ── Ajout ────────────────────────────────────────────────────────────────
    @FXML
    private void showAddDialog() {
        Dialog<Client> dialog = creerDialog("Nouveau client", "Remplir les informations du client");

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
        Dialog<Client> dialog = creerDialog("Modifier client", "Modifier les informations de " + c.getNom());

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
        confirm.setHeaderText("Confirmer la suppression");
        confirm.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .ifPresent(r -> {
                    service.supprimerClient(c.getId());
                    chargerClients();
                });
    }

    // ── Utilitaire ───────────────────────────────────────────────────────────
    private Dialog<Client> creerDialog(String titre, String header) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(titre);
        dialog.setHeaderText(header);
        return dialog;
    }
}
