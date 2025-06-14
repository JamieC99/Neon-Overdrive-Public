package mathlib;

import engine.GameDataHandler;
import gamedata.GameObject;

public class GameMath
{
    /** Return the distance between two points */
    public static double distanceBetweenPoints(double x1, double y1, double x2, double y2)
    {
        double xDist = x2 - x1;
        double yDist = y2 - y1;
        return java.lang.Math.sqrt(xDist*xDist + yDist*yDist);
    }




    public static double lookTowards(double x1, double y1, double x2, double y2)
    {
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        return Math.toDegrees(Math.atan2(yDiff, xDiff));
    }




    /// returns closest obj of the passed class
    public static GameObject getClosestObject(GameObject self, Class cls)
    {
        GameObject closestObject = null;
        double temp = 999999;
        for (GameObject object : GameDataHandler.getGameObjectList())
        {
            if (object.getClass() == cls)
            {
                if (distanceBetweenPoints(object.getX(), object.getY(), self.getX(), self.getY()) < temp)
                {
                    temp = distanceBetweenPoints(object.getX(), object.getY(), self.getX(), self.getY());
                    closestObject = object;
                }
            }
        }
        return closestObject;
    }
}