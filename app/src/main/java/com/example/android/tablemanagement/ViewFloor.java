package com.example.android.tablemanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewFloor extends AppCompatActivity {
    GridView tablegrid, statusgrid;
    ArrayList<Table> tables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_floor);

        // get tables from shared preferences
        tables = loadSharedPreferencesTables(this);
        if (tables.isEmpty()) {
            for (int i = 0; i < 30; i++) {
                tables.add(new Table("", 1));
            }
        }

        // get the grid of tables from the view floor layout so we can use it programmatically
        tablegrid = findViewById(R.id.view_floor_grid);
        // this sets what each item looks like and sets it up as grid
        tablegrid.setAdapter(new TableAdapter(ViewFloor.this, tables));

        // get grid for status legend from layout
        statusgrid = findViewById(R.id.status_legend);
        statusgrid.setAdapter(new StatusAdapter(this));

        // make an onclick listener for each table
        tableOnClick();
        // when the activity pauses, it will save the tables
        onPause();
    }

    // when activity pauses, it will save the tables for later use so we don't lose data
    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            saveSharedPreferencesTables(this, tables);
        }
    }

    // create the options menu
    // using onCreateOptionsMenu automatically puts the menu in the top right corner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_floor_menu, menu);
        return true;
    }

    // when an option is selected from the menu, this is what will happen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();
        // allow user to change server sections
        if (selected == R.id.assign_section_button) {
            Intent intent = new Intent(this, AssignSection.class);
            startActivity(intent);
            return true;
        }
        // allows user to click the back button. For some reason we have to override
        // the method; otherwise it doesn't let you go back after clicking options menu
        else if (selected == android.R.id.home) {
            this.finish();
            return true;
        }
        return true;
    }

    // save tables so they are accessible to this class and other classes later
    public static void saveSharedPreferencesTables(Context context, List<Table> tables) {
        SharedPreferences mPrefs = context.getSharedPreferences("mPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tables);
        prefsEditor.putString("myJson", json);
        prefsEditor.apply();
    }

    // load tables from previous app use
    public static ArrayList<Table> loadSharedPreferencesTables(Context context) {
        ArrayList<Table> tables;
        SharedPreferences mPrefs = context.getSharedPreferences("mPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("myJson", "");
        if (json.isEmpty()) {
            tables = new ArrayList<Table>();
        } else {
            Type type = new TypeToken<List<Table>>() {
            }.getType();
            tables = gson.fromJson(json, type);
        }
        return tables;
    }

    // create an action for when a table is clicked
    private void tableOnClick() {
        tablegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
            Table table = tables.get(position);
            if (table.exists()) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.edit_table_popup, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                // label popup
                TextView textView = popupView.findViewById(R.id.table_number);
                String tableText = "Table " + table.getTableID();
                textView.setText(tableText);
                // Create and apply the adapter to the spinner
                createSpinner(popupView, table);

                Button doneButton = (Button) popupView.findViewById(R.id.done_button);
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
            // otherwise do nothing
            }
        });
    }

    // creates spinner and spinner item onClick for popup windows when tables are clicked
    private void createSpinner(View popupView, final Table table) {
        final Spinner spinner = popupView.findViewById(R.id.status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ViewFloor.this,
                R.array.status_array, R.layout.support_simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // override the listener when opening the spinner again
        spinner.setSelection(table.getTableStatus());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                table.setTableStatus(pos);
                tablegrid.invalidateViews();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }
}
