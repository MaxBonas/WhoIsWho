package com.max.whoiswho;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {
    private final List<Character> characters;
    private final LayoutInflater inflater;
    private final ImageView floatingImageView;
    private boolean isLongPress = false;
    private String currentPlayer;
    private Character lastCrossedOutCharacter = null;

    public CharacterAdapter(Context context, List<Character> charactersP1, ImageView floatingImageView) {
        this.characters = new ArrayList<>(charactersP1);
        this.inflater = LayoutInflater.from(context);
        this.floatingImageView = floatingImageView;
        this.currentPlayer = getString(R.string.player_1);
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character current = characters.get(position);
        holder.characterImage.setImageResource(current.getImagePath());
        holder.characterName.setText(current.getName());

        if (current.isCrossedOut()) {
            holder.crossedImage.setVisibility(View.VISIBLE);
        } else {
            holder.crossedImage.setVisibility(View.INVISIBLE);
        }

        holder.characterName.setShadowLayer(5, 0, 0, Color.BLACK);
        holder.characterName.setShadowLayer(5, 0, 1, Color.BLACK);
        holder.characterName.setShadowLayer(5, 1, 0, Color.BLACK);
        holder.characterName.setShadowLayer(5, 0, -1, Color.BLACK);
        holder.characterName.setShadowLayer(5, -1, 0, Color.BLACK);

        holder.characterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLongPress) {
                    current.setCrossedOut(!current.isCrossedOut());
                    sortCharacters(characters);
                    if (!current.isCrossedOut()) {
                        // Si se va a tachar, actualiza lastCrossedOutCharacter
                        lastCrossedOutCharacter = current;
                    } else {
                        // Si se va a destachar y es el último tachado, limpia lastCrossedOutCharacter
                        if (lastCrossedOutCharacter != null && lastCrossedOutCharacter.getName().equals(current.getName())) {
                            lastCrossedOutCharacter = null;
                        }
                    }
                    notifyDataSetChanged();

                    // Obtén el contexto de la vista para usarlo en el chequeo de ganador.
                    Context context = v.getContext();
                    if (context instanceof LocalPvPActivity) {
                        ((LocalPvPActivity) context).checkForWinnerAfterCrossing(currentPlayer);
                    }
                } else {
                    isLongPress = false;
                }
            }
        });



        Runnable enlargeImageRunnable = new Runnable() {
            @Override
            public void run() {
                Context context = holder.characterImage.getContext();
                if (context instanceof MainActivity) {
                    ((MainActivity) context).setRecyclerViewScrollEnabled(false);
                } else if (context instanceof LocalPvPActivity) {
                    RecyclerView currentRecyclerView = getString(R.string.player_1).equals(currentPlayer) ? ((LocalPvPActivity) context).characterRecyclerViewP1 : ((LocalPvPActivity) context).characterRecyclerViewP2;
                    ((LocalPvPActivity) context).setRecyclerViewScrollEnabled(currentRecyclerView, true);  // Pasar el RecyclerView correcto
                }
                floatingImageView.setImageResource(current.getImagePath());
                floatingImageView.setVisibility(View.VISIBLE);
                isLongPress = true;
            }
        };


        holder.characterImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Context context = v.getContext();
                if (context instanceof MainActivity) {
                    ((MainActivity) context).setRecyclerViewScrollEnabled(true);
                } else if (context instanceof LocalPvPActivity) {
                    RecyclerView currentRecyclerView = getString(R.string.player_1).equals(currentPlayer) ? ((LocalPvPActivity) context).characterRecyclerViewP1 : ((LocalPvPActivity) context).characterRecyclerViewP2;
                    ((LocalPvPActivity) context).setRecyclerViewScrollEnabled(currentRecyclerView, true);  // Pasar el RecyclerView correcto
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.postDelayed(enlargeImageRunnable, 500);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isLongPress) {
                            floatingImageView.setVisibility(View.GONE);
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).setRecyclerViewScrollEnabled(true);
                            } else if (context instanceof LocalPvPActivity) {
                                RecyclerView currentRecyclerView = getString(R.string.player_1).equals(currentPlayer) ? ((LocalPvPActivity) context).characterRecyclerViewP1 : ((LocalPvPActivity) context).characterRecyclerViewP2;
                                ((LocalPvPActivity) context).setRecyclerViewScrollEnabled(currentRecyclerView, true);  // Pasar el RecyclerView correcto
                            }
                        } else {
                            v.performClick();
                        }
                        v.removeCallbacks(enlargeImageRunnable);
                        isLongPress = false;
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).setRecyclerViewScrollEnabled(true);
                        } else if (context instanceof LocalPvPActivity) {
                            RecyclerView currentRecyclerView = getString(R.string.player_1).equals(currentPlayer) ? ((LocalPvPActivity) context).characterRecyclerViewP1 : ((LocalPvPActivity) context).characterRecyclerViewP2;
                            ((LocalPvPActivity) context).setRecyclerViewScrollEnabled(currentRecyclerView, true);  // Pasar el RecyclerView correcto
                        }
                        if(floatingImageView != null) {
                            floatingImageView.setVisibility(View.GONE);
                        }
                        v.removeCallbacks(enlargeImageRunnable);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    private void sortCharacters(List<Character> characters) {
        Collections.sort(characters, new Comparator<Character>() {
            @Override
            public int compare(Character c1, Character c2) {
                return Boolean.compare(c1.isCrossedOut(), c2.isCrossedOut());
            }
        });
    }

    static class CharacterViewHolder extends RecyclerView.ViewHolder {
        ClickableImageView characterImage;
        ImageView crossedImage;
        TextView characterName;

        public CharacterViewHolder(View itemView) {
            super(itemView);
            characterImage = itemView.findViewById(R.id.character_image);
            crossedImage = itemView.findViewById(R.id.crossed_image);
            characterName = itemView.findViewById(R.id.character_name);
        }
    }

    public void restoreLastCrossedOut() {
        if (lastCrossedOutCharacter != null) {
            for (Character character : characters) {
                if (character.getName().equals(lastCrossedOutCharacter.getName())) {
                    character.setCrossedOut(false);
                    break;
                }
            }
            lastCrossedOutCharacter = null; // Resetea la referencia una vez restaurado
            sortCharacters(characters);
            notifyDataSetChanged();
        }
    }


    public int getNumberOfUncrossedCharacters() {
        int count = 0;
        for (Character character : characters) {
            if (!character.isCrossedOut()) {
                count++;
            }
        }
        return count;
    }

    public Character getUncrossedCharacter() {
        for (Character character : characters) {
            if (!character.isCrossedOut()) {
                return character;
            }
        }
        return null;
    }

    public Character getLastCrossedOutCharacter() {
        return lastCrossedOutCharacter;
    }
}

