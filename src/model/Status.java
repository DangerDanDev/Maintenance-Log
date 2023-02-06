package model;

import data.DatabaseObject;

import java.awt.*;

public class Status extends DatabaseObject {

    private String title = "";

    private Color color = Color.WHITE;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
