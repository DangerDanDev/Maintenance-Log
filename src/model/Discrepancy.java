package model;

import data.DatabaseObject;

public class Discrepancy extends DatabaseObject {

    private String text = "";

    private String turnover = "";

    private String discoveredBy = "";

    private String partsOnOrder = "";


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
}
