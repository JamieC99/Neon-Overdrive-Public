package gamedata.characters;

import engine.*;
import gamedata.GameObject;
import gamedata.vehicles.CopCar;
import gamedata.vehicles.Vehicle;
import gamedata.weapons.Bullet;
import gamedata.weapons.Explosion;
import gamedata.weapons.Laser;
import gamedata.weapons.Rocket;
import gamedata.particles.BloodEmitter;
import gamedata.world.Building;
import mathlib.GameMath;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class CopCharacter extends Character
{
    private int health = 100;
    private final int MIN_HEALTH = 0;
    private double shotTimer;
    private double shotTimerTarget;
    private int fireRange = 750;
    private Random rand = new Random();
    private boolean enteringVehicle;
    private CopCar currentVehicle;

    public CopCharacter(double x, double y, CopCar car)
    {
        super(x, y, "res/cop_character.png");
        shootSFX = new SFX("res/weapon_sounds/gun_cop.wav");
        onFoot = false;
        currentVehicle = car;
        solid = true;
    }




    public boolean isOnFoot()
    {
        return onFoot;
    }




    public void damage(int d)
    {
        health -= d;
        if (health <= MIN_HEALTH)
        {
            health = MIN_HEALTH;
            characterImage = bufferedCharacterImage.getSubimage(72, 0, 72, 72);
            solid = false;
            GameStats.increaseCopsKilled();
            GameStats.increaseChaos(GameStats.KILL_COP_CHAOS);
            GameStats.removeCop();
        }
    }




    public int getHealth()
    {
        return health;
    }




    private void collide()
    {
        for (ObjectLayer layer : GameDataHandler.getLayers())
        {
            for (int i = 0; i < layer.getObjectList().size(); i++)
            {
                GameObject object = GameDataHandler.getGameObjectList().get(i);

                int offset = 64;
                if (GameMath.distanceBetweenPoints((int) x, (int) y, (int) object.getX(), (int) object.getY()) < offset * 2)
                {
                    if (object.isSolid())
                    {
                        if (y > object.getY() + offset)
                            y += 1;
                        if (y < object.getY() - offset)
                            y -= 1;
                        if (x > object.getX() + offset)
                            x += 1;
                        if (x < object.getX() - offset)
                            x -= 1;
                    }
                }

                if (getBounds().intersects(object.getBounds().getBounds2D()))
                {
                    if (object instanceof Building)
                    {
                        if (y > object.getY())
                            y += offset * 2;
                        if (y < object.getY())
                            y -= offset * 2;
                        if (x > object.getX())
                            x += offset * 2;
                        if (x < object.getX())
                            x -= offset * 2;
                    }

                    if (object instanceof Bullet)
                    {
                        damage(15);
                        // Blood emitter created by Bullet
                    }

                    if (object instanceof Rocket)
                    {
                        damage(100);
                        GameDataHandler.addObject(new BloodEmitter(x, y));
                    }

                    if (object instanceof Laser)
                    {
                        damage(100);
                        GameDataHandler.addObject(new BloodEmitter(x, y));
                    }

                    if (object instanceof Vehicle car)
                    {
                        if (car.getSpeed() > 600)
                        {
                            damage(100);
                            if (car instanceof CopCar)
                            {
                                GameStats.increaseCopsRunoverByOtherCops();
                            }
                        }
                    }

                    if (object instanceof Explosion)
                    {
                        GameDataHandler.addObject(new BloodEmitter(x, y));
                        damage(100);
                    }
                }
            }
        }
    }




    private void bounds()
    {
        // Keep the player within the bounds
        if (x <= GameDataHandler.BOUNDS_NORTH)
        {
            x = GameDataHandler.BOUNDS_NORTH;
        }
        if (y <= GameDataHandler.BOUNDS_WEST)
        {
            y = GameDataHandler.BOUNDS_WEST;
        }
        if (x >= GameDataHandler.BOUNDS_EAST)
        {
            x = GameDataHandler.BOUNDS_EAST;
        }
        if (y >= GameDataHandler.BOUNDS_SOUTH)
        {
            y = GameDataHandler.BOUNDS_SOUTH;
        }
    }




    @Override
    public void update(double dt)
    {
        if (health == MIN_HEALTH)
        {
            return;
        }

        if (!enteringVehicle)
        {
            if (!onFoot)
            {
                x = currentVehicle.getX();
                y = currentVehicle.getY();
            }

            collide();
            bounds();

            // Look at player
            direction = GameMath.lookTowards(x, y,
                    GameDataHandler.getPlayerCharacter().getX(),
                    GameDataHandler.getPlayerCharacter().getY());

            // Get distance to player
            double distanceToPlayer = GameMath.distanceBetweenPoints(x, y,
                    GameDataHandler.getPlayerCharacter().getX(),
                    GameDataHandler.getPlayerCharacter().getY());

            // Shoot at player if in range
            if (distanceToPlayer < fireRange)
            {
                shotTimer += 1 * dt;
                if (shotTimer >= shotTimerTarget)
                {
                    fireGun();
                }
            }


            if (distanceToPlayer > fireRange
            && !GameDataHandler.getPlayerCharacter().isOnFoot()
            && currentVehicle.getHealth() > 0
            && onFoot)
            {
                enteringVehicle = true;
            }
        }

        if (enteringVehicle && onFoot)
        {
            direction = GameMath.lookTowards(x, y, currentVehicle.getX(), currentVehicle.getY());

            x += Math.cos(Math.toRadians(direction)) * 200;
            y += Math.sin(Math.toRadians(direction)) * 200;

            double distanceToVehicle = GameMath.distanceBetweenPoints(x, y, currentVehicle.getX(), currentVehicle.getY());

            if (distanceToVehicle < 100)
            {
                if (currentVehicle.getHealth() > 0)
                {
                    enteringVehicle = false;
                    currentVehicle.embark(this);
                    onFoot = false;
                }
            }
        }
    }




    private void fireGun()
    {
        shootSFX.proximityVolume(x, y, fireRange);
        shotTimer = 0;
        shotTimerTarget = rand.nextDouble(2);

        double shotDist;
        if (onFoot)
        {
            shotDist = 64;
        }
        else
        {
            shotDist = 96;
        }

        double spawnX = x + Math.cos(Math.toRadians(direction)) * shotDist;
        double spawnY = y + Math.sin(Math.toRadians(direction)) * shotDist;
        GameDataHandler.addObject(new Bullet("cop", spawnX, spawnY, 1500, direction));
        shootSFX.play(-15);
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        if (!onFoot) return;
        AffineTransform defaultTransform = g2d.getTransform();

        g2d.rotate(Math.toRadians(direction), x, y);
        g2d.drawImage(characterImage, (int) x - damageRad, (int) y - damageRad, null);
        g2d.setTransform(defaultTransform);

        if (Debug.showCollisionBounds) {
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public void enterVehicle()
    {
        onFoot = false;
    }




    @Override
    public void exitVehicle()
    {
        onFoot = true;
        x = currentVehicle.getX() + Math.cos(Math.toRadians(currentVehicle.getDirection() + 90)) * 64;
        y = currentVehicle.getY() + Math.sin(Math.toRadians(currentVehicle.getDirection() + 90)) * 64;
    }
}