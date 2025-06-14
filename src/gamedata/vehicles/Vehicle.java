package gamedata.vehicles;

import engine.Debug;
import engine.GameDataHandler;
import engine.ObjectLayer;
import gamedata.GameObject;
import gamedata.characters.CopCharacter;
import gamedata.weapons.Explosion;
import gamedata.weapons.Laser;
import gamedata.weapons.Mine;
import gamedata.weapons.Rocket;
import gamedata.world.Building;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public abstract class Vehicle extends GameObject
{
    /** Current acceleration */
    protected double acceleration = 0;
    /** Acceleration rate */
    protected final double ACCEL_DELTA = 400;
    /** Current speed */
    protected double speed = 0;
    /** Max speed */
    protected double maxSpeed = 1000;
    /** Current turn speed */
    protected double turnSpeed = 0;
    /** Turn rate */
    protected final double TURN_DELTA = 150;
    /** Vehicle Health */
    protected int health = 100;
    protected final int MAX_HEALTH = 100;
    protected final int MIN_HEALTH = 0;

    protected Image carImage;
    protected final int IMAGE_WIDTH = 96;
    protected final int IMAGE_HEIGHT = 48;

    protected boolean isDriving;
    protected boolean isRunning;
    private double despawnTimer;

    public Vehicle(double x, double y)
    {
        super(x, y);
        solid = true;
    }




    public void setRunning(boolean running)
    {
        isRunning = running;
    }




    /// getter for normalised speed - used for determining the camerazoom thresholds
    public double getNormalizedSpeed()
    {
        if (speed > 0)
        {
            return speed / maxSpeed;
        }
        else
        {
            return 0;
        }
    }




    public double getSpeed()
    {
        return speed;
    }




    ///  Getter and setter for car health
    public int getHealth()
    {
        return health;
    }




    public void setHealth(int health)
    {
        this.health = health;
    }




    public boolean isRunning()
    {
        return isRunning;
    }




    public void reduceHealth(int damage)
    {
        // Return if the vehicle is already destroyed
        if (health == MIN_HEALTH) return;
        // Reduce health
        health -= damage;
        // Clamp health to 0
        if (health < MIN_HEALTH) health = MIN_HEALTH;
        // Destroy vehicle
        if (health == MIN_HEALTH)
        {
            GameDataHandler.addObject(new Mine(x, y, 0));
        }
    }




    /**
     * Destroy car
     *
     * This is abstract because the different Vehicle implementations
     * need their own logic for selecting the destroyed sprite
     */
    public abstract void destroy();




    private final void collideWithWall()
    {
        // Basic Collision
        for (ObjectLayer layer : GameDataHandler.getLayers())
        {
            for (int j = 0; j < layer.getObjectList().size(); j++)
            {
                GameObject object = layer.getObjectList().get(j);

                if (object.getBounds() != null)
                {
                    if (getBounds().intersects(object.getBounds().getBounds2D())
                    && object != this && object.isSolid())
                    {
                        if (object instanceof CopCharacter cop)
                        {
                            if (speed > maxSpeed / 2)
                            {
                                cop.damage(100);
                                return;
                            }
                        }

                        // Take damage when crashing at a high speed
                        if (object instanceof Building || object instanceof Vehicle)
                        {
                            if (speed > maxSpeed - (maxSpeed * 0.2))
                            {
                                reduceHealth(25);
                                if (object instanceof Vehicle vehicle)
                                {
                                    vehicle.reduceHealth(25);
                                }
                            }
                        }

                        if (object instanceof Explosion)
                        {
                            reduceHealth(1);
                        }

                        if (object instanceof Rocket || object instanceof Laser && isRunning)
                        {
                            continue;
                        }

                        releaseS();
                        releaseW();

                        // These checks offset the car's position by 1.
                        // This ensures the car never ends up inside the colliding object.
                        if (y > object.getY())
                            y += 1;
                        if (y < object.getY())
                            y -= 1;
                        if (x > object.getX())
                            x += 1;
                        if (x < object.getX())
                            x -= 1;

                        speed = -speed / 4;
                    }
                }
            }
        }
    }




    /**
     * This update function serves to provide the common functionality for the car class.
     * Since this class gets extended for the player and enemies, a second method 'updateExtended' is provided for unique functionality.
     */
    @Override
    public final void update(double dt)
    {
        // Slow down and stop when neither W nor S is pressed
        if (!isDriving)
        {
            if (speed > 1)
            {
                acceleration = -ACCEL_DELTA / 2;
            }
            else if (speed <- 1)
            {
                acceleration = ACCEL_DELTA / 2;
            }
            else if (speed < 1 && speed > -1) // Stop the car when the speed is between 1 and -1
            {
                acceleration = 0;
                speed = 0;
            }
        }

        // Clamp speed positive
        if (speed >= maxSpeed) speed = maxSpeed;
        // Clamp speed negative
        if (speed <= -maxSpeed / 2) speed = -maxSpeed / 2;

        // Accelerate
        speed += acceleration * dt;

        // Turn
        if (speed > 1)
        {
            direction += turnSpeed * dt;
        }
        else if (speed < -1)
        {
            direction -= turnSpeed * dt;
        }

        // Move towards direction
        x += Math.cos(Math.toRadians(direction)) * speed * dt;
        y += Math.sin(Math.toRadians(direction)) * speed * dt;

        collideWithWall();

        if (x <= GameDataHandler.BOUNDS_WEST + IMAGE_WIDTH / 2)
        {
            speed = -speed / 4;
            x += 1;
        }
        if (x >= GameDataHandler.BOUNDS_EAST - IMAGE_WIDTH / 2)
        {
            speed = -speed / 4;
            x -= 1;
        }
        if (y <= GameDataHandler.BOUNDS_NORTH + IMAGE_WIDTH / 2)
        {
            speed = -speed / 4;
            y += 1;
        }
        if (y >= GameDataHandler.BOUNDS_SOUTH - IMAGE_WIDTH / 2)
        {
            speed = -speed / 4;
            y -= 1;
        }

        // Despawn when destroyed
        if (health == MIN_HEALTH)
        {
            despawnTimer += 1 * dt;
            if (despawnTimer >= GameDataHandler.DESPAWN_TIME)
            {
                GameDataHandler.removeObject(this);
            }
        }

        updateExtended(dt);
    }




    /** This method allows us to provide unique functionality for child cars */
    protected abstract void updateExtended(double dt);




    private double acceleration()
    {
        return ACCEL_DELTA * (1 - speed / maxSpeed);
    }




    /** Accelerate forward. Brake when in reverse */
    public final void pressW()
    {
        if (!isRunning)
        {
            return;
        }

        isDriving = true;

        // Exponential Acceleration if moving forwards
        if (speed >= 0)
        {
            acceleration = acceleration();
        }
        //slow down if reversing
        else
        {
            acceleration = ACCEL_DELTA * 2;
        }
    }




    /** Brake. Reverse */
    public final void pressS()
    {
        if (!isRunning)
        {
            return;
        }

        isDriving = true;

        // Exponential acceleration whilst reversing
        if (speed <= 0)
        {
            acceleration = -acceleration();
        }
        // Slow down if driving forwards/braking
        else
        {
            acceleration = -ACCEL_DELTA * 2;
        }
    }




    /** Turn left */
    public final void pressA()
    {
        if (!isRunning)
        {
            return;
        }
        turnSpeed = -TURN_DELTA;
    }




    /** Turn right */
    public final void pressD()
    {
        if (!isRunning)
        {
            return;
        }
        turnSpeed = TURN_DELTA;
    }




    /** Set driving to false when W is released */
    public final void releaseW()
    {
        isDriving = false;
    }

    /** Set driving to false when S is released */
    public final void releaseS()
    {
        isDriving = false;
    }

    /** Stop left turn */
    public final void releaseA()
    {
        turnSpeed = 0;
    }

    /** Stop right turn */
    public final void releaseD()
    {
        turnSpeed = 0;
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        AffineTransform defaultTransform = g2d.getTransform();

        g2d.rotate(Math.toRadians(direction), x, y);
        g2d.drawImage(carImage, (int) x - IMAGE_WIDTH / 2, (int) y - IMAGE_HEIGHT / 2, null);
        g2d.setTransform(defaultTransform);

        // TODO DEBUG
        if (Debug.showCollisionBounds)
        {
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public Shape getBounds()
    {
        int Width = (int) (IMAGE_WIDTH / 2.25);
        int Height = (int) (IMAGE_HEIGHT / 2.25);

        // Define the four corners of the unrotated rectangle
        Path2D.Float rect = new Path2D.Float();
        rect.moveTo(-Width, -Height);  // top-left
        rect.lineTo(Width, -Height);   // top-right
        rect.lineTo(Width, Height);    // bottom-right
        rect.lineTo(-Width, Height);   // bottom-left
        rect.closePath();

        //Create a transform to rotate and translate
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y); // move to player position
        transform.rotate(Math.toRadians(direction)); // rotate by your player's rotation

        //Apply the transform
        return transform.createTransformedShape(rect);
    }
}