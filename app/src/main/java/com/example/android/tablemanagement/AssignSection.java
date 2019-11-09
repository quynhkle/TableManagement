package com.example.android.tablemanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.tablemanagement.ViewFloor.loadSharedPreferencesTables;
import static com.example.android.tablemanagement.ViewFloor.saveSharedPreferencesTables;

public class AssignSection extends AppCompatActivity {
    GridView tablegrid;
    SeekBar seekbar;
    ArrayList<Table> tables;
    ArrayList<String> servers;
    int serverNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_section);

        // get server names from shared preferences
        servers = loadSharedPreferencesServers(this);

        // get tables from shared preferences
        tables = loadSharedPreferencesTables(this);

        // get grid of tables from edit floor layout
        tablegrid = findViewById(R.id.edit_floor_tables);
        // Table adapter sets what each table looks like
        tablegrid.setAdapter(new TableAdapter(this, tables));

        serverNum = servers.size();
        seekbar = findViewById(R.id.seekBar);
        seekbar.setMax(serverNum);
        seekBarOnChange(seekbar);

        // tell user to what to do
        Toast message = Toast.makeText(AssignSection.this, R.string.assign_section_message, Toast.LENGTH_LONG);
        message.show();

        onPause();
    }

    // create the options menu
    // using onCreateOptionsMenu automatically puts the menu in the top right corner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assign_section_menu, menu);
        return true;
    }

    // when an option is selected from the menu, this is what will happen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();
        // allow user to change number of servers
        if (selected == R.id.select_server_num) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater.inflate(R.layout.select_server_num, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            popupWindow.showAtLocation(tablegrid, Gravity.CENTER, 0, 0);

            // Create and apply the adapter to the spinner
            createSpinner(popupView);

            Button doneButton = popupView.findViewById(R.id.done_button);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    addInServerNames();
                }
            });
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

    private void addInServerNames() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.add_server_names, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.showAtLocation(tablegrid, Gravity.CENTER, 0, 0);

        // Create EditTexts to add in server names
        final ViewGroup layout = popupView.findViewById(R.id.add_server_names);

        // create edit texts so user can input server names
        for (int i = 0; i<serverNum; i++) {
            EditText edit = new EditText(AssignSection.this);
            edit.setId(i);
            edit.setHint("Type server name here...");
            edit.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.setMargins(20,20,20,20);
            edit.setLayoutParams(params);
            layout.addView(edit);
        }

        Button doneButton = popupView.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servers.clear();

                for (int i = 0; i<serverNum; i++) {
                    // get names from edit texts
                    EditText edit = layout.findViewById(i);
                    String name = edit.getText().toString();
                    servers.add(i, name);
                }
                popupWindow.dismiss();
            }
        });
    }

    private void seekBarOnChange(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                // tell user to select tables for the server's section
                Toast message = Toast.makeText(AssignSection.this, R.string.seekbar_change_message, Toast.LENGTH_LONG);
                message.show();

                tablegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Table table = tables.get(position);
                        if (table.exists()) {
                            table.setServer(progress);
                            tablegrid.invalidateViews();

                            saveSharedPreferencesTables(AssignSection.this, tables);
                        }
                        // otherwise do nothing
                    }
                });
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // when activity pauses, it will save the tables for later use so we don't lose data
    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            saveSharedPreferencesTables(this, tables);
            saveSharedPreferencesServers(this, servers);
        }
    }

    // save servers so they are accessible to this class and other classes later
    public static void saveSharedPreferencesServers(Context context, List<String> servers) {
        SharedPreferences mPrefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(servers);
        prefsEditor.putString("myJson", json);
        prefsEditor.apply();
    }

    // load servers from previous app use
    public static ArrayList<String> loadSharedPreferencesServers(Context context) {
        ArrayList<String> servers;
        SharedPreferences mPrefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("myJson", "");
        if (json.isEmpty()) {
            servers = new ArrayList<String>();
        } else {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            servers = gson.fromJson(json, type);
        }
        return servers;
    }

    // creates spinner and spinner onClick for popup windows when tables are clicked
    private void createSpinner(final View popupView) {
        final Spinner spinner = (Spinner) popupView.findViewById(R.id.server_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AssignSection.this,
                R.array.server_num, R.layout.support_simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // override the listener when opening the spinner again
        spinner.setSelection(serverNum);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                if (pos != serverNum) {
                    serverNum = pos;
                    seekbar.setMax(serverNum);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }
}
