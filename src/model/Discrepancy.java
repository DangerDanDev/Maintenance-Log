package model;

import data.DatabaseObject;

public class Discrepancy extends DatabaseObject {
    private String text = "";

    private String turnover = "";

    private String crew = "";

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

    public String getCrew() {
        return crew;
    }

    public void setCrew(String crew) {
        this.crew = crew;
    }

    public String getPartsOnOrder() {
        return partsOnOrder;
    }

    public void setPartsOnOrder(String partsOnOrder) {
        this.partsOnOrder = partsOnOrder;
    }
}
