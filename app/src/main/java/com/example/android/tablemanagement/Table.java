package com.example.android.tablemanagement;

class Table {
    private int tableStatus, seatNum;
    private String tableID = "";
    private boolean exists;
    private int server;

    Table(String theTableID, int numSeats) {tableID=theTableID;seatNum=numSeats;exists=true;}

    String getTableID() {
        return tableID;
    }

    void setTableID(String table) {
        tableID = table;
    }

    int getTableStatus() {
        return tableStatus;
    }

    void setTableStatus(int status) {
        tableStatus = status;
    }

    void setSeatNum(int numSeats) {seatNum = numSeats;}

    int getNumSeats() { return seatNum; }

    boolean exists() {
        return exists;
    }

    void deleteTable() { exists = false; }

    int getServer() { return server; };

    void setServer(int serverNum) { server = serverNum; }
}
