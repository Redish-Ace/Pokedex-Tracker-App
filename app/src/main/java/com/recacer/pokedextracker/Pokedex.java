package com.recacer.pokedextracker;

import java.util.ArrayList;

public class Pokedex {
    String region;
    ArrayList<String> games, regional_dex, national_dex;

    Pokedex(String r, ArrayList<String> g, ArrayList<String> rd, ArrayList<String> nd){
        this.region = r;
        this.games = g;
        this.regional_dex = rd;
        this.national_dex = nd;
    }
}
