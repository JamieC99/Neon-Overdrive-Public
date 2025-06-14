package gamedata.particles;

import gamedata.GameObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ParticleEmitter extends GameObject
{
    protected  int nParticles = 5;
    protected double emitterFrequency = 100;
    protected double sweepAngle = 360;
    protected double sweepSpeed = 100;
    protected double sweepRandom;
    protected List<Particle> particles;
    protected double emitterAngle = 0;
    protected double startX, startY;
    protected double angle = 0f;
    protected double time = 0;
    protected double lastEmitTime = 0;

    protected ParticleEmitter(int nparticles, int emitterfrequency, float sweepangle, float sweepspeed, float sweeprandom)
    {
        super(-10000,-1000);
        this.x = -10000;
        this.y = -10000;
        this.nParticles = nparticles;
        this.emitterFrequency = emitterfrequency;
        this.sweepAngle = sweepangle;
        this.sweepSpeed = sweepspeed;
        this.sweepRandom = sweeprandom;
        this.particles=new ArrayList<>(nparticles);
    }

    protected ParticleEmitter()
    {
        super(-10000, -1000);

        particles = new ArrayList<>(nParticles);
    }




    private Particle add(Particle p)
    {
        particles.add(p);
        return p;
    }




    protected ParticleEmitter(double x, double y)
    {
        this(120, 100,.04f,40,.1f);
    }




    public abstract Particle newParticle();




    public void move(float x, float y)
    {
        startX = x;
        startY = y;
        this.x = x;
        this.y = y;
    }




    // gets a particle that is no longer visible
    // or makes a new one if the pool is not full
    public Particle getParticle()
    {
        if (particles.size() < nParticles)
        {
            return add(newParticle());
        }
        for (Particle p : particles)
        {
            if (p.notVisible())
            {
                return p;
            }
        }
        return null; // if pool is full and all particles are visible
    }




    @Override
    public void update(double dt)
    {
        time += dt;
        emitterAngle = angle +
                (float)Math.sin(time*sweepSpeed) *
                sweepAngle * (float)Math.PI + (float)Math.PI +
                (float)Math.random() * sweepRandom - sweepRandom/2.0f;
        if (time > lastEmitTime + 1.0 / emitterFrequency)
        {
            Particle p = getParticle();
            if(p != null) {
                p.init((float) x, (float) y, (float) emitterAngle);
            }
            lastEmitTime=time;
        }
        for(Particle p:particles)
        {
            p.update(dt);
        }
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        for (Particle p:particles)
        {
            if (!p.notVisible())
            {
                p.paintComponent(g2d);
            }
        }
    }




    @Override
    public Shape getBounds()
    {
        return new Rectangle(0,0);
    }
}