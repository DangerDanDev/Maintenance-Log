package data.tables;

import data.DBManager;
import data.QueryIndexer;
import model.Discrepancy;
import model.LogEntry;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        addColumn(COL_PARENT_DISCREPANCY);

        COL_NARRATIVE = new Column(this,"narrative", TEXT);
        addColumn(COL_NARRATIVE);

        COL_CREW = new Column(this,"crew", TEXT);
        addColumn(COL_CREW);
    }



    @Override
    public LogEntry getItemFromResultSet(ResultSet rs) throws SQLException {

        long discrepancyId = rs.getLong(COL_PARENT_DISCREPANCY.NAME);

        LogEntry logEntry = new LogEntry(
                DiscrepancyTable.getInstance().getItemById(discrepancyId),
                rs.getLong(COL_ID.NAME),
                rs.getString(COL_NARRATIVE.NAME),
                rs.getString(COL_CREW.NAME)
        );

        logEntry.setSaved(true);
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

        try {
           ArrayList<LogEntry> logEntries = getAllItems();

            return logEntries;
        } catch (SQLException ex) {

        }

        return null;
    }

    public static void main(String[] args) {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            LogEntry entry = new LogEntry(DiscrepancyTable.getInstance().getItemById(15), "Test log entry", "swing shifffft");
            LogEntryTable.getInstance().addItem(entry);

            entry.setNarrative("edited test log entry");

            LogEntryTable.getInstance().updateItem(entry);

            //for(LogEntry entry : getInstance().getLogEntriesAgainstDiscrepancy(15))
            //    JOptionPane.showMessageDialog(null, entry.getNarrative());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
