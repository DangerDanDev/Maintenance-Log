package data.queries;

import data.tables.Column;

/**
 * A wrapper class that matches a column to its specified values in a query
 */
public class Criterion {

    public final Column COLUMN;
    public final String VALUE;

    public Criterion(Column col, String val) {
        this.COLUMN = col;
        this.VALUE = val;
    }

}
