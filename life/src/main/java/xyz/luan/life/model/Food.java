package xyz.luan.life.model;

import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.awt.geom.Point2D;
import java.util.List;


public class Food extends Entity {

    private static final double SIZE = 2;

    private static Polygon generateBody(Point2D position) {
        Polygon body = new Polygon(position.getX() - SIZE,
                position.getY() - SIZE,
                position.getX() + SIZE,
                position.getY() - SIZE,
                position.getX() + SIZE,
                position.getY() + SIZE,
                position.getX() - SIZE,
                position.getY() + SIZE);
        body.setFill(Util.DEFAULT_FOOD_COLOR);
        return body;
    }

    private static Polygon generateBody(Individual individual) {
        Polygon body = individual.getBody();
        body.setFill(Util.DEFAULT_FOOD_COLOR);
        return body;
    }

    public Food(Point2D position, double energy) {
        super(Food.generateBody(position), energy);
    }

    public Food(Individual individual) {
        super(Food.generateBody(individual), 0);
    }

    @Override
    public void tick(List<Entity> entities) {

    }

    @Override
    public void onCollide(Entity entity, Shape intersection, Group group, List<Entity> entities) {

    }
}
