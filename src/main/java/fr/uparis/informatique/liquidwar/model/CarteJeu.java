package fr.uparis.informatique.liquidwar.model;

import java.util.ArrayList;
import java.util.List;

public class CarteJeu {
    private final int largeur;
    private final int hauteur;
    private final Case[][] cases;

    public CarteJeu(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.cases = new Case[hauteur][largeur];
        initialiserVide();
    }

    private void initialiserVide() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                cases[y][x] = new Case(Case.TypeCase.VIDE);
            }
        }
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public Case getCase(int x, int y) {
        if (x < 0 || x >= largeur || y < 0 || y >= hauteur) {
            return null;
        }
        return cases[y][x];
    }

    public boolean estLibre(int x, int y) {
        Case c = getCase(x, y);
        return c != null && c.getType() == Case.TypeCase.VIDE;
    }

    public List<Case> trouverVoisinsLibres(int x, int y) {
        List<Case> voisins = new ArrayList<>();
        int[] dx = { -1, 0, 1 };
        int[] dy = { -1, 0, 1 };

        for (int i : dx) {
            for (int j : dy) {
                if (i == 0 && j == 0)
                    continue; // ignore la case actuelle
                int nx = x + i;
                int ny = y + j;

                if (nx >= 0 && nx < largeur && ny >= 0 && ny < hauteur) {
                    if (estLibre(nx, ny)) {
                        voisins.add(getCase(nx, ny));
                    }
                }
            }
        }
        return voisins;
    }

    public boolean placerParticule(int x, int y, Particule p) {
        if (x < 0 || x >= largeur || y < 0 || y >= hauteur)
            return false; // hors limite
        Case c = getCase(x, y);
        if (c != null && c.getType() == Case.TypeCase.VIDE) {
            c.setParticule(p);
            return true;
        }
        return false; // pas placee
    }

    public void retirerParticule(int x, int y) {
        Case c = getCase(x, y);
        if (c != null && c.getType() == Case.TypeCase.PARTICULE) {
            c.setParticule(null);
        }
    }

    public boolean placerObstacle(int x, int y) {
        if (x < 0 || x >= largeur || y < 0 || y >= hauteur)
            return false;

        Case c = getCase(x, y);
        if (c != null && c.getType() != Case.TypeCase.PARTICULE) {
            c.setType(Case.TypeCase.OBSTACLE);
            return true;
        }
        return false; // obstacle non placee
    }
}
