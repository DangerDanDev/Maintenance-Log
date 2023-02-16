package model;

import data.DatabaseObject;

import java.awt.*;

public class Status extends DatabaseObject {

    private String title = "";

    private Color color = Color.WHITE;

    private boolean showOnNotes = true;

    private boolean completesJob = false;


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

    public boolean isShowOnNotes() {
        return showOnNotes;
    }

    public void setShowOnNotes(boolean showOnNotes) {
        this.showOnNotes = showOnNotes;
    }

    public boolean isCompletesJob() {
        return completesJob;
    }

    public void setCompletesJob(boolean completesJob) {
        this.completesJob = completesJob;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
