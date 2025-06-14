package gamedata.pickups;

import engine.*;
import gamedata.characters.PlayerCharacter;

public class HealthPowerUp extends PickUp
{
    public HealthPowerUp()
    {
        super("res/health.png", "res/powerUp.wav", PlayerCharacter.class);
    }

    @Override
    protected void pickupEffect()
    {
        GameStats.resetPlayerHealth();
    }
}