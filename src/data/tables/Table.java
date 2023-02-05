package data.tables;

import data.DBManager;
import data.DatabaseObject;

import java.sql.*;
import java.time.Instant;
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

    public void updateItem(T item) {

        item.setSaved(true);
        onItemUpdated(item);
    }

    public final void addItem(T item) {

        item.setDateCreated(Instant.now());
        item.setDateLastEdited(Instant.now());

        String addSQL = "INSERT INTO " + NAME + "(" + COL_DATE_CREATED + ", " + COL_DATE_EDITED + ") " +
                "VALUES (?,?)";

        System.out.println("Add item SQL: " + addSQL);

        try(PreparedStatement st = DBManager.getConnection().prepareStatement(addSQL, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, item.getDateCreated().toString());
            st.setString(2, item.getDateLastEdited().toString());

            st.execute();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if(rs.next())
                    item.setId(rs.getLong(1));

                System.out.println("New item ID is: " + item.getId());
            }catch (SQLException ex) {
                System.err.println("Could not get generated keys");
                System.err.println(ex.getMessage());
            }
        }
        catch (SQLException ex) {
            System.out.println("Error adding default items.");
            System.err.println(ex.getMessage());
        }

        item.setSaved(true);
        onItemAdded(item);
    }

    public void removeItem(T item) {
        onItemDeleted(item);
    }

    /**
     *
     */
    public void create() {

        String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS " + NAME + "(" +
                getColumnDefinitionSQLStr() +
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

    public String getColumnDefinitionSQLStr() {
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

        public String toString() {
            return NAME;
        }
    }

    private ArrayList<TableListener<T>> listeners = new ArrayList();

    public void addListener(TableListener<T> listener) {
        listeners.add(listener);
    }
    public void removeListener(TableListener<T> listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners that an item has been created/added in this table
     * @param item
     */
    private void onItemAdded(T item) {
        for(TableListener<T> listener : listeners)
            listener.onItemAdded(item);
    }

    /**
     * Notifies all listeners that an item has been edited in this table
     * @param item
     */
    private void onItemUpdated(T item) {
        for(TableListener<T> listener : listeners)
            listener.onItemUpdated(item);
    }

    /**
     * Notifies all listeners that an item has been removed from this table
     * @param item
     */
    private void onItemDeleted(T item) {
        for(TableListener<T> listener : listeners)
            listener.onItemDeleted(item);
    }

    /**
     * Allows an outsider to listen for items being added, removed, or edited in this table
     * @param <T>
     */
    public interface TableListener<T>{
        /**
         * Called on a listener when an item in this table is created
         * @param addedItem
         */
        void onItemAdded(T addedItem);

        /**
         * Called on a listener when an item in this table is edited
         * @param editedItem
         */
        void onItemUpdated(T editedItem);

        /**
         * Called on a listener when an item is deleted from this table
         * @param deletedItem
         */
        void onItemDeleted(T deletedItem);
    }
}
