package gamedata.vehicles;

import engine.Debug;
import engine.GameDataHandler;
import engine.ObjectLayer;
import gamedata.GameObject;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CarSpawner extends GameObject
{
    private final double spawnTime;
    private double timer;
    private final double spawnDirection;

    public CarSpawner(double x, double y, double spawnTime, double spawnDirection)
    {
        super(x, y);
        this.spawnDirection = spawnDirection;
        this.spawnTime = spawnTime;
        solid = false;
    }




    @Override
    public void update(double dt)
    {
        timer += 1 * dt;
        // Check if the timer has elapsed
        if (timer >= spawnTime)
        {
            // Check if any objects are within the spawner's boundaries
            if (!checkCollision())
            {
                // Spawn a car if nothing is within the boundaries
                spawnCar();
            }
            timer = 0;
        }
    }



    public void spawnCar()
    {
        timer = 0;
        GameDataHandler.addObject(new PlayerCar(x, y, spawnDirection));
    }




    private boolean checkCollision()
    {
        for (GameObject object : GameDataHandler.getGameObjectList())
        {
            if (getBounds().intersects(object.getBounds().getBounds2D()))
            {
                if (object instanceof Vehicle)
                {
                    return true;
                }
            }
        }
        return false;
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        if (Debug.showCollisionBounds)
        {
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public Shape getBounds()
    {
        return new Ellipse2D.Double(x - 100, y - 100, 200, 200);
    }
}