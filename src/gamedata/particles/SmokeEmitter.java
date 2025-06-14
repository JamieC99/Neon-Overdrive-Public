package gamedata.particles;

import engine.GameDataHandler;

import javax.swing.*;
import java.awt.*;

public class SmokeEmitter extends ParticleEmitter
{
    private Image image[] = new Image[4];
    private boolean disabled = false;
    private double dir;



    public SmokeEmitter(double x, double y, double dir)
    {
        super(x,y);
        image[0] = new ImageIcon("res/smoke/smoke1.png").getImage();
        image[1] = new ImageIcon("res/smoke/smoke2.png").getImage();
        image[2] = new ImageIcon("res/smoke/smoke3.png").getImage();
        image[3] = new ImageIcon("res/smoke/smoke4.png").getImage();
        this.startX = (float) x;
        this.startY = (float) y;
        this.dir = dir;
    }




    public void disable()
    {
        disabled = true;
        Timer deleteTimer = new Timer(5000, e ->
        {
            particles.clear();
            GameDataHandler.removeObject(this);
        });
        deleteTimer.start();
    }




    @Override
    public void update(double dt)
    {
        time += dt;

        emitterAngle = dir +
                (float)Math.sin(time * sweepSpeed) *
                sweepAngle * (float)Math.PI + (float)Math.PI +
                (float)Math.random() * sweepRandom - sweepRandom/2.0f;


        if (time > lastEmitTime + 1.0 / emitterFrequency && !disabled)
        {
            Particle p = getParticle();
            if (p != null)
            {
                p.init((float) x, (float) y, (float) emitterAngle);
            }
            lastEmitTime = time;
        }

        for(Particle p : particles)
        {
            p.update(dt);
        }
    }




    @Override
    public SmokeParticle newParticle()
    {
        return new SmokeParticle(image[(int)(Math.random()*4)], x, y);
    }
}