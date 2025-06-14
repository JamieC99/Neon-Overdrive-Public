package gamedata.characters;

import engine.*;
import gamedata.GameObject;
import gamedata.vehicles.PlayerCar;
import gamedata.vehicles.Vehicle;
import gamedata.weapons.*;
import gamedata.world.Building;
import mathlib.GameMath;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

public class PlayerCharacter extends GameObject implements KeyListener, MouseListener
{
    private boolean onFoot;
    private final int ENTER_VEHICLE_DIST = 100;
    private double velX;
    private double velY;
    private double walkSpeed = 200;
    private SFX[] steps =
    {
        new SFX("res/steps/steps0.wav"),
        new SFX("res/steps/steps1.wav"),
        new SFX("res/steps/steps2.wav"),
        new SFX("res/steps/steps3.wav"),
        new SFX("res/steps/steps4.wav"),
        new SFX("res/steps/steps5.wav"),
        new SFX("res/steps/steps6.wav"),
        new SFX("res/steps/steps7.wav"),
    };
    private SFX shootSFX = new SFX("res/weapon_sounds/gun_9mm.wav");
    private SFX rocketSFX = new SFX("res/weapon_sounds/rocket.wav");
    private SFX laserSFX = new SFX("res/laserLoop.wav");
    private PlayerCar currentCar;
    private double distanceToCurrentCar;
    private final SFX carDoorSound = new SFX("res/car_door.wav");

    public enum Weapons
    {
        MACHINE_GUN,
        ROCKET,
        LASER
    }
    private Weapons currentWeapon = Weapons.MACHINE_GUN;

    private boolean firing;
    private final double machineGunFireRate = 0.1;
    private double machineGunFireTimer;

    private boolean canSpawnLaser = true;
    private Laser laser;

    /** How far from the player the projectile spawns */
    private double shotDist;

    private BufferedImage playerBufferedImage;
    private Image characterImage;

    //for ammo capping upgraded weapons
    private double laserTimer;
    private int rocketShots;
    private final int maxShotsRockets = 15;
    private final int maxShotsLasers = 10; //this one is seconds

