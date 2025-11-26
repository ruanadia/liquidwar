package liquidwar.vue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;

import liquidwar.controleur.MoteurJeu;
import liquidwar.model.CarteJeu;
import liquidwar.model.Particule;

public class CarteVue extends JPanel {
    private final CarteJeu carte;
    private final int tailleCase;
    private final MoteurJeu moteur;
    private final int idEquipe; // l'équipe qui suit la souris

    public CarteVue(CarteJeu carte, int tailleCase, MoteurJeu moteur, int idEquipe) {
        this.carte = carte;
        this.tailleCase = tailleCase;
        this.moteur = moteur;
        this.idEquipe = idEquipe;

        int largeurPx = carte.getLargeur() * tailleCase;
        int hauteurPx = carte.getHauteur() * tailleCase;
        setPreferredSize(new Dimension(largeurPx, hauteurPx));

        // suivi de la souris
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                float x = (float) e.getX() / tailleCase;
                float y = (float) e.getY() / tailleCase;
                moteur.setCibleEquipe(idEquipe, x, y);
            }
        });

        // clic de la souris pour définir la cible
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                float x = (float) e.getX() / tailleCase;
                float y = (float) e.getY() / tailleCase;
                moteur.setCibleEquipe(idEquipe, x, y);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // dessiner toutes les particules
        for (int y = 0; y < carte.getHauteur(); y++) {
            for (int x = 0; x < carte.getLargeur(); x++) {
                Particule p = carte.getCase(x, y).getParticule();
                if (p != null) {
                    g.setColor(new Color(p.getCouleur()));
                    int px = Math.round(p.getX() * tailleCase);
                    int py = Math.round(p.getY() * tailleCase);
                    g.fillOval(px, py, tailleCase, tailleCase);
                }
            }
        }
    }
}
