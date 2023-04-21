package data.tables;

import data.QueryIndexer;
import data.queries.AndOr;
import data.queries.Criterion;
import data.queries.Query;
import model.Discrepancy;
import model.LogEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

public class LogEntryTable extends Table<LogEntry> {

    private static final LogEntryTable instance = new LogEntryTable();
    public static final LogEntryTable getInstance() { return instance; }

    public enum QueryType {
        ALL_ENTRIES,
        ON_NOTES_ONLY,
    }

    public final Column COL_NARRATIVE;
    public final Column COL_CREW;
    public final Column COL_PARENT_DISCREPANCY_ID;
    public final Column COL_SHOW_ON_NOTES = new Column(this, "show_on_notes", BOOL, "");

    private LogEntryTable() {
        super("log_entries");

        COL_PARENT_DISCREPANCY_ID = new Column(this, "parent_discrepancy", INTEGER, References(DiscrepancyTable.getInstance().COL_ID));

        COL_NARRATIVE = new Column(this,"narrative", TEXT);

        COL_CREW = new Column(this,"crew", TEXT);
    }



    @Override
    public LogEntry inflateItemFromResultSet(ResultSet rs) throws SQLException {

        long discrepancyId = rs.getLong(COL_PARENT_DISCREPANCY_ID.NAME);

        LogEntry logEntry = new LogEntry(
                DiscrepancyTable.getInstance().getItemById(discrepancyId),
                rs.getLong(COL_ID.NAME),
                rs.getString(COL_NARRATIVE.NAME),
                rs.getString(COL_CREW.NAME),
                rs.getBoolean(COL_SHOW_ON_NOTES.NAME)
        );

        logEntry.setDateCreated(Instant.parse(rs.getString(COL_DATE_CREATED.NAME)));
        logEntry.setDateLastEdited(Instant.parse(rs.getString(COL_DATE_EDITED.NAME)));

        return logEntry;
    }

    @Override
    public void setStatementValues(PreparedStatement statement, QueryIndexer indexer, LogEntry item) throws SQLException {
        super.setStatementValues(statement, indexer, item);

        statement.setLong(indexer.indexOf(COL_PARENT_DISCREPANCY_ID), item.getParentDiscrepancy().getId());
        statement.setString(indexer.indexOf(COL_NARRATIVE), item.getNarrative());
        statement.setString(indexer.indexOf(COL_CREW), item.getCrew());
        statement.setBoolean(indexer.indexOf(COL_SHOW_ON_NOTES), item.isShowOnNotes());
    }

    public ArrayList<LogEntry> getLogEntriesAgainstDiscrepancy(Discrepancy d, QueryType onNotesOnly) throws SQLException {
        return getLogEntriesAgainstDiscrepancy(d.getId(), onNotesOnly);
    }

    /**
     * Queries all log entries against the given discrepancy, or if selected, pulls only the ones that should be on the notes
     * @param discrepancyId
     * @param onNotesOnly
     * @return
     * @throws SQLException
     */
    public ArrayList<LogEntry> getLogEntriesAgainstDiscrepancy(long discrepancyId, QueryType onNotesOnly) throws SQLException {

        Query logEntriesAgainstDiscrepancyQuery = new Query(this);

        Criterion logEntryIdMatchesDiscrepancyId = new Criterion(COL_PARENT_DISCREPANCY_ID, discrepancyId + "");
        Criterion onNotesOnlyCriteria = new Criterion(COL_SHOW_ON_NOTES, TRUE + "");

        if(onNotesOnly == QueryType.ON_NOTES_ONLY)
            logEntriesAgainstDiscrepancyQuery.addWhereCriterion(onNotesOnlyCriteria, AndOr.AND);

        logEntriesAgainstDiscrepancyQuery.addWhereCriterion(logEntryIdMatchesDiscrepancyId, AndOr.NONE);

        return query(logEntriesAgainstDiscrepancyQuery);
    }

}
