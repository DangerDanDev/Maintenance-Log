package data;

import data.Tables.AircraftTable;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static Connection connection;

    public static final String NAME = "logbook_db.db";

    public static void main(String[] args)
    {

        connection = null;

        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            Table aircraft = new AircraftTable(connection);
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
