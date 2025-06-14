package gamedata.pickups;

import engine.*;
import gamedata.characters.PlayerCharacter;
import gamedata.vehicles.PlayerCar;

public class RocketPickUp extends PickUp
{
    public RocketPickUp()
    {
        super("res/rocketPickUp.png", "res/gunPowerUp.wav", PlayerCharacter.class, PlayerCar.class);
    }

    @Override
    public void pickupEffect()
    {
        GameDataHandler.getPlayerCharacter().selectRocket();
    }
}