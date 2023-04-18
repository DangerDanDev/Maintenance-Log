package data;

import data.queries.Criterion;
import data.tables.Column;
import data.tables.Table;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class that simplifies tracking the "?" indexes in JDBC PreparedStatements
 */
public class QueryIndexer {

    /**
     * Obvs we start at the first index
     */
    private int currentIndex = 1;

    /**
     *
     */
    private HashMap<Column, Integer> values = new HashMap<>();

    /**
     * Adds and indexes a column
     * @param c
     * @return "?" so it can be used inline in SQL PreparedStatements
     */
    public String index(Column c) {
        values.put(c, currentIndex);

        currentIndex++;
        return "?";
    }

    /**
     * Adds and indexes a criterion's column
     * @param c
     * @return
     */
    public String index(Criterion c) {
        return index(c.COLUMN);
    }

    /**
     * Returns the index of a column
     * @param c
     * @return
     */
    public int indexOf(Column c) {
        return values.get(c);
    }

    public int indexOf(Criterion c) {
        return values.get(c.COLUMN);
    }

    public int getTotalIndexes() {
        return currentIndex;
    }

}
