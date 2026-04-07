package com.hotel.dao;

import com.hotel.model.Reservation;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = """
            SELECT r.*, c.nom || ' ' || c.prenom AS nom_client,
                   ch.numero AS num_chambre
            FROM reservations r
            JOIN clients c ON c.id = r.client_id
            JOIN chambres ch ON ch.id = r.chambre_id
            ORDER BY r.date_arrivee DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setClientId(rs.getInt("client_id"));
                r.setChambreId(rs.getInt("chambre_id"));
                r.setDateArrivee(rs.getDate("date_arrivee").toLocalDate());
                r.setDateDepart(rs.getDate("date_depart").toLocalDate());
                r.setStatut(rs.getString("statut"));
                r.setMontantTotal(rs.getDouble("montant_total"));
                r.setNomClient(rs.getString("nom_client"));
                r.setNumeroChambre(rs.getString("num_chambre"));
                list.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Reservation findById(int id) {
        String sql = """
            SELECT r.*, c.nom || ' ' || c.prenom AS nom_client,
                   ch.numero AS num_chambre
            FROM reservations r
            JOIN clients c ON c.id = r.client_id
            JOIN chambres ch ON ch.id = r.chambre_id
            WHERE r.id = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setClientId(rs.getInt("client_id"));
                r.setChambreId(rs.getInt("chambre_id"));
                r.setDateArrivee(rs.getDate("date_arrivee").toLocalDate());
                r.setDateDepart(rs.getDate("date_depart").toLocalDate());
                r.setStatut(rs.getString("statut"));
                r.setMontantTotal(rs.getDouble("montant_total"));
                r.setNomClient(rs.getString("nom_client"));
                r.setNumeroChambre(rs.getString("num_chambre"));
                return r;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Reservation r) {
        String sql = """
            INSERT INTO reservations
                (client_id, chambre_id, date_arrivee, date_depart, statut, montant_total)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getClientId());
            ps.setInt(2, r.getChambreId());
            ps.setDate(3, Date.valueOf(r.getDateArrivee()));
            ps.setDate(4, Date.valueOf(r.getDateDepart()));
            ps.setString(5, r.getStatut());
            ps.setDouble(6, r.getMontantTotal());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateStatut(int id, String statut) {
        String sql = "UPDATE reservations SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM reservations WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean verifierDisponibilite(int chambreId,
                                         LocalDate arrivee, LocalDate depart) {
        String sql = """
            SELECT COUNT(*) FROM reservations
            WHERE chambre_id = ? AND statut != 'ANNULEE'
              AND date_arrivee < ? AND date_depart > ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chambreId);
            ps.setDate(2, Date.valueOf(depart));
            ps.setDate(3, Date.valueOf(arrivee));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}