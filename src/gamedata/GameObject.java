package gamedata;

import java.awt.*;

/**
 * This is an abstract class used to build the game objects
 */
public abstract class GameObject
{
    protected double x;
    protected double y;
    protected double direction = 0; // 0 = right
    protected boolean solid;
    protected boolean active;

    public GameObject(double x, double y)
    {
        this.x = x;
        this.y = y;
        active = true;
    }

    /** Used by the engine to update the object. No to be changed. */
    public final void engineUpdate(double dt)
    {
        if (active)
        {
            update(dt);
        }
    }

    public abstract void update(double dt);
    public abstract void paintComponent(Graphics2D g2d);


    public void setActive(boolean active) {this.active = active;}
    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public boolean isSolid() { return solid; }

    public abstract Shape getBounds();
}