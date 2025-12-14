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

        if (x < 5)
            return false; // ici j'empeche de mettre un obastacle dans les 5er colonnes

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
        placerZoneCirculaire(largeur / 2, hauteur / 2, 32);

        placerZoneCirculaire(largeur / 4, hauteur / 6, 18);
        placerZoneCirculaire(3 * largeur / 4, hauteur / 6, 18);

        placerZoneCirculaire(largeur / 2, 4 * hauteur / 5, 25);

        genererVagueVerticale(largeur / 3, 10, 0.12);
        genererVagueVerticale(2 * largeur / 3, 10, 0.12);

        genererVagueHorizontale(hauteur / 2, 12, 0.1);
    }

    private void placerZoneCirculaire(int cx, int cy, int r) {
        for (int y = cy - r; y <= cy + r; y++) {
            for (int x = cx - r; x <= cx + r; x++) {
                int dx = x - cx;
                int dy = y - cy;
                double d = Math.sqrt(dx * dx + dy * dy);

                // bord lissé
                if (d <= r + Math.sin(d * 0.3) * 2) {
                    placerObstacle(x, y);
                }
            }
        }
    }

    private void genererVagueVerticale(int col, int amplitude, double freq) {
        for (int y = 5; y < hauteur - 5; y++) {
            int offset = (int) (Math.sin(y * freq) * amplitude);
            placerObstacle(col + offset, y);

            // épaissir légèrement la vague
            placerObstacle(col + offset + 1, y);
            placerObstacle(col + offset - 1, y);
        }
    }

    private void genererVagueHorizontale(int row, int amplitude, double freq) {
        for (int x = 5; x < largeur - 5; x++) {
            int offset = (int) (Math.cos(x * freq) * amplitude);
            placerObstacle(x, row + offset);

            // léger épaississement
            placerObstacle(x, row + offset + 1);
            placerObstacle(x, row + offset - 1);
        }
    }

    public boolean estDansCarte(Position pos) {
        return pos.x() >= 0 && pos.x() < largeur &&
                pos.y() >= 0 && pos.y() < hauteur;
    }

    public void genererCarte() {
        initialiserVide();
        genererRonds();  
    }

    private void genererRonds(){
        int nombreIlots = 14; 
        int taillePinceauMin = 6;
        int taillePinceauMax = 12;
        int longueurTrace = 50;
        for (int i = 0; i < nombreIlots; i++) {
            double x = Math.random() * largeur;
            double y = Math.random() * hauteur;

            int rayon = (int) (taillePinceauMin + Math.random() * (taillePinceauMax - taillePinceauMin));

            for (int pas = 0; pas<longueurTrace; pas++) {
                peindreCercleObstacle((int) x, (int) y, rayon);
                x += (Math.random()*2-1)*1.5;
                y += (Math.random()*2-1)*1.5;
                if (x < 0 || x >= largeur || y < 0 || y >= hauteur)
                    break;
            }
        }
    }


    private void peindreCercleObstacle(int cx, int cy, int r) {
        for (int y = cy - r; y <= cy + r; y++) {
            for (int x = cx - r; x <= cx + r; x++) {
                if (x >= 0 && x < largeur && y >= 0 && y < hauteur) {
                    if ((x - cx) * (x - cx) + (y - cy) * (y - cy) <= r * r) {
                        placerObstacle(x, y);
                    }
                }
            }
        }
    }

}
