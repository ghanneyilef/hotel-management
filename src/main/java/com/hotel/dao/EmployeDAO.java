package com.hotel.dao;

import com.hotel.model.Employe;
import java.sql.*;
import java.util.*;

public class EmployeDAO {


    public List<Employe> findAll() {
        List<Employe> list = new ArrayList<>();
        String sql = """
            SELECT e.*, COUNT(ce.chambre_id) AS nb_chambres
            FROM employes e
            LEFT JOIN chambre_employes ce ON ce.employe_id = e.id
            GROUP BY e.id ORDER BY e.nom
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Employe> findByChambre(int chambreId) {
        List<Employe> list = new ArrayList<>();
        String sql = """
            SELECT e.*, 0 AS nb_chambres FROM employes e
            JOIN chambre_employes ce ON ce.employe_id = e.id
            WHERE ce.chambre_id = ?
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, chambreId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    public List<Employe> findDisponibles(String role) {
        String sql = """
            SELECT e.*, COUNT(ce.chambre_id) AS nb_chambres
            FROM employes e
            LEFT JOIN chambre_employes ce ON ce.employe_id = e.id
            WHERE e.statut = 'ACTIF'
              AND (? = '' OR e.role = ?)
            GROUP BY e.id
            HAVING COUNT(ce.chambre_id) < 2
            ORDER BY e.nom
            """;
        List<Employe> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String r = (role == null || role.isBlank()) ? "" : role;
            ps.setString(1, r); ps.setString(2, r);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    public boolean assigner(int chambreId, int employeId) throws Exception {
        String check = "SELECT COUNT(*) FROM chambre_employes WHERE chambre_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(check)) {
            ps.setInt(1, chambreId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) >= 2)
                throw new IllegalStateException(
                        "Cette chambre a déjà 2 employés assignés");
        }
        String sql = "INSERT INTO chambre_employes(chambre_id,employe_id) VALUES(?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, chambreId); ps.setInt(2, employeId);
            return ps.executeUpdate() > 0;
        }
    }


    public boolean desassigner(int chambreId, int employeId) {
        String sql = "DELETE FROM chambre_employes WHERE chambre_id=? AND employe_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, chambreId); ps.setInt(2, employeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }


    public long countChambresNonCouvertes() {
        String sql = """
            SELECT COUNT(*) FROM chambres ch
            WHERE ch.statut = 'OCCUPEE'
              AND NOT EXISTS (
                  SELECT 1 FROM chambre_employes ce
                  WHERE ce.chambre_id = ch.id
              )
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean insert(Employe e) {
        String sql = "INSERT INTO employes(nom,prenom,role,telephone,statut) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getNom());    ps.setString(2, e.getPrenom());
            ps.setString(3, e.getRole());   ps.setString(4, e.getTelephone());
            ps.setString(5, e.getStatut());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean update(Employe e) {
        String sql = "UPDATE employes SET nom=?,prenom=?,role=?,telephone=?,statut=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getNom());    ps.setString(2, e.getPrenom());
            ps.setString(3, e.getRole());   ps.setString(4, e.getTelephone());
            ps.setString(5, e.getStatut()); ps.setInt(6, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM employes WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Employe mapRow(ResultSet rs) throws SQLException {
        Employe e = new Employe();
        e.setId(rs.getInt("id"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setRole(rs.getString("role"));
        e.setTelephone(rs.getString("telephone"));
        e.setStatut(rs.getString("statut"));
        try { e.setNbChambres(rs.getInt("nb_chambres")); }
        catch (SQLException ignored) {}
        return e;
    }
}