package data.tables;

import data.DBManager;
import data.DatabaseObject;

import javax.print.attribute.standard.MediaSize;
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

    public void updateItem(T item) throws SQLException{
        QueryIndexer idx = new QueryIndexer();

        String updateText = getUpdateStatement(item, idx);
        System.out.println(updateText);
        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(updateText)) {

            setStatementValues(ps, idx, item);
            setUpdateQueryItemId(item, idx, ps);
            ps.execute();

            //if we get here, the item was officially saved
            item.setSaved(true);
            onItemUpdated(item);

        } catch (SQLException ex) {
            System.out.println("Error updating database object.");
            System.out.println(updateText);
            throw ex;
        }
    }

    public String getUpdateStatement(T item, QueryIndexer idx) {
        StringBuilder str = new StringBuilder();

        str.append("UPDATE " + NAME + " SET ");

        //start at 1 because we're skipping the ID column
        for(int i = 1; i < columns.size(); i++) {
            Column c = columns.get(i);

            str.append(c.NAME + "=" + idx.index(c));

            //append a comma unless we're on the last column
            if(i != columns.size() - 1)
                str.append(",");
        }

        str.append(" WHERE " + COL_ID + "=" + idx.index(COL_ID));

        return str.toString();
    }

    public void setUpdateQueryItemId(T item, QueryIndexer idx, PreparedStatement ps) throws SQLException {
        ps.setLong(idx.indexOf(COL_ID), item.getId());
    }

    /**
     *
     * @param item
     */
    public void addItem(T item) {

        item.setDateCreated(Instant.now());
        item.setDateLastEdited(Instant.now());

        QueryIndexer indexer = new QueryIndexer();

        try(PreparedStatement st = DBManager.getConnection().prepareStatement(getAddItemSQL(indexer), Statement.RETURN_GENERATED_KEYS)) {
            setStatementValues(st, indexer, item);

            st.execute();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if(rs.next())
                    item.setId(rs.getLong(1));

                System.out.println("New item ID is: " + item.getId());

                //if we reach here, we have successfully saved!
                item.setSaved(true);
                onItemAdded(item);

            }catch (SQLException ex) {
                System.err.println("Could not get generated keys");
                System.err.println(ex.getMessage());
            }

        }

        catch (SQLException ex) {
            System.out.println("Error adding default items.");
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Sets a PreparedStatement's values using a QueryIndexer.
     * Override this method in subclasses to set additional columns' values.
     * Note: This method does NOT set the ID field, so for update queries you
     * will need to call the setsetUpdateQueryItemId() method to make sure you are
     * updating the right row.
     * @param statement
     * @param indexer
     * @param item
     * @throws SQLException
     */
    public void setStatementValues(PreparedStatement statement, QueryIndexer indexer, T item) throws SQLException {
        statement.setString(indexer.indexOf(COL_DATE_CREATED), item.getDateCreated().toString());
        statement.setString(indexer.indexOf(COL_DATE_EDITED), item.getDateLastEdited().toString());
    }

    private String getAddItemSQL(QueryIndexer indexer) {
        StringBuilder str = new StringBuilder();

        str.append(" INSERT INTO " + NAME + " (");

        //start at 1 because we are getting the ADD ITEM sql string,
        //we do not yet have an ID for it
        for(int i = 1; i < columns.size(); i++) {
            Column c = columns.get(i);

            //here is where we add the column names
            //INSERT INTO table_name(...,...,...)
            str.append(c.NAME);
            indexer.index(c);

            //as usual, append a comma unless we are on the last index
            if(i != columns.size() - 1)
                str.append(",");
        }

        str.append(")");

        str.append(" VALUES (" );

        //start at 1 because we are getting the ADD ITEM sql string,
        //we do not yet have an ID for it
        for(int i = 1; i < columns.size(); i++) {

            //here is we add the "?,?,?" and index the columns for it
            str.append("?");

            //as usual, append a comma unless we are on the last index
            if(i != columns.size() - 1)
                str.append(",");
        }

        str.append(")");

        return str.toString();
    }

    public void removeItem(T item) {
        onItemDeleted(item);
    }

    /**
     *
     */
    public void create() throws SQLException {

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
            throw ex;
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
