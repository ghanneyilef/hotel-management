package com.hotel.controller;

import com.hotel.model.Chambre;
import com.hotel.service.ChambreService;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChambreController {

    @FXML private TableView<Chambre>              chambreTable;
    @FXML private TableColumn<Chambre, Integer>   colId;
    @FXML private TableColumn<Chambre, String>    colNumero;
    @FXML private TableColumn<Chambre, String>    colType;
    @FXML private TableColumn<Chambre, Double>    colPrix;
    @FXML private TableColumn<Chambre, String>    colStatut;
    @FXML private TableColumn<Chambre, Void>      colActions;

    private final ChambreService service = new ChambreService();
    private ObservableList<Chambre> data;

    @FXML
    public void initialize() {
        colId    .setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colType  .setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrix  .setCellValueFactory(new PropertyValueFactory<>("prixNuit"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Badge coloré pour le statut
        colStatut.setCellFactory(col -> new TableCell<Chambre, String>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(val);
                String color = "LIBRE".equals(val) ? "#34d399" : "#f87171";
                badge.setStyle("-fx-background-color:" + color +
                        ";-fx-text-fill:white;-fx-padding:2 8;" +
                        "-fx-background-radius:10;-fx-font-size:11;");
                setGraphic(badge);
                setText(null);
            }
        });

        colActions.setCellFactory(col -> new TableCell<Chambre, Void>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDel  = new Button("🗑️");
            {
                btnEdit.setStyle("-fx-cursor:hand;");
                btnDel .setStyle("-fx-cursor:hand;-fx-text-fill:#f87171;");
                btnEdit.setOnAction(e ->
                        modifierChambre(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e ->
                        supprimerChambre(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnEdit, btnDel));
            }
        });

        chargerChambres();
    }

    private void chargerChambres() {
        data = FXCollections.observableArrayList(service.toutesChambres());
        chambreTable.setItems(data);
    }

    @FXML
    private void showAddDialog() {
        Dialog<Chambre> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Chambre");
        dialog.setHeaderText("Remplir les informations de la chambre");

        TextField numero = new TextField(); numero.setPromptText("Numéro (ex: 101)");
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Simple", "Double", "Suite");
        type.setPromptText("Type");
        TextField prix = new TextField(); prix.setPromptText("Prix/nuit (DT)");

        VBox content = new VBox(10, new Label("Numéro:"), numero,
                new Label("Type:"), type, new Label("Prix/nuit:"), prix);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    return new Chambre(numero.getText(),
                            type.getValue(),
                            Double.parseDouble(prix.getText()));
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Prix invalide.").show();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(c -> {
            try {
                service.ajouterChambre(c);
                chargerChambres();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        });
    }

    private void modifierChambre(Chambre c) {
        Dialog<Chambre> dialog = new Dialog<>();
        dialog.setTitle("Modifier Chambre");
        dialog.setHeaderText("Chambre n° " + c.getNumero());

        TextField numero = new TextField(c.getNumero());
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Simple", "Double", "Suite");
        type.setValue(c.getType());
        TextField prix = new TextField(String.valueOf(c.getPrixNuit()));
        ComboBox<String> statut = new ComboBox<>();
        statut.getItems().addAll("LIBRE", "OCCUPEE");
        statut.setValue(c.getStatut());

        VBox content = new VBox(10,
                new Label("Numéro:"), numero,
                new Label("Type:"), type,
                new Label("Prix/nuit:"), prix,
                new Label("Statut:"), statut);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                c.setNumero(numero.getText());
                c.setType(type.getValue());
                try { c.setPrixNuit(Double.parseDouble(prix.getText())); }
                catch (NumberFormatException ignored) {}
                c.setStatut(statut.getValue());
                return c;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            try {
                service.modifierChambre(updated);
                chargerChambres();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        });
    }

    private void supprimerChambre(Chambre c) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer la chambre n° " + c.getNumero() + " ?");
        confirm.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .ifPresent(r -> {
                    service.supprimerChambre(c.getId());
                    chargerChambres();
                });
    }
}
