package com.hotel.service;

import com.hotel.dao.ChambreDAO;
import com.hotel.model.Chambre;

import java.util.List;

public class ChambreService {

    private final ChambreDAO dao = new ChambreDAO();


    public List<Chambre> toutesChambres() {
        return dao.findAll();
    }


    public List<Chambre> chambresDisponibles() {
        return dao.findAll().stream()
                .filter(ch -> "LIBRE".equals(ch.getStatut()))
                .toList();
    }


    public boolean ajouterChambre(Chambre c) {
        if (c.getNumero() == null || c.getNumero().isBlank())
            throw new IllegalArgumentException("Le numéro de chambre est obligatoire.");
        if (c.getType() == null || c.getType().isBlank())
            throw new IllegalArgumentException("Le type de chambre est obligatoire.");
        return dao.insert(c);
    }


    public boolean modifierChambre(Chambre c) {
        return dao.update(c);
    }


    public boolean supprimerChambre(int id) {
        return dao.delete(id);
    }
}