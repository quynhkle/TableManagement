package com.example.android.tablemanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void launchEditFloor(View view) {
        Intent intent = new Intent(this, EditFloor.class);
        startActivity(intent);
    }

    public void launchWaitlist(View view) {
        Intent intent = new Intent(this, ViewWaitlist.class);
        startActivity(intent);
    }

    public void launchStats(View view) {
        Intent intent = new Intent(this, Statistics.class);
        startActivity(intent);
    }

    public void launchFloor(View view) {
        Intent intent = new Intent(this, ViewFloor.class);
        startActivity(intent);
    }
}
