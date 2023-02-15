package data.tables;

import data.QueryIndexer;
import model.Aircraft;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class AircraftTable extends Table<Aircraft> {

    private static final AircraftTable instance = new AircraftTable();
    public static final AircraftTable getInstance() {return instance; }

    public final Column COL_TAIL_NUM = new Column(this, "tail_num", TEXT, NOT_NULL);
    public final Column COL_ENABLED = new Column(this, "enabled", BOOL, NOT_NULL);

    public AircraftTable() {
        super("aircraft");

        addColumn(COL_TAIL_NUM);
        addColumn(COL_ENABLED);
    }

    @Override
    public Aircraft inflateItemFromResultSet(ResultSet rs) throws SQLException {
        Aircraft aircraft = new Aircraft(
                rs.getLong(COL_ID.NAME),
                Instant.parse(rs.getString(COL_DATE_CREATED.NAME)),
                Instant.parse(rs.getString(COL_DATE_EDITED.NAME)),
                rs.getString(COL_TAIL_NUM.NAME),
                rs.getBoolean(COL_ENABLED.NAME));

        return aircraft;
    }

    @Override
    public void setStatementValues(PreparedStatement statement, QueryIndexer indexer, Aircraft item) throws SQLException {
        super.setStatementValues(statement, indexer, item);

        statement.setString(indexer.indexOf(COL_TAIL_NUM), item.getTailNumber());
        statement.setBoolean(indexer.indexOf(COL_ENABLED), item.isEnabled());
    }
}
