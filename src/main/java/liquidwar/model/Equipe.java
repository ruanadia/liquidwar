package liquidwar.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Equipe {
    private final int numero;
    private final String nom;
    private final int couleur;
    private Cible cible;
    private final List<Particule> particules;
    private volatile int[][] gradient;

    public Equipe(int numero, String nom, int couleur, Cible cible) {
        this.numero = numero;
        this.nom = nom;
        this.couleur = couleur;
        this.cible = cible;
        this.particules = new ArrayList<>();
    }

    public synchronized List<Particule> getParticules() {
        return Collections.unmodifiableList(new ArrayList<>(particules));
    }

    public synchronized void ajouterParticule(Particule p) {
        particules.add(p);
    }

    public synchronized void retirerParticule(Particule p) {
        particules.remove(p);
    }

    public Cible getCible() {
        return cible;
    }

    public void setCible(Cible cible) {
        this.cible = cible;
    }

    public int getCouleur() {
        return couleur;
    }

    public String getNom() {
        return nom;
    }

    public int getNumero() {
        return numero;
    }

    public int[][] getGradient() {
        return gradient;
    }

    public void setGradient(int[][] gradient) {
        this.gradient = gradient;
    }
}
