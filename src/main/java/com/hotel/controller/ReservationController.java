package com.hotel.controller;

import com.hotel.model.Chambre;
import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.service.ChambreService;
import com.hotel.service.ClientService;
import com.hotel.service.ReservationService;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.List;

public class ReservationController {

    @FXML private TableView<Reservation>              reservationTable;
    @FXML private TableColumn<Reservation, Integer>   colId;
    @FXML private TableColumn<Reservation, String>    colClient;
    @FXML private TableColumn<Reservation, String>    colChambre;
    @FXML private TableColumn<Reservation, LocalDate> colArrivee;
    @FXML private TableColumn<Reservation, LocalDate> colDepart;
    @FXML private TableColumn<Reservation, Double>    colMontant;
    @FXML private TableColumn<Reservation, String>    colStatut;
    @FXML private TableColumn<Reservation, Void>      colActions;

    private final ReservationService service       = new ReservationService();
    private final ClientService      clientService = new ClientService();
    private final ChambreService     chambreService= new ChambreService();
    private ObservableList<Reservation> data;

    @FXML
    public void initialize() {
        colId     .setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient .setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        colChambre.setCellValueFactory(new PropertyValueFactory<>("numeroChambre"));
        colArrivee.setCellValueFactory(new PropertyValueFactory<>("dateArrivee"));
        colDepart .setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colStatut .setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Badge coloré statut
        colStatut.setCellFactory(col -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(val);
                String color = switch (val) {
                    case "CONFIRMEE" -> "#34d399";
                    case "ANNULEE"   -> "#f87171";
                    default          -> "#f59e0b";
                };
                badge.setStyle("-fx-background-color:" + color +
                        ";-fx-text-fill:white;-fx-padding:2 8;" +
                        "-fx-background-radius:10;-fx-font-size:11;");
                setGraphic(badge);
                setText(null);
            }
        });

        colActions.setCellFactory(col -> new TableCell<Reservation, Void>() {
            private final Button btnAnnuler = new Button("❌ Annuler");
            {
                btnAnnuler.setStyle("-fx-cursor:hand;-fx-text-fill:#f87171;");
                btnAnnuler.setOnAction(e ->
                        annulerReservation(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Reservation r = getTableView().getItems().get(getIndex());
                setGraphic("ANNULEE".equals(r.getStatut()) ? null : btnAnnuler);
            }
        });

        chargerReservations();
    }

    private void chargerReservations() {
        data = FXCollections.observableArrayList(service.toutesReservations());
        reservationTable.setItems(data);
    }

    @FXML
    private void showAddDialog() {
        List<Client>  clients  = clientService.rechercherClients("");
        List<Chambre> chambres = chambreService.chambresDisponibles();

        if (clients.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Aucun client enregistré.").show();
            return;
        }
        if (chambres.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Aucune chambre disponible.").show();
            return;
        }

        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Réservation");
        dialog.setHeaderText("Créer une réservation");

        ComboBox<Client>  clientBox  = new ComboBox<>(FXCollections.observableArrayList(clients));
        clientBox.setPromptText("Choisir un client");
        clientBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Client c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNom() + " " + c.getPrenom());
            }
        });
        clientBox.setButtonCell(clientBox.getCellFactory().call(null));

        ComboBox<Chambre> chambreBox = new ComboBox<>(FXCollections.observableArrayList(chambres));
        chambreBox.setPromptText("Choisir une chambre");
        chambreBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Chambre ch, boolean empty) {
                super.updateItem(ch, empty);
                setText(empty || ch == null ? null :
                        "N°" + ch.getNumero() + " — " + ch.getType() + " — " + ch.getPrixNuit() + " DT/nuit");
            }
        });
        chambreBox.setButtonCell(chambreBox.getCellFactory().call(null));

        DatePicker arrivee = new DatePicker(LocalDate.now());
        DatePicker depart  = new DatePicker(LocalDate.now().plusDays(1));

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Client :"),  clientBox);
        grid.addRow(1, new Label("Chambre :"), chambreBox);
        grid.addRow(2, new Label("Arrivée :"), arrivee);
        grid.addRow(3, new Label("Départ :"),  depart);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (clientBox.getValue() == null || chambreBox.getValue() == null) {
                    new Alert(Alert.AlertType.ERROR, "Veuillez sélectionner un client et une chambre.").show();
                    return null;
                }
                return new Reservation(
                        clientBox.getValue().getId(),
                        chambreBox.getValue().getId(),
                        arrivee.getValue(),
                        depart.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            try {
                service.creerReservation(r.getClientId(), r.getChambreId(),
                        r.getDateArrivee(), r.getDateDepart());
                chargerReservations();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        });
    }

    private void annulerReservation(Reservation r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Annuler la réservation #" + r.getId() + " ?");
        confirm.showAndWait()
                .filter(res -> res == ButtonType.OK)
                .ifPresent(res -> {
                    try {
                        service.annulerReservation(r.getId());
                        chargerReservations();
                    } catch (Exception e) {
                        new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                    }
                });
    }
}
