package liquidwar.controleur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import liquidwar.model.CarteJeu;
import liquidwar.model.Equipe;
import liquidwar.model.Particule;
import liquidwar.model.Position;

public class MoteurJeu {
    private final CarteJeu carte;
    private final List<Equipe> equipes;
    private final GestionJeu gradient;
    private volatile boolean enCours = true; // volatile pour visibilite entre threads
    private final ExecutorService executeur = Executors.newVirtualThreadPerTaskExecutor();

    public MoteurJeu(CarteJeu carte, List<Equipe> equipes) {
        this.carte = carte;
        this.equipes = equipes;
        this.gradient = new GestionJeu(carte);
    }

    public void demarrer() {
        Thread.ofVirtual().start(() -> {
            while (enCours) {
                long debut = System.currentTimeMillis();
                update();

                long duree = System.currentTimeMillis() - debut;
                if (duree < 16) { // pour ~60 FPS = 16ms par frame
                    try {
                        Thread.sleep(16 - duree);
                    } catch (InterruptedException e) {
                        System.err.print(e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }
            arreterExecuteur();
        });
    }

    // calcul des gradients + deplace lesparticules
    private void update() {
        List<Callable<Void>> tachesCalcul = new ArrayList<>();
        for (Equipe equipe : equipes) {
            tachesCalcul.add(() -> {
                int[][] resultat = gradient.calculGradient(equipe);
                equipe.setGradient(resultat);
                return null;
            });
        }
        try {
            executeur.invokeAll(tachesCalcul);
            deplacerParticules();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void deplacerParticules() {
        int largeur = carte.getLargeur();
        int hauteur = carte.getHauteur();

        for (Equipe equipe : equipes) {
            int[][] gradient = equipe.getGradient();
            if (gradient == null)
                continue;

            for (Particule p : new ArrayList<>(equipe.getParticules())) {
                Position curPos = p.getPosition();
                int x = curPos.x();
                int y = curPos.y();

                int bestX = x;
                int bestY = y;
                int bestDist = gradient[y][x];

                int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
                for (int[] dir : directions) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];

                    if (nx >= 0 && nx < largeur && ny >= 0 && ny < hauteur) {
                        int valVoisin = gradient[ny][nx];
                        if (valVoisin != -1 && valVoisin < bestDist) {
                            if (carte.estLibre(nx, ny)) {
                                bestDist = valVoisin;
                                bestX = nx;
                                bestY = ny;
                            }
                        }
                    }
                }
                if (bestX != x || bestY != y) {
                    carte.retirerParticule(x, y); // retirer à l'ancienne position
                    p.setPosition(bestX, bestY);
                    carte.placerParticule(bestX, bestY, p);
                }
            }
        }
    }

    private void arreterExecuteur() { // stop l executor et libere les ressources
        executeur.shutdown();
        try {
            if (!executeur.awaitTermination(5, TimeUnit.SECONDS)) {
                executeur.shutdownNow();
                if (!executeur.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("executeur n'a pas pu etre arrete");
                }
            }
        } catch (InterruptedException e) {
            executeur.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void arreter() {
        enCours = false;
    }
}
