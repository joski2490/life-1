package xyz.luan.life.model;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Shape;

public class Food extends Entity {

    private static final double SIZE = 2d;

    private static EntityShape generateBody(Point2D position) {
        double[] chars = {
                SIZE, 1, // a * sin^2(b * t)
                0, 0, 0, // a * sin^2(b * t) * cos^2(c * t)
                SIZE, 1, // a * cos^2(b * t)
                0, 0, // a * sin(b)
                SIZE / 8d, 2, 2, // a * sin(b) * cos(c)
                0, 0}; // a * cos(b)
        return new EntityShape(position, chars, Util.DEFAULT_FOOD_COLOR);
    }

    private static EntityShape generateBody(Individual individual) {
        EntityShape body = individual.getBody();
        body.setColor(Util.DEFAULT_FOOD_COLOR);
        return body;
    }

    public Food(Point2D position, double energy) {
        super(Food.generateBody(position), energy);
    }

    public Food(Individual individual) {
        super(Food.generateBody(individual), 0);
    }

    @Override
    public boolean tick() {
        return true;
    }

    @Override
    public void onCollide(Entity entity, Shape intersection, Group group, List<Entity> entities) {

    }

    @Override
    public Entity onDeath() {
        return null;
    }
}
