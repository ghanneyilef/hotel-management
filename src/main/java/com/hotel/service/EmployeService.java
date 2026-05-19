package com.hotel.service;

import com.hotel.dao.EmployeDAO;
import com.hotel.model.Employe;

import java.util.List;

public class EmployeService {

    private final EmployeDAO dao = new EmployeDAO();


    public boolean ajouterEmploye(Employe e) {
        if (e.getNom() == null || e.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (e.getRole() == null || e.getRole().isBlank())
            throw new IllegalArgumentException("Le rôle est obligatoire.");
        if (e.getStatut() == null || e.getStatut().isBlank())
            e.setStatut("ACTIF");
        return dao.insert(e);
    }


    public List<Employe> tousLesEmployes() {
        return dao.findAll();
    }


    public List<Employe> getEmployesDeChambre(int chambreId) {
        return dao.findByChambre(chambreId);
    }


    public List<Employe> getDisponibles(String role) {
        return dao.findDisponibles(role);
    }


    public boolean modifier(Employe e) {
        return dao.update(e);
    }


    public boolean supprimer(int id) {
        return dao.delete(id);
    }


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


    public long countChambresNonCouvertes() {
        return dao.countChambresNonCouvertes();
    }
}