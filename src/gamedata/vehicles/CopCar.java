package gamedata.vehicles;

import engine.*;
import gamedata.GameObject;
import gamedata.characters.CopCharacter;
import gamedata.weapons.Laser;
import gamedata.weapons.Mine;
import gamedata.weapons.Projectile;
import gamedata.world.Building;
import mathlib.GameMath;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class CopCar extends Vehicle
{
    private BufferedImage carBufferedImage;
    private double animationTimer;
    private SFX siren = new SFX("res/police_siren.wav");
    private final double SIREN_RANGE = 4344.66;

    // AI Behaviour
    private int followRange = 4000;
    private int innerFollowRange = 400;

    // Ray casting
    private Ray rayLeft;
    private Ray rayLeftCenter;
    private Ray rayCenter;
    private Ray rayRightCenter;
    private Ray rayRight;

    private double mineTimer;
    private final double MINE_SPAWN_INTERVAL = 15;

    /** Is this car occupied? */
    private boolean occupied;
    /** The cop inside the car */
    private CopCharacter occupant;

    public CopCar(double x, double y, double dir)
    {
        super(x, y);
        direction = dir;
        try
        {
            File image = new File("res/police_car.png");
            carBufferedImage = ImageIO.read(image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Random rand = new Random();
        maxSpeed = maxSpeed + rand.nextDouble(-100, 300);

        isRunning = true;
        siren.loop(-50);
        this.mineTimer = 0;

        occupant = (CopCharacter) GameDataHandler.addObject(new CopCharacter(x, y, this));
        occupied = true;

        rayLeft = new Ray(-45, 1000);
        rayCenter = new Ray(0, 1000);
        rayRight = new Ray(45, 1000);
    }




    public SFX getSiren()
    {
        return siren;
    }




    @Override
    protected void updateExtended(double dt)
    {
        if (!active)
        {
            return;
        }

        /// create timer to drop a mine at interval
        mineTimer += dt;
        /// when minetimer => mineinterval spawn a mine and the cop car is moving (at 400)
        if (speed > 400 && mineTimer >= MINE_SPAWN_INTERVAL)
        {
            GameDataHandler.addObject(new Mine(x, y, 2));
            mineTimer = 0;
        }

        // Basic animation. This gives us flashing lights
        if (health > MIN_HEALTH)
        {
            animationTimer += 4 * dt;
            if (animationTimer >= 0.5)
            {
                carImage = carBufferedImage.getSubimage(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
            }
            else
            {
                carImage = carBufferedImage.getSubimage(IMAGE_WIDTH, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
            }
            if (animationTimer >= 1)
            {
                animationTimer = 0;
            }
        }

        if (isRunning)
        {
            //set proximity volume for siren based on position to copCar to player
            siren.proximityVolume(x, y, SIREN_RANGE);

            // Get the distance to the player
            double playerX = GameDataHandler.getPlayerCharacter().getX();
            double playerY = GameDataHandler.getPlayerCharacter().getY();
            double distanceToPlayer = GameMath.distanceBetweenPoints(x, y, playerX, playerY);

            double dirToPlayer = Math.toRadians(GameMath.lookTowards(x, y, playerX, playerY));

            // Get the relative direction of the player
            double directionRadians = Math.toRadians(direction);
            double relativeDiretion = directionRadians - dirToPlayer;
            relativeDiretion = Math.atan2(Math.sin(relativeDiretion), Math.cos(relativeDiretion));
            relativeDiretion = Math.toDegrees(relativeDiretion);

            rayLeft.update(dt);
            rayCenter.update(dt);
            rayRight.update(dt);

            // Accelerate
            if (distanceToPlayer >= innerFollowRange)
            {
                pressW();
            }
            else if (distanceToPlayer < innerFollowRange)
            {
                if (distanceToPlayer > innerFollowRange / 2)
                {
                    if (speed < 50 && speed > 0)
                    {
                        pressS();
                    }
                    else if (speed < 0 && speed > -50)
                    {
                        pressW();
                    }
                    else
                    {
                        releaseW();
                        releaseS();
                    }
                }
                else
                {
                    releaseW();
                    releaseS();
                }
            }


            if (rayLeft.getTargetObject() != null && rayRight.getTargetObject() != null
            && rayCenter.distanceToTarget() < 512)
            {
                releaseA();
                pressD();
            }


            // Check if the cop is in range of the player
            if (distanceToPlayer < followRange && distanceToPlayer > innerFollowRange / 2 && getHealth() > 0)
            {
                if (relativeDiretion > 0)
                {
                    if (rayLeft.getTargetObject() != null)
                    {
                        releaseA();
                        pressD();
                    }
                    else
                    {
                        pressA();
                    }
                }
                // If the player is on the right
                else if (relativeDiretion < 0)
                {
                    if (rayRight.getTargetObject() != null)
                    {
                        releaseD();
                        pressA();
                    }
                    else
                    {
                        pressD();
                    }
                }

            }
            else
            {
                releaseA();
                releaseD();
            }


            // Disembark the occupant
            if (speed < 100)
            {
                if (distanceToPlayer < 300)
                {
                    if (GameDataHandler.getPlayerCharacter().isOnFoot())
                    {
                        disembark();
                    }
                }
            }
        }
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        super.paintComponent(g2d);
        if (Debug.showCollisionBounds && health > MIN_HEALTH)
        {
            rayLeft.paint(g2d);
            rayCenter.paint(g2d);
            rayRight.paint(g2d);
        }
    }




    public void embark(CopCharacter cop)
    {
        if (health == MIN_HEALTH && occupied) return;
        occupant = cop;
        isRunning = true;
        siren.loop(-50);
    }




    public void disembark()
    {
        releaseW();
        releaseS();
        releaseA();
        releaseD();
        occupant.exitVehicle();
        occupant = null;
        isRunning = false;
        siren.stop();
    }




    @Override
    public void destroy()
    {
        health = 0;
        isRunning = false;
        releaseW();
        releaseS();
        releaseA();
        releaseD();
        carImage = carBufferedImage.getSubimage(192, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        siren.close();
        GameStats.increaseCarsDestroyed();
        GameStats.increaseChaos(GameStats.CAR_DESTROY_CHAOS);
        if (occupied && occupant != null)
        {
            occupant.damage(100);
        }
    }




    private class Ray
    {
        private double rayX, rayY;
        private double xOrigin, yOrigin;
        private double rayDirection;
        private double rayDistance;
        private double rayAngle;
        private int turnDir;
        private double rayMaxDist;
        private double raySpeed = 10000;
        private double angleOffset;
        private GameObject targetObject;
        private double distanceToTarget;

        public Ray(double offset, double rayMaxDist)
        {
            this.rayMaxDist = rayMaxDist;
            rayX = x;
            rayY = y;
            xOrigin = x;
            yOrigin = y;
            angleOffset = offset;
            rayDirection = direction + angleOffset;
        }


        private void reset()
        {
            rayDirection = direction + angleOffset;
            rayX = x;
            rayY = y;
            xOrigin = rayX;
            yOrigin = rayY;
        }


        private void checkForHit()
        {
            targetObject = null;
            distanceToTarget = 0;
            for (ObjectLayer layer : GameDataHandler.getLayers())
            {
                for (int j = 0; j < layer.getObjectList().size(); j++)
                {
                    GameObject object = layer.getObjectList().get(j);
                    if (getBounds().intersects(object.getBounds().getBounds2D()))
                    {
                        if (object instanceof Building || object instanceof Vehicle || object instanceof Mine
                        || object instanceof Projectile || object instanceof Laser)
                        {
                            targetObject = object;
                            distanceToTarget = GameMath.distanceBetweenPoints(x, y, targetObject.getX(), targetObject.getY());
                            reset();
                        }
                    }
                }
            }
        }


        public GameObject getTargetObject()
        {
            return targetObject;
        }


        public double distanceToTarget()
        {
            return distanceToTarget;
        }


        public void update(double dt)
        {
            rayX += Math.cos(Math.toRadians(rayDirection)) * raySpeed * dt;
            rayY += Math.sin(Math.toRadians(rayDirection)) * raySpeed * dt;
            rayDistance = GameMath.distanceBetweenPoints((int) xOrigin,(int) yOrigin, (int) rayX, (int) rayY);

            checkForHit();

            if (rayDistance > rayMaxDist)
            {
                reset();
            }
        }


        public void paint(Graphics2D g2d)
        {
            g2d.setColor(Color.BLUE);
            g2d.drawLine((int) xOrigin, (int) yOrigin, (int) rayX, (int) rayY);
        }


        private Shape getBounds()
        {
            return new Rectangle((int) rayX - 2, (int) rayY - 2, 4, 4);
        }
    }
}