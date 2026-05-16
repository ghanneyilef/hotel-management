package com.hotel.controller;

import com.hotel.model.Paiement;
import com.hotel.model.Reservation;
import com.hotel.service.PaiementService;
import com.hotel.service.ReservationService;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.util.List;

public class PaiementController {

    @FXML private TableView<Paiement>              paiementTable;
    @FXML private TableColumn<Paiement, Integer>   colId;
    @FXML private TableColumn<Paiement, String>    colClient;
    @FXML private TableColumn<Paiement, String>    colChambre;
    @FXML private TableColumn<Paiement, Double>    colMontant;
    @FXML private TableColumn<Paiement, String>    colMode;
    @FXML private TableColumn<Paiement, String>    colStatut;
    @FXML private TableColumn<Paiement, LocalDateTime> colDate;
    @FXML private TableColumn<Paiement, Void>      colActions;
    @FXML private ComboBox<String>                 filtreStatut;
    @FXML private Label                            lblTotal;

    private final PaiementService    service     = new PaiementService();
    private final ReservationService resaService = new ReservationService();
    private ObservableList<Paiement> data;

    @FXML
    public void initialize() {
        colId     .setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient .setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        colChambre.setCellValueFactory(new PropertyValueFactory<>("numeroChambre"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colMode   .setCellValueFactory(new PropertyValueFactory<>("modePaiement"));
        colStatut .setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate   .setCellValueFactory(new PropertyValueFactory<>("datePaiement"));


        colStatut.setCellFactory(col -> new TableCell<Paiement, String>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(val);
                String color = switch (val) {
                    case "PAYE"       -> "#34d399";
                    case "EN_ATTENTE" -> "#f59e0b";
                    case "REMBOURSE"  -> "#94a3b8";
                    default           -> "#f87171";
                };
                badge.setStyle("-fx-background-color:" + color +
                        ";-fx-text-fill:white;-fx-padding:2 8;" +
                        "-fx-background-radius:10;-fx-font-size:11;");
                setGraphic(badge);
                setText(null);
            }
        });

        colActions.setCellFactory(col -> new TableCell<Paiement, Void>() {
            private final Button btnDel = new Button("delete");
            {
                btnDel.setStyle("-fx-cursor:hand;-fx-text-fill:#f87171;");
                btnDel.setOnAction(e ->
                        supprimerPaiement(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnDel));
            }
        });


        filtreStatut.getItems().addAll("Tous", "PAYE", "EN_ATTENTE", "REMBOURSE");
        filtreStatut.setValue("Tous");

        chargerPaiements();
    }

    private void chargerPaiements() {
        List<Paiement> tous = service.tousLesPaiements();
        String filtre = filtreStatut.getValue();
        if (filtre != null && !"Tous".equals(filtre)) {
            tous = tous.stream().filter(p -> filtre.equals(p.getStatut())).toList();
        }
        data = FXCollections.observableArrayList(tous);
        paiementTable.setItems(data);

        double total = service.getTotalEncaisse();
        lblTotal.setText(String.format("Total encaissé : %.2f DT", total));
    }

    @FXML
    private void handleFiltre() {
        chargerPaiements();
    }

    @FXML
    private void showAddDialog() {
        List<Reservation> reservations = resaService.toutesReservations()
                .stream()
                .filter(r -> !"ANNULEE".equals(r.getStatut()))
                .toList();

        if (reservations.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Aucune réservation active disponible.").show();
            return;
        }

        Dialog<Paiement> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Paiement");
        dialog.setHeaderText("Enregistrer un paiement");

        ComboBox<Reservation> resaBox = new ComboBox<>(FXCollections.observableArrayList(reservations));
        resaBox.setPromptText("Choisir une réservation");
        resaBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Reservation r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null :
                        "#" + r.getId() + " — " + r.getNomClient() +
                        " — Chambre " + r.getNumeroChambre() +
                        " (" + r.getMontantTotal() + " DT)");
            }
        });
        resaBox.setButtonCell(resaBox.getCellFactory().call(null));

        TextField montantField = new TextField();
        montantField.setPromptText("Montant (DT)");


        resaBox.setOnAction(e -> {
            Reservation sel = resaBox.getValue();
            if (sel != null) montantField.setText(String.valueOf(sel.getMontantTotal()));
        });

        ComboBox<String> modeBox = new ComboBox<>();
        modeBox.getItems().addAll("CARTE", "ESPECES", "VIREMENT");
        modeBox.setValue("ESPECES");

        ComboBox<String> statutBox = new ComboBox<>();
        statutBox.getItems().addAll("PAYE", "EN_ATTENTE", "REMBOURSE");
        statutBox.setValue("PAYE");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Réservation :"), resaBox);
        grid.addRow(1, new Label("Montant :"),     montantField);
        grid.addRow(2, new Label("Mode :"),        modeBox);
        grid.addRow(3, new Label("Statut :"),      statutBox);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK && resaBox.getValue() != null) {
                try {
                    double montant = Double.parseDouble(montantField.getText());
                    return new Paiement(resaBox.getValue().getId(),
                            montant, modeBox.getValue(), statutBox.getValue());
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Montant invalide.").show();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            try {
                service.enregistrerPaiement(p.getReservationId(),
                        p.getMontant(), p.getModePaiement(), p.getStatut());
                chargerPaiements();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        });
    }

    private void supprimerPaiement(Paiement p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le paiement #" + p.getId() + " ?");
        confirm.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .ifPresent(r -> {
                    try {
                        service.supprimer(p.getId());
                        chargerPaiements();
                    } catch (Exception e) {
                        new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                    }
                });
    }
}
