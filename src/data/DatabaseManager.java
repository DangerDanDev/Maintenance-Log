package data;

import data.Tables.AircraftTable;
import data.Tables.DiscrepancyTable;
import data.Tables.StatusTable;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

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

            AircraftTable.get().createTableIfNotExists(connection);
            DiscrepancyTable.get().createTableIfNotExists(connection);
            StatusTable.get().createTableIfNotExists(connection);
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
