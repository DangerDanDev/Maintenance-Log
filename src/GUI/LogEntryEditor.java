package GUI;

import GUI.BaseClasses.EditorPanel;
import data.tables.LogEntryTable;
import model.LogEntry;

import javax.swing.*;
import java.awt.*;

public class LogEntryEditor extends EditorPanel<LogEntry> {
    private JTextArea tfNarrative;
    private JPanel borderPanel;
    private JCheckBox cbShowOnNotes;
    private JTextField tfDateCreated;
    private JTextField tfDateLastEdited;
    private JTextArea tfCrew;
    private JPanel eastPanel;

    public LogEntryEditor(LogEntry entry, Window owner, EditorPanelHost host) {
        super(owner, LogEntryTable.getInstance(), host);

        tfNarrative.addKeyListener(getItemEditListener());
        cbShowOnNotes.addActionListener(getItemEditListener());

        setItem(entry);
    }

    @Override
    public void setItem(LogEntry item) {
        super.setItem(item);

        System.out.println("LogEntryEditor setting item to: " + item);
        System.out.println("ID: " + item.getId());
    }

    @Override
    public JPanel getContentPane() {
        return borderPanel;
    }

    @Override
    public void refreshData() {
        tfNarrative.setText(getItem().getNarrative());
        tfDateCreated.setText(getItem().getDateCreated().toString());
        tfDateLastEdited.setText(getItem().getDateLastEdited().toString());
    }

    @Override
    public void pushChanges() {
        getItem().setNarrative(tfNarrative.getText());
    }
}
