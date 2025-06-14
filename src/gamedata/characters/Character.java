package gamedata.characters;

import engine.SFX;
import gamedata.GameObject;
import gamedata.vehicles.Vehicle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class Character extends GameObject
{
    protected boolean onFoot;
    protected SFX shootSFX;
    protected BufferedImage bufferedCharacterImage;
    protected Image characterImage;
    protected int damageRad = 36;

    public Character(double x, double y, String imageDir)
    {
        super(x, y);

        try
        {
            File image = new File(imageDir);
            bufferedCharacterImage = ImageIO.read(image);
            characterImage = bufferedCharacterImage.getSubimage(0, 0, 72, 72);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public abstract void update(double dt);

    @Override
    public abstract void paintComponent(Graphics2D g2d);

    public abstract void enterVehicle();

    public abstract void exitVehicle();



    @Override
    public final Shape getBounds()
    {
        if (onFoot)
        {
            return new Ellipse2D.Double((int) x - damageRad / 2, (int) y - damageRad / 2, damageRad, damageRad);
        }
        else
        {
            return new Rectangle();
        }
    }
}