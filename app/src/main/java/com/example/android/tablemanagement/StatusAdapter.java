package com.example.android.tablemanagement;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StatusAdapter extends BaseAdapter {
    private Context mContext;
    // array of hex codes for colors: gray, teal, pink, blue, red, green, yellow, orange, purple
    private final String[] colorsArray = {"#cfcfcf", "#40E0D0", "#FFB6C1", "#00BFFF", "#a32020",
            "#66CD00", "#FFFFa4", "#F5AC0A", "#B23AEE"};

    StatusAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return colorsArray.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] statusArray = mContext.getResources().getStringArray(R.array.status_array);
        // create the text showing the status legend
        LinearLayout line = new LinearLayout(mContext);
        line.setOrientation(LinearLayout.HORIZONTAL);

        // show a sample of the color
        Button colorExample = new Button(mContext);
        colorExample.setText("");
        colorExample.setTextSize(20);
        colorExample.setPadding(5, 5, 5, 5);
        colorExample.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
        colorExample.setBackgroundColor(Color.parseColor(colorsArray[position]));
        line.addView(colorExample);

        TextView text = new TextView(mContext);
        text.setText(statusArray[position]);
        text.setPadding(5, 5, 5, 5);
        text.setTextSize(20);
        line.addView(text);

        return line;
    }
}
