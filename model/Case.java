package model;

public class Case {
    public enum TypeCase {
        VIDE,
        OBSTACLE,
        PARTICULE
    }

    private TypeCase type;
    private Particule particule;

    public Case(TypeCase type) {
        this.type = type;
        this.particule = null;
    }

    public TypeCase getType() {
        return type;
    }

    public void setType(TypeCase type) {
        this.type = type;
        if (type != TypeCase.PARTICULE) {
            this.particule = null;
        }
    }

    public Particule getParticule() {
        return particule;
    }

    public void setParticule(Particule p) {
        this.particule = p;
        this.type = (p == null) ? TypeCase.VIDE : TypeCase.PARTICULE;
    }
}
