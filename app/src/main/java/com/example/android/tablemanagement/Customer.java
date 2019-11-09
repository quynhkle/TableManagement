package com.example.android.tablemanagement;

public class Customer {
    public String name = "";
    public String partyNum = "";
    //public String time = "";

    public Customer(String name, String partyNum) {
        this.name = name;
        this.partyNum = partyNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPartyNum (String partyNum) {
        this.partyNum = partyNum;
    }

//    public void setTime (String time) {
//        this.time = time;
//    }
}
