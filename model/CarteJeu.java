package model;

public class CarteJeu {
    private final int largeur;
    private final int hauteur;
    private final Case[][] cases;

    public enum TypeCase {
        VIDE,
        OBSTACLE,
        PARTICULE
    }

    public static class Case {
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
        }

        public Particule getParticule() {
            return particule;
        }

        public void setParticule(Particule p) {
            this.particule = p;
            this.type = (p == null) ? TypeCase.VIDE : TypeCase.PARTICULE;
        }
    }

    public CarteJeu(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.cases = new Case[hauteur][largeur];
        initialiserVide();
    }

    private void initialiserVide() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                cases[y][x] = new Case(TypeCase.VIDE);
            }
        }
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public Case getCase(int x, int y) {
        if (x < 0 || x >= largeur || y < 0 || y >= hauteur) {
            return null;
        }
        return cases[y][x];
    }
}
