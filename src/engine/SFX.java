package engine;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/// Create an object first, passing it the path, then you can play it, loop it, or stop etc.
public class SFX
{
    private Clip clip;
    private FloatControl control;

    public SFX(String path)
    {
        AudioInputStream audioInputStream;
        try
        {
            audioInputStream = AudioSystem.getAudioInputStream(new File(path));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            audioInputStream.close();
        }
        catch (UnsupportedAudioFileException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }




    public void close()
    {
        if (clip != null)
        {
            clip.stop();
            clip.close();
            clip = null;
            control = null;
        }
    }




    public void setVolume(float volume)
    {
        control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(volume);
    }




    public void play(float volume)
    {
        try
        {
            clip.setFramePosition(0);
            control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(volume);
            clip.start();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }




    public void stop()
    {
        if (clip != null)
        {
            clip.stop();
        }
    }




    public void loop(float volume)
    {
        try
        {
            clip.setFramePosition(0);
            control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(volume);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }




    public boolean isPlaying()
    {
        return clip.isRunning();
    }




    /// This adjusts the current SFX volume based on the players distance to another object\
    /// @param x the x coordinate of the object (that isn't the player) that you want the volume to be tied to (usually the one emitting the sound)
    /// @param y the y coordinate of the object (that isn't the player) that you want the volume to be tied to (usually the one emitting the sound)
    public void proximityVolume(double x, double y, double maxDistance)
    {
        double distance = mathlib.GameMath.distanceBetweenPoints(
            x,
            y,
            GameDataHandler.getPlayerCharacter().getX(),
            GameDataHandler.getPlayerCharacter().getY()
        );
        double normalised = Math.min(distance/maxDistance,1);
        float volume = (float) ((-60 + (1-normalised) * (60)));//43344.66 is two furthest points on map size, 60 is decibel range (roughly)
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(Math.clamp(volume,-60,5));
    }
}