package com.project.stephencao.mysatellitemenu.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.project.stephencao.mysatellitemenu.R;
import com.project.stephencao.mysatellitemenu.view.SatelliteMenu;

public class MainActivity extends AppCompatActivity implements SatelliteMenu.OnMenuItemClickListener {
    private SatelliteMenu mSatelliteMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSatelliteMenu = findViewById(R.id.id_menu);
        mSatelliteMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public void onClick(View view, int pos) {

    }
}
