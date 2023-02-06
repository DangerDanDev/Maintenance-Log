package GUI;

import data.DatabaseObject;

import javax.swing.*;

/**
 * A class to use as a base for everything that will show up on Notes, Status boards, or other reports.
 * @param <T>
 */
public abstract class LineItemBase<T extends DatabaseObject> {
    private T item;


    public abstract void refreshData();

    public abstract JPanel getCustomContentPane();

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;

        if(item != null)
            refreshData();
    }
}
