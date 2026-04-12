package com.hotel.service;

import com.hotel.dao.ChambreDAO;
import com.hotel.model.Chambre;

import java.util.List;

public class ChambreService {

    private final ChambreDAO dao = new ChambreDAO();

    // ── Lecture ────────────────────────────────────────────────────────────
    public List<Chambre> toutesChambres() {
        return dao.findAll();
    }

    // CORRECTION : ChambreDAO n'a pas findDisponibles()
    // → on filtre côté Java sur le statut LIBRE
    public List<Chambre> chambresDisponibles() {
        return dao.findAll().stream()
                .filter(ch -> "LIBRE".equals(ch.getStatut()))
                .toList();
    }

    // ── Ajout ─────────────────────────────────────────────────────────────
    public boolean ajouterChambre(Chambre c) {
        if (c.getNumero() == null || c.getNumero().isBlank())
            throw new IllegalArgumentException("Le numéro de chambre est obligatoire.");
        if (c.getType() == null || c.getType().isBlank())
            throw new IllegalArgumentException("Le type de chambre est obligatoire.");
        return dao.insert(c);
    }

    // ── Modification ──────────────────────────────────────────────────────
    public boolean modifierChambre(Chambre c) {
        return dao.update(c);
    }

    // ── Suppression ───────────────────────────────────────────────────────
    public boolean supprimerChambre(int id) {
        return dao.delete(id);
    }
}