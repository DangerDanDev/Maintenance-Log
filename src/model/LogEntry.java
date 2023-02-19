package model;

import data.DatabaseObject;

public class LogEntry extends DatabaseObject {
    private String narrative = "";

    private String crew = "";

    private Discrepancy parentDiscrepancy = null;

    boolean showOnNotes = true;

    public LogEntry(Discrepancy d, long id, String narrative, String crew, boolean showOnNotes) {
        setParentDiscrepancy(d);
        setNarrative(narrative);
        setCrew(crew);
        setShowOnNotes(showOnNotes);
        setId(id);
    }

    public LogEntry(Discrepancy d) {
        this(d, INVALID_ID, "", "", true);
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

    public boolean isShowOnNotes() {
        return showOnNotes;
    }

    public void setShowOnNotes(boolean showOnNotes) {
        this.showOnNotes = showOnNotes;
    }
}
