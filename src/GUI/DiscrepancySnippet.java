package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.tables.DiscrepancyTable;
import data.tables.LogEntryTable;
import data.tables.StatusTable;
import data.tables.Table;
import model.Discrepancy;
import model.LogEntry;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class DiscrepancySnippet extends EditorPanel<Discrepancy> {
    private JComboBox cbStatus;
    private JTextField tfNarrative;
    private JTextField tfTurnover;
    private JTextField tfPartsOnOrder;
    private JPanel logEntriesPanel;
    private JPanel contentPane;

    private ArrayList<LogEntrySnippet> logEntrySnippets = new ArrayList<>();

    public DiscrepancySnippet(Discrepancy disc) throws SQLException {
        super(DiscrepancyTable.getInstance(), null);

        populateComboBox();
        setItem(disc);

        StatusTable.getInstance().addListener(statusTableListener);
        DiscrepancyTable.getInstance().addListener(discrepancyTableListener);

        tfNarrative.addMouseListener(doubleClickListener);
        tfPartsOnOrder.addMouseListener(doubleClickListener);
        tfTurnover.addMouseListener(doubleClickListener);
        cbStatus.addItemListener(e -> onStatusChanged(e));
    }

    /**
     * Populates my logEntriesPanel with log entries that
     * pertain to my discrepancy
     */
    private void initLogEntriesPanel() {
        logEntrySnippets.clear();

        if(getItem() != null)
            for (LogEntry entry : LogEntryTable.getInstance().getLogEntriesAgainstDiscrepancy(getItem()))
                addLogEntry(entry);

    }

    private void addLogEntry(LogEntry entry) {
        LogEntrySnippet snippet = new LogEntrySnippet(entry);
        logEntrySnippets.add(snippet);
        logEntriesPanel.add(snippet.getContentPane());
    }

    @Override
    public void setItem(Discrepancy item) {
        super.setItem(item);

        initLogEntriesPanel();
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    @Override
    public void pushChanges() {
        getItem().setStatus((Status) cbStatus.getSelectedItem());
    }

    /**
     * Called when the user selects a new item in cbStatus
     * Changes the color of cbStatus to match the color of the selected status,
     * updates the Discrepancy.Status and saves the changes to the table
     */
    private void onStatusChanged(ItemEvent e) {

        if(e.getStateChange() == ItemEvent.SELECTED) {
            cbStatus.setBackground(((Status) cbStatus.getSelectedItem()).getColor());
            getItem().setSaved(false);

            if (!save()) {
                JOptionPane.showMessageDialog(null, "Save failed due to database error.");
            }
        }
    }

    /**
     * Listens for the user double clicking on discrepancy fields to open the editor
     */
    private DoubleClickListener doubleClickListener = new DoubleClickListener();

    private void createUIComponents() {
        logEntriesPanel = new JPanel();
        logEntriesPanel.setLayout(new BoxLayout(logEntriesPanel,BoxLayout.Y_AXIS));
    }

    /**
     * Listens for the user double clicking on the discrepancy fields
     * and opens the discrepancy editor when that happens
     */
    private class DoubleClickListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

                EditorDialog<Discrepancy> discrepancyEditorDialog = new EditorDialog<Discrepancy>("Discrepancy Editor",
                        new DiscrepancyEditor(getItem(), null));

                discrepancyEditorDialog.setVisible(true);
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

    /**
     * Populates cbStatus with all the statuses from the StatusTable
     * @throws SQLException
     */
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
        cbStatus.setBackground(getItem().getStatus().getColor());
    }

    private StatusTableListener statusTableListener = new StatusTableListener();

    public class StatusTableListener implements Table.TableListener<Status> {
        @Override
        public void onItemAdded(Status addedItem, long transactionId) {

        }

        @Override
        public void onItemUpdated(Status editedItem, long transactionId) {
        }

        @Override
        public void onItemDeleted(Status deletedItem, long transactionId) {

        }
    }

    private DiscrepancyTableListener discrepancyTableListener = new DiscrepancyTableListener();

    public class DiscrepancyTableListener implements Table.TableListener<Discrepancy> {
        @Override
        public void onItemAdded(Discrepancy addedItem, long transactionId) {

        }

        @Override
        public void onItemUpdated(Discrepancy editedItem, long transactionId) {
            if(editedItem.equals(getItem()) && transactionId != getLastTransactionId())
                refreshData();
        }

        @Override
        public void onItemDeleted(Discrepancy deletedItem, long transactionId) {

        }
    }
}
