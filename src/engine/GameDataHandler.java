package engine;

import gamedata.*;
import gamedata.characters.PlayerCharacter;
import gamedata.pickups.*;
import gamedata.vehicles.*;
import gamedata.weapons.Explosion;
import gamedata.weapons.Fire;
import gamedata.world.*;
import mathlib.GameMath;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;

import java.lang.Math;
import java.lang.Math;

/** This class handles the game objects. Using an ArrayList allows for fast iterations.
 *
 * This class handles:
 *      updates
 *      drawing
 *      adding objects
 *      removing objects
 * This way, we only need to call this class's update and paint methods.
 */
public class GameDataHandler
{
    // Map
    private static final Image MAP_IMAGE = new ImageIcon("res/map.png").getImage();
    // Map dimensions. Note that the map dimensions are the size of the map image multiplied by 2.
    public static final int MAP_WIDTH = MAP_IMAGE.getWidth(null) * 2;
    public static final int MAP_HEIGHT = MAP_IMAGE.getHeight(null) * 2;


    private static List<ObjectLayer> layers = new ArrayList();
    /** This layer is for all interactive game objects such as cars, weapons and the player */
    private static ObjectLayer objectsLayer = new ObjectLayer();
    /** This layer is for buildings. This does not get modified. */
    private static ObjectLayer buildingsLayer = new ObjectLayer();
    /** This layer is for the helicopter and is added last. */
    private static ObjectLayer topLayer = new ObjectLayer();


    // Create player
    private static int playerSpawnX = MAP_WIDTH / 2 - 768;
    private static int playerSpawnY = MAP_HEIGHT / 2;
    private static PlayerCharacter playerCharacter;


    // Camera position
    private static double cameraX;
    private static double cameraY;
    private static double cameraZ;
    //made this a var so it can be changed, for instance of not in car it can lerp for illusion of speed in car.
    private static final double LERP_FACTOR = 7.5;
    

    // Map boundaries
    public static final int BOUNDS_NORTH = 768;
    public static final int BOUNDS_SOUTH = 5374;
    public static final int BOUNDS_EAST = 5374;
    public static final int BOUNDS_WEST = 768;
    public static final int DESPAWN_TIME = 30;
    private static final int CELL_SIZE = 128;

    /** Objects beyond this distance from the camera are not drawn */
    private static final int DRAW_DISTANCE = 1200;

    /** This serves as the repawn point */
    private static Hospital hospital = new Hospital(27 * CELL_SIZE, 29 * CELL_SIZE, 256, 256);

    public static void setupGameWorld()
    {
        layers.add(objectsLayer);
        layers.add(buildingsLayer);
        layers.add(topLayer);

        addBuildings();

        // Spawn helicopter
        topLayer.addObject(new Helicopter(-1000, MAP_WIDTH / 2 - 512));
        topLayer.addObject(new Helicopter(-1000, MAP_WIDTH / 2 - 512));

        addCopSpawners();
        addPlayerCarSpawners();
        addPickups();

        // Add player character
        playerCharacter = new PlayerCharacter(playerSpawnX, playerSpawnY);
        objectsLayer.addObject(playerCharacter);
    }




    private static void addBuildings()
    {
        // Add buildings
        buildingsLayer.addObject(new Building(13 * CELL_SIZE, 12 * CELL_SIZE, 256, 256));
        buildingsLayer.addObject(new Building(19 * CELL_SIZE, 12 * CELL_SIZE, 512, 256));
        buildingsLayer.addObject(new Building(19 * CELL_SIZE, 16 * CELL_SIZE, 512, 256));
        buildingsLayer.addObject(new Building(27 * CELL_SIZE, 12 * CELL_SIZE, 512, 768));

        buildingsLayer.addObject(new Building(13 * CELL_SIZE, 22 * CELL_SIZE, 384, 384));
        buildingsLayer.addObject(new Building(20 * CELL_SIZE, 22 * CELL_SIZE, 384, 384));
        buildingsLayer.addObject(new Building(27 * CELL_SIZE, 22 * CELL_SIZE, 256, 384));
        buildingsLayer.addObject(new Building(33 * CELL_SIZE, 22 * CELL_SIZE, 256, 384));

        buildingsLayer.addObject(new Building(14 * CELL_SIZE, 30 * CELL_SIZE, 256, 768));
        buildingsLayer.addObject(new Building(20 * CELL_SIZE, 30 * CELL_SIZE, 256, 768));

        buildingsLayer.addObject(hospital);
        buildingsLayer.addObject(new Building(33 * CELL_SIZE, 29 * CELL_SIZE, 256, 256));
    }




