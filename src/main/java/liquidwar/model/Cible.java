package liquidwar.model;

public class Cible {
    private volatile float x;
    private volatile float y;

    public Cible(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized Position getPosition() {
        return new Position(Math.round(x), Math.round(y));
    }

    public synchronized void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public synchronized void setPosition(Position pos) {
        this.x = pos.x();
        this.y = pos.y();
    }

    public synchronized float getX() {
        return x;
    }

    public synchronized float getY() {
        return y;
    }
}
