package liquidwar.model;

public class Particule {
    private Equipe equipe;
    private int energie;
    private final int energieMin;
    private final int energieMax;

    private float x, y; // position flottante
    private float vx, vy; // vitesse en pixels/frame ou unité arbitraire

    public Particule(Equipe equipe, float x, float y, int energieInitiale, int energieMin, int energieMax) {
        this.equipe = equipe;
        this.x = x;
        this.y = y;
        this.vx = 0f;
        this.vy = 0f;
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setVitesse(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void updatePosition() {
        this.x += vx;
        this.y += vy;
    }

    public Position getPosition() {
        return new Position((int) x, (int) y);
    }

    // on fait deux methodes pour set la position, une avec des float et une avec
    // Position
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(Position pos) {
        this.x = pos.x();
        this.y = pos.y();
    }

    public boolean subirAttaque(int degats, Equipe attaquant){
        this.energie-=degats;
        if (this.energie<=0){
            convertir(attaquant);
            return true;
        }
        return false;
    }

    public void recevoirSoin(int soin){
        this.energie+=soin;
        if(this.energie>energieMax){
            this.energie=energieMax;
        }
    }

    public void convertir(Equipe nvlleEquipe){
        this.equipe=nvlleEquipe;
        this.energie=(energieMax+energieMin)/2; //on remet l energie a la moitie 
    }  

}
