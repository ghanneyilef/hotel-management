package com.hotel.service;

import com.hotel.dao.ChambreDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.model.Chambre;
import com.hotel.model.Reservation;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ChambreDAO     chambreDAO     = new ChambreDAO();

    public Reservation creerReservation(int clientId, int chambreId,
                                        LocalDate arrivee, LocalDate depart) {

        // Validation des dates
        if (!arrivee.isBefore(depart))
            throw new IllegalArgumentException("La date d'arrivée doit être avant la date de départ");

        if (arrivee.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Impossible de réserver dans le passé");

        // Vérification disponibilité
        if (!reservationDAO.verifierDisponibilite(chambreId, arrivee, depart))
            throw new IllegalStateException("Cette chambre n'est pas disponible pour ces dates");

        // Calcul montant
        Chambre chambre = chambreDAO.findById(chambreId);
        long nbNuits = ChronoUnit.DAYS.between(arrivee, depart);
        double montant = chambre.getPrixNuit() * nbNuits;

        Reservation resa = new Reservation(clientId, chambreId, arrivee, depart);
        resa.setMontantTotal(montant);

        if (!reservationDAO.insert(resa))
            throw new RuntimeException("Erreur lors de la création de la réservation");

        // Mettre à jour le statut de la chambre
        chambreDAO.updateStatut(chambreId, "OCCUPEE");

        return resa;
    }

    public void annulerReservation(int reservationId) {
        Reservation resa = reservationDAO.findById(reservationId);
        if (resa == null)
            throw new IllegalArgumentException("Réservation introuvable");

        reservationDAO.updateStatut(reservationId, "ANNULEE");
        chambreDAO.updateStatut(resa.getChambreId(), "LIBRE");
    }

    public List<Reservation> toutesReservations() {
        return reservationDAO.findAll();
    }
}