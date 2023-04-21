package data.tables;

import data.DBManager;
import data.QueryIndexer;
import data.queries.*;
import model.Aircraft;
import model.Discrepancy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;

public class DiscrepancyTable extends Table<Discrepancy> {

    public final Column COL_TEXT;
    public final Column COL_TURNOVER;
    public final Column COL_DISC_BY;
    public final Column COL_PARTS_ON_ORDER;
    public final Column COL_STATUS_ID;
    public final Column COL_AIRCRAFT_ID;
    public final Column COL_DATE_COMPLETED;

    private static DiscrepancyTable instance = new DiscrepancyTable();
    public static DiscrepancyTable getInstance() { return instance; }

    private DiscrepancyTable() {
        super("discrepancies");

        COL_TEXT = new Column(this, "text", TEXT);

        COL_TURNOVER = new Column(this, "turnover", TEXT);

        COL_DISC_BY = new Column(this, "crew", TEXT);

        COL_PARTS_ON_ORDER = new Column(this, "parts_on_order", TEXT);

        COL_DATE_COMPLETED = new Column(this, "date_completed", TEXT);

        COL_STATUS_ID = new Column(this,"status_id", INTEGER);

        COL_AIRCRAFT_ID = new Column(this, "aircraft_id", INTEGER, References(AircraftTable.getInstance().COL_ID));
    }

    @Override
    public void setStatementValues(PreparedStatement statement, QueryIndexer indexer, Discrepancy discrepancy) throws SQLException {
        statement.setString(indexer.indexOf(COL_TEXT), discrepancy.getText());
        statement.setString(indexer.indexOf(COL_TURNOVER), discrepancy.getTurnover());
        statement.setString(indexer.indexOf(COL_DISC_BY), discrepancy.getDiscoveredBy());
        statement.setString(indexer.indexOf(COL_PARTS_ON_ORDER), discrepancy.getPartsOnOrder());
        statement.setLong(indexer.indexOf(COL_STATUS_ID), discrepancy.getStatus().getId());
        statement.setLong(indexer.indexOf(COL_AIRCRAFT_ID), discrepancy.getAircraft().getId());

        //date completed may or may not be null
        if(discrepancy.getDateCompleted() != null)
            statement.setString(indexer.indexOf(COL_DATE_COMPLETED), discrepancy.getDateCompleted().toString());
        else
            statement.setString(indexer.indexOf(COL_DATE_COMPLETED), null);

        super.setStatementValues(statement, indexer, discrepancy);
    }

    /**
     * Inflates a Discrepancy from a ResultSet provided from the Table class
     * @param rs
     * @return
     * @throws SQLException
     */
    @Override
    public Discrepancy inflateItemFromResultSet(ResultSet rs) throws SQLException {
        Discrepancy d = new Discrepancy();

        d.setId(rs.getLong(COL_ID.NAME));
        d.setText(rs.getString(COL_TEXT.NAME));
        d.setDiscoveredBy(rs.getString(COL_DISC_BY.NAME));
        d.setTurnover(rs.getString(COL_TURNOVER.NAME));
        d.setDateCreated(Instant.parse(rs.getString(COL_DATE_CREATED.NAME)));
        d.setDateLastEdited(Instant.parse(rs.getString(COL_DATE_EDITED.NAME)));
        d.setPartsOnOrder(rs.getString(COL_PARTS_ON_ORDER.NAME));

        //inflate the status+aircraft and give it to me
        d.setStatus(StatusTable.getInstance().getItemById(rs.getLong(COL_STATUS_ID.NAME)));
        d.setAircraft(AircraftTable.getInstance().getItemById(rs.getLong(COL_AIRCRAFT_ID.NAME)));

        String dateStr = rs.getString(COL_DATE_COMPLETED.NAME);
        try {
            d.setDateCompleted(Instant.parse(dateStr));
        }catch ( DateTimeException ex) {
            d.setDateCompleted(null);
        } catch (NullPointerException ex) {
            d.setDateCompleted(null);
        }

        return d;
    }

    public ArrayList<Discrepancy> getDiscrepanciesForNotes(Aircraft aircraft) throws SQLException {

        Query byAircraft_OnNotes = new Query(this);

        final Column COL_SHOW_ON_NOTES = StatusTable.getInstance().COL_SHOW_ON_NOTES;

        //for pulling all the discrepancies against this aircraft
        Criterion matchesAircraft = new Criterion(COL_AIRCRAFT_ID, aircraft.getId() + "");
        Criterion showOnNotes = new Criterion(COL_SHOW_ON_NOTES, TRUE + "");

        byAircraft_OnNotes.addWhereCriterion(matchesAircraft, AndOr.AND);
        byAircraft_OnNotes.addWhereCriterion(showOnNotes, AndOr.NONE);

        Table statusTable = StatusTable.getInstance();

        JoinClause innerJoin_Status = new JoinClause(JoinType.InnerJoin, statusTable, COL_STATUS_ID, statusTable.COL_ID);
        byAircraft_OnNotes.addJoinClause(innerJoin_Status);

        return query(byAircraft_OnNotes);

    }
}
