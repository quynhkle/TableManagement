package com.example.android.tablemanagement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.android.tablemanagement.ViewFloor.loadSharedPreferencesTables;
import static com.example.android.tablemanagement.ViewFloor.saveSharedPreferencesTables;

public class MoveTable extends AppCompatActivity {
    private ArrayList<Table> tables;
    private GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_table);

        tables = loadSharedPreferencesTables(this);
        gridview = findViewById(R.id.move_table_grid);
        gridview.setAdapter(new TableAdapter(this, tables));

        moveOnClick();
        onPause();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            saveSharedPreferencesTables(this, tables);
        }
    }

    // select table you want to move
    void moveOnClick() {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
            final Table tableToMove = tables.get(position1);
            if (tableToMove.exists()) {
                Toast toast = Toast.makeText(MoveTable.this, R.string.place_table_message, Toast.LENGTH_LONG);
                toast.show();
                selectPosition(tableToMove);
            }
            }
        });
    }

    // select the new position for the table
    void selectPosition(final Table tableToMove) {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int desiredPos, long id) {
                final Table table = tables.get(desiredPos);
                if (table.exists()) {
                    Toast toast = Toast.makeText(MoveTable.this, R.string.place_table_message2, Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Table t = new Table(tableToMove.getTableID(),tableToMove.getNumSeats());
                    tables.set(desiredPos, t);

                    tableToMove.deleteTable();
                    gridview.invalidateViews();
                    moveOnClick();

                    Toast toast = Toast.makeText(MoveTable.this, R.string.place_table_message3, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }
}
