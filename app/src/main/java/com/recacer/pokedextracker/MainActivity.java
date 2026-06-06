package com.recacer.pokedextracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ContentView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        for (final Button button : buttons) {
            String[] games = button.getText().toString().split("/");
            int [] colors = new int[3];



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