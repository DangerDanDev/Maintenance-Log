package model;

import data.DatabaseObject;

public class LogEntry extends DatabaseObject {
    private String narrative = "";

    private String crew = "";

    private Discrepancy parentDiscrepancy = null;

    public LogEntry(Discrepancy d, long id, String narrative, String crew) {
        setParentDiscrepancy(d);
        setNarrative(narrative);
        setCrew(crew);
        setId(id);
    }

    public LogEntry(Discrepancy d, String narrative, String crew) {
        this(d, INVALID_ID, narrative, crew);
    }

    public LogEntry() {

    }

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