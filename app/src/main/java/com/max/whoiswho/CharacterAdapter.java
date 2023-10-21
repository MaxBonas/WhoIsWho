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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {
    private final List<Character> characters;
    private final LayoutInflater inflater;
    private final ImageView floatingImageView;
    private boolean isLongPress = false;  // Añadido para gestionar la detección de pulsación larga
    private boolean isEnlargedViewActive = false;  // Añadido para gestionar si la vista ampliada está activa


    public CharacterAdapter(Context context, List<Character> characters, ImageView floatingImageView) {
        this.characters = characters;
        this.inflater = LayoutInflater.from(context);
        this.floatingImageView = floatingImageView;
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
                    sortCharacters();
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
                floatingImageView.setImageResource(current.getImagePath());
                ((MainActivity) holder.characterImage.getContext()).setRecyclerViewScrollEnabled(false);
                floatingImageView.setVisibility(View.VISIBLE);
                isLongPress = true;  // Indicar que es una pulsación larga
            }
        };

        holder.characterImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isEnlargedViewActive) {
                    return true;  // Si la vista ampliada está activa, no procesar más eventos táctiles
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
                            ((MainActivity) holder.characterImage.getContext()).setRecyclerViewScrollEnabled(true);  // Habilita el desplazamiento
                        } else {
                            v.performClick();
                        }
                        v.removeCallbacks(enlargeImageRunnable);
                        isLongPress = false;  // Restablecer la bandera para la próxima interacción
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        ((MainActivity) v.getContext()).setRecyclerViewScrollEnabled(true);
                        floatingImageView.setVisibility(View.GONE);
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

    @Override
    public int getItemCount() {
        return characters.size();
    }

    private void sortCharacters() {
        Collections.sort(characters, new Comparator<Character>() {
            @Override
            public int compare(Character c1, Character c2) {
                return Boolean.compare(c1.isCrossedOut(), c2.isCrossedOut());
            }
        });
    }

    class CharacterViewHolder extends RecyclerView.ViewHolder {
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
}
