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

    public FenetreJeu(CarteJeu carte, List<Equipe> equipes, MoteurJeu moteur) {
        super("Liquid War");

        this.moteur = moteur;

        this.carteVue = new CarteVue(carte, 10, moteur, 1);

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

            Cible cibleRouge = new Cible(50, 50);
            Cible cibleBleu  = new Cible(100, 30); 

            Equipe rouge = new Equipe(1, "rouge", 0xFF0000, cibleRouge);
            Equipe bleu  = new Equipe(2, "bleu", 0x0000FF, cibleBleu);

            genererNuage(carte, rouge, 30, 120, 20, 20, 500);   
            genererNuage(carte, bleu, 170, 30, 20, 20, 500);    

            

            List<Equipe> equipes = List.of(rouge, bleu);

            MoteurJeu moteur = new MoteurJeu(carte, equipes);

            FenetreJeu fenetre = new FenetreJeu(carte, equipes, moteur);
            fenetre.lancer();
        });
    }


    private static void genererNuage(CarteJeu carte, Equipe equipe, float centreX, float centreY,float rayonX, float rayonY,int nbParticules) {

    for (int i = 0; i < nbParticules; i++) {
        double angle = 2 * Math.PI * Math.random();
        float rX = (float)(rayonX * Math.random());
        float rY = (float)(rayonY * Math.random());

        float x = centreX + rX * (float)Math.cos(angle);
        float y = centreY + rY * (float)Math.sin(angle);

        Particule p = new Particule(equipe, x, y, 100, 0, 100);

        equipe.ajouterParticule(p);
        carte.placerParticule(Math.round(x), Math.round(y), p);
    }
}

}


