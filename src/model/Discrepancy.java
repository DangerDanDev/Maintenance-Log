package model;

import data.DatabaseObject;

import java.time.Instant;

public class Discrepancy extends DatabaseObject {

    private String text = "";

    private String turnover = "";

    private String discoveredBy = "";

    private String partsOnOrder = "";

    private Status status;

    private Aircraft aircraft;


    public Discrepancy() {
        super();
    }

    public Discrepancy(long id, Instant dateCreated, Instant dateEdited, String text, String turnover,
                       String discoveredBy, String partsOnOrder, Status status, Aircraft aircraft) {
        super(id, dateCreated, dateEdited);

        setText(text);
        setTurnover(turnover);
        setDiscoveredBy(discoveredBy);
        setPartsOnOrder(partsOnOrder);
        setStatus(status);
        setAircraft(aircraft);
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getDiscoveredBy() {
        return discoveredBy;
    }

    public void setDiscoveredBy(String discoveredBy) {
        this.discoveredBy = discoveredBy;
    }

    public String getPartsOnOrder() {
        return partsOnOrder;
    }

    public void setPartsOnOrder(String partsOnOrder) {
        this.partsOnOrder = partsOnOrder;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }
}
