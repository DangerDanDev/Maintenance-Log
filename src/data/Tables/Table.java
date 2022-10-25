package data.Tables;

import data.Column;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public abstract class Table {
    protected final ArrayList<Column> columns = new ArrayList<>();

    public static final String TEXT = " TEXT ";
    public static final String INTEGER = " INTEGER ";
    public static final String REAL = " REAL ";
    public static final String PRIMARY_KEY = " PRIMARY KEY ";
    public static final String NOT_NULL = " NOT NULL ";
    public static final String AUTO_INCREMENT = "AUTOINCREMENT ";
    public static final String UNIQUE = " UNIQUE " ;

    public static final Column COL_ID = new Column("_id", INTEGER, PRIMARY_KEY + AUTO_INCREMENT);

    protected Table() {
        //every table should start with the ID field, and this line
        //makes it automatically happen for all Table subclasses
        addColumn(COL_ID);
    }

    public static String References(Table table, Column column) {
        return " REFERENCES " + table.getName() +"(" + column.NAME +")";
    }

    public abstract String getName();

    protected void addColumn(Column column) {
        this.columns.add(column);
    }

    public void createTableIfNotExists(Connection connection) throws SQLException {

        System.out.println("Creating table: " + getName());
        try(Statement statement = connection.createStatement()) {

            String sqlCreateStr = "CREATE TABLE IF NOT EXISTS " + getName() + " (" + allColumnsToSQLString() +
                    ")";
            System.out.println(sqlCreateStr);
            statement.executeUpdate(sqlCreateStr);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     *
     * @return A string that can be put into a "CREATE TABLE" SQL statement
     * containing all of the columns in this table
     */
    public String allColumnsToSQLString() {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < this.columns.size(); i++) {
            stringBuilder.append(columns.get(i).getCreateString());
            if(i != columns.size()-1)
                stringBuilder.append(",");
        }


        return stringBuilder.toString();
    }

    public static String getUpdateRowString(Column column, String value) {
        return column + "=" + value;
    }

    public static String getUpdateRowStringAddComma(Column column, String value) {
        return getUpdateRowString(column, value) + ", ";
    }
}
