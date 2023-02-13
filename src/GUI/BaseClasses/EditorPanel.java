package GUI.BaseClasses;

import data.DatabaseObject;
import data.tables.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public abstract class EditorPanel<T extends DatabaseObject> implements Table.TableListener<T> {

    private long lastTransactionId = Table.INVALID_TRANSACTION_ID;
    public long getLastTransactionId() { return lastTransactionId ;}

    /**
     *
     */
    private Table<T> table;

    /**
     * The DatabaseObject I'm here to edit
     */
    private T item;

    /**
     * The window that owns me; used to launch dialogs and stuff
     */
    private final Window OWNER;

    /**
     *
     * @param table
     * @param host: the dialog or jFrame that hosts me, if applicable
     */
    public EditorPanel(Window owner, Table<T> table, EditorPanelHost host) {
        this.OWNER = owner;
        setTable(table);
        setEditorPanelHost(host);
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

    public void unsubscribeFromTableUpdates() {
        getTable().removeListener(this);
    }

    /**
     * Pushes the user's changes to the object and attempts to save the
     * changes to database. If the save fails, calls onSaveFailed();
     * @return
     */
    public boolean save() {

        //bypass saving if the user hasn't made any changes
        if(getItem().isSaved())
            return true;

        lastTransactionId = Table.getTransactionId();

        //push the user's changes to the discrepancy object
        pushChanges();

        try {
            //if it's a new object we want to insert it into the database first
            //if it's not a new item, update it
            if(getItem().getId() == DatabaseObject.INVALID_ID)
                getTable().addItem(getItem());
            else
                getTable().updateItem(getItem());

            onSaveSucceeded();

            //if I'm being hosted in a JFrame or Dialog, let it know that my item was saved
            if(getEditorPanelHost() != null)
                getEditorPanelHost().onItemSaved(getItem());

            return true;
        } catch (SQLException ex) {
            onSaveFailed();
            return false;
        }
    }

    /**
     * Called when a save is failed. Should notify the user of what happened
     * and give them options on what to do
     */
    public void onSaveFailed() {
        if(getEditorPanelHost() != null)
            getEditorPanelHost().onItemSaveFailed(getItem());
    }

    /**
     * Called when a save is successful; if applicable, should close the EditorDialog,
     * remove "unsaved" markers from window titles, etc
     */
    public void onSaveSucceeded() {
        if(getEditorPanelHost() != null)
            getEditorPanelHost().onItemSaved(getItem());
    }

    /**
     * Called when an item is added to the database table
     * @param addedItem
     */
    @Override
    public void onItemAdded(T addedItem, long transactionId) {
        //do nothing, we're just an editor panel and should only edit existing items
    }

    /**
     * Called when the table notifies us that the item has been changed
     * @param editedItem
     */
    @Override
    public void onItemUpdated(T editedItem, long transactionId) {

        if(editedItem.equals(getItem()) && transactionId != lastTransactionId)
            refreshData();

    }

    @Override
    public void onItemDeleted(T deletedItem, long transactionId) {
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

    public Window getOwner() { return this.OWNER; }

    /////////////////////////////////////////////////////////////////////////////////
    // ITEM EDIT LISTENER
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Called when the user has made changes to the item we are editing via one of the GUI controls;
     * Marks the item as unsaved and lets the PanelHost (JDialog or JFrame, usually) that the iteam
     * has been edited
     */
    public void onItemEdited() {
        getItem().setSaved(false);

        if(getEditorPanelHost() != null)
            getEditorPanelHost().onItemEdited(getItem());
    }

    private ItemEditListener itemEditListener = new ItemEditListener();

    public ItemEditListener getItemEditListener() {
        return itemEditListener;
    }

    public void setItemEditListener(ItemEditListener itemEditListener) {
        this.itemEditListener = itemEditListener;
    }

    /**
     * Implements various swing event handlers to listen for any edited controls
     */
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

    /////////////////////////////////////////////////////////////////////////////
    // EDITOR PANEL HOST LISTENER
    /////////////////////////////////////////////////////////////////////////////

    private EditorPanelHost<T> editorPanelHost;

    public EditorPanelHost<T> getEditorPanelHost() {
        return editorPanelHost;
    }

    public void setEditorPanelHost(EditorPanelHost<T> editorPanelHost) {
        this.editorPanelHost = editorPanelHost;
    }

    /**
     * An interface so that I can notify my host (ie: JDialog or JFrame) that
     * I have been edited, saved, failed at saving, or that a close button has been clicked
     */
    public interface EditorPanelHost<T> {
        void onItemEdited(T item);
        void onItemSaved(T item);
        void onItemSaveFailed(T item);
    }

    /////////////////////////////////////////////////////////////////////////////
    // End EDITOR PANEL HOST LISTENER
    /////////////////////////////////////////////////////////////////////////////
}
