package data.tables;

import data.DatabaseObject;
import model.Discrepancy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class DiscrepancyTable extends Table<Discrepancy> {

    public final Column COL_TEXT;
    public final Column COL_TURNOVER;
    public final Column COL_CREW;
    public final Column COL_PARTS_ON_ORDER;

    private static DiscrepancyTable instance = new DiscrepancyTable();
    public static DiscrepancyTable getInstance() { return instance; }

    private DiscrepancyTable() {
        super("discrepancies");

        COL_TEXT = new Column("text", TEXT);
        addColumn(COL_TEXT);

        COL_TURNOVER = new Column("turnover", TEXT);
        addColumn(COL_TURNOVER);

        COL_CREW = new Column( "crew", TEXT);
        addColumn(COL_CREW);

        COL_PARTS_ON_ORDER = new Column( "parts_on_order", TEXT);
        addColumn(COL_PARTS_ON_ORDER);
    }

    @Override
    public void setStatementValues(PreparedStatement statement, QueryIndexer indexer, Discrepancy discrepancy) throws SQLException {
        statement.setString(indexer.indexOf(COL_TEXT), discrepancy.getText());
        statement.setString(indexer.indexOf(COL_TURNOVER), discrepancy.getTurnover());
        statement.setString(indexer.indexOf(COL_CREW), discrepancy.getCrew());
        statement.setString(indexer.indexOf(COL_PARTS_ON_ORDER), discrepancy.getPartsOnOrder());

        super.setStatementValues(statement, indexer, discrepancy);
    }

    @Override
    public Discrepancy getItemFromResultSet(ResultSet rs) throws SQLException {
        Discrepancy d = new Discrepancy();

        d.setId(rs.getLong(COL_ID.NAME));
        d.setText(rs.getString(COL_TEXT.NAME));
        d.setCrew(rs.getString(COL_CREW.NAME));
        d.setTurnover(rs.getString(COL_TURNOVER.NAME));
        d.setDateCreated(Instant.parse(rs.getString(COL_DATE_CREATED.NAME)));
        d.setDateLastEdited(Instant.parse(rs.getString(COL_DATE_EDITED.NAME)));
        d.setPartsOnOrder(rs.getString(COL_PARTS_ON_ORDER.NAME));
        d.setSaved(true); //we just pulled it from the database so it's obviously saved

        return d;
    }
}
