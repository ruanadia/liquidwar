package liquidwar.controleur;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import liquidwar.model.*;


public class GestionJeu {   //on peut peut être renommée cette classe en CalculateurGradient
    private final CarteJeu carte;

    //c'est mieux de separer les equipes du calcul de gradient, pour gerer les calculs du gradient des deux equipes simultanés (multithreading)

    //private final Equipe[] equipes;
    //private int[][] gradient; // tab de distances pour le gradient

    public GestionJeu(CarteJeu carte) {
        this.carte = carte;

        //this.equipes = equipes;
        //this.gradient = new int[carte.getHauteur()][carte.getLargeur()];
    }

    private void initialiserGradient() {
        // déplacer ce qui avait ici dans calcul gradient pour avoir la variable local de distances
    }

    public int[][] calculGradient(Equipe equipe) {
        int largeur=carte.getLargeur();
        int hauteur=carte.getHauteur();
        int [][] distances=new int[hauteur][largeur]; //variable local pour securite thread

        for (int y = 0; y < carte.getHauteur(); y++) {
            for (int x = 0; x < carte.getLargeur(); x++) {
                distances[y][x] = -1;
            }
        }

        Cible cible = equipe.getCible();
        Position depart=cible.getPosition();
        if (depart.x() < 0 || depart.x() >= largeur || depart.y() < 0 || depart.y() >= hauteur) {
            return distances; 
        }
        distances[depart.y()][depart.x()] = 0;

        Queue<Position> queue = new LinkedList<>();
        queue.add(depart);

        while (!queue.isEmpty()) {
            Position pos = queue.poll();
            int x=pos.x();
            int y=pos.y();
            int dist = distances[pos.y()][pos.x()];

            
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if(dx==0&&dy==0) continue;

                    //coordonnées voisins
                    int nx=x+dx;
                    int ny=y+dy;
                    if(nx>=0&&nx<largeur&&ny>=0&&ny<hauteur){
                        if(carte.estLibre(nx, ny)&&distances[ny][nx]==-1){
                            distances[ny][nx]=dist+1;
                            queue.add(new Position(nx,ny));
                        }
                    }
                }
            }       
        }
        return distances;
    }
    
/* 
    public int[][] getGradient() {
        return gradient;
    }

    public void miseAJour() {
        for (Equipe e : equipes) {
            calculGradient(e);
        }
    }
*/

}
