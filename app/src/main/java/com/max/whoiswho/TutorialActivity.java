package com.max.whoiswho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    private int[] tutorialImages = {
            R.drawable.tutorial1,
            R.drawable.tutorial2,
            R.drawable.tutorial3,
            R.drawable.tutorial4,
            R.drawable.tutorial5,
            R.drawable.tutorial6
    };
    private int currentIndex = 0;
    private ImageView tutorialImageView;
    private Button prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial); // Diseñaremos este layout en el siguiente paso.

        tutorialImageView = findViewById(R.id.tutorial_image_view);
        prevButton = findViewById(R.id.prev_button);

        tutorialImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < tutorialImages.length - 1) {
                    currentIndex++;
                    updateTutorialImage();
                } else {
                    startGame();
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    updateTutorialImage();
                }
            }
        });

        updateTutorialImage();
    }

    private void updateTutorialImage() {
        tutorialImageView.setImageResource(tutorialImages[currentIndex]);
        prevButton.setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
    }

    private void startGame() {
        Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
        intent.putExtra("DIFFICULTY", "facil");
        startActivity(intent);
        finish();
    }
}
