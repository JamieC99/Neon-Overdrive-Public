package engine;

public class GameStats
{
    private static int chaos;
    private static int totalChaos;

    public static final int CAR_DESTROY_CHAOS = 20;
    public static final int KILL_COP_CHAOS = 10;
    public static final int MINE_CHAOS = 5;
    public static final int CAR_HORN_CHAOS = 1;

    // Stats
    private static int copsKilled;
    private static int carsDestroyed;
    private static int copsRunoverByOtherCops;
    private static int bulletsFired;
    private static int rocketsFired;
    private static int lasersFired;
    private static int hornsHonked;
    private static int timesSaidOw;

    public static void increaseCopsKilled() { copsKilled++; }
    public static void increaseCarsDestroyed() { carsDestroyed++; }
    public static void increaseCopsRunoverByOtherCops() { copsRunoverByOtherCops++; chaos += 500; }
    public static void increaseBulletsFired() { bulletsFired++; }
    public static void increaseRocketsFired() { rocketsFired++; }
    public static void increaseLasersFired() { lasersFired++; }
    public static void increaseHornsHonked() { hornsHonked++; }
    public static void increaseTimesSaidOw() { timesSaidOw++; }
    public static int getFinalScore()
    {
        return totalChaos +
                copsKilled +
                carsDestroyed +
                copsRunoverByOtherCops +
                bulletsFired +
                rocketsFired +
                lasersFired +
                hornsHonked +
                timesSaidOw;
    }

    public static int getCopsKilled() { return copsKilled; }
    public static int getCarsDestroyed() { return carsDestroyed; }
    public static int getCopsRunoverByOtherCops() { return copsRunoverByOtherCops; }
    public static int getBulletsFired() { return bulletsFired; }
    public static int getRocketsFired() { return rocketsFired; }
    public static int getLasersFired() { return lasersFired; }
    public static int getHornsHonked() { return hornsHonked; }
    public static int getTimesSaidOw() { return timesSaidOw; }
    public static int getTotalChaos() { return totalChaos; }


    public static void resetAllStats()
    {
        copsKilled = 0;
        carsDestroyed = 0;
        bulletsFired = 0;
        rocketsFired = 0;
        lasersFired = 0;
        hornsHonked = 0;
        timesSaidOw = 0;
        totalChaos = 0;
    }


    public static void increaseChaos(int c)
    {
        chaos += c;
        totalChaos += c;
    }

    public static int getChaos()
    {
        return chaos;
    }

    public static void resetChaos()
    {
        chaos = 0;
    }




    /* Lives and Health */
    private static int lives;
    private static int playerHealth;

    public static int getLives()
    {
        return lives;
    }

    public static void removeALife()
    {
        lives--;
        resetPlayerHealth();
        if (lives == 0)
        {
            GameDataHandler.cleanupOnGameOver();
            Game.getMenuSystem().setCurrentMenu(MenuSystem.Menu.GAME_OVER);
        }
        else
        {
            Game.getMenuSystem().setCurrentMenu(MenuSystem.Menu.WASTED);
        }
    }

    public static void resetLives()
    {
        lives = 3;
    }

    public static int getPlayerHealth()
    {
        return playerHealth;
    }

    private static final SFX OW = new SFX("res/ow.wav");

    public static void damagePlayer(int damage)
    {
        playerHealth -= damage;
        if (!OW.isPlaying())
        {
            OW.play(-10);
            increaseTimesSaidOw();
        }

        if (playerHealth <= 0)
        {
            removeALife();
            GameDataHandler.getPlayerCharacter().selectMachineGun();
            if (!GameDataHandler.getPlayerCharacter().isOnFoot())
            {
                GameDataHandler.getPlayerCharacter().exitCar();
            }
        }
    }

    public static void resetPlayerHealth()
    {
        playerHealth = 100;
    }

    private static int copCount;

    public static final int MAX_COP_COUNT = 10;

    public static void addCop()
    {
        copCount++;
    }

    public static void removeCop()
    {
        copCount--;
    }

    public static int getCopCount()
    {
        return copCount;
    }

    public static void resetCopCount()
    {
        copCount = 0;
    }
}