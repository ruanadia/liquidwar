package liquidwar.controleur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import liquidwar.model.CarteJeu;
import liquidwar.model.Cible;
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
        for (Equipe equipe : equipes) {
            Cible cible = equipe.getCible();
            float cibleX = (float) cible.getPosition().x();
            float cibleY = (float) cible.getPosition().y();

            for (Particule p : new ArrayList<>(equipe.getParticules())) {

                // on sauve l'ancienne position
                int oldX = Math.round(p.getX());
                int oldY = Math.round(p.getY());

                float dx = cibleX - p.getX();
                float dy = cibleY - p.getY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance > 0) {
                    float vitesse = 0.1f;

                    float vx = vitesse * dx / distance;
                    float vy = vitesse * dy / distance;

                    // pousser les particules voisines
                    float repelX = 0, repelY = 0;
                    for (Particule autre : equipe.getParticules()) {
                        if (autre == p)
                            continue;
                        float diffX = p.getX() - autre.getX();
                        float diffY = p.getY() - autre.getY();
                        float distPart = (float) Math.sqrt(diffX * diffX + diffY * diffY);
                        if (distPart > 0 && distPart < 1.0f) {
                            repelX += diffX / distPart / distPart;
                            repelY += diffY / distPart / distPart;
                        }
                    }

                    // hasard pour eviter que les particules restent collees
                    double angle = 2 * Math.PI * Math.random();
                    float randomX = 0.01f * (float) Math.cos(angle);
                    float randomY = 0.01f * (float) Math.sin(angle);

                    vx += repelX + randomX;
                    vy += repelY + randomY;

                    // vitesse max on borne
                    float vitesseMax = 0.2f;
                    float vitesseActuelle = (float) Math.sqrt(vx * vx + vy * vy);
                    if (vitesseActuelle > vitesseMax) {
                        vx = vx / vitesseActuelle * vitesseMax;
                        vy = vy / vitesseActuelle * vitesseMax;
                    }

                    p.setVitesse(vx, vy); // maj de la vitesse
                    p.updatePosition();

                    float nx = Math.max(0, Math.min(carte.getLargeur() - 1, p.getX()));// respect des limites de la
                                                                                       // carte
                    float ny = Math.max(0, Math.min(carte.getHauteur() - 1, p.getY()));
                    p.setPosition(nx, ny);

                    carte.mettreAJourParticule(p, oldX, oldY); // maj de la carte

                }
            }
        }
    }

    public void setCibleEquipe(int idEquipe, float x, float y) {
        for (Equipe e : equipes) {
            if (e.getId() == idEquipe) { // <-- selon ton modèle Equipe
                e.getCible().setPosition(x, y);
                return;
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
