package data.Tables;

import data.Column;
import data.DatabaseManager;
import data.Discrepancy;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

/**
 * A singleton table instance
 */
public class DiscrepancyTable extends Table {

    public static final Column COL_TAIL_NUM = new Column("tail_num", TEXT, NOT_NULL + References(AircraftTable.get(), AircraftTable.COL_TAIL_NUM));
    public static final Column COL_NARRATIVE = new Column("narrative", TEXT, "");
    public static final Column COL_DATE_CREATED = new Column("date_created", INTEGER, "");
    public static final Column COL_TURNOVER = new Column("turnover", TEXT, "");
    public static final Column COL_PARTS_ON_ORDER = new Column("parts_on_order", TEXT, "");
    public static final Column COL_STATUS = new Column("Status", INTEGER,  NOT_NULL + References(StatusTable.get(), StatusTable.COL_ID));

    private static DiscrepancyTable discrepancyTable = new DiscrepancyTable();
    public static DiscrepancyTable get() { return discrepancyTable; }

    protected DiscrepancyTable () {
        super();

        addColumn(COL_TAIL_NUM);
        addColumn(COL_NARRATIVE);
        addColumn(COL_DATE_CREATED);
        addColumn(COL_TURNOVER);
        addColumn(COL_PARTS_ON_ORDER);
        addColumn(COL_STATUS);
    }

    @Override
    public String getName() {
        return "discrepancies";
    }

    /**
     *
     * @param rs
     * @return A discrepancy from the result set
     * @throws SQLException
     */
    public static Discrepancy getDiscrepancyFromResultSet(ResultSet rs) throws SQLException {
        return new Discrepancy(
                rs.getLong(COL_ID.NAME),
                rs.getString(COL_TAIL_NUM.NAME),
                rs.getString(COL_NARRATIVE.NAME),
                Instant.parse(rs.getString(COL_DATE_CREATED.NAME)),
                rs.getString(COL_TURNOVER.NAME),
                rs.getString(COL_PARTS_ON_ORDER.NAME),
                StatusTable.getStatusFromResultSet(rs)
        );
    }

    public static int updateDiscrepancyInDatabase(Connection conn, Discrepancy disc) throws SQLException {
        String UPDATE_DISCREPANCIES = " UPDATE " + get().getName() + " ";

        //TODO: Figure out a workaround for having the "'" + "'" snippets in the update query,
        //since that is a very poor method of getting spaces into an update
        String SET_DISCREPANCY_VALUES = " SET " +
                //getUpdateRowStringAddComma(COL_ID, disc.getId() + "") +
                        getUpdateRowStringAddComma(COL_TAIL_NUM, disc.getTailNum()) +
                        getUpdateRowStringAddComma(COL_NARRATIVE, "'" + disc.getNarrative() + "'") +
                        getUpdateRowStringAddComma(COL_DATE_CREATED, "'" + disc.getDateCreated().toString() + "'" ) +
                        getUpdateRowStringAddComma(COL_TURNOVER, "'" + disc.getTurnover() + "'") +
                        getUpdateRowStringAddComma(COL_PARTS_ON_ORDER, "'" + disc.getPartsOnOrder() + "'") +
                        getUpdateRowString(COL_STATUS, disc.getStatus().getId() + " ");

        /*String SET_DISCREPANCY_VALUES = " SET " +
                getUpdateRowStringAddComma(COL_ID, "?") +               //1
                getUpdateRowStringAddComma(COL_TAIL_NUM, "?") +         //2
                getUpdateRowStringAddComma(COL_NARRATIVE, "?") +        //3
                getUpdateRowStringAddComma(COL_DATE_CREATED, "?") +     //4
                getUpdateRowStringAddComma(COL_TURNOVER, "?") +         //5
                getUpdateRowStringAddComma(COL_PARTS_ON_ORDER, "?") +   //6
                getUpdateRowString(COL_STATUS, "?");                    //7 */

        String WHERE_ID_EQUALS_ID = " WHERE " + COL_ID + "=" + disc.getId() + " ";                  //8

        String fullSQL = UPDATE_DISCREPANCIES + SET_DISCREPANCY_VALUES + WHERE_ID_EQUALS_ID;
        System.out.println(fullSQL);

        try (Statement statement = conn.createStatement()) {

            /*statement.setLong(1, disc.getId());
            statement.setString(2, disc.getTailNum());
            statement.setString(3, disc.getNarrative());
            statement.setString(4, disc.getDateCreated().toString());
            statement.setString(5, disc.getTurnover());
            statement.setString(6, disc.getPartsOnOrder());
            statement.setLong(7, disc.getStatus().getId());
            statement.setLong(8, disc.getId());*/

            return statement.executeUpdate(fullSQL);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
}
