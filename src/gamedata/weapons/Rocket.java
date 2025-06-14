package gamedata.weapons;

import engine.Debug;
import engine.GameDataHandler;
import engine.ObjectLayer;
import gamedata.GameObject;
import gamedata.characters.CopCharacter;
import gamedata.characters.PlayerCharacter;
import gamedata.particles.SmokeEmitter;
import gamedata.vehicles.PlayerCar;
import gamedata.vehicles.Vehicle;
import gamedata.world.Building;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Rocket extends Projectile
{
    private final Image image = new ImageIcon("res/rocket.png").getImage();
    private final SmokeEmitter smoke;

    public Rocket(double x, double y, double speed, double direction)
    {
        super(x, y, speed, direction);
        damage = 100;
        solid = true;

        smoke = new SmokeEmitter(x, y, direction);
        GameDataHandler.addObject(smoke);
    }




    public SmokeEmitter getSmoke()
    {
        return smoke;
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        AffineTransform transform = g2d.getTransform();
        g2d.rotate(Math.toRadians(direction), x, y);
        g2d.drawImage(image, (int) x - 256, (int) y - 8, null);
        g2d.setTransform(transform);

        if (Debug.showCollisionBounds)
        {
            g2d.drawString("Projectile", (int) x, (int) y);
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public void update(double dt)
    {
        super.update(dt);
        smoke.move((float) x, (float) y);
        if (x < 0 || y < 0 || x > GameDataHandler.MAP_WIDTH || y > GameDataHandler.MAP_HEIGHT)
        {
            GameDataHandler.removeObject(smoke);
        }
    }




    @Override
    protected void collide()
    {
        for (ObjectLayer layer : GameDataHandler.getLayers())
        {
            for (int j = 0; j < layer.getObjectList().size(); j++)
            {
                GameObject object = layer.getObjectList().get(j);

                if (getBounds().intersects(object.getBounds().getBounds2D()))
                {
                    if (object instanceof PlayerCharacter
                     || object instanceof Rocket
                     || object instanceof Explosion)
                    {
                        continue;
                    }

                    if (object instanceof Vehicle vehicle)
                    {
                        if (object instanceof PlayerCar car)
                        {
                            if (car.isRunning())
                            {
                                return;
                            }
                        }

                        if (vehicle.getHealth() > 0)
                        {
                            GameDataHandler.addObject(new Mine(x, y, 0));
                        }
                        else
                        {
                            GameDataHandler.addObject(new Explosion(x, y));
                        }
                        smoke.disable();
                        GameDataHandler.removeObject(this);
                    }

                    if (object instanceof CopCharacter cop)
                    {
                        if (cop.getHealth() > 0)
                        {
                            GameDataHandler.addObject(new Explosion(x, y));
                            GameDataHandler.removeObject(this);
                        }
                    }

                    if (object instanceof Building)
                    {
                        GameDataHandler.addObject(new Explosion(x, y));
                        smoke.disable();
                        GameDataHandler.removeObject(this);
                    }
                }
            }
        }
    }




    @Override
    public Shape getBounds()
    {
        return new Rectangle((int) x - 6, (int) y - 6, 12, 12);
    }
}