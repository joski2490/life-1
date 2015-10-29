package xyz.luan.life.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import xyz.luan.life.EntityManager;
import xyz.luan.life.model.genetics.Genome;

public class Individual extends Entity {

    private Genome genome;
    private int tickAge = 0;
    private long timeAge = System.currentTimeMillis();
    private int generation = 0;

    private static class LazyIntersection {

        private Entity e1, e2;
        private Shape shape;

        public LazyIntersection(Entity e1, Entity e2) {
            this.e1 = e1;
            this.e2 = e2;
        }

        public Shape getShape() {
            if (shape == null) {
                shape = e1.intersects(e2);
            }
            return shape;
        }
    }

    private static EntityShape generateBody(Point2D position, Genome genome, int precision) {
        EntityShape body = new EntityShape(position);
        genome.getTranslation().initialSpeed(body);
        genome.getColor().dye(body);
        genome.getMorfological().generateShape(body);
        return body;
    }

    public static Individual abiogenesis(Dimension2D dimension) {
        Random r = new Random();
        return new Individual(new Point2D(r.nextInt((int) dimension.getWidth()), r.nextInt((int) dimension.getHeight())), 50000, new Genome());
    }

    private Individual(Point2D position, double energy, Genome genome) {
        super(Individual.generateBody(position, genome, 100), energy);

        this.body.toFront();
        this.genome = genome;
    }

    public Genome getGenome() {
        return genome;
    }

    private Individual reproduce(Individual pair, Shape intersection) {
        Random random = new Random();
        Genome genome = new Genome();
        for (Gene2 gene : this.getGenome().getGenes().keySet()) {
            double a = this.getGenome().get(gene);
            double b = pair.getGenome().get(gene);
            double diff = Math.abs(a - b);
            double mix = Math.min(a, b) + diff * random.nextDouble();
            if (random.nextInt(Util.RARITY_OF_IMMUTABILITY) == 0) {
                mix = mix + random.nextDouble() * Math.pow(-1, random.nextInt(1));
            }
            genome.getGenes().put(gene, Math.abs(mix));
        }

        double initialEnergy = this.divide() + pair.divide();
        Bounds bounds = intersection.getBoundsInParent();
        Point2D center = new Point2D((bounds.getMaxX() + bounds.getMinX()) / 2, (bounds.getMaxY() + bounds.getMinY()) / 2);
        Individual child = new Individual(center, initialEnergy, genome);
        child.generation = Math.max(this.generation, pair.generation) + 1;
        return child;
    }

    private Food onDeath() {
        System.out.println("death { tick: " + tickAge + " time: " + (System.currentTimeMillis() - timeAge) + " generation: " + generation + " }");
        return new Food(this);
    }

    public double divide() {
        double amount = genome.getReproduction().careCost(body);
        this.loseEnergy(genome.getReproduction().reproductionCost(body));
        return amount;
    }

    public boolean isAvailableToReproduce() {
        return genome.getReproduction().isAvailableToReproduce(body, energy);
    }

    public void tryToReproduce(Entity entity, EntityManager em, LazyIntersection intersection) {
        if (this.isAvailableToReproduce() && entity instanceof Individual) {
            if (((Individual) entity).isAvailableToReproduce()
                    && genome.geneticDistance(((Individual) entity).genome) < Util.ACCEPTABLE_GENETIC_DISTANCE_TO_REPRODUCE) {
                if (intersection.getShape() != null && intersection.getShape().getLayoutBounds().getHeight() > 0
                        && intersection.getShape().getLayoutBounds().getWidth() > 0) {
                    Individual child = reproduce((Individual) entity, intersection.getShape());
                    em.add(child);
                }
            }
        }
    }

    public void tryToEat(Entity entity, EntityManager em, LazyIntersection intersection) {
        if (Util.ACCEPTABLE_AREA_PROPORTION_TO_EAT < this.getArea() / entity.getArea()) {
            double cost = Util.BASE_METABOLIZATION_ENERGY_COST * entity.getArea();
            if (this.getTotalEnergy() >= cost) {
                if (intersection.getShape() != null && intersection.getShape().getLayoutBounds().getHeight() > 0
                        && intersection.getShape().getLayoutBounds().getWidth() > 0) {
                    this.loseEnergy(cost);
                    this.gainEnergy(entity.getTotalEnergy());
                    em.remove(entity);
                }
            }
        }
    }

    @Override
    public void onCollide(Entity entity, EntityManager em) {
        LazyIntersection intersection = new LazyIntersection(this, entity);
        tryToReproduce(entity, em, intersection);
        tryToEat(entity, em, intersection);
    }

    @Override
    public void tick(EntityManager em) {
        tickAge++;
        this.loseEnergy(Util.BASE_LIFE_ENERGY_COST * this.getArea());

        if (this.getEnergy() < 0) {
            em.remove(this);
            em.add(onDeath());
            return;
        }

        // never changes...
        // genome.getColor().set(body);
        move();
    }

    private void move() {
        genome.getRotation().rotate(body);
        genome.getTranslation().translate(body);
        body.move();
    }

}
