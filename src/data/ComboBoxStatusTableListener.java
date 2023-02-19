package data;

import data.tables.StatusTable;
import data.tables.Table;
import model.Status;

import javax.swing.*;

/**
 * A utility class designed to listen for removed or newly created statuses and
 * add them to relevant combo boxes
 */
public class ComboBoxStatusTableListener implements Table.TableListener<Status> {

    public final JComboBox COMBO_BOX;

    public ComboBoxStatusTableListener(JComboBox comboBox) {
        COMBO_BOX = comboBox;

        StatusTable.getInstance().addListener(this);
    }

    public void unsubscribe() {
        StatusTable.getInstance().removeListener(this);
    }

    @Override
    public void onItemAdded(Status addedItem) {
        COMBO_BOX.addItem(addedItem);
    }

    @Override
    public void onItemUpdated(Status editedItem) {

    }

    @Override
    public void onItemDeleted(Status deletedItem) {
        COMBO_BOX.removeItem(deletedItem);
    }
}
