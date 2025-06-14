package engine;

import gamedata.GameObject;
import gamedata.characters.CopCharacter;
import gamedata.characters.PlayerCharacter;
import gamedata.pickups.*;
import gamedata.vehicles.CopCar;
import gamedata.vehicles.PlayerCar;
import gamedata.vehicles.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class MenuSystem implements KeyListener, MouseListener
{
    public enum Menu
    {
        IN_GAME,                // In-game
        MAIN,                   // Main menu - Access on game start/restart.
        INSTRUCTIONS,           // Instructions menu - access from main menu.
        PAUSE,                  // Pause menu - access from in-game.
        WASTED,                  // Death menu - shows when player dies with lives remaining.
        GAME_OVER               // Game over menu - shows when player dies/arrested with no remaining lives.
    } private Menu currentMenu = Menu.MAIN;

    private final Font hudFont = new Font("Arial", Font.PLAIN, 32);
    private double phoneY;
    private boolean phoneOn;
    private final int PHONE_SPEED = 50;
    private Image phoneImage = new ImageIcon("res/ui/phone.png").getImage();
    private final SFX PHONE_CLICK = new SFX("res/ui/phone_click.wav");

    // Pause map and icons
    // Map width - 1024
    // Map height - 1024
    // World width - 6144
    // World height - 6144
    // Divide world position by 8 to get map position
    private final int MAP_FACT = 8;
    private Image mapImage = new ImageIcon("res/pause/pause_map.png").getImage();
    private Image playerIcon = new ImageIcon("res/pause/player_icon.png").getImage();
    private Image copIcon = new ImageIcon("res/pause/cop_icon.png").getImage();
    private Image carIcon0 = new ImageIcon("res/pause/car_icon_0.png").getImage();
    private Image carIcon1 = new ImageIcon("res/pause/car_icon_1.png").getImage();
    private Image carIcon2 = new ImageIcon("res/pause/car_icon_2.png").getImage();
    private Image copCarIcon = new ImageIcon("res/pause/cop_car_icon.png").getImage();


    public MenuSystem()
    {
        Radio.begin();
    }




    public Menu getCurrentMenu()
    {
        return currentMenu;
    }




    public void setCurrentMenu(Menu menu)
    {
        currentMenu = menu;
    }




    private final Image gunImage = new ImageIcon("res/ui/ui_gun.png").getImage();
    private final Image rocketImage = new ImageIcon("res/ui/ui_rocket.png").getImage();
    private final Image laserImage = new ImageIcon("res/ui/ui_laser.png").getImage();




    /** Draw the Heads-Up Display when in-game */
    public void paintHUD(Graphics2D g2d)
    {
        if (currentMenu != Menu.IN_GAME)
        {
            return;
        }

        int xOffset = 32;
        int yOffset = 48;
        int shadowOffset = 2;

        // Chaos meter
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, Game.WINDOW_WIDTH, 16);

        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, GameStats.getChaos(), 16);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("CHAOS: " + GameStats.getChaos(), 0, 14);

        // Machine gun
        if (GameDataHandler.getPlayerCharacter().getCurrentWeapon() == PlayerCharacter.Weapons.MACHINE_GUN)
        {
            g2d.drawImage(gunImage, xOffset, yOffset, null);
            g2d.setFont(hudFont);

            g2d.setColor(Color.BLACK);
            g2d.drawString("INF", xOffset * 4 + shadowOffset, yOffset * 2 + shadowOffset);

            g2d.setColor(Color.WHITE);
            g2d.drawString("INF", xOffset * 4, yOffset * 2);
        }
        // Rockets
        else if (GameDataHandler.getPlayerCharacter().getCurrentWeapon() == PlayerCharacter.Weapons.ROCKET)
        {
            g2d.drawImage(rocketImage, xOffset, yOffset, null);
            int ammo = GameDataHandler.getPlayerCharacter().getRocketShots();
            g2d.setFont(hudFont);

            g2d.setColor(Color.BLACK);
            g2d.drawString((15 - ammo) + "/15", xOffset * 4 + shadowOffset, yOffset * 2 + shadowOffset);

            g2d.setColor(Color.WHITE);
            g2d.drawString((15 - ammo) + "/15", xOffset * 4, yOffset * 2);
        }
        // Laser
        else if (GameDataHandler.getPlayerCharacter().getCurrentWeapon() == PlayerCharacter.Weapons.LASER)
        {
            g2d.drawImage(laserImage, xOffset, yOffset, null);
            double ammo = GameDataHandler.getPlayerCharacter().getLaserTimer();
            String ammoString = String.format("%.2f/10", 10 - ammo);
            g2d.setFont(hudFont);

            g2d.setColor(Color.BLACK);
            g2d.drawString(ammoString, xOffset * 4 + shadowOffset, yOffset * 2 + shadowOffset);

            g2d.setColor(Color.WHITE);
            g2d.drawString(ammoString, xOffset * 4, yOffset * 2);
        }

        // Draw health bar
        g2d.setColor(Color.BLACK);
        g2d.fillRect(xOffset - 2, yOffset * 3 - 2, 204, 54);

        g2d.setColor(Color.GREEN);
        g2d.fillRect(xOffset, yOffset * 3, GameStats.getPlayerHealth() * 2, 50);

        // Lives
        g2d.setColor(Color.BLACK);
        g2d.setFont(hudFont);
        g2d.drawString("Lives: " + GameStats.getLives(), xOffset + shadowOffset, yOffset * 5 + shadowOffset);

        g2d.setColor(Color.WHITE);
        g2d.setFont(hudFont);
        g2d.drawString("Lives: " + GameStats.getLives(), xOffset, yOffset * 5);

       paintPhone(g2d);
    }



    public void paintVehicleHealthbars(Graphics2D g2d)
    {
        for (GameObject object : GameDataHandler.getGameObjectList())
        {
            if (object instanceof Vehicle car)
            {
                int health = car.getHealth();
                int maxHealth = 100;
                int minHealth = 0;
                int carX = (int) car.getX();
                int carY = (int) car.getY();

                // Draw health bar
                if (health < maxHealth && health > minHealth)
                {
                    // Background makes the health bar more visible.
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(carX - 26, carY - 76, 52, 7);

                    // Got some color adjustments. Health bar turns yellow-ish as the car loses health.
                    g2d.setColor(new Color(155 - health, 155 + health, 0));
                    g2d.fillRect(carX - 25, carY - 75, health / 2, 5);
                }
            }
        }
    }




    private void paintPhone(Graphics2D g2d)
    {
        // Draw phone
        AffineTransform defaultTransform = g2d.getTransform();

        if (phoneOn)
        {
            if (phoneY > -512)
            {
                phoneY -= PHONE_SPEED;
            }
            if (phoneY <= -512)
            {
                phoneY = -512;
            }
        }
        else
        {
            if (phoneY < 0)
            {
                phoneY += PHONE_SPEED;
            }
            if (phoneY > 0)
            {
                phoneY = 0;
            }
        }

        g2d.translate(Game.WINDOW_WIDTH - 256, Game.WINDOW_HEIGHT + phoneY);
        g2d.drawImage(phoneImage, 0, 0, null);
        printStats(g2d, new Font("Arial", Font.BOLD, 15), 16, 52, 32);
        g2d.setTransform(defaultTransform);
    }




    private void printStats(Graphics2D g2d, Font font, int x, int y, int yPos)
    {
        g2d.setColor(Color.WHITE);
        g2d.setFont(font);
        g2d.drawString("Cops Killed: " + GameStats.getCopsKilled(), x, y + yPos);
        g2d.drawString("Cars Destroyed: " + GameStats.getCarsDestroyed(), x, y + yPos * 2);
        g2d.drawString("Cops Run by Other Cops: " + GameStats.getCopsRunoverByOtherCops(), x, y + yPos * 3);
        g2d.drawString("Bullets Fired: " + GameStats.getBulletsFired(), x, y + yPos * 4);
        g2d.drawString("Rockets Fired: " + GameStats.getRocketsFired(), x, y + yPos * 5);
        g2d.drawString("Lasers Fired: " + GameStats.getLasersFired(), x, y + yPos * 6);
        g2d.drawString("Horns Honked: " + GameStats.getHornsHonked(), x, y + yPos * 7);
        g2d.drawString("Times Said Ow: " + GameStats.getTimesSaidOw(), x, y + yPos * 8);
        g2d.drawString("Total Chaos: " + GameStats.getTotalChaos(), x, y + yPos * 9);
    }




    /** Paint the various menus */
    public void paintMenu(Graphics2D g2d)
    {
        //Main Menu
        if (currentMenu == Menu.MAIN)
        {
            g2d.drawImage(new ImageIcon("res/ui/title_screen.png").getImage(), 0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, null);
        }

        // Instructions menu
        if (currentMenu == Menu.INSTRUCTIONS)
        {
            g2d.drawImage(new ImageIcon("res/ui/instructions_screen.png").getImage(), 0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, null);
        }

        // Pause Menu
        if (currentMenu == Menu.PAUSE)
        {
            paintPauseMap(g2d);
        }

        // Wasted
        if (currentMenu == Menu.WASTED)
        {
            g2d.drawImage(new ImageIcon("res/ui/wasted.png").getImage(), 0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, null);
        }

        // Game over screen
        if (currentMenu == Menu.GAME_OVER)
        {
            g2d.drawImage(new ImageIcon("res/ui/game_over.png").getImage(), 0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, null);

            // Display stats
            int x = Game.WINDOW_WIDTH / 2 - 180;
            int y = Game.WINDOW_HEIGHT / 2 - 200;
            int yPos = 48;
            Font font = new Font("Arial", Font.BOLD, 40);
            printStats(g2d, font, x, y, yPos);
            g2d.setFont(font);
            g2d.drawString("Final Score: " + GameStats.getFinalScore(), x, y + yPos * 10);
        }
    }



    private void paintPauseMap(Graphics2D g2d)
    {
        AffineTransform defaultTransform = g2d.getTransform();
        int mapX = Game.WINDOW_WIDTH / 2 - 384;
        int mapY = Game.WINDOW_HEIGHT / 2 - 384;
        g2d.drawImage(mapImage, 0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, null);
        g2d.translate(mapX, mapY);

        for (GameObject object : GameDataHandler.getGameObjectList())
        {
            Image icon = null;
            int iconWidthHalf = 0;
            int iconHeightHalf = 0;
            if (object instanceof PlayerCar car && car.getHealth() > 0)
            {
                if (car.getCarType() == 0)
                {
                    icon = carIcon0;
                }
                else if (car.getCarType() == 1)
                {
                    icon = carIcon1;
                }
                else if (car.getCarType() == 2)
                {
                    icon = carIcon2;
                }
            }
            else if (object instanceof CopCar car && car.getHealth() > 0)
            {
                icon = copCarIcon;
            }
            else if (object instanceof CopCharacter cop && cop.getHealth() > 0 && cop.isOnFoot())
            {
                icon = copIcon;
            }
            else if (object instanceof RocketPickUp || object instanceof LaserPickUp)
            {
                g2d.setColor(Color.MAGENTA);
                g2d.fillOval((int) (object.getX() / MAP_FACT) - 10, (int) (object.getY() / MAP_FACT) - 10, 20 ,20);
            }
            else if (object instanceof FixPowerUp || object instanceof HealthPowerUp)
            {
                g2d.setColor(Color.CYAN);
                g2d.fillOval((int) (object.getX() / MAP_FACT) - 10, (int) (object.getY() / MAP_FACT) - 10, 20 ,20);
            }

            if (icon != null)
            {
                drawIcon(g2d, icon, object);
            }
        }

        // Draw player icon
        if (GameDataHandler.getPlayerCharacter().isOnFoot())
        {
            drawIcon(g2d, playerIcon, GameDataHandler.getPlayerCharacter());
        }
        else
        {
            int playerX = (int) GameDataHandler.getPlayerCharacter().getX();
            int playerY = (int) GameDataHandler.getPlayerCharacter().getY();
            g2d.setColor(Color.RED);
            g2d.drawOval(playerX / MAP_FACT - 25, playerY / MAP_FACT - 25, 50, 50);
        }

        g2d.setTransform(defaultTransform);
    }




    private void drawIcon(Graphics2D g2d, Image icon, GameObject object)
    {
        int iconWidthHalf = icon.getWidth(null) / 2;
        int iconHeightHalf = icon.getHeight(null) / 2;
        double posX = object.getX() / MAP_FACT;
        double posY = object.getY() / MAP_FACT;
        g2d.rotate(Math.toRadians(object.getDirection()), posX, posY);
        g2d.drawImage(icon, (int) (posX) - iconWidthHalf, (int) (posY) - iconHeightHalf, null);
        g2d.rotate(Math.toRadians(-object.getDirection()), posX, posY);
    }




    @Override
    public void keyReleased(KeyEvent e)
    {
        // Main menu
        if (currentMenu == Menu.MAIN)
        {
            // Start game
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                GameDataHandler.newGame();
                currentMenu = Menu.IN_GAME;
            }

            // Go to Instructions menu
            if (e.getKeyCode() == KeyEvent.VK_C)
            {
                currentMenu = Menu.INSTRUCTIONS;
                return;
            }
        }


        // Instructions menu
        if (currentMenu == Menu.INSTRUCTIONS)
        {
            // Go to Main menu
            if (e.getKeyCode() == KeyEvent.VK_C)
            {
                currentMenu = Menu.MAIN;
                return;
            }
        }


        // In game
        if (currentMenu == Menu.IN_GAME)
        {
            // Pause
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                currentMenu = Menu.PAUSE;
                return;
            }
        }


        // Pause menu
        if (currentMenu == Menu.PAUSE)
        {
            // Un-pause
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                currentMenu = Menu.IN_GAME;
                return;
            }

            // Quit
            if (e.getKeyCode() == KeyEvent.VK_Q)
            {
                System.exit(1);
            }
        }


        // Wasted menu
        if (currentMenu == Menu.WASTED)
        {
            // Restart game
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                 GameDataHandler.respawn();
                 currentMenu = Menu.IN_GAME;
            }

            // Quit game
            if (e.getKeyCode() == KeyEvent.VK_Q)
            {
                System.exit(1);
            }
        }


        // Game over menu
        if (currentMenu == Menu.GAME_OVER)
        {
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                currentMenu = Menu.MAIN;
            }

            // Quit game
            if (e.getKeyCode() == KeyEvent.VK_Q)
            {
                System.exit(1);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON2)
        {
            if (currentMenu == Menu.IN_GAME)
            {
                phoneOn = !phoneOn;
                if (!phoneOn)
                {
                    PHONE_CLICK.play(6);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e){}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}