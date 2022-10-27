package data.Tables;

import data.Column;
import data.Discrepancy;

import java.sql.*;
import java.time.Instant;

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
        //since that is a very poor method of getting spaces into an update. Probs will
        //need to convert to a PreparedStatement
        String SET_DISCREPANCY_VALUES = " SET " +
                //getUpdateRowStringAddComma(COL_ID, disc.getId() + "") +
                        getUpdateRowStringAddComma(COL_TAIL_NUM, disc.getTailNum()) +
                        getUpdateRowStringAddComma(COL_NARRATIVE, "'" + disc.getNarrative() + "'") +
                        getUpdateRowStringAddComma(COL_DATE_CREATED, "'" + disc.getDateCreated().toString() + "'" ) +
                        getUpdateRowStringAddComma(COL_TURNOVER, "'" + disc.getTurnover() + "'") +
                        getUpdateRowStringAddComma(COL_PARTS_ON_ORDER, "'" + disc.getPartsOnOrder() + "'") +
                        getUpdateRowString(COL_STATUS, disc.getStatus().getId() + " ");

        String WHERE_ID_EQUALS_ID = " WHERE " + COL_ID + "=" + disc.getId() + " ";                  //8

        String fullSQL = UPDATE_DISCREPANCIES + SET_DISCREPANCY_VALUES + WHERE_ID_EQUALS_ID;
        System.out.println(fullSQL);

        try (Statement statement = conn.createStatement()) {
            return statement.executeUpdate(fullSQL);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }

    public static void insertDiscrepancyIntoDatabase(Connection conn,Discrepancy discrepancy) throws SQLException {

        final String INSERT_INTO_DISCREPANCIES = " INSERT INTO " + get().getName() + " ";

        final String ALL_COLUMNS_EXCEPT_ID = " (" + get().getAllColumnsUpdateString() + ") ";

        final String DISCREPANCY_VALUES = " VALUES (?,?,?,?,?,?) ";

        final int TAIL_NUM = 1;
        final int NARRATIVE = 2;
        final int DATE_CREATED = 3;
        final int TURNOVER = 4;
        final int PARTS_ON_ORDER = 5;
        final int STATUS = 6;

        final String UPDATE_QUERY = INSERT_INTO_DISCREPANCIES + ALL_COLUMNS_EXCEPT_ID + DISCREPANCY_VALUES;
        System.out.println("Discrepancy Insertion query: " + UPDATE_QUERY);

        try (PreparedStatement ps = conn.prepareStatement(UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(TAIL_NUM, discrepancy.getTailNum());
            ps.setString(NARRATIVE, discrepancy.getNarrative());
            ps.setString(DATE_CREATED, discrepancy.getDateCreated().toString());
            ps.setString(TURNOVER, discrepancy.getTurnover());
            ps.setString(PARTS_ON_ORDER, discrepancy.getPartsOnOrder());
            ps.setLong(STATUS, discrepancy.getStatus().getId());

            ps.executeUpdate();

            long id = ps.getGeneratedKeys().getLong(1);
            discrepancy.setId(id);

        }catch ( SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
}
