package fr.uparis.informatique.liquidwar.controleur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.uparis.informatique.liquidwar.model.CarteJeu;
import fr.uparis.informatique.liquidwar.model.Equipe;
import fr.uparis.informatique.liquidwar.model.Particule;
import fr.uparis.informatique.liquidwar.model.Position;

public class MoteurJeu {
    private final CarteJeu carte;
    private final List<Equipe> equipes;
    private final GestionJeu gradient;
    private boolean enCours= true;
    private final ExecutorService executeur=Executors.newVirtualThreadPerTaskExecutor(); //executeur pour les threads virtuels

    public MoteurJeu(CarteJeu carte, List<Equipe> equipes){
        this.carte=carte;
        this.equipes=equipes;
        this.gradient=new GestionJeu(carte);
    }

    public void demarrer(){ //boucle principale du jeu qui est lancé sur un thread
        Thread.ofVirtual().start(() -> {
            while ((enCours)) {
                long debut=System.currentTimeMillis();
                update();

                long duree=System.currentTimeMillis()-debut;
                if(duree<16){
                    try{
                        Thread.sleep(16-duree);
                    } catch (InterruptedException e){
                        System.err.print(e.getMessage());
                    }
                }
            }
        });
    }

    private void update(){
        List<Callable<Void>> tachesCalcul=new ArrayList<>();
        for(Equipe equipe:equipes){
            tachesCalcul.add(()->{
                int [][] resultat=gradient.calculGradient(equipe);
                equipe.setGradient(resultat);
                return null;
            });
        }
        try {
            executeur.invokeAll(tachesCalcul); //attente du calcul de tous les gradient avt mouvement partivules
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        // puis appel methode pour deplacer les particules

    }

    private void deplacerParticules(){
        int largeur=carte.getLargeur();
        int hauteur=carte.getHauteur();

        for(Equipe equipe:equipes){
            int[][] gradient=equipe.getGradient();
            if(gradient==null) continue;

            for(Particule p:new ArrayList<>(equipe.getParticules())){
                Position curPos=p.getPosition();
                int x=curPos.x();
                int y=curPos.y();

                int bestX=x;
                int bestY=y;
                int bestDist=gradient[y][x];

                int[][] directions={{0,-1},{0,1},{-1,0},{1,0}};
                for(int[] dir:directions){
                    int nx=x+dir[0];
                    int ny=y+dir[1];

                    if(nx>=0&&nx<largeur&&ny>=0&&ny<hauteur){
                        int valVoisin=gradient[ny][nx];
                        if(valVoisin!=-1&&valVoisin<bestDist){
                            bestDist=valVoisin;
                            bestX=nx;
                            bestY=ny;
                        }
                    }
                }
                if(bestX!=x||bestY!=y){
                    //Methode pr faire le deplacement (todo)
                }
            }
        }
    }

    private void deplacement(){
        //todo gerer les mouvements sur la carte, les collisions 
    }
}
