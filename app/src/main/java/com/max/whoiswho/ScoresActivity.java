package com.max.whoiswho;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoresActivity extends AppCompatActivity {

    private RecyclerView scoresRecyclerView;
    private List<ScoreEntry> scores;
    private Button sortButton;
    private String currentSort = "Puntuación"; // Puntuación es el orden inicial

    // Mapa para almacenar la puntuación más alta de cada jugador en cada nivel de dificultad
    private final Map<String, Map<String, ScoreEntry>> playerBestScores = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        scoresRecyclerView = findViewById(R.id.scoresRecyclerView);
        sortButton = findViewById(R.id.sortButton);
        scores = new ArrayList<>();

        // Initialize adapter and set it to RecyclerView
        ScoreAdapter scoreAdapter = new ScoreAdapter(scores);
        scoresRecyclerView.setAdapter(scoreAdapter);
        scoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia el criterio de ordenación aquí y actualiza el adaptador
                sortScores();
            }
        });

        // Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("scores");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerBestScores.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ScoreEntry score = postSnapshot.getValue(ScoreEntry.class);
                    if (score != null) {
                        updatePlayerBestScores(score);
                    }
                }
                // Prepara la lista de puntuaciones para el adaptador
                prepareScoresList();
                sortScores();  // Ordenar las puntuaciones cada vez que se actualizan
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                Toast.makeText(ScoresActivity.this, "Error al cargar puntuaciones: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePlayerBestScores(ScoreEntry newScore) {
        String playerId = newScore.getPlayerId();
        String difficulty = newScore.getDifficulty();

        if (!playerBestScores.containsKey(playerId)) {
            playerBestScores.put(playerId, new HashMap<>());
        }

        Map<String, ScoreEntry> playerScores = playerBestScores.get(playerId);

        if (!playerScores.containsKey(difficulty) || newScore.getScore() > playerScores.get(difficulty).getScore()) {
            playerScores.put(difficulty, newScore);
        }
    }

    private void prepareScoresList() {
        scores.clear();
        for (Map<String, ScoreEntry> playerScores : playerBestScores.values()) {
            scores.addAll(playerScores.values());
        }
    }

    private void sortScores() {
        Comparator<ScoreEntry> comparator = null;

        switch (currentSort) {
            case "Puntuación":
                comparator = (o1, o2) -> Integer.compare(o2.getScore(), o1.getScore());
                currentSort = "Fecha";
                sortButton.setText("Ordenar por Fecha");
                break;
            case "Fecha":
                comparator = (o1, o2) -> o2.getDate().compareTo(o1.getDate());
                currentSort = "Dificultad";
                sortButton.setText("Ordenar por Dificultad");
                break;
            case "Dificultad":
                comparator = (o1, o2) -> o1.getDifficulty().compareTo(o2.getDifficulty());
                currentSort = "Jugador";
                sortButton.setText("Ordenar por Jugador");
                break;
            case "Jugador":
                comparator = (o1, o2) -> o1.getPlayerId().compareTo(o2.getPlayerId());
                currentSort = "Puntuación";
                sortButton.setText("Ordenar por Puntuación");
                break;
        }

        if (comparator != null) {
            Collections.sort(scores, comparator);
            // Actualiza el adaptador
            ScoreAdapter scoreAdapter = new ScoreAdapter(scores);
            scoresRecyclerView.setAdapter(scoreAdapter);
        }
    }

}