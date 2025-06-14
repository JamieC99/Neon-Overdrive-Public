package gamedata.vehicles;

import engine.GameDataHandler;
import engine.GameStats;
import engine.SFX;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class PlayerCar extends Vehicle
{
    private BufferedImage carBufferedImage;
    private int carType;
    private SFX horn = new SFX("res/car_horn.wav");

    public PlayerCar(double x, double y, double direction)
    {
        super(x, y);
        this.direction = direction;

        // Load image
        try
        {
            File image = new File("res/player_car.png");
            carBufferedImage = ImageIO.read(image);
            Random rand = new Random();
            carType = rand.nextInt(0, 3) * IMAGE_HEIGHT;
            carImage = carBufferedImage.getSubimage(0, carType, IMAGE_WIDTH, IMAGE_HEIGHT);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }




    /** Used for representing the correct car in the pause map */
    public int getCarType()
    {
        return carType / IMAGE_HEIGHT;
    }




    @Override
    public void destroy()
    {
        if (isRunning && GameDataHandler.getPlayerCharacter().getCurrentCar().equals(this)
        && !GameDataHandler.getPlayerCharacter().isOnFoot())
        {
            GameDataHandler.getPlayerCharacter().exitCar();
        }

        horn.close();
        isRunning = false;
        health = 0;
        releaseW();
        releaseS();
        releaseA();
        releaseD();
        carImage = carBufferedImage.getSubimage(192, carType, IMAGE_WIDTH, IMAGE_HEIGHT);
        GameStats.increaseCarsDestroyed();
        GameStats.increaseChaos(GameStats.CAR_DESTROY_CHAOS);
    }




    @Override
    protected void updateExtended(double dt) {}




    public void horn()
    {
        if (!horn.isPlaying())
        {
            horn.play(-10);
            GameStats.increaseChaos(GameStats.CAR_HORN_CHAOS);
            GameStats.increaseHornsHonked();
        }
    }
}