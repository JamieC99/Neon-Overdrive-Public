package gamedata.weapons;

import engine.Debug;
import engine.GameDataHandler;
import engine.SFX;
import gamedata.GameObject;
import gamedata.particles.SmokeEmitter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Fire extends GameObject
{
    private int width = 128;
    private int height = 128;
    private int rows = 5;
    private int cols = 5;
    private int totalFrames = rows * cols;
    private GameObject parent;
    private double timer = 0;

    private static ArrayList<GameObject> objectsOnFire = new ArrayList<>();

    private BufferedImage[] fireSprite = new BufferedImage[totalFrames];
    private double frame = 0;
    private SFX fireSFX = new SFX("res/fire.wav");
    private SmokeEmitter smoke;

    public Fire(double x, double y, GameObject parent)
    {
        super(x, y);
        this.parent = parent;
        solid = false;

        objectsOnFire.add(parent);
        BufferedImage fireSheet = null;
        try
        {
            fireSFX.loop(-50);
            File image = new File("res/fire.png");
            fireSheet = ImageIO.read(image);
            //Load images in a 2D array
            for (int iy = 0; iy < rows; iy++)
            {
                for (int ix = 0; ix < cols; ix++)
                {
                    //had to lift this from the engine because it's not static there.
                    fireSprite[iy * cols + ix] = fireSheet.getSubimage(ix * 128, iy * 128, 128, 128);
                    //128 is the width and 128 is the height of the sprite
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        smoke = new SmokeEmitter(x, y, 0);
        GameDataHandler.addObject(smoke);
    }

    public SFX getFireSFX() { return fireSFX; }


    @Override
    public void update(double dt)
    {
        timer += dt;
        frame += 30 * dt;
        x = parent.getX();
        y = parent.getY();
        fireSFX.proximityVolume(x, y, 1000);
        direction = parent.getDirection();

        smoke.move((float) x, (float) y);

        if (frame >= totalFrames)
        {
            frame = 0;
        }

        // Despawn after 30 seconds.
        if (timer >= GameDataHandler.DESPAWN_TIME)
        {
            fireSFX.stop();
            if (parent != null)
            {
                objectsOnFire.remove(parent);
            }
            objectsOnFire.clear();
            smoke.disable();
            fireSFX.close();
            GameDataHandler.removeObject(this);
        }
    }

    @Override
    public void paintComponent(Graphics2D g2d)
    {
        AffineTransform original = g2d.getTransform();
        g2d.rotate(Math.toRadians(parent.getDirection()-90), x, y);
        g2d.drawImage(fireSprite[(int) frame], (int) x - (width / 2), (int) y  - (height / 2), null);
        g2d.setTransform(original);
        if (Debug.showCollisionBounds)
        {
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public Shape getBounds() { return parent.getBounds(); }
}