package data;

import data.Tables.StatusTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static data.Tables.StatusTable.COL_TITLE;
import static data.Tables.StatusTable.COL_ABBREVIATION;
import static data.Tables.Table.COL_ID;

/**
 * Discrepancy Status - Determines how or if a discrepancy is displayed on the notes
 */
public class Status {
    private long id = -1;
    private String Title;
    private String abbreviation;

    public Status() {

    }

    public Status(long id, String title, String abbreviation) {
        setId(id);
        setTitle(title);
        setAbbreviation(abbreviation);
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return getAbbreviation();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Status)
            return ((Status) obj).getId() == this.getId();

        return super.equals(obj);
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
