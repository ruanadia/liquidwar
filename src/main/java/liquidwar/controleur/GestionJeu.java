package liquidwar.controleur;

import java.util.LinkedList;
import java.util.Queue;

import liquidwar.model.CarteJeu;
import liquidwar.model.Equipe;
import liquidwar.model.Position;

public class GestionJeu { // on peut peut être renommée cette classe en CalculateurGradient
    private final CarteJeu carte;



    public GestionJeu(CarteJeu carte) {
        this.carte = carte;
    }

   

    public int[][] calculGradient(Equipe equipe) {
        int largeur = carte.getLargeur();
        int hauteur = carte.getHauteur();
        int[][] dist = new int[hauteur][largeur];
    
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                dist[y][x] = -1; // non visite
            }
        }
    
        // commencer depuis la cible
        Position posCible = equipe.getCible().getPosition();
        int cx = posCible.x();
        int cy = posCible.y();
    
        if (cx < 0 || cx >= largeur || cy < 0 || cy >= hauteur) // hors carte
            return dist;
    
        dist[cy][cx] = 0;
    
        Queue<Position> file = new LinkedList<>();
        file.add(new Position(cx, cy));
    
        int[][] dirs = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0},   // orthogonales
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonales
        };
        
    
        while (!file.isEmpty()) {
            Position p = file.poll();
            int x = p.x();
            int y = p.y();
            int d = dist[y][x];
    
            for (int[] dir : dirs) {
                int nx = x + dir[0];
                int ny = y + dir[1];
    
                // hors carte
                if (nx < 0 || nx >= largeur || ny < 0 || ny >= hauteur)
                    continue;

                if (carte.estObstacle(nx, ny)) // obstacle alors traverse pas 
                    continue;
    
                if (dist[ny][nx] == -1) { // pas visite encore 
                    dist[ny][nx] = d + 1;
                    file.add(new Position(nx, ny));
                }
            }
        }
    
        return dist;
    }
    
    
}
