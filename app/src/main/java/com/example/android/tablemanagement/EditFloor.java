package com.example.android.tablemanagement;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.android.tablemanagement.ViewFloor.loadSharedPreferencesTables;
import static com.example.android.tablemanagement.ViewFloor.saveSharedPreferencesTables;

public class EditFloor extends AppCompatActivity {
    private ArrayList<Table> tables = new ArrayList<>();
    private GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_floor);

        // get tables from shared preferences
        tables = loadSharedPreferencesTables(this);
        if (tables.isEmpty()) {
            for (int i = 0; i < 40; i++) {
                tables.add(new Table("", 1));
            }
        }

        // get grid of tables from edit floor layout
        gridview = findViewById(R.id.edit_floor_tables);
        // Table adapter sets what each table looks like
        gridview.setAdapter(new TableAdapter(EditFloor.this, tables));

        // create on click for each table
        gridOnClick();
        fabOnClick();

        onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_floor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.menu_deleteall_button) {
            gridview.removeAllViews();
            for (int i = 0; i<tables.size(); i++) {
                // don't really delete. The Table objects are still there, just treated as gone.
                tables.get(i).deleteTable();
            }
            return true;
        }
        else if (selected == R.id.menu_move_button) {
            // since this option leaves editFloor, we save the tables before leaving
            saveSharedPreferencesTables(this, tables);
            startActivity(new Intent(this, MoveTable.class));
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

    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            saveSharedPreferencesTables(this, tables);
        }
    }

    // after table button is created, you can click on it to edit it again.
    void gridOnClick() {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Table table = tables.get(position);
                if (table.exists()) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = inflater.inflate(R.layout.table_settings, null);

                    // create the popup window
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                    // label popup with table name
                    final TextView textView = popupView.findViewById(R.id.table_id);
                    String tableText = "Editing Table " + table.getTableID();
                    textView.setText(tableText);

                    final Spinner spinner = (Spinner) popupView.findViewById(R.id.num_of_seats);
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditFloor.this,
                            R.array.seatnum_array, R.layout.support_simple_spinner_dropdown_item);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Apply the adapter to the spinner
                    spinner.setAdapter(adapter);
                    spinner.setSelection(table.getNumSeats());

                    Button doneButton = (Button) popupView.findViewById(R.id.settings_done_button);
                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // change table object name according to input.
                            EditText editText = popupView.findViewById(R.id.add_table_name);
                            String text = editText.getText().toString();
                            if (!text.equals("")) {
                                table.setTableID(text);
                            }

                            // change size of table according to choice.
                            int seatNum = spinner.getSelectedItemPosition();
                            table.setSeatNum(seatNum);

                            gridview.invalidateViews();

                            popupWindow.dismiss();
                        }
                    });

                    Button deleteButton = popupView.findViewById(R.id.settings_delete_button);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            table.deleteTable();
                            gridview.invalidateViews();

                            popupWindow.dismiss();
                        }
                    });
                }
            }
        });
    }

    // whenever the plus button is clicked, window will pop up with options to add table.
    private void fabOnClick() {
        FloatingActionButton fab = findViewById(R.id.edit_floor_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.add_table, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                final Spinner spinner = popupView.findViewById(R.id.num_of_seats);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditFloor.this,
                        R.array.seatnum_array, R.layout.support_simple_spinner_dropdown_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                // show the popup window
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                // dismiss popup window when DONE is clicked
                Button doneButton = popupView.findViewById(R.id.settings_done_button);
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get input from user for table name and number of seats
                        EditText tableName = popupView.findViewById(R.id.add_table_name);
                        String nameOfTable = tableName.getText().toString();

                        int seatNum = spinner.getSelectedItemPosition();

                        // create the button and table using the input from user
                        final Table table = new Table(nameOfTable, seatNum);
                        popupWindow.dismiss();

                        // tell user to select position of table
                        Toast message = Toast.makeText(EditFloor.this, R.string.place_table_message, Toast.LENGTH_LONG);
                        message.show();

                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                                if (tables.get(position).exists()) {
                                    Toast toast = Toast.makeText(EditFloor.this, R.string.place_table_message2, Toast.LENGTH_LONG);
                                    toast.show();
                                }
                                else {
                                    tables.set(position, table);
                                    gridview.invalidateViews();
                                    gridOnClick();
                                    Toast toast = Toast.makeText(EditFloor.this, R.string.place_table_message3, Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}