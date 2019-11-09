package com.example.android.tablemanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewWaitlist extends AppCompatActivity {

    private ArrayList<Customer> waitlist;
    private Button doneButton, deleteButton, seatButton;
    private EditText customerName, partyNum, customerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_waitlist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get waitlist from shared preferences
        waitlist = loadSharedPreferencesWaitlist(this);

        // create adapter to format customers for the list
        final CustomerAdapter adapter = new CustomerAdapter(this, waitlist);
        // create list view to associate with adapter
        ListView listView = findViewById(R.id.theList);
        listView.setAdapter(adapter);

        // set an onClick for each listView item
        setListOnClick(listView, adapter);
        // set onClick for the fab (allows user to add customers)
        fabOnClick(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // save waitlist to shared preferences if activity is closing.
        onPause();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            saveSharedPreferencesWaitlist(this, waitlist);
        }
    }

    public static void saveSharedPreferencesWaitlist(Context context, List<Customer> waitlist) {
        SharedPreferences wPrefs = context.getSharedPreferences("waitlistPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = wPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(waitlist);
        prefsEditor.putString("waitlistP", json);
        prefsEditor.apply();
    }

    // get waitlist from shared preferences
    public static ArrayList<Customer> loadSharedPreferencesWaitlist(Context context) {
        ArrayList<Customer> waitlist;
        SharedPreferences wPrefs = context.getSharedPreferences("waitlistPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = wPrefs.getString("waitlistP", "");
        if (json.isEmpty()) {
            waitlist = new ArrayList<Customer>();
        } else {
            Type type = new TypeToken<List<Customer>>(){}.getType();
            waitlist = gson.fromJson(json, type);
        }
        return waitlist;
    }

    private void setListOnClick(ListView listView, final CustomerAdapter ca) {
        // whenever you click on list item...
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.activity_edit_waitlist, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                Customer customer = ca.getItem(position);
                // dismiss the popup window when DONE is clicked and save new customer.
                doneButton = popupView.findViewById(R.id.edit_done_button);
                doneButton.setTag(customer);
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Customer cust = (Customer) v.getTag();
                        customerName = (EditText) popupView.findViewById(R.id.edit_customer_name);
                        partyNum = (EditText) popupView.findViewById(R.id.edit_party_num);
                        //customerTime = (EditText) popupView.findViewById(R.id.edit_time_arrival);

                        String name = customerName.getText().toString();
                        String num = partyNum.getText().toString();
                        //String time = customerTime.getText().toString();

                        if (!name.equals("")){
                            cust.setName(name);
                            ca.notifyDataSetChanged();
                        }
                        if (!num.equals("")){
                            cust.setPartyNum(num);
                            //cust.setTime(time);
                            ca.notifyDataSetChanged();
                        }
                        popupWindow.dismiss();
                    }
                });

                // dismiss the popup window when DELETE is clicked and delete that customer.
                deleteButton = popupView.findViewById(R.id.edit_delete_button);
                deleteButton.setTag(customer);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Customer cust = (Customer) v.getTag();
                        ca.remove(cust);
                        popupWindow.dismiss();
                    }
                });

                // dismiss popup window when SEAT is clicked and start floor activity.
                seatButton = popupView.findViewById(R.id.edit_seat_button);
                seatButton.setTag(customer);
                seatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Customer cust = (Customer) v.getTag();
                        ca.remove(cust);
                        popupWindow.dismiss();

                        Intent intent = new Intent(ViewWaitlist.this, ViewFloor.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void fabOnClick(final CustomerAdapter ca) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.activity_add_to_waitlist, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                // dismiss popup window when DONE is clicked and save new customer.
                doneButton = popupView.findViewById(R.id.settings_done_button);
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customerName = (EditText) popupView.findViewById(R.id.add_customer_name);
                        partyNum = (EditText) popupView.findViewById(R.id.add_party_num);
                        customerTime = (EditText) popupView.findViewById(R.id.edit_time_arrival);

                        String name = customerName.getText().toString();
                        String num = partyNum.getText().toString();
                        String time = customerTime.getText().toString();

                        if ((!name.equals("")) && (!num.equals(""))) {
                            ca.add(new Customer(name, num));
                            ca.notifyDataSetChanged();
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }
}