package data.tables;

import data.DatabaseObject;
import model.Discrepancy;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DiscrepancyTable extends Table<Discrepancy> {

    public final Column COL_TEXT;
    public final Column COL_TURNOVER;
    public final Column COL_CREW;
    public final Column COL_PARTS_ON_ORDER;

    private static DiscrepancyTable instance = new DiscrepancyTable();
    public static DiscrepancyTable getInstance() { return instance; }

    private DiscrepancyTable() {
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

    @Override
    public void setStatementValues(PreparedStatement statement, QueryIndexer indexer, Discrepancy discrepancy) throws SQLException {
        super.setStatementValues(statement, indexer, discrepancy);

        statement.setString(indexer.indexOf(COL_TEXT), discrepancy.getText());
        statement.setString(indexer.indexOf(COL_TURNOVER), discrepancy.getTurnover());
        statement.setString(indexer.indexOf(COL_CREW), discrepancy.getCrew());
        statement.setString(indexer.indexOf(COL_PARTS_ON_ORDER), discrepancy.getPartsOnOrder());
    }
}
