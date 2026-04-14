package com.hotel.controller;

import com.hotel.model.Chambre;
import com.hotel.model.Employe;
import com.hotel.service.ChambreService;
import com.hotel.service.EmployeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class EmployeController {

    // ===== TABLE =====
    @FXML private TableView<Employe> employeTable;

    @FXML private TableColumn<Employe, Integer> colId;
    @FXML private TableColumn<Employe, String> colNom;
    @FXML private TableColumn<Employe, String> colPrenom;
    @FXML private TableColumn<Employe, String> colRole;
    @FXML private TableColumn<Employe, String> colTel;
    @FXML private TableColumn<Employe, String> colStatut;
    @FXML private TableColumn<Employe, Integer> colNbCh;
    @FXML private TableColumn<Employe, Void> colActions;

    // ===== UI =====
    @FXML private ComboBox<String> filtreRole;
    @FXML private TextField searchField;
    @FXML private ListView<String> chambresList;
    @FXML private Label alerteLabel;
    @FXML private Label limiteLabel;

    // ===== SERVICES =====
    private final EmployeService service = new EmployeService();
    private final ChambreService chambreService = new ChambreService();

    private ObservableList<Employe> data;

    // ================= INIT =================
    @FXML
    public void initialize() {

        // Columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colNbCh.setCellValueFactory(new PropertyValueFactory<>("nbChambres"));

        // Actions column
        colActions.setCellFactory(col -> new TableCell<>() {

            private final Button assignBtn = new Button("Assign");
            private final Button deleteBtn = new Button("Delete");

            {
                assignBtn.setOnAction(e -> {
                    Employe emp = getTableView().getItems().get(getIndex());
                    assignChambre(emp);
                });

                deleteBtn.setOnAction(e -> {
                    Employe emp = getTableView().getItems().get(getIndex());
                    service.supprimer(emp.getId());
                    load();
                });

                assignBtn.setStyle("-fx-background-color:#4f8ef7; -fx-text-fill:white;");
                deleteBtn.setStyle("-fx-background-color:#f87171; -fx-text-fill:white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, assignBtn, deleteBtn));
            }
        });

        // FIX VBox / ComboBox null safe
        if (filtreRole != null) {
            filtreRole.setItems(FXCollections.observableArrayList(
                    "Tous", "MENAGE", "RECEPTION", "SERVICE", "MAINTENANCE"
            ));
            filtreRole.setValue("Tous");
        }

        load();
    }

    // ================= LOAD =================
    private void load() {
        data = FXCollections.observableArrayList(service.tousLesEmployes());
        employeTable.setItems(data);
    }

    // ================= ADD =================
    @FXML
    private void showAddDialog() {

        TextField nom = new TextField();
        TextField prenom = new TextField();
        TextField tel = new TextField();

        ComboBox<String> role = new ComboBox<>(
                FXCollections.observableArrayList("MENAGE", "RECEPTION", "SERVICE", "MAINTENANCE")
        );

        ComboBox<String> statut = new ComboBox<>(
                FXCollections.observableArrayList("ACTIF", "CONGE", "INACTIF")
        );
        statut.setValue("ACTIF");

        VBox box = new VBox(10, nom, prenom, tel, role, statut);
        box.setStyle("-fx-padding:10;");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter Employé");
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {

                Employe e = new Employe(
                        nom.getText(),
                        prenom.getText(),
                        role.getValue(),
                        tel.getText()
                );

                e.setStatut(statut.getValue());

                service.ajouterEmploye(e);
                load();
            }
        });
    }

    // ================= ASSIGN =================
    private void assignChambre(Employe emp) {

        List<Chambre> chambres = chambreService.toutesChambres();
        if (chambres.isEmpty()) return;

        ChoiceDialog<Chambre> dialog =
                new ChoiceDialog<>(chambres.get(0), chambres);

        dialog.setTitle("Assignation");

        dialog.showAndWait().ifPresent(ch -> {
            service.assignerAChambre(ch.getId(), emp.getId());
            load();
        });
    }

    // ================= FILTER =================
    @FXML
    private void handleFiltreRole() {

        if (filtreRole == null) return;

        String role = filtreRole.getValue();

        if (role == null || role.equals("Tous")) {
            load();
            return;
        }

        data.setAll(service.tousLesEmployes().stream()
                .filter(e -> e.getRole().equals(role))
                .toList());
    }

    // ================= SEARCH =================
    @FXML
    private void handleSearch() {

        String q = searchField.getText().toLowerCase();

        data.setAll(service.tousLesEmployes().stream()
                .filter(e -> e.getNom().toLowerCase().contains(q)
                        || e.getPrenom().toLowerCase().contains(q)
                        || e.getRole().toLowerCase().contains(q))
                .toList());
    }
}