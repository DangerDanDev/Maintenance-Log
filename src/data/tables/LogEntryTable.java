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

    public final Column COL_NARRATIVE;
    public final Column COL_CREW;
    public final Column COL_PARENT_DISCREPANCY;

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
                true //logEntry.isShowOnNotes()
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
    }

    public ArrayList<LogEntry> getLogEntriesAgainstDiscrepancy(Discrepancy d) {
        return getLogEntriesAgainstDiscrepancy(d.getId());
    }

    public ArrayList<LogEntry> getLogEntriesAgainstDiscrepancy(long discrepancyId) {
        ArrayList<LogEntry> logEntries = new ArrayList<>();

        final String QUERY = " SELECT * FROM " + NAME +
                WHERE + COL_PARENT_DISCREPANCY + "=?";

        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(QUERY)) {

            ps.setLong(1, discrepancyId);

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
