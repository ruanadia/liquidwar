package fr.uparis.informatique.liquidwar.model;

public class Particule {
    private final Equipe equipe;
    private int energie;
    private final int energieMin; // energie minimum possible
    private final int energieMax;

    public Particule(Equipe equipe, int energieInitiale, int energieMin, int energieMax) {
        this.equipe = equipe;
        this.energie = energieInitiale;
        this.energieMin = energieMin;
        this.energieMax = energieMax;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public int getEnergie() {
        return energie;
    }

    public void setEnergie(int energie) {
        this.energie = Math.max(energieMin, Math.min(energieMax, energie));
    }

    public int getCouleur() {
        return equipe.getCouleur();
    }

    public boolean diminuerEnergie(int quantite) {
        setEnergie(energie - quantite);
        return energie <= energieMin;
    }

    public void augmenterEnergie(int quantite) {
        setEnergie(energie + quantite);
    }

}
