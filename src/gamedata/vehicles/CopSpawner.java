package gamedata.vehicles;

import engine.Debug;
import engine.GameDataHandler;
import engine.GameStats;
import gamedata.GameObject;
import mathlib.GameMath;

import java.awt.*;

public class CopSpawner extends GameObject
{
    private double timer;
    private double timerTarget;
    private double initialTarget;
    private double spawnDirection;
    /** The player must be beyond this distance for a cop to spawn */
    private final double SPAWN_BOUND = 1536;

    public CopSpawner(double x, double y, double initialTarget, double dir)
    {
        super(x, y);
        spawnDirection = dir;
        this.initialTarget = initialTarget;
        solid = false;
    }

    @Override
    public void update(double dt)
    {
        timer += 1 * dt;
        timerTarget = initialTarget - GameStats.getChaos() / 100.0;
        if (timerTarget < initialTarget / 10)
        {
            timerTarget = initialTarget / 10;
        }
        if (timer >= timerTarget)
        {
            if (GameMath.distanceBetweenPoints(x, y,
            GameDataHandler.getPlayerCharacter().getX(),
            GameDataHandler.getPlayerCharacter().getY()) > SPAWN_BOUND
            && GameStats.getCopCount() < GameStats.MAX_COP_COUNT
            && GameStats.getChaos() > 0)
            {
                GameDataHandler.addObject(new CopCar(x, y, spawnDirection));
                GameStats.addCop();
            }
            timer = 0;
        }
    }

    @Override
    public void paintComponent(Graphics2D g2d)
    {
        if (Debug.showCollisionBounds)
        {
            g2d.setColor(Color.BLUE);
            g2d.draw(getBounds());
        }
    }

    @Override
    public Shape getBounds()
    {
        return new Rectangle((int) x - 50, (int) y - 50, 100, 100);
    }
}