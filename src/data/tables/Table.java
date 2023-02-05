package data.tables;

import data.DBManager;
import data.DatabaseObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Table<T extends DatabaseObject> {

    public static String INTEGER = " INTEGER ";
    public static String TEXT = " TEXT ";

    public static String PRIMARY_KEY = " PRIMARY KEY AUTOINCREMENT ";

    /**
     * The list of items that this table's "CREATE TABLE" statement will make
     */
    private ArrayList<Column> columns = new ArrayList<>();

    /**
     * The name of this table
     */
    public final String NAME;

    public Table(String name) {
        this.NAME = name;

        this.COL_ID = new Column(this,"id", INTEGER + PRIMARY_KEY);
        addColumn(COL_ID);

        this.COL_DATE_CREATED = new Column(this,"_date_created", TEXT);
        addColumn(COL_DATE_CREATED);

        this.COL_DATE_EDITED = new Column(this,"_date_edited", TEXT);
        addColumn(COL_DATE_EDITED);
    }

    public Table(String name, ArrayList<Column> cols) {
        this(name);
        for(Column c : cols)
            columns.add(c);
    }

    /**
     *
     */
    public void create() {

        String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS " + NAME + "(" +
                getColumnsAsSQLStr() +
                ")";

        Connection c = DBManager.getConnection();

        try (PreparedStatement statement = c.prepareStatement(CREATE_TABLE_IF_NOT_EXISTS);){
            statement.execute();
        } catch (SQLException ex) {
            System.out.println("SQLite error in " + this + ".create() method");
            System.out.println(CREATE_TABLE_IF_NOT_EXISTS);
            System.err.println(ex.getMessage());
        }

        //check to see if any columns have been added to the table (ie: program was updated)
        for(Column column : columns) {
            String alterTable = "ALTER TABLE " + NAME + " ADD COLUMN " + column.getDefinitionSQL();
            try(PreparedStatement alterStatement = c.prepareStatement(alterTable)) {
                alterStatement.execute();
            }catch (SQLException ex) {
                System.out.println("Alter table text: " + alterTable);
                System.out.println("Error in altering table");
                System.out.println(ex.getMessage());
            }
        }
    }

    public String getColumnsAsSQLStr() {
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);

            str.append(col.getDefinitionSQL());

            //there is no comma after the last column name
            if(i != columns.size() - 1)
                str.append(",");
        }

        return str.toString();
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public final Column COL_ID;
    public final Column COL_DATE_CREATED;
    public final Column COL_DATE_EDITED;

    public class Column {
        public final String NAME;
        public final String TYPE;
        public final String CONSTRAINTS;
        public final String DEFAULTS;

        public Column(Table table, String name, String type, String constraints, String defaults) {
            NAME = table.NAME + "_" + name;
            TYPE = type;
            CONSTRAINTS = constraints;
            DEFAULTS = defaults;
        }

        public Column(Table table, String name, String type, String constraints) {
            this(table, name, type, constraints, "");
        }

        public Column(Table table, String name, String type) {
            this(table, name,type, "");
        }

        public String getDefinitionSQL() {
            StringBuilder str = new StringBuilder();

            str.append(NAME + " ");
            str.append(TYPE + " ");

            if(CONSTRAINTS != "")
                str.append(CONSTRAINTS);

            if(DEFAULTS != "")
                str.append( " DEFAULTS " + DEFAULTS);

            return str.toString();
        }
    }
}
