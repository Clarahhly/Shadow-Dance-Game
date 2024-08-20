import bagel.Image;
import bagel.util.Vector2;

import java.util.List;
/**
 * Represents the Guardian character in stage 3 of the game.
 * It has a fixed position and is responsible for identifying the closest enemy.
 */
public class Guardian {
    private static final Image GUARDIAN_IMAGE = new Image("res/guardian.png");
    private static final Vector2 GUARDIAN_POSITION = new Vector2(800, 600);
    /**
     * Draws the Guardian's image on the screen.
     */
    public void drawGuardian() {
        GUARDIAN_IMAGE.draw(GUARDIAN_POSITION.x, GUARDIAN_POSITION.y);
    }


    public static Vector2 getGuardianPosition() {
        return GUARDIAN_POSITION;
    }

    /**
     * Finds and returns the closest enemy to the Guardian from a given list of enemies.
     * Distance is measured using Euclidean distance.
     *
     * @param enemies A list of enemies to search through.
     * @return The nearest enemy to the Guardian or null if the enemies list is empty.
     */
    // find the closest enemy to the guardian
    public Enemy findNearestEnemy(List<Enemy> enemies) {
        Enemy nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double distance = Math.sqrt(Math.pow(GUARDIAN_POSITION.x - enemy.getX(), 2) +
                    Math.pow(GUARDIAN_POSITION.y - enemy.getY(), 2));
            if (distance < nearestDist) {
                nearestDist = distance;
                nearest = enemy;
            }
        }

        return nearest;
    }
}