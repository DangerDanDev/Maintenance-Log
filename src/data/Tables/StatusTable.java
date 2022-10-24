package data.Tables;

import data.Column;

public class StatusTable extends Table {

    public static final Column COL_TITLE = new Column("title", TEXT);
    public static final Column COL_ABBREVIATION = new Column("abbreviation", TEXT);

    private static final StatusTable statusTable = new StatusTable();
    public static StatusTable get() { return statusTable; }

    protected StatusTable() {
        super();
        addColumn(COL_TITLE);
        addColumn(COL_ABBREVIATION);
    }

    @Override
    public String getName() {
        return "status_table";
    }
}
