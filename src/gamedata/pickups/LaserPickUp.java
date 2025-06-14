package gamedata.pickups;

import engine.*;
import gamedata.characters.PlayerCharacter;
import gamedata.vehicles.PlayerCar;

public class LaserPickUp extends PickUp
{
    public LaserPickUp()
    {
        super("res/laserPickUp.png", "res/gunPowerUp.wav", PlayerCharacter.class, PlayerCar.class);
    }

    @Override
    protected void pickupEffect()
    {
        GameDataHandler.getPlayerCharacter().selectLaser();
    }
}