package data.Tables;

import data.Column;
import data.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class AircraftTable extends Table {

    public static final String TEXT = Table.TEXT;
    public static final String INTEGER = Table.INTEGER;
    public static final String REAL = Table.REAL;

    public static final String NAME = "Aircraft";

    public static final Column COL_TAIL_NUM = new Column("tail_number", Table.TEXT);
    public static final Column COL_KEY_TYPE_1 = new Column("key_type_1", Table.TEXT);

    public static ArrayList<Column> getColumns() {
        ArrayList<Column> columns = new ArrayList<>();

        columns.add(COL_TAIL_NUM);
        columns.add(COL_KEY_TYPE_1);

        return columns;
    }

    public AircraftTable(Connection connection) throws SQLException {
        super(NAME, getColumns(), connection);
    }
}
