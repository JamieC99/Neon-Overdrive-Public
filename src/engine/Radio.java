package engine;

import java.util.Random;

public class Radio
{
    private static final SFX[] songs = new SFX[3];
    private static final SFX songOne = new SFX("res/music/One.wav");
    private static final SFX songTwo = new SFX("res/music/Two.wav");
    private static final SFX songThree = new SFX("res/music/Three.wav");

    private static final int MUTE_VOL = -1000;
    private static final int PLAY_VOL = 0;

    public static void begin()
    {
        songs[0] = songOne;
        songs[1] = songTwo;
        songs[2] = songThree;
        songOne.loop(MUTE_VOL);
        songTwo.loop(MUTE_VOL);
        songThree.loop(MUTE_VOL);
    }

    public static void chooseSong()
    {
        Random rand = new Random();
        int song = rand.nextInt(0, 3);
        songs[song].setVolume(PLAY_VOL);
    }

    public static void muteSong()
    {
        songs[0].setVolume(MUTE_VOL);
        songs[1].setVolume(MUTE_VOL);
        songs[2].setVolume(MUTE_VOL);
    }
}