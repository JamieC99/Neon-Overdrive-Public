package gamedata.weapons;

import engine.GameDataHandler;
import engine.ObjectLayer;
import gamedata.characters.CopCharacter;
import gamedata.characters.PlayerCharacter;
import gamedata.vehicles.Vehicle;
import gamedata.GameObject;
import gamedata.particles.BloodEmitter;
import gamedata.particles.SparkEmitter;
import gamedata.world.Building;

import java.awt.*;
import java.util.Random;

public abstract class Projectile extends GameObject
{
    protected double speed;
    protected int damage;

    /// creates a projectile that fires from the given x and y pos in the given direction
    /// @param x x of the object shooting the projectille
    /// @param y y of the object shooting the projectile
    /// @param playerSpeed the speed in which the shooting object is moving
    /// @param direction the player's direction
    public Projectile(double x, double y, double speed, double direction)
    {
        super(x, y);

        this.speed = speed;
        Random rand = new Random();
        int acc = rand.nextInt(-5, 5);
        this.direction = direction + acc;
    }




    protected abstract void collide();




    @Override
    public void update(double dt)
    {
        collide();
        x += Math.cos(Math.toRadians(direction)) * speed * dt;
        y += Math.sin(Math.toRadians(direction)) * speed * dt;
        // Despawn when outside of the map
        if (x < 0 || y < 0 || x > GameDataHandler.MAP_WIDTH || y > GameDataHandler.MAP_HEIGHT)
        {
            GameDataHandler.removeObject(this);
        }
    }




    @Override
    public abstract void paintComponent(Graphics2D g2d);




    @Override
    public abstract Shape getBounds();
}