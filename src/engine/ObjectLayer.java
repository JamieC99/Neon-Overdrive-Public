package engine;

import gamedata.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ObjectLayer
{
    private final List<GameObject> objectList = new ArrayList<>();

    public List<GameObject> getObjectList()
    {
        return objectList;
    }

    public void addObject(GameObject object)
    {
        objectList.add(object);
    }

    public void removeObject(GameObject object)
    {
        int index = objectList.indexOf(object);
        if (index >= 0)
        {
            ListIterator<GameObject> iterator = objectList.listIterator(index);
            iterator.next();
            iterator.remove();
        }
    }
}