package com.hotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modèle d'une alerte/notification hôtelière.
 * Compatible JavaFX — peut être utilisé dans ObservableList.
 */
public class AlerteNotification {

    public enum Type {
        CHAMBRE_SANS_EMPLOYE,
        EMPLOYE_SURCHARGE,
        DEPART_IMMINENT,
        PAIEMENT_EN_ATTENTE
    }

    public enum Niveau {
        CRITIQUE,   // Rouge
        ATTENTION,  // Orange
        INFO        // Bleu
    }

    private Type          type;
    private Niveau        niveau;
    private String        message;
    private int           chambreId;
    private String        chambreNumero;
    private LocalDateTime dateCreation;

    public AlerteNotification(Type type, Niveau niveau, String message,
                              int chambreId, String chambreNumero) {
        this.type          = type;
        this.niveau        = niveau;
        this.message       = message;
        this.chambreId     = chambreId;
        this.chambreNumero = chambreNumero;
        this.dateCreation  = LocalDateTime.now();
    }

    // ── Helpers pour l'affichage ──────────────────────────────────────────

    public String getDateFormatee() {
        return dateCreation.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }

    /** CSS class name à appliquer sur la carte dans le FXML */
    public String getCssClass() {
        return switch (niveau) {
            case CRITIQUE  -> "alerte-critique";
            case ATTENTION -> "alerte-attention";
            case INFO      -> "alerte-info";
        };
    }

    public String getBadgeTexte() {
        return switch (niveau) {
            case CRITIQUE  -> "CRITIQUE";
            case ATTENTION -> "ATTENTION";
            case INFO      -> "INFO";
        };
    }

    public String getIcone() {
        return switch (type) {
            case CHAMBRE_SANS_EMPLOYE -> "🚨";
            case EMPLOYE_SURCHARGE    -> "⚠️";
            case DEPART_IMMINENT      -> "🛎";
            case PAIEMENT_EN_ATTENTE  -> "💳";
        };
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public Type          getType()                        { return type; }
    public void          setType(Type t)                  { this.type = t; }
    public Niveau        getNiveau()                      { return niveau; }
    public void          setNiveau(Niveau n)              { this.niveau = n; }
    public String        getMessage()                     { return message; }
    public void          setMessage(String m)             { this.message = m; }
    public int           getChambreId()                   { return chambreId; }
    public void          setChambreId(int id)             { this.chambreId = id; }
    public String        getChambreNumero()               { return chambreNumero; }
    public void          setChambreNumero(String n)       { this.chambreNumero = n; }
    public LocalDateTime getDateCreation()                { return dateCreation; }
    public void          setDateCreation(LocalDateTime d) { this.dateCreation = d; }
}