package data.Tables;

import data.Column;

import java.util.ArrayList;

public class AircraftTable extends Table {

    public static final String TEXT = Table.TEXT;
    public static final String INTEGER = Table.INTEGER;
    public static final String REAL = Table.REAL;

    public static final String _NAME = "Aircraft";

    public static final Column COL_TAIL_NUM = new Column("tail_number", TEXT, NOT_NULL + UNIQUE);
    public static final Column COL_KEY_TYPE_1 = new Column("key_type_1", TEXT, "");

    private static final AircraftTable aircraftTable = new AircraftTable();
    public static AircraftTable get() { return aircraftTable; }

    protected AircraftTable() {
        super();

        addColumn(COL_TAIL_NUM);
        addColumn(COL_KEY_TYPE_1);
    }

    @Override
    public String getName() {
        return "Aircraft";
    }
}
