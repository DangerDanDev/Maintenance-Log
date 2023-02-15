package model;

import data.DatabaseObject;

import javax.swing.*;
import java.time.Instant;

public class Aircraft extends DatabaseObject {
    private String tailNumber = "";

    /**
     * Whether or not this tail number is to be
     * shown on the Notes and StatusBoard pages
     */
    private boolean enabled = true;

    public Aircraft() {
        super();
    }

    public Aircraft(long id, Instant dateCreated, Instant dateLastEdited, String tailNum, boolean enabled) {
        super(id, dateCreated, dateLastEdited);

        setTailNumber(tailNum);
        setEnabled(enabled);
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return getTailNumber();
    }
}
