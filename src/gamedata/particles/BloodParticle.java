package gamedata.particles;

import java.awt.*;

public class BloodParticle extends Particle
{
    public BloodParticle(double x, double y)
    {
        super(x, y);
        R = 255;
        G = 1;
        B = 1;
    }


    @Override
    public void update(double dt)
    {
        time += dt;
        x += velX * dt;
        y += velY * dt;
        alpha = (float) Math.clamp(Math.exp(-time * 5.5f) * 255, 0, 255);
        velY += 400f * dt;
    }


    @Override
    public void paintComponent(Graphics2D g2d)
    {
        if (this.notVisible())
        {
            return;
        }
        g2d.setPaint(new Color(R, G, B, (int) alpha)); //this wouldn't work unless cast to int
        g2d.drawRect((int) x, (int) y, 2, 2);
        g2d.fillRect((int) x, (int) y, 2, 2);

    }


    @Override
    public Shape getBounds()
    {
        return new Rectangle(0, 0);
    }
}