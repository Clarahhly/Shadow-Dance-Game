import bagel.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.lang.Math;public class ShadowDance extends AbstractGame  {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DANCE";
    private final static String GAME_INSTRUCTIONS1 = "SELECT LEVELS WITH";
    private final static String GAME_INSTRUCTIONS2 = "NUMBER KEYS";
    private final static String GAME_INSTRUCTIONS3 = "1    2    3";
    private final Image BACKGROUND_IMAGE = new Image("res/background.png");
    private final Font font = new Font("res/FSO8BITR.TTF", 64);
    private final Font font24 = new Font("res/FSO8BITR.TTF", 24);
    private final Font score_font = new Font("res/FSO8BITR.TTF", 30);
    private final Font scoremsg_font = new Font("res/FSO8BITR.TTF", 40);
    private Random random = new Random();
    Guardian guardian = new Guardian();
    private List<Projectile> projectiles = new ArrayList<>();
    private List<Notes> notes = new ArrayList<>();
    private final static ArrayList<Lanes> lanes = new ArrayList<Lanes>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<Integer> doubleScoreCounters = new ArrayList<>();

    private boolean gameStart = false;
    private boolean gameEnd = false;
    private int targetScore1 = 150;
    private int targetScore2 = 400;
    private int targetScore3 = 350;
    private String csvFile = "";
    private int gameLevel = 0;
    private int LANEDOWN_X;
    private int LANEUPORSPEICAL_X;
    private int LANELEFT_X;
    private int LANERIGHT_X;
    private double startY = 100;
    private int currentFrame = 0;
    private int k=0;
    private int messageFrameCounter = 0;
    private String currentScoreMessage = "";
    private final int PERFECT_THRESHOLD = 15;
    private final int GOOD_THRESHOLD = 50;
    private final int BAD_THRESHOLD = 100;
    private final int MISS_THRESHOLD = 200;
    private final int PERFECT_SCORE = 10;
    private final int GOOD_SCORE = 5;
    private final int BAD_SCORE = -1;
    private final int MISS_SCORE = -5;
    protected final static int STATIONARY_Y = 657;
    protected final static int HOLD_NOTE_Y_ADJUSTMENT = 82;
    protected final static int SPECIAL_NOTE_ACTIVATED = 50;
    private int isDoubleScore = 0;
    private Track track1 = new Track("res/track1.wav");
    private Track track2 = new Track("res/track2.wav");
    private Track track3 = new Track("res/track3.wav");




    public ShadowDance(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
    }

    /**
     * Reads data from the specified CSV file and initializes the game objects.
     *
     * @param csvFile The CSV file to read data from.
     * @throws IOException if there's an error reading from the specified file.
     */
    private void readCSV(String csvFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                // for the 1st 4 lines, add it to the arraylist for lanes
                if (columns[0].equals("Lane")) {
                    Lanes lane = new Lanes(columns[1], Integer.parseInt(columns[2]));
                    lanes.add(lane);
                    if (columns[1].equals("Down")) {
                        LANEDOWN_X = lane.getLANE_X();
                    } else if (columns[1].equals("Up") || columns[1].equals("Special")) {
                        LANEUPORSPEICAL_X = lane.getLANE_X();
                    } else if (columns[1].equals("Left")) {
                        LANELEFT_X = lane.getLANE_X();
                    } else if (columns[1].equals("Right")) {
                        LANERIGHT_X = lane.getLANE_X();
                    }
                } else {
                    //add the details of each note to the note array
                    String noteLane = columns[0];
                    String noteType = columns[1];
                    double startFrame = Integer.parseInt(columns[2]);
                    boolean isHolding = noteType.equals("Hold");
                    boolean isSpecial = noteLane.equals("Special") || noteType.equals("Bomb");



                    Notes note = new Notes(noteLane, noteType, startY, startFrame, isHolding, isSpecial);
                    notes.add(note);
                    for (Lanes lane : lanes) {
                        if (lane.getLaneType().equals(noteLane)) {
                            lane.addNote(note);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDance game = new ShadowDance();
        game.run();
    }
    /**
     * Handles the stationary features of the game.
     * <p>
     * If there are no notes present, it reloads them from the CSV file.
     * Lanes are drawn.
     * In the third game level, it also draws the guardian.
     * </p>
     */
    protected void runningStationary() {
        if (notes.isEmpty()) {
            readCSV(csvFile); // Call readCSV if column3Values is empty
        }
        for (Lanes lane : lanes) {
            lane.draw();
            if (gameLevel == 3) {
                guardian.drawGuardian();
            }
        }
    }
    /**
     * Handles the behavior of falling notes in the game.
     * <p>
     * This method updates the position of each note based on the current frame, drawing
     * them in their respective lanes.
     * It also handles collisions between enemies and
     * normal notes, removing notes when they collide or go beyond the screen.
     * </p>
     */
    protected void fallingNotes() {
        for (Iterator<Notes> iterator = notes.iterator(); iterator.hasNext(); ) {
            Notes notes = iterator.next();
            double laneX = 0;
            // ensure that the frame is correct so that the note will be printed at the right timing
            if (currentFrame >= notes.getStartFrame()) {
                //make the note fall at 2 pixels per frame as the frames increase
                notes.updatePosition(currentFrame);
                //identify which lane the note falls from
                switch (notes.getNoteLane()) {
                    case "Down":
                        laneX = LANEDOWN_X;
                        break;
                    case "Up":
                    case "Special":
                        laneX = LANEUPORSPEICAL_X;
                        break;
                    case "Left":
                        laneX = LANELEFT_X;
                        break;
                    case "Right":
                        laneX = LANERIGHT_X;
                        break;
                }
                //printing the note
                notes.draw(laneX);
                notes.isDrawn = true;
            }
            // if the enemy collides with the normal notes
            if (gameLevel == 3) {
                for (Enemy enemy : enemies) {
                    if (notes.getNoteType().equals("Normal") &&
                            enemy.isColliding(laneX, notes.getCurrentY())) {
                        iterator.remove();
                    }
                }
            }
            if (notes.getCurrentY() > WINDOW_HEIGHT) {
                iterator.remove();
                if (!notes.isSpecial) {
                    k += MISS_SCORE;
                    currentScoreMessage = "MISS";
                    messageFrameCounter = 15;
                }
            }

        }
        currentFrame++;
    }
    /**
     * Removes the specified note from the notes list and from all lanes.
     * @param note The note to be removed.
     */
    public void removeNoteFromAll(Notes note) {
        notes.remove(note);

        // Notify all lanes to remove this note as well
        for (Lanes lane : lanes) {
            lane.removeNote(note);
        }
    }
    /**
     * Evaluates and updates the score based on the distance of the note to the stationary Y position
     * when the user presses the corresponding key. Depending on the accuracy,
     * the score increases or decreases and displays its corresponding messages such as
     * "PERFECT", "GOOD", "BAD", or "MISS".
     * If the note is special and within a specific range, a special note method is called.
     *
     * @param noteDirection The key that was pressed which corresponds to the note direction
     */
    public void score(String noteDirection) {
        currentScoreMessage = ""; // Reset the score message here
        for (Lanes lane : lanes) {
            // ensure key is pressed for the correct note
            if (lane.getLaneType().equals(noteDirection)) {
                //find the closest note in the lane to the Stationary y note
                Notes closestNote = lane.findClosestNoteToY(STATIONARY_Y);
                if (closestNote != null) {
                    double distance;
                    // if holding note or not holding note
                    if (closestNote.isHolding){
                        distance = Math.abs((closestNote.getCurrentY() + HOLD_NOTE_Y_ADJUSTMENT) - STATIONARY_Y);
                    } else {
                        distance = Math.abs(closestNote.getCurrentY() - STATIONARY_Y);
                    }
                    // It's a special note and within range
                    if (closestNote.isSpecial && distance <= SPECIAL_NOTE_ACTIVATED ) {
                        specialNote(closestNote, lane);
                        removeNoteFromAll(closestNote); //Remove the note once activated
                        return; // Exit the method
                    }

                    //counting score and printing the message accordingly
                    if (distance <= PERFECT_THRESHOLD && !closestNote.isSpecial) {
                        if (isDoubleScore != 0) {
                            k += PERFECT_SCORE * (2 * isDoubleScore);
                        }
                        else {
                            k += PERFECT_SCORE;
                        }
                        currentScoreMessage = "PERFECT";
                        if (!closestNote.isHolding) {
                            removeNoteFromAll(closestNote);
                        }
                    } else if (distance <= GOOD_THRESHOLD && !closestNote.isSpecial) {
                        if (isDoubleScore != 0) {
                            k += GOOD_SCORE * (2 * isDoubleScore);
                        }
                        else {
                            k += GOOD_SCORE;
                        }
                        currentScoreMessage = "GOOD";
                        if (!closestNote.isHolding) {
                            removeNoteFromAll(closestNote);
                        }
                    } else if (distance <= BAD_THRESHOLD && !closestNote.isSpecial) {
                        if (isDoubleScore != 0) {
                            k += BAD_SCORE * (2 * isDoubleScore);
                        }
                        else {
                            k += BAD_SCORE;
                        }
                        currentScoreMessage = "BAD";
                        if (!closestNote.isHolding) {
                            removeNoteFromAll(closestNote);
                        }
                    } else if (distance <= MISS_THRESHOLD && !closestNote.isSpecial) {
                        if (isDoubleScore != 0) {
                            k += MISS_SCORE * (2 * isDoubleScore);
                        }
                        else {
                            k += MISS_SCORE;
                        }
                        currentScoreMessage = "MISS";
                        if (!closestNote.isHolding) {
                            removeNoteFromAll(closestNote);
                        }
                    }
                    messageFrameCounter = 30; // Resetting counter every time
                }
                break;
            }
        }
    }
    /**
     * Evaluates and updates the score based on the distance of holding note to the stationary
     * Y position when the user presses the corresponding key. Depending on the accuracy,
     * the score increases or decreases and displays its corresponding messages such as
     * "PERFECT", "GOOD", "BAD", or "MISS".
     *
     * @param noteDirection The key that was pressed which corresponds to the note direction
     */
    public void holdingScore(String noteDirection) {
        for (Lanes lane : lanes) {
            if (lane.getLaneType().equals(noteDirection)) {
                Notes closestNote = lane.findClosestNoteToY(STATIONARY_Y);
                // note has to be a holding note in order for this method to run
                if (closestNote != null && closestNote.isHolding) {
                    double distance;
                    distance = Math.abs((closestNote.getCurrentY() - HOLD_NOTE_Y_ADJUSTMENT) - STATIONARY_Y);
                    if (distance <= PERFECT_THRESHOLD) {
                        if (isDoubleScore != 0) {
                            k += PERFECT_SCORE * (2 * isDoubleScore);
                        }
                        else {
                            k += PERFECT_SCORE;
                        }
                        currentScoreMessage = "PERFECT";
                        removeNoteFromAll(closestNote);
                    } else if (distance <= GOOD_THRESHOLD) {
                        if (isDoubleScore != 0) {
                            k += GOOD_SCORE * (2 * isDoubleScore);
                        }
                        else {
                            k += GOOD_SCORE;
                        }
                        currentScoreMessage = "GOOD";
                        removeNoteFromAll(closestNote);
                    } else if (distance <= BAD_THRESHOLD) {
                        if (isDoubleScore != 0) {
                            k += BAD_SCORE * (2 * isDoubleScore);
                        }
                        else {
                            k += BAD_SCORE;
                        }
                        currentScoreMessage = "BAD";
                        removeNoteFromAll(closestNote);
                    } else if (distance <= MISS_THRESHOLD) {
                        if (isDoubleScore != 0) {
                            k += MISS_SCORE * (2 * isDoubleScore);
                        }
                        else {
                            k += MISS_SCORE;
                        }
                        currentScoreMessage = "MISS";
                        removeNoteFromAll(closestNote);
                    }
                    messageFrameCounter = 30; // Resetting counter every time
                }
                break;
            }
        }
    }
    /**
     * Different types of special notes have different effects on the game, such as doubling the score,
     * clearing a lane, slowing down notes, or speeding them up. The method updates game state variables
     * and displays a messages about the effect triggered by the special note.
     *
     * @param note The note that has been activated.
     * @param lane The lane in which the special note is located.
     */
    public void specialNote(Notes note, Lanes lane) {
        switch (note.getNoteType()) {
            case "DoubleScore":
                isDoubleScore += 1;
                //add the count-down for the double score
                doubleScoreCounters.add(480);
                currentScoreMessage = "Double Score";
                removeNoteFromAll(note);
                break;
            case "Bomb":
                Iterator<Notes> iterator = notes.iterator();
                while (iterator.hasNext()) {
                    Notes currNote = iterator.next();
                    // makes sure only notes on the screen is removed
                    if (currNote.getCurrentY() <= WINDOW_HEIGHT && currNote.getCurrentY() >= 0 &&
                            currNote.getNoteLane().equals(note.getNoteLane()) &&
                            currNote.isDrawn ) {
                        iterator.remove();
                    }
                }
                currentScoreMessage = "Lane Clear";
                removeNoteFromAll(note);
                break;
            case "SlowDown":
                for (Notes currentNote : notes) {
                    currentNote.setSlowDown(1);
                }
                currentScoreMessage = "Slow Down";
                removeNoteFromAll(note);
                k += 15;
                break;
            case "SpeedUp":
                for (Notes currentNote : notes) {
                    currentNote.setSpeedUp(1);
                }
                currentScoreMessage = "Speed Up";
                removeNoteFromAll(note);
                k += 15;
                break;
        }
        messageFrameCounter = 30; // Display the message for 30 frames
    }
    /**
     * Renders a score-related message at the center of the game window for a 30 of frames.
     */
    public void renderScoreMessage() {
        if (messageFrameCounter > 0) {
            // Render the message to the center of the window
            double textX = (double) WINDOW_WIDTH / 2 - scoremsg_font.getWidth(currentScoreMessage) / 2;
            double textY = (double) WINDOW_HEIGHT / 2 -40;

            scoremsg_font.drawString(currentScoreMessage, textX, textY);
            messageFrameCounter--; // decrement the frame counter
        }
    }

    /**
     * Count down for the double score effect.
     */
    public void updateDoubleScoreCounters() {
        ListIterator<Integer> iterator = doubleScoreCounters.listIterator();
        while (iterator.hasNext()) {
            int counter = iterator.next();
            counter--;
            if (counter <= 0) {
                iterator.remove();
                isDoubleScore -= 1;
            } else {
                // update the value in the list
                iterator.set(counter);
            }
        }
    }

    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     * Handles the different game levels and state of game (game end or game start)
     *
     * @param input To detect and process player's keyboard inputs.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }
        BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        if (!gameStart && !gameEnd) {
            font.drawString(GAME_TITLE, 220, 250);
            font24.drawString(GAME_INSTRUCTIONS1, 320, 440);
            font24.drawString(GAME_INSTRUCTIONS2, 320, 470);
            font24.drawString(GAME_INSTRUCTIONS3, 320, 550);

            if (input.wasPressed(Keys.NUM_1)) {
                csvFile = "res/level1-.csv";
                gameLevel = 1;
                gameStart = true;
                track1.start();

            } else if (input.wasPressed(Keys.NUM_2)) {
                csvFile = "res/level2.csv";
                gameLevel = 2;
                gameStart = true;
                track2.start();
            } else if (input.wasPressed(Keys.NUM_3)) {
                csvFile = "res/level3.csv";
                gameLevel = 3;
                gameStart = true;
                track3.start();
            }
        }

        if (gameStart && !gameEnd) {
            score_font.drawString("Score " + k, 35, 35);
            if (input.wasPressed(Keys.DOWN)) {
                score("Down");
            } else if (input.wasPressed(Keys.UP)) {
                score("Up");
            } else if (input.wasPressed(Keys.LEFT)) {
                score("Left");
            } else if (input.wasPressed(Keys.RIGHT)) {
                score("Right");
            }else if (input.wasPressed(Keys.SPACE)) {
                score("Special");
            }
            //will only calculate if note is holding note
            if (input.wasReleased(Keys.DOWN)) {
                holdingScore("Down");
            } else if (input.wasReleased(Keys.UP)) {
                holdingScore("Up");
            } else if (input.wasReleased(Keys.LEFT)) {
                holdingScore("Left");
            } else if (input.wasReleased(Keys.RIGHT)) {
                holdingScore("Right");
            }
            if (gameLevel == 3) {
                if (currentFrame % 600 == 0 && currentFrame != 0) {
                    //determine where the enemy spawns
                    int x = random.nextInt(801) + 100;
                    int y = random.nextInt(401) + 100;
                    int direction = random.nextBoolean() ? 1 : -1;
                    enemies.add(new Enemy(x, y, direction));
                }
                // Loop through each enemy and draw
                for (Enemy enemy : enemies) {
                    enemy.updatePosition();
                    enemy.draw();
                }
                if (input.wasPressed(Keys.LEFT_SHIFT)) {
                    Enemy nearestEnemy = guardian.findNearestEnemy(enemies);
                    if (nearestEnemy != null) {
                        projectiles.add(new Projectile(Guardian.getGuardianPosition(),
                                nearestEnemy.getX(), nearestEnemy.getY()));
                    }
                }

                //update projectile position and rotation
                Iterator<Projectile> projectileIterator = projectiles.iterator();
                while (projectileIterator.hasNext()) {
                    Projectile projectile = projectileIterator.next();
                    projectile.update();
                    projectile.draw();

                    // Check collisions with enemies
                    Iterator<Enemy> enemyIterator = enemies.iterator();
                    boolean hasCollided = false;
                    while (enemyIterator.hasNext()) {
                        Enemy enemy = enemyIterator.next();
                        if (projectile.isColliding(enemy.getX(), enemy.getY())) {
                            enemyIterator.remove();  // Remove enemy on collision
                            hasCollided = true;
                            break;  // Exit the inner loop after removing the enemy
                        }
                    }
                    // Remove the projectile if outside of screen
                    if (hasCollided || projectile.isOutsideScreen()) {
                        projectileIterator.remove();
                    }
                }
            }
            runningStationary();
            fallingNotes();
            updateDoubleScoreCounters();
            renderScoreMessage();
            //end the game when all the notes have fallen
            if (notes.isEmpty()) {
                gameEnd = true;
                gameStart = false;
            }
        }

        if (gameEnd && !gameStart) {
            String instructions = "PRESS SPACE TO RETURN TO LEVEL SELECTION";
            double instructionsX = (double) WINDOW_WIDTH / 2 - font24.getWidth(instructions) / 2;
            font24.drawString(instructions, instructionsX, 500);
            int targetScore = 0;
            if (gameLevel == 1) {
                targetScore = targetScore1;
            } else if (gameLevel == 2) {
                targetScore = targetScore2;
            } else{
                targetScore = targetScore3;
            }
            if (k >= targetScore) {
                String message = "CLEAR!";
                double textX = (double) WINDOW_WIDTH / 2 - font.getWidth(message) / 2;
                double textY = (double) WINDOW_HEIGHT / 2;

                font.drawString(message, textX, textY);

            } else {
                String message = "TRY AGAIN";
                double textX = (double) WINDOW_WIDTH / 2 - font.getWidth(message) / 2;
                double textY = (double) WINDOW_HEIGHT / 2;

                font.drawString(message, textX, textY);
            }
            // restart the game
            if (input.wasPressed(Keys.SPACE)){
                gameEnd = false;
                gameStart = false;
                k = 0;
                gameLevel = 0;
                notes.clear();
                lanes.clear();
                enemies.clear();
                projectiles.clear();
                csvFile = "";
                messageFrameCounter = 0;
                currentScoreMessage = "";
                currentFrame = 0;
                isDoubleScore = 0;
            }
        }
    }
}
