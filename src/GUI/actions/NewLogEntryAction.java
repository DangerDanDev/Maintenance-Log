package GUI.actions;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import GUI.LogEntryEditor;
import data.DatabaseObject;
import model.Discrepancy;
import model.LogEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Helper class for adding a new LogEntry
 */
public class NewLogEntryAction extends AbstractAction {

    private final Window OWNER;
    private final Discrepancy DISCREPANCY;
    private final EditorPanel.EditorPanelHost EDITOR_PANEL_HOST;

    public NewLogEntryAction(Window owner, Discrepancy discrepancy, EditorPanel.EditorPanelHost editorPanelHost) {
        super("New Log Entry");

        this.OWNER = owner;
        this.DISCREPANCY = discrepancy;
        this.EDITOR_PANEL_HOST = editorPanelHost;

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //if the item has an invalid ID, it is not yet in the database
        //and we cannot save a log entry to it
        if(DISCREPANCY.getId() != DatabaseObject.INVALID_ID)
            createNewLogEntry();

            //if we need to prompt the user to save, prompt them
        else {
            String options[] = { "Ok", };

            JOptionPane.showOptionDialog(OWNER, "You must save the discrepancy before adding a log entry to it.",
                    "Save required!", 0, 0, null, options, 0);
        }
    }

    private void createNewLogEntry() {
        LogEntry logEntry = new LogEntry(DISCREPANCY, "", "");
        LogEntryEditor editor = new LogEntryEditor(logEntry, OWNER, EDITOR_PANEL_HOST, EditorPanel.Mode.EDIT);

        EditorDialog<LogEntry> dialog = new EditorDialog(OWNER, "New Log Entry");
        dialog.addEditorPanel(editor, BorderLayout.CENTER);
        dialog.setSize(800,600);
        dialog.setVisible(true);
    }
}
