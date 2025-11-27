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
import liquidwar.model.Cible;
import liquidwar.model.Particule;

// pr la cible 
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Color;

public class CarteVue extends JPanel {
    private final CarteJeu carte;
    private final int tailleCase;
    private final MoteurJeu moteur;
    private final int idEquipe;

    public CarteVue(CarteJeu carte, int tailleCase, MoteurJeu moteur, int idEquipe) {
        this.carte = carte;
        this.tailleCase = tailleCase;
        this.moteur = moteur;
        this.idEquipe = idEquipe;

        int largeurPx = carte.getLargeur() * tailleCase;
        int hauteurPx = carte.getHauteur() * tailleCase;
        setPreferredSize(new Dimension(largeurPx, hauteurPx));

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                float x = (float) e.getX() / tailleCase;
                float y = (float) e.getY() / tailleCase;
                moteur.setCibleEquipe(idEquipe, x, y);
            }
        });

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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        int pixelSize = 2; // taille particule en pixels

        // dessiner les particules
        for (int y = 0; y < carte.getHauteur(); y++) {
            for (int x = 0; x < carte.getLargeur(); x++) {
                Particule p = carte.getCase(x, y).getParticule();
                if (p != null) {
                    g.setColor(new Color(p.getCouleur()));
                    int px = Math.round(p.getX() * tailleCase);
                    int py = Math.round(p.getY() * tailleCase);
                    g.fillRect(px, py, pixelSize, pixelSize);
                }
            }
        }

        // dessiner la cible comme un petit cercle rouge
        Cible cible = moteur.getCibleEquipe(idEquipe);
        if (cible != null) {
            int cx = Math.round(cible.getPosition().x() * tailleCase);
            int cy = Math.round(cible.getPosition().y() * tailleCase);

            int rayonExterieur = 6;
            int rayonInterieur = 3;

            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(Color.PINK);
            for (int i = 3; i >= 1; i--) {
                float alpha = 0.1f * i; // les bord un peu transparents
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                int r = rayonExterieur + i * 3;
                g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.setColor(Color.PINK);
            g2.fillOval(cx - rayonExterieur, cy - rayonExterieur, 2 * rayonExterieur, 2 * rayonExterieur);

            // trou au centre
            g2.setColor(Color.BLACK);
            g2.fillOval(cx - rayonInterieur, cy - rayonInterieur, 2 * rayonInterieur, 2 * rayonInterieur);
        }
    }
}
