package liquidwar.model;

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
            return false;
        Case c = getCase(x, y);
        if (c != null && c.getType() == Case.TypeCase.VIDE) {
            c.setParticule(p);
            c.setType(Case.TypeCase.PARTICULE);
            return true;
        }
        return false;
    }

    public void retirerParticule(int x, int y) {
        Case c = getCase(x, y);
        if (c != null && c.getType() == Case.TypeCase.PARTICULE) {
            c.setParticule(null);
            c.setType(Case.TypeCase.VIDE);
        }
    }

    public boolean placerObstacle(int x, int y) {
        if (x < 0 || x >= largeur || y < 0 || y >= hauteur)
            return false;
    
        if (x < 5) return false; // ici j'empeche de mettre un obastacle dans les 5er colonnes
    
        Case c = getCase(x, y);
        if (c != null && c.getType() != Case.TypeCase.PARTICULE) {
            c.setType(Case.TypeCase.OBSTACLE);
            return true;
        }
        return false;
    }
    

    public void mettreAJourParticule(Particule p, int oldX, int oldY) {

        if (oldX >= 0 && oldX < largeur && oldY >= 0 && oldY < hauteur) {
            Case ancienne = getCase(oldX, oldY);
            if (ancienne != null && ancienne.getParticule() == p) {
                ancienne.setParticule(null);
            }
        }

        int nx = Math.round(p.getX());
        int ny = Math.round(p.getY());

        if (nx >= 0 && nx < largeur && ny >= 0 && ny < hauteur) {
            Case nouvelle = getCase(nx, ny);
            if (nouvelle != null && nouvelle.getType() == Case.TypeCase.VIDE) {
                nouvelle.setParticule(p);
            }
        }
    }

    public void genererObstacles(int nombre) {
        int count = 0;
        while (count < nombre) {
            int x = (int) (Math.random() * largeur);
            int y = (int) (Math.random() * hauteur);
            if (placerObstacle(x, y)) {
                count++;
            }
        }
    }
    
    public boolean estObstacle(int x, int y) {
        Case c = getCase(x, y);
        return c != null && c.getType() == Case.TypeCase.OBSTACLE;
    }
    public void stylecarte() {
        int cx = largeur / 2;
        int cy = hauteur / 2;
        int r = 20;
    
        // Rond central
        for (int y = cy - r; y <= cy + r; y++) {
            for (int x = cx - r; x <= cx + r; x++) {
                int dx = x - cx;
                int dy = y - cy;
                if (dx*dx + dy*dy <= r*r) {
                    placerObstacle(x, y);
                }
            }
        }
    
        placerRond(20, 20, 18);    
        placerRond(largeur / 2, hauteur - 20, 22); 
    
        genererVagueVerticale(largeur / 4, 8);
       // genererVagueVerticale(3 * largeur / 4, 8);
    }
    
    private void placerRond(int cx, int cy, int r) {
        for (int y = cy - r; y <= cy + r; y++) {
            for (int x = cx - r; x <= cx + r; x++) {
                int dx = x - cx;
                int dy = y - cy;
                if (dx*dx + dy*dy <= r*r) {
                    placerObstacle(x, y);
                }
            }
        }
    }
    
    private void genererVagueVerticale(int col, int amplitude) {
        for (int y = 10; y < hauteur - 10; y++) {
            int offset = (int)(Math.sin(y * 0.1) * amplitude);
            placerObstacle(col + offset, y);
        }
    }

    public boolean estDansCarte(Position pos) {
        return pos.x() >= 0 && pos.x() < largeur &&
               pos.y() >= 0 && pos.y() < hauteur;
    }
    
    

}
