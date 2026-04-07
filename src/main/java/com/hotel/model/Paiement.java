package com.hotel.model;

import java.time.LocalDateTime;

public class Paiement {

    private int           id;
    private int           reservationId;
    private double        montant;
    private LocalDateTime datePaiement;
    private String        modePaiement; // CARTE, ESPECES, VIREMENT
    private String        statut;       // PAYE, EN_ATTENTE, REMBOURSE

    // Champs calculés (JOIN) pour l'affichage
    private String        nomClient;
    private String        numeroChambre;

    // ─── Constructeurs ───────────────────────────────────────────
    public Paiement() {}

    public Paiement(int reservationId, double montant,
                    String modePaiement, String statut) {
        this.reservationId = reservationId;
        this.montant       = montant;
        this.modePaiement  = modePaiement;
        this.statut        = statut;
        this.datePaiement  = LocalDateTime.now();
    }

    // ─── Getters & Setters ───────────────────────────────────────
    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public int    getReservationId()           { return reservationId; }
    public void   setReservationId(int rid)   { this.reservationId = rid; }

    public double getMontant()                { return montant; }
    public void   setMontant(double m)        { this.montant = m; }

    public LocalDateTime getDatePaiement()              { return datePaiement; }
    public void          setDatePaiement(LocalDateTime d) { this.datePaiement = d; }

    public String getModePaiement()             { return modePaiement; }
    public void   setModePaiement(String m)     { this.modePaiement = m; }

    public String getStatut()                   { return statut; }
    public void   setStatut(String s)            { this.statut = s; }

    public String getNomClient()                { return nomClient; }
    public void   setNomClient(String n)        { this.nomClient = n; }

    public String getNumeroChambre()            { return numeroChambre; }
    public void   setNumeroChambre(String n)    { this.numeroChambre = n; }

    @Override
    public String toString() {
        return "Paiement #" + id + " — " + montant + " DT (" + statut + ")";
    }
}