package gamedata.weapons;

import engine.Debug;
import engine.Game;
import engine.GameDataHandler;
import engine.GameStats;
import engine.ObjectLayer;
import gamedata.GameObject;

import gamedata.characters.CopCharacter;
import gamedata.characters.PlayerCharacter;
import gamedata.vehicles.CopCar;
import gamedata.vehicles.PlayerCar;
import gamedata.vehicles.Vehicle;
import gamedata.world.Building;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class Mine extends GameObject
{
    BufferedImage[] mineSprite = new BufferedImage[2];// 0 is active 1 is inert sprite
    private int width = 20;
    private int damage = 35;
    private double cooldownTimer;
    private double cooldown; //the mine will be inert until this is 0 (it is measured in seconds)
    private final int SIZE = 20;

    /// this constructor allows you to choose WHERE the mine is spawned.
    public Mine(double x, double y, double coolDown)
    {
        super(x,y);
        this.cooldown = coolDown;
        try
        {
            File image = new File("res/mine.png");
            BufferedImage mineSheet = ImageIO.read(image);
            mineSprite[0] = mineSheet.getSubimage(0, 0, 20, 20);//active
            mineSprite[1] = mineSheet.getSubimage(20, 0, 20, 20);//inert
            // Convert to a buffered image
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void collide()
    {
        for (ObjectLayer layer : GameDataHandler.getLayers())
        {
            for (int j = 0; j < layer.getObjectList().size(); j++)
            {
                GameObject object = layer.getObjectList().get(j);

                //if it hits any object
                if (getBounds().intersects(object.getBounds().getBounds2D()) && !(object instanceof Mine))
                {
                    if (object instanceof Vehicle car)
                    {
                        car.destroy();
                        GameDataHandler.addObject(new Explosion(x, y));
                        GameDataHandler.removeObject(this);
                        GameDataHandler.addObject(new Fire(x, y, object));
                    }

                    if (object instanceof Projectile)
                    {
                        GameDataHandler.addObject(new Explosion(x, y));
                        if (object instanceof Rocket rocket)
                        {
                            rocket.getSmoke().disable();
                        }
                        GameDataHandler.removeObject(object);
                        GameDataHandler.removeObject(this);
                    }

                    if (object instanceof Explosion)
                    {
                        GameDataHandler.addObject(new Explosion(x, y));
                        GameDataHandler.removeObject(this);
                    }

                    if (object instanceof PlayerCharacter)
                    {
                        GameDataHandler.addObject(new Explosion(x, y));
                        GameStats.damagePlayer(damage);
                        GameDataHandler.removeObject(this);
                    }

                    if (object instanceof CopCharacter)
                    {
                        GameDataHandler.addObject(new Explosion(x, y));
                        GameDataHandler.removeObject(this);
                    }
                }
            }
        }
    }




    @Override
    public void update(double dt)
    {
        //this will prevent them from exploding upon spawn from cop cars.
        if (cooldownTimer < cooldown)
        {
            cooldownTimer += dt;
            return;
        }
        collide();
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        AffineTransform defaultTransform = g2d.getTransform();
        g2d.translate(x - SIZE / 2, y - SIZE / 2);
        if (cooldownTimer < cooldown)
        {
            g2d.drawImage(mineSprite[0],0,0,null);//inert
        }
        else
        {
            g2d.drawImage(mineSprite[1],0,0,null);//active
        }
        g2d.setTransform(defaultTransform);

        if (Debug.showCollisionBounds)
        {
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public Shape getBounds()
    {
        return new Ellipse2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
    }
}