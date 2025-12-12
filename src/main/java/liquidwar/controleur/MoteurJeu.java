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
    private final long DUREE_PARTIE = 180_000;

    public MoteurJeu(CarteJeu carte, List<Equipe> equipes) {
        this.carte = carte;
        this.equipes = equipes;
        this.gradient = new GestionJeu(carte);
    }

    public void demarrer() {
        this.debutPartie = System.currentTimeMillis();
        Thread.ofVirtual().start(() -> {
            while (enCours) {

                if (getTempsRestant() <= 0) {
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

            int[][] grad = equipe.getGradient();
            if (grad == null) continue;

            Position next = choisirDirectionLW(p, grad);
            if (next == null) continue;

            Particule ennemi = null;
            Case caseNext = carte.getCase(next.x(), next.y());
            if (caseNext != null) {
                ennemi = caseNext.getParticule();
            }

            if (ennemi != null && ennemi.getEquipe() != p.getEquipe()) {

                boolean convertie = ennemi.subirAttaque(1, p.getEquipe());

                if (convertie) {
                    retirerParticuleDeSonEquipe(ennemi);
                    ajouterParticuleDansEquipe(ennemi, p.getEquipe());
                }

                continue;
            }

            if (carte.estLibre(next.x(), next.y())) {
                p.setPosition(next.x(), next.y());
                carte.mettreAJourParticule(p, oldX, oldY);
            }
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
        if (debutPartie == 0)
            return DUREE_PARTIE;
        long ecoule = System.currentTimeMillis() - debutPartie;
        return Math.max(0, DUREE_PARTIE - ecoule);
    }

  
private int[] choisirDirectionGradient(Particule p, int[][] grad) {
    int x = Math.round(p.getX());
    int y = Math.round(p.getY());

    int gActuel = grad[y][x];
    if (gActuel < 0) return new int[]{0,0};

    int[][] dirs = {
        {0,-1}, {0,1}, {-1,0}, {1,0},
        {-1,-1}, {-1,1}, {1,-1}, {1,1}
    };

    int[] dirPrincipale = null;
    int[] dirBonne = null;
    int[] dirAcceptable = null;

    for (int[] d : dirs) {
        int nx = x + d[0];
        int ny = y + d[1];

        if (nx < 0 || nx >= carte.getLargeur() || ny < 0 || ny >= carte.getHauteur())
            continue;

        if (carte.estObstacle(nx, ny))
            continue;

        int gVoisin = grad[ny][nx];
        if (gVoisin < 0) continue;

        if (gVoisin < gActuel) {
            // direction principale c le gradient minimal
            if (dirPrincipale == null || gVoisin < grad[y + dirPrincipale[1]][x + dirPrincipale[0]]) {
                dirBonne = dirPrincipale;
                dirPrincipale = d;
            }
        } 
        else if (gVoisin == gActuel) {
            dirAcceptable = d;
        }
        
    }

    
    if (dirPrincipale != null) return dirPrincipale;
    if (dirBonne != null) return dirBonne;
    if (dirAcceptable != null) return dirAcceptable;

    return new int[]{0,0}; 
}
public Position choisirDirectionLW(Particule p, int[][] grad) {

    int x = Math.round(p.getX());
    int y = Math.round(p.getY());
    int g0 = grad[y][x];

    Position[] voisins = {
        new Position(x+1, y),
        new Position(x-1, y),
        new Position(x, y+1),
        new Position(x, y-1)
    };

    Position principale = null;
    Position bonne = null;
    Position acceptable = null;

    int minG = Integer.MAX_VALUE;

    for (Position v : voisins) {
        int nx = v.x(), ny = v.y();

        if (!carte.estDansCarte(v)) continue;

        if (carte.estObstacle(nx, ny)) continue;

        int gv = grad[ny][nx];

        if (gv < minG) { minG = gv; principale = v; }
        if (gv < g0) bonne = v;
        else if (gv == g0) acceptable = v;
    }

    if (principale != null) {
        int nx = principale.x(), ny = principale.y();

        if (carte.estLibre(nx, ny)) return principale;

        if (estEnnemi(nx, ny, p)) {
            attaquer(p, carte.getCase(nx, ny).getParticule());
            return null;
        }
    }

    if (bonne != null) {
        int nx = bonne.x(), ny = bonne.y();

        if (carte.estLibre(nx, ny)) return bonne;

        if (estEnnemi(nx, ny, p)) {
            attaquer(p, carte.getCase(nx, ny).getParticule());
            return null;
        }
    }

    if (acceptable != null) {
        int nx = acceptable.x(), ny = acceptable.y();

        if (carte.estLibre(nx, ny)) return acceptable;
    }

    return null;
}


    private boolean estEnnemi(int x, int y, Particule p) {
        Particule autre = carte.getCase(x, y).getParticule();
        return autre != null && autre.getEquipe() != p.getEquipe();
    }

    private void attaquer(Particule attaquant, Particule cible) {
        boolean convertie = cible.subirAttaque(1, attaquant.getEquipe());

        if (convertie) {
            //System.out.println("une conversion a eu lieu !"); pour debug
        }
    }
    private void retirerParticuleDeSonEquipe(Particule p) {
        for (Equipe e : equipes) {
            e.getParticules().remove(p);
        }
    }
    
    private void ajouterParticuleDansEquipe(Particule p, Equipe nouvelleEquipe) {
        nouvelleEquipe.getParticules().add(p);
    }



}
