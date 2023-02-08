package GUI.BaseClasses;

import data.DatabaseObject;
import data.tables.Table;

import javax.swing.*;
import java.awt.event.*;
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

    public EditorPanel(Table<T> table) {
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
        if(editedItem.equals(getItem()))
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

    /////////////////////////////////////////////////////////////////////////////////
    // ITEM EDIT LISTENER
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Called when the user has made changes to the item we are editing via one of the GUI controls;
     * Mark the window title to say unsaved and set the item's saved flag to false as well
     */
    public void onItemEdited() {
        getItem().setSaved(false);
    }

    private ItemEditListener itemEditListener = new ItemEditListener();

    public ItemEditListener getItemEditListener() {
        return itemEditListener;
    }

    public void setItemEditListener(ItemEditListener itemEditListener) {
        this.itemEditListener = itemEditListener;
    }

    private class ItemEditListener implements KeyListener, ItemListener, ActionListener {
        @Override
        public void keyTyped(KeyEvent e) {
            onItemEdited();
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == ItemEvent.SELECTED)
                onItemEdited();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onItemEdited();
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // END ITEM EDIT LISTENER
    /////////////////////////////////////////////////////////////////////////////
}
