package GUI;

import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import data.tables.Table;
import model.Discrepancy;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

public class DiscrepancyLineItem extends LineItemBase<Discrepancy> {
    private JComboBox cbStatus;
    private JPanel panel1;
    private JTextField tfNarrative;
    private JTextField tfTurnover;
    private JTextField tfPartsOnOrder;

    public DiscrepancyLineItem(Discrepancy disc) throws SQLException {

        populateComboBox();
        setItem(disc);

        StatusTable.getInstance().addListener(statusTableListener);
        DiscrepancyTable.getInstance().addListener(discrepancyTableListener);

        tfNarrative.addMouseListener(doubleClickListener);
        tfPartsOnOrder.addMouseListener(doubleClickListener);
        tfTurnover.addMouseListener(doubleClickListener);
    }

    /**
     * Listens for the user double clicking on discrepancy fields to open the editor
     */
    private DoubleClickListener doubleClickListener = new DoubleClickListener();

    /**
     * Listens for the user double clicking on the discrepancy fields
     * and opens the discrepancy editor when that happens
     */
    private class DoubleClickListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                JDialog dialog = new JDialog();
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setModal(true);

                DiscrepancyEditor editor = new DiscrepancyEditor(getItem(), null);
                dialog.setContentPane(editor.getContentPane());
                dialog.pack();
                dialog.setVisible(true);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private void populateComboBox() throws SQLException {
        for(Status s : StatusTable.getInstance().getAllItems())
            cbStatus.addItem(s);
    }

    @Override
    public void refreshData() {
        tfNarrative.setText(getItem().getText());
        tfTurnover.setText(getItem().getTurnover());
        tfPartsOnOrder.setText(getItem().getPartsOnOrder());

        getItem().getStatus().selectInComboBox(cbStatus);
    }

    @Override
    public JPanel getCustomContentPane() {
        return panel1;
    }

    private StatusTableListener statusTableListener = new StatusTableListener();

    public class StatusTableListener implements Table.TableListener<Status> {
        @Override
        public void onItemAdded(Status addedItem) {

        }

        @Override
        public void onItemUpdated(Status editedItem) {
        }

        @Override
        public void onItemDeleted(Status deletedItem) {

        }
    }

    private DiscrepancyTableListener discrepancyTableListener = new DiscrepancyTableListener();

    public class DiscrepancyTableListener implements Table.TableListener<Discrepancy> {
        @Override
        public void onItemAdded(Discrepancy addedItem) {

        }

        @Override
        public void onItemUpdated(Discrepancy editedItem) {
            if(editedItem.equals(getItem()))
                refreshData();
        }

        @Override
        public void onItemDeleted(Discrepancy deletedItem) {

        }
    }
}
