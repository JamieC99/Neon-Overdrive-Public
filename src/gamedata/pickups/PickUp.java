package gamedata.pickups;

import engine.*;
import gamedata.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public abstract class PickUp extends GameObject
{
    private final int SIZE = 40;
    private final int ROWS = 1;
    private final int COLS = 4;
    private double timer;
    private final int TIMER_TARGET = 20;
    private final int TOTAL_FRAMES = ROWS * COLS;
    private double frame = 0;
    private boolean fadeIn = true;
    private BufferedImage spriteSheet;
    private final BufferedImage[] spriteArray = new BufferedImage[TOTAL_FRAMES];
    private final SFX PICKUP_SOUND;
    private final Class[] TRIGGER;
    protected GameObject triggerObject;

    public PickUp(String imagePath, String soundPath, Class ... TRIGGER)
    {
        super(0, 0);
        this.TRIGGER = TRIGGER;
        PICKUP_SOUND = new SFX(soundPath);

        try
        {
            File image = new File(imagePath);
            spriteSheet = ImageIO.read(image);

            //Load images in a 2D array
            for (int iy = 0; iy < ROWS; iy++)
            {
                for (int ix = 0; ix < COLS; ix++)
                {
                    spriteArray[ix] = spriteSheet.getSubimage(ix * SIZE, 0, SIZE, SIZE);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        solid = false;
        position();
    }




    private void position()
    {
        timer = 0;
        Random rand = new Random();
        y = rand.nextInt(GameDataHandler.BOUNDS_WEST, GameDataHandler.BOUNDS_EAST);
        x = rand.nextInt(GameDataHandler.BOUNDS_NORTH, GameDataHandler.BOUNDS_SOUTH);

        for (ObjectLayer layer : GameDataHandler.getLayers())
        {
            for (int i = 0; i < layer.getObjectList().size(); i++)
            {
                GameObject object = layer.getObjectList().get(i);
                if (getBounds().intersects(object.getBounds().getBounds2D()) && !object.equals(this))
                {
                    position();
                    break;
                }
            }
        }
    }




    @Override
    public void update(double dt)
    {
        timer += dt;

        if (fadeIn)
        {
            frame += 15 * dt;
        }
        if (!fadeIn)
        {
            frame -= 15 * dt;
        }

        if (frame >= TOTAL_FRAMES -1)
        {
            frame = TOTAL_FRAMES - 1;
            fadeIn = false;
        }
        if (frame <= 0)
        {
            frame = 0;
            fadeIn = true;
        }

        if (timer > TIMER_TARGET)
        {
            position();
        }

        collide();
    }




    private void collide()
    {
        for (int j = 0; j < GameDataHandler.getGameObjectList().size(); j++)
        {
            GameObject object = GameDataHandler.getGameObjectList().get(j);
            if (getBounds().intersects(object.getBounds().getBounds2D()))
            {
                for (Class trigger : TRIGGER)
                {
                    if (object.getClass().equals(trigger))
                    {
                        triggerObject = object;
                        pickupEffect();
                        PICKUP_SOUND.play(-10);
                        position();
                    }
                }
            }
        }
    }




    protected abstract void pickupEffect();




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        g2d.drawImage(spriteArray[(int)frame], (int) x - SIZE / 2, (int) y - SIZE / 2, null);

        if (Debug.showCollisionBounds)
        {
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public Shape getBounds()
    {
        return new Ellipse2D.Double(x - (SIZE / 2.0), y - (SIZE / 2.0), SIZE, SIZE);
    }
}