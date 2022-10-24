package data.Tables;

import data.Column;
import data.Discrepancy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A singleton table instance
 */
public class DiscrepancyTable extends Table {

    private static DiscrepancyTable discrepancyTable = new DiscrepancyTable();
    public static DiscrepancyTable get() { return discrepancyTable; }

    public static final Column COL_TAIL_NUM = new Column("tail_num", TEXT);
    public static final Column COL_NARRATIVE = new Column("narrative", TEXT);
    public static final Column COL_DATE_CREATED = new Column("date_created", INTEGER);
    public static final Column COL_TURNOVER = new Column("turnover", TEXT);
    public static final Column COL_PARTS_ON_ORDER = new Column("parts_on_order", TEXT);
    //public static final Column COL_STATUS = new Column("Status", INTEGER + References())

    protected DiscrepancyTable () {
        super();

        addColumn(COL_TAIL_NUM);
        addColumn(COL_NARRATIVE);
        addColumn(COL_DATE_CREATED);
        addColumn(COL_TURNOVER);
        addColumn(COL_PARTS_ON_ORDER);
    }

    @Override
    public String getName() {
        return "discrepancies";
    }
}
