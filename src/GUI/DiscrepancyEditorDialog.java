package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.tables.LogEntryTable;
import data.tables.Table;
import model.Discrepancy;
import model.LogEntry;

import java.awt.*;
import java.util.HashMap;

public class DiscrepancyEditorDialog extends EditorDialog<Discrepancy> implements Table.TableListener<LogEntry> {

    private DiscrepancyEditor discrepancyEditor;

    public Discrepancy getDiscrepancy() { return discrepancyEditor.getItem(); }

    public HashMap<LogEntry,LogEntryEditor> logEntryEditors = new HashMap<>();

    public DiscrepancyEditorDialog(Window owner, Discrepancy discrepancy) {
        super(owner, "Discrepancy Editor");

        discrepancyEditor = new DiscrepancyEditor(this,discrepancy, this);
        addEditorPanel(discrepancyEditor, BorderLayout.WEST);

        populateLogEntries(discrepancy);

        LogEntryTable.getInstance().addListener(this);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
    }

    private void populateLogEntries(Discrepancy discrepancy) {
        for(LogEntry logEntry : LogEntryTable.getInstance().getLogEntriesAgainstDiscrepancy(discrepancy)) {
            addLogEntry(logEntry);
        }
    }

    @Override
    public void unsubscribeFromTableUpdates() {
        super.unsubscribeFromTableUpdates();

        LogEntryTable.getInstance().removeListener(this);
    }

    /**
     * Adds a LogEntry to my center panel
     * @param entry
     */
    private void addLogEntry(LogEntry entry) {
        LogEntryEditor editor = new LogEntryEditor(entry, this, this, EditorPanel.Mode.VIEW_ONLY);

        logEntryEditors.put(entry, editor);
        addEditorPanel((EditorPanel)editor, BorderLayout.CENTER);
    }

    /**
     * Removes a LogEntry from my center panel
     * @param entry
     */
    private void removeLogEntry(LogEntry entry) {
        removeEditorPanel((EditorPanel)logEntryEditors.get(entry));
        logEntryEditors.remove(entry);
    }

    /**
     * Checks if the added item belongs to my discrepancy; if so, adds it to the center display panel.
     * @param addedItem
     * @param transactionId
     */
    @Override
    public void onItemAdded(LogEntry addedItem, long transactionId) {
        if(!logEntryEditors.containsKey(addedItem) && addedItem.getParentDiscrepancy().equals(getDiscrepancy()))
            addLogEntry(addedItem);
    }

    @Override
    public void onItemUpdated(LogEntry editedItem, long transactionId) {

    }

    @Override
    public void onItemDeleted(LogEntry deletedItem, long transactionId) {
        if(logEntryEditors.containsKey(deletedItem))
            removeLogEntry(deletedItem);
    }
}
