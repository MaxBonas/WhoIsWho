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
    private final List<Character> charactersP1;
    private final List<Character> charactersP2;
    private final LayoutInflater inflater;
    private final ImageView floatingImageView;
    private boolean isLongPress = false;
    private final boolean isMultiplayer;
    private String currentPlayer;

    public CharacterAdapter(Context context, List<Character> charactersP1, List<Character> charactersP2, ImageView floatingImageView, boolean isMultiplayer) {
        this.charactersP1 = new ArrayList<>(charactersP1);
        this.charactersP2 = new ArrayList<>(charactersP2);
        this.inflater = LayoutInflater.from(context);
        this.floatingImageView = floatingImageView;
        this.isMultiplayer = isMultiplayer;
        this.currentPlayer = "Player 1"; // Default
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        List<Character> charactersTemp = isMultiplayer ? ("Player 1".equals(currentPlayer) ? charactersP1 : charactersP2) : charactersP1;
        Context context = holder.characterImage.getContext();
        if (context instanceof MainActivity) {
            charactersTemp = charactersP1;
        } else if (context instanceof LocalPvPActivity) {
            charactersTemp = isMultiplayer ? ("Player 1".equals(currentPlayer) ? charactersP1 : charactersP2) : charactersP1;
        } else {
            charactersTemp = charactersP1;  // Por defecto
        }

        final List<Character> characters = charactersTemp;  // Variable final

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
                    sortCharacters(characters);  // Ahora debería funcionar
                    notifyDataSetChanged();
                } else {
                    // Restablecer la bandera para la próxima interacción
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
                    RecyclerView currentRecyclerView = "Player 1".equals(currentPlayer) ? ((LocalPvPActivity) context).characterRecyclerViewP1 : ((LocalPvPActivity) context).characterRecyclerViewP2;
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
                    RecyclerView currentRecyclerView = "Player 1".equals(currentPlayer) ? ((LocalPvPActivity) context).characterRecyclerViewP1 : ((LocalPvPActivity) context).characterRecyclerViewP2;
                    ((LocalPvPActivity) context).setRecyclerViewScrollEnabled(currentRecyclerView, true);  // Pasar el RecyclerView correcto
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.postDelayed(enlargeImageRunnable, 500);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Si se ha ejecutado una pulsación larga, simplemente oculte la imagen.
                        // Si no, ejecute el evento de clic.
                        if (isLongPress) {
                            floatingImageView.setVisibility(View.GONE);
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).setRecyclerViewScrollEnabled(true);
                            } else if (context instanceof LocalPvPActivity) {
                                RecyclerView currentRecyclerView = "Player 1".equals(currentPlayer) ? ((LocalPvPActivity) context).characterRecyclerViewP1 : ((LocalPvPActivity) context).characterRecyclerViewP2;
                                ((LocalPvPActivity) context).setRecyclerViewScrollEnabled(currentRecyclerView, true);  // Pasar el RecyclerView correcto
                            }
                        } else {
                            v.performClick();
                        }
                        v.removeCallbacks(enlargeImageRunnable);
                        isLongPress = false;  // Restablecer la bandera para la próxima interacción
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).setRecyclerViewScrollEnabled(true);
                        } else if (context instanceof LocalPvPActivity) {
                            RecyclerView currentRecyclerView = "Player 1".equals(currentPlayer) ? ((LocalPvPActivity) context).characterRecyclerViewP1 : ((LocalPvPActivity) context).characterRecyclerViewP2;
                            ((LocalPvPActivity) context).setRecyclerViewScrollEnabled(currentRecyclerView, true);  // Pasar el RecyclerView correcto
                        }
                        if(floatingImageView != null) {
                            floatingImageView.setVisibility(View.GONE);
                        }
                        v.removeCallbacks(enlargeImageRunnable);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // No hacer nada
                        break;
                }
                return true;
            }
        });
    }

    public Character updatePlayerBoard(String player, String characterName, boolean answer) {
        List<Character> characters = "Player 1".equals(player) ? charactersP1 : charactersP2;
        Character lastCrossedOut = null;
        for (Character character : characters) {
            if (!character.getName().equals(characterName)) {
                if (answer && !character.isCrossedOut()) {
                    lastCrossedOut = character;
                }
                character.setCrossedOut(answer);
            }
        }
        sortCharacters(characters);
        notifyDataSetChanged();
        return lastCrossedOut;
    }

    @Override
    public int getItemCount() {
        return isMultiplayer ? ("Player 1".equals(currentPlayer) ? charactersP1.size() : charactersP2.size()) : charactersP1.size();
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
    // Método para restaurar el último personaje tachado para el Jugador 1
    public void restoreCharacterP1(String characterName) {
        for (Character character : charactersP1) {
            if (character.getName().equals(characterName)) {
                character.setCrossedOut(false);
                break;
            }
        }
        sortCharacters(charactersP1);
        notifyDataSetChanged();
    }

    // Método para restaurar el último personaje tachado para el Jugador 2
    public void restoreCharacterP2(String characterName) {
        for (Character character : charactersP2) {
            if (character.getName().equals(characterName)) {
                character.setCrossedOut(false);
                break;
            }
        }
        sortCharacters(charactersP2);
        notifyDataSetChanged();
    }

    public int getNumberOfUncrossedCharacters() {
        // Debes decidir qué lista usar en función de tu lógica.
        // Por ahora, sólo estamos usando charactersP1 para mantenerlo simple.
        int count = 0;
        for (Character character : charactersP1) {
            if (!character.isCrossedOut()) {
                count++;
            }
        }
        return count;
    }
    public Character getUncrossedCharacter() {
        // Debes decidir qué lista usar en función de tu lógica.
        // Por ahora, sólo estamos usando charactersP1 para mantenerlo simple.
        for (Character character : charactersP1) {
            if (!character.isCrossedOut()) {
                return character;
            }
        }
        return null;
    }
}

