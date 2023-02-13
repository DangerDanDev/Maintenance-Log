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

    public LogEntryEditor(LogEntry entry, Window owner, EditorPanelHost host, Mode mode) {
        super(owner, LogEntryTable.getInstance(), host);

        tfNarrative.addKeyListener(getItemEditListener());
        cbShowOnNotes.addActionListener(getItemEditListener());

        setItem(entry);
        setMode(mode);
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

        cbShowOnNotes.setSelected(getItem().isSaved());
    }

    @Override
    public void pushChanges() {
        getItem().setNarrative(tfNarrative.getText());
    }

    @Override
    public void setMode(Mode mode) {

        boolean editable = mode == Mode.EDIT;

        Color color = Color.WHITE;
        if(!editable)
            color = Color.LIGHT_GRAY;

        tfNarrative.setEditable(editable);
        tfNarrative.setBackground(color);

        tfCrew.setEditable(editable);
        tfCrew.setBackground(color);

        super.setMode(mode);
    }
}
