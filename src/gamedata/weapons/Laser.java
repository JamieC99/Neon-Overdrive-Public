package gamedata.weapons;

import engine.Debug;
import engine.GameDataHandler;
import engine.ObjectLayer;
import gamedata.GameObject;
import gamedata.characters.CopCharacter;
import gamedata.characters.PlayerCharacter;
import gamedata.vehicles.PlayerCar;
import gamedata.vehicles.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class Laser extends GameObject
{
    private final Image image = new ImageIcon("res/laser.png").getImage();
    private final int damage = 100;

    public Laser(double x, double y)
    {
        super(x, y);
        solid = true;
    }




    protected void collide()
    {
        for (int i = 0; i < GameDataHandler.getLayers().size() ; i++)
        {
            ObjectLayer layer = GameDataHandler.getLayers().get(i);

            for (int j = 0; j < layer.getObjectList().size(); j++)
            {
                GameObject object = layer.getObjectList().get(j);

                if (getBounds().intersects(object.getBounds().getBounds2D()))
                {
                    if (object.equals(this)
                    || object instanceof PlayerCharacter)
                    {
                        return;
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
                        vehicle.reduceHealth(damage);
                    }

                    if (object instanceof CopCharacter cop)
                    {
                        if (cop.getHealth() > 0)
                        {
                            GameDataHandler.addObject(new Explosion(object.getX(), object.getY()));
                        }
                    }

                    if (object instanceof Mine)
                    {
                        GameDataHandler.addObject(new Explosion(object.getX(), object.getY()));
                        GameDataHandler.removeObject(object);
                    }
                }
            }
        }
    }




    @Override
    public void update(double dt)
    {
        collide();

        int offset;
        if (GameDataHandler.getPlayerCharacter().isOnFoot())
        {
            offset = 96;
        }
        else
        {
            offset = 96;
        }

        x = GameDataHandler.getPlayerCharacter().getX() +
                Math.cos(Math.toRadians(GameDataHandler.getPlayerCharacter().getDirection())) * offset;
        y = GameDataHandler.getPlayerCharacter().getY() +
                Math.sin(Math.toRadians(GameDataHandler.getPlayerCharacter().getDirection())) * offset;
        direction = GameDataHandler.getPlayerCharacter().getDirection();
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        AffineTransform defaultTransform = g2d.getTransform();
        g2d.rotate(Math.toRadians(direction), x, y);
        g2d.drawImage(image, (int) x, (int) y - 8, 1024, 16, null);
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
        Path2D.Float rect = new Path2D.Float();
        rect.moveTo(0, -8);  // top-left
        rect.lineTo(1024, -8);   // top-right
        rect.lineTo(1024, 8);    // bottom-right
        rect.lineTo(0, 8);   // bottom-left
        rect.closePath();

        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(Math.toRadians(direction));

        return transform.createTransformedShape(rect);
    }
}