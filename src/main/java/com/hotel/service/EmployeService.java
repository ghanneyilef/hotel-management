package com.hotel.service;

import com.hotel.dao.EmployeDAO;
import com.hotel.model.Employe;

import java.util.List;

public class EmployeService {

    private final EmployeDAO dao = new EmployeDAO();

    // ── Ajout ─────────────────────────────────────────────────────────────
    public boolean ajouterEmploye(Employe e) {
        if (e.getNom() == null || e.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (e.getRole() == null || e.getRole().isBlank())
            throw new IllegalArgumentException("Le rôle est obligatoire.");
        if (e.getStatut() == null || e.getStatut().isBlank())
            e.setStatut("ACTIF");
        return dao.insert(e);
    }

    // ── Lecture ───────────────────────────────────────────────────────────
    public List<Employe> tousLesEmployes() {
        return dao.findAll();
    }

    // CORRECTION : appelle dao.findByChambre(int) — nom exact du DAO
    public List<Employe> getEmployesDeChambre(int chambreId) {
        return dao.findByChambre(chambreId);
    }

    // CORRECTION : appelle dao.findDisponibles(String) — nom exact du DAO
    public List<Employe> getDisponibles(String role) {
        return dao.findDisponibles(role);
    }

    // ── Modification ─────────────────────────────────────────────────────
    public boolean modifier(Employe e) {
        return dao.update(e);
    }

    // ── Suppression ──────────────────────────────────────────────────────
    public boolean supprimer(int id) {
        return dao.delete(id);
    }

    // ── Assignation ──────────────────────────────────────────────────────
    // CORRECTION : appelle dao.assigner(int,int) — pas "assignerAChambre"
    public void assignerAChambre(int chambreId, int employeId) {
        try {
            dao.assigner(chambreId, employeId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean desassignerDeChambre(int chambreId, int employeId) {
        return dao.desassigner(chambreId, employeId);
    }

    // ── Alerte chambres non couvertes ─────────────────────────────────────
    // CORRECTION : méthode déléguée au DAO (à ajouter dans EmployeDAO)
    public long countChambresNonCouvertes() {
        return dao.countChambresNonCouvertes();
    }
}