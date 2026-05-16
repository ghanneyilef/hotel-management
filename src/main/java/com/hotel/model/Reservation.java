package com.hotel.model;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private int clientId;
    private int chambreId;
    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private String statut;
    private double montantTotal;


    private String nomClient;
    private String numeroChambre;

    public Reservation() {}

    public Reservation(int clientId, int chambreId,
                       LocalDate dateArrivee, LocalDate dateDepart) {
        this.clientId = clientId;
        this.chambreId = chambreId;
        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
        this.statut = "CONFIRMEE";
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int c) { this.clientId = c; }

    public int getChambreId() { return chambreId; }
    public void setChambreId(int c) { this.chambreId = c; }

    public LocalDate getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(LocalDate d) { this.dateArrivee = d; }

    public LocalDate getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDate d) { this.dateDepart = d; }

    public String getStatut() { return statut; }
    public void setStatut(String s) { this.statut = s; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double m) { this.montantTotal = m; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String n) { this.nomClient = n; }

    public String getNumeroChambre() { return numeroChambre; }
    public void setNumeroChambre(String n) { this.numeroChambre = n; }
}