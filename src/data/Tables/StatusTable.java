package data.Tables;

import data.Column;

public class StatusTable extends Table {

    private static final StatusTable statusTable = new StatusTable();
    public static StatusTable get() { return statusTable; }

    public static final Column COL_TITLE = new Column("Title", TEXT);
    public static final Column COL_ABBREVIATION = new Column("Abbreviation", TEXT);

    protected StatusTable() {
        addColumn(COL_TITLE);
        addColumn(COL_ABBREVIATION);
    }

    @Override
    public String getName() {
        return "status_table";
    }
}
