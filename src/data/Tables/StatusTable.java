package data.Tables;

import data.Column;

public class StatusTable extends Table {

    public static final Column COL_TITLE = new Column("Title", TEXT);
    public static final Column COL_ABBREVIATION = new Column("Abbreviation", TEXT);

    public StatusTable() {
        addColumn(COL_TITLE);
        addColumn(COL_ABBREVIATION);
    }

    @Override
    public String getName() {
        return "status_table";
    }
}
