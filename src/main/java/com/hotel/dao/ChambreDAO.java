package com.hotel.dao;

import com.hotel.model.Chambre;
import java.sql.*;
import java.util.*;

public class ChambreDAO {

    public List<Chambre> findAll() {
        List<Chambre> list = new ArrayList<>();
        String sql = "SELECT * FROM chambres ORDER BY numero";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Chambre findById(int id) {
        String sql = "SELECT * FROM chambres WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Chambre c) {
        String sql = "INSERT INTO chambres(numero, type, prix_nuit, statut, description) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNumero());
            ps.setString(2, c.getType());
            ps.setDouble(3, c.getPrixNuit());
            ps.setString(4, c.getStatut() != null ? c.getStatut() : "LIBRE");
            ps.setString(5, c.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Chambre c) {
        String sql = "UPDATE chambres SET numero=?, type=?, prix_nuit=?, statut=?, description=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNumero());
            ps.setString(2, c.getType());
            ps.setDouble(3, c.getPrixNuit());
            ps.setString(4, c.getStatut());
            ps.setString(5, c.getDescription());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateStatut(int id, String statut) {
        String sql = "UPDATE chambres SET statut = ? WHERE id = ?";
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
                     "DELETE FROM chambres WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Chambre> toutesChambres() {
        return findAll();
    }

    private Chambre mapRow(ResultSet rs) throws SQLException {
        Chambre c = new Chambre();
        c.setId(rs.getInt("id"));
        c.setNumero(rs.getString("numero"));
        c.setType(rs.getString("type"));
        c.setPrixNuit(rs.getDouble("prix_nuit"));
        c.setStatut(rs.getString("statut"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}