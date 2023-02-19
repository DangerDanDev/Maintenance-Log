package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.ComboBoxStatusTableListener;
import data.tables.DiscrepancyTable;
import data.tables.LogEntryTable;
import data.tables.StatusTable;
import data.tables.Table;
import model.Discrepancy;
import model.LogEntry;
import model.Status;
import GUI.actions.NewLogEntryAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.HashMap;

public class DiscrepancySnippet extends EditorPanel<Discrepancy> {
    private JComboBox cbStatus;
    private JTextArea tfNarrative;
    private JTextArea tfTurnover;
    private JTextArea tfPartsOnOrder;
    private JPanel logEntriesPanel;
    private JPanel contentPane;

    private HashMap<LogEntry, LogEntrySnippet> logEntrySnippets = new HashMap<>();

    private ComboBoxStatusTableListener cbStatusTableListener = new ComboBoxStatusTableListener(cbStatus);

    public DiscrepancySnippet(Window owner, Discrepancy disc) throws SQLException {
        super(owner, DiscrepancyTable.getInstance(), null);

        populateComboBox();
        setItem(disc);

        DiscrepancyTable.getInstance().addListener(discrepancyTableListener);

        MenuManager menuManager = new MenuManager();

        tfNarrative.addMouseListener(doubleClickListener);
        tfPartsOnOrder.addMouseListener(doubleClickListener);
        tfTurnover.addMouseListener(doubleClickListener);
        cbStatus.addItemListener(e -> onStatusChanged(e));
    }

    @Override
    public void unsubscribeFromTableUpdates() {
        super.unsubscribeFromTableUpdates();

        cbStatusTableListener.unsubscribe();
        logEntryTableListener.unsubscribe();
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
        LogEntrySnippet snippet = new LogEntrySnippet(getOwner(), entry);
        logEntrySnippets.put(entry, snippet);
        logEntriesPanel.add(snippet.getContentPane());

        logEntriesPanel.revalidate();
        logEntriesPanel.repaint();
    }

    private void removeLogEntry(LogEntry entry) {
        logEntriesPanel.remove(logEntrySnippets.get(entry).getContentPane());
        logEntrySnippets.remove(entry);

        logEntriesPanel.revalidate();
        logEntriesPanel.repaint();
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

            refreshColors(((Status)cbStatus.getSelectedItem()).getColor());
            setSaved(false);

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
                EditorDialog.showDiscrepancy(getItem(), getOwner());
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
        refreshColors(getItem().getStatus().getColor());
    }

    private void refreshColors(Color color) {
        cbStatus.setBackground(color);
        tfNarrative.setBackground(color);
    }

    private class MenuManager {

        public final PopupMenuListener popupMenuListener = new PopupMenuListener();

        JPopupMenu popupMenu = new JPopupMenu();

        public MenuManager() {
            popupMenu.add(new NewLogEntryAction(getOwner(), getItem(), getEditorPanelHost()));

            tfNarrative.addMouseListener(popupMenuListener);
        }

        /**
         * Exists to open up the popupmenu when a control is right clicked
         */
        private class PopupMenuListener implements  MouseListener {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
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

    private LogEntryTableListener logEntryTableListener = new LogEntryTableListener();

    public class LogEntryTableListener implements Table.TableListener<LogEntry> {

        public LogEntryTableListener() {
            LogEntryTable.getInstance().addListener(this);
        }

        public void unsubscribe() {
            LogEntryTable.getInstance().removeListener(this);
        }

        @Override
        public void onItemAdded(LogEntry addedItem) {
            if(addedItem.getParentDiscrepancy().getId() == getItem().getId())
                addLogEntry(addedItem);
        }

        @Override
        public void onItemUpdated(LogEntry editedItem) {

        }

        @Override
        public void onItemDeleted(LogEntry deletedItem) {
            if(logEntrySnippets.containsKey(deletedItem))
                removeLogEntry(deletedItem);
        }
    }
}
