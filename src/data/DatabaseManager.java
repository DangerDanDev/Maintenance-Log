package data;

import data.Tables.AircraftTable;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + NAME);
    }

    public static AircraftTable AIRCRAFT_TABLE;

    public static final String NAME = "logbook_db.db";

    public static void initialize()
    {

        Connection connection = null;

        try
        {
            // create a database connection
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

             AIRCRAFT_TABLE = new AircraftTable(connection);
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
}
