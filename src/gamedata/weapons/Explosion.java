package gamedata.weapons;

import engine.*;
import gamedata.GameObject;
import gamedata.particles.SparkEmitter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class Explosion extends GameObject
{
    private final int WIDTH = 128;
    private final int HEIGHT = 128;
    private final int ROWS = 4;
    private final int COLS = 4;
    private int totalFrames = ROWS * COLS;
    public static int count;

    private BufferedImage[] explosionSprite = new BufferedImage[totalFrames];
    private double frame = 0;

    /** Create collision bounds object. This allows us to expand the bounds throughout the explosion. */
    private Ellipse2D collisionBounds = new Ellipse2D.Double();
    private double collisionBoundsSize = 50;

    public Explosion(double x, double y)
    {
        super(x,y);
        solid = true;

        count++;
        BufferedImage explosionSheet;
        try
        {
            WeaponSounds.EXPLOSION.play(0);
            File image = new File("res/explosion.png");
            explosionSheet = ImageIO.read(image);

            //Load images in a 2D array
            for (int iy = 0; iy < ROWS; iy++)
            {
                for (int ix = 0; ix < COLS; ix++)
                {
                    //had to lift this from the engine because it's not static there.
                    explosionSprite[iy * COLS + ix] = explosionSheet.getSubimage(ix * WIDTH, iy * HEIGHT, WIDTH, HEIGHT);
                    //128 is the width and 128 is the height of the sprite
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        GameStats.increaseChaos(GameStats.MINE_CHAOS);
    }




    @Override
    public void update(double dt)
    {
        // Expand collision bounds
        collisionBoundsSize += 100 * dt;
        collisionBounds = new Ellipse2D.Double(
                x - collisionBoundsSize, y - collisionBoundsSize,
                collisionBoundsSize * 2, collisionBoundsSize * 2);

        frame += 30 * dt;
        if (frame >= totalFrames)
        {
            count--;
            GameDataHandler.removeObject(this);
        }
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        g2d.drawImage(explosionSprite[(int)frame], (int) x - WIDTH / 2, (int) y - HEIGHT / 2, null);
        g2d.setColor(Color.ORANGE);
        g2d.draw(collisionBounds);
    }




    @Override
    public Shape getBounds() { return collisionBounds; }
}