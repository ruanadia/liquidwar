package controleur;

import model.CarteJeu;
import model.Case;
import model.Equipe;
import model.Cible;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GestionJeu {
    private final CarteJeu carte;
    private final Equipe[] equipes;
    private int[][] gradient; // tab de distances pour le gradient

    public GestionJeu(CarteJeu carte, Equipe[] equipes) {
        this.carte = carte;
        this.equipes = equipes;
        this.gradient = new int[carte.getHauteur()][carte.getLargeur()];
    }

    private void initialiserGradient() {
        for (int y = 0; y < carte.getHauteur(); y++) {
            for (int x = 0; x < carte.getLargeur(); x++) {
                gradient[y][x] = -1;
            }
        }
    }

    public void calculGradient(Equipe equipe) {
        initialiserGradient();

        Cible cible = equipe.getCible();
        int cibleX = cible.getX();
        int cibleY = cible.getY();

        gradient[cibleY][cibleX] = 0;

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { cibleX, cibleY });

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0];
            int y = pos[1];
            int dist = gradient[y][x];

            List<Case> voisins = carte.trouverVoisinsLibres(x, y);
            for (Case voisin : voisins) {
                int vx = -1, vy = -1;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < carte.getLargeur() && ny >= 0 && ny < carte.getHauteur()) {
                            if (carte.getCase(nx, ny) == voisin) {
                                vx = nx;
                                vy = ny;
                            }
                        }
                    }
                }
                if (gradient[vy][vx] == -1) {
                    gradient[vy][vx] = dist + 1;
                    queue.add(new int[] { vx, vy });
                }
            }
        }
    }

    public int[][] getGradient() {
        return gradient;
    }

    public void miseAJour() {
        for (Equipe e : equipes) {
            calculGradient(e);
        }
    }
}
