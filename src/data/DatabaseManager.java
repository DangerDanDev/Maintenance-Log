package data;

import data.Tables.AircraftTable;
import data.Tables.DiscrepancyTable;
import data.Tables.StatusTable;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

public class DatabaseManager {

    public static final String NAME = "logbook_db.db";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + NAME);
        connection.setAutoCommit(true);
        return connection;
    }

    public static void initialize()
    {

        System.out.println(Instant.now());

        try (Connection connection = getConnection())
        {
            try(Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                AircraftTable.get().createTableIfNotExists(connection);
                DiscrepancyTable.get().createTableIfNotExists(connection);
                StatusTable.get().createTableIfNotExists(connection);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
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
