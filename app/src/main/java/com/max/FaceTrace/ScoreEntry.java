package com.max.FaceTrace;

public class ScoreEntry {
    private String date;
    private int score;
    private String difficulty;
    private String playerId;

    public ScoreEntry() {
        // Constructor vacío requerido por Firebase
    }

    public ScoreEntry(String date, int score, String difficulty, String playerId) {
        this.date = date;
        this.score = score;
        this.difficulty = difficulty;
        this.playerId = playerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }


}
