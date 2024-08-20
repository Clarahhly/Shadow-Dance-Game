import bagel.*;

/**
 * Music Demo Sample for SWEN20003 Project 1, Semester 2, 2023
 *
 * @author Stella Li & Tharun Dharmawickrema
 */
public class Music extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "MUSIC DEMO";
    private final Image BACKGROUND_IMAGE = new Image("res/background.png");
    private final static String FONT_FILE = "res/FSO8BITR.TTF";
    private final Font INSTRUCTION_FONT = new Font(FONT_FILE, 42);
    private static final String INSTRUCTIONS = "Press Space to Play\nOr Pause Music";
    private Track track = new Track("res/track1.wav");
    private boolean started = false;
    private boolean paused = true;

    public Music(){

        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
    }


    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDance game = new ShadowDance();
        game.run();
    }

    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        INSTRUCTION_FONT.drawString(INSTRUCTIONS,
                WINDOW_WIDTH/2 - INSTRUCTION_FONT.getWidth(INSTRUCTIONS)/2,
                WINDOW_HEIGHT/2);

        if (input.wasPressed(Keys.SPACE)) {
            if (!started) {
                track.start();
                started = true;
                paused = false;
            } else if (paused) {
                track.run();
                paused = false;
            } else {
                track.pause();
                paused = true;
            }
        }

    }
}