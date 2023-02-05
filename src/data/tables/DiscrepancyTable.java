package data.tables;

import model.Discrepancy;

public class DiscrepancyTable<Discrepancy> extends Table {

    public final Column COL_TEXT;
    public final Column COL_TURNOVER;
    public final Column COL_CREW;
    public final Column COL_PARTS_ON_ORDER;

    public DiscrepancyTable() {
        super("discrepancies");

        COL_TEXT = new Column(this, "text", TEXT);
        addColumn(COL_TEXT);

        COL_TURNOVER = new Column(this, "turnover", TEXT);
        addColumn(COL_TURNOVER);

        COL_CREW = new Column(this, "crew", TEXT);
        addColumn(COL_CREW);

        COL_PARTS_ON_ORDER = new Column(this, "parts_on_order", TEXT);
        addColumn(COL_PARTS_ON_ORDER);
    }
}
