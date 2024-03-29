package data;

import data.tables.*;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    public static final String NAME = "logbook.db";

    private static Connection conn = null;

    /**
     * Returns the current existing connection object, unless there is none. In that case, it
     * Gets a shiny new unique instance of a database connection.
     * The user must close it on their own.
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() {
        return conn;
    }

    private static void initConnection() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        conn = DriverManager.getConnection("jdbc:sqlite:" + NAME, config.toProperties());
    }

    private static Table tables[] = {
            DiscrepancyTable.getInstance(),
            StatusTable.getInstance(),
            LogEntryTable.getInstance(),
            AircraftTable.getInstance(),
    };

    public static void initialize() throws SQLException {

        initConnection();

        for(Table table : tables)
            table.create();
    }
}
