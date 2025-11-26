package liquidwar.vue;

import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import liquidwar.controleur.MoteurJeu;
import liquidwar.model.CarteJeu;
import liquidwar.model.Equipe;
import liquidwar.model.Particule;
import liquidwar.model.Position;

public class FenetreJeu extends JFrame {
    private final CarteVue carteVue;
    private final MoteurJeu moteur;

    public FenetreJeu(CarteJeu carte, List<Equipe> equipes, MoteurJeu moteur) {
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CarteJeu carte = new CarteJeu(80, 60);

                Equipe equipe1 = new Equipe(1, "Rouge", 0xFF0000, new liquidwar.model.Cible(70, 50));

                Particule p1 = new Particule(equipe1, 10.0f, 10.0f, 100, 0, 100);
                Particule p2 = new Particule(equipe1, 11.0f, 10.5f, 100, 0, 100);
                Particule p3 = new Particule(equipe1, 12.3f, 10.8f, 100, 0, 100);

                equipe1.ajouterParticule(p1);
                equipe1.ajouterParticule(p2);
                equipe1.ajouterParticule(p3);

                carte.placerParticule(10, 10, p1);
                carte.placerParticule(11, 10, p2);
                carte.placerParticule(12, 10, p3);

                List<Equipe> equipes = List.of(equipe1);

                MoteurJeu moteur = new MoteurJeu(carte, equipes);
                FenetreJeu fenetre = new FenetreJeu(carte, equipes, moteur);
                fenetre.lancer();
            }
        });
    }

}
