package com.max.whoiswho;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Referencia a la imagen
        ImageView splashImage = findViewById(R.id.splash_image);

        // Animación de fading in
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(2000);  // Duración en milisegundos

        // Animación de fading out
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(2000);  // Comienza después del fading in
        fadeOut.setDuration(2000);     // Duración en milisegundos

        // Combina ambas animaciones en un conjunto
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        splashImage.setAnimation(animation);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Reproduce el maullido
                mediaPlayer = MediaPlayer.create(SplashActivity.this, R.raw.cat_toti);
                mediaPlayer.start();
            }
        }, 500);  // 500 milisegundos equivalen a medio segundo


        // Pasar a la siguiente actividad después de una duración
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, IntroActivity.class));  // Cambiado a TitleActivity
            finish();
        }, 4000);  // 4 segundos de duración total
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
