package com.example.espapp10;

public class TodayDataModel {
    private  String D;
    private String R;

    public TodayDataModel() {
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public TodayDataModel(String d, String r) {
        D = d;
        R = r;
    }

    public String getR() {
        return R;
    }

    public void setR(String r) {
        R = r;
    }
}