    private static void addCopSpawners()
    {
        // Add cop spawners
        int copSpawnerOffset = 512;
        objectsLayer.addObject(new CopSpawner(BOUNDS_WEST + copSpawnerOffset, MAP_HEIGHT / 2 - copSpawnerOffset, 15, 0)); // West
        objectsLayer.addObject(new CopSpawner(BOUNDS_EAST - copSpawnerOffset, MAP_HEIGHT / 2 - copSpawnerOffset, 15, 180)); // East
        objectsLayer.addObject(new CopSpawner(MAP_WIDTH / 2 + 128,            BOUNDS_NORTH + copSpawnerOffset, 35, 90)); // North
        objectsLayer.addObject(new CopSpawner(MAP_WIDTH / 2 + 128,            BOUNDS_SOUTH - copSpawnerOffset, 35, 270)); // South
    }




    private static void addPlayerCarSpawners()
    {
        // Add vehicle spawners
        objectsLayer.addObject(new CarSpawner(21 * CELL_SIZE, 15 * CELL_SIZE, 30, 180));
        objectsLayer.addObject(new CarSpawner(19 * CELL_SIZE, 24 * CELL_SIZE, 60, 90));
        objectsLayer.addObject(new CarSpawner(16 * CELL_SIZE + 64, 34 * CELL_SIZE, 30, 90));
        objectsLayer.addObject(new CarSpawner(33 * CELL_SIZE, 36 * CELL_SIZE, 60, 0));
    }




    private static void addPickups()
    {
        //add powerUps - the amount of these is set here, they will despawn and respawn depending on the targetTimer variable in it
        addObject(new HealthPowerUp());
        addObject(new HealthPowerUp());
        addObject(new HealthPowerUp());

        addObject(new FixPowerUp());
        addObject(new FixPowerUp());
        addObject(new FixPowerUp());

        addObject(new RocketPickUp());
        addObject(new RocketPickUp());
        addObject(new RocketPickUp());

        addObject(new LaserPickUp());
        addObject(new LaserPickUp());
        addObject(new LaserPickUp());
    }




    /** Start a new game. This is called at the very start, or when you restart after running out of lives. */
    public static void newGame()
    {
        // Reset player position
        playerCharacter.setX(playerSpawnX);
        playerCharacter.setY(playerSpawnY);

        respawnVehicles();

        // Reset stats
        GameStats.resetLives();
        GameStats.resetPlayerHealth();
        GameStats.resetChaos();
        GameStats.resetAllStats();
        GameStats.resetCopCount();

        // Activate objects
        for (GameObject object : objectsLayer.getObjectList())
        {
            object.setActive(true);
        }
    }



    
    private static void respawnVehicles()
    {
        for (int i = 0; i < objectsLayer.getObjectList().size(); i++)
        {
            GameObject object = objectsLayer.getObjectList().get(i);
            if (object instanceof CarSpawner spawner)
            {
                spawner.spawnCar();
            }
        }
    }




    public static void respawn()
    {
        Explosion.count = 0;
        GameStats.resetPlayerHealth();
        GameStats.resetChaos();
        GameStats.resetCopCount();

        // Place player at hospital
        playerCharacter.setX(hospital.getX());
        playerCharacter.setY(hospital.getY() + 192);

        // Multiple passes are needed to clear all the objects.
        int passes = 15;
        for (int pass = 0; pass < passes; pass++)
        {
            for (int i = 0; i < objectsLayer.getObjectList().size(); i++)
            {
                GameObject object = objectsLayer.getObjectList().get(i);

                if (object instanceof CopCar car)
                {
                    car.getSiren().close();
                }

                if (object instanceof Fire fire)
                {
                    fire.getFireSFX().close();
                }

                if (!(object instanceof PlayerCharacter)
                && !(object instanceof PickUp)
                && !(object instanceof CopSpawner)
                && !(object instanceof CarSpawner))
                {
                    objectsLayer.removeObject(object);
                }
            }
        }

        respawnVehicles();
        ((ArrayList<GameObject>) objectsLayer.getObjectList()).trimToSize();
    }




