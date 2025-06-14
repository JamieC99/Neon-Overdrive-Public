package gamedata.particles;

import engine.GameDataHandler;

import java.awt.*;

public class SparkEmitter extends ParticleEmitter
{
    private double lifespan = .5;//to make them temporary
    private double timer;

    public SparkEmitter(double x, double y)
    {
        this.x = x;
        this.y = y;
    }




    @Override
    public Particle newParticle()
    {
        return new SparkParticle(super.x, super.y);
    }




    @Override
    public void update(double dt)
    {
        timer += dt;
        if (timer >= lifespan)
        {
            particles.clear();
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