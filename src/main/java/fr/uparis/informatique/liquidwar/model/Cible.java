package fr.uparis.informatique.liquidwar.model;

public class Cible {
    private volatile Position pos;//volatile pour que changement soit visible pour les threads

    public Cible(int x, int y) {
        this.pos=new Position(x,y);
    }

    public Position getPosition(){
        return pos;
    }

    public void setPosition(int x, int y) {
        this.pos=new Position(x,y);
    }

    public int getX() {
        return pos.x();
    }

    public int getY() {
        return pos.y();
    }

}
