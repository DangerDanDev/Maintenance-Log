package data;

import javax.print.attribute.standard.MediaSize;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Table {
    public final ArrayList<Column> COLUMNS;
    public final String NAME;

    public static final String TEXT = " TEXT ";
    public static final String INTEGER = " INTEGER ";
    public static final String REAL = " REAL ";
    public static final String PRIMARY_KEY = " PRIMARY KEY ";
    public static final String NOT_NULL = " NOT NULL ";

    public final Column COL_ID = new Column("_id", INTEGER + PRIMARY_KEY);

    public Table(String name, ArrayList<Column> columns, Connection connection) throws SQLException {
        this.NAME = name;

        columns.add(0, COL_ID);
        this.COLUMNS = columns;

        createTableIfNotExists(connection);
    }

    private void createTableIfNotExists(Connection connection) throws SQLException {

        Statement statement = connection.createStatement();

        String sqlCreateStr = "CREATE TABLE IF NOT EXISTS " + NAME + " (" + COL_ID.toString() + ")";
        System.out.println(sqlCreateStr);
        statement.executeUpdate(sqlCreateStr);

        //Rather than creating all of the columns in the CREATE TABLE statement, we
        //add them via the following ALTER TABLE statement after creation. This way, columns that were added
        //to the program after the table was created will be auto-added with no separate update logic
        // or other nonsense to fiddle around with;
        // pre-existing tables will not be added, as they will throw an SQLException that is harmlessly caught.
        for (Column column : COLUMNS) {
            try {
                String addColumnStatement = "ALTER TABLE " + NAME + " ADD COLUMN " + column;
                System.out.println(addColumnStatement);
                statement.executeUpdate(addColumnStatement);
            } catch (SQLException e) {
                System.out.println("Column (" + column + ") probably already exists");
            }
        }
    }


}
