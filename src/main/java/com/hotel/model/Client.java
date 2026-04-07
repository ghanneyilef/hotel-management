package com.hotel.model;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String cin;

    public Client() {}

    public Client(String nom, String prenom, String email,
                  String telephone, String cin) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.cin = cin;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String t) { this.telephone = t; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + email + ")";
    }
}