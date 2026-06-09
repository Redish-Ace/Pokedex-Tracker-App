package com.recacer.pokedextracker;

import java.util.Dictionary;

public class CaughtPokemon {
    String name;
    Dictionary<String, Boolean> caught;

    CaughtPokemon(String name, Dictionary<String, Boolean> caught){
        this.name = name;
        this.caught = caught;
    }
}
