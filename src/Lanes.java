import bagel.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the lanes, which can have one of several types
 * such as "Down", "Up", "Left", "Right", and "Special".
 * Each lane can contain multiple notes that move through it.
 */
public class Lanes {
    private final Image LANEDOWN_IMAGE = new Image("res/laneDown.png");
    private final Image LANEUP_IMAGE = new Image("res/laneUp.png");
    private final Image LANELEFT_IMAGE = new Image("res/laneLeft.png");
    private final Image LANERIGHT_IMAGE = new Image("res/laneRight.png");
    private final Image LANESPECIAL_IMAGE = new Image("res/laneSpecial.png");
    private String laneType; // special left right up or down
    private int LANE_X; // position
    private final static int LANE_Y = 384;
    private List<Notes> notes = new ArrayList<>();

    /**
     * Constructs a new Lanes object with the given type and position.
     *
     * @param laneType The type of the lane ("Down", "Up", "Left", "Right", or "Special").
     * @param LANE_X The x-coordinate of the lane's position.
     */
    public Lanes(String laneType, int LANE_X) {
        this.laneType = laneType;
        this.LANE_X = LANE_X;
    }

    public int getLANE_X() {
        return this.LANE_X;
    }

    public String getLaneType() {
        return this.laneType;
    }


    /**
     * Draws the lane image on the screen based on its type.
     */
    public void draw() {
        if (laneType.equals("Down")) {
            LANEDOWN_IMAGE.draw(LANE_X, LANE_Y);
        }
        if (laneType.equals("Up")) {
            LANEUP_IMAGE.draw(LANE_X, LANE_Y);
        }
        if (laneType.equals("Left")) {
            LANELEFT_IMAGE.draw(LANE_X, LANE_Y);
        }
        if (laneType.equals("Right")) {
            LANERIGHT_IMAGE.draw(LANE_X, LANE_Y);
        }
        if (laneType.equals("Special")) {
            LANESPECIAL_IMAGE.draw(LANE_X, LANE_Y);
        }
    }
    /**
     * Finds the note that is closest to the given y-coordinate within the lane.
     *
     * @param STATIONARY_Y The y-coordinate of the stationary note of each lane.
     * @return The closest note to the specified y-coordinate, or null if there are no notes in the lane.
     */

    // closest note to the stationary note of each lane
    public Notes findClosestNoteToY(int STATIONARY_Y) {
        Notes nearestNote = null;
        double smallestDistance = Double.MAX_VALUE;

        for (Notes notes : notes) {
            double distance = Math.abs(notes.getCurrentY()- STATIONARY_Y);
            if (distance < smallestDistance) {
                smallestDistance = distance;
                nearestNote = notes;
            }
        }

        return nearestNote;  // This will return null if there are no notes.
    }

    /**
     * Removes a specified note from the lane.
     *
     * @param note The note to be removed.
     */
    public void removeNote(Notes note) {
        notes.remove(note);
    }

    /**
     * Adds a specified note to the lane.
     *
     * @param note The note to be added.
     */
    public void addNote(Notes note) {
        notes.add(note);
    }
}