    public static void cleanupOnGameOver()
    {
        // This stops any camera shake
        Explosion.count = 0;

        // Multiple passes are needed to clear all the objects.
        int passes = 15;
        for (int pass = 0; pass < passes; pass++)
        {
            for (int i = 0; i < objectsLayer.getObjectList().size(); i++)
            {
                GameObject object = objectsLayer.getObjectList().get(i);

                if (object instanceof CopCar car)
                {
                    car.getSiren().close();
                }

                if (object instanceof Fire fire)
                {
                    fire.getFireSFX().close();
                }

                if (!(object instanceof PlayerCharacter)
                && !(object instanceof PickUp)
                && !(object instanceof CopSpawner)
                && !(object instanceof CarSpawner))
                {
                    objectsLayer.removeObject(object);
                }
            }
        }
        ((ArrayList<GameObject>) objectsLayer.getObjectList()).trimToSize();
    }




    public static List<GameObject> getGameObjectList()
    {
        return objectsLayer.getObjectList();
    }




    public static List<ObjectLayer> getLayers()
    {
        return layers;
    }




    public static PlayerCharacter getPlayerCharacter()
    {
        return playerCharacter;
    }




    /** Add object to gameObjectsList */
    public static GameObject addObject(GameObject object)
    {
        objectsLayer.addObject(object);
        return object;
    }




    /** Remove a game object */
    public static void removeObject(GameObject object)
    {
        objectsLayer.removeObject(object);
    }




    /// Update camera, this relies on dt for smoothing.
    private static void updateCameraPos(double dt)
    {
        /// change camera position here so that we can smooth it with dt.
        cameraX += ((-playerCharacter.getX() - cameraX + (.5 * Game.WINDOW_WIDTH))) * dt * LERP_FACTOR;
        cameraY += ((-playerCharacter.getY() - cameraY + (.5 * Game.WINDOW_HEIGHT))) * dt * LERP_FACTOR;
        /// if there's an explosion camera will shake
        if (Explosion.count > 0)
        {
            cameraX += new Random().nextDouble(-3, 3);
            cameraY += new Random().nextDouble(-3, 3);
        }

        if (playerCharacter.getCurrentCar() != null)
        {
            // apply via scaling to simulate z pos, it's clamped to  1-1.4. Scaling works by multipling normalized speed by .4 by an amount that when normalized speed = 1 (full speed), it equals max zoom out scale.
            cameraZ = Math.clamp(0.8 + Math.exp(-playerCharacter.getCurrentCar().getNormalizedSpeed()), 1, 1.6); //negative for expo decay
        }
    }




    private static void scaleCamera(Graphics2D g2d)
    {
        g2d.translate(Game.WINDOW_WIDTH / 2.0, Game.WINDOW_HEIGHT / 2.0); // translate to middle of window
        g2d.scale((cameraZ), (cameraZ)); //use camera z to scale (updated in updateCameraPos
        g2d.translate(-Game.WINDOW_WIDTH / 2.0, -Game.WINDOW_HEIGHT / 2.0); // undo transform
    }




    /**
     * Update game objects
     * TODO Note that this function uses a regular for-loop for the object iteration, not a for-each loop.
     * Experience has taught me that for-each loops do not play nice with concurrent modifications.
     */
    public static void update(double dt)
    {
        for (ObjectLayer layer : layers)
        {
            for (int j = 0; j < layer.getObjectList().size(); j++)
            {
                GameObject object = layer.getObjectList().get(j);
                object.engineUpdate(dt);
            }
        }

        // change camera position here so that we can use dt to smooth it with dt.
        updateCameraPos(dt);
    }




    /** Paint game objects */
    public static void paintComponent(Graphics2D g2d)
    {
        /// scale first
        scaleCamera(g2d);

        // Follow the player
        if (playerCharacter != null)
        {
            g2d.translate(cameraX, cameraY);
        }

        // Draw background
        g2d.drawImage(MAP_IMAGE, 0, 0, MAP_WIDTH, MAP_HEIGHT, null);

        // Draw objects
        for (ObjectLayer layer : layers)
        {
            for (int j = 0; j < layer.getObjectList().size(); j++)
            {
                GameObject object = layer.getObjectList().get(j);
                double distToCam = GameMath.distanceBetweenPoints(object.getX(), object.getY(),
                        -cameraX + Game.WINDOW_WIDTH / 2, -cameraY + Game.WINDOW_HEIGHT / 2);
                if (distToCam < DRAW_DISTANCE)
                {
                    object.paintComponent(g2d);
                }
            }
        }
    }




    public static double getCameraX()
    {
        return cameraX;
    }

    public static double getCameraY()
    {
        return cameraY;
    }
}