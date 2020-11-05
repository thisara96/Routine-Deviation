package com.example.espapp10;

public class OutlierDataModel {
    private String room;
    private String time;

    public OutlierDataModel() {
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTime() {
        String separator ="T";
        int sepPos = time.indexOf(separator);
        return time.substring(sepPos + separator.length());
    }

    public void setTime(String time) {
        this.time = time;
    }
}
