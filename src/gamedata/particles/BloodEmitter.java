package gamedata.particles;

import engine.GameDataHandler;

import java.awt.*;

public class BloodEmitter extends ParticleEmitter
{
    private final double lifespan = .5;//to make them temporary
    private double timer;

    public BloodEmitter(double x, double y)
    {
        super.x = x;
        super.y = y;
    }


    @Override
    public Particle newParticle()
    {
        return new BloodParticle(super.x, super.y);
    }

    @Override
    public Shape getBounds()
    {
        return new Rectangle(0,0);
    }

    @Override
    public void update(double dt)
    {
        timer += dt;
        if (timer >= lifespan)
        {
            GameDataHandler.removeObject(this);
        }
        super.update(dt);
    }

    @Override
    public void paintComponent(Graphics2D g2d)
    {
        super.paintComponent(g2d);
    }
}