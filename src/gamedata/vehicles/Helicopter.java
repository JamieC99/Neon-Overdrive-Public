package gamedata.vehicles;

import engine.Game;
import engine.GameDataHandler;
import gamedata.GameObject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Helicopter extends GameObject
{
    private double xDistToScreen;
    private double yDistToScreen;
    private double topX;
    private double topY;
    private int roofHeight = 1;
    private double bladesRotation = 0;

    public Helicopter(double x, double y)
    {
        super(x, y);
        solid = false;
    }




    @Override
    public void update(double dt)
    {
        xDistToScreen = -GameDataHandler.getCameraX() + Game.WINDOW_WIDTH / 2 - x;
        yDistToScreen = -GameDataHandler.getCameraY() + Game.WINDOW_HEIGHT / 2 - y;

        topX = x - (xDistToScreen / roofHeight);
        topY = y - (yDistToScreen / roofHeight);

        direction = 0;

        bladesRotation += 1000 * dt;
        if (bladesRotation > 359) bladesRotation = 0;

        double speed = 100;
        x += Math.cos(Math.toRadians(direction)) * speed * dt;
        y += Math.sin(Math.toRadians(direction)) * speed * dt;

        if (x > GameDataHandler.MAP_WIDTH + 1000)
        {
            x = -1000;
        }
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        AffineTransform defaultTransform = g2d.getTransform();

        g2d.rotate(Math.toRadians(direction), topX, topY);
        g2d.drawImage(new ImageIcon("res/helicopter_shadow.png").getImage(), (int) x - 175, (int) y - 43, null);
        g2d.drawImage(new ImageIcon("res/helicopter.png").getImage(), (int) topX - 350, (int) topY - 87, null);

        g2d.rotate(Math.toRadians(bladesRotation), topX, topY);
        g2d.drawImage(new ImageIcon("res/helicopter_blades.png").getImage(), (int) topX - 256, (int) topY - 256, null);

        g2d.setTransform(defaultTransform);
    }

    @Override
    public Shape getBounds()
    {
        return new Rectangle();
    }
}