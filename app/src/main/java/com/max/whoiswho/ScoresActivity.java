package com.max.whoiswho;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScoresActivity extends AppCompatActivity {

    private RecyclerView scoresRecyclerView;
    private List<ScoreEntry> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        scoresRecyclerView = findViewById(R.id.scoresRecyclerView);
        scores = readScoresFromFile();

        ScoreAdapter scoreAdapter = new ScoreAdapter(scores);
        scoresRecyclerView.setAdapter(scoreAdapter);
        scoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<ScoreEntry> readScoresFromFile() {
        List<ScoreEntry> scores = new ArrayList<>();
        String filename = "scores.csv";

        try {
            FileInputStream fis = openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    scores.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3]));
                }
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scores;
    }
}
