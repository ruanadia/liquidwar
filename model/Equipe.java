package model;

public class Equipe {
    private final int numero;
    private final String nom;
    private final int couleur;

    public Equipe(int numero, String nom, int couleur) {
        this.numero = numero;
        this.nom = nom;
        this.couleur = couleur;
    }

    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public int getCouleur() {
        return couleur;
    }
}
