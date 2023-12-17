package com.max.whoiswho;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioManager {

    private static List<MediaPlayer> mediaPlayers = new ArrayList<>();
    private static int currentSongIndex = 0;
    private static final int FADE_DURATION = 2000;
    private float volume = 0.0f;
    private final float volumeStep = 0.05f;
    private final Handler fadeInHandler = new Handler();
    private Context context;  // Agrega esto

    public AudioManager(Context context) {
        this.context = context;  // Y esto
        if (mediaPlayers.isEmpty()) {
            mediaPlayers.add(MediaPlayer.create(context, R.raw.piano_bso));
            mediaPlayers.add(MediaPlayer.create(context, R.raw.lone_wolf));
            mediaPlayers.add(MediaPlayer.create(context, R.raw.mystery_unfold));
            mediaPlayers.add(MediaPlayer.create(context, R.raw.mysterious_forest));
            mediaPlayers.add(MediaPlayer.create(context, R.raw.comedy_detective));
            for (MediaPlayer mp : mediaPlayers) {
                mp.setLooping(true);
            }
        }
    }

    public void startMusic() {
        if (!mediaPlayers.get(currentSongIndex).isPlaying()) {
            volume = 0.75f;
            mediaPlayers.get(currentSongIndex).setVolume(volume, volume);
            new Handler().postDelayed(() -> {
                mediaPlayers.get(currentSongIndex).start();
                fadeInHandler.post(fadeInRunnable);
            }, 500);
        }
    }

    private final Runnable fadeInRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayers.get(currentSongIndex) != null && volume < 1.0f) {
                    volume += volumeStep;
                    mediaPlayers.get(currentSongIndex).setVolume(volume, volume);
                    fadeInHandler.postDelayed(this, FADE_DURATION / (int) (1.0f / volumeStep));
                }
            } catch (Exception e) {
                Log.e("AudioManager", "Error adjusting volume for mediaPlayer", e);
            }
        }
    };

    public static void pauseMusic() {
        if (!mediaPlayers.isEmpty() && mediaPlayers.get(currentSongIndex).isPlaying()) {
            mediaPlayers.get(currentSongIndex).pause();
        }
    }

    public static void playMusic() {
        if (!mediaPlayers.isEmpty() && !mediaPlayers.get(currentSongIndex).isPlaying()) {
            mediaPlayers.get(currentSongIndex).start();
        }
    }

    public static boolean isMusicPlaying() {
        return !mediaPlayers.isEmpty() && mediaPlayers.get(currentSongIndex).isPlaying();
    }

    public static void nextSong() {
        if (!mediaPlayers.isEmpty()) {
            mediaPlayers.get(currentSongIndex).stop();
            currentSongIndex = (currentSongIndex + 1) % mediaPlayers.size();
            mediaPlayers.get(currentSongIndex).start();
        }
    }

    public void startSpecificMusic(int index) {
        if (currentSongIndex != index) {
            mediaPlayers.get(currentSongIndex).stop();
            mediaPlayers.get(currentSongIndex).reset();  // Resetear el MediaPlayer

            try {
                // Reconfigurar el MediaPlayer
                AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.piano_bso);
                if (afd != null) {
                    mediaPlayers.get(currentSongIndex).setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    mediaPlayers.get(currentSongIndex).prepare();
                }
            } catch (IOException e) {
                Log.e("AudioManager", "Error resetting media player", e);
            }

            currentSongIndex = index;
        }
        startMusic();
    }
}
