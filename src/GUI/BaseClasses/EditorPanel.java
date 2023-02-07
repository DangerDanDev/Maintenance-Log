package GUI.BaseClasses;

import data.DatabaseObject;
import data.tables.Table;

import javax.swing.*;
import java.sql.SQLException;

public abstract class EditorPanel<T extends DatabaseObject> implements Table.TableListener<T> {

    /**
     *
     */
    private Table<T> table;

    /**
     * The DatabaseObject I'm here to edit
     */
    private T item;

    public EditorPanel(T item, Table table) {
        setItem(item);

        setTable(table);
    }

    /**
     *
     * @return The main content area for this editor panel
     */
    public abstract JPanel getContentPane();

    /**
     * Updates the GUI fields to reflect changes in the
     * managed Item
     */
    public abstract void refreshData();

    /**
     * Pushes user changes in the GUI to the stored Item
     */
    public abstract void pushChanges();

    /**
     * Pushes the user's changes to the object and attempts to save the
     * changes to database. If the save fails, calls onSaveFailed();
     * @return
     */
    public boolean save() {
        //push the user's changes to the discrepancy object
        pushChanges();

        try {
            getTable().updateItem(getItem());
            onSaveSucceeded();
            return true;
        } catch (SQLException ex) {
            onSaveFailed();
            return false;
        }
    }

    /**
     * Called when a save is failed. Shold notify the user of what happened
     * and give them options on what to do
     */
    public abstract void onSaveFailed();

    /**
     * Called when a save is successful; if applicable, should close the EditorDialog,
     * remove "unsaved" markers from window titles, etc
     */
    public abstract void onSaveSucceeded();

    @Override
    public void onItemAdded(T addedItem) {
        //do nothing, we're just an editor panel and should only edit existing items
    }

    @Override
    public void onItemUpdated(T editedItem) {
        if(table.equals(getItem()))
            refreshData();
    }

    @Override
    public void onItemDeleted(T deletedItem) {
        //do nothing, we're just an editor panel and should only edit existing items
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;

        refreshData();
    }

    public Table<T> getTable() {
        return table;
    }

    public void setTable(Table<T> table) {
        if(this.table != null)
            this.table.removeListener(this);

        this.table = table;

        if(this.table != null)
            this.table.addListener(this);
    }
}
