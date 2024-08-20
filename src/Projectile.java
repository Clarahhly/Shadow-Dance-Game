import bagel.*;
import bagel.util.Vector2;

import java.util.List;
import java.lang.Math;
/**
 * Represents a projectile from stage 3 of the game that moves from a starting position
 * towards a target location. This projectile is used to target and remove enemies in the game.
 * Projectile will be removed if it is out of screen.
 */
public class Projectile {
    private Vector2 position;
    private double dirX;
    private double dirY;
    private static final double speed = 6;
    private static final Image PROJECTILE_IMAGE = new Image("res/arrow.png");
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;
    private static final double COLLISION_DISTANCE = 62;
    /**
     * Constructs a new projectile.
     *
     * @param startPosition Initial position of the projectile.
     * @param targetX X-coordinate of the enemy target location.
     * @param targetY Y-coordinate of the enemy target location.
     */
    public Projectile(Vector2 startPosition, double targetX, double targetY) {
        this.position = startPosition;
        double angle = Math.atan2(targetY - position.y, targetX - position.x);
        this.dirX = Math.cos(angle);
        this.dirY = Math.sin(angle);
    }

    /**
     * Checks if the projectile has collided with an enemy based on their positions.
     *
     * @param enemyX X-coordinate of the enemy's position.
     * @param enemyY Y-coordinate of the enemy's position; false otherwise.
     */
    // check if projectile collides with enemy
    public boolean isColliding(double enemyX, double enemyY) {
        double distance = Math.sqrt(Math.pow(position.x - enemyX, 2) + Math.pow(position.y - enemyY, 2));
        return distance <= COLLISION_DISTANCE;
    }
    /**
     * Updates the position of the projectile based on its direction and speed.
     * This method should be called every frame to move the projectile.
     */
    public void update() {
        double newX = position.x + dirX * speed;
        double newY = position.y + dirY * speed;
        position = new Vector2(newX, newY);
    }
    /**
     * Draws the projectile on the screen with the appropriate rotation based on its direction.
     */
    public void draw() {
        // determine the direction of the image
        double rotation = Math.atan2(dirY, dirX);
        PROJECTILE_IMAGE.draw(position.x, position.y, new DrawOptions().setRotation(rotation));
    }
    /**
     * Checks if the projectile has moved outside the boundaries of the game screen.
     *
     * @return true if the projectile is outside the screen boundaries; false otherwise.
     */
    public boolean isOutsideScreen() {
        return position.x < 0 || position.x > WINDOW_WIDTH || position.y < 0 || position.y > WINDOW_HEIGHT;
    }
}