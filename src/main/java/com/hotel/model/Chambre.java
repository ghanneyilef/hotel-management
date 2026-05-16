package com.hotel.model;

public class Chambre {
    private int id;
    private String numero;
    private String type;
    private double prixNuit;
    private String statut;
    private String description;

    public Chambre() {}

    public Chambre(String numero, String type, double prixNuit) {
        this.numero = numero;
        this.type = type;
        this.prixNuit = prixNuit;
        this.statut = "LIBRE";
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrixNuit() { return prixNuit; }
    public void setPrixNuit(double p) { this.prixNuit = p; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    public boolean isLibre() { return "LIBRE".equals(statut); }
}