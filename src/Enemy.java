import bagel.Image;
/**
 * Represents the enemy in stage 3 of the game that moves horizontally on the screen.
 * The enemy's position and movement are defined by its x, y coordinates, and its direction.
 */
public class Enemy {
    private int x;
    private int y;
    private int direction;
    private static final Image ENEMY_IMAGE = new Image("res/enemy.png");
    /**
     * Constructs a new Enemy object with specified attributes.
     *
     * @param x Initial x-coordinate of the enemy.
     * @param y Initial y-coordinate of the enemy.
     * @param direction Initial direction in which the enemy moves. A positive value moves to the right
     * and a negative value moves to the left.
     */
    public Enemy(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    /**
     * Checks if the enemy's position collides with normal notes.
     *
     * @param x1 x-coordinate of the note
     * @param y1 y-coordinate of the note.
     * @return true if there is a collision, false otherwise.
     */
    // check if enemy collides with normal note
    public boolean isColliding(double x1, double y1) {
        double distance = Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));
        return distance <= 104;
    }
    /**
     * Updates the enemy's position based on its direction.
     * If the enemy hits the edges (x=100 or x=900), it reverses direction.
     */
    public void updatePosition() {
        x += direction;
        // change direction when it hits 100 or 900
        if (x < 100) {
            x = 100;
            direction *= -1;
        } else if (x > 900) {
            x = 900;
            direction *= -1;
        }
    }


    public int getY() {
        return y;
    }


    public int getX() {
        return x;
    }

    /**
     * Draws the enemy image on the screen at its current position.
     */
    public void draw() {
        ENEMY_IMAGE.draw(x, y);
    }


}
