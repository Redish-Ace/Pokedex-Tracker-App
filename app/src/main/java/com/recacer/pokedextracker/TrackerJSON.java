package com.recacer.pokedextracker;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;

public class TrackerJSON {
    static String caughtJSON = "caught_pokemon.json";
    static JSONObject jsonObject;

    public static void createInternalJSON(Context context){
        if (context.getFileStreamPath(caughtJSON).exists()) return;

        try (InputStream is = context.getResources().openRawResource(R.raw.caught_pokemon);
             FileOutputStream fos = context.openFileOutput(caughtJSON, Context.MODE_PRIVATE)) {
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            Log.d("Create Internal JSON", "Successful");
        } catch (Exception e) {
            Log.e("Create Internal JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    public static ArrayList<CaughtPokemon> readInternalJSON(Context context){
        ArrayList<CaughtPokemon> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try{
            InputStream is = context.openFileInput(caughtJSON);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            br.close();

            jsonObject = new JSONObject(sb.toString());
            Iterator<String> pokemon = jsonObject.keys();
            while(pokemon.hasNext()){
                String name = pokemon.next();
                Dictionary<String, Boolean> gamesCaught = new Hashtable<>();
                JSONObject gamesObject = jsonObject.getJSONObject(name);
                Iterator<String> games = gamesObject.keys();
                while(games.hasNext()){
                    String game = games.next();
                    boolean caught = gamesObject.getBoolean(game);
                    gamesCaught.put(game, caught);
                }
                CaughtPokemon caughtPokemon = new CaughtPokemon(name, gamesCaught);
                list.add(caughtPokemon);
            }
        } catch (FileNotFoundException fe){
            createInternalJSON(context);
            list = readInternalJSON(context);
        } catch (Exception e){
            Log.d("Read Intenal JSON Error", Objects.requireNonNull(e.getMessage()));
        }
        return list;
    }

    public static ArrayList<CaughtPokemon> readInternalJSONDex(Context context, ArrayList<String> dex){
        ArrayList<CaughtPokemon> list = readInternalJSON(context);
        ArrayList<CaughtPokemon> dexList = new ArrayList<>();

        for(String pokemon: dex){
            for(CaughtPokemon mon: list){
                if(mon.name.equals(pokemon)){
                    dexList.add(mon);
                }
            }
        }

        return dexList;
    }

    public static void writeInternalJSON(Context context, CaughtPokemon pokemon){
        try{
            if (jsonObject == null) {
                readInternalJSON(context);
            }
            
            JSONObject gamesObject = new JSONObject();
            Enumeration<String> keys = pokemon.caught.keys();
            while (keys.hasMoreElements()) {
                String game = keys.nextElement();
                gamesObject.put(game, pokemon.caught.get(game));
            }
            
            jsonObject.put(pokemon.name, gamesObject);
            
            try (FileOutputStream fos = context.openFileOutput(caughtJSON, Context.MODE_PRIVATE)) {
                fos.write(jsonObject.toString().getBytes());
                Log.d("Update JSON", "Successful: " + pokemon.name);
            }
        } catch (Exception e){
            Log.e("Update JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    public static void createExternalJSON(Context context){
        String filename = "caught_pokemon.json";
        ContentResolver resolver = context.getContentResolver();
        Uri collectionUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " + MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{filename, Environment.DIRECTORY_DOCUMENTS + "/"};
        resolver.delete(collectionUri, selection, selectionArgs);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/json");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri uri = resolver.insert(collectionUri, values);

        if(uri == null) return;

        try(OutputStream outputStream = resolver.openOutputStream(uri); OutputStreamWriter writer = new OutputStreamWriter(outputStream)){
            ArrayList<CaughtPokemon> list = readInternalJSON(context);

            JSONObject outputObject = new JSONObject();
            for(int i=0; i<list.size(); i++){
                String name = list.get(i).name;
                Dictionary<String, Boolean> caught = list.get(i).caught;
                outputObject.accumulate(name, caught);
            }
            writer.write(outputObject.toString());

            Toast.makeText(context, "Saved into caught_pokemon.json in Documents folder", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            Log.d("JSON Write Error", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(context, "Failed to save JSON file", Toast.LENGTH_SHORT).show();
        }
    }

    public static ArrayList<Pokedex> readPokedexJSON(Context context){
        ArrayList<Pokedex> list = new ArrayList<>();

        try{
            InputStream is = context.getResources().openRawResource(R.raw.pokedexes);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            br.close();

            JSONObject dexObject = new JSONObject(sb.toString());
            Iterator<String> regions = dexObject.keys();
            while(regions.hasNext()){
                String region = regions.next();
                JSONObject dexes = dexObject.getJSONObject(region);

                ArrayList<String> games = new ArrayList<>();
                ArrayList<String> regional = new ArrayList<>();
                ArrayList<String> national = new ArrayList<>();

                JSONArray jsonGames = dexes.getJSONArray("games");
                JSONArray jsonRegional = dexes.getJSONArray("regional");
                JSONArray jsonNational = dexes.getJSONArray("national");

                for(int i = 0; i < jsonGames.length(); i++){
                    games.add(jsonGames.getString(i));
                }
                for(int i = 0; i < jsonRegional.length(); i++){
                    regional.add(jsonRegional.getString(i));
                }
                for(int i = 0; i < jsonNational.length(); i++){
                    national.add(jsonNational.getString(i));
                }

                Pokedex dex = new Pokedex(region, games, regional, national);
                list.add(dex);
            }
        } catch(Exception e){
            Log.d("Read Pokedex JSON", Objects.requireNonNull(e.getMessage()));
        }

        return list;
    }

    public static void writeExternalCSV(Context context){
        String filename = "caught_pokemon.csv";
        ContentResolver resolver = context.getContentResolver();
        Uri collectionUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " + MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{filename, Environment.DIRECTORY_DOCUMENTS + "/"};
        resolver.delete(collectionUri, selection, selectionArgs);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri uri = resolver.insert(collectionUri, values);

        if(uri == null) return;

        try(OutputStream outputStream = resolver.openOutputStream(uri); OutputStreamWriter writer = new OutputStreamWriter(outputStream)){
            ArrayList<CaughtPokemon> list = readInternalJSON(context);

            writer.write("Pokemon,Red,Blue,Yellow,Gold,Silver,Crystal,Ruby,Sapphire,Emerald,Fire Red,Leaf Green," +
                    "Diamond,Pearl,Platinum,Heart Gold,Soul Silver,Black,White,Black 2,White 2,X,Y,Omega Ruby,Alpha Sapphire," +
                    "Sun,Moon,Ultra Sun,Ultra Moon,Let's Go Pikachu,Let's Go Eevee,Sword,Shield,Brilliant Diamond,Shinning Pearl,Legends Arceus,Scarlet,Violet,Legends ZA,Winds,Waves\n");

            String[] games = {"Red","Blue","Yellow","Gold","Silver","Crystal","Ruby","Sapphire","Emerald","Fire Red","Leaf Green",
                    "Diamond","Pearl","Platinum","Heart Gold","Soul Silver","Black","White","Black 2","White 2","X","Y","Omega Ruby","Alpha Sapphire",
                    "Sun","Moon","Ultra Sun","Ultra Moon","Let's Go Pikachu","Let's Go Eevee","Sword","Shield","Brilliant Diamond","Shinning Pearl",
                    "Legends Arceus","Scarlet","Violet","Legends ZA","Winds","Waves"};

            for(int i=0; i<list.size(); i++){
                String name = list.get(i).name;
                Dictionary<String, Boolean> caught = list.get(i).caught;
                String line = name;

                for (String game: games){
                    line = line.concat(",");
                    if(caught.get(game) != null){
                        line = line.concat(caught.get(game).toString());
                    }
                }
                writer.write(line.concat("\n"));
            }

            Toast.makeText(context, "Saved into caught_pokemon.csv in Documents folder", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            Log.d("CSV Write Error", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(context, "Failed to save CSV file", Toast.LENGTH_SHORT).show();
        }
    }
}
