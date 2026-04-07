package com.hotel.dao;

import com.hotel.model.Paiement;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PaiementDAO {

    // ─── Récupérer tous les paiements (avec JOIN) ────────────────
    public List<Paiement> findAll() {
        List<Paiement> list = new ArrayList<>();
        String sql = """
            SELECT p.*,
                   c.nom || ' ' || c.prenom AS nom_client,
                   ch.numero               AS num_chambre
            FROM   paiements p
            JOIN   reservations r  ON r.id  = p.reservation_id
            JOIN   clients      c  ON c.id  = r.client_id
            JOIN   chambres     ch ON ch.id = r.chambre_id
            ORDER  BY p.date_paiement DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ─── Paiements d'une réservation précise ────────────────────
    public List<Paiement> findByReservation(int reservationId) {
        List<Paiement> list = new ArrayList<>();
        String sql = """
            SELECT p.*,
                   c.nom || ' ' || c.prenom AS nom_client,
                   ch.numero               AS num_chambre
            FROM   paiements p
            JOIN   reservations r  ON r.id  = p.reservation_id
            JOIN   clients      c  ON c.id  = r.client_id
            JOIN   chambres     ch ON ch.id = r.chambre_id
            WHERE  p.reservation_id = ?
            ORDER  BY p.date_paiement DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ─── Insérer un paiement ────────────────────────────────────
    public boolean insert(Paiement p) {
        String sql = """
            INSERT INTO paiements
                (reservation_id, montant, date_paiement, mode_paiement, statut)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, p.getReservationId());
            ps.setDouble(2, p.getMontant());
            ps.setTimestamp(3, Timestamp.valueOf(
                    p.getDatePaiement() != null
                            ? p.getDatePaiement() : LocalDateTime.now()));
            ps.setString(4, p.getModePaiement());
            ps.setString(5, p.getStatut());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ─── Mettre à jour le statut d'un paiement ──────────────────
    public boolean updateStatut(int id, String statut) {
        String sql = "UPDATE paiements SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt   (2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ─── Supprimer un paiement ──────────────────────────────────
    public boolean delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM paiements WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ─── Total encaissé (toutes réservations) ───────────────────
    public double totalEncaisse() {
        String sql = "SELECT COALESCE(SUM(montant), 0) FROM paiements WHERE statut = 'PAYE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // ─── Mapper un ResultSet → Paiement ─────────────────────────
    private Paiement mapRow(ResultSet rs) throws SQLException {
        Paiement p = new Paiement();
        p.setId            (rs.getInt      ("id"));
        p.setReservationId (rs.getInt      ("reservation_id"));
        p.setMontant       (rs.getDouble   ("montant"));
        p.setModePaiement  (rs.getString   ("mode_paiement"));
        p.setStatut        (rs.getString   ("statut"));
        Timestamp ts = rs.getTimestamp("date_paiement");
        if (ts != null) p.setDatePaiement(ts.toLocalDateTime());
        p.setNomClient     (rs.getString   ("nom_client"));
        p.setNumeroChambre (rs.getString   ("num_chambre"));
        return p;
    }
}