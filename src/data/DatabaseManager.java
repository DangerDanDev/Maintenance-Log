package data;

import data.Tables.AircraftTable;
import data.Tables.DiscrepancyTable;
import data.Tables.StatusTable;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    public static final AircraftTable AIRCRAFT_TABLE = new AircraftTable();
    public static final DiscrepancyTable DISCREPANCY_TABLE = new DiscrepancyTable();
    public static final StatusTable STATUS_TABLE = new StatusTable();

    public static final String NAME = "logbook_db.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + NAME);
    }

    public static void initialize()
    {


        try (Connection connection = getConnection())
        {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            AIRCRAFT_TABLE.createTableIfNotExists(connection);
            DISCREPANCY_TABLE.createTableIfNotExists(connection);
            STATUS_TABLE.createTableIfNotExists(connection);
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {

        }
    }
}
