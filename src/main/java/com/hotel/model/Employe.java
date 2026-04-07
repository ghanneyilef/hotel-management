package com.hotel.model;

public class Employe {
    private int    id;
    private String nom, prenom, role, telephone, statut;
    private int    nbChambres; // champ calculé — combien de chambres assignées

    public Employe() {}

    public Employe(String nom, String prenom, String role, String telephone) {
        this.nom = nom; this.prenom = prenom;
        this.role = role; this.telephone = telephone;
        this.statut = "ACTIF";
    }

    public int    getId()               { return id; }
    public void   setId(int id)          { this.id = id; }
    public String getNom()              { return nom; }
    public void   setNom(String n)       { this.nom = n; }
    public String getPrenom()           { return prenom; }
    public void   setPrenom(String p)    { this.prenom = p; }
    public String getRole()             { return role; }
    public void   setRole(String r)      { this.role = r; }
    public String getTelephone()        { return telephone; }
    public void   setTelephone(String t) { this.telephone = t; }
    public String getStatut()           { return statut; }
    public void   setStatut(String s)    { this.statut = s; }
    public int    getNbChambres()        { return nbChambres; }
    public void   setNbChambres(int n)  { this.nbChambres = n; }

    public String getNomComplet() { return prenom + " " + nom; }

    @Override
    public String toString() { return getNomComplet() + " (" + role + ")"; }
}