package liquidwar.vue;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import liquidwar.controleur.MoteurJeu;
import liquidwar.model.CarteJeu;
import liquidwar.model.Cible;
import liquidwar.model.Equipe;
import liquidwar.model.Particule;

public class FenetreJeu extends JFrame {
    private final CarteVue carteVue;
    private final MoteurJeu moteur;

    public FenetreJeu(CarteJeu carte, List<Equipe> equipes, MoteurJeu moteur, Cible cible) {
        super("Liquid War");
        this.moteur = moteur;
        this.carteVue = new CarteVue(carte, 10, moteur, 1); // équipe 1 suit la souris

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(carteVue);
        pack();
        setLocationRelativeTo(null);

        Timer timer = new Timer(16, e -> carteVue.repaint());
        timer.start();
    }

    public void lancer() {
        moteur.demarrer();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarteJeu carte = new CarteJeu(200, 150);
            carte.stylecarte();
            // creer cible
            Cible cible = new Cible(50, 50);

            Equipe equipe1 = new Equipe(1, "Rouge", 0xFF0000, cible);

            // nuage initial de particules
            float centreX = 50;
            float centreY = 50;
            float rayonX = 20;
            float rayonY = 20;
            int nbParticules = 1500;

            for (int i = 0; i < nbParticules; i++) {
                double angle = 2 * Math.PI * Math.random();
                float rX = (float) (rayonX * Math.random());
                float rY = (float) (rayonY * Math.random());
                float x = centreX + rX * (float) Math.cos(angle);
                float y = centreY + rY * (float) Math.sin(angle);

                Particule p = new Particule(equipe1, x, y, 100, 0, 100);
                equipe1.ajouterParticule(p);
                carte.placerParticule(Math.round(x), Math.round(y), p);
            }

            List<Equipe> equipes = List.of(equipe1);

            MoteurJeu moteur = new MoteurJeu(carte, equipes);
            FenetreJeu fenetre = new FenetreJeu(carte, equipes, moteur, cible);
            fenetre.lancer();
        });
    }
}
