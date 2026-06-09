package com.recacer.pokedextracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TrackerJSON.createInternalJSON(this);

        Button[] buttons = {findViewById(R.id.rby_button), findViewById(R.id.gsc_button), findViewById(R.id.rse_button), findViewById(R.id.frlg_button),
                findViewById(R.id.dppt_button), findViewById(R.id.hgss_button), findViewById(R.id.bw_button), findViewById(R.id.b2w2_button), findViewById(R.id.xy_button),
                findViewById(R.id.oras_button), findViewById(R.id.sm_button), findViewById(R.id.usum_button), findViewById(R.id.pe_button), findViewById(R.id.swsh_button),
                findViewById(R.id.bdsp_button), findViewById(R.id.la_button), findViewById(R.id.sv_button), findViewById(R.id.lza_button)};

        Map<String, Integer> drawableGame = Map.ofEntries(
                Map.entry("Red/Blue/Yellow", R.drawable.gradient_rby),
                Map.entry("Gold/Silver/Crystal", R.drawable.gradient_gsc),
                Map.entry("Ruby/Sapphire/Emerald", R.drawable.gradient_rse),
                Map.entry("Fire Red/Leaf Green", R.drawable.gradient_frlg),
                Map.entry("Diamond/Pearl/Platinum", R.drawable.gradient_dppt),
                Map.entry("Heart Gold/Soul Silver", R.drawable.gradient_hgss),
                Map.entry("Black/White", R.drawable.gradient_bw),
                Map.entry("Black 2/White 2", R.drawable.gradient_b2w2),
                Map.entry("X/Y", R.drawable.gradient_xy),
                Map.entry("Omega Ruby/Alpha Sapphire", R.drawable.gradient_oras),
                Map.entry("Sun/Moon", R.drawable.gradient_sm),
                Map.entry("Ultra Sun/Ultra Moon", R.drawable.gradient_usum),
                Map.entry("Let's Go Pikachu/Let's Go Eevee", R.drawable.gradient_pe),
                Map.entry("Sword/Shield", R.drawable.gradient_swsh),
                Map.entry("Brilliant Diamond/Shinning Pearl", R.drawable.gradient_bdsp),
                Map.entry("Legends Arceus", R.drawable.gradient_la),
                Map.entry("Scarlet/Violet", R.drawable.gradient_sv),
                Map.entry("Legends ZA", R.drawable.gradient_za)
        );

        for (final Button button : buttons) {
            String games = button.getText().toString();

            button.setBackground(ResourcesCompat.getDrawable(getResources(), Objects.requireNonNull(drawableGame.get(games)), null));
            if(games.equals("Legends Arceus")){
                button.setTextColor(R.color.black);
            }

            button.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, TrackerActivity.class);
                intent.putExtra("GAME_NAME", button.getText().toString());
                startActivity(intent);
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

}