public class Particule {
    private final int equipe; // num de l'equipe
    private int energie;
    private final int energieMin; // minimum d'energie possible
    private final int energieMax;

    private final int couleur; // couleur de l'equipe (en rgb surement )

    public Particule(int equipe, int energieInitiale, int energieMin, int energieMax, int couleur) {
        this.equipe = equipe;
        this.energie = energieInitiale;
        this.energieMin = energieMin;
        this.energieMax = energieMax;
        this.couleur = couleur;
    }

    public int getEquipe() {
        return equipe;
    }

    public int getEnergie() {
        return energie;
    }

    public void setEnergie(int energie) {
        this.energie = Math.max(energieMin, Math.min(energieMax, energie));
    }

    public int getCouleur() {
        return couleur;
    }

    public boolean diminuerEnergie(int quantite) {
        setEnergie(energie - quantite);
        return energie <= energieMin; // retourne vrai si energie est en dessous du minimum
    }

    public void augmenterEnergie(int quantite) {
        setEnergie(energie + quantite);
    }
}
