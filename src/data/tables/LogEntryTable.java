package data.tables;

import data.DBManager;
import data.QueryIndexer;
import model.Discrepancy;
import model.LogEntry;

import java.sql.Connection;
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
    public final Column COL_PARENT_DISCREPANCY;
    public final Column COL_SHOW_ON_NOTES = new Column(this, "show_on_notes", BOOL, "");

    private LogEntryTable() {
        super("log_entries");

        COL_PARENT_DISCREPANCY = new Column(this, "parent_discrepancy", INTEGER, References(DiscrepancyTable.getInstance().COL_ID));

        COL_NARRATIVE = new Column(this,"narrative", TEXT);

        COL_CREW = new Column(this,"crew", TEXT);
    }



    @Override
    public LogEntry inflateItemFromResultSet(ResultSet rs) throws SQLException {

        long discrepancyId = rs.getLong(COL_PARENT_DISCREPANCY.NAME);

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

        statement.setLong(indexer.indexOf(COL_PARENT_DISCREPANCY), item.getParentDiscrepancy().getId());
        statement.setString(indexer.indexOf(COL_NARRATIVE), item.getNarrative());
        statement.setString(indexer.indexOf(COL_CREW), item.getCrew());
        statement.setBoolean(indexer.indexOf(COL_SHOW_ON_NOTES), item.isShowOnNotes());
    }

    public ArrayList<LogEntry> getLogEntriesAgainstDiscrepancy(Discrepancy d, QueryType onNotesOnly) {
        return getLogEntriesAgainstDiscrepancy(d.getId(), onNotesOnly);
    }

    public ArrayList<LogEntry> getLogEntriesAgainstDiscrepancy(long discrepancyId, QueryType onNotesOnly) {

        ArrayList<LogEntry> logEntries = new ArrayList<>();

        QueryIndexer idx = new QueryIndexer();

        String QUERY = " SELECT * FROM " + NAME +
                WHERE + COL_PARENT_DISCREPANCY + "=" + idx.index(COL_PARENT_DISCREPANCY);

        //TOOD: if onNotesOnly, append a check to the query to only show log entries
        //with onNotes enabled
        if(onNotesOnly == QueryType.ON_NOTES_ONLY)
            QUERY += AND + COL_SHOW_ON_NOTES + "=" + idx.index(COL_SHOW_ON_NOTES);

        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(QUERY)) {

            ps.setLong(idx.indexOf(COL_PARENT_DISCREPANCY), discrepancyId);

            if(onNotesOnly == QueryType.ON_NOTES_ONLY)
                ps.setBoolean(idx.indexOf(COL_SHOW_ON_NOTES), true);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logEntries.add(getItemFromResultSet(rs));
                }
            }

        }catch (SQLException ex) {

        }

        return logEntries;
    }

}
