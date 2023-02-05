package GUI;

import data.DatabaseObject;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class EditorDialogAbstract<T extends DatabaseObject> extends JDialog implements DatabaseObject.ChangeListener {

    private String editorTitle;
    public String getEditorTitle() {
        return editorTitle;
    }
    public void setEditorTitle(String TITLE) {
        this.editorTitle = TITLE;
    }

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
            return; //do not refresh and do not set a listener if we have no discrepancy

        this.getItem().setListener(this);

        refreshData();
    }

    public abstract void refreshData();

    public EditorDialogAbstract(String windowTitle) {
        setEditorTitle(windowTitle);
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

    private class ItemEditListener implements KeyListener {
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
    }

    //////////////////////////////////////////////////////////////////////////////
    // END ITEM EDIT LISTENER
    /////////////////////////////////////////////////////////////////////////////


}
