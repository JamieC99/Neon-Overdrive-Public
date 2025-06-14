/** Group Members
 *
 * Jamie Coleman - 22016309
 * Max Thomson - 24010405
 * Detroit Travers - 24018288
 */

package engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Game creation and main method
 */
public class Game extends GameEngine
{
    // Frame constants
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 900;
    public static final int FRAME_RATE = 144; // cuz i haz 144hz monitor

    private static int mouseX;
    private static int mouseY;

    private final static MenuSystem menuSystem = new MenuSystem();

    /** Set up window and initialise game. Called from Main. */
    public Game()
    {
        // Create window
        setupWindow(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set icon
        mFrame.setIconImage(new ImageIcon("res/ui/icon.png").getImage());
        // Set title
        mFrame.setTitle("Neon Overdrive");
        // Disable resizing

        mFrame.setResizable(false);
        // Set size. This accounts for the frame decorations meaning the inner panel is the desired size
        setWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        // Place the frame in the center of the afas  screen. (when it works anyway)
        mFrame.setLocationRelativeTo(null);

        // Add a custom cursor
        try
        {
            mFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                    loadImage("res/crosshair.png"), new Point(16, 16), "CrossHair"));
        }
        catch (Exception e)
        {
            System.out.println("Could not load crosshair");
        }

        // Create world
        GameDataHandler.setupGameWorld();

        // Register the player's key listener
        mFrame.addKeyListener(GameDataHandler.getPlayerCharacter());

        // Register the player's mouse listener
        mPanel.addMouseListener(GameDataHandler.getPlayerCharacter());

        // Regsiter UI key listener
        mFrame.addKeyListener(menuSystem);
        mPanel.addMouseListener(menuSystem);

        // Ambient wind sound
        AudioClip windSound = loadAudio("res/city_sounds.wav");
        startAudioLoop(windSound);
    }




    // Update game objects
    @Override
    public void update(double dt)
    {
        if (menuSystem.getCurrentMenu().equals(MenuSystem.Menu.IN_GAME))
        {
            GameDataHandler.update(dt);
        }
    }




    // Paint game objects
    @Override
    public void paintComponent()
    {
        // Clear background and save transform
        changeBackgroundColor(black);
        clearBackground(WINDOW_WIDTH, WINDOW_HEIGHT);
        saveCurrentTransform();

        // Paint game objects
        if (!menuSystem.getCurrentMenu().equals(MenuSystem.Menu.MAIN)
        && !menuSystem.getCurrentMenu().equals(MenuSystem.Menu.INSTRUCTIONS))
        {
            GameDataHandler.paintComponent(mGraphics);
            menuSystem.paintVehicleHealthbars(mGraphics);
        }

        // Restore the transform to display the UI properly
        restoreLastTransform();

        /* Draw UI */
        paintUI(mGraphics);
    }




    private void paintUI(Graphics2D g2d)
    {
        menuSystem.paintHUD(g2d);
        menuSystem.paintMenu(g2d);

        if (Debug.showPlayerProperties)
        {
            if (GameDataHandler.getPlayerCharacter().getCurrentCar() == null) return;
            changeColor(black);
            drawText(10, 40, "Player X: " + GameDataHandler.getPlayerCharacter().getCurrentCar().getX());
            drawText(10, 100, "Player Y: " + GameDataHandler.getPlayerCharacter().getCurrentCar().getY());
            drawText(10, 160, "Player Dir: " + GameDataHandler.getPlayerCharacter().getCurrentCar().getDirection());
            drawText(10, 220, "Player Speed: " + GameDataHandler.getPlayerCharacter().getCurrentCar().getSpeed());
            drawText(10, 320, "Zoom: " + (max(1, min((GameDataHandler.getPlayerCharacter().getCurrentCar().getSpeed()/300),1.2))));
        }
    }




    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /** Return mouse x in world space */
    public static int getMouseWorldX()
    {
        return mouseX - (int) GameDataHandler.getCameraX();
    }

    /** Return mouse y in world space */
    public static int getMouseWorldY()
    {
        return mouseY - (int) GameDataHandler.getCameraY();
    }

    public static MenuSystem getMenuSystem()
    {
        return menuSystem;
    }

    /** Main */
    public static void main(String[] args)
    {
        createGame(new Game(), FRAME_RATE);
    }
}
