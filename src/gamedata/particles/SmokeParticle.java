package gamedata.particles;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class SmokeParticle extends Particle
{
    private float angle;
    private float rotationSpeed;
    private Image image = null;

    SmokeParticle(Image i,double x, double y)
    {
        super(x,y);
        init((float) x, (float) y,0);
        image = i;
    }

    @Override
    public void init(float startX, float startY, float emitterAngle)
    {
        super.init(startX, startY, emitterAngle);
        float initialSpeed = getInitialSpeed();
        velY = initialSpeed * (float)Math.cos(emitterAngle);
        velX = initialSpeed * (float)Math.sin(emitterAngle);
        rotationSpeed= (float) Math.random() * .05f;
        angle = (float) Math.random() * 360f;
        scale = (float) Math.random() * 0.07f;
        alpha = (float) Math.random() * 0.1f + 0.9f;
        time = 0;
    }


    @Override
    public void update(double dt)
    {
        time += dt;
        x += velX * dt;
        y += velY * dt;

        angle = (float) (time + 30 * rotationSpeed * (float) Math.exp(-time * 2f));
        scale = 0.07f * (float) Math.log(time * 100f);
        alpha = 0.7f * (float) Math.exp(-time * 2.5f);
    }

    @Override
    public void paintComponent(Graphics2D g2d)
    {
        if (notVisible())
        {
            return;
        }
        AffineTransform og = g2d.getTransform();
        g2d.translate(x,y-20);
        g2d.rotate(angle);
        g2d.scale(scale*.5,scale*.5);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
        g2d.drawImage(image,null,null);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setTransform(og);
    }

    @Override
    public Shape getBounds()
    {
        return new Rectangle(0,0);
    }
}