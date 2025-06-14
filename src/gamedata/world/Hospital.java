package gamedata.world;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Hospital extends Building
{
    public Hospital(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        roofHeight = 4;
        scale = 1.25f;

        try
        {
            roofImage = ImageIO.read(new File("res/hospital.png"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        active = true;
    }
}