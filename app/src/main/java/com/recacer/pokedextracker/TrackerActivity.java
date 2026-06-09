package com.recacer.pokedextracker;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;

public class TrackerActivity extends AppCompatActivity {
    Map<String, String> dexesIndex = Map.ofEntries(
            Map.entry("Red/Blue/Yellow", "kanto"),
            Map.entry("Gold/Silver/Crystal", "johto"),
            Map.entry("Ruby/Sapphire/Emerald", "hoenn"),
            Map.entry("Fire Red/Leaf Green", "extended-kanto"),
            Map.entry("Diamond/Pearl/Platinum", "extended-sinnoh"),
            Map.entry("Heart Gold/Soul Silver", "extended-johto"),
            Map.entry("Black/White", "unova"),
            Map.entry("Black 2/White 2", "extended-unova"),
            Map.entry("X/Y", "kalos"),
            Map.entry("Omega Ruby/Alpha Sapphire", "extended-hoenn"),
            Map.entry("Sun/Moon", "alola"),
            Map.entry("Ultra Sun/Ultra Moon", "extended-alola"),
            Map.entry("Let's Go Pikachu/Let's Go Eevee", "letsgo-kanto"),
            Map.entry("Sword/Shield", "galar"),
            Map.entry("Brilliant Diamond/Shinning Pearl", "sinnoh"),
            Map.entry("Legends Arceus", "hisui"),
            Map.entry("Scarlet/Violet", "paldea"),
            Map.entry("Legends ZA", "lumiose-city")
    );
    ArrayList<CaughtPokemon> gamePokemon = new ArrayList<>();
    ArrayList<Pokedex> dexList;
    ArrayList<CaughtPokemon> pokemonList;
    String gameName;
    String[] games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tracker), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dexList = TrackerJSON.readPokedexJSON(this);
        pokemonList = TrackerJSON.readInternalJSON(this);

        gameName = getIntent().getStringExtra("GAME_NAME");
        TextView gameTextView = findViewById(R.id.game_text);
        if (gameName != null) {
            gameTextView.setText(gameName);

            gamePokemon = getRegionalDex();
            Button[] dexButtons = {findViewById(R.id.regional_button), findViewById(R.id.national_button), findViewById(R.id.galar_dex_button), findViewById(R.id.armor_dex_button),
                    findViewById(R.id.crown_dex_button), findViewById(R.id.paldea_dex_button), findViewById(R.id.mask_dex_button), findViewById(R.id.disk_dex_button),
                    findViewById(R.id.lumiose_dex_button), findViewById(R.id.hyperspace_dex_button)};

            setButtons(dexButtons);
            setGameNames();
            setListView();
            setSearchBar();
        }
    }

    public ArrayList<CaughtPokemon> getRegionalDex(){
        String regionKey = dexesIndex.get(gameName);
        if (regionKey == null) return new ArrayList<>();

        ArrayList<String> regionalDex = null;
        for (Pokedex dex : dexList) {
            if (dex.region.equals(regionKey)) {
                regionalDex = dex.regional_dex;
                break;
            }
        }

        if (regionalDex == null) return new ArrayList<>();

        return TrackerJSON.readInternalJSONDex(this, regionalDex);
    }

    public ArrayList<CaughtPokemon> getNationalDex(){
        String regionKey = dexesIndex.get(gameName);
        if (regionKey == null) return new ArrayList<>();

        ArrayList<String> nationalDex = null;
        for (Pokedex dex : dexList) {
            if (dex.region.equals(regionKey)) {
                nationalDex = dex.national_dex;
                break;
            }
        }

        if (nationalDex == null) return new ArrayList<>();

        return TrackerJSON.readInternalJSONDex(this, nationalDex);
    }

    public ArrayList<CaughtPokemon> getOtherDex(String region){
        ArrayList<String> pokedex = null;
        for (Pokedex dex : dexList) {
            if (dex.region.equals(region)) {
                pokedex = dex.regional_dex;
                break;
            }
        }

        if (pokedex == null) return new ArrayList<>();

        return TrackerJSON.readInternalJSONDex(this, pokedex);
    }

    public void setButtons(Button[] buttons){
        LinearLayout general = findViewById(R.id.general_dexes);
        LinearLayout swshButtons = findViewById(R.id.swsh_dexes_button_container);
        LinearLayout svButtons = findViewById(R.id.sv_dexes_button_container);
        LinearLayout lzaButtons = findViewById(R.id.za_dexes_button_container);

        switch (gameName){
            case "Red/Blue/Yellow":
            case "Let's Go Pikachu/Let's Go Eevee":
            case "Legends Arceus": {
                general.setVisibility(GONE);
                swshButtons.setVisibility(GONE);
                svButtons.setVisibility(GONE);
                lzaButtons.setVisibility(GONE);
                break;
            }
            case "Sword/Shield":{
                general.setVisibility(GONE);
                swshButtons.setVisibility(VISIBLE);
                svButtons.setVisibility(GONE);
                lzaButtons.setVisibility(GONE);
                break;
            }
            case "Scarlet/Violet":{
                general.setVisibility(GONE);
                swshButtons.setVisibility(GONE);
                svButtons.setVisibility(VISIBLE);
                lzaButtons.setVisibility(GONE);
                break;
            }
            case "Legends ZA":{
                general.setVisibility(GONE);
                swshButtons.setVisibility(GONE);
                svButtons.setVisibility(GONE);
                lzaButtons.setVisibility(VISIBLE);
                break;
            }
            default:{
                general.setVisibility(VISIBLE);
                swshButtons.setVisibility(GONE);
                svButtons.setVisibility(GONE);
                lzaButtons.setVisibility(GONE);
            }
        }

        for(Button btn : buttons){
            btn.setOnClickListener(v -> {
                changeList(btn.getText().toString());
            });
        }

        Button csvButton = findViewById(R.id.csv_button);
        csvButton.setOnClickListener(v -> {
            TrackerJSON.writeExternalCSV(this);
        });

        Button jsonButton = findViewById(R.id.json_button);
        jsonButton.setOnClickListener(v -> {
            TrackerJSON.createExternalJSON(this);
        });
    }

    public void setGameNames(){
        games = gameName.split("/");
        TextView text1 = findViewById(R.id.textGame1);
        TextView text2 = findViewById(R.id.textGame2);
        TextView text3 = findViewById(R.id.textGame3);

        switch(games.length){
            case 1:{
                text2.setText(games[0]);
                text1.setText("");
                text1.setVisibility(INVISIBLE);
                text3.setText("");
                text3.setVisibility(INVISIBLE);
                break;
            }
            case 2:{
                text2.setText(games[0]);
                text3.setText(games[1]);
                text1.setText("");
                text1.setVisibility(INVISIBLE);
                text3.setVisibility(VISIBLE);
                break;
            }
            case 3:{
                text1.setText(games[0]);
                text2.setText(games[1]);
                text3.setText(games[2]);
                text1.setVisibility(VISIBLE);
                text2.setVisibility(VISIBLE);
                text3.setVisibility(VISIBLE);
                break;
            }
        }
    }

    public void setListView(){
        ListView listView = findViewById(R.id.dex_list);

        DexAdapter adapter = new DexAdapter(this, gamePokemon, games);
        listView.setAdapter(adapter);
    }

    public void setSearchBar(){
        EditText searchBar = findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = searchBar.getText().toString();

                if(searchString.isEmpty()){
                    setListView();
                } else {
                    ArrayList<CaughtPokemon> searchedList = new ArrayList<>();
                    for(CaughtPokemon mon: gamePokemon){
                        if(mon.name.toLowerCase().contains(searchString)){
                            searchedList.add(mon);
                        }
                    }
                    Log.d("Size", searchedList.size()+"");
                    Log.d("List", searchedList.toString());
                    ListView listView = findViewById(R.id.dex_list);

                    DexAdapter adapter = new DexAdapter(TrackerActivity.this, searchedList, games);
                    listView.setAdapter(adapter);
                }
            }
        });
    }

    public void changeList(String btnText){
        gamePokemon.clear();
        switch (btnText){
            case "Regional":
            case "Galar":
            case "Paldea":
            case "Lumiose":
                gamePokemon = getRegionalDex();
            break;
            case "National": gamePokemon = getNationalDex();
            break;
            default: gamePokemon = getOtherDex(btnText.toLowerCase().replace(" ", "-"));
        }

        setListView();
    }
}
