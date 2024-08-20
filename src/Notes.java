import bagel.Image;

/**
 * Represent notes in the game that falls down a specified lane.
 * This note has a type (e.g., normal, hold, bomb, speed up, slow down) and
 * belongs to a lane (e.g., up, down, left, right).
 */
public class Notes {
    private final Image NOTEDOWN_IMAGE = new Image("res/noteDown.png");
    private final Image HOLDNOTEDOWN_IMAGE = new Image("res/holdNoteDown.png");
    private final Image NOTEUP_IMAGE = new Image("res/noteUp.png");
    private final Image HOLDNOTEUP_IMAGE = new Image("res/holdNoteUp.png");
    private final Image NOTELEFT_IMAGE = new Image("res/noteLeft.png");
    private final Image HOLDNOTELEFT_IMAGE = new Image("res/holdNoteLeft.png");
    private final Image NOTERIGHT_IMAGE = new Image("res/noteRight.png");
    private final Image HOLDNOTERIGHT_IMAGE = new Image("res/holdNoteRight.png");
    private final Image NOTE2X_IMAGE = new Image("res/note2x.png");
    private final Image NOTEBOMB_IMAGE = new Image("res/noteBomb.png");
    private final Image NOTESLOWDOWN_IMAGE = new Image("res/noteSlowDown.png");
    private final Image NOTESPEEDUP_IMAGE = new Image ("res/noteSpeedUp.png");
    boolean isHolding;
    boolean isSpecial;
    private String noteLane; // special, down, left n right
    private String noteType; // normal. hold, bomb, slowdown etc
    private double startY;
    private double currentY;
    private double startFrame;
    boolean isDrawn = false;
    private double speed = 5;
    /**
     * Constructs a new Note object.
     *
     * @param noteLane The lane of the note (e.g., "Down", "Up", "Left", "Right", "Special").
     * @param noteType The type of the note (e.g., "Normal", "Hold", "Bomb", "SpeedUp", "SlowDown").
     * @param startY The starting Y-coordinate of the note (100).
     * @param startFrame The frame on which the note starts falling.
     * @param isHolding Indicates whether this note is a holding note.
     * @param isSpecial Indicates whether this note is a special note.
     */
    public Notes(String noteLane, String noteType, double startY, double startFrame,
                 boolean isHolding, boolean isSpecial) {
        this.noteLane = noteLane;
        this.noteType = noteType;
        this.startY = startY;
        this.currentY = startY;
        this.startFrame = startFrame;
        this.isHolding = isHolding;
        this.isSpecial = isSpecial;
    }

    /**
     * Draws the appropriate note on the screen in the appropriate lane.
     *
     * @param laneX The X-coordinate of the lane in which the note should be drawn.
     */
    public void draw(double laneX) {
        Image imageToDraw = null;
        // determine what kind of note it is
        switch (noteLane) {
            case "Down":
                if (noteType.equals("Hold")){
                    imageToDraw = HOLDNOTEDOWN_IMAGE;
                } else if (noteType.equals("Bomb")) {
                    imageToDraw = NOTEBOMB_IMAGE;
                } else if (noteType.equals("Normal")) {
                    imageToDraw = NOTEDOWN_IMAGE;
                }
                break;
            case "Up":
                if (noteType.equals("Hold")) {
                    imageToDraw = HOLDNOTEUP_IMAGE;
                } else if (noteType.equals("Bomb")) {
                    imageToDraw = NOTEBOMB_IMAGE;
                } else {
                    imageToDraw = NOTEUP_IMAGE;
                }
                break;
            case "Left":
                if (noteType.equals("Hold")) {
                    imageToDraw = HOLDNOTELEFT_IMAGE;
                } else if (noteType.equals("Bomb")){
                    imageToDraw = NOTEBOMB_IMAGE;
                } else {
                    imageToDraw = NOTELEFT_IMAGE;
                }
                break;
            case "Right":
                if (noteType.equals("Hold")) {
                    imageToDraw = HOLDNOTERIGHT_IMAGE;
                } else if (noteType.equals("Bomb")) {
                    imageToDraw = NOTEBOMB_IMAGE;
                }else {
                    imageToDraw = NOTERIGHT_IMAGE;
                }
                break;
            case "Special":
                if (noteType.equals("SpeedUp")) {
                    imageToDraw = NOTESPEEDUP_IMAGE;
                } else if (noteType.equals("SlowDown")) {
                    imageToDraw = NOTESLOWDOWN_IMAGE;
                } else if (noteType.equals("DoubleScore")) {
                    imageToDraw = NOTE2X_IMAGE;
                }
                break;
            default:
                return;
        }
        assert imageToDraw != null;
        imageToDraw.draw(laneX, currentY);
    }


    public double getCurrentY() {
        return currentY;
    }


    public String getNoteLane() {
        return noteLane;
    }

    /**
     * Updates the position of the note based on its speed and the current frame.
     * @param currentFrame The current frame of the game.
     */
    public void updatePosition(int currentFrame) {
        currentY = startY + speed * (currentFrame - startFrame);
    }


    public double getStartFrame() {
        return startFrame;
    }


    public String getNoteType() {
        return noteType;
    }
    /**
     * Increases the speed of the note by a specified increment.
     *
     * @param increment The amount to increase the speed by.
     * @return The new speed of the note.
     */
    public double setSpeedUp(double increment) {
        speed += increment;
        return speed;
    }

    /**
     * Increases the speed of the note by a specified increment.
     *
     * @param decrement The amount to increase the speed by.
     * @return The new speed of the note.
     */
    public double setSlowDown(double decrement) {
        speed -= decrement;
        return speed;
    }
}
