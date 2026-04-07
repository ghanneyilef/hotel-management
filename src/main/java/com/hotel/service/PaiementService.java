package com.hotel.service;

import com.hotel.dao.PaiementDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.model.Paiement;
import com.hotel.model.Reservation;
import java.util.List;

public class PaiementService {

    private final PaiementDAO    dao     = new PaiementDAO();
    private final ReservationDAO resaDAO = new ReservationDAO();

    // ─── Enregistrer un nouveau paiement ────────────────────────
    public void enregistrerPaiement(int reservationId, double montant,
                                    String mode, String statut) {

        // Validation du montant
        if (montant <= 0)
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");

        // Vérifier que la réservation existe
        Reservation resa = resaDAO.findById(reservationId);
        if (resa == null)
            throw new IllegalArgumentException("Réservation introuvable (id=" + reservationId + ")");

        // Vérifier qu'il n'y a pas déjà un paiement PAYE sur cette réservation
        if ("PAYE".equals(statut)) {
            boolean dejaPaye = dao.findByReservation(reservationId)
                    .stream()
                    .anyMatch(p -> "PAYE".equals(p.getStatut()));
            if (dejaPaye)
                throw new IllegalStateException(
                        "Cette réservation est déjà marquée comme payée");
        }

        Paiement p = new Paiement(reservationId, montant, mode, statut);
        if (!dao.insert(p))
            throw new RuntimeException("Échec de l'insertion du paiement");
    }

    // ─── Lister tous les paiements ──────────────────────────────
    public List<Paiement> tousLesPaiements() {
        return dao.findAll();
    }

    // ─── Paiements d'une réservation ────────────────────────────
    public List<Paiement> paiementsDeReservation(int reservationId) {
        return dao.findByReservation(reservationId);
    }

    // ─── Changer le statut d'un paiement ────────────────────────
    public void changerStatut(int paiementId, String nouveauStatut) {
        if (!List.of("PAYE", "EN_ATTENTE", "REMBOURSE").contains(nouveauStatut))
            throw new IllegalArgumentException("Statut invalide : " + nouveauStatut);
        if (!dao.updateStatut(paiementId, nouveauStatut))
            throw new RuntimeException("Mise à jour échouée");
    }

    // ─── Supprimer un paiement ──────────────────────────────────
    public void supprimer(int paiementId) {
        if (!dao.delete(paiementId))
            throw new RuntimeException("Suppression échouée");
    }

    // ─── Résumé financier ───────────────────────────────────────
    public double getTotalEncaisse() {
        return dao.totalEncaisse();
    }
}