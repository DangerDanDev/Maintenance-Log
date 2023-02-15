package data.tables;

import data.DBManager;
import data.DatabaseObject;
import data.QueryIndexer;

import javax.swing.*;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Table<T extends DatabaseObject> {

    public static final String WHERE = " WHERE ";

    public static final String INTEGER = " INTEGER ";
    public static final String TEXT = " TEXT ";
    public static final String BOOL = " INTEGER "; //SQLite doesn't have bool, just 1 and 0

    public static final String NOT_NULL = " NOT NULL ";

    public static final String PRIMARY_KEY = " PRIMARY KEY AUTOINCREMENT ";

    /**
     * A starter number for transaction trackers that will never match the current transaction ID
     */
    public static final long INVALID_TRANSACTION_ID = -1;

    /**
     * A unique ID for database updates, insertions, deletions, etc;
     * This allows table listeners to track if the database is notifying them on
     * a change that they initiated to avoid a feedback loop
     */
    public static long transactionId = 0;

    /**
     * Returns a unique number transaction initiators can avoid responding to events that
     * they themselves initiated, which can result in feedback loops.
     * @return
     */
    public static long getTransactionId() { return ++transactionId;}

    /**
     * A hash map of all loaded items stored by ID; used to
     * ensure we aren't loading the same objects multiple times
     */
    private HashMap<Long, T> loadedItems = new HashMap<>();

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

        this.COL_ID = new Column(this, "id", INTEGER + PRIMARY_KEY);
        addColumn(COL_ID);

        this.COL_DATE_CREATED = new Column(this, "date_created", TEXT, NOT_NULL);
        addColumn(COL_DATE_CREATED);

        this.COL_DATE_EDITED = new Column(this,"date_edited", TEXT, NOT_NULL);
        addColumn(COL_DATE_EDITED);
    }

    public String References(Column col) {
        return " REFERENCES " + col.PARENT_TABLE.NAME + "(" + col + ")";
    }

    /**
     * Looks an item up by its ID and inflates it from the given resultset
     * @param id
     * @return
     * @throws SQLException
     */
    public T getItemById(long id) throws SQLException {

        QueryIndexer idx = new QueryIndexer();
        String QUERY_BY_ID = " SELECT * FROM " + NAME + WHERE + COL_ID + "=" + idx.index(COL_ID);

        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(QUERY_BY_ID)) {

            //we can't nest the try-with-resources because we have to set this
            //index here which makes this block a lot messier :/
            ps.setLong(idx.indexOf(COL_ID), id);

            try (ResultSet rs = ps.executeQuery()) {

                if(rs.next()) {
                    return getItemFromResultSet(rs);
                }

                else {
                    return null;
                }

            } catch (SQLException ex) {
                throw ex;
            }

        } catch (SQLException ex) {
            System.out.println("Error inflating item from database");
            System.err.println(ex.getMessage());
            throw ex;
        }
    }

    /**
     * Checks the ID returned by a ResultSet, and if we already have the object loaded,
     * returns it to the calling code. Otherwise it inflates it from the ResultSet
     * and returns it to the calling code.
     * @param rs
     * @return
     * @throws SQLException
     */
    public T getItemFromResultSet(ResultSet rs) throws SQLException {
        //if we already have the object loaded, return it without
        //inflating the result set
        if(this.containsItem(rs.getLong(COL_ID.NAME)))
            return loadedItems.get(rs.getLong(COL_ID.NAME));

        //if we do not have the object loaded, inflate it
        //and add it to our list of loaded objects
        else {
            T item = inflateItemFromResultSet(rs);
            loadedItems.put(item.getId(), item);
            return item;
        }
    }

    public boolean containsItem(DatabaseObject item) {
        return containsItem(item.getId());
    }

    public boolean containsItem(long id) {
        return loadedItems.containsKey(id);
    }

    /**
     * Grabs all of the items out of this database and returns them to the calling code
     * @return
     * @throws SQLException
     */
    public ArrayList<T> getAllItems() throws SQLException{
        String QUERY_ALL_ITEMS = "SELECT * FROM " + NAME;

        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(QUERY_ALL_ITEMS);
            ResultSet rs = ps.executeQuery()) {

            ArrayList<T> items = new ArrayList<>();

            while(rs.next()) {
                items.add(getItemFromResultSet(rs));
            }

            return items;

        } catch (SQLException ex) {
            throw ex;
        }
    }

    public HashMap<Long, T> getLoadedItems() {
        return loadedItems;
    }

    /**
     * Subclasses must implement this to inflate their objects from a Query ResultSet
     * @param rs
     * @return
     */
    public abstract T inflateItemFromResultSet(ResultSet rs) throws SQLException;

    /**
     * Updates a single item in the database, including updating
     * it's "Last edited" field
     * @param item
     * @throws SQLException
     */
    public void updateItem(T item) throws SQLException{
        QueryIndexer idx = new QueryIndexer();
        Instant previousLastEditedDate = item.getDateLastEdited();

        String updateText = getUpdateStatement(item, idx);
        System.out.println(updateText);
        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(updateText)) {

            //if we're updating the record, keep the live one on the App updated too
            item.setDateLastEdited(Instant.now());

            setStatementValues(ps, idx, item);
            setUpdateQueryItemId(item, idx, ps);
            ps.executeUpdate();

            //if we get here, the item was officially saved
            item.setSaved(true);
            onItemUpdated(item);

        } catch (SQLException ex) {

            //if the update failed, be sure to revert the last edited date
            item.setDateLastEdited(previousLastEditedDate);

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

    /**
     * Called before executing an update query to set the ID field so
     * we update the right records. IE:
     * "SET COL = VALUE, ..., ...,
     * WHERE COL_ID = ? <------ use this method to set that value before executing the query
     * @param item
     * @param idx
     * @param ps
     * @throws SQLException
     */
    public void setUpdateQueryItemId(T item, QueryIndexer idx, PreparedStatement ps) throws SQLException {
        ps.setLong(idx.indexOf(COL_ID), item.getId());
    }

    /**
     *
     * @param item
     * @return the change id, allowing the addItem initiator to avoid responding to this exact event
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
     * will need to call the setUpdateQueryItemId() method to make sure you are
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

    public void removeItem(T item) throws SQLException {
        //TODO: Make this actually remove the item from the database

        QueryIndexer idx = new QueryIndexer();

        final String DELETE_SQL = "DELETE FROM " + NAME +
                WHERE + COL_ID + "=" + idx.index(COL_ID);

        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(DELETE_SQL)) {

            ps.setLong(idx.indexOf(COL_ID), item.getId());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw ex;
        }

        //JOptionPane.showMessageDialog(null, "I know you said you want to delete this item, but that functionality is not yet implemented.");
        onItemDeleted(item);
    }

    /**
     * Executes the SQL CREATE TABLE IF NOT EXISTS statement for this table
     * Manually adds columns after the fact if they do not exist to see if
     * any of them were added after the table was created (ie: program update)
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

        //check to see if any columns have been added to the table since it was created (ie: program was updated)
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

    /**
     *
     * @return an SQL string with column definitions; IE: "NAME TYPE, NAME TYPE, NAME TYPE"
     * Note: does not include the parenthese, the returned string needs to be wrapped in parenthese
     * if applicable.
     */
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
            SwingUtilities.invokeLater(() -> listener.onItemAdded(item, transactionId));
    }

    /**
     * Notifies all listeners that an item has been edited in this table
     * @param item
     */
    private void onItemUpdated(T item) {
        for(TableListener<T> listener : listeners)
            SwingUtilities.invokeLater(() -> listener.onItemUpdated(item, transactionId));
    }

    /**
     * Notifies all listeners that an item has been removed from this table
     * @param item
     */
    private void onItemDeleted(T item) {
        for(TableListener<T> listener : listeners)
            SwingUtilities.invokeLater(() -> listener.onItemDeleted(item,transactionId));
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
        void onItemAdded(T addedItem, long transactionId);

        /**
         * Called on a listener when an item in this table is edited
         * @param editedItem
         */
        void onItemUpdated(T editedItem, long transactionId);

        /**
         * Called on a listener when an item is deleted from this table
         * @param deletedItem
         */
        void onItemDeleted(T deletedItem, long transactionId);
    }
}
