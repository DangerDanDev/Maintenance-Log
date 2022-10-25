package data;

import java.sql.Date;
import java.time.Instant;

public class Discrepancy {
    private long id = -1;
    private String tailNum;
    private String narrative;
    private Instant dateCreated;
    private String turnover;
    private String partsOnOrder;

    private Status status;

    public Discrepancy() {
        this(-1, "", "", Instant.now(), "", "", null);
    }

    public Discrepancy(long id, String tailNum, String narrative, Instant dateCreated, String turnover, String partsOnOrder, Status status) {
        setId(id);
        setTailNum(tailNum);
        setNarrative(narrative);
        setDateCreated(dateCreated);
        setTurnover(turnover);
        setPartsOnOrder(partsOnOrder);

        setStatus(status);
    }



    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTailNum() {
        return tailNum;
    }

    public void setTailNum(String tailNum) {
        this.tailNum = tailNum;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getPartsOnOrder() {
        return partsOnOrder;
    }

    public void setPartsOnOrder(String partsOnOrder) {
        this.partsOnOrder = partsOnOrder;
    }

}
