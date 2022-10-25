package data.Tables;

import data.Column;
import data.DatabaseManager;
import data.Status;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class StatusTable extends Table {

    public static final Column COL_TITLE = new Column("title", TEXT,  NOT_NULL + UNIQUE);
    public static final Column COL_ABBREVIATION = new Column("abbreviation", TEXT, NOT_NULL + UNIQUE);

    private static final StatusTable statusTable = new StatusTable();
    public static StatusTable get() { return statusTable; }

    protected StatusTable() {
        super();
        addColumn(COL_TITLE);
        addColumn(COL_ABBREVIATION);
    }

    @Override
    public String getName() {
        return "status_table";
    }

    public static void populateStatusComboBox(JComboBox comboBox, Connection conn) throws SQLException {
            ArrayList<Status> statuses = StatusTable.get().getAllStatuses(conn);
            for(Status status : statuses) {
                comboBox.addItem(status);
            }
    }

    public static ArrayList<Status> getAllStatuses(Connection connection) throws SQLException {
        ArrayList<Status> statuses = new ArrayList<>();

        String query = "SELECT * FROM " + StatusTable.get().getName();

        try (Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery(query)) {

                while(resultSet.next()) {
                    statuses.add(getStatusFromResultSet(resultSet));
                }

            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }


        return statuses;
    }

    public static Status getStatusFromResultSet(ResultSet resultSet) throws SQLException {
        Status status = new Status(resultSet.getLong(COL_ID.NAME),
                resultSet.getString(COL_TITLE.NAME),
                resultSet.getString(COL_ABBREVIATION.NAME));

        System.out.println("Status ID: " + status.getId());

        return status;
    }
}
