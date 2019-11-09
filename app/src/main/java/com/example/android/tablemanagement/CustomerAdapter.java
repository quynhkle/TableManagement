package com.example.android.tablemanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomerAdapter extends ArrayAdapter<Customer> {
    CustomerAdapter(Context context, ArrayList<Customer> customers) {
        super(context, 0, customers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Customer customer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_layout, parent, false);
        }

        TextView lineNum = (TextView) convertView.findViewById(R.id.line_number);

        lineNum.setText(Integer.toString(position+1));

        TextView customerName = (TextView) convertView.findViewById(R.id.customer_name);
        customerName.setText(customer.name);

        TextView partyNum = (TextView) convertView.findViewById(R.id.party_num);
        String partyNumText = "Party of: " + customer.partyNum;
        partyNum.setText(partyNumText);

        return convertView;
    }
}