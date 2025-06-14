package gamedata.particles;

import gamedata.*;
import java.awt.*;

public abstract class Particle extends GameObject
{
    protected float velX;
    protected float velY;
    protected float scale;
    protected float alpha;
    protected float time;
    protected int R, G, B;
    protected final float maxTime = 2; //2 seconds

    public Particle(double x, double y)
    {
        super(x, y);
    }

    protected float getInitialSpeed()
    {
        return 2.0f * ((float) Math.random() * 80.0f + 150f);
    }

    public void init(float startX, float startY, float emitterRange)
    {
        this.x = startX;
        this.y = startY;
        float initialSpeed = getInitialSpeed();
        velX = initialSpeed * (float) Math.cos(emitterRange);
        velY = initialSpeed * (float) Math.sin(emitterRange);
        scale = (float) Math.random() * 0.1f + 0.2f;
        alpha = (float) Math.random() * 0.1f + 0.9f;
        time = 0;
    }



    protected boolean notVisible()
    {
        return (alpha <= 0.02f || time > maxTime);
    }


    public abstract void update(double dt);
    public abstract void paintComponent(Graphics2D g2d);

}