    public PlayerCharacter(double x, double y)
    {
        super(x, y);
        onFoot = true;
        solid = true;

        try
        {
            File image = new File("res/player_character.png");
            playerBufferedImage = ImageIO.read(image);
            characterImage = playerBufferedImage.getSubimage(0, 0, 72, 72);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void collide()
    {
        for (int i = 0; i < GameDataHandler.getLayers().size(); i++)
        {
            ObjectLayer layer = GameDataHandler.getLayers().get(i);
            for (int j = 0; j < layer.getObjectList().size(); j++)
            {
                GameObject object = layer.getObjectList().get(j);
                //if it hits any object
                if (getBounds().intersects(object.getBounds().getBounds2D()) && !(object instanceof PlayerCharacter))
                {
                    if (object instanceof Building || object instanceof Explosion)
                    {
                        velX = 0;
                        velY = 0;
                        if (y > object.getY())
                            y += 1;
                        if (y < object.getY())
                            y -= 1;
                        if (x > object.getX())
                            x += 1;
                        if (x < object.getX())
                            x -= 1;
                    }

                    if (object instanceof Vehicle vehicle && vehicle.getSpeed() >= 400)
                    {
                        GameStats.damagePlayer(10);
                    }

                    if (object instanceof Explosion)
                    {
                        GameStats.damagePlayer(1);
                    }

                    if (object instanceof Projectile)
                    {
                        GameStats.damagePlayer(10);
                    }

                    if (GameStats.getPlayerHealth() == 0)
                    {
                        characterImage = playerBufferedImage.getSubimage(72, 0, 72, 72);
                    }
                }
            }
        }
    }




    /// audio for footsteps
    public void footstepSFX()
    {
        if (velX != 0 || velY != 0 || (velX != 0 && velY != 0))
        {
            for (SFX step : steps)
            {
                if (step.isPlaying())
                {
                    return;
                }
            }
            steps[(int) (Math.random() * 7)].play(0);
        }
    }




    /// needed for zooming cam
    public boolean isOnFoot()
    {
        return onFoot;
    }




    public void setX(double x)
    {
        this.x = x;
    }




    public void setY(double y)
    {
        this.y = y;
    }




    public PlayerCar getCurrentCar()
    {
        return currentCar;
    }




    @Override
    public void update(double dt)
    {
        if (currentWeapon == Weapons.LASER)
        {
            if (firing)
            {
                laserTimer += dt;
            }
            if (laserTimer >= maxShotsLasers)
            {
                selectMachineGun();
                if (laser != null)
                {
                    GameDataHandler.removeObject(laser);
                }
            }
        }

        if (currentWeapon == Weapons.ROCKET)
        {
            if (rocketShots >= maxShotsRockets)
            {
                selectMachineGun();
            }
        }

        direction = GameMath.lookTowards(x, y, Game.getMouseWorldX(), Game.getMouseWorldY());
        // On foot
        if (onFoot)
        {
            x += velX * dt;
            y += velY * dt;
            /// playfootsteps after calculating Velocity
            footstepSFX();
            collide();

            currentCar = (PlayerCar) GameMath.getClosestObject(this, PlayerCar.class);
            if (currentCar != null)
            {
                distanceToCurrentCar = GameMath.distanceBetweenPoints(x, y, currentCar.getX(), currentCar.getY());
            }
            else
            {
                distanceToCurrentCar = 0;
            }

            shotDist = 32;
        }
        // In vehicle
        else
        {
            x = currentCar.getX();
            y = currentCar.getY();
            shotDist = 64;
        }

        if (currentWeapon == Weapons.MACHINE_GUN)
        {
            if (firing)
            {
                fireMachineGun(dt);
            }
        }

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
    public void paintComponent(Graphics2D g2d)
    {
        if (!onFoot) return;

        AffineTransform defaultTransform = g2d.getTransform();

        g2d.rotate(Math.toRadians(direction), x, y);
        g2d.drawImage(characterImage, (int) x - 36, (int) y - 36, null);

        g2d.rotate(Math.toRadians(-direction), x, y);

        if (Debug.showCollisionBounds)
        {
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }

        g2d.setTransform(defaultTransform);

        // Highlight current car
        if (currentCar != null && currentCar.getHealth() > 0 && distanceToCurrentCar < ENTER_VEHICLE_DIST)
        {
            g2d.setColor(Color.WHITE);
            g2d.drawOval((int) currentCar.getX() - 100, (int) currentCar.getY() - 100, 200, 200);
        }
    }




    @Override
    public Shape getBounds()
    {
        return new Rectangle((int) x - 20, (int) y - 20, 32, 32);
    }




    @Override
    public void keyPressed(KeyEvent e)
    {
        // Move player
        if (onFoot)
        {
            if (e.getKeyCode() == KeyEvent.VK_W) velY = -walkSpeed;
            if (e.getKeyCode() == KeyEvent.VK_S) velY = walkSpeed;
            if (e.getKeyCode() == KeyEvent.VK_A) velX = -walkSpeed;
            if (e.getKeyCode() == KeyEvent.VK_D) velX = walkSpeed;
        }
        // Drive car
        else
        {
            if (e.getKeyCode() == KeyEvent.VK_W) currentCar.pressW();
            if (e.getKeyCode() == KeyEvent.VK_S) currentCar.pressS();
            if (e.getKeyCode() == KeyEvent.VK_A) currentCar.pressA();
            if (e.getKeyCode() == KeyEvent.VK_D) currentCar.pressD();
            if (e.getKeyCode() == KeyEvent.VK_E) currentCar.horn();
        }
    }




    public void enterCar()
    {
        Radio.chooseSong();
        carDoorSound.play(0);
        velX = 0;
        velY = 0;
        onFoot = false;
        solid = false;
        currentCar.setRunning(true);
    }




    public void exitCar()
    {
        Radio.muteSong();
        carDoorSound.play(0);
        onFoot = true;
        x += Math.cos(Math.toRadians(currentCar.getDirection() + 90)) * 64;
        y += Math.sin(Math.toRadians(currentCar.getDirection() + 90)) * 64;
        solid = true;
        currentCar.releaseW();
        currentCar.releaseS();
        currentCar.setRunning(false);
    }




    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_Q)
        {
            Debug.showCollisionBounds = !Debug.showCollisionBounds;
        }

        // On foot
        if (onFoot)
        {
            if (e.getKeyCode() == KeyEvent.VK_W) velY = 0;
            if (e.getKeyCode() == KeyEvent.VK_S) velY = 0;
            if (e.getKeyCode() == KeyEvent.VK_A) velX = 0;
            if (e.getKeyCode() == KeyEvent.VK_D) velX = 0;

            // Enter car
            if (e.getKeyCode() == KeyEvent.VK_F)
            {
                if (currentCar != null && distanceToCurrentCar < ENTER_VEHICLE_DIST && currentCar.getHealth() > 0)
                {
                    enterCar();
                }
            }
        }
        else // In vehicle
        {
            if (e.getKeyCode() == KeyEvent.VK_W) currentCar.releaseW();
            if (e.getKeyCode() == KeyEvent.VK_S) currentCar.releaseS();
            if (e.getKeyCode() == KeyEvent.VK_A) currentCar.releaseA();
            if (e.getKeyCode() == KeyEvent.VK_D) currentCar.releaseD();

            // Exit vehicle
            if (e.getKeyCode() == KeyEvent.VK_F)
            {
                exitCar();
            }
        }
    }




    public void selectMachineGun()
    {
        currentWeapon = Weapons.MACHINE_GUN;
        rocketShots = 0;
        if (laser != null)
        {
            GameDataHandler.removeObject(laser);
        }
        /// this can still be playing if mouse is held down
        if (laserSFX.isPlaying())
        {
            laserSFX.stop();
        }
    }




    public void selectRocket()
    {
        currentWeapon = Weapons.ROCKET;
        rocketShots = 0;
        if (laser != null)
        {
            GameDataHandler.removeObject(laser);
        }
        if (laserSFX.isPlaying())
        {
            laserSFX.stop();
        }
    }




    public int getRocketShots()
    {
        return rocketShots;
    }




    public void selectLaser()
    {
        currentWeapon = Weapons.LASER;
        canSpawnLaser = true;
        laserTimer = 0;
    }




    public double getLaserTimer()
    {
        return laserTimer;
    }




    public Weapons getCurrentWeapon()
    {
        return currentWeapon;
    }




    private void fireMachineGun(double dt)
    {
        machineGunFireTimer += 1 * dt;
        if (machineGunFireTimer >= machineGunFireRate)
        {
            double spawnX = x + Math.cos(Math.toRadians(direction)) * shotDist;
            double spawnY = y + Math.sin(Math.toRadians(direction)) * shotDist;
            GameDataHandler.addObject(new Bullet("player", spawnX, spawnY, 1500, direction));
            shootSFX.play(-15);
            machineGunFireTimer = 0;
            GameStats.increaseBulletsFired();
        }
    }




    private void fireRocket()
    {
        shotDist = 64;
        double spawnX = x + Math.cos(Math.toRadians(direction)) * shotDist;
        double spawnY = y + Math.sin(Math.toRadians(direction)) * shotDist;

        GameDataHandler.addObject(new Rocket(spawnX, spawnY, 1500, direction));
        rocketSFX.play(-5);
        machineGunFireTimer = 0;
        GameStats.increaseRocketsFired();
    }




    private void fireLaser()
    {
        if (canSpawnLaser)
        {
            shotDist = 128;
            double spawnX = x + Math.cos(Math.toRadians(direction)) * shotDist;
            double spawnY = y + Math.sin(Math.toRadians(direction)) * shotDist;
            laser = (Laser) GameDataHandler.addObject(new Laser(spawnX, spawnY));
            canSpawnLaser = false;
            GameStats.increaseLasersFired();
        }
    }




    @Override
    public void mousePressed(MouseEvent e)
    {
        if (Game.getMenuSystem().getCurrentMenu() != MenuSystem.Menu.IN_GAME) return;

        /// spawn projectiles
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            rocketShots++;

            if (currentWeapon == Weapons.MACHINE_GUN)
            {
                if (!firing)
                {
                    machineGunFireTimer = machineGunFireRate;
                    firing = true;
                }
            }
            if (currentWeapon == Weapons.ROCKET)
            {
                fireRocket();
            }
            if (currentWeapon == Weapons.LASER)
            {
                if (!laserSFX.isPlaying()) {
                    laserSFX.loop(-10);
                }
                firing = true;
                fireLaser();
            }
        }
    }




    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            if (currentWeapon == Weapons.MACHINE_GUN)
            {
                firing = false;
            }

            if (currentWeapon == Weapons.LASER)
            {
                firing = false;
                canSpawnLaser = true;
                GameDataHandler.removeObject(laser);
                if (laserSFX.isPlaying())
                {
                    laserSFX.stop();
                }
            }
        }
    }




    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}