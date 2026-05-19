package com.hotel.service;

import com.hotel.model.AlerteNotification;
import com.hotel.model.AlerteNotification.Niveau;
import com.hotel.model.AlerteNotification.Type;
import com.hotel.model.Chambre;
import com.hotel.model.Employe;
import com.hotel.model.Paiement;
import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Génère toutes les alertes actives du système hôtelier.
 *
 * Appelé par AlerteController à l'ouverture du panneau
 * et périodiquement via un Timeline JavaFX.
 */
public class AlerteService {

    private static final int MAX_CHAMBRES_PAR_EMPLOYE = 3;

    private final ChambreService     chambreService     = new ChambreService();
    private final EmployeService     employeService     = new EmployeService();
    private final ReservationService reservationService = new ReservationService();
    private final PaiementService    paiementService    = new PaiementService();

    // ── Point d'entrée ────────────────────────────────────────────────────

    /**
     * Retourne toutes les alertes triées CRITIQUE → ATTENTION → INFO.
     */
    public List<AlerteNotification> genererToutesAlertes() {
        List<AlerteNotification> alertes = new ArrayList<>();
        alertes.addAll(alertesChambresNonCouvertes());
        alertes.addAll(alertesEmployesSurcharges());
        alertes.addAll(alertesDepartsImminents());
        alertes.addAll(alertesPaiementsEnAttente());
        alertes.sort((a, b) -> a.getNiveau().ordinal() - b.getNiveau().ordinal());
        return alertes;
    }

    /** Nombre d'alertes actives — pour le badge rouge sur le bouton */
    public long countAlertes() {
        return genererToutesAlertes().size();
    }

    // ── 1. Chambres sans employé ──────────────────────────────────────────

    public List<AlerteNotification> alertesChambresNonCouvertes() {
        List<AlerteNotification> alertes = new ArrayList<>();
        for (Chambre ch : chambreService.toutesChambres()) {
            List<Employe> employes = employeService.getEmployesDeChambre(ch.getId());
            if (employes.isEmpty()) {
                boolean occupee = "OCCUPEE".equals(ch.getStatut());
                Niveau  niveau  = occupee ? Niveau.CRITIQUE : Niveau.ATTENTION;
                String  msg     = occupee
                        ? "Chambre " + ch.getNumero() + " occupée — aucun employé assigné !"
                        : "Chambre " + ch.getNumero() + " (" + ch.getType() + ") sans employé";
                alertes.add(new AlerteNotification(
                        Type.CHAMBRE_SANS_EMPLOYE, niveau, msg,
                        ch.getId(), ch.getNumero()
                ));
            }
        }
        return alertes;
    }

    // ── 2. Employés en surcharge ──────────────────────────────────────────

    public List<AlerteNotification> alertesEmployesSurcharges() {
        List<AlerteNotification> alertes = new ArrayList<>();
        for (Employe emp : employeService.tousLesEmployes()) {
            if (emp.getNbChambres() > MAX_CHAMBRES_PAR_EMPLOYE) {
                String msg = emp.getNomComplet() + " gère " + emp.getNbChambres()
                        + " chambres (max recommandé : " + MAX_CHAMBRES_PAR_EMPLOYE + ")";
                alertes.add(new AlerteNotification(
                        Type.EMPLOYE_SURCHARGE, Niveau.ATTENTION, msg, 0, "—"
                ));
            }
        }
        return alertes;
    }

    // ── 3. Départs imminents ──────────────────────────────────────────────

    public List<AlerteNotification> alertesDepartsImminents() {
        List<AlerteNotification> alertes = new ArrayList<>();
        LocalDate demain = LocalDate.now().plusDays(1);
        for (Reservation r : reservationService.toutesReservations()) {
            if (!"CONFIRMEE".equals(r.getStatut())) continue;
            boolean auj    = r.getDateDepart().isEqual(LocalDate.now());
            boolean dm     = r.getDateDepart().isEqual(demain);
            if (auj || dm) {
                String quand = auj ? "aujourd'hui" : "demain";
                String msg   = "Départ " + quand + " — Ch." + r.getNumeroChambre()
                        + "  |  " + r.getNomClient();
                alertes.add(new AlerteNotification(
                        Type.DEPART_IMMINENT, Niveau.INFO, msg,
                        r.getChambreId(), r.getNumeroChambre()
                ));
            }
        }
        return alertes;
    }

    // ── 4. Paiements en attente ───────────────────────────────────────────

    public List<AlerteNotification> alertesPaiementsEnAttente() {
        List<AlerteNotification> alertes = new ArrayList<>();
        for (Paiement p : paiementService.tousLesPaiements()) {
            if ("EN_ATTENTE".equals(p.getStatut())) {
                String client = p.getNomClient() != null ? p.getNomClient() : "N/A";
                String chambre = p.getNumeroChambre() != null ? p.getNumeroChambre() : "—";
                String msg = p.getMontant() + " DT en attente  |  " + client
                        + "  |  Ch." + chambre;
                alertes.add(new AlerteNotification(
                        Type.PAIEMENT_EN_ATTENTE, Niveau.ATTENTION, msg, 0, chambre
                ));
            }
        }
        return alertes;
    }
}