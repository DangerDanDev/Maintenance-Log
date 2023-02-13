package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.tables.LogEntryTable;
import data.tables.Table;
import model.Discrepancy;
import model.LogEntry;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DiscrepancyEditorDialog extends EditorDialog<Discrepancy> implements Table.TableListener<LogEntry> {

    private DiscrepancyEditor discrepancyEditor;

    public Discrepancy getDiscrepancy() { return discrepancyEditor.getItem(); }

    public HashMap<LogEntry,LogEntryEditor> logEntryEditors = new HashMap<>();

    public DiscrepancyEditorDialog(Window owner, Discrepancy discrepancy) {
        super(owner, "Discrepancy Editor");

        discrepancyEditor = new DiscrepancyEditor(this,discrepancy, this);
        addEditorPanel(discrepancyEditor, BorderLayout.WEST);

        for(LogEntry logEntry : LogEntryTable.getInstance().getLogEntriesAgainstDiscrepancy(discrepancy)) {
            addLogEntry(logEntry);
        }

        LogEntryTable.getInstance().addListener(this);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1024, 768);
    }

    @Override
    public void unsubscribeFromTableUpdates() {
        super.unsubscribeFromTableUpdates();

        LogEntryTable.getInstance().removeListener(this);
    }

    private void addLogEntry(LogEntry entry) {
        LogEntryEditor editor = new LogEntryEditor(entry, this, this, EditorPanel.Mode.VIEW_ONLY);

        logEntryEditors.put(entry, editor);
        addEditorPanel((EditorPanel)editor, BorderLayout.CENTER);
    }

    private void removeLogEntry(LogEntry entry) {
        removeEditorPanel((EditorPanel)logEntryEditors.get(entry));
        logEntryEditors.remove(entry);
    }

    @Override
    public void onItemAdded(LogEntry addedItem, long transactionId) {
        if(!logEntryEditors.containsKey(addedItem))
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
