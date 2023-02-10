package data.tables;

import data.QueryIndexer;
import model.LogEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogEntryTable extends Table<LogEntry> {

    public final Column COL_NARRATIVE;
    public final Column COL_CREW;
    public final Column COL_PARENT_DISCREPANCY;

    private LogEntryTable() {
        super("log_entries");

        COL_PARENT_DISCREPANCY = new Column(this, "parent_discrepancy", INTEGER, References(DiscrepancyTable.getInstance().COL_ID));

        COL_NARRATIVE = new Column(this,"narrative", TEXT);
        addColumn(COL_NARRATIVE);

        COL_CREW = new Column(this,"crew", TEXT);
        addColumn(COL_CREW);
    }

    @Override
    public LogEntry getItemFromResultSet(ResultSet rs) throws SQLException {
        //TODO: inflate me from my parent discrepancy, narrative, and crew fields
        return null;
    }

    @Override
    public void setStatementValues(PreparedStatement statement, QueryIndexer indexer, LogEntry item) throws SQLException {
        super.setStatementValues(statement, indexer, item);

        //TODO: set my narrative, crew, and (if applicable) parent discrepancy fields
    }
}
