package com.max.FaceTrace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<ScoreEntry> scores;

    public ScoreAdapter(List<ScoreEntry> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item, parent, false);
        return new ScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreEntry score = scores.get(position);
        holder.dateTextView.setText(score.getDate());
        holder.scoreTextView.setText(String.valueOf(score.getScore()));
        holder.difficultyTextView.setText(score.getDifficulty());
        holder.playerIdTextView.setText(score.getPlayerId());
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, scoreTextView, difficultyTextView, playerIdTextView;

        ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            difficultyTextView = itemView.findViewById(R.id.difficultyTextView);
            playerIdTextView = itemView.findViewById(R.id.playerIdTextView);
        }
    }
}
