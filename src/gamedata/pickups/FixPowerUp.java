package gamedata.pickups;

import gamedata.vehicles.PlayerCar;

public class FixPowerUp extends PickUp
{
    public FixPowerUp()
    {
        super("res/fix.png", "res/powerUp.wav", PlayerCar.class);
    }

    @Override
    protected void pickupEffect()
    {
        PlayerCar car = (PlayerCar) triggerObject;
        if (car.getHealth() > 0)
        {
            car.setHealth(100);
        }
    }
}