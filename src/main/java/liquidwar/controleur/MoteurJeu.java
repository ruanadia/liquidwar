package liquidwar.controleur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import liquidwar.model.CarteJeu;
import liquidwar.model.Case;
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

    private volatile long debutPartie;
    private final long DUREE_PARTIE=180_000;

    public MoteurJeu(CarteJeu carte, List<Equipe> equipes) {
        this.carte = carte;
        this.equipes = equipes;
        this.gradient = new GestionJeu(carte);
    }

    public void demarrer() {
        this.debutPartie=System.currentTimeMillis();
        Thread.ofVirtual().start(() -> {
            while (enCours) {

                if(getTempsRestant()<=0){
                    arreter();
                    System.out.println("fin de la partie");
                    break;
                }

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

            float centreX = 0, centreY = 0;
            for (Particule p : equipe.getParticules()) {
                centreX += p.getX();
                centreY += p.getY();
            }
            int nbParticules = equipe.getParticules().size();
            if (nbParticules > 0) {
                centreX /= nbParticules;
                centreY /= nbParticules;
            }

            for (Particule p : new ArrayList<>(equipe.getParticules())) {
                int oldX = Math.round(p.getX());
                int oldY = Math.round(p.getY());

                // deplace vers la cible
                float dx = cibleX - p.getX();
                float dy = cibleY - p.getY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                float vx = 0, vy = 0;
                if (distance > 0) {
                    float vitesse = 0.25f; // vitesse de base un peu plus rapide
                    vx = vitesse * dx / distance;
                    vy = vitesse * dy / distance;
                }

                // repulsiondes particules proches
                float repelX = 0, repelY = 0;
                float distSeuilRepulsion = 0.45f; // plus petit = particules très serrées
                for (Particule autre : equipe.getParticules()) {
                    if (autre == p)
                        continue;
                    float diffX = p.getX() - autre.getX();
                    float diffY = p.getY() - autre.getY();
                    float distPart = (float) Math.sqrt(diffX * diffX + diffY * diffY);
                    if (distPart > 0 && distPart < distSeuilRepulsion) {
                        repelX += diffX / distPart / distPart * 0.5f; // repulsion plus faible
                        repelY += diffY / distPart / distPart * 0.5f;
                    }
                }

                double angle = 2 * Math.PI * Math.random();
                float randomX = 0.003f * (float) Math.cos(angle);
                float randomY = 0.003f * (float) Math.sin(angle);

                vx += repelX + randomX;
                vy += repelY + randomY;

                float vitesseMax = 0.35f;
                float vitesseActuelle = (float) Math.sqrt(vx * vx + vy * vy);
                if (vitesseActuelle > vitesseMax) {
                    vx = vx / vitesseActuelle * vitesseMax;
                    vy = vy / vitesseActuelle * vitesseMax;
                }

                p.setVitesse(vx, vy);
                p.updatePosition();

                // limites de la carte
                float nx = Math.max(0, Math.min(carte.getLargeur() - 1, p.getX()));
                float ny = Math.max(0, Math.min(carte.getHauteur() - 1, p.getY()));
                p.setPosition(nx, ny);

                carte.mettreAJourParticule(p, oldX, oldY);
            }
        }
    }

    public void setCibleEquipe(int idEquipe, float x, float y) {
        for (Equipe e : equipes) {
            if (e.getId() == idEquipe) {
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

    public Cible getCibleEquipe(int idEquipe) {
        for (Equipe e : equipes) {
            if (e.getId() == idEquipe) {
                return e.getCible();
            }
        }
        return null;
    }

    public long getTempsRestant() {
        if(debutPartie==0) return DUREE_PARTIE;
        long ecoule=System.currentTimeMillis()-debutPartie;
        return Math.max(0, DUREE_PARTIE-ecoule);
    }

    private void traiterDeplacement(Particule p, int oldX, int oldY, int newX,int newY){
        if(carte.estLibre(newX, newY)){
            carte.retirerParticule(oldX, oldY);
            carte.placerParticule(newX, newY, p);
            p.setPosition(new Position(newX,newY));
        } else {
            Case caseCible=carte.getCase(newX, newY);
            if(caseCible!=null&&caseCible.getType()==Case.TypeCase.PARTICULE){
                Particule autre=caseCible.getParticule();
                if(autre==null) return;

                if(autre.getEquipe()!=p.getEquipe()){
                    int degats=10;
                    Equipe oldEquipe=autre.getEquipe();
                    boolean converti=autre.subirAttaque(degats, p.getEquipe());
                    if(converti){
                        oldEquipe.retirerParticule(autre);
                        p.getEquipe().ajouterParticule(autre);

                    }
                } else {
                    int soin=1; //si c'est de la meme equipe, on soigne (transfert d'energie)
                    if(p.getEnergie()>20){
                        p.diminuerEnergie(soin);
                        autre.recevoirSoin(soin);
                    }
                }
            }
        }
    }
}
