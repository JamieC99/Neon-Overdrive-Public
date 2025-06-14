package gamedata.weapons;

import engine.Debug;
import engine.GameDataHandler;
import engine.ObjectLayer;
import engine.SFX;
import gamedata.GameObject;
import gamedata.characters.CopCharacter;
import gamedata.characters.PlayerCharacter;
import gamedata.particles.BloodEmitter;
import gamedata.particles.SparkEmitter;
import gamedata.vehicles.PlayerCar;
import gamedata.vehicles.Vehicle;
import gamedata.world.Building;

import java.awt.*;

public class Bullet extends Projectile
{
    private final String TAG;
    public Bullet(String tag, double x, double y, double speed, double direction)
    {
        super(x, y, speed, direction);
        TAG = tag;
        damage = 5;
    }



    public String getTAG()
    {
        return TAG;
    }




    @Override
    protected void collide()
    {
        for (int i = 0; i < GameDataHandler.getLayers().size() ; i++)
        {
            ObjectLayer layer = GameDataHandler.getLayers().get(i);

            for (int j = 0; j < layer.getObjectList().size(); j++)
            {
                GameObject object = layer.getObjectList().get(j);

                if (getBounds().intersects(object.getBounds().getBounds2D()) && !(object instanceof Projectile)) // except  these objects
                {
                    if (object instanceof Vehicle vehicle)
                    {
                        if (vehicle instanceof PlayerCar car)
                        {
                            if (car.isRunning() && TAG == "player")
                            {
                                continue;
                            }
                        }
                        GameDataHandler.addObject(new SparkEmitter(x, y));
                        // Damage all other vehicles
                        vehicle.reduceHealth(damage);
                        WeaponSounds.BULLET_IMPACT_SURFACE.play(-30);
                        GameDataHandler.removeObject(this);
                    }

                    if (object instanceof Building)
                    {
                        // remove the bullet itself
                        GameDataHandler.addObject(new SparkEmitter(x, y));
                        WeaponSounds.BULLET_IMPACT_SURFACE.play(-30);
                        GameDataHandler.removeObject(this);
                    }

                    if (object instanceof PlayerCharacter player && !player.isOnFoot())
                    {
                        GameDataHandler.addObject(new BloodEmitter(x, y));
                        WeaponSounds.BULLET_IMPACT_NPC.play(-30);
                        GameDataHandler.removeObject(this);
                    }

                    if (object instanceof CopCharacter cop)
                    {
                        if (cop.getHealth() > 0)
                        {
                            GameDataHandler.addObject(new BloodEmitter(x, y));
                            WeaponSounds.BULLET_IMPACT_NPC.play(-30);
                            GameDataHandler.removeObject(this);
                        }
                    }
                }
            }
        }
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        g2d.setColor(Color.RED);
        g2d.fillOval((int) x - 2, (int) y - 2,4,4);

        if (Debug.showCollisionBounds)
        {
            g2d.drawString("Projectile", (int) x, (int) y);
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public Shape getBounds()
    {
        return new Rectangle((int) x - 2, (int) y - 2,4,4);
    }
}