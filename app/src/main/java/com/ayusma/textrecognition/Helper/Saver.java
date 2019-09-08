package com.ayusma.textrecognition.Helper;

public class Saver {

    public Saver() {
    }


    public Saver(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    private  int id;
    private  String text;


    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
