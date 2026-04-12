package com.hotel.service;

import com.hotel.dao.ClientDAO;
import com.hotel.model.Client;

import java.util.List;

public class ClientService {

    private final ClientDAO dao = new ClientDAO();

    // ── Ajout ─────────────────────────────────────────────────────────────
    public boolean ajouterClient(Client c) {
        if (c.getNom() == null || c.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (c.getEmail() == null || !c.getEmail().contains("@"))
            throw new IllegalArgumentException("Email invalide.");
        return dao.insert(c);
    }

    // ── Recherche / lecture ────────────────────────────────────────────────
    public List<Client> rechercherClients(String q) {
        if (q == null || q.isBlank()) return dao.findAll();
        return dao.search(q);
    }

    // ── Modification ──────────────────────────────────────────────────────
    // AJOUT : méthode appelée par ClientController ligne 143
    public boolean modifierClient(Client c) {
        if (c.getNom() == null || c.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (c.getEmail() == null || !c.getEmail().contains("@"))
            throw new IllegalArgumentException("Email invalide.");
        return dao.update(c);
    }

    // ── Suppression ───────────────────────────────────────────────────────
    // AJOUT : méthode appelée par ClientController ligne 158
    public boolean supprimerClient(int id) {
        return dao.delete(id);
    }
}