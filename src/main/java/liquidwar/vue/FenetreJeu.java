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
        SwingUtilities.invokeLater(() -> {
            CarteJeu carte = new CarteJeu(200, 150);

            Equipe equipe1 = new Equipe(1, "Rouge", 0xFF0000, new liquidwar.model.Cible(180, 140));

            for (int i = 0; i < 500; i++) {
                float x = (float) (Math.random() * carte.getLargeur());
                float y = (float) (Math.random() * carte.getHauteur());
                Particule p = new Particule(equipe1, x, y, 100, 0, 100);
                equipe1.ajouterParticule(p);
                carte.placerParticule(Math.round(x), Math.round(y), p);
            }

            List<Equipe> equipes = List.of(equipe1);

            // moteur et fenetre
            MoteurJeu moteur = new MoteurJeu(carte, equipes);
            FenetreJeu fenetre = new FenetreJeu(carte, equipes, moteur);
            fenetre.lancer();
        });
    }

}
