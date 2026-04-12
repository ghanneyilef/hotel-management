package com.hotel.controller;

import com.hotel.model.Chambre;
import com.hotel.model.Employe;
import com.hotel.service.ChambreService;
import com.hotel.service.EmployeService;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class EmployeController {

    // ── TableView ─────────────────────────────────────────────────────────
    @FXML private TableView<Employe>              employeTable;
    @FXML private TableColumn<Employe, Integer>   colId;
    @FXML private TableColumn<Employe, Integer>   colNbCh;
    @FXML private TableColumn<Employe, String>    colNom;
    @FXML private TableColumn<Employe, String>    colPrenom;
    @FXML private TableColumn<Employe, String>    colRole;
    // CORRECTION : type <Employe, String> pour updateItem(String, boolean)
    @FXML private TableColumn<Employe, String>    colStatut;
    // CORRECTION : type <Employe, Void> pour updateItem(Void, boolean)
    @FXML private TableColumn<Employe, Void>      colActions;

    // ── Autres composants FXML ────────────────────────────────────────────
    @FXML private ListView<String>  chambresList;
    @FXML private ComboBox<String>  filtreRole;
    @FXML private TextField         searchField;
    @FXML private Label             alerteLabel;
    @FXML private Label             limiteLabel;

    // ── Services ──────────────────────────────────────────────────────────
    private final EmployeService    service        = new EmployeService();
    private final ChambreService    chambreService = new ChambreService();

    // ── État ──────────────────────────────────────────────────────────────
    private ObservableList<Employe> data;
    private Employe                 selected;

    // ── Initialisation ────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Liaison colonnes ↔ propriétés du modèle
        colId    .setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom   .setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colRole  .setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colNbCh  .setCellValueFactory(new PropertyValueFactory<>("nbChambres"));

        // Badge coloré pour la colonne Statut
        // CORRECTION : updateItem(String val, boolean empty) — correspond à <Employe, String>
        colStatut.setCellFactory(col -> new TableCell<Employe, String>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(val);
                String color = switch (val) {
                    case "ACTIF"  -> "#34d399";
                    case "CONGE"  -> "#f59e0b";
                    default       -> "#f87171";
                };
                badge.setStyle("-fx-background-color:" + color +
                        ";-fx-text-fill:white;-fx-padding:2 8;" +
                        "-fx-background-radius:10;-fx-font-size:11;");
                setGraphic(badge);
                setText(null);
            }
        });

        // Affichage "X/2" en rouge si plein
        // CORRECTION : updateItem(Integer val, boolean empty) — correspond à <Employe, Integer>
        colNbCh.setCellFactory(col -> new TableCell<Employe, Integer>() {
            @Override
            protected void updateItem(Integer val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); return; }
                setText(val + "/2");
                setStyle(val >= 2
                        ? "-fx-text-fill:#f87171;-fx-font-weight:bold;" : "");
            }
        });

        // Boutons Assigner / Supprimer par ligne
        // CORRECTION : updateItem(Void v, boolean empty) — correspond à <Employe, Void>
        colActions.setCellFactory(col -> new TableCell<Employe, Void>() {
            private final Button bA = new Button("Assigner");
            private final Button bD = new Button("Suppr.");
            {
                bA.setStyle("-fx-background-color:#4f8ef7;-fx-text-fill:white;-fx-font-size:11;");
                bD.setStyle("-fx-background-color:#f87171;-fx-text-fill:white;-fx-font-size:11;");
                bA.setOnAction(e ->
                        showAssignDialog(getTableView().getItems().get(getIndex())));
                bD.setOnAction(e ->
                        supprimerEmploye(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(4, bA, bD));
            }
        });

        // ComboBox filtre rôle
        filtreRole.setItems(FXCollections.observableArrayList(
                "Tous", "MENAGE", "RECEPTION", "SERVICE", "MAINTENANCE"));
        filtreRole.setValue("Tous");

        // Sélection → panneau droite avec chambres
        employeTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, emp) -> {
                    if (emp != null) { selected = emp; afficherChambres(emp); }
                });

        chargerAlerte();
        chargerEmployes();
    }

    // ── Chargement ────────────────────────────────────────────────────────
    private void chargerEmployes() {
        data = FXCollections.observableArrayList(service.tousLesEmployes());
        employeTable.setItems(data);
    }

    // ── Affichage chambres d'un employé ───────────────────────────────────
    private void afficherChambres(Employe emp) {
        ObservableList<String> noms = FXCollections.observableArrayList(
                chambreService.toutesChambres().stream()
                        .filter(ch -> !service.getEmployesDeChambre(ch.getId())
                                .stream()
                                .filter(e -> e.getId() == emp.getId())
                                .toList().isEmpty())
                        .map(ch -> "Chambre " + ch.getNumero() + " — " + ch.getType())
                        .toList());
        chambresList.setItems(noms);
        limiteLabel.setText(emp.getNbChambres() >= 2
                ? "Limite atteinte — 2 chambres max." : "");
    }

    // ── Alerte chambres non couvertes ─────────────────────────────────────
    private void chargerAlerte() {
        long n = service.countChambresNonCouvertes();
        alerteLabel.setText(n > 0
                ? "⚠  " + n + " chambre(s) occupée(s) sans employé assigné !"
                : "");
        alerteLabel.setManaged(n > 0);
        alerteLabel.setVisible(n > 0);
    }

    // ── Recherche ────────────────────────────────────────────────────────
    @FXML
    private void handleSearch() {
        String q = searchField.getText().toLowerCase();
        data.setAll(service.tousLesEmployes().stream()
                .filter(e -> e.getNomComplet().toLowerCase().contains(q)
                        || e.getRole().toLowerCase().contains(q))
                .toList());
    }

    // ── Filtre par rôle ──────────────────────────────────────────────────
    @FXML
    private void handleFiltreRole() {
        String role = filtreRole.getValue();
        if ("Tous".equals(role)) { chargerEmployes(); return; }
        data.setAll(service.tousLesEmployes().stream()
                .filter(e -> e.getRole().equals(role)).toList());
    }

    // ── Ajout d'un employé ───────────────────────────────────────────────
    @FXML
    private void showAddDialog() {
        Dialog<Employe> dialog = new Dialog<>();
        dialog.setTitle("Nouvel Employé");
        dialog.setHeaderText("Remplir les informations de l'employé");

        TextField        nom    = new TextField();       nom   .setPromptText("Nom");
        TextField        prenom = new TextField();       prenom.setPromptText("Prénom");
        ComboBox<String> role   = new ComboBox<>(FXCollections.observableArrayList(
                "MENAGE", "RECEPTION", "SERVICE", "MAINTENANCE"));
        role.setPromptText("Rôle");
        TextField        tel    = new TextField();       tel   .setPromptText("Téléphone");
        ComboBox<String> statut = new ComboBox<>(FXCollections.observableArrayList(
                "ACTIF", "CONGE", "INACTIF"));
        statut.setValue("ACTIF");

        VBox content = new VBox(10, nom, prenom, role, tel, statut);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Employe e = new Employe(nom.getText(), prenom.getText(),
                        role.getValue(), tel.getText());
                e.setStatut(statut.getValue());
                return e;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(e -> {
            try {
                service.ajouterEmploye(e);
                chargerEmployes();
                chargerAlerte();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        });
    }

    // ── Assignation chambre (appelé depuis bouton FXML ou bouton ligne) ───
    @FXML
    private void showAssignDialogFxml() {
        if (selected != null) showAssignDialog(selected);
    }

    private void showAssignDialog(Employe emp) {
        if (emp.getNbChambres() >= 2) {
            new Alert(Alert.AlertType.WARNING,
                    emp.getNomComplet() + " a déjà 2 chambres assignées.").show();
            return;
        }
        List<Chambre> chambres = chambreService.toutesChambres();
        if (chambres.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Aucune chambre disponible.").show();
            return;
        }
        ChoiceDialog<Chambre> dialog = new ChoiceDialog<>(chambres.get(0), chambres);
        dialog.setTitle("Assigner une chambre");
        dialog.setHeaderText("Choisir une chambre pour " + emp.getNomComplet());
        dialog.showAndWait().ifPresent(ch -> {
            try {
                service.assignerAChambre(ch.getId(), emp.getId());
                chargerEmployes();
                chargerAlerte();
                afficherChambres(emp);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        });
    }

    // ── Suppression ──────────────────────────────────────────────────────
    private void supprimerEmploye(Employe emp) {
        new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + emp.getNomComplet() + " ?")
                .showAndWait()
                .filter(r -> r == ButtonType.OK)
                .ifPresent(r -> {
                    service.supprimer(emp.getId());
                    chargerEmployes();
                    chargerAlerte();
                });
    }
}