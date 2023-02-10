package model;

import data.DatabaseObject;

public class LogEntry extends DatabaseObject {
    private String narrative = "";

    private String crew = "";

    private Discrepancy parentDiscrepancy = null;

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public String getCrew() {
        return crew;
    }

    public void setCrew(String crew) {
        this.crew = crew;
    }

    public Discrepancy getParentDiscrepancy() {
        return parentDiscrepancy;
    }

    public void setParentDiscrepancy(Discrepancy parentDiscrepancy) {
        this.parentDiscrepancy = parentDiscrepancy;
    }
}
