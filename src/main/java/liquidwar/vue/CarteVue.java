package liquidwar.vue;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.ListResourceBundle;

import javax.swing.JPanel;

import liquidwar.controleur.MoteurJeu;
import liquidwar.model.CarteJeu;
import liquidwar.model.Cible;
import liquidwar.model.Equipe;
import liquidwar.model.Particule;

public class CarteVue extends JPanel {
    private final CarteJeu carte;
    private final int tailleCase;
    private final MoteurJeu moteur;
    private final int idEquipe;
    private boolean up = false, down = false, left = false, right = false;

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
        // controle pour l'equipe 2
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP -> up = true;
                    case java.awt.event.KeyEvent.VK_DOWN -> down = true;
                    case java.awt.event.KeyEvent.VK_LEFT -> left = true;
                    case java.awt.event.KeyEvent.VK_RIGHT -> right = true;
                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP -> up = false;
                    case java.awt.event.KeyEvent.VK_DOWN -> down = false;
                    case java.awt.event.KeyEvent.VK_LEFT -> left = false;
                    case java.awt.event.KeyEvent.VK_RIGHT -> right = false;
                }
            }
        });
        new javax.swing.Timer(1, e -> {
            mettreAJourCibleBleue(); // la cible est mise à jour à 60 FPS
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // mettreAJourCibleBleue();
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        int pixelSize = 2; // taille particule en pixels

        // on dessin les obstacles avant les particules pour qu'elles soient par dessus
        for (int y = 0; y < carte.getHauteur(); y++) {
            for (int x = 0; x < carte.getLargeur(); x++) {
                if (carte.estObstacle(x, y)) {
                    g.setColor(Color.GRAY);
                    g.fillRoundRect(x * tailleCase, y * tailleCase,
                            tailleCase, tailleCase,
                            tailleCase, tailleCase);
                }
            }
        }

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
        // DESSIN DES CIBLES ROUGE (id 1) ET BLEUE (id 2)
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // sécurité

        for (int equipeId = 1; equipeId <= 2; equipeId++) {

            Cible cible = moteur.getCibleEquipe(equipeId);
            if (cible == null)
                continue;

            int cx = Math.round(cible.getPosition().x() * tailleCase);
            int cy = Math.round(cible.getPosition().y() * tailleCase);

            int rayonExterieur = 6;
            int rayonInterieur = 3;

            // couleur suivant l'équipe
            Color couleur = (equipeId == 1) ? Color.PINK : Color.CYAN;

            for (int i = 3; i >= 1; i--) { // pr le halo
                float alpha = 0.1f * i;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                int r = rayonExterieur + i * 3;
                g2.setColor(couleur);
                g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            g2.setColor(couleur);
            g2.fillOval(cx - rayonExterieur, cy - rayonExterieur, 2 * rayonExterieur, 2 * rayonExterieur);

            g2.setColor(Color.BLACK);
            g2.fillOval(cx - rayonInterieur, cy - rayonInterieur, 2 * rayonInterieur, 2 * rayonInterieur);
        }

        dessinerTimer(g);
        dessinerBarreEnergie(g);
        if(moteur.estTermine()){
            finDePartie(g);
        }
    }

    private void dessinerTimer(Graphics g) {
        long tmpsRestant = moteur.getTempsRestant();
        long min = (tmpsRestant / 1000) / 60;
        long sec = (tmpsRestant / 1000) % 60;
        String txt = String.format("%02d:%02d", min, sec);

        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.setColor(Color.WHITE);

        if (tmpsRestant < 30000) { // si il reste moins de 30s
            g.setColor(Color.RED);
        }
        FontMetrics mertrics = g.getFontMetrics();
        int x = getWidth() - mertrics.stringWidth(txt) - 10;
        int y = mertrics.getHeight() + 5;

        g.drawString(txt, x, y);

    }

    private void mettreAJourCibleBleue() {
        Cible cible = moteur.getCibleEquipe(2);

        if (cible == null)
            return;

        float x = cible.getPosition().x();
        float y = cible.getPosition().y();

        float speed = 2.0f;

        if (up)
            y -= speed;
        if (down)
            y += speed;
        if (left)
            x -= speed;
        if (right)
            x += speed;

        float maxX = (getWidth() / tailleCase) - 1;
        float maxY = (getHeight() / tailleCase) - 1;

        x = Math.max(0f, Math.min(maxX, x));
        y = Math.max(0f, Math.min(maxY, y));

        moteur.setCibleEquipe(2, x, y);
    }

    private void dessinerBarreEnergie(Graphics g){
        List<Equipe> equipes=moteur.getEquipes();
        int totalParticules=0;
        for(Equipe e:equipes){
            totalParticules+=e.getNbParticules();
        }
        if (totalParticules==0)return;
        int hauteurBarre=10;
        int y=getHeight()-hauteurBarre;
        int x=0;
        int largeurTotale=getWidth();

        for(Equipe e:equipes){
            int nb=e.getParticules().size();
            int largeurEquipe=(int)((nb/(float)totalParticules)*largeurTotale);
            g.setColor(new Color(e.getCouleur()));
            g.fillRect(x, y, largeurEquipe, hauteurBarre);
            x+=largeurEquipe;
        }

        g.setColor(Color.WHITE);
        g.drawRect(0,y, getWidth()-1,hauteurBarre-1);
    }

    private void finDePartie(Graphics g){
        g.setColor(new Color(0,0,0,150));
        g.fillRect(0, 0, getWidth(), getHeight());

        String msg;
        Color couleurTxt;

        Equipe gagnant=moteur.getGagnant();
        if(gagnant!=null){
            msg="VICTOIRE : "+gagnant.getNom().toUpperCase()+" !";
            couleurTxt=new Color(gagnant.getCouleur());
        } else {
            msg="EGALITE !";
            couleurTxt=Color.WHITE;
        }
        g.setFont(new Font("Arial",Font.BOLD,40));
        FontMetrics metrics=g.getFontMetrics();
        int x=(getWidth()-metrics.stringWidth(msg))/2;
        int y=(getHeight()/2);

        g.setColor(Color.DARK_GRAY);
        g.drawString(msg, x+2, y+2);

        g.setColor(couleurTxt);
        g.drawString(msg,x,y);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String sousTitre = "Fermez la fenêtre pour quitter";
        int x2 = (getWidth() - g.getFontMetrics().stringWidth(sousTitre)) / 2;
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(sousTitre, x2, y + 40);
    }
}
