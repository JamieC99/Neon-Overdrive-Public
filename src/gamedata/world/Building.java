package gamedata.world;

import engine.Debug;
import engine.Game;
import engine.GameDataHandler;
import gamedata.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Building extends GameObject
{
    protected Image roofImage;

    protected double xDistToScreen;
    protected double yDistToScreen;

    protected double topX;
    protected double topY;

    protected double widthHalf;
    protected double heightHalf;
    private float topH = 0;
    private float bottomH = 0;

    /** The number of wall image slices. Performance can seriously degrade at higher values.
     * 16 gives a good balance between fidelity and performance. */
    private final int IMAGE_RESOLUTION = 64;

    private int roofEdge = 0;
    private int floorEdge = 0;
    private int edgeXDiff = 0;
    private int edgeYDiffTop = 0;
    private int edgeYDiffBottom = 0;

    protected int roofHeight;
    protected double scale;

    protected int width;
    protected int height;

    protected double cameraX, cameraY;
    private final int OFFSET = 36;

    protected Color wallColor, color2, color3, color4;

    private BufferedImage image;
    private BufferedImage wallImage;
    private BufferedImage buildingImage;

    public Building(int x, int y, int width, int height)
    {
        super(x + width / 2, y + height / 2);
        solid = true;

        this.width = width;
        this.height = height;

        widthHalf = width / 2;
        heightHalf = height / 2;

        Random rand = new Random();
        roofHeight = rand.nextInt(3, 4);

        if (roofHeight == 3) scale = 1.375f;
        if (roofHeight == 4) scale = 1.25f;

        try
        {
            roofImage = ImageIO.read(new File("res/roof.png"));
            buildingImage = ImageIO.read(new File("res/buildings/building.png"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        int choice = rand.nextInt(0, 3);
        wallImage = buildingImage.getSubimage(choice * 64, 0, 64, 64);

        if (choice == 0)
        {
            wallColor = new Color(38, 142, 217);
        }
        else if (choice == 1)
        {
            wallColor = new Color(217, 38, 115);
        }
        else if (choice == 2)
        {
            wallColor = new Color(255, 106, 0);
        }


        active = true;
    }




    @Override
    public void update(double dt)
    {
        // Calculate the distance between the building and the camera
        xDistToScreen = -GameDataHandler.getCameraX() + Game.WINDOW_WIDTH / 2 - x;
        yDistToScreen = -GameDataHandler.getCameraY() + Game.WINDOW_HEIGHT / 2 - y;

        topX = x - (xDistToScreen / roofHeight);
        topY = y - (yDistToScreen / roofHeight);

        cameraX = -GameDataHandler.getCameraX() + Game.WINDOW_WIDTH / 2;
        cameraY = -GameDataHandler.getCameraY() + Game.WINDOW_HEIGHT / 2;
    }




    @Override
    public void paintComponent(Graphics2D g2d)
    {
        // Draw floor
        g2d.setColor(wallColor);
        g2d.fillRect((int) (x - widthHalf), (int) (y - heightHalf), width, height);

		// NORTH WALL
		if (cameraY < y - heightHalf)
        {
			g2d.setColor(wallColor);
			g2d.fillPolygon(new int[] { (int) (topX - widthHalf * scale), (int) (x - widthHalf), (int) (x + widthHalf)},              new int[] { (int) (topY - heightHalf * scale), (int) (y - heightHalf), (int) (y - heightHalf) },           3); // NORTH WALL BOTTOM
			g2d.fillPolygon(new int[] { (int) (topX - widthHalf * scale), (int) (x + widthHalf), (int) (topX + widthHalf * scale) },  new int[] { (int) (topY - heightHalf * scale), (int) (y - heightHalf), (int) (topY - heightHalf * scale) },3); // NORTH WALL TOP
		}

		// SOUTH WALL
		if (cameraY > y + heightHalf)
        {
			g2d.setColor(wallColor);
			g2d.fillPolygon(new int[] { (int) (topX - widthHalf * scale), (int) (x - widthHalf), (int) (x + widthHalf) },               new int[] { (int) (topY + heightHalf * scale), (int) (y + heightHalf), (int) (y + heightHalf) },            3); // SOUTH WALL BOTTOM
			g2d.fillPolygon(new int[] { (int) (topX - widthHalf * scale), (int) (x + widthHalf), (int) (topX + widthHalf * scale) },    new int[] { (int) (topY + heightHalf * scale), (int) (y + heightHalf), (int) (topY + heightHalf * scale) }, 3); // SOUTH WALL TOP
		}
		// EAST WALL
		if (cameraX > (x + widthHalf) + OFFSET)
        {
            roofEdge = (int) (topX + widthHalf * scale);
            floorEdge = (int) (x + widthHalf);
            edgeXDiff = Math.abs(floorEdge - roofEdge);
            edgeYDiffTop = (int) (topY - heightHalf * scale) - (int) (y - heightHalf);
            edgeYDiffBottom = (int) (topY + heightHalf * scale) - (int) (y + heightHalf);

            if (edgeXDiff > 0)
            {
                image = new BufferedImage(edgeXDiff, wallImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

                Graphics2D imageg2d = image.createGraphics();
                imageg2d.drawImage(wallImage, 0, 0, edgeXDiff, image.getHeight(), null);
                imageg2d.dispose();

                int	segments = 1;
                if (edgeXDiff >= IMAGE_RESOLUTION)
                {
                    segments = (edgeXDiff / IMAGE_RESOLUTION);
                }
                // Middle lines
                for (int i = 0; i < edgeXDiff; i += segments)
                {
                    if (i > 0)
                    {
                        g2d.drawImage(image.getSubimage(i, 0, 1, image.getHeight()), roofEdge + (i - segments),
                                (int) (topY - heightHalf * scale) - (int) topH,
                                segments,
                                (int) (height * scale) - (int) bottomH + (int) topH, null);
                    }

                    topH = ((float) i / edgeXDiff) * edgeYDiffTop;
                    bottomH = ((float) i / edgeXDiff) * edgeYDiffBottom;
                }

                g2d.drawImage(image.getSubimage(image.getWidth() - segments, 0, segments, image.getHeight()), (int) (x + widthHalf) - segments, (int) (y - heightHalf), segments, height, null);
            }
        }

		// WEST WALL
		if (cameraX < (x - widthHalf) - OFFSET)
        {
            roofEdge = (int) (topX - widthHalf * scale);
            floorEdge = (int) (x - widthHalf);
            edgeXDiff = Math.abs(floorEdge - roofEdge);
            edgeYDiffTop = (int) (topY - heightHalf * scale) - (int) (y - heightHalf);
            edgeYDiffBottom = (int) (topY + heightHalf * scale) - (int) (y + heightHalf);

            if (edgeXDiff > 0)
            {
                image = new BufferedImage(edgeXDiff, wallImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

                Graphics2D imageg2d = image.createGraphics();
                imageg2d.drawImage(wallImage, 0, 0, edgeXDiff, image.getHeight(), null);
                imageg2d.dispose();

                int	segments = 1;
                if (edgeXDiff >= IMAGE_RESOLUTION)
                {
                    segments = (edgeXDiff / IMAGE_RESOLUTION);
                }

                // Middle lines
                for (int i = 0; i < edgeXDiff; i += segments)
                {
                    if (i > 0)
                    {
                        g2d.drawImage(image.getSubimage(i, 0, 1, image.getHeight()),
                                roofEdge - (i - segments),
                                (int) (topY - heightHalf * scale) - (int) topH,
                                segments,
                                (int) (height * scale) - (int) bottomH + (int) topH, null);
                    }
                    topH = ((float) i / edgeXDiff) * edgeYDiffTop;
                    bottomH = ((float) i / edgeXDiff) * edgeYDiffBottom;
                }

                g2d.drawImage(image.getSubimage(image.getWidth() - segments, 0, segments, image.getHeight()), (int) (x - widthHalf), (int) (y - heightHalf), segments, height, null);
            }
        }

        // Roof with image
        if (roofImage != null)
        {
            g2d.drawImage(roofImage, (int) (topX - widthHalf * scale), (int) (topY - heightHalf * scale), (int) (width * scale) + 1, (int) (height * scale) + 1, null);
        }

        // TODO DEBUG
        if (Debug.showCollisionBounds)
        {
            g2d.setColor(Color.RED);
            g2d.draw(getBounds());
        }
    }




    @Override
    public Rectangle getBounds()
    {
        return new Rectangle((int) (x - widthHalf),  (int) (y - heightHalf), width, height);
    }
}