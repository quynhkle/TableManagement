package com.example.android.tablemanagement;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TableAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Table> tables;
    // array of hex codes for colors: gray, teal, light pink, blue, dark red, green, yellow, orange, purple
    private final String[] statusColorArray = {"#cfcfcf", "#40E0D0", "#FFB6C1", "#00BFFF", "#a32020",
            "#66CD00", "#FFFFa4", "#F5AC0A", "#B23AEE"};
    // array of hex codes for colors of sections: baby yellow, soft red, orange yellow, pastel blue, lavender, pale green
    private final String[] sectionColorArray = {"#ffeead", "#FF6f69", "#FFcc5f", "#baefff", "#e4a8f9", "#b3f9a8"};

    TableAdapter(Context c, ArrayList<Table> theTables) {
        mContext = c;
        tables = theTables;
    }

    public int getCount() {
        return tables.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout wholeTable = new RelativeLayout(mContext);
        // the text view that will go on the table
        TextView text = new TextView(mContext);

        // set size and margins for table button
        LinearLayout.LayoutParams params;
        Table table = tables.get(position);
        if (table.exists()) {
            // set color for section in background
            wholeTable.setBackgroundColor(Color.parseColor(sectionColorArray[table.getServer()]));

            // set table size depending on the number of seats needed
            int seatNum = table.getNumSeats();
            if (seatNum == 0) {
                params = new LinearLayout.LayoutParams(175, 175);
            } else if (seatNum == 1) {
                params = new LinearLayout.LayoutParams(175, 200);
            } else if (seatNum == 2) {
                params = new LinearLayout.LayoutParams(175, 250);
            } else {
                params = new LinearLayout.LayoutParams(175, 300);
            }
            text.setLayoutParams(params);

            // set the text and text attributes on the table
            text.setText(table.getTableID());
            text.setTextSize(40);
            text.setAllCaps(true);
            text.setTextColor(Color.BLACK);
            text.setGravity(Gravity.CENTER);
            // set color of table to previous selection
            int status = table.getTableStatus();
            text.setBackgroundColor(Color.parseColor(statusColorArray[status]));
        }
        // table doesn't exist, so will appear as white space
        else {
            wholeTable.setBackgroundColor(Color.parseColor("#FFFFFF"));

            params = new LinearLayout.LayoutParams(175, 150);
            params.setMargins(20, 20, 20, 20);
            text.setLayoutParams(params);

            text.setTextColor(Color.WHITE);
            // set color of table to previous selection
            text.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        wholeTable.addView(text);
        return wholeTable;
    };
}
