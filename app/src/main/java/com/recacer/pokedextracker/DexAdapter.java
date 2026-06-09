package com.recacer.pokedextracker;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DexAdapter extends ArrayAdapter<CaughtPokemon> {
    String[] games;
    public DexAdapter(@NonNull Context context, @NonNull List<CaughtPokemon> objects, @NonNull String[] g){
        super(context, R.layout.pokemon_item_layout, R.id.pokemonName, objects);
        games = g;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent){
        CaughtPokemon pokemon = getItem(position);

        View view = convertView;
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.pokemon_item_layout, parent, false);

        View checkLayout = view.findViewById(R.id.check_layout);
        int layoutWidth = checkLayout.getWidth();
        checkLayout.setMinimumWidth(layoutWidth);
        assert pokemon != null;

        String game1 = "", game2 = "", game3 = "";
        TextView name = view.findViewById(R.id.pokemonName);
        CheckBox check1 = view.findViewById(R.id.check1);
        CheckBox check2 = view.findViewById(R.id.check2);
        CheckBox check3 = view.findViewById(R.id.check3);

        switch(games.length){
            case 1:{
                game2 = games[0];
                check1.setVisibility(INVISIBLE);
                check3.setVisibility(INVISIBLE);
                break;
            }
            case 2:{
                game2 = games[0];
                game3 = games[1];
                check1.setVisibility(INVISIBLE);
                check3.setVisibility(VISIBLE);
                check3.setChecked(pokemon.caught.get(game3));
                break;
            }
            case 3:{
                game1 = games[0];
                game2 = games[1];
                game3 = games[2];
                check1.setVisibility(VISIBLE);
                check1.setChecked(pokemon.caught.get(game1));
                check3.setVisibility(VISIBLE);
                check3.setChecked(pokemon.caught.get(game3));
                break;
            }
        }
        check2.setChecked(pokemon.caught.get(game2));

        name.setText(pokemon.name);
        String finalgame1 = game1, finalgame2 = game2, finalgame3 = game3;

        check1.setOnClickListener(v -> {
            pokemon.caught.put(finalgame1, check1.isChecked());
            TrackerJSON.writeInternalJSON(getContext(), pokemon);
        });

        check2.setOnClickListener(v -> {
            pokemon.caught.put(finalgame2, check2.isChecked());
            TrackerJSON.writeInternalJSON(getContext(), pokemon);
        });

        check3.setOnClickListener(v -> {
            pokemon.caught.put(finalgame3, check3.isChecked());
            TrackerJSON.writeInternalJSON(getContext(), pokemon);
        });

        return view;
    }
}
