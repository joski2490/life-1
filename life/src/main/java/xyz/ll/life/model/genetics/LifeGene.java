package xyz.ll.life.model.genetics;

/**
 * Created by lucas-cleto on 10/29/15.
 */
public class LifeGene implements Gene<LifeGene> {

    private static final double HEALTH_MAX = 1, HEALTH_MIN = 0, HEALTH_VARIANCE = 0.00005;

    private double health;

    private LifeGene(double health) {
        this.health = health;
    }

    public LifeGene() {
        this.health = 0.999;
    }

    public boolean disease() {
        return Math.random() > this.health;
    }

    @Override
    public void mutation() {
        this.health += Math.random() * LifeGene.HEALTH_VARIANCE * (Math.random() > .5 ? 1 : -1);
        if (this.health < LifeGene.HEALTH_MIN) {
            this.health = 2 * LifeGene.HEALTH_MIN - this.health;
        }
        if (this.health > LifeGene.HEALTH_MAX) {
            this.health = 2 * LifeGene.HEALTH_MAX - this.health;
        }
    }

    @Override
    public LifeGene meiosis(LifeGene gene) {
        double health = (this.health + gene.health) / 2;
        LifeGene childGene = new LifeGene(health);
        if (Math.random() < MUTATION_PROBABILITY) {
            childGene.mutation();
        }
        return childGene;
    }

    @Override
    public double distance(LifeGene gene) {
        double fh = LifeGene.HEALTH_MAX - LifeGene.HEALTH_MIN;
        return Math.abs(this.health - gene.health) / fh;
    }
}
