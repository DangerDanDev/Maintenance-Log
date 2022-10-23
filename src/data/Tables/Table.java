package data.Tables;

import data.Column;

import javax.print.attribute.standard.MediaSize;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public abstract class Table {
    protected final ArrayList<Column> COLUMNS = new ArrayList<>();

    public static final String TEXT = " TEXT ";
    public static final String INTEGER = " INTEGER ";
    public static final String REAL = " REAL ";
    public static final String PRIMARY_KEY = " PRIMARY KEY ";
    public static final String NOT_NULL = " NOT NULL ";
    public static final String AUTO_INCREMENT = "AUTOINCREMENT";

    public final Column COL_ID = new Column("_id", INTEGER + PRIMARY_KEY + AUTO_INCREMENT);

    public Table() {
        //every table should start with the ID field, and this line
        //makes it automatically happen for all Table subclasses
        addColumn(COL_ID);
    }

    public abstract String getName();

    protected void addColumn(Column column) {
        this.COLUMNS.add(column);
    }

    public void createTableIfNotExists(Connection connection) throws SQLException {

        Statement statement = connection.createStatement();

        String sqlCreateStr = "CREATE TABLE IF NOT EXISTS " + getName() + " (" + COL_ID.toString() + ")";
        System.out.println(sqlCreateStr);
        statement.executeUpdate(sqlCreateStr);

        //Rather than creating all of the columns in the CREATE TABLE statement, we
        //add them via the following ALTER TABLE statement after creation. This way, columns that were added
        //to the program after the table was created will be auto-added with no separate update logic
        // or other nonsense to fiddle around with;
        // pre-existing tables will not be added, as they will throw an SQLException that is harmlessly caught.
        for (Column column : COLUMNS) {
            try {
                String addColumnStatement = "ALTER TABLE " + getName() + " ADD COLUMN " + column;
                System.out.println(addColumnStatement);
                statement.executeUpdate(addColumnStatement);
            } catch (SQLException e) {
                System.out.println("Column (" + column + ") probably already exists");
            }
        }
    }


}
