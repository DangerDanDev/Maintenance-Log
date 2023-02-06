package GUI;

import data.DatabaseObject;
import data.tables.DiscrepancyTable;
import data.tables.Table;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.sql.SQLException;

public abstract class EditorDialogAbstract<T extends DatabaseObject> extends JDialog implements DatabaseObject.ChangeListener, Table.TableListener {

    private String editorTitle;
    public String getEditorTitle() {
        return editorTitle;
    }
    public void setEditorTitle(String TITLE) {
        this.editorTitle = TITLE;
    }

    private Table table;

    /**
     * The item I am responsible for editing
     */
    private T item;

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        //if we were subscribed to a previous discrepancy, now is the time
        //to unsubscribe
        if(this.getItem() != null)
            this.getItem().setListener(null);

        this.item = item;

        if(this.getItem() == null)
            return; //do not refresh and do not set a listener if our item is null

        this.getItem().setListener(this);

        refreshData();
    }

    /**
     * Updates all the GUI fields to match our discrepancy's latest data
     */
    public abstract void refreshData();

    /**
     * Pushes all the changes to the item we're editing, usually called when the save button is clicked
     */
    public abstract void pushChanges();

    public EditorDialogAbstract(String windowTitle, Table table) {
        setEditorTitle(windowTitle);
        setTitle(getEditorTitle());
        setTable(table);
    }

    protected void onSave() {

        //if the discrepancy has not been edited, we don't need to go through any of that saving
        //funny business
        if(!getItem().isSaved())
            save();

        closeWindow();
    }

    /**
     * Attempts to save the item we are editing, displays a dialog if we fail
     */
    public void save() {
        //push the user's changes to the discrepancy object
        pushChanges();

        try {
            getTable().updateItem(getItem());
        } catch (SQLException ex) {
            String options[] = {
                    "Close Window Anyway",
                    "Continue Editing",
            };

            int result = JOptionPane.showOptionDialog(null, "Save failed. Would Would you like to discard changes and close anyways or remain on " +
                    "this screen?", "Save error!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{}, 1);

            if (result != 0)
                return;
        }
    }


    protected void onCancel() {
        closeWindow();
    }

    /**
     * Unsubscribes from the Table updates and from Item updates
     * then closes the dialog
     */
    public void closeWindow() {

        getTable().removeListener(this);
        getItem().setListener(null);

        dispose();
    }

    @Override
    public void onItemSaved() {
        setTitle(getEditorTitle());
    }

    /////////////////////////////////////////////////////////////////////////////////
    // ITEM EDIT LISTENER
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Called when the user has made changes to the item we are editing via one of the GUI controls;
     * Mark the window title to say unsaved and set the item's saved flag to false as well
     */
    public void onItemEdited() {
        setTitle(getEditorTitle() + " (unsaved)");
        getItem().setSaved(false);
    }

    private ItemEditListener itemEditListener = new ItemEditListener();

    public ItemEditListener getItemEditListener() {
        return itemEditListener;
    }

    public void setItemEditListener(ItemEditListener itemEditListener) {
        this.itemEditListener = itemEditListener;
    }

    private class ItemEditListener implements KeyListener, ItemListener, ActionListener, ChangeListener {
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

        @Override
        public void stateChanged(ChangeEvent e) {
            onItemEdited();
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // END ITEM EDIT LISTENER
    /////////////////////////////////////////////////////////////////////////////


    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        if(this.table != null)
            this.table.removeListener(this);

        this.table = table;

        if(this.table != null)
            this.table.addListener(this);
    }



    @Override
    public void onItemAdded(Object addedItem) {
        //do nothing
    }

    @Override
    public void onItemUpdated(Object editedItem) {
        //react to the changed item
        if(editedItem.equals(getItem()))
            refreshData();
    }

    @Override
    public void onItemDeleted(Object deletedItem) {

    }
}
