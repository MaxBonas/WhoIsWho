package com.max.FaceTrace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class TutorialActivity extends AppCompatActivity {

    private String[] tutorialTexts;
    private int[] arrowPositions; // Array of drawable resources for arrows
    private int currentIndex = 0;
    private ImageView tutorialImageView, arrowImageView;
    private TextView tutorialTextView;
    private Button prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialImageView = findViewById(R.id.tutorial_image_view);
        tutorialTextView = findViewById(R.id.tutorial_text_view);
        arrowImageView = findViewById(R.id.arrow_image_view);
        prevButton = findViewById(R.id.prev_button);

        // Initialize your texts and arrow positions here
        tutorialTexts = new String[] {
                getString(R.string.tutorial_welcome),
                getString(R.string.tutorial_objective),
                // ... otros strings del tutorial ...
        };

        arrowPositions = new int[] {
                R.drawable.arrow, // Example arrow position
                // ... otros recursos de flechas ...
        };

        tutorialImageView.setOnClickListener(v -> advanceTutorial());
        prevButton.setOnClickListener(v -> reverseTutorial());

        updateTutorial();
    }

    private void advanceTutorial() {
        if (currentIndex < tutorialTexts.length - 1) {
            currentIndex++;
            updateTutorial();
        } else {
            startGame();
        }
    }

    private void reverseTutorial() {
        if (currentIndex > 0) {
            currentIndex--;
            updateTutorial();
        }
    }

    private void updateTutorial() {
        tutorialTextView.setText(tutorialTexts[currentIndex]);
        tutorialTextView.setVisibility(View.VISIBLE);

        if (arrowPositions.length > currentIndex) {
            // Usa Glide para mostrar el GIF
            Glide.with(this)
                    .asGif()
                    .load(arrowPositions[currentIndex])
                    .into(arrowImageView);
            arrowImageView.setVisibility(View.VISIBLE);
        } else {
            arrowImageView.setVisibility(View.INVISIBLE);
        }

        prevButton.setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
    }

    private void startGame() {
        Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
        intent.putExtra("DIFFICULTY", getString(R.string.easy));
        startActivity(intent);
        finish();
    }
}
